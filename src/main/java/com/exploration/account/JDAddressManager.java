package com.exploration.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exploration.request.HttpClientRequestHandler;
import com.exploration.request.HttpRequestHeaderGenerator;
import com.exploration.utils.map.MapUtils;
import com.exploration.utils.regex.RegexUtils;
import com.exploration.utils.request.ResponseRet;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicNameValuePair;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;



public class JDAddressManager {
	
	private JDAccountWrapper jdAccountWrapper;
	
	public JDAddressManager() {
		
	}
	
	public JDAddressManager(JDAccountWrapper jdAccountWrapper) {
		this.jdAccountWrapper = jdAccountWrapper;
	}
	/**
	 * 获取收货地址
	 * @param
	 * @return  收货地址 list
	 * Request URL:http://easybuy.jd.com/address/getEasyBuyList.action  | Request Method:GET
	 */
	public List<String> fetchAddressList(final HttpClientRequestHandler requestHandler){
		if(null == requestHandler) {
			return null;
		}
		List<String> addressList = new ArrayList<String>();
		// 设置访问方法GET "http://easybuy.jd.com/address/getEasyBuyList.action"
		RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
				.setUri("http://easybuy.jd.com/address/getEasyBuyList.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setFetchAddressListHeaders(requestBuilder);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "fetchAddressList");
		try {
			String addressHtml = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
			addressList = RegexUtils.findValueGroup(addressHtml, "(?<=addressId=\").*?(?=\")");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return addressList;
	}
	/**
	 * 调用该方法就是为了从新获取SessionId
	 * 京东商城打开添加收货地址的页面会返回Cookie(更新SessionId  JSESSIONID)
	 * @param requestHandler
	 */
	public void fetchAddAddressCookie(final HttpClientRequestHandler requestHandler) {
		if(null==requestHandler) {
			return;
		}
		//设置访问方法 POST https://easybuy.jd.com/address/formatAddAddressDiag.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://easybuy.jd.com/address/formatAddAddressDiag.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setFetchAddressListHeaders(requestBuilder);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		//ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "fetchAddressList");
		requestHandler.GetHttpResponse_generalMethod(requestHandler, "fetchAddressList");
	}
	/**
	 * 添加新收货地址并获取该新收货地址的addressId
	 * @param requestHandler
	 * @param newAddrFormDataMap 设置新收货地址需要添加的form表单数据
	 * @param addressList 添加收货地址之前获取到的
	 * @return 新添加的addressId
	 */
	public  String addNewAddress(final HttpClientRequestHandler requestHandler,final Map<String,Object> newAddrFormDataMap, final List<String> addressList) {
		String addrId = null;
		if(null == requestHandler ||null==newAddrFormDataMap || 0==newAddrFormDataMap.size()) {
			return addrId;
		}
		//设置访问方法 POST https://easybuy.jd.com/address/addAddress.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://easybuy.jd.com/address/addAddress.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setAddNewAddressHeaders(requestBuilder);
		//添加From表单数据
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(newAddrFormDataMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "addNewAddress");
		//解析请求返回的页面获取新添加的收货地址的addressId
		List<String> newAddressList = new ArrayList<String>();
		try {
			String addressHtml = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
			newAddressList = RegexUtils.findValueGroup(addressHtml, "(?<=addressId=\").*?(?=\")");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(newAddressList.size()>0) {
			newAddressList.removeAll(addressList);
			addrId = newAddressList.get(0);
		}
		System.out.println("addrId = "+addrId);
		return addrId; 
	}
	/**
	 * 设置默认收货地址  POST http://easybuy.jd.com/address/setAddressAllDefaultById.action
	 * @param requestHandler
	 * @param addressId 准备设置为默认收货地址的 addressId
	 * @return 不需要返回值   以后可以加上返回 200 判断请求返回成功
	 */
	public void setDefaultAddressById(final HttpClientRequestHandler requestHandler,final String addressId){
		
		if(null==requestHandler || null==addressId) {
			return ;
		}
		//设置访问方法 POST http://easybuy.jd.com/address/setAddressAllDefaultById.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("http://easybuy.jd.com/address/setAddressAllDefaultById.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setDefaultAddressByIdHeaders(requestBuilder);
		//添加From表达数据
	    requestBuilder.addParameter(new BasicNameValuePair("addressId", addressId));
	    //使用EntityBuilder的setParamters和build方法
	    //requestBuilder.setEntity(EntityBuilder.create().setParameters(new BasicNameValuePair("addressId", addressId)).build());
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		//ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "fetchAddressList");
		requestHandler.GetHttpResponse_generalMethod(requestHandler, "setDefaultAddressById");
		System.out.println("set "+addressId+" default success");
	}
	
	/**
	 * 为用户添加新的收获地址并设置为默认收获地址
	 * @return
	 */
	public void addNewAddressAndSetDefault(HttpClientRequestHandler requestHandler){
		if(null == requestHandler) {
			return ;
		}
		//获取所有的收货地址
		List<String> addressList = fetchAddressList(requestHandler);
		//构造添加新地址的form表单数据
		AddressInfoParam aip = new AddressInfoParam();		
		Map<String,Object> newAddrInfoParam = aip.generateDefaultAddresInfoParam();
		MapUtils.traversalMap(newAddrInfoParam);
		//获取Cookie 添加新收货地址页面
		fetchAddAddressCookie(requestHandler);
		//添加新收获地址并获取该新收获地址的addressId  提交添加新收货地址请求
		String addressId = addNewAddress(requestHandler,newAddrInfoParam,addressList);
		//设置addressId为默认收货地址 
        setDefaultAddressById(requestHandler, addressId);
		
	}
	/**
	 * 抢购模式中订单结算页 获取收货地址
	 * @param requestHandler
	 * @return 
	 */
	public static Map<String,Object> fetchConsigneeAddress(final HttpClientRequestHandler requestHandler,final Map<String,Object> checkOrderAndSettleAccountPageMap){
		if(null==requestHandler || null==checkOrderAndSettleAccountPageMap || 0==checkOrderAndSettleAccountPageMap.size()) {
			return null;
		}
		Map<String,Object> consigneeAddress = new HashMap<String,Object>();
		//获取参数
		String skuId = String.valueOf(checkOrderAndSettleAccountPageMap.get("skuId"));
		/*String num = String.valueOf(checkOrderAndSettleAccountPageMap.get("num"));
		String rid = String.valueOf(checkOrderAndSettleAccountPageMap.get("rid"));*/
		String referer = String.valueOf(checkOrderAndSettleAccountPageMap.get("secondLocation"));
		//设置访问方法 POST https://marathon.jd.com/async/getUsualAddressList.action?skuId=1552946
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				//.setUri("https://marathon.jd.com/async/getUsualAddressList.action?skuId="+skuId);// 设置访问的Uri
				.setUri("https://marathon.jd.com/async/getUsualAddressList.action?skuId="+skuId+"&yuyue=");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setFetchUsualAddressListHeaders(requestBuilder,referer);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		//发起请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler,"fetchConsigneeAddress");
		//解析返回的json获取用户的收货地址列表
		String initJsonString = null;
		try {
			initJsonString = new String(responseRet.getRetContent(),responseRet.getContentEncoding());
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		if(null!=initJsonString) {
			//使用fastJson解析json获取用户收货地址列表
			List<Map<String,Object>> listmap = JSON.parseObject(initJsonString, new TypeReference<List<Map<String,Object>>>(){});
			//如果只有一个收获地址,就默认保存这个地址;若有多个收获收获地址,优先保存默认收获地址,若没有默认收获地址,保存第一个收获地址
			//遍历listmap找到带有默认收货地址的那个map  "defaultAddress":true
			consigneeAddress = listmap.get(0);
			for(int index=0;index<listmap.size();index++) {
				Map<String,Object> tMap = listmap.get(index);
				if("true".equalsIgnoreCase(String.valueOf(tMap.get("defaultAddress")))) {
					System.out.println("找到默认收货地址Map,遍历该map--------");
					MapUtils.traversalMap(tMap);
					consigneeAddress = tMap;
					checkOrderAndSettleAccountPageMap.putAll(consigneeAddress);
				}
			}
		}
		return checkOrderAndSettleAccountPageMap;
	}
	
	


}
