package com.bcb.helper.market;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.KeyValueBean;
import com.bcb.bean.NXbean;
import com.bcb.bean.NXbeanItem;
import com.bcb.bean.NXvo.JsonRootBean;
import com.bcb.domain.enums.IBaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class BbexHelper {
	private static String url = "http://mkt.bbex.io/api/marketprices?lang=en&market=%s&pageNo=%s&pageSize=%s";
	private static String c2cUrl = "http://mkt.bbex.io/api/otcmarketprices?buyOrSell=0&lang=en&pageNo=%s&pageSize=%s";
	private static String searchUrl = "http://mkt.bbex.io/api/marketprices/search?key=%s&lang=en";

//	private static String nxUrl = "https://exapi.6x.com/exapi/api/klinevtwo/message";
//	private static String USDtoRMBUrl="https://exapi.6x.com/exapi/api/klinevtwo/getC2CPrice?coinCode=USDX";

	private static String nxUrl = "https://test.6x.com/exapi/api/klinevtwo/message";
	private static String USDtoRMBUrl="https://test.6x.com/exapi/api/klinevtwo/getC2CPrice?coinCode=USDX";

	private static double DEFALUT_USDX2RMB = 6.56f;

	private static String NiuX_DATA = "";

	private static String niuX_EthBcBValue = "";

	private static Map<String,String> niuX_EthBcBMapValue = new HashMap<String,String>();

	public static List<KeyValueBean> getMarketType() {
		List<KeyValueBean> list = new ArrayList<>();
		list.add(new KeyValueBean("TOP100", MarketEnum.TOP100.getDisplay()));
		list.add(new KeyValueBean("C2C", "C2C"));
		list.add(new KeyValueBean("BINANCE", MarketEnum.BINANCE.getDisplay()));
		list.add(new KeyValueBean("HUOBI", MarketEnum.HUOBI.getDisplay()));
		list.add(new KeyValueBean("GATE", MarketEnum.GATE.getDisplay()));
		list.add(new KeyValueBean("POLONIEX", MarketEnum.POLONIEX.getDisplay()));
		list.add(new KeyValueBean("BITTREX", MarketEnum.BITTREX.getDisplay()));

		return list;
	}

	public static List<KeyValueBean> getMarketEnglishType() {
		List<KeyValueBean> list = new ArrayList<>();
		list.add(new KeyValueBean("TOP100", MarketEnglishEnum.TOP100.getDisplay()));
		list.add(new KeyValueBean("C2C", "C2C"));
		list.add(new KeyValueBean("BINANCE", MarketEnglishEnum.BINANCE.getDisplay()));
		list.add(new KeyValueBean("HUOBI", MarketEnglishEnum.HUOBI.getDisplay()));
		list.add(new KeyValueBean("GATE", MarketEnglishEnum.GATE.getDisplay()));
		list.add(new KeyValueBean("POLONIEX", MarketEnglishEnum.POLONIEX.getDisplay()));
		list.add(new KeyValueBean("BITTREX", MarketEnglishEnum.BITTREX.getDisplay()));

		return list;
	}

	public static String getMarketInfo(String type, Integer pageIndex, Integer pageSize) {
		String tempUrl;
		switch (type.toUpperCase()) {
		case "TOP100":
			tempUrl = String.format(url, MarketEnum.TOP100.getValue(), pageIndex, pageSize);
			break;
		case "BINANCE":
			tempUrl = String.format(url, MarketEnum.BINANCE.getValue(), pageIndex, pageSize);
			break;
		case "POLONIEX":
			tempUrl = String.format(url, MarketEnum.POLONIEX.getValue(), pageIndex, pageSize);
			break;
		case "BITTREX":
			tempUrl = String.format(url, MarketEnum.BITTREX.getValue(), pageIndex, pageSize);
			break;
		case "HUOBI":
			tempUrl = String.format(url, MarketEnum.HUOBI.getValue(), pageIndex, pageSize);
			break;
		case "GATE":
			tempUrl = String.format(url, MarketEnum.GATE.getValue(), pageIndex, pageSize);
			break;
		case "C2C":
			tempUrl = String.format(c2cUrl, pageIndex, pageSize);
			break;
		default:
			tempUrl = String.format(searchUrl, type, pageIndex, pageSize);
			break;
		}

		return executeGet(tempUrl);
	}

	/**
	 * 获取实时Eth兑美元价格
	 */
	public static BigDecimal getEthDollar() {
		BigDecimal ethDollar = null;
		String result = BbexHelper.getMarketInfo("TOP100", 0, 20);
		if (result == null) {
			log.error("=====getEthDollar:getMarketInfo TOP100 error");
			return null;
		}

		JSONArray jsonArray = JSON.parseObject(result).getJSONArray("data");
		if (null != jsonArray) {
			for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
				JSONObject jsonObject = (JSONObject) iterator.next();
				if (jsonObject.getString("name").equalsIgnoreCase("ETH")) {
					String dollar = jsonObject.getString("dollar");
					dollar = dollar.replace("≈", "").replace("$", "").replace(",", "");
					ethDollar = new BigDecimal(dollar);
					break;
				}
			}
		}
		return ethDollar;
	}

	// 执行get请求
	private static String executeGet(String url) {
		//HttpClient httpClient = new DefaultHttpClient();
		HttpClient httpClient = HttpClients.createDefault(); //new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String entityStr = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			entityStr = EntityUtils.toString(entity);
			//log.info("=====statusCode:" + statusCode + "|响应返回内容:" + entityStr);
		} catch (Exception e) {
			log.error("执行接口get请求错误", e);
		}
		return entityStr;
	}

	// bbex 市场枚举
	private enum MarketEnum implements IBaseEnum {
		TOP100(1, "牛X"),
		BINANCE(2, "币安"),
		POLONIEX(3, "Poloniex"),
		BITTREX(4, "BitTrex"),
		HUOBI(83, "火币"),
		GATE(91, "Gate");

		private Integer value;
		private String display;
		private static Map<Integer, MarketEnum> valueMap = new HashMap<>();

		static {
			for (MarketEnum e : MarketEnum.values()) {
				valueMap.put(e.value, e);
			}
		}

		MarketEnum(Integer value, String display) {
			this.value = value;
			this.display = display;
		}

		@Override
		public Integer getValue() {
			return this.value;
		}

		@Override
		public String getDisplay() {
			return this.display;
		}

		public static MarketEnum getByValue(Integer value) {
			MarketEnum result = valueMap.get(value);
			if (result == null) {
				throw new IllegalArgumentException("No element matches " + value);
			}
			return result;
		}
	}

	// bbex 市场枚举英文
	private enum MarketEnglishEnum implements IBaseEnum {
		TOP100(1, "6X"),
		BINANCE(2, "Binance"),
		POLONIEX(3, "Poloniex"),
		BITTREX(4, "BitTrex"),
		HUOBI(83, "Huobi"),
		GATE(91, "Gate");

		private Integer value;
		private String display;
		private static Map<Integer, MarketEnglishEnum> valueMap = new HashMap<>();

		static {
			for (MarketEnglishEnum e : MarketEnglishEnum.values()) {
				valueMap.put(e.value, e);
			}
		}

		MarketEnglishEnum(Integer value, String display) {
			this.value = value;
			this.display = display;
		}

		@Override
		public Integer getValue() {
			return this.value;
		}

		@Override
		public String getDisplay() {
			return this.display;
		}

		public static MarketEnglishEnum getByValue(Integer value) {
			MarketEnglishEnum result = valueMap.get(value);
			if (result == null) {
				throw new IllegalArgumentException("No element matches " + value);
			}
			return result;
		}
	}
	/*
	 * 处理https GET/POST请求
	 * 请求地址、请求方法、参数
	 * */
	private static String httpsRequest(String requestUrl,String requestMethod,String outputStr){
		StringBuffer buffer = new StringBuffer();
		try{
			//创建SSLContext
			SSLContext sslContext= SSLContext.getInstance("SSL");
			TrustManager[] tm={new MyX509TrustManager()};
			//初始化
			sslContext.init(null, tm, new java.security.SecureRandom());;
			// 获取SSLSocketFactory对象
			SSLSocketFactory ssf=sslContext.getSocketFactory();
			URL url=new URL(requestUrl);
			HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
			
			// 设置超时时间，防止出现卡住的情况，单位ms，1.5min
			conn.setConnectTimeout(90000);
			conn.setReadTimeout(90000);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			//设置当前实例使用的SSLSoctetFactory
			conn.setSSLSocketFactory(ssf);
			conn.connect();
			//往服务器端写内容
			if(null!=outputStr){
				OutputStream os=conn.getOutputStream();
				os.write(outputStr.getBytes("utf-8"));
				os.close();
			}
			//读取服务器端返回的内容
			InputStream is=conn.getInputStream();
			InputStreamReader isr=new InputStreamReader(is,"utf-8");
			BufferedReader br=new BufferedReader(isr);
			String line=null;
			while((line=br.readLine())!=null){
				buffer.append(line);
			}
		}catch(Exception e){
			log.error("抓取https出错", e);
			return null;
		}
		return new String(buffer);
	}

	public static String niuXEthBcBValueLast(String coinType) {
		String returnStr = niuXEthBcBValue(coinType);
		if (null == returnStr) {
			returnStr = niuX_EthBcBValue;
		}
		else {
			niuX_EthBcBValue = returnStr;
		}

		return returnStr;
	}

	public static Map<String,String> niuXEthBcBMapValueLast(String coinType){
		Map<String,String> map = niuXEthBcBMapValue(coinType);
		if (null == map || 0 == map.size()) {
			map = niuX_EthBcBMapValue;
		}
		else {
			niuX_EthBcBMapValue = map;
		}

		return map;
	}
	/**
	 *  获取代币最新汇率
	 * @param coinType
	 * @return
	 */
	public static Map<String,String> niuXEthBcBMapValue(String coinType){
		String result=httpsRequest(nxUrl,"GET",null);
		Map<String,String> map = new HashMap<String,String>();

		if (result == null) {
			log.error("=====niuXEthBcBValue,httpsRequest="+nxUrl);
			return null;
		}

		JSONObject jsonObject  =  JSON.parseObject(result).getJSONObject("marketDetail");
		if (jsonObject == null) {
			log.error("=====return:" + result);
			return null;
		}
		List<JsonRootBean> ls = new ArrayList<>();
		for (Iterator iterator = jsonObject.keySet().iterator();iterator.hasNext();){
			ls.add((JsonRootBean) JSONObject.toJavaObject(jsonObject.getJSONArray(iterator.next().toString()).getJSONObject(0), JsonRootBean.class));
		}
		for (JsonRootBean jrt: ls) {
			double price = jrt.getPayload().getPriceNew();
			map.put(jrt.getSymbolId(), new Double(price).toString());
		}

		return map;
	}

	public static String niuXEthBcBValue(String coinType) {
		String result=httpsRequest(nxUrl,"GET",null);
		if (result == null) {
			log.error("=====niuXEthBcBValue,httpsRequest="+nxUrl);
			return null;
		}

		JSONObject jsonObject  =  JSON.parseObject(result).getJSONObject("marketDetail");
		if (jsonObject == null) {
			log.error("=====return:" + result);
			return null;
		}
		List<JsonRootBean> ls = new ArrayList<>();
		for (Iterator iterator = jsonObject.keySet().iterator();iterator.hasNext();){
			ls.add((JsonRootBean) JSONObject.toJavaObject(jsonObject.getJSONArray(iterator.next().toString()).getJSONObject(0), JsonRootBean.class));
		}
		for (JsonRootBean jrt: ls) {
			if (jrt.getSymbolId().equals("ETH_"+coinType)){
				return new Double(jrt.getPayload().getPriceNew()).toString();
			}
		}

		return null;
	}

	public static String tranJSONtoObjectLast() {
		String returnStr = tranJSONtoObject();
		if (null == returnStr) {
			returnStr = NiuX_DATA;
		}
		else {
			NiuX_DATA = returnStr;
		}

		return returnStr;
	}

	private static String tranJSONtoObject(){
		String result=httpsRequest(nxUrl,"GET",null);
		if(result == null){
			log.error("=====tranJSONtoObject,httpsRequest:" + nxUrl);
			return null;
		}
		JSONObject jsonObject = JSON.parseObject(result).getJSONObject("marketDetail");
		if (jsonObject == null) {
			log.error("=====return:" + result);
			return null;
		}

		// USDX2RMB取一次就可以了
		BigDecimal USDtoRMB = getUSDX2RMB();
		if (USDtoRMB == null) {
			USDtoRMB = new  BigDecimal(DEFALUT_USDX2RMB);
		}

		List<JsonRootBean> ls = new ArrayList<>();
		for (Iterator iterator = jsonObject.keySet().iterator();iterator.hasNext();){
			ls.add((JsonRootBean) JSONObject.toJavaObject(jsonObject.getJSONArray(iterator.next().toString()).getJSONObject(0), JsonRootBean.class));
		}

		List<NXbeanItem> nXbeanItems = new ArrayList<>();

		HashMap<String, BigDecimal> dollarMap = new HashMap<String, BigDecimal>();
		for (JsonRootBean jrt: ls) {
			if (jrt.getSymbolId().indexOf("_USDX")!=-1){
				NXbeanItem nbi = new NXbeanItem();
				double std =  (jrt.getPayload().getPriceNew()-jrt.getPayload().getYestdayPriceLast())*100.0/jrt.getPayload().getYestdayPriceLast();
				nbi.setChg(generateChg(std));

				nbi.setDetailUrl("");
				nbi.setIsSelect("");
				nbi.setName(jrt.getSymbolId().replaceAll("_","/"));

				double rmb = USDtoRMB.multiply(new BigDecimal(jrt.getPayload().getPriceNew())).doubleValue();
				BigDecimal b = new BigDecimal(rmb);
				double rmb2 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 

				String desc = jrt.getSymbolId().substring(0,jrt.getSymbolId().indexOf("_"));
				nbi.setDesc(desc);

				// app显示顺序错误，避免app上的修改，此处将错就错,2018-03-16
				nbi.setDollar("≈￥"+rmb2);
				nbi.setRmb(""+jrt.getPayload().getPriceNew());
				nbi.setTokenId("");
				nXbeanItems.add(nbi);

				// 保存到map里面避免下次重复循环,2018-03-21
				// 保存的是
				dollarMap.put(desc, new BigDecimal(rmb2));
			}
		}

		for (JsonRootBean jrt: ls) {
			if (jrt.getSymbolId().indexOf("_USDX")==-1){
				NXbeanItem nbi = new NXbeanItem();
				double std =  (jrt.getPayload().getPriceNew()-jrt.getPayload().getYestdayPriceLast())*100.0/jrt.getPayload().getYestdayPriceLast();
				nbi.setChg(generateChg(std));

				nbi.setDetailUrl("");
				nbi.setIsSelect("");
				String symbolId = jrt.getSymbolId().replaceAll("_","/");
				nbi.setName(symbolId);

				String desc = jrt.getSymbolId().substring(jrt.getSymbolId().indexOf("_")+1);
				nbi.setDesc(desc);

				BigDecimal priceNew = new BigDecimal(jrt.getPayload().getPriceNew());
				BigDecimal rmb = dollarMap.get(desc).multiply(priceNew);
				double rmb2 = rmb.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				
				nbi.setDollar("≈￥"+rmb2);

				nbi.setRmb(""+jrt.getPayload().getPriceNew());
				nbi.setTokenId("");
				nXbeanItems.add(nbi);
			}
		}

		NXbean nXbean = new NXbean();
		nXbean.setData(nXbeanItems);
		nXbean.setCode("0");
		nXbean.setMsg("ok");
		String json = JSONObject.toJSONString(nXbean);
		return  json;
	}

	private static String generateChg(double std) {
		BigDecimal b = new BigDecimal(std); 
		double std2 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
		String st = new Double(std2)+"%";
		if (std2 > 0.0) {
			st = "+"+st;
		}
		return st;
	}

	public static BigDecimal getUSDX2RMB(){
		String result=httpsRequest(USDtoRMBUrl,"POST",null);
		if(result == null){
			log.warn("=====return:getUSDX2RMB="+result);
			return null;
		}

		String obj = JSONObject.parseObject(result).getString("obj");
		if (obj == null) {
			log.error("=====return:" + result);
			return null;
		}

		String  c2cBuyPrice = JSONObject.parseObject(obj).getString("c2cBuyPrice");
		return  new BigDecimal(c2cBuyPrice);
	}

	public static String coinTypeCNYValue(String coinType) {
		String url = "https://exapi.6x.com//exapi/api/invest/investDouble?currencyIn="+coinType+"&currencyOut=CNY";
		String result=httpsRequest(url,"GET",null);
		if (result == null) {
			log.error("=====niuXEthBcBValue,httpsRequest="+nxUrl);
			return "";
		}
		String obj  =  JSON.parseObject(result).getString("obj");
		String exchange =  JSON.parseObject(obj).getString("exchange");
		
		return exchange;
	}
	//七天浮动率
	public static Map<String,Object> getCurrentSevenDaysSchedule(String url){
		String result=httpsRequest(url,"GET",null);
		Map<String,Object> map = new HashMap();
		if (result == null) {
			log.error("=====niuXEthBcBValue,httpsRequest="+nxUrl);
			return  map;
		}
		String obj  =  JSON.parseObject(result).getString("obj");
		String exchange =  JSON.parseObject(obj).getString("exchange");
		SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd");
        String time = format.format(new Date(Long.parseLong(JSON.parseObject(obj).getString("time"))*1000L));
        map.put("exchange",exchange);
        map.put("time",time);
		return map;
	}

//	public static void main(String[] args){
//		System.out.println(getCurrentSevenDaysSchedule("https://exapi.6x.com//exapi/api/invest/investDouble?currencyIn=BCB&currencyOut=CNY"));
//	}

}
