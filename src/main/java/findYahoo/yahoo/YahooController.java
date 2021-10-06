package findYahoo.yahoo;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Controller
@RequiredArgsConstructor
@Slf4j
public class YahooController {
	
	private final YahooService yahooService;

	@GetMapping("/yahoo")
	public String yahooForm() {
		return "yahoo/yahoo";
	}

	@PostMapping("/yahoo")
	public String yahoo(@RequestParam("stockName") String stockName, Model model) throws IOException {
		Stock stock = YahooFinance.get(stockName);
		
		if (stock != null) {
			HashMap<String, Object> lowValueInfoMap = (HashMap<String, Object>) yahooService.lowValueByStock(stockName); // 최저가
			List<HashMap<String, Object>> diviInfoList = yahooService.dividendByStock(stockName); // 배당금
			BigDecimal prevClose = yahooService.prevClose(stockName); // 종가
			String findMarketCap = yahooService.findtMarketCap(stockName); // 시가총액
			
			model.addAttribute("stockName", stockName);
			model.addAttribute("lowValueInfoMap", lowValueInfoMap);
			model.addAttribute("diviInfoList", diviInfoList);
			model.addAttribute("prevClose", prevClose);
			model.addAttribute("findMarketCap", findMarketCap);
		}
		
		return "yahoo/yahoo";
	}

	@GetMapping("/search/yahoo-table")
	public String yahooTableForm(Model model) {
		model.addAttribute("yahooStock", new YahooStock());
		return "yahoo/yahoo-table";
	}

	@PostMapping("/search/yahoo-table")
	public String getYahooData(@ModelAttribute YahooStock yahooStock, Model model) throws Exception {
		String stockName = yahooStock.getStockName();
		Stock stock = YahooFinance.get(stockName);

		if (stock != null) {
			List<Map<String, Object>> lowValueInfoList = (List<Map<String, Object>>) yahooService.lowValueByStockStartYear(stockName); // 최저가(상장후, 2015년 이후)
			String findMarketCap = yahooService.findtMarketCap(stockName); // 시가총액

			yahooStock.setStockName(stockName);
			yahooStock.setLowValueInfoList(lowValueInfoList);
			yahooStock.setFindMarketCap(findMarketCap);

			model.addAttribute("yahooStock", yahooStock);

			log.debug("yahooStock >>>>>>>>>>> {}", yahooStock);
//			model.addAttribute("stockName", stockName);
//			model.addAttribute("lowValueInfoMap", lowValueInfoMap);
//			model.addAttribute("findMarketCap", findMarketCap);
		}

		return "yahoo/yahoo-table";
	}
}
