package com.docusign.jwt.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "rel", "href" })
public class Link {

	@JsonProperty("rel")
	private String rel;
	@JsonProperty("href")
	private String href;

	@JsonProperty("rel")
	public String getRel() {
		return rel;
	}

	@JsonProperty("rel")
	public void setRel(String rel) {
		this.rel = rel;
	}

	@JsonProperty("href")
	public String getHref() {
		return href;
	}

	@JsonProperty("href")
	public void setHref(String href) {
		this.href = href;
	}

}