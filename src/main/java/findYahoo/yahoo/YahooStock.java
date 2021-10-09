package findYahoo.yahoo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YahooStock {

  @NotBlank(message = "게임을 선택하시지 않았습니다.")
  @NotNull
  private String stockName; // 종목명

  private String findMarketCap; // 시가총액
  private List<Map<String, Object>> lowValueInfoList = new ArrayList<>(); // 최저가 YY/MM, 가격 (상장 후, 2015이후)
  private Map<String, Object> lowValueInfoMap = new HashMap<>(); // 2020년 3월 ~ 4월 중 최저가
  private BigDecimal prevClose; // 종가
}
