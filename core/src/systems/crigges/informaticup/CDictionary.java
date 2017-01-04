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

import systems.crigges.informaticup.InputFileReader.Repository;

public class CDictionary {

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
		generate();
		ArrayList<DictionaryEntry> list = new ArrayList<DictionaryEntry>();
		list.addAll(dictionaryWords);
		SerializeHelper.serialize("./assets/dictionary.ser", list);
	}
	
	public static class DictionaryEntry implements Serializable, Comparable<DictionaryEntry>{
		private static final long serialVersionUID = 1L;
		private String word;
		private double occurence;
		
		public DictionaryEntry(String word, double occurence) {
			this.word = word;
			this.occurence = occurence;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public String getWord() {
			return word;
		}

		public double getOccurence() {
			return occurence;
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
		for (Repository r : repositorys) {
			try {
				unifiedGroupDictonary.get(r.getTyp()).add(RepoCacher.get(r.getName()).getWordCount());
			} catch (IOException | InterruptedException | ExecutionException e) {
				// skip for now
			}
		}
		for (RepositoryTyp type : RepositoryTyp.values()) {
			WordUnifier unifier = unifiedGroupDictonary.get(type);
			unifier.finish(Constants.dictionaryIntersectionStrength);
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
				if (count < Constants.dictionaryWordCountPerType) {
					uniqueWords.add(word.getKey());
					dictionaryWordStatistic.add(word.getKey(), naturalWordStatistic.getAbsoluteCount(word.getKey()));
					count++;
				} else{
					break;
				}
			}
		}
		double d = 0;
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
