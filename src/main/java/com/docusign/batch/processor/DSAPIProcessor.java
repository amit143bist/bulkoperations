/**
 * 
 */
package com.docusign.batch.processor;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.EnvelopeDetails;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.domain.Expirations;
import com.docusign.proserv.application.domain.Notification;
import com.docusign.proserv.application.domain.Reminders;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Amit.Bist
 *
 */
public class DSAPIProcessor extends AbstractAPIProcessor implements ItemProcessor<EnvelopeDetails, EnvelopeDetails> {

	final static Logger logger = LogManager.getLogger(DSAPIProcessor.class);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public EnvelopeDetails process(EnvelopeDetails envDetails) throws Exception {

		Notification notification = createRequest(envDetails);

		objectMapper.setSerializationInclusion(Include.NON_NULL);

		String msgBody = objectMapper.writeValueAsString(notification);

		AccessToken accessToken = cacheManager.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> uri = new HttpEntity<String>(msgBody, headers);
		try {

			String url = MessageFormat.format(psProps.getEnvelopesNotificationApi(), baseUri, accountGuid,
					envDetails.getEnvelopeId());

			logger.debug("url in DSNotificationProcessor.process() " + url + " uri is " + uri);

			callDSAPI(proServServiceTemplate, appParameters, envDetails, uri, url, HttpMethod.PUT);
		} catch (Exception exp) {

			exp.printStackTrace();

			handleExceptionData(objectMapper, envDetails, exp);
		}

		return envDetails;
	}

	/**
	 * @param envDetails
	 * @return
	 */
	private Notification createRequest(EnvelopeDetails envDetails) {

		Notification notification = new Notification();

		if (null != envDetails.getReminderEnabled()) {
			Reminders reminders = new Reminders();
			reminders.setReminderDelay(String.valueOf(envDetails.getReminderDelay()));
			reminders.setReminderEnabled(String.valueOf(envDetails.getReminderEnabled()));
			reminders.setReminderFrequency(String.valueOf(envDetails.getReminderFrequency()));
			notification.setReminders(reminders);
		}

		if (null != envDetails.getExpireEnabled()) {
			Expirations expirations = new Expirations();
			expirations.setExpireAfter(String.valueOf(envDetails.getExpireAfter()));
			expirations.setExpireEnabled(String.valueOf(envDetails.getExpireEnabled()));
			expirations.setExpireWarn(String.valueOf(envDetails.getExpireWarn()));
			notification.setExpirations(expirations);
		}

		return notification;
	}
}