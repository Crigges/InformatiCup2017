package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFAnalyzer {
	
	private PDDocument pd;

	public PDFAnalyzer(File pfd) {
		try {
			pd = PDDocument.load(pfd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Set<Map.Entry<String, Integer>> getSortedWordCount(){
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			PipedOutputStream out = new PipedOutputStream();
			WordCounter counter = new WordCounter();
			counter.feed(stripper.getText(pd));
			counter.close();
			return counter.getSortedEntrys();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		PDFAnalyzer pfd = new PDFAnalyzer(new File("assets\\Nahostkonflikt.pdf"));
		for(Entry<String, Integer> e : pfd.getSortedWordCount()){
			System.out.println(e);
		}
	}

}
