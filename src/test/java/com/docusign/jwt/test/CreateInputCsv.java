/**
 * 
 */
package com.docusign.jwt.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.docusign.jwt.test.domain.EnvelopeData;
import com.docusign.jwt.test.util.CSVTestFileWriter;

/**
 * @author Amit.Bist
 *
 */
public class CreateInputCsv {

	@Test
	public void test() {

		System.out.println("Reading one input file CreateInputCsv.test() at " + Calendar.getInstance().getTime());
		String line = "";
		List<EnvelopeData> envelopeDataList = new ArrayList<EnvelopeData>();
		List<List<EnvelopeData>> envelopeDataDataList = new ArrayList<List<EnvelopeData>>();
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\SWSetup\\UHG\\csv/Envelope_Report_-_MedSurge.csv"))) {

			int count = 0;
            while ((line = br.readLine()) != null) {
            	
            	EnvelopeData envelopeData = new EnvelopeData();
            	
            	String envelopeId = null;
            	String envelopeStatus = null;
            	if(line.contains(",")){
            		String splitArr[] = line.split(",");
            		envelopeId = splitArr[0];
            		envelopeStatus = splitArr[1];
            	}
            	
            	if(!"Completed".equalsIgnoreCase(envelopeStatus) && !"Declined".equalsIgnoreCase(envelopeStatus)){
            		
            		envelopeData.setEnvelopeId(envelopeId);
                	envelopeData.setReminderEnabled("FALSE");
                	envelopeData.setReminderDelay("3");
                	envelopeData.setReminderFrequency("3");
                	envelopeData.setExpireEnabled("");
                	envelopeData.setExpireAfter("");
                	envelopeData.setExpireWarn("");
                	
                	envelopeDataList.add(envelopeData);
                	count++;
                	
                	if(count%9500 == 0){
                		envelopeDataDataList.add(envelopeDataList);
                		envelopeDataList = new ArrayList<EnvelopeData>();
                	}
            	}
            	
            }
            
            if(null != envelopeDataList && !envelopeDataList.isEmpty()){
            	envelopeDataDataList.add(envelopeDataList);
            }

            int i = 1;
            for(List<EnvelopeData> envelopeDataListLoop: envelopeDataDataList){
            	
            	CSVTestFileWriter.writeEnvelopeCsv("C:\\SWSetup\\UHG\\csv\\split/" + "envelopeInput_" + i + ".csv", envelopeDataListLoop);
            	i++;
            }
            
            System.out.println("All files written and splitted in CreateInputCsv.test() at " + Calendar.getInstance().getTime());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
