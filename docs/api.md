# DEMANDAI API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 知华需求预测 AI 平台运营控制台 |
| GET | `/api/admin/work-orders` | 需求预测任务列表 |
| GET | `/api/shopfloor/dashboard` | 需求预测运营台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交处理反馈 |
| POST | `/api/ai/demand/forecast` | 日需求预测、安全库存与补货信号 |
| POST | `/api/ai/demand/backtest` | 用实际值回测 WAPE、偏差和版本发布门禁 |
| POST | `/api/shopfloor/ai-risk-assessment` | AI 功能上线风险初筛 |

除登录外均需 `Authorization: Bearer <token>`。社区演示实现不调用外部模型，不需要 API Key。
