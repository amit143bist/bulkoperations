package com.docusign.proserv.application.commands;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.shell.support.util.StringUtils;
import org.supercsv.util.ReflectionUtils;

import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.csv.CsvFileWriter;
import com.docusign.proserv.application.csv.Entitlement;
import com.docusign.proserv.application.csv.UserAccount;
import com.docusign.proserv.application.domain.Group;
import com.docusign.proserv.application.domain.UserResponse;
import com.docusign.proserv.application.domain.UserSettings;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

public abstract class AbstractPSCommands {

	final static Logger logger = LogManager.getLogger(AbstractPSCommands.class);

	protected static final String TOKEN_TYPE = "Bearer ";
	protected static final String COMMA_DELIMITER = ",";
	protected static final String NEW_LINE = "\n";
	private static final String KEY_SUFFIX = "_Required";

	@Autowired
	public PSProperties psProps;

	protected boolean errorFlag = false;

	protected List<HttpMessageConverter<?>> getMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJackson2HttpMessageConverter());
		return converters;
	}

	/**
	 * @param path
	 * @return
	 */
	protected int showSuccessFailureMessages(String path, String jobName) {
		if (errorFlag) {

			logger.error(
					"******************************** Please check logs for error messages ********************************");
			logger.error("******************************** " + jobName
					+ " Job abruptly interrupted ********************************");
			return -1;
		} else {

			logger.info(MessageFormat.format(psProps.getProcessOutputMessage(), path));
			logger.info("******************************** " + jobName
					+ " Job finished successfully ********************************");
			return 0;
		}
	}

	/**
	 * @param userId
	 * @param integratorKey
	 * @param secretKey
	 * @param privatePemPath
	 * @param publicPemPath
	 * @param scope
	 * @param tokenExpiryLimit
	 * @param proxyHost
	 * @param proxyPort
	 * @param audience
	 * @param oAuthUrl
	 * @return
	 * @throws IOException
	 */
	protected AccessToken createOAuthToken(final String userId, final String integratorKey, final String privatePemPath,
			final String publicPemPath, final String scope, final String tokenExpiryLimit,
			final String proxyHost, final String proxyPort, String audience, String oAuthUrl) throws IOException {

		return PSUtils.generateAccessToken(userId, integratorKey, privatePemPath, publicPemPath, scope,
				tokenExpiryLimit, proxyHost, proxyPort, audience, oAuthUrl);

	}

	/**
	 * @param environment
	 * @return
	 */
	protected String getAudienceForJWT(final String environment) {

		return psProps.getEnvironmentHost(environment);
	}

	/**
	 * @param userResponses
	 * @param commandParams
	 * @param props
	 * @param systemUserEmailsList
	 * @throws IOException
	 */
	protected void generateCSVReports(List<UserResponse> userResponses, Map<String, String> commandParams,
			Properties props, List<String> systemUserEmailsList, String includeInvalidUsersFlag, String csvFieldNames)
			throws IOException {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String today = format.format(new Date());
		File outputDir = getOutputDirectory(commandParams.get("path"));

		String owner = commandParams.get("narId");

		String groupNameContains = commandParams.get("groupNameContains");
		List<String> groupNameContainsLst = null;

		if (!StringUtils.isEmpty(groupNameContains)) {

			groupNameContainsLst = PSUtils.splitStringtoList(groupNameContains, COMMA_DELIMITER);
		}

		List<String> csvFieldNamesList = null;
		if (!StringUtils.isEmpty(csvFieldNames)) {

			csvFieldNamesList = PSUtils.splitStringtoList(csvFieldNames, COMMA_DELIMITER);
		}

		Set<String> permissionProfileSet = new HashSet<String>(5);
		List<UserAccount> userAccounts = new ArrayList<UserAccount>();

		for (int i = 0; i < userResponses.size(); i++) {

			UserResponse userResponse = userResponses.get(i);
			List<UserSettings> list = userResponse.getUserSettings();

			// Called only if
			if (i < 1 && (null == props)) {

				createUserDetailsEntitlements(list, owner, outputDir, today);
			}

			createAndUpdateUserAccounts(props, systemUserEmailsList, owner, groupNameContainsLst, permissionProfileSet,
					userAccounts, userResponse, list, includeInvalidUsersFlag, csvFieldNamesList);

		}

		if (null != props && !permissionProfileSet.isEmpty()) {

			createPermissionProfileDescCsv(props, today, outputDir, owner, permissionProfileSet);
		}

		createUserAccountCsv(today, outputDir, owner, userAccounts);
	}

	/**
	 * @param today
	 * @param outputDir
	 * @param owner
	 * @param userAccounts
	 * @throws IOException
	 */
	private void createUserAccountCsv(String today, File outputDir, String owner, List<UserAccount> userAccounts)
			throws IOException {
		String userAccountsFile = outputDir + MessageFormat.format(psProps.getUserAccountFilename(), owner, today);

		if (null != userAccounts && !userAccounts.isEmpty()) {

			logger.info("Total Active users pending to be written on a CSV are " + userAccounts.size());
		}

		CsvFileWriter.writeUserAccountCsv(userAccountsFile, userAccounts);
	}

	/**
	 * @param props
	 * @param systemUserEmailsList
	 * @param owner
	 * @param groupNameContainsLst
	 * @param permissionProfileSet
	 * @param userAccounts
	 * @param userResponse
	 * @param list
	 * @param includeInvalidUsersFlag
	 * @param csvFieldNamesList
	 */
	private void createAndUpdateUserAccounts(Properties props, List<String> systemUserEmailsList, String owner,
			List<String> groupNameContainsLst, Set<String> permissionProfileSet, List<UserAccount> userAccounts,
			UserResponse userResponse, List<UserSettings> list, String includeInvalidUsersFlag,
			List<String> csvFieldNamesList) {

		String disabled = userResponse.getUserStatus().equals("Active") ? "N" : "Y";
		String lastLogin = userResponse.getLastLogin();

		String formated = "1970-01-01 00:00:00Z";

		if (lastLogin != null) {

			formated = lastLogin.substring(0, 10) + " " + lastLogin.substring(11, 19) + "Z";
		}

		UserAccount userAccount = createUserAccount(props, owner, permissionProfileSet, userResponse, list, disabled,
				formated);

		if (null != groupNameContainsLst && !groupNameContainsLst.isEmpty()) {

			checkUpdateUserAccountWithGroup(groupNameContainsLst, userAccount, owner, userResponse.getGroupList());
		}

		if (null != systemUserEmailsList && !systemUserEmailsList.isEmpty()) {

			checkUpdateUserAccountWithEmail(systemUserEmailsList, userAccount, userResponse.getEmail(), owner);
		}

		if (userResponse.getUserStatus().compareToIgnoreCase("ACTIVE") == 0) {

			checkUserAccountFields(userAccounts, includeInvalidUsersFlag, userAccount, csvFieldNamesList, userResponse);
		}
	}

	private void checkUserAccountFields(List<UserAccount> userAccounts, String includeInvalidUsersFlag,
			UserAccount userAccount, List<String> classFields, UserResponse userResponse) {

		boolean foundEmptyValue = false;
		boolean includeInvalidUser = false;

		if (!StringUtils.isEmpty(includeInvalidUsersFlag) && "true".equalsIgnoreCase(includeInvalidUsersFlag)) {

			includeInvalidUser = true;
		}

		try {

			if (null != classFields && !classFields.isEmpty()) {

				for (String classField : classFields) {

					Method method = ReflectionUtils.findGetter(userAccount, classField);

					String methodValue = (String) method.invoke(userAccount);
					if (StringUtils.isEmpty(methodValue)) {

						errorFlag = true;
						logger.error(classField + " column is empty for " + userResponse.getEmail() + " in accountId "
								+ userResponse.getAccountId());

						foundEmptyValue = true;
					}
				}

				if (foundEmptyValue) {
					if (includeInvalidUser) {

						userAccounts.add(userAccount);
					}
				}
			}

			if (!foundEmptyValue) {
				userAccounts.add(userAccount);
			}

		} catch (IllegalAccessException e) {

			errorFlag = true;
			logger.error("IllegalAccessException message" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			errorFlag = true;
			logger.error("IllegalArgumentException message" + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {

			errorFlag = true;
			logger.error("InvocationTargetException message" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param props
	 * @param owner
	 * @param permissionProfileSet
	 * @param userResponse
	 * @param list
	 * @param disabled
	 * @param formated
	 * @return
	 */
	private UserAccount createUserAccount(Properties props, String owner, Set<String> permissionProfileSet,
			UserResponse userResponse, List<UserSettings> list, String disabled, String formated) {

		String updatePermissionKey = null;
		String permissionName = userResponse.getPermissionProfileName();

		if (!StringUtils.isEmpty(permissionName) && null != props) {

			updatePermissionKey = getPermissionKeyUpdatePermissionSet(props, permissionProfileSet, permissionName);
		}

		String permissionRights = buildRights(list);

		String rights = (null == props && StringUtils.isEmpty(updatePermissionKey)) ? permissionRights
				: updatePermissionKey;

		UserAccount userAccount = new UserAccount(psProps.getCustomerSystemText(), owner,
				userResponse.getAccountId() + "-" + userResponse.getEmail(), formated, rights, disabled,
				userResponse.getFirstName(), userResponse.getLastName(), userResponse.getEmail(), "mail");
		userAccount.setPersonal("Y");

		if (!StringUtils.isEmpty(permissionRights)) {

			userAccount.setPriv(permissionRights.contains("canManageAccount") ? "Y" : "N");
		} else {
			userAccount.setPriv("N");
		}

		return userAccount;
	}

	/**
	 * @param props
	 * @param today
	 * @param outputDir
	 * @param owner
	 * @param permissionProfileSet
	 * @throws IOException
	 */
	private void createPermissionProfileDescCsv(Properties props, String today, File outputDir, String owner,
			Set<String> permissionProfileSet) throws IOException {

		List<Entitlement> entitlements = new ArrayList<Entitlement>();

		for (String permissionProfile : permissionProfileSet) {

			String longDescription = null;
			if (null != PSUtils.getPropertyIgnoreCase(permissionProfile, props)) {

				longDescription = (String) PSUtils.getPropertyIgnoreCase(permissionProfile, props);
			} else {

				errorFlag = true;
				logger.error("No Description available in Property file for " + permissionProfile);
			}

			if (longDescription != null) {

				Entitlement entitlement = new Entitlement(owner, permissionProfile, longDescription);
				entitlement.setLanguage("EN");
				entitlements.add(entitlement);
			}

		}

		String entitlementFile = outputDir + MessageFormat.format(psProps.getEntitlementFilename(), owner, today);
		CsvFileWriter.writeEntitlementCsv(entitlementFile, entitlements);
	}

	/**
	 * @param props
	 * @param permissionProfileSet
	 * @param permissionName
	 * @return
	 */
	private String getPermissionKeyUpdatePermissionSet(Properties props, Set<String> permissionProfileSet,
			String permissionName) {

		String updatePermissionKey;
		if (null != PSUtils.getPropertyIgnoreCase(permissionName + KEY_SUFFIX, props)) {

			updatePermissionKey = (String) PSUtils.getPropertyIgnoreCase(permissionName + KEY_SUFFIX, props);
		} else {

			updatePermissionKey = permissionName;
		}

		permissionProfileSet.add(updatePermissionKey);

		return updatePermissionKey;
	}

	/**
	 * @param groupNameContainsLst
	 * @param userAccount
	 * @param owner
	 * @param userGroupList
	 */
	private void checkUpdateUserAccountWithGroup(List<String> groupNameContainsLst, UserAccount userAccount,
			String owner, List<Group> userGroupList) {

		for (Group group : userGroupList) {
			if (groupNameContainsLst.contains(group.getGroupName())) {
				userAccount.setPersonal("N");
				userAccount.setOwner(owner);
				userAccount.setOwnerType("nar");
			}
		}
	}

	/**
	 * @param systemUserEmailsList
	 * @param userAccount
	 * @param userEmail
	 * @param owner
	 */
	private void checkUpdateUserAccountWithEmail(List<String> systemUserEmailsList, UserAccount userAccount,
			String userEmail, String owner) {

		for (String systemUserEmail : systemUserEmailsList) {

			if (systemUserEmail.equalsIgnoreCase(userEmail)) {

				userAccount.setPersonal("N");
				userAccount.setOwner(owner);
				userAccount.setOwnerType("nar");
			}
		}
	}

	/**
	 * @param list
	 * @param commandParams
	 * @param outputDir
	 * @param today
	 * @throws IOException
	 */
	private void createUserDetailsEntitlements(List<UserSettings> list, String owner, File outputDir, String today)
			throws IOException {

		Map<String, String> map = loadPermissionDescription();
		List<Entitlement> entitlements = new ArrayList<Entitlement>();
		for (UserSettings userSettings : list) {
			String longDescription = map.get(userSettings.getName());
			if (longDescription != null) {
				Entitlement entitlement = new Entitlement(owner, userSettings.getName(), longDescription);
				entitlement.setLanguage("EN");
				entitlements.add(entitlement);
			}

		}

		String entitlementFile = outputDir + MessageFormat.format(psProps.getEntitlementFilename(), owner, today);
		CsvFileWriter.writeEntitlementCsv(entitlementFile, entitlements);
	}

	/**
	 * @param path
	 * @return
	 */
	private File getOutputDirectory(String path) {
		path = StringUtils.isEmpty(path) ? psProps.getDefaultCsvFolder() : path;
		File outputDir = new File(path);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
		return outputDir;
	}

	protected String buildRights(List<UserSettings> list) {
		String rights = "";
		for (UserSettings userSettings : list) {
			if ("true".equals(userSettings.getValue())) {
				rights += userSettings.getName() + " | ";
			}
		}
		if (rights.endsWith(" | ")) {
			rights = rights.substring(0, rights.length() - 3);
		}
		return rights;
	}

	protected Map<String, String> loadPermissionDescription() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("apiAccountWideAccess", "Manage envelopes for the entire account through the DocuSign API");
		map.put("allowEmailChange", "Allow recipient to change their email address");
		map.put("allowPasswordChange", "Allo recipient to change their password");
		map.put("bulksend", "Allow sender to bulk send envelopes to a list of recipient");
		map.put("canManageAccount", "Manage account settings, manage user settings, add users, and remove users");
		map.put("canLockEnvelopes", "Enable member to lock envelopes");
		map.put("canUseScratchpad", "Enable member to use scratchpad");
		map.put("canSendEnvelope", "Can send envelopes via console or API");
		map.put("canSendAPIRequests", "canSendAPIRequests - this entitlement is deprecated (not used)");
		map.put("enableVaulting", "When true, this user can use electronic vaulting for documents");
		map.put("vaultingMode",
				"This element sets the electronic vaulting mode for the user. Enumeration values are: None, eStored and electronicOriginal");
		map.put("enableTransactionPoint",
				"When true, this user can select an envelope from their member console and upload the envelope documents to TransactionPoint (not used for DB)");
		map.put("enableSequentialSigningAPI",
				"When true, this user can define the routing order of recipients for envelopes sent using the DocuSign API");
		map.put("enableSquentialSigningUI",
				"When true, this user can define the routing order of recipients while sending documents for signature");
		map.put("enableDSPro", "enableDSPro - this entitlement is deprecated (not used)");
		map.put("powerFormAdmin", "When true, this user can create, manage and download the PowerForms documents");
		map.put("powerFormUser", "When true, this user can view and download PowerForms documents");
		map.put("canEditSharedAddressbook",
				"This element sets the address book usage and management rights for the user. Enumeration values are: None, UseOnlyShared, UsePrivateAndShared, Share");
		map.put("canManageTemplates",
				"This element sets the template usage and management rights for the user. Enumeration values are: None, Use, Create, Share");
		map.put("enableSignOnPaperOverride",
				"When true, this user can override the account setting that determines if signers may sign their documents on paper as an option to signing electronically");
		map.put("enableSignerAttachments",
				"When true, this user can add requests for attachments from signers while sending documents");
		map.put("allowSendOnBehalfOf",
				"When true, this user can send envelopes 'on behalf of' other users through the API");
		map.put("allowRecipientLanguageSelection",
				"When true, this provides the sender with the option to set the language used in the standard email format for a recipient when creating an envelope");
		return map;
	}
}