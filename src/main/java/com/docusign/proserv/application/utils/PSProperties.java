package com.docusign.proserv.application.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.docusign.batch.domain.AppConstants;

@Component(value = "psProps")
@PropertySources({ @PropertySource("classpath:application.properties"),
		@PropertySource(value = "${external.application.properties}", ignoreResourceNotFound = true)

})
public class PSProperties {

	@Autowired
	Environment env;

	public String getCommitInterval() {

		if (!StringUtils.isEmpty(env.getProperty("ds.job.commit.interval"))) {

			return env.getProperty("ds.job.commit.interval");
		} else {
			return "1";
		}
	}

	public Long getBulkBatchSleepInterval() {

		return Long.parseLong(env.getProperty("ds.job.step.bulk.batch.sleep.interval"));
	}

	public String getCustomerSystemText() {
		return env.getProperty("customer.system.text");
	}

	public String getDefaultCsvFolder() {
		return env.getProperty("output.default.folder");
	}

	public String getUserAccountFilename() {
		return env.getProperty("useraccount.filename");
	}

	public String getEntitlementFilename() {
		return env.getProperty("entitlement.filename");
	}

	@Deprecated
	public String getDSOAuthTokenAPI() {
		return env.getProperty("ds.oauth.token.api");
	}

	public String getDSOAuth2TokenAPI() {
		return env.getProperty("ds.oauth2.token.api");
	}

	@Deprecated
	public String getDSUsersAPI() {
		return env.getProperty("ds.users.api");
	}

	public String getTokenOutputMessage() {
		return env.getProperty("token.output.message");
	}

	public String getProcessOutputMessage() {
		return env.getProperty("process.output.message");
	}

	public String getDSAPIError() {
		return env.getProperty("ds.api.error");
	}

	public String getPSUnknownError() {
		return env.getProperty("ps.unknown.error");
	}

	public String getEnvErrorMessage() {
		return env.getProperty("environment.error.message");
	}

	public String getOperationErrorMessage() {
		return env.getProperty("dsoperation.error.message");
	}

	public String getAccountErrorMessage() {
		return env.getProperty("account.error.message");
	}

	@Deprecated
	public String getAuthLoginApi() {
		return env.getProperty("ds.auth.login.api");
	}

	public String getEnvironmentErrorMessage(String environment) {

		return env.getProperty(environment.toLowerCase() + ".environment.error.message");
	}

	public String getEnvironmentHost(String environment) {

		return env.getProperty(environment.toLowerCase() + ".oauth2.as.api.host");
	}

	public String getAuthUserInfo() {
		return env.getProperty("ds.oauth2.userinfo.api");
	}

	public String getUserListApi() {
		return env.getProperty("ds.users.list.api");
	}

	public String getAccountCustomFieldsApi() {
		return env.getProperty("ds.account.envelope.customfields.api");
	}

	public String getEnvelopesNotificationApi() {
		return env.getProperty("ds.account.envelope.notification.api");
	}

	public String getEnvelopesUpdateApi() {
		return env.getProperty("ds.account.envelope.update.api");
	}

	public String getReqKeysMissingErrorMessage() {
		return env.getProperty("ps.req.keys.missing.error.message");
	}

	public String getFriendlyAccountIdMissingErrorMessage() {
		return env.getProperty("ps.friendlyaccountid.missing.error.message");
	}

	public String getFriendlyAccountIdListEmptyErrorMessage() {
		return env.getProperty("ps.friendlyaccountid.listitems.empty.error.message");
	}

	public String getFriendlyAccountIdListInvalidErrorMessage() {
		return env.getProperty("ps.friendlyaccountid.listitems.invalid.error.message");
	}

	public String getDSAPIThresholdPercent() {
		return env.getProperty("ds.api.thresholdpercent");
	}

	public boolean isBurstLimitCheckEnabled() {
		return Boolean.parseBoolean(env.getProperty("ds.api.enableburstlimit"));
	}

	public boolean isTooManyRequestCheckEnabled() {
		return Boolean.parseBoolean(env.getProperty("ds.api.enabletoomanyrequest"));
	}

	public String getEnvelopesResumeApi() {
		return env.getProperty("ds.account.envelope.resume.api");
	}

	public String getEnvelopesCreateApi() {
		return env.getProperty("ds.account.envelope.create.api");
	}

	public String getEnvelopesGetApi() {
		return env.getProperty("ds.account.envelope.get.api");
	}

	public String getTemplatesGetApi() {
		return env.getProperty("ds.account.template.get.api");
	}

	public String getTemplatesRecipientTypes() {
		return env.getProperty("ds.account.template.recipienttypes");
	}

	public String getTemplatesLabelTabTypes() {
		return env.getProperty("ds.account.template.labeltabtypes");
	}

	public String getTemplatesGroupTabTypes() {
		return env.getProperty("ds.account.template.grouptabtypes");
	}

	public String getTemplatesIds() {
		return env.getProperty("ds.api.templateids");
	}

	public List<String> getEnvelopeFields() {

		return Stream.of(env.getProperty("ds.envelope.fields").split(AppConstants.COMMA_DELIMITER)).map(String::trim)
				.collect(Collectors.toList());

	}

