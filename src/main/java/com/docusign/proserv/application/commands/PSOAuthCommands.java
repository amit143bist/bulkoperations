package com.docusign.proserv.application.commands;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.docusign.jwt.domain.AccessToken;
import com.docusign.jwt.domain.Account;
import com.docusign.jwt.domain.LoginUserInfo;
import com.docusign.proserv.application.domain.CustomFieldsResponse;
import com.docusign.proserv.application.domain.DSEnvironment;
import com.docusign.proserv.application.domain.ListCustomField;
import com.docusign.proserv.application.domain.UserResponse;
import com.docusign.proserv.application.domain.UsersResponse;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

@Configuration
@Component
@Deprecated
public class PSOAuthCommands extends AbstractPSCommands implements CommandMarker {

	final static Logger logger = LogManager.getLogger(PSOAuthCommands.class);

	@Resource
	private Environment env;

//	@Bean
	public PropertiesFactoryBean profileProperties() {

		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(new FileSystemResource(env.resolvePlaceholders("${propertyPath}")));

		return pfb;
	}

	@Autowired
	public PSProperties psProps;

//	@Autowired
	PropertiesFactoryBean profileProperties;

	@CliAvailabilityIndicator({ "ps_oauth_process" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "ps_oauth_process", help = "Generate PROD Report CSVs")
	public int process(@CliOption(key = { "userIds" }, mandatory = true, help = "UserIds") final String userIds,
			@CliOption(key = { "integratorKey" }, mandatory = true, help = "Integrator Key") final String integratorKey,
			@CliOption(key = { "secretKey" }, mandatory = true, help = "Secret Key") final String secretKey,
			@CliOption(key = { "scope" }, mandatory = true, help = "Scope") final String scope,
			@CliOption(key = {
					"tokenExpiryLimit" }, mandatory = true, help = "Access Token Expiry Limit") final String tokenExpiryLimit,
			@CliOption(key = {
					"privatePemPath" }, mandatory = true, help = "Private Pem Path") final String privatePemPath,
			@CliOption(key = {
					"publicPemPath" }, mandatory = true, help = "Public Pem Path") final String publicPemPath,
			@CliOption(key = {
					"excludedAccountGUIDs" }, mandatory = false, help = "DS Excluded Accounts GUIDs") final String excludedAccountGUIDs,
			@CliOption(key = { "env" }, mandatory = true, help = "DS environment") final String environment,
			@CliOption(key = { "csvpath" }, mandatory = true, help = "Output Directory Path") String path,
			@CliOption(key = { "narid" }, mandatory = true, help = "NAR Id") String narId,
			@CliOption(key = {
					"groupNamecontains" }, mandatory = false, help = "DS group names contains this value") String groupNameContains,
			@CliOption(key = {
					"systemUserEmails" }, mandatory = false, help = "DS System UserEmails") final String systemUserEmails,
			@CliOption(key = {
					"accountIdEnvelopeCustomFieldName" }, mandatory = true, help = "DS AccountId Envelope Custom Field Name") final String accountIdEnvelopeCustomFieldName,
			@CliOption(key = {
					"mandatoryPropKeys" }, mandatory = true, help = "DS Mandatory Prop Keys") final String mandatoryPropKeys,
			@CliOption(key = {
					"includeInvalidUsersFlag" }, mandatory = true, help = "DS Mandatory IncludeInvalidUsersFlag") final String includeInvalidUsersFlag,
			@CliOption(key = {
					"csvFieldNames" }, mandatory = true, help = "DS Mandatory CSV Field Names") final String csvFieldNames,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort)
			throws IOException {

		logger.info("******************************** ps_oauth_process Job started ********************************");

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {

			logger.error(psProps.getEnvErrorMessage());
			return -1;
		}

		try {

			printInputParameters(userIds, integratorKey, secretKey, scope, privatePemPath, publicPemPath,
					excludedAccountGUIDs, environment, path, narId, groupNameContains, systemUserEmails,
					accountIdEnvelopeCustomFieldName, mandatoryPropKeys, proxyHost, proxyPort);

			Properties props = profileProperties.getObject();

			List<String> mandatoryPropKeysList = PSUtils.splitStringtoList(mandatoryPropKeys, COMMA_DELIMITER);

			if (!PSUtils.validatePropertyFile(mandatoryPropKeysList, props)) {

				String propertyFilePath = env.resolvePlaceholders("${propertyPath}");

				logger.error(MessageFormat.format(psProps.getReqKeysMissingErrorMessage(), propertyFilePath));
				return -1;
			}

			List<String> excludedAccountGuidsLst = null;
			if (!StringUtils.isEmpty(excludedAccountGUIDs) && excludedAccountGUIDs.length() > 0) {

				excludedAccountGuidsLst = PSUtils.splitStringtoList(excludedAccountGUIDs, COMMA_DELIMITER);
			}

			List<String> systemUserEmailsList = null;
			if (!StringUtils.isEmpty(systemUserEmails) && systemUserEmails.length() > 0) {

				systemUserEmailsList = PSUtils.splitStringtoList(systemUserEmails, COMMA_DELIMITER);
			}

			List<UserResponse> userResponseInnerList = createUserInnerListForCsv(userIds, integratorKey, secretKey,
					scope, tokenExpiryLimit, privatePemPath, publicPemPath, environment,
					accountIdEnvelopeCustomFieldName, proxyHost, proxyPort, excludedAccountGuidsLst);

			if (null != userResponseInnerList && !userResponseInnerList.isEmpty()) {

				checkAndProceedWritingCsv(path, narId, groupNameContains, props, systemUserEmailsList,
						userResponseInnerList, includeInvalidUsersFlag, csvFieldNames);
			} else {

				errorFlag = true;
				logger.error("No users in the userResponseInnerList, so nothing to write in csv");
			}

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

		return showSuccessFailureMessages(path, "ps_oauth_process");
	}

	/**
	 * @param userIds
	 * @param integratorKey
	 * @param secretKey
	 * @param scope
	 * @param privatePemPath
	 * @param publicPemPath
	 * @param excludedAccountGUIDs
	 * @param environment
	 * @param path
	 * @param narId
	 * @param groupNameContains
	 * @param systemUserEmails
	 * @param accountIdEnvelopeCustomFieldName
	 * @param mandatoryPropKeys
	 * @param proxyHost
	 * @param proxyPort
	 */
	private void printInputParameters(final String userIds, final String integratorKey, final String secretKey,
			final String scope, final String privatePemPath, final String publicPemPath,
			final String excludedAccountGUIDs, final String environment, String path, String narId,
			String groupNameContains, final String systemUserEmails, final String accountIdEnvelopeCustomFieldName,
			final String mandatoryPropKeys, final String proxyHost, final String proxyPort) {

		if (logger.isDebugEnabled()) {

			logger.debug("*********************** Input Parameters are ***********************");

			logger.debug("Input userIds: " + userIds);
			logger.debug("Input integratorKey: " + integratorKey);
			logger.debug("Input secretKey: " + secretKey);
			logger.debug("Input scope: " + scope);
			logger.debug("Input privatePemPath: " + privatePemPath);
			logger.debug("Input publicPemPath: " + publicPemPath);
			logger.debug("Input excludedAccountGUIDs: " + excludedAccountGUIDs);
			logger.debug("Input environment: " + environment);
			logger.debug("Input csvpath: " + path);
			logger.debug("Input narId: " + narId);
			logger.debug("Input groupNameContains: " + groupNameContains);
			logger.debug("Input systemUserEmails: " + systemUserEmails);
			logger.debug("Input accountIdEnvelopeCustomFieldName: " + accountIdEnvelopeCustomFieldName);
			logger.debug("Input mandatoryPropKeys: " + mandatoryPropKeys);
			logger.debug("Input proxyHost: " + proxyHost);
			logger.debug("Input proxyPort: " + proxyPort);
		}
	}

	/**
	 * @param path
	 * @param narId
	 * @param groupNameContains
	 * @param props
	 * @param systemUserEmailsList
	 * @param userResponseInnerList
	 * @throws IOException
	 */
	private void checkAndProceedWritingCsv(String path, String narId, String groupNameContains, Properties props,
			List<String> systemUserEmailsList, List<UserResponse> userResponseInnerList, String includeInvalidUsersFlag,
			String csvFieldNames) throws IOException {

		Map<String, String> commandParams = new Hashtable<String, String>();

		commandParams.put("path", path);
		commandParams.put("narId", narId);

		if (!StringUtils.isEmpty(groupNameContains)) {

			commandParams.put("groupNameContains", groupNameContains);
		}

		if (errorFlag) {

			logger.error(
					"******************************** Errors occurred in processing, so not writing to CSVs ********************************");

		} else {

			generateCSVReports(userResponseInnerList, commandParams, props, systemUserEmailsList,
					includeInvalidUsersFlag, csvFieldNames);
		}
	}

	/**
	 * @param userIds
	 * @param integratorKey
	 * @param secretKey
	 * @param scope
	 * @param privatePemPath
	 * @param publicPemPath
	 * @param environment
	 * @param accountIdEnvelopeCustomFieldName
	 * @param proxyHost
	 * @param proxyPort
	 * @param excludedAccountGuidsLst
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private List<UserResponse> createUserInnerListForCsv(final String userIds, final String integratorKey,
			final String secretKey, final String scope, final String tokenExpiryLimit, final String privatePemPath,
			final String publicPemPath, final String environment, final String accountIdEnvelopeCustomFieldName,
			final String proxyHost, final String proxyPort, List<String> excludedAccountGuidsLst)
			throws IOException, URISyntaxException {

		List<String> userIdList = PSUtils.splitStringtoList(userIds, COMMA_DELIMITER);

		StringBuilder strBuilder = new StringBuilder();
		List<String> processedAccounts = new ArrayList<String>();
		List<UserResponse> userResponseInnerList = new ArrayList<UserResponse>();

		String audience = getAudienceForJWT(environment);
		String oAuthUrl = MessageFormat.format(psProps.getDSOAuth2TokenAPI(), audience);

		for (String userId : userIdList) {

			AccessToken userToken = createOAuthToken(userId, integratorKey, privatePemPath, publicPemPath, scope,
					tokenExpiryLimit, proxyHost, proxyPort, audience, oAuthUrl);
			strBuilder.append(NEW_LINE);
			strBuilder.append(userId + "'s Token: " + userToken.getAccessToken());

			getUsersList(environment, userToken, excludedAccountGuidsLst, userResponseInnerList, proxyHost, proxyPort,
					processedAccounts, accountIdEnvelopeCustomFieldName);

		}

		String tokenString = strBuilder.toString();

		logger.debug(tokenString);

		return userResponseInnerList;
	}

	/**
	 * @param environment
	 * @param accessToken
	 * @param excludedAccountGuidsLst
	 * @param userResponseInnerList
	 * @param proxyHost
	 * @param proxyPort
	 * @param processedAccounts
	 * @return list of UserResponse
	 * @throws URISyntaxException
	 */
	private List<UserResponse> getUsersList(String environment, AccessToken accessToken,
			List<String> excludedAccountGuidsLst, List<UserResponse> userResponseInnerList, String proxyHost,
			String proxyPort, List<String> processedAccounts, String accountIdEnvelopeCustomFieldName)
			throws URISyntaxException {

		List<Account> userAccounts = fetchUserAccounts(environment, accessToken, proxyHost, proxyPort);

		for (Account account : userAccounts) {

			String accountGuid = account.getAccountId();

			if ((null == excludedAccountGuidsLst) || (!excludedAccountGuidsLst.contains(accountGuid))) {

				if ((null != processedAccounts && !processedAccounts.isEmpty()
						&& !processedAccounts.contains(accountGuid))
						|| (null != processedAccounts && processedAccounts.isEmpty())) {

					String baseUri = account.getBaseUri();
					String friendlyAccountId = getExternalAccountId(accessToken, proxyHost, proxyPort,
							accountIdEnvelopeCustomFieldName, accountGuid, baseUri);

					UsersResponse usersResponse = getUsersFromDocuSignAPI(accessToken, accountGuid, proxyHost,
							proxyPort, baseUri);

					processUserResponseInnerList(userResponseInnerList, processedAccounts, accountGuid,
							friendlyAccountId, usersResponse);
				} else {

					logger.info(accountGuid + " is already processed so ignoring this one");
				}

			} else {
				logger.info(accountGuid
						+ " is present in the excludedAccountGuidsLst, so no users fetched from this account");
			}
		}

		return userResponseInnerList;
	}

	/**
	 * @param userResponseInnerList
	 * @param processedAccounts
	 * @param accountGuid
	 * @param friendlyAccountId
	 * @param usersResponse
	 */
	private void processUserResponseInnerList(List<UserResponse> userResponseInnerList, List<String> processedAccounts,
			String accountGuid, String friendlyAccountId, UsersResponse usersResponse) {

		List<UserResponse> userResponseList = usersResponse.getUsers();

		Iterator<UserResponse> userResponseIterator = userResponseList.iterator();
		while (userResponseIterator.hasNext()) {
			UserResponse userResponse = userResponseIterator.next();

			if (!StringUtils.isEmpty(friendlyAccountId)) {
				userResponse.setAccountId(friendlyAccountId);
			} else {
				userResponse.setAccountId(accountGuid);
			}
		}
		userResponseInnerList.addAll(userResponseList);

		processedAccounts.add(accountGuid);

		logger.info("Total no. of fetched users from accountGuid " + accountGuid + " are " + userResponseList.size());
	}

	/**
	 * @param accessToken
	 * @param proxyHost
	 * @param proxyPort
	 * @param accountIdEnvelopeCustomFieldName
	 * @param accountGuid
	 * @param baseUri
	 * @return
	 */
	private String getExternalAccountId(AccessToken accessToken, String proxyHost, String proxyPort,
			String accountIdEnvelopeCustomFieldName, String accountGuid, String baseUri) {

		String friendlyAccountId = null;

		if (!StringUtils.isEmpty(accountIdEnvelopeCustomFieldName)) {

			friendlyAccountId = findFriendlyAccountId(accessToken, accountIdEnvelopeCustomFieldName, baseUri,
					accountGuid, proxyHost, proxyPort);

			if (StringUtils.isEmpty(friendlyAccountId)) {

				errorFlag = true;
				logger.error(MessageFormat.format(psProps.getFriendlyAccountIdMissingErrorMessage(),
						accountIdEnvelopeCustomFieldName, accountGuid));
			} else {
				logger.info(
						accountIdEnvelopeCustomFieldName + " value for " + accountGuid + " is " + friendlyAccountId);
			}
		}
		return friendlyAccountId;
	}

	/**
	 * @param accessToken
	 * @param accountIdEnvelopeCustomFieldName
	 * @param baseUri
	 * @param accountGuid
	 * @param proxyHost
	 * @param proxyPort
	 * @return friendlyAccountId
	 * @throws Exception
	 */
	private String findFriendlyAccountId(AccessToken accessToken, String accountIdEnvelopeCustomFieldName,
			String baseUri, String accountGuid, String proxyHost, String proxyPort) {

		String friendlyAccountId = null;

		ResponseEntity<CustomFieldsResponse> customFieldsResponseEntity = fetchAccountEnvelopeCustomFields(accessToken,
				baseUri, accountGuid, proxyHost, proxyPort);

		if (null != customFieldsResponseEntity && null != customFieldsResponseEntity.getBody()) {

			CustomFieldsResponse customFieldsResponse = customFieldsResponseEntity.getBody();

			List<ListCustomField> customFieldList = customFieldsResponse.getListCustomFields();

			if (null != customFieldList && !customFieldList.isEmpty()) {

				for (ListCustomField listCustomField : customFieldList) {

					if (accountIdEnvelopeCustomFieldName.equalsIgnoreCase(listCustomField.getName())) {

						if (null == listCustomField.getListItems() || listCustomField.getListItems().isEmpty()) {

							errorFlag = true;

							logger.error(MessageFormat.format(psProps.getFriendlyAccountIdListEmptyErrorMessage(),
									accountIdEnvelopeCustomFieldName, accountGuid));
						} else {

							if (listCustomField.getListItems().size() > 1) {

								errorFlag = true;

								logger.error(MessageFormat.format(psProps.getFriendlyAccountIdListInvalidErrorMessage(),
										accountIdEnvelopeCustomFieldName, accountGuid));
							} else {

								friendlyAccountId = listCustomField.getListItems().get(0);
								break;
							}

						}

					}
				}
			}
		}

		return friendlyAccountId;
	}

	/**
	 * @param accessToken
	 * @param baseUri
	 * @param accountGuid
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 */
	private ResponseEntity<CustomFieldsResponse> fetchAccountEnvelopeCustomFields(AccessToken accessToken,
			String baseUri, String accountGuid, String proxyHost, String proxyPort) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.setContentLength(200);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);
		restTemplate.setMessageConverters(getMessageConverters());

		String url = MessageFormat.format(psProps.getAccountCustomFieldsApi(), baseUri, accountGuid);

		logger.debug("In fetchAccountEnvelopeCustomFields(), url is " + url + " entity " + entity);
		ResponseEntity<CustomFieldsResponse> customFieldsResponseEntity = restTemplate.exchange(url, HttpMethod.GET,
				entity, CustomFieldsResponse.class);
		return customFieldsResponseEntity;
	}

	/**
	 * @param accessToken
	 * @param accountGuidId
	 * @param proxyHost
	 * @param proxyPort
	 * @param baseUri
	 * @return usersResponse
	 */
	private UsersResponse getUsersFromDocuSignAPI(final AccessToken accessToken, final String accountGuid,
			final String proxyHost, final String proxyPort, String baseUri) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.setContentLength(200);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);
		restTemplate.setMessageConverters(getMessageConverters());

		String url = MessageFormat.format(psProps.getUserListApi(), baseUri, accountGuid);

		logger.debug("In getUsersFromDocuSignAPI(), url is " + url + " entity " + entity);
		ResponseEntity<UsersResponse> usersResponse = restTemplate.exchange(url, HttpMethod.GET, entity,
				UsersResponse.class);

		return usersResponse.getBody();
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

		logger.debug("In fetchUserAccounts(), url is " + url + " entity " + entity);
		ResponseEntity<LoginUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				LoginUserInfo.class);

		return response.getBody().getAccounts();

	}

}