package systems.crigges.informaticup.nnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import systems.crigges.informaticup.wordanalytics.Dictionary;
import systems.crigges.informaticup.wordanalytics.DictionaryEntry;
import systems.crigges.informaticup.wordanalytics.WordStatistic;

/**
 * This class is responsible for calculating, normalizing and formatting the
 * deviation of occurrences of Words in a DataSet to an Occurence in a
 * Dictionary.
 * 
 * @param dataSet
 * @param dictionary
 * @param functionValue
 */
public class InputDataFormatter {
	private double[] inputNeurons;
	private HashMap<String, Integer> dataMap;
	private ArrayList<DictionaryEntry> dictionary;
	private LogisticFunction logisticFunction;
	private HashMap<String, Integer> indexMap;

	/**
	 * Creates new InputDataFormatter
	 * 
	 * @param dataSet
	 * @param dictionary
	 * @param functionValue
	 * @see Dictionary
	 */
	public InputDataFormatter(HashMap<String, Integer> dataMap, ArrayList<DictionaryEntry> dictionary,
			double functionValue) {
		this.dictionary = dictionary;
		this.dataMap = dataMap;
		this.logisticFunction = new LogisticFunction(functionValue);
		this.inputNeurons = new double[2 * dictionary.size()];
		calculateInput();
	}

	/**
	 * @return two doubles for each word of dictionary which was given
	 */
	public double[] getInputNeurons() {
		return inputNeurons;
	}

	/**
	 * Uses two normalized doubles to represent the deviation between occurence
	 * of one word in given Set and Dictionary
	 * 
	 * @see LogisticFunction
	 */
	private void calculateInput() {
		for (int i = 0; i < inputNeurons.length; i++) {
			if (i % 2 == 0) {
				inputNeurons[i] = 1;
			}
		}
		indexMap = new HashMap<>();
		WordStatistic wordsInDictionary = new WordStatistic();
		int i = 0;
		for (DictionaryEntry entryD : dictionary) {
			Integer count = dataMap.get(entryD.getWord());
			if(count != null && count != 0){
				wordsInDictionary.add(entryD.getWord(), count);
			}
			indexMap.put(entryD.getWord(), i++);
		}
		for (Entry<String, Double> entry : wordsInDictionary.getSet()) {
			int index = indexMap.get(entry.getKey());
			double normalizedValue = wordsInDictionary.getStatistic(entry.getKey())
					/ dictionary.get(index).getOccurrence();
			double funcnorm = logisticFunction.calc(normalizedValue);
			//System.out.println(funcnorm);
			if (funcnorm < 0) {
				inputNeurons[index * 2] = Math.abs(funcnorm);
			} else {
				inputNeurons[index * 2 + 1] = funcnorm;
				inputNeurons[index * 2] = 0;
			}
		}

	}
}
