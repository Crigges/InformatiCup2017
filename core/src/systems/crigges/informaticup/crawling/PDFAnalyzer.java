package systems.crigges.informaticup.crawling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import systems.crigges.informaticup.wordanalytics.WordCounter;

/**
 * This class allows simple text and image extraction out of the Portable
 * Document Format (.pdf) files. Files need to be passed virtual as an array of
 * bytes for faster processing.
 * 
 * @author Rami Aly & Andre Schurat
 */
public class PDFAnalyzer {

	private PDDocument pd;

	/**
	 * Creates a new PDFAnalyzer out of the given file.
	 * 
	 * @param pfg
	 *            the .pdf file represented as byte array
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	public PDFAnalyzer(byte[] pdf) throws IOException {
		pd = PDDocument.load(pdf);
	}

	/**
	 * Extracts the raw unformatted text out of the document.
	 * 
	 * @return the raw text as String
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	public String getRawText() throws IOException {
		PDFTextStripper stripper;
		stripper = new PDFTextStripper();
		return stripper.getText(pd);
	}

	/**
	 * Recursive function to get the amount of images stored inside the
	 * document. Images them self aren't stored to save heap space.
	 * 
	 * @return the amount of images inside the document
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	public int getImageCount() throws IOException {
		int imageCount = 0;
		for (PDPage page : pd.getPages()) {
			imageCount += getImagesFromResources(page.getResources());
		}

		return imageCount;
	}

	/**
	 * Recursive function to get the amount of images stored inside the given
	 * page. Images them self aren't stored to save heap space.
	 * 
	 * @param resources
	 *            the page where images should be counted in
	 * @return the amount of images inside the given page
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	private int getImagesFromResources(PDResources resources) throws IOException {
		int imageCount = 0;
		if (resources != null) {
			for (COSName xObjectName : resources.getXObjectNames()) {
				PDXObject xObject = resources.getXObject(xObjectName);

				if (xObject instanceof PDFormXObject) {
					imageCount += getImagesFromResources(((PDFormXObject) xObject).getResources());
				} else if (xObject instanceof PDImageXObject) {
					imageCount++;
				}
			}
		}
		return imageCount;
	}
}
