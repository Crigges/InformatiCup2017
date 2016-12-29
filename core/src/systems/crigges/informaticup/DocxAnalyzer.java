package systems.crigges.informaticup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

public class DocxAnalyzer {

	private XWPFDocument doc;

	public DocxAnalyzer(byte[] data) throws IOException {
		doc = new XWPFDocument(new ByteArrayInputStream(data));
	}

	public String getRawText() throws Exception {
		XWPFWordExtractor ex = new XWPFWordExtractor(doc);
		String text = ex.getText();
		return text;
	}

	public List<XWPFPictureData> getImages() throws IOException {
		return doc.getAllPictures();
	}

	public static void main(String[] args) throws Exception {
		DocxAnalyzer doc = new DocxAnalyzer(Files.readAllBytes(new File("./assets/testDoc.docx").toPath()));
		String s = doc.getRawText();
		PrintWriter writer = new PrintWriter(new File("./test/docextract.txt"));
		writer.print(s);
		writer.close();
		doc.getImages();
	}

}
