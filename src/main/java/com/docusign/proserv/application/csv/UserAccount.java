package com.docusign.proserv.application.csv;

public class UserAccount {

	private String environment;
	private String system;
	private String id;
	private String lastLogin;
	private String rights;
	private String disabled;
	private String firstName;
	private String lastName;
	private String owner;
	private String ownerType;
	private String personal;
	private String startDate;
	private String expiry;
	private String description;
	private String node;
	private String approver;
	private String authContact;
	private String groupName;
	private String priv;

	public UserAccount(String environment, String system, String id, String lastLogin, String rights, String disabled,
			String firstName, String lastName, String owner, String ownerType) {
		this.environment = environment;
		this.system = system;
		this.id = id;
		this.lastLogin = lastLogin;
		this.rights = rights;
		this.disabled = disabled;
		this.firstName = firstName;
		this.lastName = lastName;
		this.owner = owner;
		this.ownerType = ownerType;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getSystem() {
		return system;
	}

	public String getId() {
		return id;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public String getRights() {
		return rights;
	}

	public String getDisabled() {
		return disabled;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getAuthContact() {
		return authContact;
	}

	public void setAuthContact(String authContact) {
		this.authContact = authContact;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
}
