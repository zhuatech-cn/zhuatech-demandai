/* Copyright 2026 上海如静知华信息科技有限公司 */
export const domain={
 code:'DEMANDAI',systemName:'知华需求预测 AI 平台',englishName:'AI DEMAND FORECASTING',theme:{primary:'#345a78',dark:'#21374a',accent:'#d28b3f'},
 workspace:'供应链中心 / 智能计划组',fieldWorkspace:'华东区域 / 需求计划组',period:'2026-08-15 · 滚动预测',liveText:'销售与库存数据于 10:30 更新',fieldContextLabel:'预测周期',fieldContext:'W34—W41 八周滚动',fieldUser:'林澈',fieldRole:'需求计划员',adminUser:'苏岚',adminRole:'计划平台主管',
 adminTitle:'需求计划指挥台',adminBreadcrumb:'智能预测 / 全局供需',adminSubtitle:'融合销量、活动、季节和库存信号，为每个 SKU 生成可解释的预测与补货建议。',exportAction:'导出预测版本',createAction:'新建预测任务',
 chartTitle:'滚动预测达成趋势',chartSubtitle:'实际准确率 / 目标准确率',chartLabels:['W26','W27','W28','W29','W30','W31','W32','W33','W34'],loadTitle:'品类预测负荷',loadSubtitle:'需要人工干预的 SKU 比例',recordsTitle:'重点预测任务',recordsSubtitle:'按缺货风险、活动影响和预测偏差排序',issueTitle:'供需异常信号',issueSubtitle:'需要计划、销售或采购共同确认',
 recordName:'预测任务',itemName:'商品组合',unitName:'计划团队',batchName:'预测模型',planName:'SKU',doneName:'已预测',exceptionName:'偏差',unitLabel:'个',
 listBreadcrumb:'需求计划 / 预测任务',listSubtitle:'管理预测版本、业务调整、置信区间和补货建议，保留模型与人工修订轨迹。',listSummary:[['预测 SKU','2,480'],['本周任务','42'],['高偏差','16',true],['已确认','28']],tabs:['全部','待处理','进行中','待确认','已归档'],
 fieldBreadcrumb:'计划工作台 / 需求计划员',fieldTitle:'滚动预测工作台',fieldSubtitle:'负责品类 4 个 · 待确认预测 9 项 · 缺货预警 3 条',fieldSecondary:'查看活动日历',reportAction:'提交预测调整',fieldNoticeTitle:'数据准备完成',fieldNotice:'有效特征覆盖率 95.2%',
 steps:['数据准备','基线预测','事件校正','协同确认','执行复盘'],documentAction:'查看预测说明',printAction:'导出计划表',resourceCardTitle:'预测资源状态',resourceValueLabel:'今日推理量',resourceHealthLabel:'模型健康度',quickSubtitle:'常用计划入口',
 quickActions:[['预测调整','/shopfloor/report','修订数量并记录业务依据'],['商品组合','/shopfloor/material','SKU、区域和渠道层级'],['模型中心','/shopfloor/resources','版本、特征和准确率'],['供需升级','/shopfloor/andon','缺货、积压和重大活动']],
 reportDefaults:[6,1],reportTitle:'预测调整反馈',reportSubtitle:'记录调整数量、业务事件与责任人。',reportSuccess:'需求预测反馈已提交',reportPlaceholder:'填写活动、渠道、客户变化及调整依据',reportFootnote:'调整会保留模型基线并进入平台主管复核',ruleTitle:'预测发布门禁',ruleSubtitle:'DEMAND-AI · V1.0',rules:[['大幅调整','必须填写依据'],['低置信预测','人工确认'],['活动事件','销售共同确认'],['准确率复盘','每周',true]],fieldTotals:[['4','负责品类'],['9','待确认预测'],['3','缺货预警'],['95.2%','特征覆盖率']],
 adminMenus:[['/admin','home','供需指挥台'],['/admin/work-orders','order','预测任务'],['/admin/samples','box','商品组合'],['/admin/schedule','calendar','活动日历'],['/admin/methods','process','预测策略'],['/admin/reviews','quality','版本审批'],['/admin/resources','machine','模型中心'],['/admin/report','chart','准确率分析']],
 fieldMenus:[['/shopfloor','home','预测工作台'],['/shopfloor/report','report','预测调整'],['/shopfloor/tasks','order','我的任务'],['/shopfloor/material','box','商品组合'],['/shopfloor/resources','machine','模型状态'],['/shopfloor/andon','risk','供需升级',3]],
 moduleTitles:{tasks:['我的预测任务','查看期限、偏差和确认状态'],material:['商品组合台账','查看 SKU、渠道、区域和生命周期'],resources:['预测模型中心','管理模型版本、特征与评测结果'],andon:['供需问题升级','提交缺货、积压和重大事件'],samples:['商品组合台账','维护层级、负责人和预测粒度'],schedule:['活动与事件日历','管理促销、节假日和客户项目'],methods:['预测策略','维护模型路由、安全库存和业务规则'],reviews:['预测版本审批','记录人工修订与发布决策'],report:['预测表现分析','分析 WAPE、偏差和库存影响']},
 tagline:'让每一次补货，都有需求信号和业务依据',storyTitle:'从历史销量到未来需求，<br/>保留模型与人的共同判断',storyText:'把预测、活动、库存与补货建议连接在一起，让计划团队更早看见缺货和积压风险。',pattern:[3,6,8,12,15,18,21,24,27,29,31],loginStats:[['2,480','预测 SKU'],['82.6%','预测准确率'],['16','高偏差对象']],loginTitle:'需求计划指挥台',adminDemo:'供需 / 版本 / 模型',fieldDemo:'预测 / 调整 / 确认',upgrade:{eyebrow:'V1.1 预测治理',title:'回测诊断与版本发布门禁',summary:'用真实销量回看预测表现，同时识别系统性高估或低估，未达到门槛的版本自动转人工复核。',api:'POST /api/ai/demand/backtest',metrics:[['14.8%','近八周 WAPE'],['-3.2%','预测偏差'],['PASS','发布结论']]}
}
export const records=[
 {no:'DF-260815-018',name:'夏季便携风扇组合',code:'FAN-SUMMER',unit:'小家电计划组',group:'消费事业部',plan:186,done:142,exception:12,due:'08-15',batch:'TFT-v4',status:'待确认',progress:76,priority:'加急'},
 {no:'DF-260815-021',name:'商用净水滤芯组合',code:'FILTER-B2B',unit:'商用产品组',group:'大客户事业部',plan:96,done:61,exception:5,due:'08-16',batch:'GBDT+事件',status:'进行中',progress:64,priority:'关注'},
 {no:'DF-260815-026',name:'开学季办公耗材',code:'OFFICE-BTS',unit:'渠道计划组',group:'零售事业部',plan:240,done:80,exception:18,due:'08-16',batch:'TFT-v4',status:'待处理',progress:33,priority:'加急'},
 {no:'DF-260814-015',name:'常规清洁用品',code:'CLEAN-BASE',unit:'日用计划组',group:'消费事业部',plan:168,done:168,exception:3,due:'08-14',batch:'统计基线',status:'已归档',progress:100,priority:'正常'},
 {no:'DF-260815-031',name:'华东经销新品组合',code:'EAST-NEW',unit:'区域计划组',group:'渠道中心',plan:72,done:46,exception:7,due:'08-17',batch:'冷启动模型',status:'进行中',progress:64,priority:'关注'}
]
export const resources=[{code:'PIPE-01',name:'销售与库存特征管道',unit:'数据平台组',status:'运行中',health:99,value:'2,480',valueUnit:'SKU',note:'最近批次于 10:30 完成'},{code:'MODEL-02',name:'多层级需求预测模型',unit:'算法平台组',status:'运行中',health:94,value:'82.6',valueUnit:'%',note:'近四周 WAPE 持续改善'},{code:'EVENT-03',name:'促销事件连接器',unit:'销售运营组',status:'预警',health:87,value:'38',valueUnit:'场',note:'3 场活动缺少折扣深度'}]
export const reviews=[{no:'RV-260815-032',title:'开学季办公耗材上调 24%',type:'业务调整',detail:'影响 18 个 SKU · 苏岚',result:'待确认'},{no:'RV-260815-011',title:'华东滤芯项目需求',type:'大客户事件',detail:'订单意向 1,200 件 · 林澈',result:'通过'},{no:'RV-260814-018',title:'便携风扇异常高预测',type:'偏差复核',detail:'天气特征待校正',result:'异常'}]
export const adminMetrics=[['预测 SKU','2,480','覆盖 6 个事业部','blue'],['预测准确率','82.6%','较上月提升 3.4%','green'],['高偏差对象','16','其中 5 项临近发布','orange'],['缺货风险','7','预计影响销售 42 万','red']]
export const fieldMetrics=[['我的任务','9','3 项高优先级','blue'],['今日已确认','14','人工调整 5 项','green'],['缺货预警','3','需采购协同','orange'],['特征覆盖率','95.2%','活动字段仍有缺口','slate']]
export const chartActual=[71,73,75,76,78,79,80,82,83],chartTarget=[74,75,76,78,79,80,82,84,85]
export const loads=[['小家电',86,'高偏差 7 个'],['办公耗材',78,'高偏差 5 个'],['商用产品',68,'高偏差 2 个'],['日用消费',54,'高偏差 2 个']]
export const issues=[['缺货','开学季办公耗材需求上行','当前库存仅覆盖 6.4 天','待确认'],['事件','华东直播活动缺少折扣深度','影响 26 个 SKU 预测','补充中'],['积压','便携风扇库存覆盖升至 96 天','建议下调后续采购','待处理']].map(x=>({type:x[0],title:x[1],detail:x[2],status:x[3]}))
