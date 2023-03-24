package com.docusign.batch.listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.docusign.batch.domain.AppConstants;
import com.docusign.proserv.application.utils.PSProperties;

public class CustomItemProcessStepListener implements StepExecutionListener {

	final static Logger logger = LogManager.getLogger(CustomItemProcessStepListener.class);

	@Autowired
	PSProperties psProps;

	@Value("#{jobParameters}")
	private Map<String, JobParameter> jobParameters;

	private String inboundDir;
	private String outboundSuccessDir;
	private String outputDirectoryPath;

	public void setInboundDir(String inboundDir) {
		this.inboundDir = inboundDir;
	}

	public void setOutputDirectoryPath(String outputDirectoryPath) {
		this.outputDirectoryPath = outputDirectoryPath;
	}

	public void setOutboundSuccessDir(String outboundSuccessDir) {
		this.outboundSuccessDir = outboundSuccessDir;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		logger.debug("fileResource afterStep " + stepExecution.getExecutionContext().get("fileResource"));
		logger.debug("fileName afterStep " + stepExecution.getExecutionContext().get("fileName"));
		logger.debug("summary afterStep " + stepExecution.getSummary());
		logger.debug("Status afterStep " + stepExecution.getStatus());
		logger.debug("ReadCount afterStep " + stepExecution.getReadCount());
		logger.debug("WriteCount afterStep " + stepExecution.getWriteCount());
		logger.debug("StepName afterStep " + stepExecution.getStepName());

		String jobId = jobParameters.get(AppConstants.SPRING_JOB_ID_PARAM).toString();
		String fileName = (String) stepExecution.getExecutionContext().get("fileName");

		if (null != stepExecution.getFailureExceptions() && (BatchStatus.FAILED == stepExecution.getStatus()
				|| stepExecution.getReadCount() != stepExecution.getWriteCount())) {

			if (null != stepExecution.getJobExecution().getExecutionContext().get("failureCount")) {

				int failureCount = (int) stepExecution.getJobExecution().getExecutionContext().get("failureCount");
				stepExecution.getJobExecution().getExecutionContext().put("failureCount", failureCount + 1);

			} else {
				stepExecution.getJobExecution().getExecutionContext().put("failureCount", 1);
			}

			if (null != stepExecution.getJobExecution().getExecutionContext().get("failureFileNames")) {

				String failureFileNames = (String) stepExecution.getJobExecution().getExecutionContext()
						.get("failureFileNames");
				stepExecution.getJobExecution().getExecutionContext().put("failureFileNames",
						failureFileNames + "," + stepExecution.getExecutionContext().get("fileName"));
			} else {
				stepExecution.getJobExecution().getExecutionContext().put("failureFileNames",
						stepExecution.getExecutionContext().get("fileName"));
			}

			for (Throwable throwable : stepExecution.getFailureExceptions()) {

				logger.error("Exception occurred for JobId -> " + jobId + " with Cause -> " + throwable.getCause()
						+ " Message " + throwable.getMessage());
				throwable.printStackTrace();
			}
		} else {

			if (null != stepExecution.getJobExecution().getExecutionContext().get("successCount")) {

				int successCount = (int) stepExecution.getJobExecution().getExecutionContext().get("successCount");
				stepExecution.getJobExecution().getExecutionContext().put("successCount", successCount + 1);

			} else {
				stepExecution.getJobExecution().getExecutionContext().put("successCount", 1);
			}

			if (!StringUtils.isEmpty(outboundSuccessDir)) {

				File dir = new File(outboundSuccessDir);
				if (dir.isDirectory()) {
					File[] files = dir.listFiles();

					if (null != psProps.getSleepTimeBetweenFilesEnabled() && psProps.getSleepTimeBetweenFilesEnabled()
							&& null != psProps.getSuccessCSVThresholdCount() && null != files
							&& (files.length % psProps.getSuccessCSVThresholdCount() == 0)) {

						try {

							logger.info("In JobId -> " + jobId + " Thread put to sleep after "
									+ psProps.getSuccessCSVThresholdCount() + " csvs push for "
									+ psProps.getSleepTimeBetweenFiles() + " milliseconds");
							Thread.sleep(psProps.getSleepTimeBetweenFiles());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				}
			}

			try {

				logger.info("In JobId -> " + jobId + " Copying " + fileName + " from " + inboundDir + " to "
						+ outputDirectoryPath + File.separator + AppConstants.COMPLETED_FOLDER_NAME + " folder");

				Files.copy(Paths.get(inboundDir + File.separator + fileName), Paths.get(outputDirectoryPath
						+ File.separator + AppConstants.COMPLETED_FOLDER_NAME + File.separator + fileName));

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return null;
	}
}