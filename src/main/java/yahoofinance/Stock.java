package yahoofinance;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yahoofinance.histquotes.HistQuotesRequest;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.histquotes2.HistDividendsRequest;
import yahoofinance.histquotes2.HistQuotes2Request;
import yahoofinance.histquotes2.HistSplitsRequest;
import yahoofinance.histquotes2.HistoricalDividend;
import yahoofinance.histquotes2.HistoricalSplit;
import yahoofinance.quotes.query1v7.StockQuotesQuery1V7Request;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.csv.StockQuotesData;
import yahoofinance.quotes.csv.StockQuotesRequest;
import yahoofinance.quotes.stock.StockStats;

/**
 *
 * @author Stijn Strickx
 */
public class Stock {

    private static final Logger log = LoggerFactory.getLogger(Stock.class);
  
    private final String symbol;
    private String name;
    private String currency;
    private String stockExchange;
    
    private StockQuote quote;
    private StockStats stats;
    private StockDividend dividend;
    
    private List<HistoricalQuote> history;
    private List<HistoricalDividend> dividendHistory;
    private List<HistoricalSplit> splitHistory;
    
    public Stock(String symbol) {
        this.symbol = symbol;
    }
    
    private void update() throws IOException {
        if(YahooFinance.QUOTES_QUERY1V7_ENABLED.equalsIgnoreCase("true")) {
            StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request(this.symbol);
            Stock stock = request.getSingleResult();
            if (stock != null) {
                this.setName(stock.getName());
                this.setCurrency(stock.getCurrency());
                this.setStockExchange(stock.getStockExchange());
                this.setQuote(stock.getQuote());
                this.setStats(stock.getStats());
                this.setDividend(stock.getDividend());
                log.info("Updated Stock with symbol: {}", this.symbol);
            } else {
                log.error("Failed to update Stock with symbol: {}", this.symbol);
            }
        } else {
            StockQuotesRequest request = new StockQuotesRequest(this.symbol);
            StockQuotesData data = request.getSingleResult();
            if (data != null) {
                this.setQuote(data.getQuote());
                this.setStats(data.getStats());
                this.setDividend(data.getDividend());
                log.info("Updated Stock with symbol: {}", this.symbol);
            } else {
                log.error("Failed to update Stock with symbol: {}", this.symbol);
            }
        }
    }

    /**
     * Checks if the returned name is null. This probably means that the symbol was not recognized by Yahoo Finance.
     * @return whether this stock's symbol is known by Yahoo Finance (true) or not (false)
     */
    public boolean isValid() {
        return this.name != null;
    }
    
    /**
     * Returns the basic quotes data available for this stock.
     * 
     * @return      basic quotes data available for this stock
     * @see         #getQuote(boolean) 
     */
    public StockQuote getQuote() {
        return this.quote;
    }
    
    /**
     * Returns the basic quotes data available for this stock.
     * This method will return null in the following situations:
     * <ul>
     * <li> the data hasn't been loaded yet
     *      in a previous request and refresh is set to false.
     * <li> refresh is true and the data cannot be retrieved from Yahoo Finance 
     *      for whatever reason (symbol not recognized, no network connection, ...)
     * </ul>
     * <p>
     * When the quote data gets refreshed, it will automatically also refresh
     * the statistics and dividend data of the stock from Yahoo Finance 
     * in the same request.
     * 
     * @param refresh   indicates whether the data should be requested again to Yahoo Finance
     * @return          basic quotes data available for this stock
     * @throws IOException when there's a connection problem
     */
    public StockQuote getQuote(boolean refresh) throws IOException {
        if(refresh) {
            this.update();
        }
        return this.quote;
    }
    
    public void setQuote(StockQuote quote) {
        this.quote = quote;
    }
    
    /**
     * Returns the statistics available for this stock.
     * 
     * @return      statistics available for this stock
     * @see         #getStats(boolean) 
     */
    public StockStats getStats() {
        return this.stats;
    }
    
