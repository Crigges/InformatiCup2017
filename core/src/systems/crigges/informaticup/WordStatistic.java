package systems.crigges.informaticup;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class WordStatistic {
	private HashMap<String, Double> wordCount = new HashMap<>();
	private long totalCount = 0;
	
	public void add(Set<Entry<String, Integer>> entrys){
		for(Entry<String, Integer> e : entrys){
			Double content = wordCount.get(e.getKey());
			totalCount += e.getValue();
			if(content == null || content == 0){
				wordCount.put(e.getKey(), (double) (e.getValue()));
			}else{
				wordCount.put(e.getKey(), content + e.getValue());
			}
		}
	}
	
	public void calculateVariance(WordStatistic neutralStatistic){
		for(String s : wordCount.keySet()){
			wordCount.put(s, (Math.abs(getStatistic(s) - neutralStatistic.getStatistic(s))));
		}
	}
	
	public void remove(String word){
		totalCount -= wordCount.get(word);
		wordCount.remove(word);
	}
	
	public double getStatistic(String word){
		double count = wordCount.get(word);
		return count / totalCount;
	}

	public Set<Entry<String, Double>> getSet() {
		return wordCount.entrySet();
	}
	
	public Set<Entry<String, Integer>> getIntSet() {
		HashMap<String, Integer> wordCount = new HashMap<>();
		for(Entry<String, Double> word : this.wordCount.entrySet()){
			wordCount.put(word.getKey(), word.getValue().intValue());
		}
		return wordCount.entrySet();
	}
	
	public void sortWordCount(){
		MapUtil.sortByValue(wordCount);
	}
}
