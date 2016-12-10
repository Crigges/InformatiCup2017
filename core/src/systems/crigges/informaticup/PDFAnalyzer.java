package systems.crigges.informaticup;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

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

	public List<RenderedImage> getImages() throws IOException {
		List<RenderedImage> images = new ArrayList<>();
		for (PDPage page : pd.getPages()) {
			images.addAll(getImagesFromResources(page.getResources()));
		}

		return images;
	}

	private List<RenderedImage> getImagesFromResources(PDResources resources) throws IOException {
		List<RenderedImage> images = new ArrayList<>();

		for (COSName xObjectName : resources.getXObjectNames()) {
			PDXObject xObject = resources.getXObject(xObjectName);

			if (xObject instanceof PDFormXObject) {
				images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
			} else if (xObject instanceof PDImageXObject) {
				images.add(((PDImageXObject) xObject).getImage());
			}
		}

		return images;
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
			//System.out.println(e);
		}
		int i = 0;
		for(RenderedImage img : pdf.getImages()){
			File f = new File("./test/pdfpic" + i++ + ".png");
			f.createNewFile();
			ImageIO.write(img, "png", f);
		}
		// pdf.accessMetadata();
	}

}
