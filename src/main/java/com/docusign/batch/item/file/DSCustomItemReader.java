package com.docusign.batch.item.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.docusign.batch.domain.EnvelopeBatchItem;

public class DSCustomItemReader implements ItemStreamReader<EnvelopeBatchItem> {

	final static Logger logger = LogManager.getLogger(DSCustomItemReader.class);

	private ItemStreamReader<String> delegate;

	public ItemStreamReader<String> getDelegate() {
		return delegate;
	}

	public void setDelegate(ItemStreamReader<String> delegate) {
		this.delegate = delegate;
	}

	@Override
	public EnvelopeBatchItem read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		logger.debug(" :::::::::::::::::::: Reading Data :::::::::::::::::::: ");
		String line = null;
		List<String> csvRecordList = new ArrayList<String>();

		while ((line = delegate.read()) != null) {

			logger.debug("line read is " + line);
			csvRecordList.add(line);
		}

		EnvelopeBatchItem envelopeBatchItem = null;
		if (null != csvRecordList && !csvRecordList.isEmpty()) {

			envelopeBatchItem = new EnvelopeBatchItem();
			envelopeBatchItem.setRowDataList(csvRecordList);
		}

		return envelopeBatchItem;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {

		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {

		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {

		delegate.close();
	}

}