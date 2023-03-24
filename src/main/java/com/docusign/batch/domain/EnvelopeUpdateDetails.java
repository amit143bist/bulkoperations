/**
 * 
 */
package com.docusign.batch.domain;

/**
 * @author Amit.Bist
 *
 */
//envelopeId, envelopeStatus, voidedReason, emailSubject, emailBlurb, purgeState, resendEnvelope
public class EnvelopeUpdateDetails extends AbstractEnvelopeItem {

	private String brandId;

	private String expireWarn;

	private String emailBlurb;

	private String purgeState;

	private String expireAfter;

	private String allowMarkup;

	private String voidedReason;

	private String emailSubject;

	private String allowComments;

	private String enableWetSign;

	private String allowReassign;

	private String useDisclosure;

	private String expireEnabled;

	private String reminderDelay;

	private String envelopeStatus;

	private String resendEnvelope;

	private String reminderEnabled;

	private String allowViewHistory;

	private String reminderFrequency;

	private String copyRecipientData;

	private String envelopeIdStamping;

	private String enforceSignerVisibility;

	private String allowRecipientRecursion;

	private String disableResponsiveDocument;

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

	public String getCopyRecipientData() {
		return copyRecipientData;
	}

	public void setCopyRecipientData(String copyRecipientData) {
		this.copyRecipientData = copyRecipientData;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getAllowComments() {
		return allowComments;
	}

	public void setAllowComments(String allowComments) {
		this.allowComments = allowComments;
	}

	public String getAllowMarkup() {
		return allowMarkup;
	}

	public void setAllowMarkup(String allowMarkup) {
		this.allowMarkup = allowMarkup;
	}

	public String getAllowReassign() {
		return allowReassign;
	}

	public void setAllowReassign(String allowReassign) {
		this.allowReassign = allowReassign;
	}

	public String getAllowRecipientRecursion() {
		return allowRecipientRecursion;
	}

	public void setAllowRecipientRecursion(String allowRecipientRecursion) {
		this.allowRecipientRecursion = allowRecipientRecursion;
	}

	public String getAllowViewHistory() {
		return allowViewHistory;
	}

	public void setAllowViewHistory(String allowViewHistory) {
		this.allowViewHistory = allowViewHistory;
	}

	public String getDisableResponsiveDocument() {
		return disableResponsiveDocument;
	}

	public void setDisableResponsiveDocument(String disableResponsiveDocument) {
		this.disableResponsiveDocument = disableResponsiveDocument;
	}

	public String getEnableWetSign() {
		return enableWetSign;
	}

	public void setEnableWetSign(String enableWetSign) {
		this.enableWetSign = enableWetSign;
	}

	public String getEnforceSignerVisibility() {
		return enforceSignerVisibility;
	}

	public void setEnforceSignerVisibility(String enforceSignerVisibility) {
		this.enforceSignerVisibility = enforceSignerVisibility;
	}

	public String getEnvelopeIdStamping() {
		return envelopeIdStamping;
	}

	public void setEnvelopeIdStamping(String envelopeIdStamping) {
		this.envelopeIdStamping = envelopeIdStamping;
	}

	public String getUseDisclosure() {
		return useDisclosure;
	}

	public void setUseDisclosure(String useDisclosure) {
		this.useDisclosure = useDisclosure;
	}

}