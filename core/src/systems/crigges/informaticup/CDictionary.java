package systems.crigges.informaticup;

import java.io.File;
import java.io.FileOutputStream;
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

import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.nustaq.serialization.FSTObjectOutput;

import systems.crigges.informaticup.InputFileReader.Repository;

public class CDictionary {
	private static final double defaultUnifierStrength = 0.25;
	private static final double wordsPerCategory = 100;

	private HashMap<RepositoryTyp, WordUnifier> unifiedGroupDictonary = new HashMap<>();
	private HashMap<RepositoryTyp, WordStatistic> groupWordStatistic = new HashMap<>();
	private Set<DictionaryEntry> dictionaryWords = new TreeSet<DictionaryEntry>();
	private WordStatistic naturalWordStatistic = new WordStatistic();
	private List<Repository> repositorys;

	public CDictionary(List<Repository> repositorys) throws IOException {
		this.repositorys = repositorys;
		for (RepositoryTyp type : RepositoryTyp.values()) {
			unifiedGroupDictonary.put(type, new WordUnifier());
		}
		for (Repository r : repositorys) {
			try {
				unifiedGroupDictonary.get(r.getTyp()).add(RepoCacher.get(r.getName()).getWordCount());
			} catch (IOException | InterruptedException | ExecutionException e) {
				// skip for now
			}
		}
		generate();
		SerializeHelper.serialize("./assets/dictionary.ser", dictionaryWords);
	}
	
	public static class DictionaryEntry implements Serializable, Comparable<DictionaryEntry>{
		private static final long serialVersionUID = 1L;
		private String word;
		private double occurence;
		
		public DictionaryEntry(String word, double occurence) {
			this.word = word;
			this.occurence = occurence;
		}

		@Override
		public String toString() {
			return "DictionaryEntry [word=" + word + ", occurence=" + occurence + "]";
		}

		@Override
		public int compareTo(DictionaryEntry o) {
			if(word.equals(o.word)){
				return 0;
			}else if(occurence > o.occurence){
				return 1;
			}else{
				return -1;
			}
		}	
		
		
	}

	private void generate() {
		
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
		Set<String> uniqueWords = new HashSet<>();
		WordStatistic dictionaryWordStatistic = new WordStatistic();
		for (RepositoryTyp type : RepositoryTyp.values()) {
			int count = 0;
			for (Entry<String, Double> word : groupWordStatistic.get(type).getSortedWordCount()) {
				if (count < wordsPerCategory) {
					uniqueWords.add(word.getKey());
					dictionaryWordStatistic.add(word.getKey(), naturalWordStatistic.getAbsoluteCount(word.getKey()));
					count++;
				} else{
					break;
				}
			}
		}
		for(String s : uniqueWords){
			dictionaryWords.add(new DictionaryEntry(s, dictionaryWordStatistic.getStatistic(s)));
		}
	}

	public static void main(String[] args) throws Exception {
		List<Repository> list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
		new CDictionary(list);
//		for (String word : words) {
//			System.out.println(word);
//		}
	}
}
