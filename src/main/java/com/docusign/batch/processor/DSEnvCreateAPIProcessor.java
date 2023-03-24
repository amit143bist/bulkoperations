package com.docusign.batch.processor;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.EnvelopeItem;
import com.docusign.batch.item.helper.DSEnvelopeCreateHelper;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DSEnvCreateAPIProcessor extends AbstractAPIProcessor implements ItemProcessor<String, EnvelopeItem> {

	final static Logger logger = LogManager.getLogger(DSEnvCreateAPIProcessor.class);

	@Autowired
	PSProperties psProps;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CacheManager cacheManager;

	@Autowired
	AppParameters appParameters;

	@Autowired
	DSEnvelopeCreateHelper dsEnvelopeCreateHelper;

	@Autowired
	ProServServiceTemplate proServServiceTemplate;

	String baseUri;

	String accountGuid;

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
	public EnvelopeItem process(String item) throws Exception {

		logger.debug("Entered and the Rowitem is " + item);

		String envelopeReqBodyJSON = dsEnvelopeCreateHelper.createEnvelopeRequest(convertRowToArray(item), "sent",
				baseUri, accountGuid);

		AccessToken accessToken = cacheManager.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		EnvelopeItem envelopeItem = null;
		HttpEntity<String> uri = new HttpEntity<String>(envelopeReqBodyJSON, headers);
		try {

			String url = MessageFormat.format(psProps.getEnvelopesCreateApi(), baseUri, accountGuid);

			logger.debug("url in DSEnvCreateAPIProcessor.process() " + url + " uri is " + uri);

			ResponseEntity<EnvelopeItem> responseEntity = callDSAPI(proServServiceTemplate, appParameters, uri, url,
					HttpMethod.POST, EnvelopeItem.class);

			envelopeItem = responseEntity.getBody();
			logger.info(
					appParameters.getOperationName() + " completed successfully for " + envelopeItem.getEnvelopeId());
			HttpHeaders responseHeaders = responseEntity.getHeaders();
			processResponseHeaders(envelopeItem, responseHeaders, "SuccessCall", responseEntity.getStatusCodeValue());
			envelopeItem.setSuccess(true);
			envelopeItem.setTransMessage(AppConstants.TRANS_SUCCESS_MSG);
			envelopeItem.setRowData(item);

		} catch (Exception exp) {

			exp.printStackTrace();

			envelopeItem = new EnvelopeItem();
			envelopeItem.setRowData(item);

			handleExceptionData(objectMapper, envelopeItem, exp);
		}

		return envelopeItem;
	}

	private String[] convertRowToArray(String item) {

		List<String> itemColumnList = Stream.of(item.split(",")).map(String::trim).collect(Collectors.toList());

		String[] itemColumnArray = new String[itemColumnList.size()];

		return itemColumnList.toArray(itemColumnArray);

	}

}