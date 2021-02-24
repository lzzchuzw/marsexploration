package com.exploration.account;



import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.exploration.request.HttpClientRequestHandler;
import com.exploration.request.HttpRequestHeaderGenerator;
import com.exploration.utils.map.MapUtils;
import com.exploration.utils.request.ResponseRet;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;



public class ShoppingCartManager {
	
	/**
	 * 进入京东购物车主页面,返回时会添加两个Cookie
	 * GET "https://cart.jd.com/cart.action"
	 * @param requestHandler
	 * @return
	 */
	public int gotoShoppingCartHomePage(final HttpClientRequestHandler requestHandler) {
		
		if(null==requestHandler) {
			return -1;
		}
		// 设置访问方法GET "https://cart.jd.com/cart.action"
		RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
				.setUri("https://cart.jd.com/cart.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setGotoShoppingCartHomePageHeaders(requestBuilder);
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "gotoShoppingCartHomePage");
		//解析获取Map
		//Form Data:
		//(empty)
		//t:0
		//outSkus:
		//random:0.9549662366840277
		//locationId:17-1396-1398-23394
		Map<String,Object> formDataMap = requestHandler.getCookieByName(requestHandler, "ipLoc-djd");
		formDataMap.put("t", 0);//
		formDataMap.put("outSkus", null);
		formDataMap.put("random", String.valueOf(Math.random()));
		formDataMap.put("locationId",formDataMap.get("ipLoc-djd"));
		formDataMap.remove("ipLoc-djd");
		requestHandler.getParseMap().put("gotoShoppingCartHomePage", formDataMap);
		MapUtils.traversalMap(formDataMap);
		//返回请求状态
		return responseRet.getStautsLine().getStatusCode();		
	}
	
	public int addProductToCart(final HttpClientRequestHandler requestHandler,final String productId) {
		if(null==requestHandler || null == productId) {
			return -1;
		}
		//设置访问方法 POST https://cart.jd.com/gate.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://cart.jd.com/gate.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setAddProductToCartHeaders(requestBuilder);
		//添加From表达数据
		/*try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(newAddrFormDataMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "addProductToCart");
		return responseRet.getStautsLine().getStatusCode();
	}
	/**
	 * 京东商城购物车页面取消"全选"---之前需要请求 进入购物车 页面,获取相应的Cookie
	 * @return
	 */
	public int cancelAllItem(final HttpClientRequestHandler requestHandler) {
		if(null==requestHandler) {
			return -1;
		}
		//Form Data:
		//(empty)
		//t:0
		//outSkus:
		//random:0.9549662366840277
		//locationId:17-1396-1398-23394
		Map<String,Object> formDataMap = requestHandler.getCookieByName(requestHandler, "ipLoc-djd");
		formDataMap.put("t", 0);//
		formDataMap.put("outSkus", "");
		formDataMap.put("random", String.valueOf(Math.random()));
		formDataMap.put("locationId",formDataMap.get("ipLoc-djd"));
		formDataMap.remove("ipLoc-djd");
		//设置访问方法 POST https://cart.jd.com/cancelAllItem.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://cart.jd.com/cancelAllItem.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setSelectOrCancelAllItemHeaders(requestBuilder);
		//添加From表达数据
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(formDataMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "cancelAllItem");
		return responseRet.getStautsLine().getStatusCode();
	}
	/**
	 * 京东商城购物车页面"全选"操作---之前需要请求 进入购物车 页面,获取相应的Cookie
	 * @param requestHandler
	 * @return
	 */
	public int selectAllItem(final HttpClientRequestHandler requestHandler) {
		if(null==requestHandler) {
			return -1;
		}
		//Form Data:
		//(empty)
		//t:0
		//outSkus:
		//random:0.9549662366840277
		//locationId:17-1396-1398-23394
		Map<String,Object> formDataMap = requestHandler.getCookieByName(requestHandler, "ipLoc-djd");
		formDataMap.put("t", 0);//
		formDataMap.put("outSkus", "");
		formDataMap.put("random", String.valueOf(Math.random()));
		formDataMap.put("locationId",formDataMap.get("ipLoc-djd"));
		formDataMap.remove("ipLoc-djd");
		//设置访问方法 POST https://cart.jd.com/cancelAllItem.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://cart.jd.com/cancelAllItem.action");// 设置访问的Uri
		// 设置访问的Header
		HttpRequestHeaderGenerator.setSelectOrCancelAllItemHeaders(requestBuilder);
		//添加From表达数据
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(formDataMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "selectAllItem");
		return responseRet.getStautsLine().getStatusCode();
	}
	/**
	 * 京东商城购物车页面 批量删除选择的商品  配合"selectAllItem"方法 可以实现清空购物车  
	 * 访问方法 POST https://cart.jd.com/batchRemoveSkusFromCart.action
	 * @param requestHandler
	 * @return
	 */
	public int batchRemoveSkusFromCart(final HttpClientRequestHandler requestHandler) {
		if(null==requestHandler) {
			return -1;
		}
		//Form Data:
		//(empty)
		//t:0
		//outSkus:
		//random:0.9549662366840277
		//locationId:17-1396-1398-23394
		Map<String,Object> formDataMap = requestHandler.getCookieByName(requestHandler, "ipLoc-djd");
		formDataMap.put("t", 0);//
		formDataMap.put("outSkus", "");
		formDataMap.put("random", String.valueOf(Math.random()));
		formDataMap.put("locationId",formDataMap.get("ipLoc-djd"));
		formDataMap.remove("ipLoc-djd");
		//设置访问方法 POST https://cart.jd.com/batchRemoveSkusFromCart.action
		RequestBuilder requestBuilder = RequestBuilder.post()// Post 方法
				.setUri("https://cart.jd.com/batchRemoveSkusFromCart.action");// 设置访问的Uri
		// 设置访问的Header  与selectAllItem cancelAllItem 方法的Header一样
		HttpRequestHeaderGenerator.setSelectOrCancelAllItemHeaders(requestBuilder);
		//添加From表达数据
		try {
			requestBuilder.setEntity(new UrlEncodedFormEntity(
					HttpClientRequestHandler.generateNameValuePairs(formDataMap), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 生成访问方法
		HttpUriRequest requestMethod = requestBuilder.build();
		// 保存访问方法
		requestHandler.setRequestMethod(requestMethod);
		// 发送请求
		ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "batchRemoveSkusFromCart");
		return responseRet.getStautsLine().getStatusCode();
	}
	
	

}
