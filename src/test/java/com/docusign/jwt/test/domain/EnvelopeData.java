package com.docusign.jwt.test.domain;

public class EnvelopeData {

	private String envelopeId;
	private String reminderEnabled;
	private String reminderDelay;
	private String reminderFrequency;
	private String expireEnabled;
	private String expireAfter;
	private String expireWarn;
	
	public String getEnvelopeId() {
		return envelopeId;
	}
	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}
	public String getReminderEnabled() {
		return reminderEnabled;
	}
	public void setReminderEnabled(String reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}
	public String getReminderDelay() {
		return reminderDelay;
	}
	public void setReminderDelay(String reminderDelay) {
		this.reminderDelay = reminderDelay;
	}
	public String getReminderFrequency() {
		return reminderFrequency;
	}
	public void setReminderFrequency(String reminderFrequency) {
		this.reminderFrequency = reminderFrequency;
	}
	public String getExpireEnabled() {
		return expireEnabled;
	}
	public void setExpireEnabled(String expireEnabled) {
		this.expireEnabled = expireEnabled;
	}
	public String getExpireAfter() {
		return expireAfter;
	}
	public void setExpireAfter(String expireAfter) {
		this.expireAfter = expireAfter;
	}
	public String getExpireWarn() {
		return expireWarn;
	}
	public void setExpireWarn(String expireWarn) {
		this.expireWarn = expireWarn;
	}
}