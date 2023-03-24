package com.docusign.batch.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.docusign.batch.domain.AppParameters;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DSEnvBatchStatusAPIProcessor extends AbstractAPIProcessor {

	final static Logger logger = LogManager.getLogger(DSEnvCreateAsyncAPIProcessor.class);

	@Autowired
	PSProperties psProps;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CacheManager cacheManager;

	@Autowired
	AppParameters appParameters;

	@Autowired
	ProServServiceTemplate proServServiceTemplate;

	String baseUri;

	String accountGuid;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	public void checkBatchStatus(File successDir) {

		if (null != successDir) {

			File[] successFiles = successDir.listFiles();

			Arrays.sort(successFiles, (f1, f2) -> f1.compareTo(f2));

			AccessToken accessToken = cacheManager.getAccessToken();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
			headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> uri = new HttpEntity<String>(headers);

			if (null != successFiles) {

				for (File successFile : successFiles) {

					if (successFile.isFile()) {

						// read file into stream, try-with-resources
						try (Stream<String> stream = Files.lines(Paths.get(successFile.getAbsolutePath()))) {

							stream.forEach(line -> {

								logger.info(line);

								List<String> columnList = null;
								if (!StringUtils.isEmpty(line)) {

									columnList = Stream.of(line.split(",")).map(String::trim)
											.collect(Collectors.toList());

									if (null != columnList) {

										String batchId = columnList.get(0);
										String url = MessageFormat.format(psProps.getBulkListByBatchIdApi(), baseUri,
												accountGuid, batchId);

										logger.debug("url in DSEnvBatchStatusAPIProcessor.process() " + url + " uri is "
												+ uri);

										callDSAPI(proServServiceTemplate, appParameters, uri, url, HttpMethod.GET,
												String.class);
									}
								}
							});

						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
			}
		}

	}

}