package com.docusign.batch.processor;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
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
import com.docusign.jwt.utils.AppUtils;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.domain.EnvelopeUpdateRequest;
import com.docusign.proserv.application.domain.Expirations;
import com.docusign.proserv.application.domain.Notification;
import com.docusign.proserv.application.domain.Reminders;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DSEnvUpdateAPIProcessor extends AbstractAPIProcessor
		implements ItemProcessor<EnvelopeUpdateDetails, EnvelopeUpdateDetails> {

	final static Logger logger = LogManager.getLogger(DSEnvUpdateAPIProcessor.class);

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

		EnvelopeUpdateRequest envelopeUpdateRequest = createRequest(envelopeUpdateDetails);

		objectMapper.setSerializationInclusion(Include.NON_NULL);

		String msgBody = objectMapper.writeValueAsString(envelopeUpdateRequest);

		AccessToken accessToken = cacheManager.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> uri = new HttpEntity<String>(msgBody, headers);

		try {

			String url = MessageFormat.format(psProps.getEnvelopesUpdateApi(), baseUri, accountGuid,
					envelopeUpdateDetails.getEnvelopeId());

			if (null != envelopeUpdateDetails.getResendEnvelope()
					&& AppUtils.isBooleanString(envelopeUpdateDetails.getResendEnvelope())) {
				url = url + "&resend_envelope=true";
			}

			logger.debug("url in DSEnvUpdateAPIProcessor.process() " + url);

			callDSAPI(proServServiceTemplate, appParameters, envelopeUpdateDetails, uri, url, HttpMethod.PUT);

		} catch (Exception exp) {

			exp.printStackTrace();

			handleExceptionData(objectMapper, envelopeUpdateDetails, exp);
		}

		return envelopeUpdateDetails;
	}

	private EnvelopeUpdateRequest createRequest(EnvelopeUpdateDetails envelopeUpdateDetails) {

		EnvelopeUpdateRequest envelopeUpdateRequest = new EnvelopeUpdateRequest();

		if (psProps.getEnvelopeUpdateGlobalVariables()) {

			logger.debug("-------------------- EnvelopeUpdateGlobal Variable is set -------------------- ");

			if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateStatus())) {

				envelopeUpdateRequest.setStatus(psProps.getEnvelopeUpdateStatus());
			}

			if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateVoidedReason())) {

				envelopeUpdateRequest.setVoidedReason(psProps.getEnvelopeUpdateVoidedReason());
			}

			if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateEmailSubject())) {

				envelopeUpdateRequest.setEmailSubject(psProps.getEnvelopeUpdateEmailSubject());
			}

			if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateEmailBlurb())) {

				envelopeUpdateRequest.setEmailBlurb(psProps.getEnvelopeUpdateEmailBlurb());
			}

			if (!StringUtils.isEmpty(psProps.getEnvelopeUpdatePurgeState())) {

				envelopeUpdateRequest.setPurgeState(psProps.getEnvelopeUpdatePurgeState());
			}

			if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateReminderEnabled())
					|| !StringUtils.isEmpty(psProps.getEnvelopeUpdateExpireEnabled())) {

				addNotificationPartToRequestFromGlobalVariables(envelopeUpdateRequest);
			}
		} else {

			if (!StringUtils.isEmpty(envelopeUpdateDetails.getEnvelopeStatus())) {

				envelopeUpdateRequest.setStatus(envelopeUpdateDetails.getEnvelopeStatus());
			}

			if (!StringUtils.isEmpty(envelopeUpdateDetails.getVoidedReason())) {

				envelopeUpdateRequest.setVoidedReason(envelopeUpdateDetails.getVoidedReason());
			}

			if (!StringUtils.isEmpty(envelopeUpdateDetails.getEmailSubject())) {

				envelopeUpdateRequest.setEmailSubject(envelopeUpdateDetails.getEmailSubject());
			}

			if (!StringUtils.isEmpty(envelopeUpdateDetails.getEmailBlurb())) {

				envelopeUpdateRequest.setEmailBlurb(envelopeUpdateDetails.getEmailBlurb());
			}

			if (!StringUtils.isEmpty(envelopeUpdateDetails.getPurgeState())) {

				envelopeUpdateRequest.setPurgeState(envelopeUpdateDetails.getPurgeState());
			}

			if (!StringUtils.isEmpty(envelopeUpdateDetails.getReminderEnabled())
					|| !StringUtils.isEmpty(envelopeUpdateDetails.getExpireEnabled())) {

				addNotificationPartToRequest(envelopeUpdateRequest, envelopeUpdateDetails);
			}
		}

		return envelopeUpdateRequest;
	}

	private void addNotificationPartToRequest(EnvelopeUpdateRequest envelopeUpdateRequest,
			EnvelopeUpdateDetails envelopeUpdateDetails) {

		Notification notification = new Notification();

		if (!StringUtils.isEmpty(envelopeUpdateDetails.getReminderEnabled())) {

			Reminders reminders = new Reminders();
			reminders.setReminderDelay(envelopeUpdateDetails.getReminderDelay());
			reminders.setReminderEnabled(envelopeUpdateDetails.getReminderEnabled());
			reminders.setReminderFrequency(envelopeUpdateDetails.getReminderFrequency());
			notification.setReminders(reminders);
		}

		if (!StringUtils.isEmpty(envelopeUpdateDetails.getExpireEnabled())) {

			Expirations expirations = new Expirations();
			expirations.setExpireAfter(envelopeUpdateDetails.getExpireAfter());
			expirations.setExpireEnabled(envelopeUpdateDetails.getExpireEnabled());
			expirations.setExpireWarn(envelopeUpdateDetails.getExpireWarn());
			notification.setExpirations(expirations);
		}

		envelopeUpdateRequest.setNotification(notification);
	}

	private void addNotificationPartToRequestFromGlobalVariables(EnvelopeUpdateRequest envelopeUpdateRequest) {

		Notification notification = new Notification();

		if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateReminderEnabled())) {

			logger.debug("-------------------- EnvelopeUpdateGlobalReminder is set -------------------- ");

			Reminders reminders = new Reminders();
			reminders.setReminderDelay(psProps.getEnvelopeUpdateReminderDelay());
			reminders.setReminderEnabled(psProps.getEnvelopeUpdateReminderEnabled());
			reminders.setReminderFrequency(psProps.getEnvelopeUpdateReminderFrequency());
			notification.setReminders(reminders);
		}

		if (!StringUtils.isEmpty(psProps.getEnvelopeUpdateExpireEnabled())) {

			logger.debug("-------------------- EnvelopeUpdateGlobalExpire is set -------------------- ");

			Expirations expirations = new Expirations();
			expirations.setExpireAfter(psProps.getEnvelopeUpdateExpireAfter());
			expirations.setExpireEnabled(psProps.getEnvelopeUpdateExpireEnabled());
			expirations.setExpireWarn(psProps.getEnvelopeUpdateExpireWarn());
			notification.setExpirations(expirations);
		}

		envelopeUpdateRequest.setNotification(notification);
	}

}