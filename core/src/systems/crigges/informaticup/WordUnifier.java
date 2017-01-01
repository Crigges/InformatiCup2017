package systems.crigges.informaticup;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;



public class WordUnifier {
	private HashMap<String, Integer> occurence = new HashMap<>();
	private WordStatistic statistic = new WordStatistic();
	private int count = 0;
	
	public void add(Set<Entry<String, Integer>> entrys){
		count++;
		for(Entry<String, Integer> e : entrys){
			if(!e.getKey().matches("[0-9]+")){
				Integer content = occurence.get(e.getKey());
				if(content == null || content == 0){
					occurence.put(e.getKey(), 1);
				}else{
					occurence.put(e.getKey(), content + 1);
				}
			}
		}
		statistic.add(entrys);
	}
	
	public void finish(double strength){
		int neededCount = (int) (strength * count);
		for(Entry<String, Integer> e : occurence.entrySet()){
			if(e.getValue() < neededCount){
				statistic.remove(e.getKey());
			}
		}
	}
	
	public WordStatistic getUnifiedStatistic(){
		return statistic;
	}

}
