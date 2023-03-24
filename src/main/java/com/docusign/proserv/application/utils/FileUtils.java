package com.docusign.proserv.application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.docusign.batch.domain.AppConstants;
import com.docusign.exception.InvalidInputException;

public class FileUtils {

	public static String FILE_DATE_PATTERN = "yyyyMMddHHmm";

	final static Logger logger = LogManager.getLogger(FileUtils.class);

	public static void splitFile(String inputFile, int lines) throws IOException {

		int count = -1;
		File file = new File(inputFile);
		Scanner scanner = new Scanner(file);

		while (scanner.hasNextLine()) { // counting the lines in the input file
			scanner.nextLine();
			count++;
		}
		scanner.close();

		logger.info("Total lines in the input file is " + count);

		if (count < 1) {

			throw new InvalidInputException(inputFile + " has no content");
		}

		if (count <= lines) {

			logger.info("No Split will happen for inputFile " + inputFile
					+ " as total lines in the file is less than the requested split lines count of " + lines);
			return;
		}

		int files = 0;
		if ((count % lines) == 0) {
			files = (count / lines);
		} else {
			files = (count / lines) + 1;
		}

		logger.info("Number of files to be created " + files); // number of files that shall be created

		myFunction(lines, files, inputFile, file.getName());

		Path inputfileFullPath = Paths.get(inputFile);
		Path parentDirectory = inputfileFullPath.getParent();

		PSUtils.createDirectory(parentDirectory.getParent() + File.separator + AppConstants.ORIGINAL_FOLDER_NAME);

		Files.move(Paths.get(inputFile), Paths.get(parentDirectory.getParent() + File.separator
				+ AppConstants.ORIGINAL_FOLDER_NAME + File.separator + file.getName()));

	}

	public static void myFunction(int lines, int files, String inputFile, String inputFileName)
			throws FileNotFoundException, IOException {

		Path inputfileFullPath = Paths.get(inputFile);
		Path parentDirectory = inputfileFullPath.getParent();

		BufferedReader br = new BufferedReader(new FileReader(inputFile)); // reader for input file intitialized only
		String headerLine = br.readLine();
		String strLine = null;

		String[] noExtensionFileNameArr = inputFileName.split(".csv");

		String currDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(FILE_DATE_PATTERN));

		for (int i = 1; i <= files; i++) {

			FileWriter fstream1 = new FileWriter(parentDirectory + File.separator + noExtensionFileNameArr[0] + "_"
					+ currDateTime + "_" + i + ".csv"); // creating
			// a
			// new
			// file
			// writer.
			BufferedWriter out = new BufferedWriter(fstream1);
			out.write(headerLine);
			out.newLine();
			for (int j = 0; j < lines; j++) { // iterating the reader to read only the first few lines of the csv as
												// defined earlier
				strLine = br.readLine();
				if (strLine != null) {
					out.write(strLine);
					out.newLine();
				}
			}
			out.close();
		}

		br.close();
	}

}