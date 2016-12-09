package systems.crigges.informaticup;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class WordCounter {
	private Scanner scanner;
	private PipedInputStream in;
	private PipedOutputStream out;
	private Thread analyzer;
	private boolean active;
	private HashMap<String, Integer> wordCount = new HashMap<>();

	public WordCounter() {
		out = new PipedOutputStream();
		try {
			in = new PipedInputStream(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner = new Scanner(in);
		scanner.useDelimiter(" ");
		analyzer = new Thread(() -> parseInput());
		active = true;
		analyzer.start();
	}

	public void feed(String text) {
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = text.replaceAll("\\p{M}", "");
		text = text.replaceAll("[^\\p{Alpha}\\p{Digit}]+", " ");
		if (text != null) {
			try {
				out.write(text.getBytes());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			active = false;
			out.write(0);
			out.flush();
			analyzer.join();
			out.close();
			in.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Set<String> getWordSet() {
		return wordCount.keySet();
	}

	public int getWordCount(String word) {
		return wordCount.get(word);
	}

	private void parseInput() {
		while (active && scanner.hasNext()) {
			String next = scanner.next();
			Integer count = wordCount.get(next);
			if (count == null || count == 0) {
				wordCount.put(next, 1);
			} else {
				wordCount.put(next, count + 1);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		WordCounter w = new WordCounter();
		w.feed("Skunks erreichen eine Kopfrumpfl�nge von etwa 12 bis maximal etwa 51 Zentimetern, eine Schwanzl�nge von 7 bis 41 Zentimeter und ein Gewicht von 0,2 bis 4,5 Kilogramm. Sie sind entsprechend kleine bis mittelgro�e Raubtiere und erreichen eine K�rperl�nge, die bei den gr��eren Arten etwa der einer kleinen Hauskatze entspricht, w�hrend die kleineren Arten in etwa die Gr��e eines Eichh�rnchens haben. Zwischen den Arten kommt es dabei zu starken �berschneidungen der K�rpergr��en. Tendenziell sind vor allem die drei n�rdlicher lebenden Arten der Wei�r�sselskunks (Ferkelskunk, Amazonas-Skunk und Anden-Skunk) mit einer Kopf-Rumpf-L�nge von bis zu etwa 50 Zentimeter die gr��ten Vertreter der Skunks. Ebenfalls in dieser Gr��enordnung sind auch die asiatischen Stinkdachse, die jedoch einen deutlich k�rzeren Schwanz haben. Der Streifenskunk liegt mit einer Kopf-Rumpf-L�nge von bis zu etwa 40 Zentimeter hinter diesen Arten, ist zusammen mit dem sehr langen Schwanz jedoch deutlich l�nger als die Stinkdachse. Der Haubenskunk, der Patagonische Skunk sowie die Arten der Fleckenskunks werden bis etwa 30 Zentimeter lang, wobei der Zwerg-Fleckenskunk aus Mexiko mit einer Kopf-Rumpf-L�nge von maximal 21 Zentimetern die kleinste Art der Skunks ist.Alle Skunks sind durch ihr kontrastreiches Fell gekennzeichnet. Die Grundfarbe ist schwarz oder dunkelbraun, das Gesicht, der Rumpf und auch der Schwanz sind mit wei�en Streifen oder Flecken versehen. Der Rumpf ist langgestreckt und eher schlank und die Beine sind verh�ltnism��ig kurz. Insbesondere die Vorderpfoten sind mit langen, gebogenen Krallen ausgestattet, die hervorragend zum Graben geeignet sind. Der Schwanz ist bei allen amerikanischen Arten buschig, bei den Stinkdachsen jedoch nur sehr kurz ausgebildet. Die Schnauze ist bei den meisten Arten langgestreckt, Augen und Ohren sind relativ klein.");
		//w.close();
		Thread.sleep(1000);
		for(String word : w.getWordSet()){
			System.out.println(word + ": " + w.getWordCount(word));
		}
	}

}
