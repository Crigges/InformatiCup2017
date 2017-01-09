package systems.crigges.informaticup;

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

public class PDFAnalyzer {

	private PDDocument pd;

	public PDFAnalyzer(File pfd) throws IOException {
		pd = PDDocument.load(pfd);
	}
	
	public PDFAnalyzer(InputStream pfd) throws IOException {
		pd = PDDocument.load(pfd);
	}
	
	public PDFAnalyzer(byte[] pdf) throws IOException {
		pd = PDDocument.load(pdf);
	}

	public Set<Map.Entry<String, Integer>> getSortedWordCount() {
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			WordCounter counter = new WordCounter();
			counter.feed(stripper.getText(pd));
			counter.close();
			return counter.getSortedEntrys();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getRawText(){
		PDFTextStripper stripper;
		try {
			stripper = new PDFTextStripper();
			return stripper.getText(pd);
		} catch (IOException e) {
			return "";
		}
		
	}

	public int getImageCount() throws IOException {
		int imageCount = 0;
		for (PDPage page : pd.getPages()) {
			imageCount += getImagesFromResources(page.getResources());
		}

		return imageCount;
	}

	private int getImagesFromResources(PDResources resources) throws IOException {
		int imageCount = 0;
		if(resources != null){
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

	public void accessMetadata() {
		PDDocumentInformation info = pd.getDocumentInformation();
		System.out.println("Page Count=" + pd.getNumberOfPages());
		System.out.println("Title=" + info.getTitle());
		System.out.println("Author=" + info.getAuthor());
		System.out.println("Subject=" + info.getSubject());
		System.out.println("Keywords=" + info.getKeywords());
		System.out.println("Creator=" + info.getCreator());
		System.out.println("Producer=" + info.getProducer());
		System.out.println("Creation Date=" + info.getCreationDate());
		System.out.println("Modification Date=" + info.getModificationDate());
		System.out.println("Trapped=" + info.getTrapped());
	}

	public static void main(String[] args) throws IOException {
		PDFAnalyzer pdf = new PDFAnalyzer(new File("assets\\Nahostkonflikt.pdf"));
		for (Entry<String, Integer> e : pdf.getSortedWordCount()) {
			System.out.println(e);
		}
	}

}
