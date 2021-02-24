package com.exploration.shoppingrush;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exploration.account.JDAccountWrapper;
import com.exploration.account.JDAddressManager;
import com.exploration.request.HttpClientRequestHandler;
import com.exploration.request.HttpRequestHeaderGenerator;
import com.exploration.utils.json.JsonUtils;
import com.exploration.utils.jsoup.JSoupHandler;
import com.exploration.utils.map.MapUtils;
import com.exploration.utils.random.RandomNumberGenerator;
import com.exploration.utils.regex.RegexUtils;
import com.exploration.utils.request.ResponseRet;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;




/**
 * https://item.jd.com/1552946.html 华为耳机抢购实验
 * @Description 京东抢购的类
 * @author lzz
 * @time 2018年2月10日 下午5:56:17
 */

public class JDShoppingRush {
	
	private List<JDAccountWrapper> jdAccountWrapperList ;
	private FetchUrlType fetchUrlType;
	private String productId;
	private String shoppingRushTime;
	
	
/*****************************************************************************/
	public JDShoppingRush() {
		
	}
	
	public JDShoppingRush(FetchUrlType fetchUrlType,String productId,String shoppingRushTime) {
		this.fetchUrlType = fetchUrlType;
		this.productId = productId;
		this.shoppingRushTime = shoppingRushTime;
	}
	
