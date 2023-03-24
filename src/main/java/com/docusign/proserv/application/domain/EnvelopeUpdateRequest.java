package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "voidedReason", "emailSubject", "emailBlurb", "purgeState", "notification", "envelopeId",
		"copyRecipientData", "enableWetSign", "brandId", "useDisclosure", "allowMarkup", "allowComments",
		"allowReassign", "allowViewHistory", "envelopeIdStamping", "enforceSignerVisibility", "allowRecipientRecursion",
		"disableResponsiveDocument" })
public class EnvelopeUpdateRequest {

	@JsonProperty("status")
	private String status;
	@JsonProperty("voidedReason")
	private String voidedReason;
	@JsonProperty("emailSubject")
	private String emailSubject;
	@JsonProperty("emailBlurb")
	private String emailBlurb;
	@JsonProperty("purgeState")
	private String purgeState;
	@JsonProperty("notification")
	private Notification notification;

	@JsonProperty("envelopeId")
	private String envelopeId;
	@JsonProperty("copyRecipientData")
	private String copyRecipientData;
	@JsonProperty("enableWetSign")
	private String enableWetSign;
	@JsonProperty("brandId")
	private String brandId;
	@JsonProperty("useDisclosure")
	private String useDisclosure;
	@JsonProperty("allowMarkup")
	private String allowMarkup;
	@JsonProperty("allowComments")
	private String allowComments;
	@JsonProperty("allowReassign")
	private String allowReassign;
	@JsonProperty("allowViewHistory")
	private String allowViewHistory;
	@JsonProperty("envelopeIdStamping")
	private String envelopeIdStamping;
	@JsonProperty("enforceSignerVisibility")
	private String enforceSignerVisibility;
	@JsonProperty("allowRecipientRecursion")
	private String allowRecipientRecursion;
	@JsonProperty("disableResponsiveDocument")
	private String disableResponsiveDocument;

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("voidedReason")
	public String getVoidedReason() {
		return voidedReason;
	}

	@JsonProperty("voidedReason")
	public void setVoidedReason(String voidedReason) {
		this.voidedReason = voidedReason;
	}

	@JsonProperty("emailSubject")
	public String getEmailSubject() {
		return emailSubject;
	}

	@JsonProperty("emailSubject")
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	@JsonProperty("emailBlurb")
	public String getEmailBlurb() {
		return emailBlurb;
	}

	@JsonProperty("emailBlurb")
	public void setEmailBlurb(String emailBlurb) {
		this.emailBlurb = emailBlurb;
	}

	@JsonProperty("purgeState")
	public String getPurgeState() {
		return purgeState;
	}

	@JsonProperty("purgeState")
	public void setPurgeState(String purgeState) {
		this.purgeState = purgeState;
	}

	@JsonProperty("notification")
	public Notification getNotification() {
		return notification;
	}

	@JsonProperty("notification")
	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	@JsonProperty("envelopeId")
	public String getEnvelopeId() {
		return envelopeId;
	}

	@JsonProperty("envelopeId")
	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}

	@JsonProperty("copyRecipientData")
	public String getCopyRecipientData() {
		return copyRecipientData;
	}

	@JsonProperty("copyRecipientData")
	public void setCopyRecipientData(String copyRecipientData) {
		this.copyRecipientData = copyRecipientData;
	}

	@JsonProperty("enableWetSign")
	public String getEnableWetSign() {
		return enableWetSign;
	}

	@JsonProperty("enableWetSign")
	public void setEnableWetSign(String enableWetSign) {
		this.enableWetSign = enableWetSign;
	}

	@JsonProperty("brandId")
	public String getBrandId() {
		return brandId;
	}

	@JsonProperty("brandId")
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	@JsonProperty("useDisclosure")
	public String getUseDisclosure() {
		return useDisclosure;
	}

	@JsonProperty("useDisclosure")
	public void setUseDisclosure(String useDisclosure) {
		this.useDisclosure = useDisclosure;
	}

	@JsonProperty("allowMarkup")
	public String getAllowMarkup() {
		return allowMarkup;
	}

	@JsonProperty("allowMarkup")
	public void setAllowMarkup(String allowMarkup) {
		this.allowMarkup = allowMarkup;
	}

	@JsonProperty("allowComments")
	public String getAllowComments() {
		return allowComments;
	}

	@JsonProperty("allowComments")
	public void setAllowComments(String allowComments) {
		this.allowComments = allowComments;
	}

	@JsonProperty("allowReassign")
	public String getAllowReassign() {
		return allowReassign;
	}

	@JsonProperty("allowReassign")
	public void setAllowReassign(String allowReassign) {
		this.allowReassign = allowReassign;
	}

	@JsonProperty("allowViewHistory")
	public String getAllowViewHistory() {
		return allowViewHistory;
	}

	@JsonProperty("allowViewHistory")
	public void setAllowViewHistory(String allowViewHistory) {
		this.allowViewHistory = allowViewHistory;
	}

	@JsonProperty("envelopeIdStamping")
	public String getEnvelopeIdStamping() {
		return envelopeIdStamping;
	}

	@JsonProperty("envelopeIdStamping")
	public void setEnvelopeIdStamping(String envelopeIdStamping) {
		this.envelopeIdStamping = envelopeIdStamping;
	}

	@JsonProperty("enforceSignerVisibility")
	public String getEnforceSignerVisibility() {
		return enforceSignerVisibility;
	}

	@JsonProperty("enforceSignerVisibility")
	public void setEnforceSignerVisibility(String enforceSignerVisibility) {
		this.enforceSignerVisibility = enforceSignerVisibility;
	}

	@JsonProperty("allowRecipientRecursion")
	public String getAllowRecipientRecursion() {
		return allowRecipientRecursion;
	}

	@JsonProperty("allowRecipientRecursion")
	public void setAllowRecipientRecursion(String allowRecipientRecursion) {
		this.allowRecipientRecursion = allowRecipientRecursion;
	}

	@JsonProperty("disableResponsiveDocument")
	public String getDisableResponsiveDocument() {
		return disableResponsiveDocument;
	}

	@JsonProperty("disableResponsiveDocument")
	public void setDisableResponsiveDocument(String disableResponsiveDocument) {
		this.disableResponsiveDocument = disableResponsiveDocument;
	}

}