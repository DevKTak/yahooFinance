package findYahoo.yahoo;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;
import yahoofinance.histquotes2.HistoricalDividend;

@Service
@Slf4j
public class YahooService {
	
	Stock stock;
	final Calendar _DEFAULT_FROM = new GregorianCalendar(2008,0, 1);
	final Calendar _DEFAULT_TO = Calendar.getInstance();
	
	/**
	 * 최저가
	 */
	public Map<String, Object> lowValueByStock(String stockName, Calendar from, Calendar to) {
		try {
			stock = YahooFinance.get(stockName);
			HashMap<String, Object> lowValueMap = stock.getLowValue(from, to, Interval.DAILY);
			
			return lowValueMap;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("최저가 에러");
		}
	}

	/**
	 * 최저가 YY/MM, 가격 (상장 후, 2005년 이후)
	 */
	public List<Map<String, Object>> lowValueByStockStartYear(String stockName) {
		try {
			stock = YahooFinance.get(stockName);

			List<Map<String, Object>> lowValueList = new ArrayList<>();
			lowValueList.add(stock.getLowValue(new GregorianCalendar(1800,0, 1), _DEFAULT_TO, Interval.DAILY));
			lowValueList.add(stock.getLowValue(new GregorianCalendar(2005,0, 1), _DEFAULT_TO, Interval.DAILY));

			return lowValueList;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("최저가 에러");
		}
	}

	/**
	 * 종가
	 * @return 
	 */
	public BigDecimal prevClose(String stockName) {
		try {
			stock = YahooFinance.get(stockName);
			
			return stock.getQuote().getPreviousClose();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("종가 에러");
		}
	}
	
	/**
	 * 배당금
	 */
	public List<HashMap<String, Object>> dividendByStock(String stockName) {
		try {
			stock = YahooFinance.get(stockName); // stock 데이터 가져오기
			List<HistoricalDividend> dividendList = stock.getDividendHistory(); // stock 배당금 정보 가져오기
			Calendar date;
			int[] cnt = new int[13];
			int[] yearArr = new int[13];
			int[] maxMonth = new int[13];
			double[] sum = new double[13];
			
			for (HistoricalDividend diviObj : dividendList) {
				int year = 0, month = 0;
				double dividend = 0.0d;
				date = diviObj.getDate();
				year = date.get(Calendar.YEAR);
				month = date.get(Calendar.MONTH) + 1;
				dividend = diviObj.getAdjDividend().doubleValue();
				
				switch (year) {
					case 2008:
						yearArr[0] = 2008;
						cnt[0]++;
						sum[0] += dividend;
						if (maxMonth[0] < month) maxMonth[0] = month;
						break;
					case 2009:
						yearArr[1] = 2009;
						cnt[1]++;
						sum[1] += dividend;
						if (maxMonth[1] < month) maxMonth[1] = month;
						break;
					case 2010:
						yearArr[2] = 2010;
						cnt[2]++;
						sum[2] += dividend;
						if (maxMonth[2] < month) maxMonth[2] = month;
						break;
					case 2011:
						yearArr[3] = 2011;
						cnt[3]++;
						sum[3] += dividend;
						if (maxMonth[3] < month) maxMonth[3] = month;
						break;
					case 2012:
						yearArr[4] = 2012;
						cnt[4]++;
						sum[4] += dividend;
						if (maxMonth[4] < month) maxMonth[4] = month;
						break;
					case 2013:
						yearArr[5] = 2013;
						cnt[5]++;
						sum[5] += dividend;
						if (maxMonth[5] < month) maxMonth[5] = month;
						break;
					case 2014:
						yearArr[6] = 2014;
						cnt[6]++;
						sum[6] += dividend;
						if (maxMonth[6] < month) maxMonth[6] = month;
						break;
					case 2015:
						yearArr[7] = 2015;
						cnt[7]++;
						sum[7] += dividend;
						if (maxMonth[7] < month) maxMonth[7] = month;
						break;
					case 2016:
						yearArr[8] = 2016;
						cnt[8]++;
						sum[8] += dividend;
						if (maxMonth[8] < month) maxMonth[8] = month;
						break;
					case 2017:
						yearArr[9] = 2017;
						cnt[9]++;
						sum[9] += dividend;
						if (maxMonth[9] < month) maxMonth[9] = month;
						break;
					case 2018:
						yearArr[10] = 2018;
						cnt[10]++;
						sum[10] += dividend;
						if (maxMonth[10] < month) maxMonth[10] = month;
						break;
					case 2019:
						yearArr[11] = 2019;
						cnt[11]++;
						sum[11] += dividend;
						if (maxMonth[11] < month) maxMonth[11] = month;
						break;
					case 2020:
						yearArr[12] = 2020;
						cnt[12]++;
						sum[12] += dividend;
						if (maxMonth[12] < month) maxMonth[12] = month;
						break;
				}
			}
			List<HashMap<String, Object>> diviInfoList = new ArrayList<HashMap<String, Object>>();
			
			for (int i = yearArr.length - 1; i >= 0 ; i--) {
				HashMap<String, Object> diviInfoMap = new HashMap<>();
				String year = String.valueOf(yearArr[i]);
				diviInfoMap.put("diviDateWithCnt", year.substring(2) + "/" + (maxMonth[i] < 10 ? "0" : "") + maxMonth[i] + " (" + cnt[i] + ")");
				diviInfoMap.put("diviSum", String.format("%.2f", sum[i]));
				diviInfoList.add(diviInfoMap);
			}
			
			return diviInfoList;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("배당금 에러");
		}
	}
	
	/**
	 * 시가총액
	 */
	public String findMarketCap(String stockName) {
		try {
			stock = YahooFinance.get(stockName);
			BigDecimal marketCapB = stock.getStats().getMarketCap();

			if (marketCapB != null) {
				log.info("marketCapB = {}", marketCapB);

				double marketCapD = marketCapB.doubleValue();
				log.info("marketCapD1 = {}", marketCapD);

				marketCapD /= 1000000;
				log.info("marketCapD2 = {}", marketCapD);


				DecimalFormat decimalFormat = new DecimalFormat("#,##0");
				String result = decimalFormat.format(marketCapD) + "M";
				log.info("result = {}", result);

				return result;

//				log.info("시가총액 나누기전 =>>> " + marketCapL);
//
//				marketCapL /= 1000000;
//
//				log.info("시가총액 나눈 후 =>>> " + marketCapL);
//
//				marketCapS = String.valueOf(marketCapL);
//				int marketCapLenth = marketCapS.length();
//
//				// ATHX StringIndexOutOfBoundsException: begin 0, end -1, length 2 에러 남 (나중에 확인해 볼 것!!) ALNA, ACOR
//				marketCapS = marketCapS.substring(0, marketCapLenth - 3) + "." + marketCapS.substring(marketCapLenth - 3, marketCapLenth);
//
////			log.info("시가총액 문자열 =>>> " + marketCapS);
//
//				marketCapD = Double.parseDouble(marketCapS);
//				marketCapS = String.format("%.1f", marketCapD) + "B";
//
////			log.info("시가총액 반올림 =>>> " + marketCapS);

//				return marketCapS;
			}
			return "없음";
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("시가총액 에러");
		}
	}
}
