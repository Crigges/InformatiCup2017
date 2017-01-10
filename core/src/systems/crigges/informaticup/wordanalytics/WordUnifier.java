package systems.crigges.informaticup.wordanalytics;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class allows unifying of all added entries to the unifier. It is used
 * inside the {@link Dictionary} class to determinate which words are added to a
 * dictionary. It also allows to generate a {@link WordStatistic} out of the
 * unified entries.
 * 
 * @author Rami Aly & Andre Schurat
 * @see Dictionary
 * @see WordStatistic
 */
public class WordUnifier {
	private HashMap<String, Integer> occurence = new HashMap<>();
	private WordStatistic statistic = new WordStatistic();
	private int count = 0;

	/**
	 * Adds all given entries to the unifier. The absolute count is ignored, it
	 * is just used to determinate if a word is kept.
	 * 
	 * @param entries
	 *            the Set of entries added to the unifier
	 */
	public void add(Set<Entry<String, Integer>> entries) {
		count++;
		for (Entry<String, Integer> e : entries) {
			Integer content = occurence.get(e.getKey());
			if (content == null || content == 0) {
				occurence.put(e.getKey(), 1);
			} else {
				occurence.put(e.getKey(), content + 1);
			}
		}
		statistic.add(entries);
	}

	/**
	 * Generates the unified word statistic by removing words that occur less
	 * than the given threshold. This method should only be called once and
	 * after all entries have been added.
	 * 
	 * @param strength
	 *            the intersection strength required to be taken into the
	 *            resulting unified word statistic
	 */
	public void finish(double strength) {
		int neededCount = (int) (strength * count);
		for (Entry<String, Integer> e : occurence.entrySet()) {
			if (e.getValue() < neededCount) {
				statistic.remove(e.getKey());
			}
		}
	}

	/**
	 * Returns the previously generated unified statistic. Will be empty if
	 * {@link WordUnifier#finish(double)} wasn't called before.
	 * 
	 * @return the unified statistic
	 */
	public WordStatistic getUnifiedStatistic() {
		return statistic;
	}

}
