package br.nom.leonardo.tudotopdf.pdf;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;

/**
 * Generic abstract class to handle file polling conversions. System must copy source file to a folder and collect PDF
 * result in another folder.
 * 
 * @author leonardo
 */
public abstract class FilePollingConverter extends AbstractPDFConverter {

	private Logger log = LoggerFactory.getLogger(FilePollingConverter.class);

	private final String COPY_SUFFIX = ".part";

	@Override
	public File convertPDF(File theFile, String md5UploadedFile) throws PDFConverterException {

		try {
			String sourceFileName = theFile.getName();
			// Append an extension suffix to avoid start processing before the end of copy process
			File destFile = new File(new File(this.getSourceFilesFolder()), sourceFileName + COPY_SUFFIX);
			FileUtils.copyFile(theFile, destFile);

			// Rename to correct extension to start processing
			int count = 0;
			while (!destFile.renameTo(new File(new File(this.getSourceFilesFolder()), sourceFileName))) {
				count++;
				if (count > 20) {
					throw new PDFConverterException("Could not rename source file. Check rename permissions.");
				}
				Thread.sleep(200);
				log.debug("Waiting for rename to complete...");
			}

			// Start look for result PDF
			File pdfFile = new File(new File(this.getDestinationFilesFolder()), sourceFileName + ".pdf");
			File pdfFileFinal = new File(new File(this.getDestinationFilesFolder()), sourceFileName + ".COMPLETED.pdf");
			long timeoutMillis = 1000 * 60 * getTimeOutMinutes();
			long waitMillis = 500;

			int countAvoidInfinite = 0;
			//TODO is there another way to check locked file without renaming?
			while (!(pdfFile.exists() && (pdfFile.renameTo(pdfFileFinal)))) {
				countAvoidInfinite++;
				if (countAvoidInfinite > (timeoutMillis / waitMillis)) {
					// TODO try to recover log file
					throw new PDFConverterException(
							"Could not find a complete PDF file. Check PDF service (" + countAvoidInfinite + ").");
				}
				Thread.sleep(waitMillis);
				log.debug("Waiting for PDF conversion to complete (" + countAvoidInfinite + "/"
						+ (timeoutMillis / waitMillis) + ")...");
			}

			// This must be the returned file
			String outFileName = this.getOutputFileName(md5UploadedFile);
			File outputFile = new File(Config.getString("application.staticFiles"), outFileName);
			FileUtils.copyFile(pdfFileFinal, outputFile);
			return outputFile;
		} catch (Exception e) {
			String msg = "Fail to create PDF in file polling converter";
			log.error(msg, e);
			throw new PDFConverterException(msg, e);
		}

	}

	/**
	 * @return minutes to wait for PDF result from external converter
	 */
	protected abstract int getTimeOutMinutes();

	/**
	 * @return the folder to look for completed PDF files
	 */
	protected abstract String getDestinationFilesFolder();

	/**
	 * @return the folder to put the source files
	 */
	protected abstract String getSourceFilesFolder();

}
