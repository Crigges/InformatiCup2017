package systems.crigges.informaticup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.InputFileReader.Repository;

public class CDictionary {
	private static final double defaultUnifierStrength = 0.8;
	private static final double wordsPerCategory = 20;

	private HashMap<RepositoryTyp, WordUnifier> unifiedGroupDictonary = new HashMap<>();
	private HashMap<RepositoryTyp, WordStatistic> groupWordStatistic = new HashMap<>();
	private Set<String> dictionaryWords = new HashSet<String>();
	private WordStatistic naturalWordStatistic = new WordStatistic();
	private List<Repository> repositorys;

	public CDictionary(List<Repository> repositorys) {
		this.repositorys = repositorys;
		for (RepositoryTyp type : RepositoryTyp.values()) {
			unifiedGroupDictonary.put(type, new WordUnifier());
		}
		generate();
	}

	private void generate() {
		for (Repository r : repositorys) {
			try {
				GithubRepoCrawler crawler = RepoCacher.get(r.getName());
				unifiedGroupDictonary.get(r.getTyp()).add(crawler.getWordCount());
			} catch (IOException e) {
				e.printStackTrace(); // skip for now
			}
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordUnifier unifier = unifiedGroupDictonary.get(type);
			unifier.finish(defaultUnifierStrength);
			naturalWordStatistic.add(unifier.getUnifiedStatistic().getIntSet());
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordStatistic statistic = unifiedGroupDictonary.get(type).getUnifiedStatistic();
			statistic.calculateVariance(naturalWordStatistic);
			groupWordStatistic.put(type, statistic);
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			groupWordStatistic.get(type).sortWordCount();
			int count = 0;
			for (Entry<String, Double> word : groupWordStatistic.get(type).getSet()) {
				if (count < wordsPerCategory) {
					dictionaryWords.add(word.getKey());
					count++;
				} else {
					break;
				}
			}
		}
		serializeDictionary();
	}

	private void serializeDictionary() {
		try {
			FileOutputStream fileOut = new FileOutputStream("/assets/dictionary.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(dictionaryWords);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			System.out.println("Dictionary could not be serialized");
			e.printStackTrace();
		}

	}

	public ArrayList<String> getWords() {
		return new ArrayList<String>(dictionaryWords);
	}

	public static void main(String[] args) throws Exception {
		List<Repository> list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
		ArrayList<String> words = new CDictionary(list).getWords();
		for (String word : words) {
			System.out.println(word);
		}
	}
}
