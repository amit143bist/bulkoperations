package com.docusign.jwt.test;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.jwt.test.domain.EnvelopeResponse;
import com.docusign.jwt.test.domain.EnvelopeUpdateData;
import com.docusign.jwt.test.util.CSVTestFileWriter;
import com.docusign.jwt.utils.PemUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MainApp {

	private static final String RSA_ALGO = "RSA";

	RestTemplate restTemplate = new RestTemplate();

	ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) {

		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String privatePemPath = "C:\\SWSetup\\UHG/DemoOAuth-private.pem";
		String publicPemPath = "C:\\SWSetup\\UHG/DemoOAuth-public.pem";
		String integratorKey = "4a571161-05ee-4812-83d0-da7ff5c2eab9";
		String userId = "63cf5380-3cff-4120-b321-74911a1b27c1";
		String scope = "signature";
		String audience = "account-d.docusign.com";// For Demo,
													// account.docusign.com for
													// Prod

		try {
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(privatePemPath, RSA_ALGO);
			RSAPublicKey rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(publicPemPath, RSA_ALGO);

			String jwt = createJWT(userId, integratorKey, rsaPrivateKey, rsaPublicKey, scope, audience);

			String msgBody = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=" + jwt;

			System.out.println(jwt);

			String url = "https://account-d.docusign.com/oauth/token";

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

			HttpEntity<String> entity = new HttpEntity<String>(msgBody, headers);

			ResponseEntity<AccessToken> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					AccessToken.class);

			System.out.println(response.getBody().getAccessToken());

			new MainApp().createDummyEnvelopes(response.getBody().getAccessToken());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String createJWT(final String subject, final String issuer, final RSAPrivateKey rsaPrivateKey,
			final RSAPublicKey rsaPublicKey, final String scope, final String audience) {

		Map<String, Object> headerClaims = new HashMap<String, Object>();
		headerClaims.put("alg", "RS256");
		headerClaims.put("typ", "JWT");

		long iat = Instant.now().getEpochSecond();

		Instant expInstant = Instant.now().plusSeconds(3600);
		long exp = expInstant.getEpochSecond();

		String jwt = JWT.create().withHeader(headerClaims).withIssuer(issuer).withSubject(subject).withClaim("iat", iat)
				.withClaim("exp", exp).withAudience(audience).withClaim("scope", scope)
				.sign(Algorithm.RSA256(rsaPublicKey, rsaPrivateKey));

		return jwt;
	}

	private static HttpHeaders getHeaders(String accessToken) {
		HttpHeaders headers = new HttpHeaders();

		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "Bearer " + accessToken);

		return headers;
	}

	@SuppressWarnings("deprecation")
	private void createDummyEnvelopes(String accessToken) {

		System.out.println("Start Time TestDSRestAPI.test() " + new Date(System.currentTimeMillis()));

		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

		List<EnvelopeUpdateData> envelopeUpdateDataList = new ArrayList<EnvelopeUpdateData>();
		try {

			String msgBody = "{\"documents\": [{\"documentBase64\": \"RG9jdUF0dGFjaA==\",\"documentId\": \"12345\",\"fileExtension\": \"txt\",\"name\": \"DocuAttach\"}],\"emailBlurb\": \"Policy # : 987654321001\",\"emailSubject\": \"Please sign with name\",\"enableWetSign\": \"true\",\"recipients\": {\"signers\": [{\"canSignOffline\": \"true\",\"email\": \"docusign.sso+Signer1@gmail.com\",\"name\": \"Signer1\",\"recipientId\": \"1\", \"clientUserId\": \"Signer1\",\"roleName\": \"Seller\",\"routingOrder\": \"1\",\"tabs\": {\"signHereTabs\": [{\"documentId\": \"12345\",\"pageNumber\": \"1\",\"recipientId\": \"1\",\"xPosition\": \"200\",\"yPosition\": \"400\"}]}},{\"canSignOffline\": \"true\",\"email\": \"docusign.sso+Signer2@gmail.com\",\"name\": \"Signer2\",\"recipientId\": \"2\", \"clientUserId\": \"Signer2\",\"roleName\": \"Buyer\",\"routingOrder\": \"2\",\"defaultRecipient\": \"true\",\"tabs\": {\"signHereTabs\": [{\"documentId\": \"12345\",\"pageNumber\": \"1\",\"recipientId\": \"2\",\"xPosition\": \"400\",\"yPosition\": \"400\"}]}}]},\"status\": \"sent\",\"notification\":{\"useAccountDefaults\":\"true\"}\n}";
			HttpEntity<String> uri = new HttpEntity<String>(msgBody, getHeaders(accessToken));

			for (int i = 0; i < 100; i++) {

				ResponseEntity<String> jsonResp = restTemplate.exchange(
						"https://demo.docusign.net"
								+ "/restapi/v2/accounts/2ee10c95-fcac-4ab5-bcc0-488bf607abe7/envelopes",
						HttpMethod.POST, uri, String.class);

				String infoResp = jsonResp.getBody();

				EnvelopeResponse memberInfo = objectMapper.readValue(infoResp, EnvelopeResponse.class);

				System.out.println("MainApp.test()- " + memberInfo.getEnvelopeId());
				EnvelopeUpdateData envelopeUpdateData = new EnvelopeUpdateData();
				envelopeUpdateData.setEnvelopeId(memberInfo.getEnvelopeId());
				envelopeUpdateDataList.add(envelopeUpdateData);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		int count = 0;
		for (EnvelopeUpdateData envelopeUpdateData : envelopeUpdateDataList) {

			count++;

			if (count % 20 == 0) {

				envelopeUpdateData.setPurgeState("documents_and_metadata_queued");
			} else if (count % 2 == 0) {

				envelopeUpdateData.setVoidedReason("Voided by MainApp " + count);
				envelopeUpdateData.setEnvelopeStatus("voided");
			} else if (count % 3 == 0) {

				envelopeUpdateData.setEmailSubject("Email Subject by MainApp " + count);
				envelopeUpdateData.setEmailBlurb("Email Blurb by MainApp " + count);
			} else {
				envelopeUpdateData.setEmailSubject("Email Subject by MainApp " + count);
				envelopeUpdateData.setEmailBlurb("Email Blurb by MainApp " + count);
				envelopeUpdateData.setResendEnvelope("true");
			}

		}

		try {
			CSVTestFileWriter.writeEnvelopeUpdateCsv(
					"C:\\SWSetup\\UHG\\envelopeUpdate\\input\\" + "envelopeUpdateInput.csv", envelopeUpdateDataList);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
