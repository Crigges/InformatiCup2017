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
	private ArrayList<DictionaryEntry> dictionaryWords = new ArrayList<>();
	private WordStatistic naturalWordStatistic = new WordStatistic();
	
	
	public CDictionary(List<Repository> repositorys) throws IOException {
		List<LoadedRepository> crawlers = new ArrayList<>();
		
		for (Repository r : repositorys) {
			try {
				crawlers.add(new LoadedRepository(RepoCacher.get(r.getName()), r.getTyp()));
			} catch (IOException | InterruptedException | ExecutionException e) {/**ignore empty or protected repositorys for now */}
		}
		
		clear();
		for(LoadedRepository crawler : crawlers){
			unifiedGroupDictonary.get(crawler.getType()).add(crawler.getWordCount());
		}
		generate(Constants.wordDictionaryIntersectionStrength, Constants.wordDictionaryWordCountPerType);
		SerializeHelper.serialize(Constants.wordDictionaryLocation, dictionaryWords);
		
		clear();
		for(LoadedRepository crawler : crawlers){
			unifiedGroupDictonary.get(crawler.getType()).add(crawler.getFileEndingCount());
		}
		generate(Constants.fileEndingDictionaryIntersectionStrength, Constants.fileEndingDictionaryWordCountPerType);
		SerializeHelper.serialize(Constants.fileEndingDictionaryLocation, dictionaryWords);
		
		clear();
		for(LoadedRepository crawler : crawlers){
			unifiedGroupDictonary.get(crawler.getType()).add(crawler.getFileNameCount());
		}
		generate(Constants.fileNameDictionaryIntersectionStrength, Constants.fileNameDictionaryWordCountPerType);
		SerializeHelper.serialize(Constants.fileNameDictionaryLocation, dictionaryWords);
	}
	
	private static class LoadedRepository{
		private GithubRepoCrawler crawler;
		private RepositoryTyp type;
		
		private LoadedRepository(GithubRepoCrawler crawler, RepositoryTyp type) {
			this.crawler = crawler;
			this.type = type;
		}
		
		private RepositoryTyp getType() {
			return type;
		}
		
		private Set<Entry<String, Integer>> getWordCount(){
			return crawler.getWordCount();
		}
		
		private Set<Entry<String, Integer>> getFileEndingCount(){
			return crawler.getFileEndingCount();
		}
		
		private Set<Entry<String, Integer>> getFileNameCount(){
			return crawler.getFileNameCount();
		}
		
	}
	
	public static class DictionaryEntry implements Serializable, Comparable<DictionaryEntry>{
		private static final long serialVersionUID = 1L;
		private String word;
		private double occurence;
		
		public DictionaryEntry(String word, double occurence) {
			this.word = word;
			this.occurence = occurence;
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
	
	private void clear(){
		unifiedGroupDictonary.clear();
		for (RepositoryTyp type : RepositoryTyp.values()) {
			unifiedGroupDictonary.put(type, new WordUnifier());
		}
		groupWordStatistic.clear();
		dictionaryWords.clear();
		naturalWordStatistic = new WordStatistic();
		
	}

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
				} else{
					break;
				}
			}
		}
		Set<DictionaryEntry> tempWordSet = new TreeSet<DictionaryEntry>();
		for(String s : uniqueWords){
			tempWordSet.add(new DictionaryEntry(s, dictionaryWordStatistic.getStatistic(s)));
		}
		dictionaryWords.addAll(tempWordSet);
	}

	public static void main(String[] args) throws Exception {
		List<Repository> list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
		new CDictionary(list);
//		for (String word : words) {
//			System.out.println(word);
//		}
//		ArrayList<DictionaryEntry> dictionaryWords = SerializeHelper.deserialize(Constants.fileEndingDictionaryLocation);
//		for(DictionaryEntry entry : dictionaryWords){
//			System.out.println(entry);
//		}
	}
}
