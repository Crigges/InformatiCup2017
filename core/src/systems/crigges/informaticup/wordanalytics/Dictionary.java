package systems.crigges.informaticup.wordanalytics;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import systems.crigges.informaticup.crawling.RepositoryCrawler;
import systems.crigges.informaticup.general.Constants;
import systems.crigges.informaticup.general.RepositoryDescriptor;
import systems.crigges.informaticup.general.RepositoryTyp;
import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.io.SerializeHelper;

/**
 * This class generates the dictionarys which are used as input for the neural
 * Network. The generation parameters of the {@link Constants} class are used.
 * 
 * @author Rami Aly & Andre Schurat
 * @see WordStatistic
 * @see WordUnifier
 * @see Constants
 */
public class Dictionary {
	private HashMap<RepositoryTyp, WordUnifier> unifiedGroupDictonary = new HashMap<>();
	private HashMap<RepositoryTyp, WordStatistic> groupWordStatistic = new HashMap<>();
	private ArrayList<DictionaryEntry> dictionaryWords = new ArrayList<>();
	private WordStatistic naturalWordStatistic = new WordStatistic();

	/**
	 * Creates a new dictionary out of the given repositories.
	 * 
	 * @param repositories
	 *            Repositories used to create the dictionary
	 * @throws IOException
	 *             if Dictionary could not be written to disc
	 * @see Constants
	 */
	public Dictionary(List<RepositoryDescriptor> repositories) throws IOException {
		List<LoadedRepository> crawlers = new ArrayList<>();
		for (RepositoryDescriptor r : repositories) {
			try {
				crawlers.add(new LoadedRepository(RepoCacher.get(r.getName()), r.getTyp()));
			} catch (IOException | InterruptedException | ExecutionException e) {
				/** ignore empty or protected repositories for now */
			}
		}
		clear();
		for (LoadedRepository crawler : crawlers) {
			unifiedGroupDictonary.get(crawler.getType()).add(crawler.getWordCount());
		}
		generate(Constants.wordDictionaryIntersectionStrength, Constants.wordDictionaryWordCountPerType);
		SerializeHelper.serialize(Constants.wordDictionaryLocation, dictionaryWords);

		clear();
		for (LoadedRepository crawler : crawlers) {
			unifiedGroupDictonary.get(crawler.getType()).add(crawler.getFileEndingCount());
		}
		generate(Constants.fileEndingDictionaryIntersectionStrength, Constants.fileEndingDictionaryWordCountPerType);
		SerializeHelper.serialize(Constants.fileEndingDictionaryLocation, dictionaryWords);

		clear();
		for (LoadedRepository crawler : crawlers) {
			unifiedGroupDictonary.get(crawler.getType()).add(crawler.getFileNameCount());
		}
		generate(Constants.fileNameDictionaryIntersectionStrength, Constants.fileNameDictionaryWordCountPerType);
		SerializeHelper.serialize(Constants.fileNameDictionaryLocation, dictionaryWords);
	}

	/**
	 * Simple wrapper class to bind the {@link RepositoryTyp} to the
	 * {@link RepositoryCrawler}
	 */
	private static class LoadedRepository {
		private RepositoryCrawler crawler;
		private RepositoryTyp type;

		private LoadedRepository(RepositoryCrawler crawler, RepositoryTyp type) {
			this.crawler = crawler;
			this.type = type;
		}

		private RepositoryTyp getType() {
			return type;
		}

		private Set<Entry<String, Integer>> getWordCount() {
			return crawler.getWordCount();
		}

		private Set<Entry<String, Integer>> getFileEndingCount() {
			return crawler.getFileEndingCount();
		}

		private Set<Entry<String, Integer>> getFileNameCount() {
			return crawler.getFileNameCount();
		}

	}

	/**
	 * Clears the previous generated Dictionary and prepares a new one.
	 */
	private void clear() {
		unifiedGroupDictonary.clear();
		for (RepositoryTyp type : RepositoryTyp.values()) {
			unifiedGroupDictonary.put(type, new WordUnifier());
		}
		groupWordStatistic.clear();
		dictionaryWords.clear();
		naturalWordStatistic = new WordStatistic();

	}

	/**
	 * Generates the dictionary using the words inside the
	 * <tt>unifiedGroupDictonary</tt> with the given parameters.
	 * 
	 * @param intersectionStrength
	 *            Defines the intersection strength between all repositories of
	 *            the same type that's needed for a word to be kept for further
	 *            analysis.
	 * @param wordsPerType
	 *            Words per {@link RepositoryTyp} included into the dictionary.
	 *            Duplicates does count.
	 */
	private void generate(double intersectionStrength, int wordsPerType) {
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordUnifier unifier = unifiedGroupDictonary.get(type);
			unifier.finish(intersectionStrength);
			naturalWordStatistic.addDouble(unifier.getUnifiedStatistic().getSet());
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordStatistic statistic = unifiedGroupDictonary.get(type).getUnifiedStatistic();
			statistic.calculateVariance(naturalWordStatistic);
			groupWordStatistic.put(type, statistic);
		}
		Set<String> uniqueWords = new HashSet<>();
		WordStatistic dictionaryWordStatistic = new WordStatistic();
		for (RepositoryTyp type : RepositoryTyp.values()) {
			int count = 0;
			for (Entry<String, Double> word : groupWordStatistic.get(type).getSortedWordCount()) {
				if (count < wordsPerType) {
					uniqueWords.add(word.getKey());
					dictionaryWordStatistic.add(word.getKey(), naturalWordStatistic.getAbsoluteCount(word.getKey()));
					count++;
				} else {
					break;
				}
			}
		}
		Set<DictionaryEntry> tempWordSet = new TreeSet<DictionaryEntry>();
		for (String s : uniqueWords) {
			tempWordSet.add(new DictionaryEntry(s, dictionaryWordStatistic.getStatistic(s)));
		}
		dictionaryWords.addAll(tempWordSet);
	}

	/**
	 * Main method to generate a dictionary using the parameter defined inside
	 * {@link Constants}
	 * 
	 * @param args
	 *            All <tt>args</tt> are ignored
	 * @throws Exception
	 *             if anything major goes wrong
	 */
	public static void main(String[] args) throws Exception {
		List<RepositoryDescriptor> list = new InputFileReader(Constants.trainingRepositoryLocation)
				.getRepositorysAndTypes();
		new Dictionary(list);
	}
}
