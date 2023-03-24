package com.docusign.proserv.application.commands;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.docusign.proserv.application.domain.DSEnvironment;
import com.docusign.proserv.application.domain.LoginAccount;
import com.docusign.proserv.application.domain.LoginResponse;
import com.docusign.proserv.application.domain.TokenResponse;
import com.docusign.proserv.application.domain.UserResponse;
import com.docusign.proserv.application.domain.UsersResponse;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

@Component
@Deprecated
public class PSCommands extends AbstractPSCommands implements CommandMarker {

	final static Logger logger = LogManager.getLogger(PSCommands.class);

	private static final String NA1_SITE = "www";
	private static final String NA2_SITE = "na2";
	private static final String NA3_SITE = "na3";
	private static final String EU_SITE = "eu";

	@Autowired
	public PSProperties psProps;

	@CliAvailabilityIndicator({ "ps_token, ps_process, ps_token_demo, ps_process_demo" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "ps_token", help = "GET DocuSign PROD OAuth2 Token - STEP 1")
	public String token(
			@CliOption(key = { "na1User" }, mandatory = false, help = "NA1 DS Username") final String na1UserName,
			@CliOption(key = { "na1Pwd" }, mandatory = false, help = "NA1 DS password") final String na1UserPassword,
			@CliOption(key = { "na2User" }, mandatory = false, help = "NA2 DS Username") final String na2UserName,
			@CliOption(key = { "na2Pwd" }, mandatory = false, help = "NA2 DS password") final String na2UserPassword,
			@CliOption(key = { "na3User" }, mandatory = false, help = "NA3 DS Username") final String na3UserName,
			@CliOption(key = { "na3Pwd" }, mandatory = false, help = "NA3 DS password") final String na3UserPassword,
			@CliOption(key = { "euUser" }, mandatory = false, help = "EU DS Username") final String euUserName,
			@CliOption(key = { "euPwd" }, mandatory = false, help = "EU DS password") final String euUserPassword,
			@CliOption(key = { "env" }, mandatory = true, help = "DS Environment") final String environment,
			@CliOption(key = {
					"integratorKey" }, mandatory = true, help = "DS Integrator Key") final String integratorKey,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort) {

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {
			return psProps.getEnvErrorMessage();
		}

		if (!(DSEnvironment.PROD.name().equals(environment.toUpperCase()))) {
			return psProps.getEnvironmentErrorMessage(environment);
		}

		logger.info("******************************** ps_token Job started ********************************");

		StringBuilder strBuilder = new StringBuilder();

		if (!StringUtils.isEmpty(na1UserName) && !StringUtils.isEmpty(na1UserPassword)) {

			String na1Token = fetchToken(na1UserName, na1UserPassword, NA1_SITE, integratorKey, proxyHost, proxyPort);
			strBuilder.append("\n");
			strBuilder.append("NA1Token: " + na1Token);
		}

		if (!StringUtils.isEmpty(na2UserName) && !StringUtils.isEmpty(na2UserPassword)) {

			String na2Token = fetchToken(na2UserName, na2UserPassword, NA2_SITE, integratorKey, proxyHost, proxyPort);
			strBuilder.append("\n");
			strBuilder.append("NA2Token: " + na2Token);
		}

		if (!StringUtils.isEmpty(na3UserName) && !StringUtils.isEmpty(na3UserPassword)) {

			String na3Token = fetchToken(na3UserName, na3UserPassword, NA3_SITE, integratorKey, proxyHost, proxyPort);
			strBuilder.append("\n");
			strBuilder.append("NA3Token: " + na3Token);
		}

		if (!StringUtils.isEmpty(euUserName) && !StringUtils.isEmpty(euUserPassword)) {

			String euToken = fetchToken(euUserName, euUserPassword, EU_SITE, integratorKey, proxyHost, proxyPort);
			strBuilder.append("\n");
			strBuilder.append("EUToken: " + euToken);
		}

		String tokenString = strBuilder.toString();

		logger.info(tokenString);

		logger.info(
				"******************************** ps_token Job finished successfully ********************************");

		return tokenString;
	}

	@CliCommand(value = "ps_token_demo", help = "GET DocuSign DEMO OAuth2 Token - STEP 1")
	public String tokenDemo(
			@CliOption(key = { "demoUser" }, mandatory = true, help = "DS Username") final String username,
			@CliOption(key = { "demoPwd" }, mandatory = true, help = "DS password") final String password,
			@CliOption(key = { "env" }, mandatory = true, help = "DS Environment") final String environment,
			@CliOption(key = {
					"integratorKey" }, mandatory = true, help = "DS Integrator Key") final String integratorKey,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort) {

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {
			return psProps.getEnvErrorMessage();
		}

		if (!(DSEnvironment.DEMO.name().equals(environment.toUpperCase()))) {
			return psProps.getEnvironmentErrorMessage(environment);
		}

		logger.info("******************************** ps_token_demo Job started ********************************");

		String demoToken = fetchToken(username, password, environment, integratorKey, proxyHost, proxyPort);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n");
		strBuilder.append("DemoToken: " + demoToken);

		String tokenString = strBuilder.toString();

		logger.info(tokenString);

		logger.info(
				"******************************** ps_token_demo Job finished successfully ********************************");

		return tokenString;

	}

	/**
	 * @param username
	 * @param password
	 * @param environment
	 * @param integratorKey
	 * @param proxyHost
	 * @param proxyPort
	 * @return token
	 */
	private String fetchToken(String username, String password, String environment, String integratorKey,
			String proxyHost, String proxyPort) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "password");
		map.add("client_id", integratorKey);
		map.add("username", username);
		map.add("password", password);
		map.add("scope", "api");

