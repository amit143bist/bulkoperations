package com.docusign.proserv.application.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.EnvelopeItem;
import com.docusign.exception.InvalidInputException;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.jwt.domain.Account;
import com.docusign.jwt.domain.LoginUserInfo;
import com.docusign.proserv.application.domain.BulkOperations;
import com.docusign.proserv.application.domain.DSEnvironment;
import com.docusign.proserv.application.utils.FileUtils;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

import jline.internal.Log;

@Configuration
@Component
public class PSBulkOpsCommands extends AbstractPSCommands implements CommandMarker {

	final static Logger logger = LogManager.getLogger(PSBulkOpsCommands.class);

	@Resource
	private Environment env;

	@Autowired
	public PSProperties psProps;

	@CliAvailabilityIndicator({ "ps_bulkops_process" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "ps_bulkops_process", help = "Generate Report CSVs")
	public int process(@CliOption(key = { "userIds" }, mandatory = true, help = "UserIds") final String userIds,
			@CliOption(key = { "integratorKey" }, mandatory = true, help = "Integrator Key") final String integratorKey,
			@CliOption(key = { "scope" }, mandatory = true, help = "Scope") final String scope,
			@CliOption(key = {
					"tokenExpiryLimit" }, mandatory = true, help = "Access Token Expiry Limit") final String tokenExpiryLimit,
			@CliOption(key = {
					"privatePemPath" }, mandatory = true, help = "Private Pem Path") final String privatePemPath,
			@CliOption(key = {
					"publicPemPath" }, mandatory = true, help = "Public Pem Path") final String publicPemPath,
			@CliOption(key = { "env" }, mandatory = true, help = "DS environment") final String environment,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort,
			@CliOption(key = {
					"operationNames" }, mandatory = true, help = "DS Bulk Operation Names") final String dsBulkOperationNames,
			@CliOption(key = {
					"inDirpath" }, mandatory = true, help = "Input Directory Path") String inputDirectoryPath,
			@CliOption(key = {
					"outDirpath" }, mandatory = true, help = "Output Directory Path") String outputDirectoryPath,
			@CliOption(key = {
					"appMaxThreadPoolSize" }, mandatory = true, help = "App Max Thread Pool Size") String appMaxThreadPoolSize,
			@CliOption(key = {
					"appCoreThreadPoolSize" }, mandatory = true, help = "App Core Thread Pool Size") String appCoreThreadPoolSize,
			@CliOption(key = {
					"appReminderAllowed" }, mandatory = false, help = "App Reminder Allowed") String appReminderAllowed,
			@CliOption(key = {
					"appExpirationAllowed" }, mandatory = false, help = "App Expiration Allowed") String appExpirationAllowed,
			@CliOption(key = {
					"validAccountGuids" }, mandatory = true, help = "Valid Account Guids") String validAccountGuids)
			throws IOException {

		logger.info("******************************** ps_bulkops_process Job started ********************************");

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {

			logger.error(psProps.getEnvErrorMessage());
			return -1;
		}

		try {

			printInputParameters(userIds, integratorKey, scope, tokenExpiryLimit, privatePemPath, publicPemPath,
					environment, proxyHost, proxyPort, dsBulkOperationNames, inputDirectoryPath, outputDirectoryPath,
					appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed, appExpirationAllowed,
					validAccountGuids);

			performBulkOperationForEachUser(userIds, integratorKey, scope, tokenExpiryLimit, privatePemPath,
					publicPemPath, environment, proxyHost, proxyPort, dsBulkOperationNames, inputDirectoryPath,
					outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
					appExpirationAllowed, validAccountGuids);

		} catch (HttpClientErrorException e) {

			logger.error("Exception with Response Body " + e.getResponseBodyAsString());
			logger.error(MessageFormat.format(psProps.getDSAPIError(), e.getMessage()));

			e.printStackTrace();
			return -1;

		} catch (Exception e) {

			logger.error(MessageFormat.format(psProps.getPSUnknownError(), e.getMessage()));

			e.printStackTrace();
			return -1;
		}

		return showSuccessFailureMessages(outputDirectoryPath, "ps_oauth_process");
	}

	private void printInputParameters(String userIds, String integratorKey, String scope, String tokenExpiryLimit,
			String privatePemPath, String publicPemPath, String environment, String proxyHost, String proxyPort,
			String dsBulkOperationNames, String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, String appReminderAllowed,
			String appExpirationAllowed, String validAccountGuids) {

		if (logger.isDebugEnabled()) {

			logger.debug("*********************** Input Parameters are ***********************");

			logger.debug("Input userIds: " + userIds);
			logger.debug("Input integratorKey: " + integratorKey);
			logger.debug("Input scope: " + scope);
			logger.debug("Input tokenExpiryLimit:" + tokenExpiryLimit);
			logger.debug("Input privatePemPath: " + privatePemPath);
			logger.debug("Input publicPemPath: " + publicPemPath);
			logger.debug("Input environment: " + environment);
			logger.debug("Input proxyHost: " + proxyHost);
			logger.debug("Input proxyPort: " + proxyPort);
			logger.debug("Input dsBulkOperationNames: " + dsBulkOperationNames);
			logger.debug("Input inputDirectoryPath: " + inputDirectoryPath);
			logger.debug("Input outputDirectoryPath: " + outputDirectoryPath);
			logger.debug("Input appMaxThreadPoolSize: " + appMaxThreadPoolSize);
			logger.debug("Input appCoreThreadPoolSize: " + appCoreThreadPoolSize);
			logger.debug("Input appReminderAllowed: " + appReminderAllowed);
			logger.debug("Input appExpirationAllowed: " + appExpirationAllowed);
			logger.debug("Input validAccountGuids: " + validAccountGuids);
		}
	}

	private void performBulkOperationForEachUser(final String userIds, final String integratorKey, final String scope,
			final String tokenExpiryLimit, final String privatePemPath, final String publicPemPath,
			final String environment, final String proxyHost, final String proxyPort, String dsBulkOperationNames,
			String inputDirectoryPath, String outputDirectoryPath, String appMaxThreadPoolSize,
			String appCoreThreadPoolSize, String appReminderAllowed, String appExpirationAllowed,
			String validAccountGuids) throws IOException {

		List<String> userIdList = PSUtils.splitStringtoList(userIds, COMMA_DELIMITER);
		List<String> dsBulkOperationList = PSUtils.splitStringtoList(dsBulkOperationNames, COMMA_DELIMITER);
		List<String> validAccountGuidList = PSUtils.splitStringtoList(validAccountGuids, COMMA_DELIMITER);

		logger.info("validAccountGuidList -> " + validAccountGuidList);

		StringBuilder strBuilder = new StringBuilder();

		String audience = getAudienceForJWT(environment);
		String oAuthUrl = MessageFormat.format(psProps.getDSOAuth2TokenAPI(), audience);

		if (psProps.getSplitCSV()) {

			File inputDirFile = new File(inputDirectoryPath);
			File[] inputFiles = inputDirFile.listFiles();

			if (null != inputFiles && inputFiles.length == 0) {

				throw new InvalidInputException("No files in the input directory " + inputDirFile + " to process");
			}

			if (null != inputFiles && inputFiles.length > 1) {

				throw new InvalidInputException("More than one file to split in " + inputDirFile);
			}

			FileUtils.splitFile(inputFiles[0].getAbsolutePath(), psProps.getSplitCSVLinesLimit());
		}

		for (String userId : userIdList) {

			AccessToken userToken = createOAuthToken(userId, integratorKey, privatePemPath, publicPemPath, scope,
					tokenExpiryLimit, proxyHost, proxyPort, audience, oAuthUrl);
			strBuilder.append(NEW_LINE);
			strBuilder.append(userId + "'s Token: " + userToken.getAccessToken());

			List<Account> userAccounts = fetchUserAccounts(environment, userToken, proxyHost, proxyPort);

			for (Account account : userAccounts) {

				if (null != validAccountGuidList && !validAccountGuidList.isEmpty()
						&& validAccountGuidList.contains(account.getAccountId())) {

					for (String dsOperationName : dsBulkOperationList) {

						if (!EnumUtils.isValidEnum(BulkOperations.class, dsOperationName.toUpperCase())) {

							logger.error(psProps.getOperationErrorMessage());
							throw new InvalidInputException(dsOperationName);
						}

						switch (EnumUtils.getEnum(BulkOperations.class, dsOperationName)) {

						case NOTIFICATIONCHANGES:
							processBulkNotificationChanges(inputDirectoryPath, outputDirectoryPath,
									appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
									appExpirationAllowed, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath, publicPemPath, scope,
									tokenExpiryLimit, audience, oAuthUrl);
							break;
						case ENVELOPEUPDATES:

							processBulkEnvelopeUpdates(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize,
									appCoreThreadPoolSize, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath, publicPemPath, scope,
									tokenExpiryLimit, audience, oAuthUrl);
							break;

						case ENVELOPECOPY:

							processBulkEnvelopeCopy(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize,
									appCoreThreadPoolSize, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath, publicPemPath, scope,
									tokenExpiryLimit, audience, oAuthUrl);
							break;

						case ENVELOPERESUME:

							processBulkEnvelopeResume(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize,
									appCoreThreadPoolSize, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath, publicPemPath, scope,
									tokenExpiryLimit, audience, oAuthUrl);
							break;

						case ENVELOPECREATE:

							processBulkEnvelopeCreate(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize,
									appCoreThreadPoolSize, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath, publicPemPath, scope,
									tokenExpiryLimit, audience, oAuthUrl);
							break;

						case ENVELOPECREATEASYNC:

							processBulkEnvelopeCreateAsync(inputDirectoryPath, outputDirectoryPath,
									appMaxThreadPoolSize, appCoreThreadPoolSize, proxyHost, proxyPort,
									account.getBaseUri(), account.getAccountId(), userId, integratorKey, privatePemPath,
									publicPemPath, scope, tokenExpiryLimit, audience, oAuthUrl);
							break;

						case ENVELOPETEMPLATECREATEUSINGFILE:

							createDraftEnvelope(privatePemPath, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userToken);
							break;

						default:
							logger.error("No Bulk Operation Job to run, check the DS Operation Name");

						}
					}
				}
			}

		}

		logger.debug(strBuilder.toString());

	}

	/**
	 * @param environment
	 * @param token
	 * @param proxyHost
	 * @param proxyPort
	 * @return list of LoginAccount
	 */
	private List<Account> fetchUserAccounts(String environment, AccessToken accessToken, String proxyHost,
			String proxyPort) {

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);

		String host = getAudienceForJWT(environment);

		String url = MessageFormat.format(psProps.getAuthUserInfo(), host);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		logger.debug("In PSBulkOpsCommands.fetchUserAccounts(), Resturl is " + url + " entity " + entity);
		ResponseEntity<LoginUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				LoginUserInfo.class);

