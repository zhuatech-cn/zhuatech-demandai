/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.demandai.service;

import cn.zhuatech.demandai.common.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class DemandForecastService {
  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Result forecast(Request request) {
    BigDecimal baseline =
        request
            .historicalDailyAverage()
            .multiply(new BigDecimal("0.4"))
            .add(request.recentDailyAverage().multiply(new BigDecimal("0.6")));
    BigDecimal forecast =
        baseline
            .multiply(request.seasonalityFactor())
            .multiply(BigDecimal.ONE.add(request.promotionLiftRate()))
            .setScale(0, RoundingMode.HALF_UP);
    BigDecimal dailyErrorRate =
        BigDecimal.ONE.subtract(BigDecimal.valueOf(request.modelConfidence()).movePointLeft(2));
    BigDecimal safetyStock =
        forecast
            .multiply(BigDecimal.valueOf(request.leadTimeDays()))
            .multiply(dailyErrorRate.add(new BigDecimal("0.10")))
            .setScale(0, RoundingMode.CEILING);
    int reorderQuantity =
        forecast
            .multiply(BigDecimal.valueOf(request.leadTimeDays()))
            .add(safetyStock)
            .subtract(request.availableAndInbound())
            .max(BigDecimal.ZERO)
            .setScale(0, RoundingMode.CEILING)
            .intValue();
    String signal =
        reorderQuantity > 0
            ? "REPLENISH"
            : request
                        .availableAndInbound()
                        .compareTo(
                            forecast.multiply(BigDecimal.valueOf(request.leadTimeDays() * 2L)))
                    > 0
                ? "REDUCE"
                : "HOLD";
    List<String> drivers = new ArrayList<>();
    if (request.seasonalityFactor().compareTo(BigDecimal.ONE) > 0) drivers.add("季节性需求上行");
    if (request.promotionLiftRate().signum() > 0) drivers.add("促销活动带来增量");
    if (request.recentDailyAverage().compareTo(request.historicalDailyAverage()) > 0)
      drivers.add("近期销量高于历史均值");
    if (drivers.isEmpty()) drivers.add("近期需求与历史基线基本一致");
    return new Result(
        request.skuCode(),
        forecast.intValue(),
        safetyStock.intValue(),
        reorderQuantity,
        signal,
        request.modelConfidence(),
        drivers);
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public BacktestResult backtest(BacktestRequest request) {
    if (request == null
        || request.points() == null
        || request.points().size() < 3
        || request.points().size() > 24
        || request.maximumWapePercent() == null
        || request.maximumWapePercent().signum() <= 0)
      throw new BusinessException("回测需要3–24期数据和正数WAPE门槛");
    var periods = new java.util.HashSet<String>();
    for (var point : request.points()) {
      if (point == null
          || point.period() == null
          || point.period().isBlank()
          || point.period().length() > 80
          || !periods.add(point.period())
          || point.actualDemand() == null
          || point.actualDemand().signum() < 0
          || point.forecastDemand() == null
          || point.forecastDemand().signum() < 0)
        throw new BusinessException("回测期数不可重复且需求、预测必须为非负数");
    }
    BigDecimal actualTotal =
        request.points().stream()
            .map(BacktestPoint::actualDemand)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal absoluteError =
        request.points().stream()
            .map(point -> point.forecastDemand().subtract(point.actualDemand()).abs())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal signedError =
        request.points().stream()
            .map(point -> point.forecastDemand().subtract(point.actualDemand()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BacktestPoint worst =
        request.points().stream()
            .max(
                (left, right) ->
                    left.forecastDemand()
                        .subtract(left.actualDemand())
                        .abs()
                        .compareTo(right.forecastDemand().subtract(right.actualDemand()).abs()))
            .orElseThrow();
    if (actualTotal.signum() == 0)
      return new BacktestResult(
          request.skuCode(),
          request.points().size(),
          null,
          null,
          null,
          "REVIEW",
          worst.period(),
          List.of("实际需求合计为零，WAPE、相对偏差和派生准确率不可定义"),
          false);
    BigDecimal wape =
        absoluteError.multiply(new BigDecimal("100")).divide(actualTotal, 2, RoundingMode.HALF_UP);
    BigDecimal bias =
        signedError.multiply(new BigDecimal("100")).divide(actualTotal, 2, RoundingMode.HALF_UP);
    BigDecimal accuracy = new BigDecimal("100").subtract(wape).max(BigDecimal.ZERO);
    boolean wapeExceeded =
        absoluteError
                .multiply(new BigDecimal("100"))
                .compareTo(request.maximumWapePercent().multiply(actualTotal))
            > 0;
    List<String> alerts = new ArrayList<>();
    if (wapeExceeded) alerts.add("WAPE 超过发布门槛");
    if (signedError
            .abs()
            .multiply(new BigDecimal("100"))
            .compareTo(actualTotal.multiply(new BigDecimal("10")))
        > 0) alerts.add("系统性预测偏差超过 10%");
    if (alerts.isEmpty()) alerts.add("回测指标满足当前发布门槛");
    return new BacktestResult(
        request.skuCode(),
        request.points().size(),
        wape,
        bias,
        accuracy,
        wapeExceeded ? "REVIEW" : "PASS",
        worst.period(),
        alerts,
        true);
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record Request(
      @NotBlank String skuCode,
      @DecimalMin("0") BigDecimal historicalDailyAverage,
      @DecimalMin("0") BigDecimal recentDailyAverage,
      @DecimalMin("0.1") BigDecimal seasonalityFactor,
      @DecimalMin("0") BigDecimal promotionLiftRate,
      @Min(1) int leadTimeDays,
      @DecimalMin("0") BigDecimal availableAndInbound,
      @Min(0) @Max(100) int modelConfidence) {}

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record Result(
      String skuCode,
      int forecastDailyDemand,
      int safetyStock,
      int reorderQuantity,
      String planningSignal,
      int confidence,
      List<String> drivers) {}

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record BacktestPoint(
      @NotBlank String period,
      @DecimalMin("0") BigDecimal actualDemand,
      @DecimalMin("0") BigDecimal forecastDemand) {}

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record BacktestRequest(
      @NotBlank String skuCode,
      @NotNull @Size(min = 3, max = 24) List<@Valid BacktestPoint> points,
      @DecimalMin("0.01") BigDecimal maximumWapePercent) {}

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record BacktestResult(
      String skuCode,
      int sampleCount,
      BigDecimal wapePercent,
      BigDecimal biasPercent,
      BigDecimal accuracyPercent,
      String releaseDecision,
      String worstPeriod,
      List<String> alerts,
      boolean metricsDefined) {}
}
