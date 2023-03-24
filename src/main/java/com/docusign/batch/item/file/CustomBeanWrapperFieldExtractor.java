package com.docusign.batch.item.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class CustomBeanWrapperFieldExtractor<T> implements FieldExtractor<T> {

	final static Logger logger = LogManager.getLogger(CustomBeanWrapperFieldExtractor.class);

	private CustomFlatFileHeaderCallback customFlatFileHeaderCallback;

	public CustomFlatFileHeaderCallback getCustomFlatFileHeaderCallback() {
		return customFlatFileHeaderCallback;
	}

	public void setCustomFlatFileHeaderCallback(CustomFlatFileHeaderCallback customFlatFileHeaderCallback) {
		this.customFlatFileHeaderCallback = customFlatFileHeaderCallback;
	}

	@Override
	public Object[] extract(T item) {

		String[] headerArr = customFlatFileHeaderCallback.getHeaderLine()
				.split(customFlatFileHeaderCallback.getDelimiter());

		List<Object> values = new ArrayList<Object>();

		BeanWrapper bw = new BeanWrapperImpl(item);
		for (String propertyName : headerArr) {

			values.add("\"" + bw.getPropertyValue(propertyName) + "\"");

		}

		return values.toArray();

	}

}