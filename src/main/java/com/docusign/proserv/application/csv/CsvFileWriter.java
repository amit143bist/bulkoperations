package com.docusign.proserv.application.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvFileWriter {

	public CsvPreference customCsvPreference() {
		return new CsvPreference.Builder('|', ',', "\n").build();
	}

	public static void writeEntitlementCsv(String csvFileName, List<Entitlement> entitlements) throws IOException {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);

			String[] header = new String[] { "nar-id", "rights", "long-description", "tooltip-name", "help-link", "language" };
			final CellProcessor[] processors = getEntitlementProcessors();

			beanWriter.writeHeader(header);
			header = new String[] { "narId", "rights", "longDescription", "tooltipName", "helpLink", "language" };
			
			for (final Entitlement entitlement : entitlements) {
				beanWriter.write(entitlement, header, processors);
			}
		} finally {
			if (beanWriter != null) {
				beanWriter.close();
			}
		}
	}

	private static CellProcessor[] getEntitlementProcessors() {
		return new CellProcessor[] { new NotNull(), new NotNull(), new NotNull(), new Optional(), new Optional(), new Optional() };
	}

	public static void writeUserAccountCsv(String csvFileName, List<UserAccount> userAccounts) throws IOException {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);

			String[] header = new String[] { "environment", "system", "id", "last-login", "rights", "disabled",
					"first-name", "last-name", "full-name", "owner", "owner-type", "personal", "priv", "start-date", 
					"expiry", "description", "node", "approver", "auth-contact", "group-name"};
			final CellProcessor[] processors = getUserAccountProcessors();

			beanWriter.writeHeader(header);

			header = new String[] { "environment", "system", "id", "lastLogin", "rights", "disabled",
					"firstName", "lastName", "fullName", "owner", "ownerType", "personal", "priv", "startDate", 
					"expiry", "description", "node", "approver", "authContact", "groupName"};
			
			for (final UserAccount userAccount : userAccounts) {
				beanWriter.write(userAccount, header, processors);
			}
		} finally {
			if (beanWriter != null) {
				beanWriter.close();
			}
		}
	}

	private static CellProcessor[] getUserAccountProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()
				, new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()
				, new Optional(), new Optional(), new Optional()};
	}
}
