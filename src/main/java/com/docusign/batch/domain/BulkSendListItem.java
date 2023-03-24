package com.docusign.batch.domain;

public class BulkSendListItem extends AbstractEnvelopeItem {

	private String name;
	private String listId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}
}