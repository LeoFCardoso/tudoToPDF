package br.nom.leonardo.tudotopdf.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.words.Document;

public class AsposeWordsConverter implements PDFConverter {

	private Logger log = LoggerFactory.getLogger(JODConverter.class);

	@Override
	public InputStream convertPDF(File theFile) throws PDFConverterException {

		try {

			// TEST ONLY!
			// License license = new License();
			// license.setLicense("");

			File tmpPDFOutput = File.createTempFile("JOD-PDF-Temp", ".pdf");
			Document doc = new Document(new FileInputStream(theFile));
			doc.save(tmpPDFOutput.getCanonicalPath());

			ByteArrayInputStream resultStream = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(
					tmpPDFOutput)));

			tmpPDFOutput.delete();

			return resultStream;

		} catch (Exception e) {
			log.error("Fail to create PDF in AsposeWords Converter", e);
			throw new PDFConverterException("Fail to create PDF in AsposeWords Converter", e);
		}
	}

}
