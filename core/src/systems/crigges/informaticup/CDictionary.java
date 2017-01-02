package systems.crigges.informaticup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.nustaq.serialization.FSTObjectOutput;

import systems.crigges.informaticup.InputFileReader.Repository;

public class CDictionary {
	private static final double defaultUnifierStrength = 0.25;
	private static final double wordsPerCategory = 100;

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
		serializeDictionary();
	}

	private void generate() {
		for (Repository r : repositorys) {
			try {
				RepoCacher.getThreaded(r.getName(), (GithubRepoCrawler crawler) -> {});
				//unifiedGroupDictonary.get(r.getTyp()).add(crawler.getWordCount());
			} catch (IOException | InterruptedException | ExecutionException e) {
				// skip for now
			}
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordUnifier unifier = unifiedGroupDictonary.get(type);
			unifier.finish(defaultUnifierStrength);
			naturalWordStatistic.addDouble(unifier.getUnifiedStatistic().getSet());
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordStatistic statistic = unifiedGroupDictonary.get(type).getUnifiedStatistic();
			statistic.calculateVariance(naturalWordStatistic);
			groupWordStatistic.put(type, statistic);
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			int count = 0;
			for (Entry<String, Double> word : groupWordStatistic.get(type).getSortedWordCount()) {
				if(type == RepositoryTyp.OTHER){
					System.out.println(word);
				}
				if (count < wordsPerCategory) {
					dictionaryWords.add(word.getKey());
					count++;
				} else {
					break;
				}
			}
		}
		
	}

	private void serializeDictionary() {
		try {
			File f = new File("./assets/dictionary.ser");
			f.createNewFile();
			FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(f));
			out.writeObject(dictionaryWords);
			out.close();
			out.close();
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
//		for (String word : words) {
//			System.out.println(word);
//		}
	}
}
