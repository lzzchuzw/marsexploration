package com.exploration.account;

import com.exploration.request.HttpClientRequestHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;





/**
 * 
 * @Description 管理京东账号与相应Http请求的类
 * @author lzz
 * @time 2018年2月8日 上午11:16:32
 */
public class JDAccountWrapper {
	
	private Log log = LogFactory.getLog(this.getClass());
	private JDAccount jdAccount;
	private AccountState accountState;
	private HttpClientRequestHandler requestHandler;
	
/****************************************************************************/
	public JDAccount getJdAccount() {
		return jdAccount;
	}
	public void setJdAccount(JDAccount jdAccount) {
		this.jdAccount = jdAccount;
	}
	public AccountState getAccountState() {
		return accountState;
	}
	public void setAccountState(AccountState accountState) {
		this.accountState = accountState;
	}
	public HttpClientRequestHandler getRequestHandler() {
		return requestHandler;
	}
	public void setRequestHandler(HttpClientRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

}
