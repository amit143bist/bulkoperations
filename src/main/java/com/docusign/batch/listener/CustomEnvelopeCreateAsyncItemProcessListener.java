package com.docusign.batch.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ItemProcessListener;

import com.docusign.batch.domain.EnvelopeBatchItem;

public class CustomEnvelopeCreateAsyncItemProcessListener extends AbstractItemProcessListener<EnvelopeBatchItem>
		implements ItemProcessListener<EnvelopeBatchItem, EnvelopeBatchItem> {

	final static Logger logger = LogManager.getLogger(CustomEnvelopeCreateAsyncItemProcessListener.class);

	@Override
	public void beforeProcess(EnvelopeBatchItem item) {

		logger.debug("In beforeProcess for " + item);
	}

	@Override
	public void afterProcess(EnvelopeBatchItem item, EnvelopeBatchItem result) {

		logger.info("BatchId is " + result.getBatchId());

		if (null != item) {

			isThreadRequiredToSleep(result);
		}
	}

	@Override
	public void onProcessError(EnvelopeBatchItem item, Exception e) {

		logger.error("In OnProcess Error for " + item + " exception message is " + e.getMessage());
	}
}