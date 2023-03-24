package com.docusign.batch.item.file;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.docusign.jwt.domain.AccessToken;

public class DSEnvelopeFieldExtractor extends AbstractDSFieldExtractor {

	final static Logger logger = LogManager.getLogger(DSTemplateFieldExtractor.class);

	private static ConcurrentHashMap<String, List<Map<String, String>>> envelopeIdDataMap = new ConcurrentHashMap<String, List<Map<String, String>>>();

	ReentrantLock lock = new ReentrantLock();
	static int counter = 0;

	public Map<String, List<Map<String, String>>> readEnvelopeAndPopulateMap(String baseUri, String accountGuid,
			String templateOrDraftEnvelopeId, boolean isTemplate) {

		logger.debug(" ******************** Calling readEnvelopeAndPopulateMap ******************** ");

		lock.lock();
		try {

			if (null == envelopeIdDataMap || envelopeIdDataMap.isEmpty()
					|| (null != envelopeIdDataMap && null == envelopeIdDataMap.get(templateOrDraftEnvelopeId))) {

				AccessToken accessToken = cacheManager.getAccessToken();

				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
				headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

				HttpEntity<String> uri = new HttpEntity<String>(headers);

				String url = null;
				if (isTemplate) {

					url = MessageFormat.format(psProps.getTemplatesGetApi(), baseUri, accountGuid,
							templateOrDraftEnvelopeId);
				} else {

					url = MessageFormat.format(psProps.getEnvelopesGetApi(), baseUri, accountGuid,
							templateOrDraftEnvelopeId);
				}

				logger.info(" Calling readEnvelopeAndPopulateMap with url >>>>> " + url);

				String envelopeJSON = proServServiceTemplate
						.callDSAPI(appParameters, uri, url, HttpMethod.GET, String.class).getBody();

				createMaps(templateOrDraftEnvelopeId, envelopeIdDataMap, envelopeJSON);

			} else {

				logger.info(
						"templateOrDraftEnvelopeId -> " + templateOrDraftEnvelopeId + " exist in envelopeIdDataMap");
			}

			if (logger.isDebugEnabled()) {

				logger.debug("EnvelopeIdDataMap>>> " + envelopeIdDataMap + " counter " + counter);
			}
			counter++;
		} finally {
			lock.unlock();
		}

		return envelopeIdDataMap;
	}
}