package com.docusign.batch.mapper;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.docusign.batch.domain.EnvelopeUpdateDetails;
import com.docusign.exception.EmptyDataException;
import com.docusign.exception.InvalidInputException;
import com.docusign.jwt.utils.AppUtils;
import com.docusign.proserv.application.domain.EnvelopePurgeState;
import com.docusign.proserv.application.domain.EnvelopeStatus;

public class EnvelopeUpdateSetMapper implements FieldSetMapper<EnvelopeUpdateDetails> {

	final static Logger logger = LogManager.getLogger(EnvelopeUpdateSetMapper.class);

	// envelopeId, envelopeStatus, voidedReason, emailSubject, emailBlurb,
	// purgeState, resendEnvelope

	@Override
	public EnvelopeUpdateDetails mapFieldSet(FieldSet fieldSet) throws BindException {

		EnvelopeUpdateDetails envelopeUpdateDetails = new EnvelopeUpdateDetails();

		logger.debug("envelopeId- " + fieldSet.readString("envelopeId"));
		logger.debug("resendEnvelope- " + fieldSet.readString("resendEnvelope"));
		logger.debug("reminderFrequency- " + fieldSet.readInt("reminderFrequency"));

		if (!StringUtils.isEmpty(fieldSet.readString(0))) {

			envelopeUpdateDetails.setEnvelopeId(fieldSet.readString(0));
		} else {
			throw new EmptyDataException("envelopeId");
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(1))) {

			if (!EnumUtils.isValidEnum(EnvelopeStatus.class, fieldSet.readRawString(1).toUpperCase())) {

				throw new InvalidInputException(fieldSet.readRawString(1) + " as envelopestatus ",
						envelopeUpdateDetails.getEnvelopeId());
			}
			envelopeUpdateDetails.setEnvelopeStatus(fieldSet.readRawString(1).toLowerCase());
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(2))) {

			envelopeUpdateDetails.setVoidedReason(fieldSet.readRawString(2));

			if (StringUtils.isEmpty(envelopeUpdateDetails.getEnvelopeStatus())) {
				envelopeUpdateDetails.setEnvelopeStatus(EnvelopeStatus.VOIDED.toString().toLowerCase());
			}
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(3))) {

			envelopeUpdateDetails.setEmailSubject(fieldSet.readRawString(3));
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(4))) {

			envelopeUpdateDetails.setEmailBlurb(fieldSet.readRawString(4));
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(5))) {

			if (!EnumUtils.isValidEnum(EnvelopePurgeState.class, fieldSet.readRawString(5).toUpperCase())) {

				throw new InvalidInputException(fieldSet.readRawString(5) + " as purgestate ",
						envelopeUpdateDetails.getEnvelopeId());
			}
			envelopeUpdateDetails.setPurgeState(fieldSet.readRawString(5).toLowerCase());
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(6))) {

			if (AppUtils.isBooleanString(fieldSet.readRawString(6))) {

				envelopeUpdateDetails.setResendEnvelope(fieldSet.readRawString(6));
			} else {

				throw new InvalidInputException(fieldSet.readRawString(6) + " as resendEnvelope ",
						envelopeUpdateDetails.getEnvelopeId());
			}

		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(7))) {

			if (AppUtils.isBooleanString(fieldSet.readRawString(7))) {
				
				envelopeUpdateDetails.setReminderEnabled(fieldSet.readRawString(7));
			} else {

				throw new InvalidInputException(fieldSet.readRawString(7) + " as reminderEnabled ",
						envelopeUpdateDetails.getEnvelopeId());
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(8))) {

				if (NumberUtils.isCreatable(fieldSet.readRawString(8))) {// Check if value is passed as a valid Integer
																			// or not

					envelopeUpdateDetails.setReminderDelay(fieldSet.readRawString(8));
				} else {

					throw new InvalidInputException(fieldSet.readRawString(8) + " as reminderDelay ",
							envelopeUpdateDetails.getEnvelopeId());
				}

			} else {
				throw new EmptyDataException("reminderDelay");
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(9))) {

				if (NumberUtils.isCreatable(fieldSet.readRawString(9))) {// Check if value is passed as a valid Integer
																			// or not

					envelopeUpdateDetails.setReminderFrequency(fieldSet.readRawString(9));
				} else {

					throw new InvalidInputException(fieldSet.readRawString(9) + " as reminderFrequency ",
							envelopeUpdateDetails.getEnvelopeId());
				}

			} else {
				throw new EmptyDataException("reminderFrequency");
			}
		}

		if (!StringUtils.isEmpty(fieldSet.readRawString(10))) {

			if (AppUtils.isBooleanString(fieldSet.readRawString(10))) {
				envelopeUpdateDetails.setExpireEnabled(fieldSet.readRawString(10));
			} else {

				throw new InvalidInputException(fieldSet.readRawString(10) + " as expireEnabled ",
						envelopeUpdateDetails.getEnvelopeId());
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(11))) {

				if (NumberUtils.isCreatable(fieldSet.readRawString(11))) {

					envelopeUpdateDetails.setExpireAfter(fieldSet.readRawString(11));
				} else {

					throw new InvalidInputException(fieldSet.readRawString(11) + " as expireAfter ",
							envelopeUpdateDetails.getEnvelopeId());
				}

			} else {
				throw new EmptyDataException("expireAfter");
			}

			if (!StringUtils.isEmpty(fieldSet.readRawString(12))) {

				if (NumberUtils.isCreatable(fieldSet.readRawString(12))) {

					envelopeUpdateDetails.setExpireWarn(fieldSet.readRawString(12));
				} else {

					throw new InvalidInputException(fieldSet.readRawString(12) + " as expireWarn ",
							envelopeUpdateDetails.getEnvelopeId());
				}

			} else {
				throw new EmptyDataException("expireWarn");
			}
		}

		return envelopeUpdateDetails;
	}

}