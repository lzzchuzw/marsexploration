package com.exploration.shoppingrush;
/**
 * 
* @ClassName: JDShoppingRushParam
* @Description: 京东抢购参数
* @author: leisure
* @date: 2018年9月3日 上午9:20:05
 */
public class JDShoppingRushParam {
	
	private String productId ;
	private String startTime;
	private FetchUrlType fetchUrlType;
	private boolean isNeedSetDefaultAddr;
	private boolean isNeedEmptyCart;
	
	public JDShoppingRushParam() {
		
	}
	
	public JDShoppingRushParam(String productId,String startTime) {
		this.productId = productId;
		this.startTime = startTime;
		this.isNeedSetDefaultAddr = false;
		this.isNeedEmptyCart = false;
	}
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public FetchUrlType getFetchUrlType() {
		return fetchUrlType;
	}
	public void setFetchUrlType(FetchUrlType fetchUrlType) {
		this.fetchUrlType = fetchUrlType;
	}

	public boolean isNeedSetDefaultAddr() {
		return isNeedSetDefaultAddr;
	}

	public void setNeedSetDefaultAddr(boolean isNeedSetDefaultAddr) {
		this.isNeedSetDefaultAddr = isNeedSetDefaultAddr;
	}

	public boolean isNeedEmptyCart() {
		return isNeedEmptyCart;
	}

	public void setNeedEmptyCart(boolean isNeedEmptyCart) {
		this.isNeedEmptyCart = isNeedEmptyCart;
	}

	
	
}
