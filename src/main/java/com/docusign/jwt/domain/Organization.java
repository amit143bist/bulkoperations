package com.docusign.jwt.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "organization_id", "links" })
public class Organization {

	@JsonProperty("organization_id")
	private String organizationId;
	@JsonProperty("links")
	private List<Link> links = null;

	@JsonProperty("organization_id")
	public String getOrganizationId() {
		return organizationId;
	}

	@JsonProperty("organization_id")
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
	}

	@JsonProperty("links")
	public void setLinks(List<Link> links) {
		this.links = links;
	}

}