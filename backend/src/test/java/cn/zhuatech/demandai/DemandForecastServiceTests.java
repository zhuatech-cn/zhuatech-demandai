/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.demandai;

import cn.zhuatech.demandai.service.DemandForecastService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class DemandForecastServiceTests {
    private final DemandForecastService service = new DemandForecastService();

    @Test void recommendsReplenishmentForPromotionPeak() {
        var result = service.forecast(new DemandForecastService.Request("SKU-2408", new BigDecimal("80"),
            new BigDecimal("110"), new BigDecimal("1.20"), new BigDecimal("0.25"), 7,
            new BigDecimal("300"), 86));
        assertThat(result.planningSignal()).isEqualTo("REPLENISH");
        assertThat(result.reorderQuantity()).isPositive();
        assertThat(result.drivers()).contains("促销活动带来增量");
    }

    @Test void holdsWhenInventoryCoversDemand() {
        var result = service.forecast(new DemandForecastService.Request("SKU-1002", new BigDecimal("30"),
            new BigDecimal("32"), BigDecimal.ONE, BigDecimal.ZERO, 3, new BigDecimal("150"), 92));
        assertThat(result.planningSignal()).isEqualTo("HOLD");
        assertThat(result.reorderQuantity()).isZero();
    }

    @Test void flagsForecastVersionThatMissesReleaseGate() {
        var result = service.backtest(new DemandForecastService.BacktestRequest("SKU-2408", List.of(
            new DemandForecastService.BacktestPoint("W31", new BigDecimal("100"), new BigDecimal("118")),
            new DemandForecastService.BacktestPoint("W32", new BigDecimal("120"), new BigDecimal("145")),
            new DemandForecastService.BacktestPoint("W33", new BigDecimal("90"), new BigDecimal("124"))
        ), new BigDecimal("15")));
        assertThat(result.releaseDecision()).isEqualTo("REVIEW");
        assertThat(result.wapePercent()).isGreaterThan(new BigDecimal("15"));
        assertThat(result.worstPeriod()).isEqualTo("W33");
    }
}
