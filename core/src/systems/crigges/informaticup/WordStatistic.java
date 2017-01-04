package systems.crigges.informaticup;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class WordStatistic {
	private Map<String, Double> wordCount = new HashMap<>();
	private long totalCount = 0;

	public void add(Set<Entry<String, Integer>> entrys) {
		for (Entry<String, Integer> e : entrys) {
			Double content = wordCount.get(e.getKey());
			totalCount += e.getValue();
			if (content == null || content == 0) {
				wordCount.put(e.getKey(), (double) (e.getValue()));
			} else {
				wordCount.put(e.getKey(), content + e.getValue());
			}
		}
	}
	
	public void add(String word, double count){
		totalCount += count;
		Double content = wordCount.get(word);
		if (content == null || content == 0) {
			wordCount.put(word, count);
		} else {
			wordCount.put(word, content + count);
		}
	}

	public void addDouble(Set<Entry<String, Double>> entrys) {
		for (Entry<String, Double> e : entrys) {
			Double content = wordCount.get(e.getKey());
			totalCount += e.getValue();
			if (content == null || content == 0) {
				wordCount.put(e.getKey(), (double) (e.getValue()));
			} else {
				wordCount.put(e.getKey(), content + e.getValue());
			}
		}
	}

	public void calculateVariance(WordStatistic neutralStatistic) {
		for (String s : wordCount.keySet()) {
			wordCount.put(s, (Math.abs(getStatistic(s) - neutralStatistic.getStatistic(s))));
		}
	}

	public void remove(String word) {
		Double entry = wordCount.get(word);
		if(entry != null){
			totalCount -= entry;
			wordCount.remove(word);
		}
	}

	public double getStatistic(String word) {
		double count = wordCount.get(word);
		return count / totalCount;
	}
	
	public double getAbsoluteCount(String word){
		return wordCount.get(word);
	}

	public Set<Entry<String, Double>> getSet() {
		return wordCount.entrySet();
	}

	public Set<Entry<String, Double>> getSortedWordCount() {
		return wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
				.entrySet();
	}
}