    /**
     * Returns the statistics available for this stock.
     * This method will return null in the following situations:
     * <ul>
     * <li> the data hasn't been loaded yet
     *      in a previous request and refresh is set to false.
     * <li> refresh is true and the data cannot be retrieved from Yahoo Finance 
     *      for whatever reason (symbol not recognized, no network connection, ...)
     * </ul>
     * <p>
     * When the statistics get refreshed, it will automatically also refresh
     * the quote and dividend data of the stock from Yahoo Finance 
     * in the same request.
     * 
     * @param refresh   indicates whether the data should be requested again to Yahoo Finance
     * @return          statistics available for this stock
     * @throws IOException when there's a connection problem
     */
    public StockStats getStats(boolean refresh) throws IOException {
        if(refresh) {
            this.update();
        }
        return this.stats;
    }
    
    public void setStats(StockStats stats) {
        this.stats = stats;
    }
    
    /**
     * Returns the dividend data available for this stock.
     * 
     * @return      dividend data available for this stock
     * @see         #getDividend(boolean) 
     */
    public StockDividend getDividend() {
    	System.out.println("dividend =>>> " + this.dividend);
        return this.dividend;
    }
    
    /**
     * Returns the dividend data available for this stock.
     * 
     * This method will return null in the following situations:
     * <ul>
     * <li> the data hasn't been loaded yet
     *      in a previous request and refresh is set to false.
     * <li> refresh is true and the data cannot be retrieved from Yahoo Finance 
     *      for whatever reason (symbol not recognized, no network connection, ...)
     * </ul>
     * <p>
     * When the dividend data get refreshed, it will automatically also refresh
     * the quote and statistics data of the stock from Yahoo Finance 
     * in the same request.
     * 
     * @param refresh   indicates whether the data should be requested again to Yahoo Finance
     * @return          dividend data available for this stock
     * @throws IOException when there's a connection problem
     */
    public StockDividend getDividend(boolean refresh) throws IOException {
        if(refresh) {
            this.update();
        }
        return this.dividend;
    }
    
    public void setDividend(StockDividend dividend) {
        this.dividend = dividend;
    }
    
    /**
     * This method will return historical quotes from this stock.
     * If the historical quotes are not available yet, they will 
     * be requested first from Yahoo Finance.
     * <p>
     * If the historical quotes are not available yet, the
     * following characteristics will be used for the request:
     * <ul>
     * <li> from: 1 year ago (default)
     * <li> to: today (default)
     * <li> interval: MONTHLY (default)
     * </ul>
     * <p>
     * There are several more methods available that allow you
     * to define some characteristics of the historical data.
     * Calling one of those methods will result in a new request
     * being sent to Yahoo Finance.
     * 
     * @return      a list of historical quotes from this stock
     * @throws IOException when there's a connection problem
     * @see         #getHistory(Interval)
     * @see         #getHistory(Calendar)
     * @see         #getHistory(Calendar, Calendar)
     * @see         #getHistory(Calendar, Interval)
     * @see         #getHistory(Calendar, Calendar, Interval)
     */
    public List<HistoricalQuote> getHistory() throws IOException {
        if(this.history != null) {
            return this.history;
        }
        return this.getHistory(HistQuotesRequest.DEFAULT_FROM);
    }
    
    /**
     * Requests the historical quotes for this stock with the following characteristics.
     * <ul>
     * <li> from: 1 year ago (default)
     * <li> to: today (default)
     * <li> interval: specified value
     * </ul>
     * 
     * @param interval      the interval of the historical data
     * @return              a list of historical quotes from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getHistory() 
     */
    public List<HistoricalQuote> getHistory(Interval interval) throws IOException {
        return this.getHistory(HistQuotesRequest.DEFAULT_FROM, interval);
    }
    
    /**
     * Requests the historical quotes for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: today (default)
     * <li> interval: MONTHLY (default)
     * </ul>
     * 
     * @param from          start date of the historical data
     * @return              a list of historical quotes from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getHistory() 
     */
    public List<HistoricalQuote> getHistory(Calendar from) throws IOException {
        return this.getHistory(from, HistQuotesRequest.DEFAULT_TO);
    }
    
    /**
     * Requests the historical quotes for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: today (default)
     * <li> interval: specified value
     * </ul>
     * 
     * @param from          start date of the historical data
     * @param interval      the interval of the historical data
     * @return              a list of historical quotes from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getHistory() 
     */
    public List<HistoricalQuote> getHistory(Calendar from, Interval interval) throws IOException {
        return this.getHistory(from, HistQuotesRequest.DEFAULT_TO, interval);
    }
    
