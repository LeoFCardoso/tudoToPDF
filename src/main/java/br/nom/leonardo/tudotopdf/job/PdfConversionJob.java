package br.nom.leonardo.tudotopdf.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.config.Config;
import br.nom.leonardo.tudotopdf.model.ConversionConfiguration;
import br.nom.leonardo.tudotopdf.pdf.PDF2PDFOCRConverter;
import br.nom.leonardo.tudotopdf.pdf.PDFConverter;
import br.nom.leonardo.tudotopdf.pdf.PDFConverterException;
import br.nom.leonardo.tudotopdf.pdf.PDFPostProcess;

/**
 * A job to convert content to PDF
 * 
 * @author leonardo
 */
@PersistJobDataAfterExecution
public class PdfConversionJob implements InterruptableJob {

	private Logger log = LoggerFactory.getLogger(PdfConversionJob.class);

	private PDFConverter converter;

	private String md5UploadedFile;

	private File uploadedFile;

	private ConversionConfiguration config;

	private File finalPDFFile = null;

	public PdfConversionJob() {
		super();
	}

	public PdfConversionJob(PDFConverter converter, String md5UploadedFile, File uploadedFile,
			ConversionConfiguration config) {
		super();
		this.converter = converter;
		this.md5UploadedFile = md5UploadedFile;
		this.uploadedFile = uploadedFile;
		this.config = config;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Starting conversion...");
		try {
			converter = (PDFConverter) context.getJobDetail().getJobDataMap().get("p_converter");
			md5UploadedFile = context.getJobDetail().getJobDataMap().getString("p_md5UploadedFile");
			uploadedFile = (File) context.getJobDetail().getJobDataMap().get("p_uploadedFile");
			config = (ConversionConfiguration) context.getJobDetail().getJobDataMap().get("p_config");
			doConvert();
			context.getJobDetail().getJobDataMap().put("p_finalFile", finalPDFFile);
		} catch (Exception e) {
			log.error("PDF conversion error", e);
		}
		log.info("Ended.");
		context.getJobDetail().getJobDataMap().put("p_status", JobStatus.ENDED);
		context.getJobDetail().getJobDataMap().put("p_endTime", System.currentTimeMillis());
	}

	private void doConvert() throws PDFConverterException, FileNotFoundException, IOException {
		File pdfFile = null;

		String outFileName = md5UploadedFile + "-" + converter.getCode() + ".pdf";
		// TODO - create a better code to handle converter with configuration
		if (converter instanceof PDF2PDFOCRConverter) {
			outFileName = md5UploadedFile + "-" + converter.getCode() + "-"
					+ ((PDF2PDFOCRConverter) converter).getPdf2pdfocrConfig().hashCode() + ".pdf";
		}

		File outputFile = new File(Config.getString("application.staticFiles"), outFileName);
		if (outputFile.exists()) {
			log.info("PDF already exists for this MD5 and strategy conversion. Skipping conversion.");
			pdfFile = outputFile;
		} else {
			pdfFile = converter.convertPDF(uploadedFile, md5UploadedFile);
		}
		log.debug("PDF content generated");

		FileInputStream fisPdfFile = new FileInputStream(pdfFile);
		String md5PdfFile = DigestUtils.md5Hex(fisPdfFile);
		fisPdfFile.close();

		String finalOutFileName = md5PdfFile + "-" + config.hashCode() + ".pdf";
		File finalOutputFile = new File(Config.getString("application.staticFiles"), finalOutFileName);
		if (finalOutputFile.exists()) {
			log.info("Final PDF already exists for this pdf file and configuration. Skipping post processing.");
			finalPDFFile = finalOutputFile;
		} else {
			finalPDFFile = PDFPostProcess.process(pdfFile, md5PdfFile, config);
			log.debug("PDF post processed");
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.warn("I was interrupted!");
	}

	/**
	 * Run this job in sync.
	 */
	public void runSync() throws Exception {
		doConvert();
	}

	public File getFinalPDFFile() {
		return finalPDFFile;
	}

}
