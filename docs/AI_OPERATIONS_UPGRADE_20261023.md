# 间歇性需求Croston-SBA预测

知华科技（上海如静知华信息科技有限公司） · https://www.zhuatech.cn/

## 已实现功能

对含零需求的时序执行Croston平滑及SBA偏差修正，返回非零需求大小、需求间隔和下一期预测，非硬编码场景分值。

最多500期非负等间隔需求，alpha在(0,1]。第一笔非零需求初始化大小，初始间隔取从序列起点至该期的期数；之后仅非零期更新两项平滑值，SBA=(1−alpha/2)×大小/间隔。全零序列预测0并显式allZero。是可复现统计基线，不包含季节性、置信区间或自动发布。

这是真正执行计算的本地算法，不是固定内容的演示响应，不调用外部大模型。仅处理调用者本次已获授权的数据，不查询或修改业务库，不自动审批、转账、改价、冻结、关单、发布内容或操作设备。

## 使用与接口

登录后从管理端或业务端右下角「业务计算工具」进入。可编辑JSON、执行校验、查看前100行结果、导出完整JSON；支持加载、空结果、格式错误、超时、无效认证与后端异常提示。退出登录后卸载页面内存中的输入及结果；不额外存储账号或密码。合成示例不是生产数据。

`POST /api/workbench/intermittent-demand-sba`，`Content-Type: application/json`，使用现有Bearer JWT。

- 有效认证200；无有效身份401；参数错误400。现有安全过滤器不变，控制器另有身份检查。
- 所有已认证账号可对自己提交的数据计算；不读取记录，也不授予额外业务记录权限。
- 未声明字段、重复业务编号、非法类型、越界数值/日期均明确拒绝。通用限制：深度12、节点10000、单字符串64000字符、累计128000字符、单数组最多500项；业务还有更小限制。
- 生产网关应设置128 KiB请求体和速率限制；业务校验在JSON解析之后，不替代传输层防护。前端输入另限制128 KiB。
- 日期通常为1900–2200年ISO-8601日期；时间字段按本功能口径，涉及组织本地时间时不做时区推断。

### 参数与示例

| 顶层字段 | 类型 |
| --- | --- |
| `alpha` | number |
| `demand` | array |

```json
{
  "alpha": 1,
  "demand": [
    0,
    0,
    6,
    0,
    0,
    12
  ]
}
```

```sh
# 将示例保存为request.json。TOKEN来自你自己在现有系统中的登录。
curl --fail-with-body -X POST "$API_BASE/api/workbench/intermittent-demand-sba" \\
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \\
  --data-binary @request.json
```

成功响应为 `{success:true,message,data}`，失败为明确错误，不生成伪造结果。输出字段及精度可在 `workbench/AiWorkbenchService.java` 查看。

## 既有接口兼容与修复

同时修复原需求回测接口：支持零需求期；全零历史返回metricsDefined=false、相对指标null并转人工复核，发布门槛使用未舍入比率。

原回测响应字段保留，新增\`metricsDefined\`。非零实际合计时为true；全零时WAPE、偏差和派生准确率返回null而非0或除零异常，releaseDecision=REVIEW。调用者应先检查该字段。允许单期实际需求为0，重复周期或负需求明确拒绝；新增4项回归覆盖全零、混合零、未舍入门槛和无效周期。

The existing backtest API now supports zero-demand periods; all-zero histories return metricsDefined=false and null relative metrics, and gate comparisons use unrounded ratios.

## 测试与限制

`AiWorkbenchServiceTest` 有14项测试：业务结果、领域边界、类型/范围校验、确定性/不修改输入、空请求、未知字段、文本/嵌套限制、独立控制器认证及错误处理。

`AiWorkbenchSecurityIntegrationTest` 另有4项测试：通过Spring Boot真实安全过滤器验证匿名拒绝、无效令牌拒绝、有效JWT计算及参数错误。测试JWT由测试上下文签发，H2集成环境不向运行配置添加账号或密钥。

本次继续运行原有测试。执行 `cd backend && mvn test` 及 `cd frontend && npm run build`。这些结果不等于已完成全部旧页面端到端测试、真实MySQL生产流程、容量、渗透或外部模型验收。既有许可不变。

## English

**Croston-SBA intermittent demand forecasting.** Implements a reproducible Croston-SBA statistical baseline for intermittent demand, including all-zero sequences and explicit initialization.

The endpoint above performs authenticated deterministic computation on caller-supplied, authorized data. Use the JSON sample, existing Bearer token and business UI. Errors and undefined metrics are explicit; results can be exported. No model invocation, record lookup/update, autonomous business decision, payment or device control is performed. Production gateway limits, data authorization and human review remain necessary. Tests cover algorithms, controllers and the real security chain with H2; they are not full production acceptance.

Commercial licensing or customization: [han@zhuatech.cn](mailto:han@zhuatech.cn), [jack@zhuatech.cn](mailto:jack@zhuatech.cn), [WhatsApp +86 17521234993](https://wa.me/8617521234993).

商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。中文README原有两个微信二维码保持不变。
