package com.docusign.batch.domain;

import java.util.List;

public class EnvelopeBatchTestItem extends AbstractEnvelopeItem {

	private Boolean canBeSent;
	private List<String> validationErrors = null;
	private List<String> validationErrorDetails = null;

	public Boolean getCanBeSent() {
		return canBeSent;
	}

	public void setCanBeSent(Boolean canBeSent) {
		this.canBeSent = canBeSent;
	}

	public List<String> getValidationErrors() {
		return validationErrors;
	}

	public void setValidationErrors(List<String> validationErrors) {
		this.validationErrors = validationErrors;
	}

	public List<String> getValidationErrorDetails() {
		return validationErrorDetails;
	}

	public void setValidationErrorDetails(List<String> validationErrorDetails) {
		this.validationErrorDetails = validationErrorDetails;
	}

}