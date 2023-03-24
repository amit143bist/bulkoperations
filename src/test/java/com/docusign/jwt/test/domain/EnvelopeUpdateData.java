package com.docusign.jwt.test.domain;

public class EnvelopeUpdateData {

	private String envelopeId;
	private String envelopeStatus;
	private String voidedReason;
	private String emailSubject;
	private String emailBlurb;
	private String purgeState;
	private String resendEnvelope;

	public String getEnvelopeId() {
		return envelopeId;
	}

	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}

	public String getEnvelopeStatus() {
		return envelopeStatus;
	}

	public void setEnvelopeStatus(String envelopeStatus) {
		this.envelopeStatus = envelopeStatus;
	}

	public String getVoidedReason() {
		return voidedReason;
	}

	public void setVoidedReason(String voidedReason) {
		this.voidedReason = voidedReason;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBlurb() {
		return emailBlurb;
	}

	public void setEmailBlurb(String emailBlurb) {
		this.emailBlurb = emailBlurb;
	}

	public String getPurgeState() {
		return purgeState;
	}

	public void setPurgeState(String purgeState) {
		this.purgeState = purgeState;
	}

	public String getResendEnvelope() {
		return resendEnvelope;
	}

	public void setResendEnvelope(String resendEnvelope) {
		this.resendEnvelope = resendEnvelope;
	}
}