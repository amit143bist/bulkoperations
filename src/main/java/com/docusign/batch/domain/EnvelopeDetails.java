/**
 * 
 */
package com.docusign.batch.domain;

import java.math.BigInteger;

/**
 * @author Amit.Bist
 *
 */
// envelopeId,reminderEnabled,reminderDelay,reminderFrequency,expireEnabled,expireAfter,expireWarn
public class EnvelopeDetails extends AbstractEnvelopeItem {

	private Boolean reminderEnabled;

	private BigInteger reminderDelay;

	private BigInteger reminderFrequency;

	private Boolean expireEnabled;

	private BigInteger expireAfter;

	private BigInteger expireWarn;

	public Boolean getReminderEnabled() {
		return reminderEnabled;
	}

	public void setReminderEnabled(Boolean reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}

	public BigInteger getReminderDelay() {
		return reminderDelay;
	}

	public void setReminderDelay(BigInteger reminderDelay) {
		this.reminderDelay = reminderDelay;
	}

	public BigInteger getReminderFrequency() {
		return reminderFrequency;
	}

	public void setReminderFrequency(BigInteger reminderFrequency) {
		this.reminderFrequency = reminderFrequency;
	}

	public Boolean getExpireEnabled() {
		return expireEnabled;
	}

	public void setExpireEnabled(Boolean expireEnabled) {
		this.expireEnabled = expireEnabled;
	}

	public BigInteger getExpireAfter() {
		return expireAfter;
	}

	public void setExpireAfter(BigInteger expireAfter) {
		this.expireAfter = expireAfter;
	}

	public BigInteger getExpireWarn() {
		return expireWarn;
	}

	public void setExpireWarn(BigInteger expireWarn) {
		this.expireWarn = expireWarn;
	}

}