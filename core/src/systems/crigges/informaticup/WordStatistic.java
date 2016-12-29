package systems.crigges.informaticup;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class WordStatistic {
	private HashMap<String, Integer> wordCount = new HashMap<>();
	private long totalCount = 0;
	
	public void add(Set<Entry<String, Integer>> entrys){
		for(Entry<String, Integer> e : entrys){
			Integer content = wordCount.get(e.getKey());
			totalCount += e.getValue();
			if(content == null || content == 0){
				wordCount.put(e.getKey(), e.getValue());
			}else{
				wordCount.put(e.getKey(), content + e.getValue());
			}
		}
	}
	
	public void remove(String word){
		totalCount -= wordCount.get(word);
		wordCount.remove(word);
	}
	
	public double getStatistic(String word){
		long count = wordCount.get(word);
		return count / totalCount;
	}

}
