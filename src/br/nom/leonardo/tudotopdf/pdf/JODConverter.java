package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.nom.leonardo.tudotopdf.servlet.AppContext;

public class JODConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(JODConverter.class);

	@Override
	public InputStream convertPDF(File theFile) throws PDFConverterException {

		try {
			OfficeDocumentConverter converter = new OfficeDocumentConverter(AppContext.getOfficeManager());

			File tmpPDFOutput = File.createTempFile("JOD-PDF-Temp", ".pdf");
			converter.convert(theFile, tmpPDFOutput);

			ByteArrayInputStream resultStream = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(
					tmpPDFOutput)));

			tmpPDFOutput.delete();

			return resultStream;

		} catch (Exception e) {
			log.error("Fail to create PDF in JOD Converter", e);
			throw new PDFConverterException("Fail to create PDF in JOD Converter", e);
		}
	}

}
