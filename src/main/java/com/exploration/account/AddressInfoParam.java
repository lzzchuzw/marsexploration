package com.exploration.account;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AddressInfoParam {
	private String consigneeName;
	private int provinceId ;
	private int cityId;
	private int countyId;
	private int townId;
	private String consigneeAddress;
	private String mobile;
	private String phone;
	private String fullAddress;
	private String email;
	private String addressAlias;
	private String easyBuy;
	
/**************************************************************************************/
  
	public Map<String,Object> generateDefaultAddresInfoParam(){
		Map<String,Object> defaultAddrInfoParam = null;
		AddressInfoParam aip = new AddressInfoParam();
		aip.setConsigneeName("雷章章");
		aip.setProvinceId(1);//北京
		aip.setCityId(2800);//北京市
		aip.setCountyId(2851);//海淀区
		aip.setTownId(0);
		//aip.setConsigneeAddress("门头馨园东区7号楼2单元501");
		aip.setConsigneeAddress("自在香山小区26-1");
		aip.setMobile("18311438517");
		//aip.setFullAddress("北京海淀区五环到六环之间门头馨园东区7号楼2单元501");
		aip.setFullAddress("北京海淀区五环到六环之间自在香山小区26-1");
		aip.setPhone(null);
		aip.setEmail(null);
		aip.setAddressAlias(null);
		aip.setEasyBuy("undefined");
		defaultAddrInfoParam = translateAddressInfoParam2Map(aip,"addressInfoParam.");
		return defaultAddrInfoParam;
	}
	public Map<String,Object> translateAddressInfoParam2Map(AddressInfoParam aip, String prefix){
		if(null==aip || null==prefix) {
			return null;
		}
		Map<String,Object> addrInfoParamMap = new HashMap<String,Object>();
		Field[] fields = aip.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++) {
			Field field = fields[i];
			String attrName = fields[i].getName();
			String funName = "get"+attrName.substring(0, 1).toUpperCase()+attrName.substring(1);
			Method getMethod = null;
			try {
				getMethod = aip.getClass().getMethod(funName);
				
				
			} catch (NoSuchMethodException e) {
				
				e.printStackTrace();
			} catch (SecurityException e) {
				
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			}
			try {
				addrInfoParamMap.put(prefix+attrName, getMethod.invoke(aip));
			} catch (IllegalAccessException e) {
			
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				
				e.printStackTrace();
			}
		}
		return addrInfoParamMap;
	}
/*************************************************************************************/
	public String getConsigneeName() {
		return consigneeName;
	}
	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}
	public int getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public int getCountyId() {
		return countyId;
	}
	public void setCountyId(int countyId) {
		this.countyId = countyId;
	}
	public int getTownId() {
		return townId;
	}
	public void setTownId(int townId) {
		this.townId = townId;
	}
	public String getConsigneeAddress() {
		return consigneeAddress;
	}
	public void setConsigneeAddress(String consigneeAddress) {
		this.consigneeAddress = consigneeAddress;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFullAddress() {
		return fullAddress;
	}
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddressAlias() {
		return addressAlias;
	}
	public void setAddressAlias(String addressAlias) {
		this.addressAlias = addressAlias;
	}
	public String getEasyBuy() {
		return easyBuy;
	}
	public void setEasyBuy(String easyBuy) {
		this.easyBuy = easyBuy;
	}
	

}
