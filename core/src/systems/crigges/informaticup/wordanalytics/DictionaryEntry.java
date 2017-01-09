package systems.crigges.informaticup.wordanalytics;

import java.io.Serializable;

/**
 * This class defines an entry inside a Dictionary. It simply wraps the word
 * together with it's occurence in the <tt>neutralStatisticGroup</tt>.
 * Additionally it provides word and occurrence based comparability with other
 * <tt>DictionaryEntry</tt>s.
 * 
 * @author Rami Aly & Crigges
 * @see Dictionary
 */
public class DictionaryEntry implements Serializable, Comparable<DictionaryEntry> {
	private static final long serialVersionUID = 1L;
	private String word;
	private double occurrence;

	public DictionaryEntry(String word, double occurrence) {
		this.word = word;
		this.occurrence = occurrence;
	}

	/**
	 * Get the entry's word
	 * 
	 * @return the entry's word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Get the word's natural occurrence referring to the Dictionary which was
	 * used for generation
	 * 
	 * @return the word's natural occurrence
	 */
	public double getOccurrence() {
		return occurrence;
	}

	@Override
	public String toString() {
		return "DictionaryEntry [word=" + word + ", occurrence=" + occurrence + "]";
	}

	/**
	 * Returns zero if the entry's words are equals to each other. Otherwise
	 * occurrence based comparison is used.
	 */
	@Override
	public int compareTo(DictionaryEntry o) {
		if (word.equals(o.word)) {
			return 0;
		} else if (occurrence > o.occurrence) {
			return 1;
		} else {
			return -1;
		}
	}

}
