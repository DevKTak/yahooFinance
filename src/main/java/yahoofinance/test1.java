package yahoofinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.HistoricalDividend;
import yahoofinance.quotes.stock.StockQuote;

public class test1 {

	public static void main(String[] args) {
		Stock stock;
		HistoricalDividend his = new HistoricalDividend();
//		Calendar cal = Calendar.getInstance();
//		cal.set(1992, Calendar.JANUARY, 28);
		try {
			stock = YahooFinance.get("j");
			
//			System.out.println(stock.getDividendHistory()); // 배당율
			
//			System.out.println("quote ===> " + stock.getQuote());
			System.out.println(stock.getHistory());
//			System.out.println("splitHistory ===> " + stock.getSplitHistory());
//			System.out.println(" ===> " + stock.getQuote().getDayLow());
			System.out.println("name ===> " + stock.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
