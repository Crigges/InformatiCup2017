package systems.crigges.informaticup.crawling;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

/**
 * This class allows simple text and image extraction out of Mircosoft's Office
 * .pptx files. Files need to be passed virtual as an array of bytes for faster
 * processing.
 * 
 * @author Rami Aly & Andre Schurat
 */
public class PptxAnalyzer {

	private XMLSlideShow ppt;

	/**
	 * Creates a new PptxAnalyzer out of the given file.
	 * 
	 * @param data
	 *            the .pptx file represented as byte array
	 * @throws IOException
	 *             if file can't be read or is protected
	 */
	public PptxAnalyzer(byte[] data) throws IOException {
		ppt = new XMLSlideShow(new ByteArrayInputStream(data));
	}

	/**
	 * Extracts the raw unformatted text out of the presentation.
	 * 
	 * @return the raw text as String
	 */
	public String getRawText() {
		StringBuilder builder = new StringBuilder();
		for (XSLFSlide slide : ppt.getSlides()) {
			XSLFShape[] shapes = slide.getShapes();
			for (XSLFShape shape : shapes) {
				if (shape instanceof XSLFTextShape) {
					XSLFTextShape textShape = (XSLFTextShape) shape;
					builder.append(textShape.getText());
				}
			}
		}
		return builder.toString();
	}

	/**
	 * Extracts all images out of the document.
	 * 
	 * @return the List of images contained inside the document
	 */
	public List<XSLFPictureShape> getImages() {
		ArrayList<XSLFPictureShape> res = new ArrayList<>();
		for (XSLFSlide slide : ppt.getSlides()) {
			XSLFShape[] shapes = slide.getShapes();
			for (XSLFShape shape : shapes) {
				if (shape instanceof XSLFPictureShape) {
					res.add((XSLFPictureShape) shape);
				}
			}
		}
		return res;
	}

}
