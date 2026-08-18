/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.demandai.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemandForecastService {
    public Result forecast(Request request) {
        BigDecimal baseline = request.historicalDailyAverage().multiply(new BigDecimal("0.4"))
            .add(request.recentDailyAverage().multiply(new BigDecimal("0.6")));
        BigDecimal forecast = baseline.multiply(request.seasonalityFactor())
            .multiply(BigDecimal.ONE.add(request.promotionLiftRate())).setScale(0, RoundingMode.HALF_UP);
        BigDecimal dailyErrorRate = BigDecimal.ONE.subtract(BigDecimal.valueOf(request.modelConfidence()).movePointLeft(2));
        BigDecimal safetyStock = forecast.multiply(BigDecimal.valueOf(request.leadTimeDays()))
            .multiply(dailyErrorRate.add(new BigDecimal("0.10"))).setScale(0, RoundingMode.CEILING);
        int reorderQuantity = forecast.multiply(BigDecimal.valueOf(request.leadTimeDays())).add(safetyStock)
            .subtract(request.availableAndInbound()).max(BigDecimal.ZERO).setScale(0, RoundingMode.CEILING).intValue();
        String signal = reorderQuantity > 0 ? "REPLENISH" : request.availableAndInbound().compareTo(
            forecast.multiply(BigDecimal.valueOf(request.leadTimeDays() * 2L))) > 0 ? "REDUCE" : "HOLD";
        List<String> drivers = new ArrayList<>();
        if (request.seasonalityFactor().compareTo(BigDecimal.ONE) > 0) drivers.add("季节性需求上行");
        if (request.promotionLiftRate().signum() > 0) drivers.add("促销活动带来增量");
        if (request.recentDailyAverage().compareTo(request.historicalDailyAverage()) > 0) drivers.add("近期销量高于历史均值");
        if (drivers.isEmpty()) drivers.add("近期需求与历史基线基本一致");
        return new Result(request.skuCode(), forecast.intValue(), safetyStock.intValue(), reorderQuantity,
            signal, request.modelConfidence(), drivers);
    }

    public BacktestResult backtest(BacktestRequest request) {
        BigDecimal actualTotal = request.points().stream()
            .map(BacktestPoint::actualDemand).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal absoluteError = request.points().stream()
            .map(point -> point.forecastDemand().subtract(point.actualDemand()).abs())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal signedError = request.points().stream()
            .map(point -> point.forecastDemand().subtract(point.actualDemand()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal wape = absoluteError.divide(actualTotal, 4, RoundingMode.HALF_UP)
            .movePointRight(2).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bias = signedError.divide(actualTotal, 4, RoundingMode.HALF_UP)
            .movePointRight(2).setScale(2, RoundingMode.HALF_UP);
        BigDecimal accuracy = new BigDecimal("100").subtract(wape).max(BigDecimal.ZERO);
        BacktestPoint worst = request.points().stream().max((left, right) ->
            left.forecastDemand().subtract(left.actualDemand()).abs().compareTo(
                right.forecastDemand().subtract(right.actualDemand()).abs())).orElseThrow();
        List<String> alerts = new ArrayList<>();
        if (wape.compareTo(request.maximumWapePercent()) > 0) alerts.add("WAPE 超过发布门槛");
        if (bias.abs().compareTo(new BigDecimal("10")) > 0) alerts.add("系统性预测偏差超过 10%");
        if (alerts.isEmpty()) alerts.add("回测指标满足当前发布门槛");
        return new BacktestResult(request.skuCode(), request.points().size(), wape, bias, accuracy,
            wape.compareTo(request.maximumWapePercent()) <= 0 ? "PASS" : "REVIEW",
            worst.period(), alerts);
    }

    public record Request(@NotBlank String skuCode,
                          @DecimalMin("0") BigDecimal historicalDailyAverage,
                          @DecimalMin("0") BigDecimal recentDailyAverage,
                          @DecimalMin("0.1") BigDecimal seasonalityFactor,
                          @DecimalMin("0") BigDecimal promotionLiftRate,
                          @Min(1) int leadTimeDays,
                          @DecimalMin("0") BigDecimal availableAndInbound,
                          @Min(0) @Max(100) int modelConfidence) {}
    public record Result(String skuCode, int forecastDailyDemand, int safetyStock,
                         int reorderQuantity, String planningSignal,
                         int confidence, List<String> drivers) {}
    public record BacktestPoint(@NotBlank String period,
                                @DecimalMin("0.01") BigDecimal actualDemand,
                                @DecimalMin("0") BigDecimal forecastDemand) {}
    public record BacktestRequest(@NotBlank String skuCode,
                                  @NotNull @Size(min = 3, max = 24) List<@Valid BacktestPoint> points,
                                  @DecimalMin("0.01") BigDecimal maximumWapePercent) {}
    public record BacktestResult(String skuCode, int sampleCount, BigDecimal wapePercent,
                                 BigDecimal biasPercent, BigDecimal accuracyPercent,
                                 String releaseDecision, String worstPeriod, List<String> alerts) {}
}