		return response.getBody().getAccounts();

	}

	private void processBulkNotificationChanges(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, String appReminderAllowed,
			String appExpirationAllowed, final String proxyHost, final String proxyPort, String baseUri,
			String accountGuid, final String userId, final String integratorKey, final String privatePemPath,
			final String publicPemPath, final String scope, final String tokenExpiryLimit, String audience,
			String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize,
				appReminderAllowed, appExpirationAllowed, proxyHost, proxyPort, baseUri, accountGuid, userId,
				integratorKey, privatePemPath, publicPemPath, scope, tokenExpiryLimit, audience, url,
				AppConstants.SPRING_CONTEXT_FILE_PATH, AppConstants.STRING_JOB_REPORT_FILE_PATH,
				AppConstants.SPRING_JOB_LAUNCHER, AppConstants.SPRING_REPORT_JOB_NAME,
				BulkOperations.NOTIFICATIONCHANGES.toString());
	}

	private void processBulkEnvelopeUpdates(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, null, null,
				proxyHost, proxyPort, baseUri, accountGuid, userId, integratorKey, privatePemPath, publicPemPath, scope,
				tokenExpiryLimit, audience, url, AppConstants.SPRING_CONTEXT_FILE_PATH,
				AppConstants.SPRING_JOB_ENV_UPDATE_FILE_PATH, AppConstants.SPRING_JOB_LAUNCHER,
				AppConstants.SPRING_REPORT_ENV_UPDATE_JOB_NAME, BulkOperations.ENVELOPEUPDATES.toString());

	}

	private void processBulkEnvelopeCopy(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, null, null,
				proxyHost, proxyPort, baseUri, accountGuid, userId, integratorKey, privatePemPath, publicPemPath, scope,
				tokenExpiryLimit, audience, url, AppConstants.SPRING_CONTEXT_FILE_PATH,
				AppConstants.SPRING_JOB_ENV_COPY_FILE_PATH, AppConstants.SPRING_JOB_LAUNCHER,
				AppConstants.SPRING_REPORT_ENV_COPY_JOB_NAME, BulkOperations.ENVELOPECOPY.toString());

	}

	private void processBulkEnvelopeResume(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, null, null,
				proxyHost, proxyPort, baseUri, accountGuid, userId, integratorKey, privatePemPath, publicPemPath, scope,
				tokenExpiryLimit, audience, url, AppConstants.SPRING_CONTEXT_FILE_PATH,
				AppConstants.SPRING_JOB_ENV_RESUME_FILE_PATH, AppConstants.SPRING_JOB_LAUNCHER,
				AppConstants.SPRING_REPORT_ENV_RESUME_JOB_NAME, BulkOperations.ENVELOPERESUME.toString());

	}

	private void processBulkEnvelopeCreate(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, null, null,
				proxyHost, proxyPort, baseUri, accountGuid, userId, integratorKey, privatePemPath, publicPemPath, scope,
				tokenExpiryLimit, audience, url, AppConstants.SPRING_CONTEXT_FILE_PATH,
				AppConstants.SPRING_JOB_ENV_CREATE_FILE_PATH, AppConstants.SPRING_JOB_LAUNCHER,
				AppConstants.SPRING_REPORT_ENV_CREATE_JOB_NAME, BulkOperations.ENVELOPECREATE.toString());

	}

	private void processBulkEnvelopeCreateAsync(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, null, null,
				proxyHost, proxyPort, baseUri, accountGuid, userId, integratorKey, privatePemPath, publicPemPath, scope,
				tokenExpiryLimit, audience, url, AppConstants.SPRING_CONTEXT_FILE_PATH,
				AppConstants.SPRING_JOB_ASYNC_ENV_CREATE_FILE_PATH, AppConstants.SPRING_JOB_LAUNCHER,
				AppConstants.SPRING_REPORT_ENV_CREATE_ASYNC_JOB_NAME, BulkOperations.ENVELOPECREATEASYNC.toString());

	}

	/**
	 * @param inputDirectoryPath
	 * @param outputDirectoryPath
	 * @param appMaxThreadPoolSize
	 * @param appCoreThreadPoolSize
	 * @param appReminderAllowed
	 * @param appExpirationAllowed
	 * @param proxyHost
	 * @param proxyPort
	 * @param baseUri
	 * @param accountGuid
	 * @param userId
	 * @param integratorKey
	 * @param privatePemPath
	 * @param publicPemPath
	 * @param scope
	 * @param tokenExpiryLimit
	 * @param audience
	 * @param url
	 * @param springContextFilePath
	 * @param springJobFilePath
	 * @param springLauncherName
	 * @param springJobName
	 */
	private void invokeBatchJob(String inputDirectoryPath, String outputDirectoryPath, String appMaxThreadPoolSize,
			String appCoreThreadPoolSize, String appReminderAllowed, String appExpirationAllowed,
			final String proxyHost, final String proxyPort, String baseUri, String accountGuid, final String userId,
			final String integratorKey, final String privatePemPath, final String publicPemPath, final String scope,
			final String tokenExpiryLimit, String audience, String url, String springContextFilePath,
			String springJobFilePath, String springLauncherName, String springJobName, String operationName) {

		ApplicationContext context = null;
		File inputDirFile = new File(inputDirectoryPath);
		File outputDirFile = new File(outputDirectoryPath);
		try {

			if (PSUtils.isInputDirValid(inputDirFile) && null != outputDirFile && outputDirFile.isDirectory()) {

				String[] springConfig = { springContextFilePath, springJobFilePath };

				context = new ClassPathXmlApplicationContext(springConfig);

				JobLauncher jobLauncher = (JobLauncher) context.getBean(springLauncherName);
				Job job = (Job) context.getBean(springJobName);

				Map<String, JobParameter> parametersMap = new LinkedHashMap<String, JobParameter>();

				Date jobStartDate = Calendar.getInstance().getTime();

				logger.info("JobStartDate for " + operationName + " in PSBulkOpsCommands.invokeBatchJob()- "
						+ jobStartDate);

				File[] inputFiles = inputDirFile.listFiles();

				if (null != inputFiles && inputFiles.length == 0) {

					closeAppContext(context);
					throw new InvalidInputException("No files in the input directory " + inputDirFile + " to process");
				}

				String draftEnvelopeId = null;
				Map<String, String> fileNameDraftEnvelopeMap = null;

				if (AppConstants.SPRING_REPORT_ENV_CREATE_ASYNC_JOB_NAME.equalsIgnoreCase(springJobName)) {

					AccessToken userToken = createOAuthToken(userId, integratorKey, privatePemPath, publicPemPath,
							scope, tokenExpiryLimit, proxyHost, proxyPort, audience, url);

					logger.info("FileBasedCreation flag value -> " + psProps.getFileBasedCreation()
							+ " DraftEnvelopeCreationFolder value -> " + psProps.getDraftEnvelopeCreationFolder());
					if (psProps.getFileBasedCreation()) {

						if (!StringUtils.isEmpty(psProps.getDraftEnvelopeCreationFolder())) {

							logger.info(
									"#################### FileBased Creation is based on draft envelope ####################");
							fileNameDraftEnvelopeMap = createDraftEnvelopesForFolderJSONFiles(
									psProps.getDraftEnvelopeCreationFolder(), proxyHost, proxyPort, baseUri,
									accountGuid, userToken);
						} else {

							logger.info(
									"#################### FileBased Creation is based on template ####################");
						}
					} else {

						logger.info("#################### Non-FileBased Creation ####################");
						if (psProps.getUseTemplate()) {

							if (StringUtils.isEmpty(psProps.getBulkSendTemplateId())) {

								closeAppContext(context);
								throw new InvalidInputException(
										"BulkSendTemplateId is null or empty, please check ds.account.bulksend.template.id property in application.properties");
							} else {

								logger.info(
										"#################### Non-FileBased Creation based on template ####################");
								draftEnvelopeId = psProps.getBulkSendTemplateId();
							}

						} else {

							logger.info(
									"#################### Non-FileBased Creation based on draft envelope ####################");
							draftEnvelopeId = createDraftEnvelope(privatePemPath, proxyHost, proxyPort, baseUri,
									accountGuid, userToken);
						}
					}
				}

				loadAppParameters(context, appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
						appExpirationAllowed, proxyHost, proxyPort, userId, integratorKey, privatePemPath,
						publicPemPath, scope, tokenExpiryLimit, audience, url, operationName, fileNameDraftEnvelopeMap);

				DateFormat format = new SimpleDateFormat(AppConstants.FILE_NAME_DATE_PATTERN);
				parametersMap.put(AppConstants.JOB_START_TIME_PARAM_NAME,
						new JobParameter(format.format(jobStartDate)));
				parametersMap.put(AppConstants.JOB_INPUT_DIRECTORY_PATH_NAME, new JobParameter(inputDirectoryPath));
				parametersMap.put(AppConstants.JOB_OUTPUT_DIRECTORY_PATH_NAME, new JobParameter(outputDirectoryPath));
				parametersMap.put(AppConstants.JOB_BASEURI_VALUE, new JobParameter(baseUri));
				parametersMap.put(AppConstants.JOB_ACCOUNDGUID_VALUE, new JobParameter(accountGuid));
				parametersMap.put(AppConstants.JOB_COMMIT_INTERVAL_VALUE,
						new JobParameter(psProps.getCommitInterval()));

				if (!StringUtils.isEmpty(draftEnvelopeId)) {

					parametersMap.put(AppConstants.JOB_DRAFT_ENVELOPEID, new JobParameter(draftEnvelopeId));
				}

				String totalInputFilesCount = String.valueOf(inputFiles.length);
				parametersMap.put(AppConstants.TOTAL_FILES_COUNT, new JobParameter(totalInputFilesCount));

				String jobId = UUID.randomUUID().toString();
				parametersMap.put(AppConstants.SPRING_JOB_ID_PARAM, new JobParameter(jobId));

				PSUtils.createDirectory(outputDirectoryPath + File.separator + AppConstants.COMPLETED_FOLDER_NAME);
				if (AppConstants.SPRING_REPORT_ENV_CREATE_ASYNC_JOB_NAME.equalsIgnoreCase(springJobName)) {

					PSUtils.createDirectory(outputDirectoryPath + File.separator + AppConstants.SUCCESS_FOLDER_NAME);
					PSUtils.createDirectory(outputDirectoryPath + File.separator + AppConstants.FAIL_FOLDER_NAME);

					parametersMap.put(AppConstants.JOB_OUTPUT_SUCCESS_DIRECTORY_PATH_NAME,
							new JobParameter(outputDirFile + File.separator + AppConstants.SUCCESS_FOLDER_NAME));
				}

				JobParameters jobParameters = new JobParameters(parametersMap);

				logger.info(" ------------ About to start job with jobId -> " + jobId
						+ " in PSBulkOpsCommands.invokeBatchJob() ------------ " + job);

				JobExecution execution = jobLauncher.run(job, jobParameters);

				checkAndPrintSuccessFailureCount(outputDirectoryPath, springJobName, inputFiles, totalInputFilesCount,
						execution, inputDirectoryPath, jobId);

				logger.info(" ------------ Exit Status for jobId -> " + jobId
						+ " in PSBulkOpsCommands.invokeBatchJob() ------------  " + execution.getStatus());
				List<Throwable> failureExceptions = execution.getAllFailureExceptions();

				if (null != failureExceptions && !failureExceptions.isEmpty()) {
					failureExceptions.forEach(throwable -> {

						logger.error("Error in JobId -> " + jobId + " with Cause " + throwable.getCause() + " Message "
								+ throwable.getMessage());
						throwable.printStackTrace();
					});
				}
			} else {
				logger.error(inputDirectoryPath
						+ " is not a directory or no file exists in the directory in PSBulkOpsCommands.invokeBatchJob()");
			}

		} catch (JobExecutionAlreadyRunningException e) {
			logger.error("JobExecutionAlreadyRunningException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (JobRestartException e) {
			logger.error("JobRestartException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error("JobInstanceAlreadyCompleteException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			logger.error("JobParametersInvalidException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} finally {
			closeAppContext(context);
		}
	}

	private void closeAppContext(ApplicationContext context) {

		if (null != context) {
			((ClassPathXmlApplicationContext) context).close();
		}
	}

	private String createDraftEnvelope(String envelopeJSONPath, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, AccessToken userToken) throws IOException {

		StringBuilder contentBuilder = new StringBuilder();

		Path parentDirectory = Paths.get(envelopeJSONPath).getParent();

		if (StringUtils.isEmpty(psProps.getEnvelopeTemplateFile())) {

			throw new InvalidInputException("DraftEnvelope JSON file not present in the " + parentDirectory.toString());
		}

		try (Stream<String> stream = Files.lines(
				Paths.get(parentDirectory + File.separator + psProps.getEnvelopeTemplateFile()),
				StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		String jsonReqBody = contentBuilder.toString();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, userToken.getTokenType() + " " + userToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> uri = new HttpEntity<String>(jsonReqBody, headers);

		String createEnvelopeUrl = MessageFormat.format(psProps.getEnvelopesCreateApi(), baseUri, accountGuid);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);

		ResponseEntity<EnvelopeItem> envelopeItemResponseEntity = restTemplate.exchange(createEnvelopeUrl,
				HttpMethod.POST, uri, EnvelopeItem.class);

		logger.info("Draft EnvelopeId created is -> " + envelopeItemResponseEntity.getBody().getEnvelopeId());

		return envelopeItemResponseEntity.getBody().getEnvelopeId();

	}

	// list all files from this path
	public static List<Path> findByFileExtension(Path path, String fileExtension) throws IOException {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}

		List<Path> result;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(Files::isRegularFile) // is a file
					.filter(p -> p.getFileName().toString().endsWith(fileExtension)).collect(Collectors.toList());
		}
		return result;

	}

	private Map<String, String> createDraftEnvelopesForFolderJSONFiles(String draftEnvelopeJSONFolderPath,
			final String proxyHost, final String proxyPort, String baseUri, String accountGuid, AccessToken userToken)
			throws IOException {

		Map<String, String> fileNameDraftEnvelopeMap = new HashMap<String, String>();
		Path parentEnvelopeJSONDirectory = Paths.get(draftEnvelopeJSONFolderPath).getParent();

		try {

			List<Path> paths = findByFileExtension(parentEnvelopeJSONDirectory, ".json");

			Log.info("paths-> {}", paths);
			if (null == paths) {

				throw new InvalidInputException("Either directory -> " + parentEnvelopeJSONDirectory
						+ " is empty or directory has files with not .json file extension");
			}

			paths.forEach(pathName -> {

				String filePatternToProcess = psProps.getFilePatterns().stream().filter(filePattern -> pathName
						.getFileName().toString().toLowerCase().contains(filePattern.toLowerCase())).findFirst()
						.orElse(null);

				if (null == filePatternToProcess) {

					throw new InvalidInputException("fileName -> " + pathName.getFileName().toString()
							+ " does not match any ds.account.bulksend.filePattern property in application.properties");
				}

			});

			paths.forEach(pathName -> {

				StringBuilder contentBuilder = new StringBuilder();

				String fileNameOnly = pathName.getFileName().toString();
				try (Stream<String> stream = Files.lines(pathName, StandardCharsets.UTF_8)) {
					stream.forEach(s -> contentBuilder.append(s).append("\n"));
				} catch (IOException e) {
					e.printStackTrace();
				}

				String jsonReqBody = contentBuilder.toString();

				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.AUTHORIZATION, userToken.getTokenType() + " " + userToken.getAccessToken());
				headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

				HttpEntity<String> uri = new HttpEntity<String>(jsonReqBody, headers);

				String createEnvelopeUrl = MessageFormat.format(psProps.getEnvelopesCreateApi(), baseUri, accountGuid);

				RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);

				ResponseEntity<EnvelopeItem> envelopeItemResponseEntity = restTemplate.exchange(createEnvelopeUrl,
						HttpMethod.POST, uri, EnvelopeItem.class);

				logger.info("Draft EnvelopeId created is -> " + envelopeItemResponseEntity.getBody().getEnvelopeId());

				fileNameDraftEnvelopeMap.put(fileNameOnly, envelopeItemResponseEntity.getBody().getEnvelopeId());
			});

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		return fileNameDraftEnvelopeMap;

	}

	private void checkAndPrintSuccessFailureCount(String outputDirectoryPath, String springJobName, File[] inputFiles,
			String totalInputFilesCount, JobExecution execution, String inputDirectoryPath, String jobId)
			throws IOException {

		if (AppConstants.SPRING_REPORT_ENV_CREATE_ASYNC_JOB_NAME.equalsIgnoreCase(springJobName)) {

			int successFilesCount = 0;
			int failFilesCount = 0;
			StringBuilder failFilesBuilder = new StringBuilder();

			File successDir = new File(outputDirectoryPath + File.separator + AppConstants.SUCCESS_FOLDER_NAME);

			if (null != successDir) {

				successFilesCount = successDir.listFiles().length;
			}

			File failDir = new File(outputDirectoryPath + File.separator + AppConstants.FAIL_FOLDER_NAME);

			if (null != failDir) {

				failFilesCount = failDir.listFiles().length;

				for (File failFile : failDir.listFiles()) {

					failFilesBuilder.append(failFile.getName());
					failFilesBuilder.append(",");
				}
			}

			logger.info("In PSBulkOpsCommands.invokeBatchJob() for jobId -> " + jobId + " totalFileCount- "
					+ totalInputFilesCount + " successCount- " + successFilesCount + " failureCount- "
					+ failFilesCount);

			if (failFilesCount > 0 && null != failFilesBuilder && failFilesBuilder.length() > 0) {

				logger.info("For jobId " + jobId + " failureFileNames- "
						+ failFilesBuilder.substring(0, failFilesBuilder.length() - 1));
			}

		} else {

			logger.info("In PSBulkOpsCommands.invokeBatchJob() for jobId -> " + jobId + " totalFileCount- "
					+ totalInputFilesCount + " successCount- " + execution.getExecutionContext().get("successCount")
					+ " failureCount- " + execution.getExecutionContext().get("failureCount") + " failureFileNames- "
					+ execution.getExecutionContext().get("failureFileNames"));
		}

		logger.info("Deleting all files in jobId -> " + jobId + " from " + inputDirectoryPath);

		File completedDirFile = new File(outputDirectoryPath + File.separator + AppConstants.COMPLETED_FOLDER_NAME);

		List<String> completedFilesList = Arrays.asList(completedDirFile.list());

		for (File file : inputFiles) {

			if (completedFilesList.contains(file.getName())) {

				Files.delete(Paths.get(file.getAbsolutePath()));
			}
		}
	}

	private void loadAppParameters(ApplicationContext context, String appMaxThreadPoolSize,
			String appCoreThreadPoolSize, String appReminderAllowed, String appExpirationAllowed,
			final String proxyHost, final String proxyPort, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url, String operationName, Map<String, String> fileNameDraftEnvelopeMap) {

		Integer maxThreadPoolSize = Integer.parseInt(appMaxThreadPoolSize);
		Integer coreThreadPoolSize = Integer.parseInt(appCoreThreadPoolSize);

		Boolean reminderAllowed = Boolean.parseBoolean(appReminderAllowed);
		Boolean expirationAllowed = Boolean.parseBoolean(appExpirationAllowed);

		logger.debug("Reminder Allowed in MainApp.loadAppParameters() " + reminderAllowed + " Expiration Allowed- "
				+ expirationAllowed);

		AppParameters appParameters = (AppParameters) context.getBean(AppConstants.APP_PARAMETERS_BEAN_NAME);
		appParameters.setCoreThreadPoolSize(coreThreadPoolSize);
		appParameters.setMaxThreadPoolSize(maxThreadPoolSize);
		appParameters.setReminderAllowed(reminderAllowed);
		appParameters.setExpirationAllowed(expirationAllowed);
		appParameters.setProxyHost(proxyHost);
		appParameters.setProxyPort(proxyPort);
		appParameters.setUserId(userId);
		appParameters.setIntegratorKey(integratorKey);
		appParameters.setPrivatePemPath(privatePemPath);
		appParameters.setPublicPemPath(publicPemPath);
		appParameters.setScope(scope);
		appParameters.setTokenExpiryLimit(tokenExpiryLimit);
		appParameters.setAudience(audience);
		appParameters.setUrl(url);
		appParameters.setOperationName(operationName);
		appParameters.setFileNameDraftEnvelopeMap(fileNameDraftEnvelopeMap);
	}
}