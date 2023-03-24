package com.docusign.batch.item.file;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CustomFlatFileHeaderCallback implements FlatFileHeaderCallback {

	final static Logger logger = LogManager.getLogger(CustomFlatFileHeaderCallback.class);

	private String delimiter;

	private String headerLine;

	private String[] headerArray;

	private String originalHeaderLine;

	private static ConcurrentHashMap<String, String[]> lineArrayDataMap = new ConcurrentHashMap<String, String[]>();

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getHeaderLine() {
		return headerLine;
	}

	public String getOriginalHeaderLine() {
		return originalHeaderLine;
	}

	public void setOriginalHeaderLine(String originalHeaderLine) {
		this.originalHeaderLine = originalHeaderLine;
	}

	public void setHeaderLine(String headerLine) {
		this.headerLine = headerLine;
	}

	public String[] getHeaderArray() {

		if (null == headerArray || headerArray.length == 0) {

			this.headerArray = convertHeaderLineToArray();
		}
		return headerArray;
	}

	public void setHeaderArray(String[] headerArray) {

		this.headerArray = headerArray;
	}

	private String[] convertHeaderLineToArray() {

		logger.debug("Inside convertHeaderLineToArray for headerLine " + originalHeaderLine);
		List<String> headerColumnList = Stream.of(this.originalHeaderLine.split(",")).map(String::trim)
				.collect(Collectors.toList());

		String[] headerArray = new String[headerColumnList.size()];
		return headerColumnList.toArray(headerArray);
	}

	public void prepareHeaderArrayFromMap(String fileName, String line) {

		logger.debug("Inside prepareHeaderArrayFromMap for headerLine " + originalHeaderLine + " and fileName-> "
				+ fileName);
		if (!StringUtils.isEmpty(fileName) && null != lineArrayDataMap && null == lineArrayDataMap.get(fileName)) {

			List<String> headerColumnList = Stream.of(line.split(",")).map(String::trim).collect(Collectors.toList());

			String[] headerArray = new String[headerColumnList.size()];

			lineArrayDataMap.put(fileName, headerColumnList.toArray(headerArray));
		}
	}

	public String[] getHeaderArrayFromMap(String fileName) {

		logger.debug(
				"Inside getHeaderArrayFromMap for fileName-> " + fileName + " lineArrayDataMap-> " + lineArrayDataMap);

		if (null != lineArrayDataMap && !lineArrayDataMap.isEmpty()) {

			return lineArrayDataMap.get(fileName);
		}

		return null;
	}

	@Override
	public void writeHeader(Writer writer) throws IOException {

		writer.write(headerLine);
	}

}