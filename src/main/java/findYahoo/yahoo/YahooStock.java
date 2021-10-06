package findYahoo.yahoo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class YahooStock {

  private String stockName; // 종목명
  private String findMarketCap; // 시가총액
  private List<Map<String, Object>> lowValueInfoList; // 최저가 YY/MM, 가격 (상장 후, 2015이후)

  public YahooStock() {
  }

  public YahooStock(String stockName, String findMarketCap, List<Map<String, Object>> lowValueInfoMap) {
    this.stockName = stockName;
    this.findMarketCap = findMarketCap;
    this.lowValueInfoList = lowValueInfoList;
  }
}
