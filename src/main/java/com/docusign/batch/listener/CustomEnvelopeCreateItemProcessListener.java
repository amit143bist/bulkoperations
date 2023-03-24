/**
 * 
 */
package com.docusign.batch.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ItemProcessListener;

import com.docusign.batch.domain.EnvelopeItem;

/**
 * @author Amit.Bist
 *
 */
public class CustomEnvelopeCreateItemProcessListener extends AbstractItemProcessListener<EnvelopeItem>
		implements ItemProcessListener<String, EnvelopeItem> {

	final static Logger logger = LogManager.getLogger(CustomEnvelopeCreateItemProcessListener.class);

	@Override
	public void beforeProcess(String item) {

		logger.debug("In beforeProcess for " + item);
	}

	@Override
	public void afterProcess(String item, EnvelopeItem result) {

		logger.debug("After Process " + result.getEnvelopeId());

		if (null != item) {

			isThreadRequiredToSleep(result);
		}
	}

	@Override
	public void onProcessError(String item, Exception e) {

		logger.error("In OnProcess Error for " + item + " exception message is " + e.getMessage());
	}

}