package com.docusign.batch.domain;

public class EnvelopeItem extends AbstractEnvelopeItem {

	private String uri;

	private String status;

	private String rowData;

	private String statusDateTime;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRowData() {
		return rowData;
	}

	public void setRowData(String rowData) {
		this.rowData = rowData;
	}

	public String getStatusDateTime() {
		return statusDateTime;
	}

	public void setStatusDateTime(String statusDateTime) {
		this.statusDateTime = statusDateTime;
	}

}