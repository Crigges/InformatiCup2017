package systems.crigges.informaticup.wordanalytics;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This Classes creates a statistic representation of the word occurrence for
 * all entries added to it. It is used inside the {@link Dictionary} class to
 * determinate which words are added to the dictionary.
 * 
 * @author Rami Aly & Andre Schurat
 * @see WordUnifier
 * @see Dictionary
 */
public class WordStatistic {
	private Map<String, Double> wordCount = new HashMap<>();
	private long totalCount = 0;

	/**
	 * Adds the whole given <tt>Set</tt> of entries to the statistic by calling
	 * {@link WordStatistic#add(String, double)} for each entry.
	 * 
	 * @param entries
	 *            the entries to add
	 */
	public void add(Set<Entry<String, Integer>> entries) {
		for (Entry<String, Integer> e : entries) {
			add(e.getKey(), e.getValue());
		}
	}

	/**
	 * Adds the whole given <tt>Set</tt> of entries to the statistic by calling
	 * {@link WordStatistic#add(String, double)} for each entry.
	 * 
	 * @param entries
	 *            the entries to add
	 */
	public void addDouble(Set<Entry<String, Double>> entries) {
		for (Entry<String, Double> e : entries) {
			add(e.getKey(), e.getValue());
		}
	}

	/**
	 * Adds the words with the given occurrence to the statistic. If the word
	 * does already exists the saved ammount will be increased, not overwritten.
	 * 
	 * @param word
	 *            the word to add
	 * @param count
	 *            the words absolute count
	 */
	public void add(String word, double count) {
		totalCount += count;
		Double content = wordCount.get(word);
		if (content == null || content == 0) {
			wordCount.put(word, count);
		} else {
			wordCount.put(word, content + count);
		}
	}

	/**
	 * Calculates the word variance of this statistic by subtracting the neutral
	 * word occurrence of each word. This method should only be called once
	 * after all entries was added.
	 * 
	 * @param neutralStatistic
	 *            the <tt>WordStatistic</tt> containing a neutral word occurence
	 */
	public void calculateVariance(WordStatistic neutralStatistic) {
		for (String s : wordCount.keySet()) {
			wordCount.put(s, (Math.abs(getStatistic(s) - neutralStatistic.getStatistic(s))));
		}
	}

	/**
	 * Removes the given word out of this statistic.
	 * 
	 * @param word
	 *            the word to be removed
	 */
	public void remove(String word) {
		Double entry = wordCount.get(word);
		if (entry != null) {
			totalCount -= entry;
			wordCount.remove(word);
		}
	}

	/**
	 * Returns the percentual word occurrence of the given word as double. Where
	 * 0.0 is 0% and 1.0 is 100%.
	 * 
	 * @param word
	 *            the word whose occurrence is returned
	 * @return the percentual word's occurrence
	 */
	public double getStatistic(String word) {
		double count = wordCount.get(word);
		return count / totalCount;
	}

	/**
	 * Returns the absolute word count for the given word.
	 * 
	 * @param word
	 *            the whose count is returned
	 * @return the absolute word's count
	 */
	public double getAbsoluteCount(String word) {
		return wordCount.get(word);
	}

	/**
	 * Returns the total word count of all words combined added to this
	 * statistic.
	 * 
	 * @return the total word count
	 */
	public double getTotalCount() {
		return totalCount;
	}

	/**
	 * Returns the <tt>Set</tt> of entries the backing <tt>HashMap</tt> of this
	 * <tt>WordStatistic</tt>
	 * 
	 * @return the backing <tt>Set</tt> of entries
	 */
	public Set<Entry<String, Double>> getSet() {
		return wordCount.entrySet();
	}

	/**
	 * Returns a sorted copy of all entries inside the backing <tt>HashMap</tt> of this
	 * <tt>WordStatistic</tt>
	 * 
	 * @return the sorted Set of entries
	 */
	public Set<Entry<String, Double>> getSortedWordCount() {
		return wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
				.entrySet();
	}
}
