package com.docusign.batch.domain;

import java.util.List;

public class EnvelopeBatchItem extends AbstractEnvelopeItem {

	private String batchId;
	private String fileName;
	private String totalSent;
	private String batchSize;
	private String queueLimit;
	private String totalQueued;
	private String totalFailed;
	private String submittedDateTime;
	private String envelopeOrTemplateId;
	private List<String> rowDataList;
	private List<String> errors = null;
	private List<String> errorDetails = null;

	public List<String> getRowDataList() {
		return rowDataList;
	}

	public void setRowDataList(List<String> rowDataList) {
		this.rowDataList = rowDataList;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTotalSent() {
		return totalSent;
	}

	public void setTotalSent(String totalSent) {
		this.totalSent = totalSent;
	}

	public String getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}

	public String getQueueLimit() {
		return queueLimit;
	}

	public void setQueueLimit(String queueLimit) {
		this.queueLimit = queueLimit;
	}

	public String getTotalQueued() {
		return totalQueued;
	}

	public void setTotalQueued(String totalQueued) {
		this.totalQueued = totalQueued;
	}

	public String getTotalFailed() {
		return totalFailed;
	}

	public void setTotalFailed(String totalFailed) {
		this.totalFailed = totalFailed;
	}

	public String getSubmittedDateTime() {
		return submittedDateTime;
	}

	public void setSubmittedDateTime(String submittedDateTime) {
		this.submittedDateTime = submittedDateTime;
	}

	public String getEnvelopeOrTemplateId() {
		return envelopeOrTemplateId;
	}

	public void setEnvelopeOrTemplateId(String envelopeOrTemplateId) {
		this.envelopeOrTemplateId = envelopeOrTemplateId;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<String> errorDetails) {
		this.errorDetails = errorDetails;
	}

}