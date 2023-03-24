package com.docusign.proserv.application.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
	private String userId;
	private String userName;
	private String email;
	private String firstName;
	private String lastName;
	private String userStatus;
	private String lastLogin;
	private List<UserSettings> userSettings;
	private List<Group> groupList;
	private String permissionProfileName;
	@JsonIgnore
	private String accountId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public List<UserSettings> getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(List<UserSettings> userSettings) {
		this.userSettings = userSettings;
	}

	public List<Group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Group> groupList) {
		this.groupList = groupList;
	}

	public String getPermissionProfileName() {
		return permissionProfileName;
	}

	public void setPermissionProfileName(String permissionProfileName) {
		this.permissionProfileName = permissionProfileName;
	}

	@JsonIgnore
	public String getAccountId() {
		return accountId;
	}

	@JsonIgnore
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
