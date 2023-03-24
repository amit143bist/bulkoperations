package com.docusign.jwt.utils;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

	/**
	 * @param plainCreds
	 * @return
	 */
	public static String getEncodedBase64Data(String plainCreds) {

		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		return new String(base64CredsBytes);
	}

	public static boolean isBooleanString(String booleanParam) {

		Pattern queryLangPattern = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);
		Matcher matcher = queryLangPattern.matcher(booleanParam);
		return matcher.matches();
	}
}