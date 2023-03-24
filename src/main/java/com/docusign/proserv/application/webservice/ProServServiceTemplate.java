package com.docusign.proserv.application.webservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.docusign.batch.domain.AppParameters;
import com.docusign.proserv.application.utils.PSUtils;

public class ProServServiceTemplate {

	final static Logger logger = LogManager.getLogger(ProServServiceTemplate.class);

	private RestTemplate restTemplate = null;

	public RestTemplate getRestTemplate(AppParameters appParameters) {

		if (null == restTemplate) {

			restTemplate = PSUtils.initiateRestTemplate(appParameters.getProxyHost(), appParameters.getProxyPort());
		}

		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public <T> ResponseEntity<T> callDSAPI(AppParameters appParameters, HttpEntity<String> uri, String url,
			HttpMethod httpMethod, Class<T> returnType) {

		ResponseEntity<T> responseEntity = this.getRestTemplate(appParameters).exchange(url, httpMethod, uri,
				returnType);

		return responseEntity;
	}
}