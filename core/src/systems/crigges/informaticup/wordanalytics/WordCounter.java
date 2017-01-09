package systems.crigges.informaticup.wordanalytics;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.Normalizer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class offers multithreaded word counting for unformatted Strings
 * 
 * @author Rami Aly & Crigges
 */
public class WordCounter implements Closeable {
	private Scanner scanner;
	private PipedInputStream in;
	private PipedOutputStream out;
	private Thread analyzer;
	private HashMap<String, Integer> wordCount = new HashMap<>();
	private long totalWordCount = 0;
	private long numberCount = 0;

	/**
	 * Private constructor which setups the streams and <tt>Scanner</tt> and
	 * also starts the analyze thread.
	 * 
	 * @param out
	 *            The piped stream where the formatted strings are written to
	 */
	private WordCounter(PipedOutputStream out) {
		this.out = out;
		try {
			in = new PipedInputStream(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner = new Scanner(in);
		scanner.useDelimiter(" ");
		analyzer = new Thread(() -> parseInput());
		analyzer.start();
	}

	/**
	 * Creates a new WordCounter with a empty word list
	 */
	public WordCounter() {
		this(new PipedOutputStream());
	}

	/**
	 * Offers a (unformatted) String to the counter. The String will be
	 * formatted heavily to get as much matches as possible.
	 * 
	 * @param text
	 *            The <tt>String</tt> fed to the counter
	 */
	public void feed(String text) {
		text = text + " ";
		text = text.replaceAll("-" + System.lineSeparator(), "");
		text = text.replaceAll(System.lineSeparator(), " ");
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = text.replaceAll("\\p{M}", "");
		text = text.replaceAll("ß", "ss");
		text = text.replaceAll("[^\\p{Alpha}\\p{Digit}]+", " ");
		text = text.toLowerCase();
		if (text != null) {
			try {
				out.write(text.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the Wordcounter. No further <tt>String</tt>s will be accepted.
	 * Also ensures that remaining String are analyzed.
	 */
	public void close() {
		try {
			out.flush();
			out.close();
			analyzer.join();
			out.close();
			in.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a <tt>Set</tt> of <tt>Map</tt> entries ordered by their
	 * occurrence inside the map
	 * 
	 * @return The sorted entries
	 */
	public Set<Map.Entry<String, Integer>> getSortedEntries() {
		return wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
				.entrySet();
	}

	/**
	 * Get the total count of all words offered to the WordCounter
	 * 
	 * @return The total word count
	 */
	public long getTotalWordCount() {
		return totalWordCount;
	}

	/**
	 * Get the total count of all numbers offered to the WordCounter
	 * 
	 * @return The total number count
	 */
	public long getNumberCount() {
		return numberCount;
	}

	/**
	 * Parse the submitted strings to gather the word count by using a
	 * <tt>HashMap</tt>
	 */
	private void parseInput() {
		while (scanner.hasNext()) {
			String next = scanner.next();
			if (next.equals("")) {
				continue;
			}
			if (next.matches("[0-9]+")) {
				numberCount++;
				continue;
			}
			totalWordCount++;
			Integer count = wordCount.get(next);
			if (count == null || count == 0) {
				wordCount.put(next, 1);
			} else {
				wordCount.put(next, count + 1);
			}
		}
	}

	/**
	 * Returns the backing HashMap for this WordCounter. Any changes done to
	 * this map will influence the WordCounter as well.
	 * 
	 * @return the backing HashMap
	 */
	public HashMap<String, Integer> getEntryMap() {
		return wordCount;
	}

}
