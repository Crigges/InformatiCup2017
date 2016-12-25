package systems.crigges.informaticup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;

public class DocxAnalyzer {

	private WordprocessingMLPackage doc;

	public DocxAnalyzer(byte[] data) throws Docx4JException {
		doc = WordprocessingMLPackage.load(new ByteArrayInputStream(data));
	}

	public String getRawText() throws Exception {
		final String XPATH_TO_SELECT_TEXT_NODES = "//w:p";
		final List<Object> jaxbNodes = doc.getMainDocumentPart().getJAXBNodesViaXPath(XPATH_TO_SELECT_TEXT_NODES, true);
		StringBuilder builder = new StringBuilder();
		for (Object jaxbNode : jaxbNodes) {
			builder.append(jaxbNode.toString());
			builder.append(" ");
		}
		return builder.toString();
	}

	public List<BinaryPartAbstractImage> getImages() throws IOException {
		ArrayList<BinaryPartAbstractImage> res = new ArrayList<>();
		for (Entry<PartName, Part> entry : doc.getParts().getParts().entrySet()) {
			if (entry.getValue() instanceof BinaryPartAbstractImage) {
				res.add((BinaryPartAbstractImage) entry.getValue());
				// File f = new File("./test/docImg" + i++ + ".png");
				// f.createNewFile();
				// FileOutputStream fos = new FileOutputStream(f);
				// ((BinaryPart) entry.getValue()).writeDataToOutputStream(fos);
				// fos.close();
			}
		}
		return res;
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
