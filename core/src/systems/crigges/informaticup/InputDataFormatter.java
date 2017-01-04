package systems.crigges.informaticup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;

public class InputDataFormatter {

	
	
	private double[] inputNeurons = new double[Constants.dictionaryWordCountPerType * Constants.numberOfWordCountTypes];
	private Set<Entry<String, Integer>> dataSet;
	private ArrayList<DictionaryEntry> dictionary;

	private LogisticInputCalculator logisticFunction;

	public InputDataFormatter(Set<Entry<String, Integer>> dataSet, ArrayList<DictionaryEntry> dictionary, double functionValue) {
		this.dictionary = dictionary;
		this.dataSet = dataSet;
		this.logisticFunction = new LogisticInputCalculator(functionValue);
		calculateInput();
	}
	
	public double[] getInputNeurons() {
		return inputNeurons;
	}

	private void calculateInput() {
		// TODO: Still need to clarify special indexPosition, MISSING!
		// TODO: ChangeValueSet
		WordStatistic wordsInDictionary = new WordStatistic();
		for(DictionaryEntry entryD : dictionary){
			for(Entry<String, Integer> entry : dataSet){
				if(entryD.getWord().equals(entry.getKey())){
					wordsInDictionary.add(entry.getKey(), entry.getValue());
				}
			}
		}
		for(Entry<String, Double> entry : wordsInDictionary.getSet()){
			double normalizedValue = entry.getValue() / wordsInDictionary.getTotalCount() * wordsInDictionary.getStatistic(entry.getKey()); 
			inputNeurons[indexInDictionaryEntry(entry.getKey())] = logisticFunction.calc(normalizedValue);
		}
		

	}
	

	
	private int indexInDictionaryEntry(String s){
		for(int i = 0; i < dictionary.size(); i++){
			DictionaryEntry d = dictionary.get(i);
			if(d.getWord().equals(s)){
				return i;
			}
		}
		return -1;
	}

}