		try {

			RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);

			String url = MessageFormat.format(psProps.getDSOAuthTokenAPI(), environment);
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			ResponseEntity<TokenResponse> response = restTemplate.postForEntity(url, request, TokenResponse.class);
			return response.getBody().getAccess_token();
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			return MessageFormat.format(psProps.getDSAPIError(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return MessageFormat.format(psProps.getPSUnknownError(), e.getMessage());
		}
	}

	@CliCommand(value = "ps_process", help = "Generate PROD Report CSVs - STEP 2")
	public String process(
			@CliOption(key = { "na1Token" }, mandatory = false, help = "NA1 OAuth token") final String na1Token,
			@CliOption(key = { "na2Token" }, mandatory = false, help = "NA2 OAuth token") final String na2Token,
			@CliOption(key = { "na3Token" }, mandatory = false, help = "NA3 OAuth token") final String na3Token,
			@CliOption(key = { "euToken" }, mandatory = false, help = "EU OAuth token") final String euToken,
			@CliOption(key = {
					"excludedAccountGUIDs" }, mandatory = false, help = "DS Excluded Accounts GUIDs") final String excludedAccountGUIDs,
			@CliOption(key = { "env" }, mandatory = true, help = "DS environment") final String environment,
			@CliOption(key = { "csvpath" }, mandatory = false, help = "Output Directory Path") String path,
			@CliOption(key = { "narid" }, mandatory = true, help = "NAR Id") String narId,
			@CliOption(key = {
					"groupNamecontains" }, mandatory = false, help = "DS group names contains this value") String groupNameContains,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort)
			throws IOException {

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {
			return psProps.getEnvErrorMessage();
		}

		if (!(DSEnvironment.PROD.name().equals(environment.toUpperCase()))) {
			return psProps.getEnvironmentErrorMessage(environment);
		}

		try {

			logger.info("******************************** ps_process Job started ********************************");

			List<UserResponse> userResponseInnerList = new ArrayList<UserResponse>();

			List<String> excludedAccountGuidsLst = null;
			if (!StringUtils.isEmpty(excludedAccountGUIDs) && excludedAccountGUIDs.length() > 0) {

				excludedAccountGuidsLst = PSUtils.splitStringtoList(excludedAccountGUIDs, COMMA_DELIMITER);
			}

			logger.info("AccountGUIDs elements in excludedAccountGuidsLst are " + excludedAccountGuidsLst);

			if (!StringUtils.isEmpty(na1Token)) {

				getUsersList(NA1_SITE, na1Token, excludedAccountGuidsLst, userResponseInnerList, proxyHost, proxyPort);
			}

			if (!StringUtils.isEmpty(na2Token)) {

				getUsersList(NA2_SITE, na2Token, excludedAccountGuidsLst, userResponseInnerList, proxyHost, proxyPort);
			}

			if (!StringUtils.isEmpty(na3Token)) {

				getUsersList(NA3_SITE, na3Token, excludedAccountGuidsLst, userResponseInnerList, proxyHost, proxyPort);
			}

			if (!StringUtils.isEmpty(euToken)) {

				getUsersList(EU_SITE, euToken, excludedAccountGuidsLst, userResponseInnerList, proxyHost, proxyPort);
			}

			Map<String, String> commandParams = new Hashtable<String, String>();
			commandParams.put("path", path);
			commandParams.put("narId", narId);
			if (!StringUtils.isEmpty(groupNameContains)) {

				commandParams.put("groupNameContains", groupNameContains);
			}

			generateCSVReports(userResponseInnerList, commandParams, null, null, null, null);

			logger.info(
					"******************************** ps_process Job finished successfully ********************************");

			return MessageFormat.format(psProps.getProcessOutputMessage(), path);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			return MessageFormat.format(psProps.getDSAPIError(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return MessageFormat.format(psProps.getPSUnknownError(), e.getMessage());
		}
	}

	@CliCommand(value = "ps_process_demo", help = "Generate DEMO Report CSVs - STEP 2")
	public String processDemo(
			@CliOption(key = { "demoToken" }, mandatory = true, help = "oAuth token") final String demoToken,
			@CliOption(key = {
					"excludedAccountGUIDs" }, mandatory = false, help = "DS Excluded Accounts GUIDs") final String excludedAccountGUIDs,
			@CliOption(key = { "env" }, mandatory = true, help = "DS environment") final String environment,
			@CliOption(key = { "csvpath" }, mandatory = false, help = "Output Directory Path") String path,
			@CliOption(key = { "narid" }, mandatory = true, help = "NAR Id") String narId,
			@CliOption(key = {
					"groupNamecontains" }, mandatory = false, help = "DS group names contains this value") String groupNameContains,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort)
			throws IOException {

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {
			return psProps.getEnvErrorMessage();
		}

		if (!(DSEnvironment.DEMO.name().equals(environment.toUpperCase()))) {
			return psProps.getEnvironmentErrorMessage(environment);
		}

		try {
			logger.info(
					"******************************** ps_process_demo Job started ********************************");

			List<UserResponse> userResponseInnerList = new ArrayList<UserResponse>();

			List<String> excludedAccountGuidsLst = null;
			if (!StringUtils.isEmpty(excludedAccountGUIDs) && excludedAccountGUIDs.length() > 0) {

				excludedAccountGuidsLst = PSUtils.splitStringtoList(excludedAccountGUIDs, COMMA_DELIMITER);
			}

			logger.info("AccountGUIDs elements in excludedAccountGuidsLst are " + excludedAccountGuidsLst);

			getUsersList(environment, demoToken, excludedAccountGuidsLst, userResponseInnerList, proxyHost, proxyPort);

			Map<String, String> commandParams = new Hashtable<String, String>();
			commandParams.put("path", path);
			commandParams.put("narId", narId);

			if (!StringUtils.isEmpty(groupNameContains)) {

				commandParams.put("groupNameContains", groupNameContains);
			}

			generateCSVReports(userResponseInnerList, commandParams, null, null, null, null);

			logger.info(
					"******************************** ps_process_demo Job finished successfully ********************************");

			return MessageFormat.format(psProps.getProcessOutputMessage(), path);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			return MessageFormat.format(psProps.getDSAPIError(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return MessageFormat.format(psProps.getPSUnknownError(), e.getMessage());
		}

	}

	/**
	 * @param environment
	 * @param token
	 * @return list of LoginAccount
	 */
	private List<LoginAccount> fetchLoginAccounts(String environment, String token, String proxyHost,
			String proxyPort) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", TOKEN_TYPE + token);
		headers.add("Accept", "application/json");

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);
		String url = MessageFormat.format(psProps.getAuthLoginApi(), environment);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		ResponseEntity<LoginResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				LoginResponse.class);

		return response.getBody().getLoginAccounts();

	}

	/**
	 * @param environment
	 * @param token
	 * @param excludedAccountGuidsLst
	 * @param userResponseInnerList
	 * @return list of UserResponse
	 * @throws URISyntaxException
	 */
	private List<UserResponse> getUsersList(String environment, String token, List<String> excludedAccountGuidsLst,
			List<UserResponse> userResponseInnerList, String proxyHost, String proxyPort) throws URISyntaxException {

		List<LoginAccount> loginAccounts = fetchLoginAccounts(environment, token, proxyHost, proxyPort);

		for (LoginAccount loginAccount : loginAccounts) {

			String accountGuid = loginAccount.getAccountIdGuid();

			if ((null == excludedAccountGuidsLst) || (!excludedAccountGuidsLst.contains(accountGuid))) {

				String baseUrl = loginAccount.getBaseUrl();

				URI uri = new URI(baseUrl);
				String domain = uri.getHost();

				logger.info("In PSCommands.getUsersList() BaseUrl is " + baseUrl + " domain is " + domain
						+ " environment passed is " + environment + " accountGuid is " + accountGuid);

				if (environment.equalsIgnoreCase(domain)) {

					UsersResponse usersResponse = processData(token, loginAccount.getAccountId(), environment,
							proxyHost, proxyPort);

					List<UserResponse> userResponseList = usersResponse.getUsers();

					Iterator<UserResponse> userResponseIterator = userResponseList.iterator();
					while (userResponseIterator.hasNext()) {
						UserResponse userResponse = userResponseIterator.next();
						userResponse.setAccountId(loginAccount.getAccountId());
					}
					userResponseInnerList.addAll(userResponseList);

					logger.info("Total no. of fetched users from accountGuid " + accountGuid + " are "
							+ userResponseList.size());
				} else {
					logger.info("Ignoring " + accountGuid + " as its domain is not same as " + environment);
				}

			} else {
				logger.info(accountGuid
						+ " is present in the excludedAccountGuidsLst, so no users fetched from this account");
			}
		}

		return userResponseInnerList;
	}

	/**
	 * @param token
	 * @param accountId
	 * @param environment
	 * @param path
	 * @param narId
	 * @param groupNameContains
	 * @return usersResponse
	 */
	private UsersResponse processData(String token, String accountId, String environment, String proxyHost,
			String proxyPort) {

		UsersResponse usersResponse = getUsersFromDocuSignAPI(token, accountId, environment, proxyHost, proxyPort);

		return usersResponse;
	}

	private UsersResponse getUsersFromDocuSignAPI(final String token, final String accountId, final String environment,
			final String proxyHost, final String proxyPort) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", TOKEN_TYPE + token);
		headers.setContentLength(200);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);
		restTemplate.setMessageConverters(getMessageConverters());

		String url = MessageFormat.format(psProps.getDSUsersAPI(), environment, accountId);

		ResponseEntity<UsersResponse> usersResponse = restTemplate.exchange(url, HttpMethod.GET, entity,
				UsersResponse.class);

		return usersResponse.getBody();
	}

}
