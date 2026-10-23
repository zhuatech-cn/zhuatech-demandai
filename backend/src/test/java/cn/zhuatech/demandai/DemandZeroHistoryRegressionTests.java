/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.demandai;

import static org.junit.jupiter.api.Assertions.*;

import cn.zhuatech.demandai.common.BusinessException;
import cn.zhuatech.demandai.service.DemandForecastService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class DemandZeroHistoryRegressionTests {
  private final DemandForecastService service = new DemandForecastService();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private DemandForecastService.BacktestRequest points(String actual, String forecast) {
    return new DemandForecastService.BacktestRequest(
        "SKU",
        List.of(
            new DemandForecastService.BacktestPoint(
                "P1", new BigDecimal(actual), new BigDecimal(forecast)),
            new DemandForecastService.BacktestPoint(
                "P2", new BigDecimal(actual), new BigDecimal(forecast)),
            new DemandForecastService.BacktestPoint(
                "P3", new BigDecimal(actual), new BigDecimal(forecast))),
        new BigDecimal("15"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void allZeroActualsDoNotDivideByZeroOrClaimAccuracy() {
    var r = service.backtest(points("0", "0"));
    assertFalse(r.metricsDefined());
    assertNull(r.wapePercent());
    assertNull(r.accuracyPercent());
    assertEquals("REVIEW", r.releaseDecision());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void mixedZeroAndNonzeroPeriodsAreValid() {
    var r =
        service.backtest(
            new DemandForecastService.BacktestRequest(
                "SKU",
                List.of(
                    new DemandForecastService.BacktestPoint("P1", BigDecimal.ZERO, BigDecimal.ZERO),
                    new DemandForecastService.BacktestPoint(
                        "P2", new BigDecimal("100"), new BigDecimal("80")),
                    new DemandForecastService.BacktestPoint(
                        "P3", BigDecimal.ZERO, BigDecimal.ZERO)),
                new BigDecimal("25")));
    assertTrue(r.metricsDefined());
    assertEquals(new BigDecimal("20.00"), r.wapePercent());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void releaseDecisionUsesUnroundedRatio() {
    var r = service.backtest(points("100", "115.000001"));
    assertEquals(new BigDecimal("15.00"), r.wapePercent());
    assertEquals("REVIEW", r.releaseDecision());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsInvalidOrDuplicatePeriodEvidence() {
    var point = new DemandForecastService.BacktestPoint("P", BigDecimal.ONE, BigDecimal.ONE);
    assertThrows(
        BusinessException.class,
        () ->
            service.backtest(
                new DemandForecastService.BacktestRequest(
                    "SKU", List.of(point, point, point), new BigDecimal("15"))));
    assertThrows(BusinessException.class, () -> service.backtest(points("-1", "0")));
  }
}
