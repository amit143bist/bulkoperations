/**
 * 
 */
package com.docusign.batch.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Amit.Bist
 *
 */
public class AppParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5186866194223418347L;
	private Integer maxThreadPoolSize;
	private Integer coreThreadPoolSize;
	private Boolean reminderAllowed;
	private Boolean expirationAllowed;
	private String proxyHost;
	private String proxyPort;
	private String userId;
	private String integratorKey;
	private String privatePemPath;
	private String publicPemPath;
	private String scope;
	private String audience;
	private String url;
	private String tokenExpiryLimit;
	private Date nextRequestedAccessTokenTime;
	private String operationName;
	private Map<String, String> fileNameDraftEnvelopeMap;

	public Integer getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}

	public void setMaxThreadPoolSize(Integer maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	public Integer getCoreThreadPoolSize() {
		return coreThreadPoolSize;
	}

	public void setCoreThreadPoolSize(Integer coreThreadPoolSize) {
		this.coreThreadPoolSize = coreThreadPoolSize;
	}

	public Boolean getReminderAllowed() {
		return reminderAllowed;
	}

	public void setReminderAllowed(Boolean reminderAllowed) {
		this.reminderAllowed = reminderAllowed;
	}

	public Boolean getExpirationAllowed() {
		return expirationAllowed;
	}

	public void setExpirationAllowed(Boolean expirationAllowed) {
		this.expirationAllowed = expirationAllowed;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIntegratorKey() {
		return integratorKey;
	}

	public void setIntegratorKey(String integratorKey) {
		this.integratorKey = integratorKey;
	}

	public String getPrivatePemPath() {
		return privatePemPath;
	}

	public void setPrivatePemPath(String privatePemPath) {
		this.privatePemPath = privatePemPath;
	}

	public String getPublicPemPath() {
		return publicPemPath;
	}

	public void setPublicPemPath(String publicPemPath) {
		this.publicPemPath = publicPemPath;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTokenExpiryLimit() {
		return tokenExpiryLimit;
	}

	public void setTokenExpiryLimit(String tokenExpiryLimit) {
		this.tokenExpiryLimit = tokenExpiryLimit;
	}

	public Date getNextRequestedAccessTokenTime() {
		return nextRequestedAccessTokenTime;
	}

	public void setNextRequestedAccessTokenTime(Date nextRequestedAccessTokenTime) {
		this.nextRequestedAccessTokenTime = nextRequestedAccessTokenTime;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public Map<String, String> getFileNameDraftEnvelopeMap() {
		return fileNameDraftEnvelopeMap;
	}

	public void setFileNameDraftEnvelopeMap(Map<String, String> fileNameDraftEnvelopeMap) {
		this.fileNameDraftEnvelopeMap = fileNameDraftEnvelopeMap;
	}

}