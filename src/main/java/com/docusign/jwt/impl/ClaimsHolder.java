package com.docusign.jwt.impl;

import java.util.HashMap;
import java.util.Map;

public class ClaimsHolder {

	private Map<String, Object> claims;

	public ClaimsHolder(Map<String, Object> claims) {
		this.claims = claims == null ? new HashMap<String, Object>() : claims;
	}

	Map<String, Object> getClaims() {
		return claims;
	}
}
