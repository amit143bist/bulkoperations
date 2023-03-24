package com.docusign.jwt.test.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.docusign.jwt.test.domain.EnvelopeData;
import com.docusign.jwt.test.domain.EnvelopeUpdateData;

public class CSVTestFileWriter {

	public static void writeEnvelopeCsv(String csvFileName, List<EnvelopeData> envelopeDataList) throws IOException {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);

			String[] header = new String[] { "envelopeId", "reminderEnabled", "reminderDelay", "reminderFrequency",
					"expireEnabled", "expireAfter", "expireWarn" };
			final CellProcessor[] processors = getEnvelopeDataProcessors();

//			beanWriter.writeHeader(header);

			/*header = new String[] { "envelopeId", "reminderEnabled", "reminderDelay", "reminderFrequency",
					"expireEnabled", "expireAfter", "expireWarn" };*/

			for (final EnvelopeData envelopeData : envelopeDataList) {
				beanWriter.write(envelopeData, header, processors);
			}
		} finally {
			if (beanWriter != null) {
				beanWriter.close();
			}
		}
	}
	
	public static void writeEnvelopeUpdateCsv(String csvFileName, List<EnvelopeUpdateData> envelopeDataList) throws IOException {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(csvFileName), CsvPreference.STANDARD_PREFERENCE);

			String[] header = new String[] { "envelopeId", "envelopeStatus", "voidedReason", "emailSubject",
					"emailBlurb", "purgeState", "resendEnvelope" };
			final CellProcessor[] processors = getEnvelopeDataProcessors();

//			beanWriter.writeHeader(header);

			/*header = new String[] { "envelopeId", "reminderEnabled", "reminderDelay", "reminderFrequency",
					"expireEnabled", "expireAfter", "expireWarn" };*/

			for (final EnvelopeUpdateData envelopeData : envelopeDataList) {
				beanWriter.write(envelopeData, header, processors);
			}
		} finally {
			if (beanWriter != null) {
				beanWriter.close();
			}
		}
	}

	private static CellProcessor[] getEnvelopeDataProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional() };
	}
}