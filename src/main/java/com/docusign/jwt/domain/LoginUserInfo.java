package com.docusign.jwt.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "sub", "name", "given_name", "family_name", "created", "email", "accounts" })
public class LoginUserInfo {

	@JsonProperty("sub")
	private String sub;
	@JsonProperty("name")
	private String name;
	@JsonProperty("given_name")
	private String givenName;
	@JsonProperty("family_name")
	private String familyName;
	@JsonProperty("created")
	private String created;
	@JsonProperty("email")
	private String email;
	@JsonProperty("accounts")
	private List<Account> accounts = null;

	@JsonProperty("sub")
	public String getSub() {
		return sub;
	}

	@JsonProperty("sub")
	public void setSub(String sub) {
		this.sub = sub;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("given_name")
	public String getGivenName() {
		return givenName;
	}

	@JsonProperty("given_name")
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@JsonProperty("family_name")
	public String getFamilyName() {
		return familyName;
	}

	@JsonProperty("family_name")
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	@JsonProperty("created")
	public String getCreated() {
		return created;
	}

	@JsonProperty("created")
	public void setCreated(String created) {
		this.created = created;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("accounts")
	public List<Account> getAccounts() {
		return accounts;
	}

	@JsonProperty("accounts")
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

}