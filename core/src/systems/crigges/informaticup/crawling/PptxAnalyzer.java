package systems.crigges.informaticup.crawling;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class PptxAnalyzer {

	private XMLSlideShow ppt;

	public PptxAnalyzer(byte[] data) throws IOException {
		ppt = new XMLSlideShow(new ByteArrayInputStream(data));
	}

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

	public static void main(String[] args) throws Exception {
		PptxAnalyzer doc = new PptxAnalyzer(Files.readAllBytes(new File("./assets/testPpt.pptx").toPath()));
		String s = doc.getRawText();
		PrintWriter writer = new PrintWriter(new File("./test/pptextract.txt"));
		writer.print(s);
		writer.close();
		System.out.println(doc.getImages().size());
	}

}
