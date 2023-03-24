package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "fieldId", "name", "show", "required" })
public class TextCustomField {

	@JsonProperty("fieldId")
	private String fieldId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("show")
	private String show;
	@JsonProperty("required")
	private String required;

	@JsonProperty("fieldId")
	public String getFieldId() {
		return fieldId;
	}

	@JsonProperty("fieldId")
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("show")
	public String getShow() {
		return show;
	}

	@JsonProperty("show")
	public void setShow(String show) {
		this.show = show;
	}

	@JsonProperty("required")
	public String getRequired() {
		return required;
	}

	@JsonProperty("required")
	public void setRequired(String required) {
		this.required = required;
	}

}