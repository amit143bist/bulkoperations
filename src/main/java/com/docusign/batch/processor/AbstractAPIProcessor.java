package com.docusign.batch.processor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.docusign.batch.domain.AbstractEnvelopeItem;
import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.proserv.application.domain.DSErrors;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Amit.Bist
 *
 */
public abstract class AbstractAPIProcessor {

	final static Logger logger = LogManager.getLogger(AbstractAPIProcessor.class);

	/**
	 * @param envelopeItem
	 * @param responseHeaders
	 * @param callingCode
	 * @param httpStatusCode
	 */
	protected void processResponseHeaders(AbstractEnvelopeItem envelopeItem, HttpHeaders responseHeaders,
			String callingCode, Integer httpStatusCode) {

		logger.info("Setting DS API Limit header and values");
		envelopeItem.setHttpStatus(httpStatusCode);
		Set<Entry<String, List<String>>> headerEntrySet = responseHeaders.entrySet();
		for (Entry<String, List<String>> headerEntry : headerEntrySet) {

			logger.debug("In " + callingCode + " AbstractAPIProcessor.processResponseHeaders() HeaderKey- "
					+ headerEntry.getKey() + " Value- " + headerEntry.getValue() + " httpStatusCode- "
					+ httpStatusCode);

			switch (headerEntry.getKey()) {

			case AppConstants.DS_HEADER_X_RATELIMIT_RESET:
				envelopeItem.setRateLimitReset(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_RATELIMIT_LIMIT:
				envelopeItem.setRateLimitLimit(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_RATELIMIT_REMAINING:
				envelopeItem.setRateLimitRemaining(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_BURSTLIMIT_REMAINING:
				envelopeItem.setBurstLimitRemaining(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_BURSTLIMIT_LIMIT:
				envelopeItem.setBurstLimitLimit(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_DOCUSIGN_TRACETOKEN:
				envelopeItem.setDocuSignTraceToken(headerEntry.getValue().get(0));
				break;
			default:
			}
		}
	}

	/**
	 * @param objectMapper
	 * @param envelopeItem
	 * @param exp
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	protected void handleExceptionData(ObjectMapper objectMapper, AbstractEnvelopeItem envelopeItem, Exception exp)
			throws IOException, JsonParseException, JsonMappingException {

		DSErrors error = null;
		String transMessage = null;

		if (exp instanceof HttpClientErrorException) {

			HttpClientErrorException clientExp = (HttpClientErrorException) exp;

			HttpHeaders responseHeaders = clientExp.getResponseHeaders();

			processResponseHeaders(envelopeItem, responseHeaders, "HttpClientErrorException",
					clientExp.getRawStatusCode());

			logger.error("HttpClientErrorException AbstractAPIProcessor.handleExceptionData()- "
					+ clientExp.getResponseBodyAsString() + " errorCode " + clientExp.getRawStatusCode());
			logger.error(" :::::::::::::::::::: DS Trace Token :::::::::::::::::::: "
					+ envelopeItem.getDocuSignTraceToken());

			if (clientExp.getResponseBodyAsString().contains("errorCode")) {

				error = objectMapper.readValue(clientExp.getResponseBodyAsString(), DSErrors.class);
			}
		}

		logger.error("Exception in AbstractAPIProcessor.handleExceptionData()- " + exp.getMessage() + " to process "
				+ envelopeItem.getEnvelopeId());

		transMessage = exp.getMessage();
		if (null != error) {
			transMessage = error.getErrorCode() + "_" + error.getMessage();
		}

		envelopeItem.setSuccess(false);
		envelopeItem.setTransMessage(transMessage);
		envelopeItem.setTransactionTimeStamp(LocalDateTime.now().toString());
		logger.info("Transaction failed to set in AbstractAPIProcessor.handleExceptionData() for " + envelopeItem.getEnvelopeId()
				+ " errorMessage is " + transMessage);
	}

	/**
	 * @param proServServiceTemplate
	 * @param appParameters
	 * @param envelopeItem
	 * @param uri
	 * @param url
	 */
	protected void callDSAPI(ProServServiceTemplate proServServiceTemplate, AppParameters appParameters,
			AbstractEnvelopeItem envelopeItem, HttpEntity<String> uri, String url, HttpMethod httpMethod) {

		ResponseEntity<String> responseEntity = proServServiceTemplate.getRestTemplate(appParameters).exchange(url,
				httpMethod, uri, String.class);

		logger.info(appParameters.getOperationName() + " completed successfully for " + envelopeItem.getEnvelopeId());
		HttpHeaders responseHeaders = responseEntity.getHeaders();
		processResponseHeaders(envelopeItem, responseHeaders, "SuccessCall", responseEntity.getStatusCodeValue());
		envelopeItem.setSuccess(true);
		envelopeItem.setTransMessage(AppConstants.TRANS_SUCCESS_MSG);
		envelopeItem.setTransactionTimeStamp(LocalDateTime.now().toString());
	}

	protected <T> ResponseEntity<T> callDSAPI(ProServServiceTemplate proServServiceTemplate,
			AppParameters appParameters, HttpEntity<String> uri, String url, HttpMethod httpMethod,
			Class<T> returnType) {

		ResponseEntity<T> responseEntity = proServServiceTemplate.getRestTemplate(appParameters).exchange(url,
				httpMethod, uri, returnType);

		return responseEntity;
	}

}