	public List<String> getRecipientFields() {

		return Stream.of(env.getProperty("ds.recipient.fields").split(AppConstants.COMMA_DELIMITER)).map(String::trim)
				.collect(Collectors.toList());

	}

	public Boolean getSleepTimeBetweenFilesEnabled() {

		return Boolean.parseBoolean(env.getProperty("ds.job.step.sleep.enabled"));
	}

	public Long getSleepTimeBetweenFiles() {

		return Long.parseLong(env.getProperty("ds.job.step.sleep.interval"));
	}

	public Long getSuccessCSVThresholdCount() {

		return Long.parseLong(env.getProperty("ds.job.success.csv.threshold.count"));
	}

	public String getBulkListCreateApi() {
		return env.getProperty("ds.account.bulklist.create.api");
	}

	public String getBulkListTestApi() {
		return env.getProperty("ds.account.bulklist.test.api");
	}

	public String getBulkListSendApi() {
		return env.getProperty("ds.account.bulklist.send.api");
	}

	public String getBulkListByBatchIdApi() {
		return env.getProperty("ds.account.bulklist.get.api");
	}

	public String getBulkBatchGetApi() {
		return env.getProperty("ds.account.bulkbatch.get.api");
	}

	public String getBulkBatchByBatchidGetApi() {
		return env.getProperty("ds.account.bulkbatch.batchid.get.api");
	}

	public String getEnvelopeTemplateFile() {
		return env.getProperty("ds.account.bulksend.envelopeortemplateid.file");
	}

	// Fetch Global Variables
	public boolean getEnvelopeUpdateGlobalVariables() {

		if (StringUtils.isEmpty(env.getProperty("ds.envelope.update.use.global.variables"))) {

			return false;
		}

		return Boolean.parseBoolean(env.getProperty("ds.envelope.update.use.global.variables"));
	}

	public String getEnvelopeUpdateStatus() {
		return env.getProperty("ds.envelope.update.api.status");
	}

	public String getEnvelopeUpdateVoidedReason() {
		return env.getProperty("ds.envelope.update.api.voidedreason");
	}

	public String getEnvelopeUpdateEmailSubject() {
		return env.getProperty("ds.envelope.update.api.emailsubject");
	}

	public String getEnvelopeUpdateEmailBlurb() {
		return env.getProperty("ds.envelope.update.api.emailblurb");
	}

	public String getEnvelopeUpdatePurgeState() {
		return env.getProperty("ds.envelope.update.api.purgestate");
	}

	public String getEnvelopeUpdateExpireEnabled() {
		return env.getProperty("ds.envelope.update.api.expire.enabled");
	}

	public String getEnvelopeUpdateExpireAfter() {
		return env.getProperty("ds.envelope.update.api.expire.after");
	}

	public String getEnvelopeUpdateExpireWarn() {
		return env.getProperty("ds.envelope.update.api.expire.warn");
	}

	public String getEnvelopeUpdateReminderEnabled() {
		return env.getProperty("ds.envelope.update.api.reminder.enabled");
	}

	public String getEnvelopeUpdateReminderDelay() {
		return env.getProperty("ds.envelope.update.api.reminder.delay");
	}

	public String getEnvelopeUpdateReminderFrequency() {
		return env.getProperty("ds.envelope.update.api.reminder.frequency");
	}

	// Set File Delimiter
	public String getInputFileDelimiter() {
		return env.getProperty("ds.input.file.delimiter");
	}

	public String getOutputFileDelimiter() {
		return env.getProperty("ds.output.file.delimiter");
	}

	// Set Tabvalue replace characters
	public boolean getReplaceTabData() {

		return Boolean.parseBoolean(env.getProperty("ds.account.bulksend.replace.tabdata"));

	}

	public List<String> getReplaceTabLabels() {
		return Stream.of(
				env.getProperty("ds.account.bulksend.replace.tabdata.tablabels").split(AppConstants.COMMA_DELIMITER))
				.map(String::trim).collect(Collectors.toList());
	}

	// BulkSend MailingList creation as per File and template or draftenvelope
	public boolean getFileBasedCreation() {

		return Boolean.parseBoolean(env.getProperty("ds.account.bulksend.use.filebased.creation"));

	}

	public List<String> getFilePatterns() {

		return Stream.of(env.getProperty("ds.account.bulksend.filePattern").split(AppConstants.COMMA_DELIMITER))
				.map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
	}

	// ds.account.bulksend.draftenvelope.creation.folder
	public String getDraftEnvelopeCreationFolder() {
		return env.getProperty("ds.account.bulksend.draftenvelope.creation.folder");
	}

	// Set template for all files
	public String getBulkSendTemplateId() {
		return env.getProperty("ds.account.bulksend.template.id");
	}

	public boolean getUseTemplate() {

		return Boolean.parseBoolean(env.getProperty("ds.account.bulksend.use.template"));
	}

	public boolean getSplitCSV() {

		return Boolean.parseBoolean(env.getProperty("ds.input.file.split"));
	}

	public int getSplitCSVLinesLimit() {

		if (StringUtils.isEmpty(env.getProperty("ds.input.file.split.lines"))) {

			return 500;
		}
		return Integer.parseInt(env.getProperty("ds.input.file.split.lines"));
	}
}