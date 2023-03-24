package com.docusign.jwt.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "account_id", "is_default", "account_name", "base_uri", "organization" })
public class Account {

	@JsonProperty("account_id")
	private String accountId;
	@JsonProperty("is_default")
	private Boolean isDefault;
	@JsonProperty("account_name")
	private String accountName;
	@JsonProperty("base_uri")
	private String baseUri;
	@JsonProperty("organization")
	private Organization organization;

	@JsonProperty("account_id")
	public String getAccountId() {
		return accountId;
	}

	@JsonProperty("account_id")
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@JsonProperty("is_default")
	public Boolean getIsDefault() {
		return isDefault;
	}

	@JsonProperty("is_default")
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@JsonProperty("account_name")
	public String getAccountName() {
		return accountName;
	}

	@JsonProperty("account_name")
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@JsonProperty("base_uri")
	public String getBaseUri() {
		return baseUri;
	}

	@JsonProperty("base_uri")
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	@JsonProperty("organization")
	public Organization getOrganization() {
		return organization;
	}

	@JsonProperty("organization")
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}