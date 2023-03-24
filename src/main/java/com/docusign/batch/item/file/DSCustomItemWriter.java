package com.docusign.batch.item.file;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.docusign.batch.domain.EnvelopeBatchItem;

public class DSCustomItemWriter implements ItemStreamWriter<EnvelopeBatchItem> {

	final static Logger logger = LogManager.getLogger(DSCustomItemWriter.class);

	private String fieldName;
	private ItemStreamWriter<EnvelopeBatchItem> successDelegate;
	private ItemStreamWriter<EnvelopeBatchItem> failureDelegate;

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setSuccessDelegate(ItemStreamWriter<EnvelopeBatchItem> successDelegate) {
		this.successDelegate = successDelegate;
	}

	public void setFailureDelegate(ItemStreamWriter<EnvelopeBatchItem> failureDelegate) {
		this.failureDelegate = failureDelegate;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {

		successDelegate.open(executionContext);
		failureDelegate.open(executionContext);

	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {

		successDelegate.update(executionContext);
		failureDelegate.update(executionContext);

	}

	@Override
	public void close() throws ItemStreamException {

		successDelegate.close();
		failureDelegate.close();

	}

	@Override
	public void write(List<? extends EnvelopeBatchItem> items) throws Exception {

		for (EnvelopeBatchItem item : items) {

			BeanWrapper bw = new BeanWrapperImpl(item);

			logger.debug("FieldName is " + fieldName + " value is " + bw.getPropertyValue(fieldName));
			if (null != bw.getPropertyValue(fieldName) && ((boolean) (bw.getPropertyValue(fieldName)))) {

				logger.debug("Calling SuccessDelegate for items " + items);
				successDelegate.write(items);
			} else {

				logger.debug("Calling FailureDelegate for items " + items);
				failureDelegate.write(items);
			}
		}

	}

}