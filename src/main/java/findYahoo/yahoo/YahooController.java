package findYahoo.yahoo;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
			Calendar from = new GregorianCalendar(2020,2, 1); // 2008년 1월 1일
			Calendar to = Calendar.getInstance(); // 현재 월 일
			Map<String, Object> lowValueInfoMap = yahooService.lowValueByStock(stockName, from, to); // 최저가
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
		model.addAttribute("dummy", false);

		return "yahoo/yahoo-table";
	}

	@PostMapping("/search/yahoo-table")
	public String getYahooData(@ModelAttribute YahooStock yahooStock, Model model) throws Exception {
		String stockName = yahooStock.getStockName();
		Stock stock = YahooFinance.get(stockName);

		if (stock != null) {
			String findMarketCap = yahooService.findtMarketCap(stockName); // 시가총액
			List<Map<String, Object>> lowValueInfoList = (List<Map<String, Object>>) yahooService.lowValueByStockStartYear(stockName); // 최저가 YY/MM, 가격 (상장후, 2015년 이후)

			Calendar from = new GregorianCalendar(2020,2, 1); // 2020년 3월 1일
			Calendar to = new GregorianCalendar(2020,3, 30); // 2020년 4월 30일
			Map<String, Object> lowValueInfoMap = yahooService.lowValueByStock(stockName, from, to); // 2020년 3월 ~ 4월 중 최저가

			yahooStock.setStockName(stockName);
			yahooStock.setLowValueInfoList(lowValueInfoList);
			yahooStock.setFindMarketCap(findMarketCap);
			yahooStock.setLowValueInfoMap(lowValueInfoMap);

			model.addAttribute("yahooStock", yahooStock);
			model.addAttribute("dummy", true);

			log.debug("yahooStock >>>>>>>>>>> {}", yahooStock);
//			model.addAttribute("stockName", stockName);
//			model.addAttribute("lowValueInfoMap", lowValueInfoMap);
//			model.addAttribute("findMarketCap", findMarketCap);
		} else {
			model.addAttribute("error", "마지막 관문에 탈락하셨습니다.");
		}

		return "yahoo/yahoo-table";
	}
}
