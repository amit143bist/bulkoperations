package com.docusign.batch.processor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.BulkSendListItem;
import com.docusign.batch.domain.EnvelopeBatchItem;
import com.docusign.batch.domain.EnvelopeBatchTestItem;
import com.docusign.batch.item.file.DSEnvelopeFieldExtractor;
import com.docusign.batch.item.helper.DSEnvelopeCreateAsyncHelper;
import com.docusign.exception.InvalidInputException;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;

public class DSEnvCreateAsyncAPIProcessor extends AbstractAPIProcessor
		implements ItemProcessor<EnvelopeBatchItem, EnvelopeBatchItem> {

	final static Logger logger = LogManager.getLogger(DSEnvCreateAsyncAPIProcessor.class);

	@Autowired
	private Environment env;

	@Autowired
	private PSProperties psProps;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private AppParameters appParameters;

	@Autowired
	private ProServServiceTemplate proServServiceTemplate;

	@Autowired
	private DSEnvelopeFieldExtractor dsEnvelopeFieldExtractor;

	@Autowired
	private DSEnvelopeCreateAsyncHelper dsEnvelopeCreateAsyncHelper;

	@Value("#{jobParameters}")
	private Map<String, JobParameter> jobParameters;

	private Configuration pathConfiguration = Configuration.builder()
			.options(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL).build();

	private String baseUri;

	private String fileName;

	private String delimiter;

	private String accountGuid;

	private String draftEnvelopeId;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	public String getDraftEnvelopeId() {
		return draftEnvelopeId;
	}

	public void setDraftEnvelopeId(String draftEnvelopeId) {
		this.draftEnvelopeId = draftEnvelopeId;
	}

	@Override
	public EnvelopeBatchItem process(EnvelopeBatchItem envelopeBatchItemToProcess) throws Exception {

		String jobId = jobParameters.get(AppConstants.SPRING_JOB_ID_PARAM).toString();
		logger.info("Processing Data for fileName -> " + fileName + " and JobId -> " + jobId);
		String listId = null;
		Boolean canBeSent = null;
		HttpEntity<String> uri = null;
		EnvelopeBatchItem envelopeBatchItem = null;
		try {

			AccessToken accessToken = cacheManager.getAccessToken();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
			headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

			FileBasedEnvelope fileBasedEnvelope = new FileBasedEnvelope(fileName, env, psProps, appParameters);

			listId = createBulkSendListJSON(envelopeBatchItemToProcess, headers, fileBasedEnvelope);

			logger.info("BulkSendListId is " + listId);

			String envelopeOrTemplateMsgBody = null;
			if (!StringUtils.isEmpty(fileBasedEnvelope.getTemplateOrDraftEnvelopeId())) {

				logger.info("EnvelopeOrTemplateId is " + fileBasedEnvelope.getTemplateOrDraftEnvelopeId());
				envelopeOrTemplateMsgBody = "{envelopeOrTemplateId:" + "\""
						+ fileBasedEnvelope.getTemplateOrDraftEnvelopeId() + "\"" + "}";
			} else {

				logger.info("EnvelopeOrTemplateId is " + draftEnvelopeId);
				envelopeOrTemplateMsgBody = "{envelopeOrTemplateId:" + "\"" + draftEnvelopeId + "\"" + "}";
			}
			uri = new HttpEntity<String>(envelopeOrTemplateMsgBody, headers);

			canBeSent = testBulkSendListWithEnvelopeOrTemplateId(listId, uri);

			envelopeBatchItem = sendBulkSendListWithEnvelopeOrTemplateId(listId, uri, canBeSent, 1);

			logger.info("Processing completed for " + fileName + " and JobId -> " + jobId);
		} catch (Exception exp) {

			logger.info("Exception " + exp + " occurred for " + fileName + " and JobId -> " + jobId);
			exp.printStackTrace();

			envelopeBatchItem = new EnvelopeBatchItem();
			envelopeBatchItem.setFileName(fileName);
			handleExceptionData(objectMapper, envelopeBatchItem, exp);
		}
		return envelopeBatchItem;
	}

	private EnvelopeBatchItem sendBulkSendListWithEnvelopeOrTemplateId(String listId, HttpEntity<String> uri,
			Boolean canBeSent, int counter) {

		EnvelopeBatchItem envelopeBatchItem = null;
		String sendUrl = MessageFormat.format(psProps.getBulkListSendApi(), baseUri, accountGuid, listId);
		try {

			logger.info("Inside sendBulkSendListWithEnvelopeOrTemplateId for listId " + listId + "canBeSent value is "
					+ canBeSent + " sendUrl " + sendUrl + " counter value is " + counter);
			if (null != canBeSent && canBeSent) {

				ResponseEntity<EnvelopeBatchItem> envelopeBatchItemResponseEntity = callDSAPI(proServServiceTemplate,
						appParameters, uri, sendUrl, HttpMethod.POST, EnvelopeBatchItem.class);

				if (null != envelopeBatchItemResponseEntity) {

					envelopeBatchItem = envelopeBatchItemResponseEntity.getBody();

					HttpHeaders responseHeaders = envelopeBatchItemResponseEntity.getHeaders();
					processResponseHeaders(envelopeBatchItem, responseHeaders, "SuccessCall",
							envelopeBatchItemResponseEntity.getStatusCodeValue());

					envelopeBatchItem.setSuccess(true);
					envelopeBatchItem.setTransMessage("Success");
					envelopeBatchItem.setFileName(fileName);
				}
			}
		} catch (Exception exp) {

			logger.debug("Exception caused by " + exp.getCause() + " with error message " + exp.getMessage());
			if (exp instanceof HttpClientErrorException) {

				HttpClientErrorException clientExp = (HttpClientErrorException) exp;

				logger.debug("Exception ResponseBody " + clientExp.getResponseBodyAsString());

				if (clientExp.getResponseBodyAsString().contains("errorDetails")
						&& clientExp.getResponseBodyAsString().contains("envelopes waiting")) {

					ReadContext ctx = JsonPath.using(pathConfiguration).parse(clientExp.getResponseBodyAsString());

					List<String> errorDetails = ctx.read("$.errorDetails");

					String queueError = null;

					for (String error : errorDetails) {

						if (error.contains("envelopes waiting")) {

							queueError = error;
						}
					}

					List<String> queueDetails = new ArrayList<>();
					Pattern p = Pattern.compile("\\d+");
					Matcher m = p.matcher(queueError);
					while (m.find()) {

						queueDetails.add(m.group());
					}

					Long sleepValue = psProps.getBulkBatchSleepInterval();
					if (null != queueDetails && !queueDetails.isEmpty() && queueDetails.size() >= 2) {

						logger.info("Maximum Queue size is " + queueDetails.get(0)
								+ " current envelope pending to be processed is " + queueDetails.get(1)
								+ " so sending thread to sleep for " + (sleepValue * counter) + " milliseconds");
					}

					try {

						Thread.sleep(sleepValue * counter);
						return sendBulkSendListWithEnvelopeOrTemplateId(listId, uri, canBeSent, ++counter);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} else
					throw exp;

			}
		}

		return envelopeBatchItem;
	}

	private Boolean testBulkSendListWithEnvelopeOrTemplateId(String listId, HttpEntity<String> uri) {

		logger.info("Inside testBulkSendListWithEnvelopeOrTemplateId for listId " + listId);
		String testUrl = MessageFormat.format(psProps.getBulkListTestApi(), baseUri, accountGuid, listId);

		ResponseEntity<EnvelopeBatchTestItem> envelopeBatchTestItemResponseEntity = callDSAPI(proServServiceTemplate,
				appParameters, uri, testUrl, HttpMethod.POST, EnvelopeBatchTestItem.class);

		EnvelopeBatchTestItem envelopeBatchTestItem = envelopeBatchTestItemResponseEntity.getBody();
		Boolean canBeSent = envelopeBatchTestItemResponseEntity.getBody().getCanBeSent();

		HttpHeaders responseHeaders = envelopeBatchTestItemResponseEntity.getHeaders();
		processResponseHeaders(envelopeBatchTestItem, responseHeaders, "SuccessCall",
				envelopeBatchTestItemResponseEntity.getStatusCodeValue());

		return canBeSent;
	}

	private String createBulkSendListJSON(EnvelopeBatchItem envelopeBatchItemToProcess, HttpHeaders headers,
			FileBasedEnvelope fileBasedEnvelope) {

		logger.info("Processing starting for " + fileName);

		List<ObjectNode> inlineTemplateNodeList = new ArrayList<>(envelopeBatchItemToProcess.getRowDataList().size());

		Map<String, List<Map<String, String>>> envelopeIdDataMap = null;

		List<Map<String, String>> envelopeIdDataList = null;
		if (null != fileBasedEnvelope && !StringUtils.isEmpty(fileBasedEnvelope.getTemplateOrDraftEnvelopeId())) {

			logger.info("isTemplate value is -> " + fileBasedEnvelope.isTemplate()
					+ " and templateOrDraftEnvelopeId value is -> " + fileBasedEnvelope.getTemplateOrDraftEnvelopeId()
					+ " for fileName -> " + fileName);

			envelopeIdDataMap = dsEnvelopeFieldExtractor.readEnvelopeAndPopulateMap(baseUri, accountGuid,
					fileBasedEnvelope.getTemplateOrDraftEnvelopeId(), fileBasedEnvelope.isTemplate());

			envelopeIdDataList = envelopeIdDataMap.get(fileBasedEnvelope.getTemplateOrDraftEnvelopeId());

			logger.debug("FileName-> " + fileName + " and templateOrDraftEnvelopeId-> "
					+ fileBasedEnvelope.getTemplateOrDraftEnvelopeId() + " envelopeIdDataList is -> "
					+ envelopeIdDataList);
		} else {

			envelopeIdDataMap = dsEnvelopeFieldExtractor.readEnvelopeAndPopulateMap(baseUri, accountGuid,
					draftEnvelopeId, fileBasedEnvelope.isTemplate());
			envelopeIdDataList = envelopeIdDataMap.get(draftEnvelopeId);
		}

		if (null == envelopeIdDataList || envelopeIdDataList.isEmpty()) {

			throw new InvalidInputException("Something wrong in loading envelopeIdDataList");
		}

		for (String item : envelopeBatchItemToProcess.getRowDataList()) {

			ObjectNode createInlineTemplateNode = dsEnvelopeCreateAsyncHelper
					.createInlineTemplateRequestNode(convertRowToArray(item), fileName, envelopeIdDataList);

			logger.debug("For fileName -> " + fileName + " envelopeReqBodyJSON is " + createInlineTemplateNode);

			if (null == createInlineTemplateNode) {

				logger.warn(
						"Something wrong in converting CSV row data to an element of BulkCopies for rowData " + item);
				throw new InvalidInputException(
						"Something wrong in converting CSV row data to an element of BulkCopies for rowData " + item);
			}

			inlineTemplateNodeList.add(createInlineTemplateNode);
		}

		String bulkSendListJSON = dsEnvelopeCreateAsyncHelper.createBulkSendJSON(inlineTemplateNodeList, fileName);
		logger.debug("bulkSendListJSON for fileName -> " + fileName + " is " + bulkSendListJSON);

		HttpEntity<String> uri = new HttpEntity<String>(bulkSendListJSON, headers);

		String url = MessageFormat.format(psProps.getBulkListCreateApi(), baseUri, accountGuid);

		ResponseEntity<BulkSendListItem> responseEntity = callDSAPI(proServServiceTemplate, appParameters, uri, url,
				HttpMethod.POST, BulkSendListItem.class);

		BulkSendListItem bulkSendListItem = responseEntity.getBody();
		String listId = bulkSendListItem.getListId();

		HttpHeaders responseHeaders = responseEntity.getHeaders();
		processResponseHeaders(bulkSendListItem, responseHeaders, "SuccessCall", responseEntity.getStatusCodeValue());

		return listId;
	}

	private String[] convertRowToArray(String item) {

		List<String> itemColumnList = Stream.of(item.split(",")).map(String::trim).collect(Collectors.toList());

		String[] itemColumnArray = new String[itemColumnList.size()];

		return itemColumnList.toArray(itemColumnArray);

	}

	static class FileBasedEnvelope {

		private String templateOrDraftEnvelopeId = null;
		private boolean isTemplate = false;

		public String getTemplateOrDraftEnvelopeId() {
			return templateOrDraftEnvelopeId;
		}

		public boolean isTemplate() {
			return isTemplate;
		}

		public FileBasedEnvelope(String fileName, Environment env, PSProperties psProps, AppParameters appParameters) {
			super();

			if (psProps.getFileBasedCreation()) {

				if (null != psProps.getFilePatterns() && !psProps.getFilePatterns().isEmpty()) {

					String filePatternToProcess = psProps.getFilePatterns().stream()
							.filter(filePattern -> fileName.toLowerCase().contains(filePattern.toLowerCase()))
							.findFirst().orElse(null);

					if (!StringUtils.isEmpty(filePatternToProcess)) {

						String fileToProcessPattern = env
								.getProperty("ds.account.bulksend." + filePatternToProcess + ".pattern");
						if (!StringUtils.isEmpty(fileToProcessPattern)) {

							isTemplate = AppConstants.DS_BULKSEND_FILE_PATTERN_TEMPLATE
									.equalsIgnoreCase(fileToProcessPattern);

						} else {

							throw new InvalidInputException("ds.account.bulksend." + filePatternToProcess
									+ ".pattern is empty or null in application.properties for fileName -> "
									+ fileName);
						}

						if (!isTemplate) {

							if (null != psProps.getDraftEnvelopeCreationFolder()
									&& null != appParameters.getFileNameDraftEnvelopeMap()
									&& !appParameters.getFileNameDraftEnvelopeMap().isEmpty()) {

								templateOrDraftEnvelopeId = appParameters.getFileNameDraftEnvelopeMap()
										.get(filePatternToProcess);
							} else {

								throw new InvalidInputException(
										"FileNameDraftEnvelopeMap is empty, please check correct properties and json file as no draftenvelopeid created for fileName -> "
												+ fileName + " for filePatternToProcess -> " + filePatternToProcess);
							}

						} else {

							templateOrDraftEnvelopeId = env
									.getProperty("ds.account.bulksend." + filePatternToProcess + ".templateid");
							if (StringUtils.isEmpty(templateOrDraftEnvelopeId)) {

								throw new InvalidInputException("ds.account.bulksend." + filePatternToProcess
										+ ".templateid  is empty or null in application.properties for fileName -> "
										+ fileName);
							}
						}

					} else {

						throw new InvalidInputException("fileName -> " + fileName
								+ " does not match any ds.account.bulksend.filePattern property in application.properties");
					}

				} else {

					throw new InvalidInputException(
							"ds.account.bulksend.filePattern property in application.properties cannot be blank or null for fileName -> "
									+ fileName);
				}
			} else {

				logger.info(" ************************* Processing will NOT BE DONE at filebased level for fileName -> "
						+ fileName + " ************************* ");

				isTemplate = psProps.getUseTemplate();
			}
		}

	}

}