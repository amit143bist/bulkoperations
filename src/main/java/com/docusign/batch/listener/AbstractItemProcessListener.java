package com.docusign.batch.listener;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.docusign.batch.domain.AbstractEnvelopeItem;
import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.EnvelopeBatchItem;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

public abstract class AbstractItemProcessListener<T extends AbstractEnvelopeItem> {

	final static Logger logger = LogManager.getLogger(AbstractItemProcessListener.class);

	@Autowired
	private PSProperties psProps;

	@Value("#{jobParameters}")
	private Map<String, JobParameter> jobParameters;

	protected void isThreadRequiredToSleep(AbstractEnvelopeItem result) {

		boolean threadSleeping = false;

		String jobId = jobParameters.get(AppConstants.SPRING_JOB_ID_PARAM).toString();

		if (!(result instanceof EnvelopeBatchItem)) {

			logger.info(
					"In jobId -> " + jobId + " isThreadRequiredToSleep for envelopeId -> " + result.getEnvelopeId());
		}

		if (!StringUtils.isEmpty(result.getRateLimitReset())) {

			long longEpochTime = Long.parseLong(result.getRateLimitReset());

			Date resetDate = Date.from(Instant.ofEpochSecond(longEpochTime + 5));
			logger.debug("In JobId -> " + jobId + " ResetTime for resetDate " + resetDate);

			long sleepMillis = PSUtils.getDateDiff(Calendar.getInstance().getTime(), resetDate, TimeUnit.MILLISECONDS);

			Float thresholdLimit = Float.parseFloat(result.getRateLimitLimit())
					* Integer.parseInt(psProps.getDSAPIThresholdPercent()) / 100f;

			logger.info("In JobId -> " + jobId
					+ " Checking if need to send thread to sleep in CustomItemProcessListener or not, with API Hourly RateLimitRemaining- "
					+ result.getRateLimitRemaining() + " RateLimitResetValue " + result.getRateLimitReset()
					+ " Float RateLimitRemaining " + Float.parseFloat(result.getRateLimitRemaining())
					+ " Float RateLimitLimit " + Float.parseFloat(result.getRateLimitLimit()) + " "
					+ psProps.getDSAPIThresholdPercent() + " percent of Float RateLimitLimit " + thresholdLimit);

			if ((Float.parseFloat(result.getRateLimitRemaining()) < thresholdLimit) && sleepMillis > 0) {

				sleepThread(resetDate, sleepMillis, "APIHourlyRateLimitThreshold", jobId);
				threadSleeping = true;
			}

		}

		if (psProps.isBurstLimitCheckEnabled() && !threadSleeping
				&& !StringUtils.isEmpty(result.getBurstLimitRemaining())) {

			Float thresholdBurstLimit = Float.parseFloat(result.getBurstLimitLimit())
					* Integer.parseInt(psProps.getDSAPIThresholdPercent()) / 100f;
			logger.info("In JobId -> " + jobId
					+ " Checking if need to send thread to sleep in CustomItemProcessListener or not with result.getBurstLimitRemaining() is "
					+ result.getBurstLimitRemaining() + " Float.parseFloat(result.getBurstLimitRemaining()) -> "
					+ Float.parseFloat(result.getBurstLimitRemaining()) + " and result.getBurstLimitLimit() is "
					+ result.getBurstLimitLimit() + " and ThresholdBurstLimit is " + thresholdBurstLimit);

			if (Float.parseFloat(result.getBurstLimitRemaining()) < thresholdBurstLimit) {

				sleepThread(new Date(PSUtils.addSecondsAndconvertToEpochTime(PSUtils.currentTimeInString(), 5) * 1000),
						35000, "BurstRateLimitThreshold", jobId);
				threadSleeping = true;
			}

		}

		if (psProps.isTooManyRequestCheckEnabled() && !threadSleeping
				&& !StringUtils.isEmpty(result.getRateLimitReset())
				&& HttpStatus.TOO_MANY_REQUESTS.value() == result.getHttpStatusCode()) {

			logger.info("In JobId -> " + jobId
					+ " checking with 429 - TOO_MANY_REQUESTS error code, not handled by Burst or API Hourly limit");
			long longEpochTime = Long.parseLong(result.getRateLimitReset());

			Date resetDate = Date.from(Instant.ofEpochSecond(longEpochTime + 5));
			long sleepMillis = PSUtils.getDateDiff(Calendar.getInstance().getTime(), resetDate, TimeUnit.MILLISECONDS);

			sleepThread(resetDate, (sleepMillis + 5000), "429 - TooManyRequests", jobId);
			threadSleeping = true;
		}
	}

	private void sleepThread(Date resetDate, long sleepMillis, String sleepReason, String jobId) {

		logger.info("In JobId -> " + jobId + " " + sleepReason + " reached so sending thread name -> "
				+ Thread.currentThread().getName() + " and threadId -> " + Thread.currentThread().getId()
				+ " to sleep in sleepThread() for " + sleepMillis + " milliseconds, and expected to wake up at "
				+ resetDate);
		try {

			Thread.sleep(sleepMillis);
		} catch (InterruptedException e) {

			logger.error("InterruptedException thrown for thread name- " + Thread.currentThread().getName()
					+ " and threadId- " + Thread.currentThread().getId());
			e.printStackTrace();
		}
	}
}