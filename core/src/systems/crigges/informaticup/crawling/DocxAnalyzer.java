package systems.crigges.informaticup.crawling;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

/**
 * This class allows simple text and image extraction out of Mircosoft's Office
 * .docx files. Files need to be passed virtual as array of bytes for faster
 * processing.
 * 
 * @author Rami Aly & Crigges
 */
public class DocxAnalyzer {

	private XWPFDocument doc;

	/**
	 * Creates a new DocxAnalyzer out of the given file.
	 * 
	 * @param data
	 *            the .docx file represented as byte array
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	public DocxAnalyzer(byte[] data) throws IOException {
		doc = new XWPFDocument(new ByteArrayInputStream(data));
	}

	/**
	 * Extracts the raw unformatted text out of the document.
	 * 
	 * @return the raw text as String
	 * @throws Exception
	 *             if file can't be read or is protected
	 */
	public String getRawText() throws Exception {
		XWPFWordExtractor ex = new XWPFWordExtractor(doc);
		String text = ex.getText();
		return text;
	}

	/**
	 * Extracts all images out of the document.
	 * 
	 * @return the List of images contained inside the document
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	public List<XWPFPictureData> getImages() throws IOException {
		return doc.getAllPictures();
	}

}
