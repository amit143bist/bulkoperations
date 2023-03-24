package com.docusign.proserv.application.cache;

import java.io.IOException;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.docusign.batch.domain.AppParameters;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.utils.PSUtils;

public class CacheManager {

	final static Logger logger = LogManager.getLogger(CacheManager.class);

	@Autowired
	AppParameters appParameters;

	private AccessToken accessToken;

	public AccessToken getAccessToken() {

		logger.debug("Entering CacheManager.getAccessToken()");
		if (null == appParameters.getNextRequestedAccessTokenTime()) {

			try {

				logger.info(
						"NextRequestedAccessTokenTime is null in CacheManager.getAccessToken(), so generating new AccessToken");
				accessToken = fetchAccessToken();

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, (int) (accessToken.getExpiresIn() * 0.8));
				appParameters.setNextRequestedAccessTokenTime(cal.getTime());

				logger.info("NextRequestedTimestamp is " + appParameters.getNextRequestedAccessTokenTime());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

			if (Calendar.getInstance().getTime().after(appParameters.getNextRequestedAccessTokenTime())) {

				logger.info("NextRequestedAccessTokenTime- " + appParameters.getNextRequestedAccessTokenTime()
						+ " is not null and need to create a new AccessToken in CacheManager.getAccessToken() as token has expired");

				try {
					accessToken = fetchAccessToken();
					Calendar cal = Calendar.getInstance();

					logger.info("Generated New AccessToken at " + cal.getTime());
					cal.add(Calendar.SECOND, (int) (accessToken.getExpiresIn() * 0.8));
					appParameters.setNextRequestedAccessTokenTime(cal.getTime());

				} catch (IOException e) {

					e.printStackTrace();
				}
			}

		}
		return accessToken;
	}

	private AccessToken fetchAccessToken() throws IOException {

		AccessToken accessToken = PSUtils.generateAccessToken(appParameters.getUserId(),
				appParameters.getIntegratorKey(), appParameters.getPrivatePemPath(),
				appParameters.getPublicPemPath(), appParameters.getScope(), appParameters.getTokenExpiryLimit(),
				appParameters.getProxyHost(), appParameters.getProxyPort(), appParameters.getAudience(),
				appParameters.getUrl());

		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
}