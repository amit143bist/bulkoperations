package com.docusign.batch.item.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class CustomLineCallbackAsyncHandler implements LineCallbackHandler {

	final static Logger logger = LogManager.getLogger(CustomLineCallbackAsyncHandler.class);

	private CustomFlatFileHeaderCallback customFlatFileHeaderCallback;

	private DelimitedLineTokenizer lineTokenizer;

	private String delimiter;

	private String successColumns;

	private String fileName;

	public String getSuccessColumns() {
		return successColumns;
	}

	public void setSuccessColumns(String successColumns) {
		this.successColumns = successColumns;
	}

	public CustomFlatFileHeaderCallback getCustomFlatFileHeaderCallback() {
		return customFlatFileHeaderCallback;
	}

	public void setCustomFlatFileHeaderCallback(CustomFlatFileHeaderCallback customFlatFileHeaderCallback) {
		this.customFlatFileHeaderCallback = customFlatFileHeaderCallback;
	}

	public DelimitedLineTokenizer getLineTokenizer() {
		return lineTokenizer;
	}

	public void setLineTokenizer(DelimitedLineTokenizer lineTokenizer) {
		this.lineTokenizer = lineTokenizer;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void handleLine(String line) {

		logger.debug("CustomLineCallbackAsyncHandler.handleLine() >>> " + line + " delimiter- " + this.delimiter);

		customFlatFileHeaderCallback.prepareHeaderArrayFromMap(getFileName(), line);
		customFlatFileHeaderCallback.setOriginalHeaderLine(line);
		customFlatFileHeaderCallback.getHeaderArray();

		String outputHeaderLine = this.successColumns;

		customFlatFileHeaderCallback.setHeaderLine(outputHeaderLine);

	}

}