package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "accountId", "accountIdGuid", "baseUrl", "isDefault", "userName", "userId", "email",
		"siteDescription" })
public class LoginAccount {

	@JsonProperty("name")
	private String name;
	@JsonProperty("accountId")
	private String accountId;
	@JsonProperty("accountIdGuid")
	private String accountIdGuid;
	@JsonProperty("baseUrl")
	private String baseUrl;
	@JsonProperty("isDefault")
	private String isDefault;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("userId")
	private String userId;
	@JsonProperty("email")
	private String email;
	@JsonProperty("siteDescription")
	private String siteDescription;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("accountId")
	public String getAccountId() {
		return accountId;
	}

	@JsonProperty("accountId")
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@JsonProperty("accountIdGuid")
	public String getAccountIdGuid() {
		return accountIdGuid;
	}

	@JsonProperty("accountIdGuid")
	public void setAccountIdGuid(String accountIdGuid) {
		this.accountIdGuid = accountIdGuid;
	}

	@JsonProperty("baseUrl")
	public String getBaseUrl() {
		return baseUrl;
	}

	@JsonProperty("baseUrl")
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@JsonProperty("isDefault")
	public String getIsDefault() {
		return isDefault;
	}

	@JsonProperty("isDefault")
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	@JsonProperty("userName")
	public String getUserName() {
		return userName;
	}

	@JsonProperty("userName")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty("userId")
	public String getUserId() {
		return userId;
	}

	@JsonProperty("userId")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("siteDescription")
	public String getSiteDescription() {
		return siteDescription;
	}

	@JsonProperty("siteDescription")
	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}

}
