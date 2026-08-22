/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.demandai.controller;

import cn.zhuatech.demandai.common.ApiResponse;
import cn.zhuatech.demandai.service.DemandForecastService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/demand")
@PreAuthorize("hasAnyRole('DOMAIN_USER','DOMAIN_OPERATOR','ADMIN')")
public class DemandForecastController {
    private final DemandForecastService service;
    public DemandForecastController(DemandForecastService service) { this.service = service; }

    @PostMapping("/forecast")
    public ApiResponse<DemandForecastService.Result> forecast(@Valid @RequestBody DemandForecastService.Request request) {
        return ApiResponse.ok("需求预测完成", service.forecast(request));
    }

    @PostMapping("/backtest")
    public ApiResponse<DemandForecastService.BacktestResult> backtest(
        @Valid @RequestBody DemandForecastService.BacktestRequest request) {
        return ApiResponse.ok("预测回测诊断完成", service.backtest(request));
    }
}
