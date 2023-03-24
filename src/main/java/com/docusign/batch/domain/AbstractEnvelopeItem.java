package com.docusign.batch.domain;

public abstract class AbstractEnvelopeItem {

	private String envelopeId;

	private Boolean success;

	private String transMessage;

	private String rateLimitReset;

	private String rateLimitLimit;

	private String rateLimitRemaining;

	private String burstLimitRemaining;

	private String burstLimitLimit;

	private String docuSignTraceToken;

	private Integer httpStatusCode;

	private String transactionTimeStamp;

	public String getEnvelopeId() {
		return envelopeId;
	}

	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getTransMessage() {
		return transMessage;
	}

	public void setTransMessage(String transMessage) {
		this.transMessage = transMessage;
	}

	public String getRateLimitReset() {
		return rateLimitReset;
	}

	public void setRateLimitReset(String rateLimitReset) {
		this.rateLimitReset = rateLimitReset;
	}

	public String getRateLimitLimit() {
		return rateLimitLimit;
	}

	public void setRateLimitLimit(String rateLimitLimit) {
		this.rateLimitLimit = rateLimitLimit;
	}

	public String getRateLimitRemaining() {
		return rateLimitRemaining;
	}

	public void setRateLimitRemaining(String rateLimitRemaining) {
		this.rateLimitRemaining = rateLimitRemaining;
	}

	public String getBurstLimitRemaining() {
		return burstLimitRemaining;
	}

	public void setBurstLimitRemaining(String burstLimitRemaining) {
		this.burstLimitRemaining = burstLimitRemaining;
	}

	public String getBurstLimitLimit() {
		return burstLimitLimit;
	}

	public void setBurstLimitLimit(String burstLimitLimit) {
		this.burstLimitLimit = burstLimitLimit;
	}

	public String getDocuSignTraceToken() {
		return docuSignTraceToken;
	}

	public void setDocuSignTraceToken(String docuSignTraceToken) {
		this.docuSignTraceToken = docuSignTraceToken;
	}

	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatus(Integer httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public String getTransactionTimeStamp() {
		return transactionTimeStamp;
	}

	public void setTransactionTimeStamp(String transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}
}