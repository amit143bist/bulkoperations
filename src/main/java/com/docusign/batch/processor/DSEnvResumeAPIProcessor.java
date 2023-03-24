package com.docusign.batch.processor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.EnvelopeUpdateDetails;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.domain.EnvelopeResumeRequest;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DSEnvResumeAPIProcessor extends AbstractAPIProcessor
		implements ItemProcessor<EnvelopeUpdateDetails, EnvelopeUpdateDetails> {

	final static Logger logger = LogManager.getLogger(DSEnvResumeAPIProcessor.class);

	@Autowired
	PSProperties psProps;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CacheManager cacheManager;

	@Autowired
	AppParameters appParameters;

	@Autowired
	ProServServiceTemplate proServServiceTemplate;

	private String baseUri;

	private String accountGuid;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	@Override
	public EnvelopeUpdateDetails process(EnvelopeUpdateDetails envelopeUpdateDetails) throws Exception {

		EnvelopeResumeRequest envelopeResumeRequest = createRequest(envelopeUpdateDetails);

		objectMapper.setSerializationInclusion(Include.NON_NULL);

		String msgBody = objectMapper.writeValueAsString(envelopeResumeRequest);

		AccessToken accessToken = cacheManager.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> uri = new HttpEntity<String>(msgBody, headers);

		try {

			String url = MessageFormat.format(psProps.getEnvelopesResumeApi(), baseUri, accountGuid);

			logger.debug("url in DSEnvResumeAPIProcessor.process() " + url);

			callDSAPI(proServServiceTemplate, appParameters, envelopeUpdateDetails, uri, url, HttpMethod.POST);

		} catch (Exception exp) {

			exp.printStackTrace();

			handleExceptionData(objectMapper, envelopeUpdateDetails, exp);
		}

		return envelopeUpdateDetails;
	}

	private EnvelopeResumeRequest createRequest(EnvelopeUpdateDetails envelopeUpdateDetails) {

		EnvelopeResumeRequest envelopeResumeRequest = new EnvelopeResumeRequest();

		List<String> envelopeIds = new ArrayList<String>();
		envelopeIds.add(envelopeUpdateDetails.getEnvelopeId());

		envelopeResumeRequest.setEnvelopeIds(envelopeIds);
		return envelopeResumeRequest;
	}

}