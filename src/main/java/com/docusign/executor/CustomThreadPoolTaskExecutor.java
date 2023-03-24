/**
 * 
 */
package com.docusign.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.docusign.batch.domain.AppParameters;

/**
 * @author Amit.Bist
 *
 */
public class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4960048118595819094L;
	
	private AppParameters appParameters;

	public AppParameters getAppParameters() {
		return appParameters;
	}

	public void setAppParameters(AppParameters appParameters) {
		this.appParameters = appParameters;
	}

}