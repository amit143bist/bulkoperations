/**
 * 
 */
package com.docusign.batch.partition;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.docusign.batch.domain.AppConstants;

/**
 * @author Amit.Bist
 *
 */
public class MultiFileResourcePartitioner implements Partitioner {

	final static Logger logger = LogManager.getLogger(MultiFileResourcePartitioner.class);

	private String inboundDir;

	public String getInboundDir() {
		return inboundDir;
	}

	public void setInboundDir(String inboundDir) {
		this.inboundDir = inboundDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.core.partition.support.Partitioner#partition(
	 * int)
	 */
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> partitionMap = new HashMap<String, ExecutionContext>();
		File dir = new File(inboundDir);
		if (dir.isDirectory()) {

			File[] files = dir.listFiles();

			Arrays.sort(files, Comparator.comparing(File::getName));

			for (File file : files) {

				if (file.getName().indexOf("csv") > 1) {

					ExecutionContext context = new ExecutionContext();
					logger.info("FileName which will be processed in this batch -> " + file.getName());
					context.put(AppConstants.FILE_RESOURCE_PARAM_NAME, file.toURI().toString());
					context.put(AppConstants.FILE_PARAM_NAME, file.getName());
					partitionMap.put(file.getName(), context);
				}

			}
		}
		return partitionMap;
	}

}