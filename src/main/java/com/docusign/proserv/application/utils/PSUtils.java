package com.docusign.proserv.application.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.shell.support.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.docusign.batch.domain.AppConstants;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.jwt.utils.PemUtils;

public class PSUtils {

	final static Logger logger = LogManager.getLogger(PSUtils.class);

	public static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

	public static List<String> splitStringtoList(String str, String delimiter) {

		String[] splitArr = str.split(delimiter);
		List<String> splitList = Arrays.asList(splitArr);

		return splitList;
	}

	public static void createDirectory(String newDirectoryPath) {

		File file = new File(newDirectoryPath);
		if (!file.exists()) {

			if (file.mkdir()) {

				logger.info("Directory -> " + newDirectoryPath + " is created!");
			} else {

				logger.error("Failed to create directory -> " + newDirectoryPath);
				throw new RuntimeException("Failed to create directory -> " + newDirectoryPath);
			}
		}
	}

	public static RestTemplate initiateRestTemplate(String proxyHost, String proxyPort) {

		RestTemplate restTemplate = null;

		if (!StringUtils.isEmpty(proxyHost)) {

			SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
			clientHttpRequestFactory.setProxy(proxy);

			restTemplate = new RestTemplate(clientHttpRequestFactory);
		} else {
			restTemplate = new RestTemplate();
		}

		return restTemplate;
	}

	/**
	 * @param plainCreds
	 * @return
	 */
	public static String getEncodedBase64Data(String plainCreds) {

		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		return new String(base64CredsBytes);
	}

	/**
	 * get value from {Properties}
	 * 
	 * @param props
	 * @param key
	 * @return
	 */
	public static String getPropertyIgnoreCase(String key, Properties props) {
		return getPropertyIgnoreCase(key, null, props);
	}

	/**
	 * get value from {Properties}, if no key exist then return default value.
	 * 
	 * @param props
	 * @param key
	 * @param defaultV
	 * @return
	 */
	public static String getPropertyIgnoreCase(String key, String defaultV, Properties props) {
		String value = props.getProperty(key);
		if (null != value)
			return value;

		// Not matching with the actual key then
		Set<Entry<Object, Object>> s = props.entrySet();
		Iterator<Entry<Object, Object>> it = s.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			if (key.equalsIgnoreCase((String) entry.getKey())) {
				return (String) entry.getValue();
			}
		}
		return defaultV;
	}

	/**
	 * @param mandatoryPropKeysList
	 * @param props
	 * @return
	 */
	public static boolean validatePropertyFile(List<String> mandatoryPropKeysList, Properties props) {

		boolean validPropFile = true;

		for (String key : mandatoryPropKeysList) {

			if (null == getPropertyIgnoreCase(key, props)) {

				validPropFile = false;
				break;
			}
		}

		return validPropFile;
	}

	/**
	 * @param dirFile
	 * @return
	 */
	public static boolean isInputDirValid(File dirFile) {

		boolean dirValid = false;
		if (dirFile.isDirectory() && null != dirFile.listFiles() && dirFile.listFiles().length > 0) {

			dirValid = true;

		}

		return dirValid;
	}

	/**
	 * @param date1
	 * @param date2
	 * @param timeUnit
	 * @return
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, timeUnit);
	}

	/**
	 * @param userId
	 * @param integratorKey
	 * @param secretKey
	 * @param privatePemPath
	 * @param publicPemPath
	 * @param scope
	 * @param proxyHost
	 * @param proxyPort
	 * @param audience
	 * @param oAuthUrl
	 * @param tokenExpiryLimit
	 * @return AccessToken
	 * @throws IOException
	 */
	public static AccessToken generateAccessToken(final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			final String proxyHost, final String proxyPort, String audience, String oAuthUrl) throws IOException {

		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(privatePemPath,
				AppConstants.RSA_ALGO);
		RSAPublicKey rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(publicPemPath, AppConstants.RSA_ALGO);

		String jwt = createJWT(userId, integratorKey, rsaPrivateKey, rsaPublicKey, scope,
				Long.parseLong(tokenExpiryLimit), audience);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);

		HttpEntity<String> entity = getHttpEntity(jwt);

		logger.info("oAuthUrl for generating accesstoken is " + oAuthUrl);

		ResponseEntity<AccessToken> response = restTemplate.exchange(oAuthUrl, HttpMethod.POST, entity,
				AccessToken.class);

		return response.getBody();
	}

	/**
	 * @param integratorKey
	 * @param secretKey
	 * @param jwt
	 * @return
	 */
	private static HttpEntity<String> getHttpEntity(String jwt) {

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

		String msgBody = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=" + jwt;

		HttpEntity<String> uri = new HttpEntity<String>(msgBody, headers);
		return uri;
	}

	/**
	 * @param subject
	 * @param issuer
	 * @param rsaPrivateKey
	 * @param rsaPublicKey
	 * @param scope
	 * @param audience
	 * @param tokenExpiryLimit
	 * @return JWT
	 */
	private static String createJWT(final String subject, final String issuer, final RSAPrivateKey rsaPrivateKey,
			final RSAPublicKey rsaPublicKey, final String scope, final long tokenExpiryLimit, final String audience) {

		Map<String, Object> headerClaims = new HashMap<String, Object>();
		headerClaims.put("alg", "RS256");
		headerClaims.put("typ", "JWT");

		long iat = Instant.now().getEpochSecond();

		Instant expInstant = Instant.now().plusSeconds(tokenExpiryLimit);
		long exp = expInstant.getEpochSecond();

		String jwt = JWT.create().withHeader(headerClaims).withIssuer(issuer).withSubject(subject).withClaim("iat", iat)
				.withClaim("exp", exp).withAudience(audience).withClaim("scope", scope)
				.sign(Algorithm.RSA256(rsaPublicKey, rsaPrivateKey));

		logger.debug(" Created jwt is " + jwt);

		return jwt;
	}

	public static Long addSecondsAndconvertToEpochTime(String dateTimeAsString, Integer numberOfSeconds) {

		Long epochTime = LocalDateTime.parse(dateTimeAsString, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
				.plusSeconds(numberOfSeconds).toEpochSecond(ZoneOffset.UTC);

		return epochTime;
	}

	public static String currentTimeInString() {

		LocalDateTime dateTime = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId());

		if (null != dateTime) {

			return dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
		}

		return null;
	}

	public static String cleanTextContent(String text) {

		// strips off all non-ASCII characters
		text = text.replaceAll("[^\\x00-\\x7F]", "");

		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", "");

		return text.trim();
	}

	public static String escapeMetaCharacters(String inputString) {
		final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
				">", "-", "&", "%" };

		for (int i = 0; i < metaCharacters.length; i++) {
			if (inputString.contains(metaCharacters[i])) {
				inputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
			}
		}
		return inputString;
	}

}