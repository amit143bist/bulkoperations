/**
 * 
 */
package com.docusign.batch.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ItemProcessListener;

import com.docusign.batch.domain.AbstractEnvelopeItem;

/**
 * @author Amit.Bist
 *
 */
public class CustomItemProcessListener extends AbstractItemProcessListener<AbstractEnvelopeItem>
		implements ItemProcessListener<AbstractEnvelopeItem, AbstractEnvelopeItem> {

	final static Logger logger = LogManager.getLogger(CustomItemProcessListener.class);

	@Override
	public void beforeProcess(AbstractEnvelopeItem item) {

		logger.debug("Before Process " + item.getEnvelopeId());

	}

	@Override
	public void afterProcess(AbstractEnvelopeItem item, AbstractEnvelopeItem result) {

		logger.debug("After Process " + item.getEnvelopeId());

		if (null != item) {

			isThreadRequiredToSleep(result);
		}
	}

	@Override
	public void onProcessError(AbstractEnvelopeItem item, Exception e) {

		logger.error("On Process Error " + item.getEnvelopeId());
	}

}