	/**
	 * 获取抢购的跳转链接 https://itemko.jd.com/itemShowBtn?callback=jQuery7780395&skuId=1552946&from=pc&_=1515459568002
	 * @param requestHandler
	 * @param id
	 * @return  //divide.jd.com/user_routing?skuId=1552946&sn=d3547f782154d64663da4b58f6d5ff5c&from=pc
	 */
	public String fetchRushToPurchaseUrl(final HttpClientRequestHandler requestHandler, final String id) {
		if(null==requestHandler || null==id) {
			return null;
		}
		String rushToPurchaseUrl = "";
		//设置访问方法https://itemko.jd.com/itemShowBtn?callback=jQuery7780395&skuId=1552946&from=pc&_=1515459568002
		        //https://itemko.jd.com/itemShowBtn?callback=jQuery7630387&skuId=6171814&from=pc&_=1516759200002
		//根据京东的请求习惯加上了这个参数 jQuery7780395
		String callbackString = "jQuery"+ RandomNumberGenerator.generateRandomString(7);
		//String callbackString = "";
		StringBuffer sb = new StringBuffer();
		sb.append("https://itemko.jd.com/itemShowBtn?callback=")
		  .append(callbackString)
		  .append("&skuId=")
		  .append(id)
		  .append("&from=pc&_=")
		  .append(System.currentTimeMillis());
		// 设置访问方法GET "https://itemko.jd.com/itemShowBtn?callback=jQuery7780395&skuId=1552946&from=pc&_=1515459568002"
		RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
				                                      .setUri(new String(sb));// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setFetchRushToPurchaseUrlHeaders(requestBuilder,id);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "fetchRushToPurchaseUrl");
	
		//解析返回结果
		String initJsonString = "";
		try {
			initJsonString = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		/*System.out.println("initJsonString = "+initJsonString);
		int start = initJsonString.indexOf("{");
		int end = initJsonString.lastIndexOf("}");
		System.out.println("initJsonString = "+initJsonString+"---start = "+start+"---end = "+end);*/
		
		String parseJsonString = initJsonString.substring(initJsonString.indexOf("{"),initJsonString.lastIndexOf(")"));
		System.out.println("initJsonString = "+initJsonString+"---parseJsonString = "+parseJsonString);
		System.out.println();
		long startTimes = System.currentTimeMillis();		
		Map<String,Object> jsonRetMap = JsonUtils.json2Map(parseJsonString);
		long endTimes = System.currentTimeMillis();
		System.out.println("begin fastJson: "+startTimes+"---end fastJson: "+endTimes+"---一共花费时间: "+(endTimes-startTimes));
		/*rushToPurchaseUrl = rushToPurchaseUrl+jsonRetMap.get("url");
		jsonRetMap.remove("url");
		jsonRetMap.put("fetchRushToPurchaseUrl", rushToPurchaseUrl);*/
		/*long sTimes = System.currentTimeMillis();
		Map<String,Object> fetchRushToPurchaseUrlMap = MapUtils.translateKeysMap(jsonRetMap, "fetchRushToPurchaseUrl");
		
		long eTimes = System.currentTimeMillis();
		System.out.println("begin translateMap: "+sTimes+"---eTimes: "+eTimes+"---一共花费时间: "+(eTimes-sTimes));*/
		rushToPurchaseUrl += "https:"+String.valueOf(jsonRetMap.get("url"));
		jsonRetMap.remove("url");
		jsonRetMap.put("fetchRushToPurchaseUrl_url", rushToPurchaseUrl);
		jsonRetMap.put("skuId", id);
		MapUtils.traversalMap(jsonRetMap);
		requestHandler.getParseMap().put("fetchRushToPurchaseUrl", jsonRetMap);
		return rushToPurchaseUrl;
	}
	/**
	 * 从获取到的抢购的跳转链接https://divide.jd.com/user_routing?skuId=1552946&sn=b3c7a16d75ba5e8c92cc2e1b0cc825dc&from=pc进入到订单结算页
	 * 这个请求分三步完成
	 * step1: 发起GET请求  https://divide.jd.com/user_routing?skuId=1552946&sn=b3c7a16d75ba5e8c92cc2e1b0cc825dc&from=pc 返回302 Location:https://marathon.jd.com/captcha.html?from=pc&skuId=1552946&sn=b3c7a16d75ba5e8c92cc2e1b0cc825dc;
	 * step2: 使用step1返回的Location 发起第二次GET请求 https://marathon.jd.com/captcha.html?from=pc&skuId=1552946&sn=b3c7a16d75ba5e8c92cc2e1b0cc825dc 返回302 Location:https://marathon.jd.com/seckill/seckill.action?skuId=1552946&num=1&rid=1519700453;
	 * step3: 使用step2返回的Location 发起第三次GET请求 https://marathon.jd.com/seckill/seckill.action?skuId=1552946&num=1&rid=1519700453
	 * step3返回的是titile为"<title>订单结算页 -京东商城</title>"的页面,在这个页面中用户需要核对订单并最后提交以便系统生成订单;用户需要核对的信息包括以下多个部分,其中只需要操作前面两项就可以完成订单提交:
	 *       1,默认收获地址   在这个过程中,可以设置新的收货地址及更改默认收货地址,但为了节约抢购时间,这部分都提前完成;
	 *       2,支付及配送方式  默认使用的是在线支付,不需要做修改;
	 *       3,发票信息 没有专门做设置的话是个人发票 这一步也可以提前完成;
	 *       4,使用优惠券抵消部分总额;
	 *       5,京东E卡;
	 *       6,使用京豆支付;
	 * @param requestHandler
	 * @param rushToPurchaseUrl
	 * @param id  待抢购商品的Id
	 * 
	 * @return 订单结算页面的input标签内容生成的Map
	 */
	public Map<String,Object> gotoCheckOrderAndSettleAccountPage(final HttpClientRequestHandler requestHandler,final String rushToPurchaseUrl,final String id) {
		if(null==requestHandler || null==rushToPurchaseUrl || null==id ) {
			return null;
		}
		//step1 第一次GET请求 https://divide.jd.com/user_routing?skuId=1552946&sn=b3c7a16d75ba5e8c92cc2e1b0cc825dc&from=pc
		RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
				                                      .setUri(rushToPurchaseUrl);// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setgotoCheckOrderAndSettleAccountPageStep1Headers(requestBuilder,id);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		//解析Step1请求返回的Location https://marathon.jd.com/captcha.html?from=pc&skuId=1552946&sn=5b54c1a6b3442bd1f542ebaf695223bd
		String firstLocation = requestHandler.GetHttpResponse_parseLocationUrl(requestHandler);
		//step2 第一次GET请求 https://marathon.jd.com/captcha.html?from=pc&skuId=1552946&sn=5b54c1a6b3442bd1f542ebaf695223bd
		RequestBuilder requestBuilder2 = RequestBuilder.get()// Get 方法
                .setUri(firstLocation);// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setgotoCheckOrderAndSettleAccountPageStep2Headers(requestBuilder2,id);
		// 生成访问方法
		HttpUriRequest requestMethod2 = requestBuilder2.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod2);
		//解析Step2请求返回的Location  Location:https://marathon.jd.com/seckill/seckill.action?skuId=1552946&num=1&rid=1519700453
		String secondLocation = requestHandler.GetHttpResponse_parseLocationUrl(requestHandler);
		//step3 发起第三次Get请求 进入订单结算页面
		RequestBuilder requestBuilder3 = RequestBuilder.get()
				                                       .setUri(secondLocation);//https://marathon.jd.com/seckill/seckill.action?skuId=1552946&num=1&rid=1519700453
		//设置访问的Header
		HttpRequestHeaderGenerator.setgotoCheckOrderAndSettleAccountPageStep3Headers(requestBuilder3,id);
		//保存访问方法
		requestHandler.setRequestMethod(requestBuilder3.build());
		//发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "gotoCheckOrderAndSettleAccountPage");
        //解析"订单结算页"获取相应的Input 以便提交订单时构造Form表单使用
		Map<String,Object> checkOrderAndSettleAccountPageMap = JSoupHandler.parserResponseRet_generateMapByInput(responseRet);
		//将secondLocation中的skuId num 和 rid三个参数存入到Map中
		checkOrderAndSettleAccountPageMap.put("skuId", RegexUtils.getValueBetweenHeadAndTail(secondLocation, "skuId=", "&"));
		checkOrderAndSettleAccountPageMap.put("num", RegexUtils.getValueBetweenHeadAndTail(secondLocation, "num=", "&"));
		checkOrderAndSettleAccountPageMap.put("rid", RegexUtils.getValueBetweenHeadAndTail(rushToPurchaseUrl, "rid=", null));
		checkOrderAndSettleAccountPageMap.put("secondLocation", secondLocation);
		return checkOrderAndSettleAccountPageMap;	
	}
	/**
	 * 抢购进入的订单结算页 审核订单并保存相应的信息
	 * 目前主动保存的信息包括:
	 * 1,收货地址信息;
	 * 2,支付及配送方式
	 * 其它采用的默认的,将来可能会加上支付券等信息
	 * @param requestHandler
	 * @param checkOrderAndSettleAccountPageMap
	 * @return 
	 */
	public Map<String,Object> checkOrderAndSaveRelativeInfo(final HttpClientRequestHandler requestHandler, Map<String,Object> checkOrderAndSettleAccountPageMap) {
		if(null==requestHandler || null == checkOrderAndSettleAccountPageMap || 0==checkOrderAndSettleAccountPageMap.size()) {
			return null;
		}
		//获取用于收抢购商品的收货地址
		//Map<String,Object> consigneeAddress = JDAddressManager.fetchConsigneeAddress(requestHandler, checkOrderAndSettleAccountPageMap);
		checkOrderAndSettleAccountPageMap = JDAddressManager.fetchConsigneeAddress(requestHandler, checkOrderAndSettleAccountPageMap);
		//保存收货人信息
		checkOrderAndSettleAccountPageMap = saveConsignee(requestHandler, checkOrderAndSettleAccountPageMap);
		//保存支付及配送方式
		checkOrderAndSettleAccountPageMap = savePaymentAndDeliveryMode(requestHandler, checkOrderAndSettleAccountPageMap);
		return checkOrderAndSettleAccountPageMap;
	}
	/**
	 * 保存收货人信息,需要发起两次请求:
	 * 1,POST请求 https://marathon.jd.com/async/isSupportCodPayment.action?skuId=1552946 返回布尔类型结果(true/false) 还会添加两天新的Cookie
	 * 2,GET请求 https://ss.jd.com/ss/areaStockState/mget?app=skill_sys&ch=1&skuNum=1552946&pdpin=jd_4c2b3d8b3d6c1&pduid=593018792&area=1,2800,2851,0&&callback=jQuery19106874987452516561_1519725280469&_=1519725280471
	 *   请求携带Form表达数据
	 *   返回数据  jQuery19106874987452516561_1519725280469({"1552946":{"a":"33","ab":"-1","ac":"-1","ad":"-1","ae":"-1","af":"-1","b":"1","d":"6","e":"40","f":"0","v":"0","x":"true"}})
	 *   成功时返回数据中会包含: "x":"true"
	 * @param requestHandler
	 * @param checkOrderAndSettleAccountPageMap
	 * @return
	 */
    public Map<String,Object> saveConsignee(final HttpClientRequestHandler requestHandler,final Map<String,Object> checkOrderAndSettleAccountPageMap){
    	if(null==requestHandler || null == checkOrderAndSettleAccountPageMap || 0==checkOrderAndSettleAccountPageMap.size()) {
			return null;
		}
    	//请求1 POST请求
    	boolean flag = isSupportPayment(requestHandler, checkOrderAndSettleAccountPageMap);
    	if(flag) {
    		System.out.println("isSupportPayment return true");
    	}
    	flag = false;
    	//请求2 GET请求
		//获取请求携带的参数
		List<Cookie> cookieList = requestHandler.getContext().getCookieStore().getCookies();
		for(Cookie cookie:cookieList) {
			if(cookie.getName().equalsIgnoreCase("__jdu")) {
				checkOrderAndSettleAccountPageMap.put("pduid", cookie.getValue());
			}
			if(cookie.getName().equalsIgnoreCase("pin")) {//"unick"
				checkOrderAndSettleAccountPageMap.put("pdpin", cookie.getValue());
			}
			if(cookie.getName().equalsIgnoreCase("3AB9D23F7A4B3C9B")) {
				checkOrderAndSettleAccountPageMap.put("eid", cookie.getValue());
			}
			if(cookie.getName().contains("seckill")) {
				System.out.println("seckill_cookie:"+cookie.toString());
				//seckillCookieList.add(cookie);
			}
		}
		if(null==checkOrderAndSettleAccountPageMap.get("pduid")) {
			System.out.println("get pduid cookie failed, set default value...");
			checkOrderAndSettleAccountPageMap.put("pduid", "15154973380332050715474");
		}
		checkOrderAndSettleAccountPageMap.put("app", "skill_sys");
		checkOrderAndSettleAccountPageMap.put("ch","1");
		String areaString = checkOrderAndSettleAccountPageMap.get("provinceId")+","+checkOrderAndSettleAccountPageMap.get("cityId")+","+checkOrderAndSettleAccountPageMap.get("countyId")+","+checkOrderAndSettleAccountPageMap.get("townId");
		checkOrderAndSettleAccountPageMap.put("area", areaString);
		long saveConsigneeTimes = System.currentTimeMillis();
		//jQuery19108167842314960716
		//map.put("callback", "jQuerysaveConsignee_"+saveConsigneeTimes);
		checkOrderAndSettleAccountPageMap.put("callback", "jQuery19108167842314960716_"+saveConsigneeTimes);
		checkOrderAndSettleAccountPageMap.put("_", saveConsigneeTimes+2);
		StringBuffer sb = new StringBuffer();
		sb.append("https://ss.jd.com/ss/areaStockState/mget?app=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("app")))
		  .append("&ch=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("ch")))
		  .append("&skuNum=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("skuId")))
		  .append("&pdpin=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("pdpin")))
		  .append("&pduid=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("pduid")))
		  .append("&area=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("area")))
		  .append("&callback=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("callback")))
		  .append("&_=")
		  .append(String.valueOf(checkOrderAndSettleAccountPageMap.get("_")));
		String saveConsigneeUrl = new String(sb);
		//设置请求方法 GET https://ss.jd.com/ss/areaStockState/mget?app=skill_sys&ch=1&skuNum=1552946&pdpin=jd_4c4bfb4ccc0da&pduid=1515549236928277140194&area=8,6858,6859,52426&&callback=jQuery1910275919900943294_1515553230968&_=1515553230970
		RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
				                                      .setUri(saveConsigneeUrl);// 设置访问的Uri
		
		//设置请求Header
		String referer = String.valueOf(checkOrderAndSettleAccountPageMap.get("secondLocation"));
		HttpRequestHeaderGenerator.setSaveConsigneeHeaders(requestBuilder,referer);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		//发起请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler,"saveConsignee");
		//待解析的Json数据jQuery19105051394261301718_1515402423540({"1552946":{"a":"33","ab":"-1","ac":"-1","ad":"-1","ae":"-1","b":"1","d":"616","e":"1","f":"1","v":"0","x":"true"}})
		String responseString = null;
		try {
			responseString = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(null!=responseString) {
			if(responseString.contains("true")) {
				flag = true;
			}
		}
		if(flag) {
			System.out.println("saveConsignee success!");
		}
		return checkOrderAndSettleAccountPageMap;
    	
    }
    /**
     * 保存收货人信息前发起的Post请求
     * POST请求 https://marathon.jd.com/async/isSupportCodPayment.action?skuId=1552946 返回布尔类型结果(true/false) 还会添加两天新的Cookie
     * @param requestHandler
     * @param checkOrderAndSettleAccountPageMap
     * @return boolean
     */
    public boolean isSupportPayment(final HttpClientRequestHandler requestHandler,final Map<String,Object> checkOrderAndSettleAccountPageMap) {
    	boolean flag = false;
    	if(null==requestHandler || null == checkOrderAndSettleAccountPageMap || 0==checkOrderAndSettleAccountPageMap.size()) {
			return flag;
		}
    	String skuId = String.valueOf(checkOrderAndSettleAccountPageMap.get("skuId"));
		/*String num = String.valueOf(checkOrderAndSettleAccountPageMap.get("num"));
		String rid = String.valueOf(checkOrderAndSettleAccountPageMap.get("rid")); */
		String referer = String.valueOf(checkOrderAndSettleAccountPageMap.get("secondLocation"));

    	//设置访问方法 POST  https://marathon.jd.com/async/isSupportCodPayment.action?skuId=1552946 
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://marathon.jd.com/async/isSupportCodPayment.action?skuId="+skuId);// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setIsSupportPaymentHeaders(requestBuilder,referer);
		// 设置访问携带的Form
		Map<String,Object> formMap = new HashMap<String,Object>();
		formMap.put("orderParam.provinceId", String.valueOf(checkOrderAndSettleAccountPageMap.get("provinceId")));
		formMap.put("orderParam.cityId", String.valueOf(checkOrderAndSettleAccountPageMap.get("cityId")));
		formMap.put("orderParam.countyId", String.valueOf(checkOrderAndSettleAccountPageMap.get("countyId")));
		formMap.put("orderParam.townId", String.valueOf(checkOrderAndSettleAccountPageMap.get("townId")));
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(formMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "isSupportCodPayment");
		if(null==responseRet || null==responseRet.getRetContent()) {
			return flag;
		}
		//希望返回结果: true
		String responseString = null;
		try {
			responseString = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(null!=responseString) {
			if("true".equalsIgnoreCase(responseString)) {
				flag = true;
			}
		}
    	return flag;
    }
    /**
     * 保存支付及配送方式 
     * POST请求 https://marathon.jd.com/async/calcuOrderPrice.action?skuId=1552946&num=1 携带Form表单
     * @param requestHandler
     * @param checkOrderAndSettleAccountPageMap
     * @return
     */
    public Map<String,Object> savePaymentAndDeliveryMode(HttpClientRequestHandler requestHandler,Map<String,Object> checkOrderAndSettleAccountPageMap){
    	if(null==requestHandler || null == checkOrderAndSettleAccountPageMap || 0==checkOrderAndSettleAccountPageMap.size()) {
			return null;
		}
    	String skuId = String.valueOf(checkOrderAndSettleAccountPageMap.get("skuId"));
		String num = String.valueOf(checkOrderAndSettleAccountPageMap.get("num"));
		//String rid = String.valueOf(checkOrderAndSettleAccountPageMap.get("rid")); 
		String referer = String.valueOf(checkOrderAndSettleAccountPageMap.get("secondLocation"));
		//设置访问方法 POST  https://marathon.jd.com/async/calcuOrderPrice.action?skuId=1552946&num=1 
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://marathon.jd.com/async/calcuOrderPrice.action?skuId="+skuId+"&num="+num);// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setSavePaymentAndDeliveryModeHeaders(requestBuilder,referer);
		// 设置访问携带的Form
		Map<String, Object> requestMap = new HashMap<String, Object>();
		//map.get("provinceId")+","+map.get("cityId")+","+map.get("countyId")+","+map.get("townId");
		requestMap.put("provinceId", checkOrderAndSettleAccountPageMap.get("provinceId"));
		requestMap.put("cityId", checkOrderAndSettleAccountPageMap.get("cityId"));
		requestMap.put("countyId", checkOrderAndSettleAccountPageMap.get("countyId"));
		//用户地址没有明确指定townId时 用0替代
		String townId = String.valueOf(checkOrderAndSettleAccountPageMap.get("townId"));
		if(null==townId || "".equals(townId) || " ".equals(townId)) {
			townId = "0";
		}
		requestMap.put("townId", townId);
		//抢购页面的Input标签里面有这两个值
		/*requestMap.put("paymentType", checkOrderAndSettleAccountPageMap.get("payment"));//在线支付
		requestMap.put("codTimeType", checkOrderAndSettleAccountPageMap.get("codTimeType"));//工作日和节假日都可以*/
		requestMap.put("paymentType", "4");//在线支付
		requestMap.put("codTimeType", "3");//工作日和节假日都可以
		//生成Form表单
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(requestMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "savePaymentAndDeliveryMode");
		//解析请求返回的Json
		String responseString = null;
		try {
			responseString = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//{"couponDiscount":0.00,"giftCardDiscount":0.00,"overXuZhongWeight":null,"xuZhongFreight":0,"freight":0.00,"productTotalPrice":109.00,"totalPrice":109.00,"jingdouDiscount":0.00,"showXuZhongInfo":true,"allXuZhongWeight":"0.040kg"}
		if(null!=responseString) {
			//"productTotalPrice":109.00
			Map<String, Object> jsonRetMap = JsonUtils.json2Map(responseString);			
			MapUtils.traversalMap(jsonRetMap);
			checkOrderAndSettleAccountPageMap.putAll(jsonRetMap);
		}	
		
    	return checkOrderAndSettleAccountPageMap;
    }
    /**
     * 抢购提交订单的请求
     * @param requestHandler
     * @param map
     * @return 
     */
    public String submitPurchaseOrder(final HttpClientRequestHandler requestHandler,final Map<String,Object> map) {
    	if(null==requestHandler || null == map || 0==map.size()) {
			return null;
		}
    	String responseString = null;
    	//从map中获取请求需要的部分参数
    	String skuId = String.valueOf(map.get("skuId"));
		String num = String.valueOf(map.get("num"));
		//String rid = String.valueOf(checkOrderAndSettleAccountPageMap.get("rid")); 
		String referer = String.valueOf(map.get("secondLocation"));
    	//设置访问方法 POST https://marathon.jd.com/seckill/submitOrder.action?skuId=1552946&vid=
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://marathon.jd.com/seckill/submitOrder.action?skuId="+skuId+"&vid=");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setSubmitPurchaseOrderHeaders(requestBuilder,referer);
		
		//添加携带的form表单数据
		Map<String, Object> requestMap = new HashMap<String, Object>();
		
		requestMap.put("orderParam.name", map.get("name"));
		requestMap.put("orderParam.addressDetail", map.get("addressDetail"));
		requestMap.put("orderParam.mobile", map.get("mobileWithXing"));
		requestMap.put("orderParam.email", map.get("email"));
		requestMap.put("orderParam.provinceId", map.get("provinceId"));
		requestMap.put("orderParam.cityId", map.get("cityId"));
		requestMap.put("orderParam.countyId", map.get("countyId"));
		requestMap.put("orderParam.townId", map.get("townId"));
		//requestMap.put("orderParam.paymentType", map.get("paymentType"));
		requestMap.put("orderParam.password", "");
		requestMap.put("orderParam.invoiceTitle", "4");
		requestMap.put("orderParam.invoiceContent", "1");
		requestMap.put("orderParam.invoiceCompanyName", "");
		requestMap.put("orderParam.invoiceTaxpayerNO", "");		
		requestMap.put("orderParam.invoicePhone", "18311438517");//添加发票的手机号
		requestMap.put("orderParam.usualAddressId", map.get("id"));
		requestMap.put("skuId:", skuId);
		requestMap.put("num", "1");
		requestMap.put("orderParam.provinceName", map.get("provinceName"));
		requestMap.put("orderParam.cityName", map.get("cityName"));
		requestMap.put("orderParam.countyName", map.get("countyName"));
		requestMap.put("orderParam.townName", map.get("townName"));
		//requestMap.put("orderParam.codTimeType", map.get("codTimeType"));
		requestMap.put("orderParam.mobileKey", map.get("mobileKey"));
		/*requestMap.put("orderParam.eid", map.get("eid"));
		requestMap.put("orderParam.fp", map.get("fp"));*/
		//eid =                  IRNZB6RPCFBN64NU657CNVAW4BGI3R3UZCEFZ7P2TW233YCKGDITAR7O7LUUT5S46CRDX5T737EDK5RO7F34F5ECD4
		//3AB9D23F7A4B3C9B=      IRNZB6RPCFBN64NU657CNVAW4BGI3R3UZCEFZ7P2TW233YCKGDITAR7O7LUUT5S46CRDX5T737EDK5RO7F34F5ECD4
		//fp = da4b7d33cd592b737aa65ef078da44cb
		//3AB9D23F7A4B3C9B=IRNZB6RPCFBN64NU657CNVAW4BGI3R3UZCEFZ7P2TW233YCKGDITAR7O7LUUT5S46CRDX5T737EDK5RO7F34F5ECD4
		String eidTmp = "IRNZB6RPCFBN64NU657CNVAW4BGI3R3UZCEFZ7P2TW233YCKGDITAR7O7LUUT5S46CRDX5T737EDK5RO7F34F5ECD4";
		String fpTmp = "da4b7d33cd592b737aa65ef078da44cb";
		//requestMap.put("orderParam.eid", map.get("eid"));
		requestMap.put("orderParam.eid", eidTmp);
		requestMap.put("orderParam.fp", fpTmp);		
		requestMap.put("orderParam.paymentType", "4");//在线支付
		requestMap.put("orderParam.codTimeType", "3");//工作日和节假日都可以	
		requestMap.put("addressMD5", map.get("md5"));	
		requestMap.put("yuyue", "");
		requestMap.put("yuyueAddress", "0");	
		System.out.println("----------------遍历抢购订单提交的form表单-----------------");
		MapUtils.traversalMap(requestMap);
		//生成Form表单
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(requestMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		//发起请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler,"submitPurchaseOrder");
		//解析请求返回的数据
		try {
			responseString = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (responseString.contains("koFail.html?reason=")) {
			long failTimes = System.currentTimeMillis();
			System.out.println("此次抢购失败！----failTimes = " + failTimes);
		} else if (responseString.contains("success")) {// 抢购成功
			// 返回值:"//sko.jd.com/success/success.action?orderId=70405623798&rid=0.8233757122791094"
			//String orderId = responseString.substring(responseString.indexOf("=") + 1, responseString.indexOf("&rid"));
			long successTimes = System.currentTimeMillis();
			System.out.println("恭喜！抢购成功,准备跳转至抢购支付页面.....successTimes = " + successTimes);
		}
    	return responseString;
    }
    /**
     * 这是在开始抢购后先获取抢购的Url再执行抢购 这种情况下必须等到开始抢购了才能获取到相应的抢购url
     * 调用该方法前需要初始化productId的值
     * @param requestHandler
     * @param isNeedSetDefaultAddr
     * @param isNeedEmptyCart
     * @return 抢购返回
     */
    public String fetchUrlAndrushToPurchaseTask(final HttpClientRequestHandler requestHandler,
			final boolean isNeedSetDefaultAddr,final boolean isNeedEmptyCart) {
    	if(null==requestHandler) {
    		return null;
    	}
    	//获取抢购跳转需要的Url
    	String  rushToPurchaseUrl = fetchRushToPurchaseUrl(requestHandler, this.productId);
    	//进入抢购页面
    	Map<String, Object> rushToPurchasePageMap = gotoCheckOrderAndSettleAccountPage(requestHandler, rushToPurchaseUrl, this.productId);
    	//核对并保存收货人信息/支付及配送方式
    	Map<String,Object> checkOrderAndSettleAccountPageMap = checkOrderAndSaveRelativeInfo(requestHandler, rushToPurchasePageMap);
    	//提交抢购订单
    	//return submitPurchaseOrder(requestHandler, checkOrderAndSettleAccountPageMap);//测试阶段可以先不提交订单
    	return null;
    }
    /**
     * 携带抢购的url开始抢购
     * @param requestHandler
     * @param rushToPurchaseUrl
     * @param isNeedSetDefaultAddr
     * @param isNeedEmptyCart
     * @return
     */
    public String rushToPurchaseTask(final HttpClientRequestHandler requestHandler,String rushToPurchaseUrl,
			final boolean isNeedSetDefaultAddr,final boolean isNeedEmptyCart) {
    	if(null==requestHandler || null==rushToPurchaseUrl) {
    		return null;
    	}
    	//进入抢购页面
    	Map<String, Object> rushToPurchasePageMap = gotoCheckOrderAndSettleAccountPage(requestHandler, rushToPurchaseUrl, this.productId);
    	//核对并保存收货人信息/支付及配送方式
    	Map<String,Object> checkOrderAndSettleAccountPageMap = checkOrderAndSaveRelativeInfo(requestHandler, rushToPurchasePageMap);
    	//提交抢购订单
    	return submitPurchaseOrder(requestHandler, checkOrderAndSettleAccountPageMap);//测试阶段可以先不提交订单
    	//return null;
    }
	
/*****************************************************************************/
 
	public List<JDAccountWrapper> getJdAccountWrapperList() {
		return jdAccountWrapperList;
	}

	public void setJdAccountWrapperList(List<JDAccountWrapper> jdAccountWrapperList) {
		this.jdAccountWrapperList = jdAccountWrapperList;
	}

	public FetchUrlType getFetchUrlType() {
		return fetchUrlType;
	}

	public void setFetchUrlType(FetchUrlType fetchUrlType) {
		this.fetchUrlType = fetchUrlType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getShoppingRushTime() {
		return shoppingRushTime;
	}

	public void setShoppingRushTime(String shoppingRushTime) {
		this.shoppingRushTime = shoppingRushTime;
	}
	


}
