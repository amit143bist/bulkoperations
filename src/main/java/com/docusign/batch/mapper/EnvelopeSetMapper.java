package com.docusign.batch.mapper;

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.EnvelopeDetails;
import com.docusign.exception.EmptyDataException;

/**
 * @author Amit.Bist
 *
 */
public class EnvelopeSetMapper implements FieldSetMapper<EnvelopeDetails> {

	final static Logger logger = LogManager.getLogger(EnvelopeSetMapper.class);

	private AppParameters appParameters;

	public AppParameters getAppParameters() {
		return appParameters;
	}

	public void setAppParameters(AppParameters appParameters) {
		this.appParameters = appParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.file.mapping.FieldSetMapper#mapFieldSet(
	 * org.springframework.batch.item.file.transform.FieldSet)
	 * 
	 * envelopeId,reminderEnabled,reminderDelay,reminderFrequency,expireEnabled,
	 * expireAfter,expireWarn
	 */
	@Override
	public EnvelopeDetails mapFieldSet(FieldSet fieldSet) throws BindException {

		logger.debug("EnvelopeSetMapper.mapFieldSet()- " + fieldSet.toString());
		EnvelopeDetails envelopeDetails = new EnvelopeDetails();

		if (!StringUtils.isEmpty(fieldSet.readString(0))) {

			envelopeDetails.setEnvelopeId(fieldSet.readString(0));
		} else {
			throw new EmptyDataException("envelopeId");
		}

		if (null != appParameters && null != appParameters.getReminderAllowed() && appParameters.getReminderAllowed()) {

			if (!StringUtils.isEmpty(fieldSet.readRawString(1))) {
				envelopeDetails.setReminderEnabled(Boolean.parseBoolean(fieldSet.readRawString(1)));
			} else {
				throw new EmptyDataException("reminderEnabled");
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(2))) {
				envelopeDetails.setReminderDelay(BigInteger.valueOf(fieldSet.readInt(2)));
			} else {
				throw new EmptyDataException("reminderDelay");
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(3))) {
				envelopeDetails.setReminderFrequency(BigInteger.valueOf(fieldSet.readInt(3)));
			} else {
				throw new EmptyDataException("reminderFrequency");
			}
		}

		if (null != appParameters && null != appParameters.getExpirationAllowed()
				&& appParameters.getExpirationAllowed()) {

			logger.debug("expireEnabled EnvelopeSetMapper.mapFieldSet()- " + fieldSet.readRawString(4) + " readBoolean "
					+ fieldSet.readBoolean(4) + " with parseBoolean " + Boolean.parseBoolean(fieldSet.readRawString(4))
					+ " for envelopeId " + envelopeDetails.getEnvelopeId());

			if (!StringUtils.isEmpty(fieldSet.readRawString(4))) {
				envelopeDetails.setExpireEnabled(Boolean.parseBoolean(fieldSet.readRawString(4)));
			} else {
				throw new EmptyDataException("expireEnabled");
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(5))) {
				envelopeDetails.setExpireAfter(BigInteger.valueOf(fieldSet.readInt(5)));
			} else {
				throw new EmptyDataException("expireAfter");
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(6))) {
				envelopeDetails.setExpireWarn(BigInteger.valueOf(fieldSet.readInt(6)));
			} else {
				throw new EmptyDataException("expireWarn");
			}
		}

		return envelopeDetails;
	}

}