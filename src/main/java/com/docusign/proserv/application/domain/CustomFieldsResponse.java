package com.docusign.proserv.application.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "textCustomFields", "listCustomFields" })
public class CustomFieldsResponse {

	@JsonProperty("textCustomFields")
	private List<TextCustomField> textCustomFields = null;
	@JsonProperty("listCustomFields")
	private List<ListCustomField> listCustomFields = null;

	@JsonProperty("textCustomFields")
	public List<TextCustomField> getTextCustomFields() {
		return textCustomFields;
	}

	@JsonProperty("textCustomFields")
	public void setTextCustomFields(List<TextCustomField> textCustomFields) {
		this.textCustomFields = textCustomFields;
	}

	@JsonProperty("listCustomFields")
	public List<ListCustomField> getListCustomFields() {
		return listCustomFields;
	}

	@JsonProperty("listCustomFields")
	public void setListCustomFields(List<ListCustomField> listCustomFields) {
		this.listCustomFields = listCustomFields;
	}

}