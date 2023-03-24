package com.docusign.proserv.application.csv;

public class Entitlement {

	private String narId;
	private String rights;
	private String longDescription;
	private String tooltipName;
	private String helpLink;
	private String language;


	public Entitlement(String narId, String rights, String longDescription) {
		this.narId = narId;
		this.rights = rights;
		this.longDescription = longDescription;
	}

	public String getNarId() {
		return narId;
	}

	public void setNarId(String narId) {
		this.narId = narId;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	
	public String getTooltipName() {
		return tooltipName;
	}

	public void setTooltipName(String tooltipName) {
		this.tooltipName = tooltipName;
	}

	public String getHelpLink() {
		return helpLink;
	}

	public void setHelpLink(String helpLink) {
		this.helpLink = helpLink;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "Entitlement [narId=" + narId + ", rights=" + rights + ", longDescription=" + longDescription
				+ ", tooltipName=" + tooltipName + ", helpLink=" + helpLink + ", language=" + language + "]";
	}
}
