package com.docusign.proserv.application.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "envelopeIds" })
public class EnvelopeResumeRequest {

	@JsonProperty("envelopeIds")
	private List<String> envelopeIds = null;

	@JsonProperty("envelopeIds")
	public List<String> getEnvelopeIds() {
		return envelopeIds;
	}

	@JsonProperty("envelopeIds")
	public void setEnvelopeIds(List<String> envelopeIds) {
		this.envelopeIds = envelopeIds;
	}

}