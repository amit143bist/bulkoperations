package com.docusign.batch.item.file;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.docusign.jwt.domain.AccessToken;

public class DSTemplateFieldExtractor extends AbstractDSFieldExtractor {

	final static Logger logger = LogManager.getLogger(DSTemplateFieldExtractor.class);

	private static Map<String, List<Map<String, String>>> templateIdDataMap;

	ReentrantLock lock = new ReentrantLock();
	int counter = 0;

	public Map<String, List<Map<String, String>>> readTemplateAndPopulateMap(String baseUri, String accountGuid) {

		logger.debug(" ******************** Calling readTemplateAndPopulateMap ******************** ");

		lock.lock();
		try {

			if (null == templateIdDataMap || templateIdDataMap.isEmpty()) {

				AccessToken accessToken = cacheManager.getAccessToken();

				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
				headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

				HttpEntity<String> uri = new HttpEntity<String>(headers);

				String[] templateIds = psProps.getTemplatesIds().split(",");

				templateIdDataMap = new LinkedHashMap<String, List<Map<String, String>>>();

				for (String templateId : templateIds) {

					String url = MessageFormat.format(psProps.getTemplatesGetApi(), baseUri, accountGuid, templateId);

					logger.info("Calling readTemplateAndPopulateMap with url >>>>> " + url);

					String templateJSON = proServServiceTemplate
							.callDSAPI(appParameters, uri, url, HttpMethod.GET, String.class).getBody();

					createMaps(templateId, templateIdDataMap, templateJSON);
				}

			}

			logger.debug("TemplateIdDataMap>>> " + templateIdDataMap + " counter " + counter);
			counter++;
		} finally {
			lock.unlock();
		}

		return templateIdDataMap;
	}

}