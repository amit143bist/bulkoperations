package com.docusign.proserv.application.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "loginAccounts" })
public class LoginResponse {

	@JsonProperty("loginAccounts")
	private List<LoginAccount> loginAccounts = null;

	@JsonProperty("loginAccounts")
	public List<LoginAccount> getLoginAccounts() {
		return loginAccounts;
	}

	@JsonProperty("loginAccounts")
	public void setLoginAccounts(List<LoginAccount> loginAccounts) {
		this.loginAccounts = loginAccounts;
	}

}
