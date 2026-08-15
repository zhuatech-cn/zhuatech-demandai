/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.demandai.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
}
