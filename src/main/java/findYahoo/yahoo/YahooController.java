package findYahoo.yahoo;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
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
			String findMarketCap = yahooService.findMarketCap(stockName); // 시가총액
			
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
	public String getYahooData(@Validated @ModelAttribute YahooStock yahooStock, BindingResult bindingResult, Model model) {
		String stockName = yahooStock.getStockName();
		stockName = stockName.toUpperCase();

		try {
			Stock stock = YahooFinance.get(stockName);

			if (stock != null) {
				String findMarketCap = yahooService.findMarketCap(stockName); // 시가총액
				List<Map<String, Object>> lowValueInfoList = yahooService.lowValueByStockStartYear(stockName); // 최저가 YY/MM, 가격 (상장 후, 2005년 이후)
				BigDecimal prevClose = yahooService.prevClose(stockName); // 종가

				Calendar from = new GregorianCalendar(2020, 2, 1); // 2020년 3월 1일
				Calendar to = new GregorianCalendar(2020, 3, 30); // 2020년 4월 30일
				Map<String, Object> lowValueInfoMap = yahooService.lowValueByStock(stockName, from, to); // 2020년 3월 ~ 4월 중 최저가

				yahooStock.setStockName(stockName); // 종목명
				yahooStock.setFindMarketCap(findMarketCap); // 시가총액
				yahooStock.setLowValueInfoList(lowValueInfoList);// 최저가 YY/MM, 가격 (상장 후, 2015이후)
				yahooStock.setLowValueInfoMap(lowValueInfoMap); // 2020년 3월 ~ 4월 중 최저가
				yahooStock.setPrevClose(prevClose); // 종가

				model.addAttribute("dummy", true);
			} else {
				bindingResult.rejectValue("stockName", "errorTest", "없는 게임입니다. 다시 선택 해주시기 바랍니다.");
				bindingResult.addError(new FieldError("yahooStock", "stockName", "없는 게임입니다. 다시 선택 해주시기 바랍니다."));
				model.addAttribute("errorText", "없는 게임입니다. 다시 선택 해주시기 바랍니다.");
			}
		} catch (Exception e) {
			bindingResult.rejectValue("stockName", "errorTest", "없는 게임입니다. 다시 선택 해주시기 바랍니다.");
			bindingResult.addError(new FieldError("yahooStock", "stockName", "없는 게임입니다. 다시 선택 해주시기 바랍니다."));
			e.printStackTrace();
			model.addAttribute("errorText", "없는 게임입니다. 다시 선택 해주시기 바랍니다.");
			log.info("yahooStock eeee >>>>>>>>>>> {}", yahooStock);
			log.info("bindingResult.getTarget() >>>>>>>>>>> {}", bindingResult.getTarget());

		}
		log.info("yahooStock >>>>>>>>>>> {}", yahooStock);

		return "yahoo/yahoo-table";
	}
}
