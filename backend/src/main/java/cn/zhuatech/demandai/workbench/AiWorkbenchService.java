/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.demandai.workbench;

import static cn.zhuatech.demandai.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "alpha", "demand");
    double alpha = decimal(r, "alpha", 0.000001, 1).doubleValue();
    var raw = list(r, "demand", 1, 500);
    double size = 0, interval = 0;
    int last = -1;
    List<Map<String, Object>> updates = new ArrayList<>();
    for (int i = 0; i < raw.size(); i++) {
      double x =
          decimal(Collections.singletonMap("value", raw.get(i)), "value", 0, 1e9).doubleValue();
      if (x == 0) continue;
      if (last < 0) {
        size = x;
        interval = i + 1;
      } else {
        size = alpha * x + (1 - alpha) * size;
        interval = alpha * (i - last) + (1 - alpha) * interval;
      }
      last = i;
      updates.add(
          Map.of(
              "period",
              i + 1,
              "smoothedSize",
              round(size),
              "smoothedInterval",
              round(interval),
              "forecast",
              round((1 - alpha / 2) * size / interval)));
    }
    return Map.of(
        "forecast",
        last < 0 ? BigDecimal.ZERO : round((1 - alpha / 2) * size / interval),
        "nonzeroPeriods",
        updates.size(),
        "allZero",
        last < 0,
        "updates",
        updates,
        "method",
        "CROSTON_SBA");
  }
}