    /**
     * Requests the historical quotes for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: specified value
     * <li> interval: MONTHLY (default)
     * </ul>
     * 
     * @param from          start date of the historical data
     * @param to            end date of the historical data
     * @return              a list of historical quotes from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getHistory() 
     */
    public List<HistoricalQuote> getHistory(Calendar from, Calendar to) throws IOException {
        return this.getHistory(from, to, Interval.MONTHLY);
    }
    
    /**
     * Requests the historical quotes for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: specified value
     * <li> interval: specified value
     * </ul>
     * 
     * @param from          start date of the historical data
     * @param to            end date of the historical data
     * @param interval      the interval of the historical data
     * @return              a list of historical quotes from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getHistory() 
     */
    public List<HistoricalQuote> getHistory(Calendar from, Calendar to, Interval interval) throws IOException {
        if(YahooFinance.HISTQUOTES2_ENABLED.equalsIgnoreCase("true")) {
            HistQuotes2Request hist = new HistQuotes2Request(this.symbol, from, to, interval);
            this.setHistory(hist.getResult());
        } else {
            HistQuotesRequest hist = new HistQuotesRequest(this.symbol, from, to, interval);
            this.setHistory(hist.getResult());
        }
        return this.history;
    }
    
    public void setHistory(List<HistoricalQuote> history) {
        this.history = history;
       
    }
    
    public HashMap<String, Object> getLowValue(Calendar from, Calendar to, Interval interval) throws IOException {
    	HistQuotes2Request hist= new HistQuotes2Request(this.symbol, from, to, interval);
    	List<HistoricalQuote> history = hist.getResult();

    	String temp;
        int tempI = 0;
        double lowValue = 10000000d;
         
        for (int i = 0; i < history.size(); i++) {
//        	temp = history.get(i).getClose().setScale(2, RoundingMode.HALF_EVEN).doubleValue(); // 소수 2번째 자리까지 반올림 BigDecimal => double 형변환
        	temp = String.format("%.2f", history.get(i).getClose().doubleValue()); // 소수 2번째 자리까지 반올림 BigDecimal => double 형변환
//        	System.out.println("최저가 =>>> " + temp);
        	if (Double.valueOf(temp) < lowValue) {
        		lowValue = Double.valueOf(temp);
        		tempI = i;
        	}
        }
         
         int year = history.get(tempI).getDate().get(1);
         int month = history.get(tempI).getDate().get(2) + 1;
         
         System.out.println("\n");
         System.out.println("=============== " + this.symbol + " ===============");
         System.out.println("\nDate ===> " + year + "년 " + month + "월  | "
         						+ "최저가 ===> " + lowValue + "\n");
         System.out.println("========================================");
         System.out.println("\n");
         
         HashMap<String, Object> map = new HashMap<String, Object>();
         map.put("lowValueDate", String.valueOf(year).substring(2) + "/" + (month < 10 ? "0" : "") + month);
         map.put("lowValue", lowValue);
         
         return map;
    	
    }
    
    /**
     * This method will return historical dividends from this stock.
     * If the historical dividends are not available yet, they will 
     * be requested first from Yahoo Finance.
     * <p>
     * If the historical dividends are not available yet, the
     * following characteristics will be used for the request:
     * <ul>
     * <li> from: 1 year ago (default)
     * <li> to: today (default)
     * </ul>
     * <p>
     * There are several more methods available that allow you
     * to define some characteristics of the historical data.
     * Calling one of those methods will result in a new request
     * being sent to Yahoo Finance.
     * 
     * @return      a list of historical dividends from this stock
     * @throws IOException when there's a connection problem
     * @see         #getDividendHistory(Calendar)
     * @see         #getDividendHistory(Calendar, Calendar)
     */
    public List<HistoricalDividend> getDividendHistory() throws IOException {
        if(this.dividendHistory != null) {
//        	System.out.println("getDividendHistory =>>> null");
            return this.dividendHistory;
        }
//        System.out.println("getDividendHistory =>>> " + this.getDividendHistory(HistDividendsRequest.DEFAULT_FROM));
        return this.getDividendHistory(HistDividendsRequest.DEFAULT_FROM);
    }
    
    /**
     * Requests the historical dividends for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: today (default)
     * </ul>
     * 
     * @param from          start date of the historical data
     * @return              a list of historical dividends from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getDividendHistory() 
     */
    public List<HistoricalDividend> getDividendHistory(Calendar from) throws IOException {
        return this.getDividendHistory(from, HistDividendsRequest.DEFAULT_TO);
    }
    
    /**
     * Requests the historical dividends for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: specified value
     * </ul>
     * 
     * @param from          start date of the historical data
     * @param to            end date of the historical data
     * @return              a list of historical dividends from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getDividendHistory() 
     */
    public List<HistoricalDividend> getDividendHistory(Calendar from, Calendar to) throws IOException {
        if(YahooFinance.HISTQUOTES2_ENABLED.equalsIgnoreCase("true")) {
            HistDividendsRequest histDiv = new HistDividendsRequest(this.symbol, from, to);
            this.setDividendHistory(histDiv.getResult());
        } else {
        	// Historical dividends cannot be retrieved without CRUMB
        	this.setDividendHistory(null);
        }
        return this.dividendHistory;
    }
    
    public void setDividendHistory(List<HistoricalDividend> dividendHistory) {
        this.dividendHistory = dividendHistory;
    }
    
    /**
     * This method will return historical splits from this stock.
     * If the historical splits are not available yet, they will 
     * be requested first from Yahoo Finance.
     * <p>
     * If the historical splits are not available yet, the
     * following characteristics will be used for the request:
     * <ul>
     * <li> from: 1 year ago (default)
     * <li> to: today (default)
     * </ul>
     * <p>
     * There are several more methods available that allow you
     * to define some characteristics of the historical data.
     * Calling one of those methods will result in a new request
     * being sent to Yahoo Finance.
     * 
     * @return      a list of historical splits from this stock
     * @throws IOException when there's a connection problem
     * @see         #getSplitHistory(Calendar)
     * @see         #getSplitHistory(Calendar, Calendar)
     */
    public List<HistoricalSplit> getSplitHistory() throws IOException {
        if(this.splitHistory != null) {
            return this.splitHistory;
        }
        return this.getSplitHistory(HistSplitsRequest.DEFAULT_FROM);
    }
    
    /**
     * Requests the historical splits for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: today (default)
     * </ul>
     * 
     * @param from          start date of the historical data
     * @return              a list of historical splits from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getSplitHistory() 
     */
    public List<HistoricalSplit> getSplitHistory(Calendar from) throws IOException {
        return this.getSplitHistory(from, HistSplitsRequest.DEFAULT_TO);
    }
    
    /**
     * Requests the historical splits for this stock with the following characteristics.
     * <ul>
     * <li> from: specified value
     * <li> to: specified value
     * </ul>
     * 
     * @param from          start date of the historical data
     * @param to            end date of the historical data
     * @return              a list of historical splits from this stock
     * @throws IOException when there's a connection problem
     * @see                 #getSplitHistory() 
     */
    public List<HistoricalSplit> getSplitHistory(Calendar from, Calendar to) throws IOException {
        if(YahooFinance.HISTQUOTES2_ENABLED.equalsIgnoreCase("true")) {
            HistSplitsRequest histSplit = new HistSplitsRequest(this.symbol, from, to);
            this.setSplitHistory(histSplit.getResult());
        } else {
        	// Historical splits cannot be retrieved without CRUMB
        	this.setSplitHistory(null);
        }
        return this.splitHistory;
    }
    
    public void setSplitHistory(List<HistoricalSplit> splitHistory) {
        this.splitHistory = splitHistory;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * Get the full name of the stock
     * 
     * @return the name or null if the data is not available
     */
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the currency of the stock
     * 
     * @return the currency or null if the data is not available
     */
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    /**
     * Get the exchange on which the stock is traded
     * 
     * @return the exchange or null if the data is not available
     */
    public String getStockExchange() {
        return stockExchange;
    }
    
    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }
    
    @Override
    public String toString() {
        return this.symbol + ": " + this.quote.getPrice();
    }
    
    public void print() {
        System.out.println(this.symbol);
        System.out.println("--------------------------------");
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                System.out.println(f.getName() + ": " + f.get(this));
            } catch (IllegalArgumentException ex) {
                log.error(null, ex);
            } catch (IllegalAccessException ex) {
                log.error(null, ex);
            }
        }
        System.out.println("--------------------------------");
    }
    
}
