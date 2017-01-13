package systems.crigges.informaticup.nnetwork;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.general.CollectedDataSet;
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
	private Set<Entry<String, Integer>> dataSet;
	private ArrayList<DictionaryEntry> dictionary;

	private LogisticFunction logisticFunction;

	/**
	 * Creates new InputDataFormatter
	 * 
	 * @param dataSet
	 * @param dictionary
	 * @param functionValue
	 * @see Dictionary
	 */
	public InputDataFormatter(Set<Entry<String, Integer>> dataSet, ArrayList<DictionaryEntry> dictionary,
			double functionValue) {
		this.dictionary = dictionary;
		this.dataSet = dataSet;
		this.logisticFunction = new LogisticFunction(functionValue);
		this.inputNeurons = new double[dictionary.size()];
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
				inputNeurons[i] = 0;
			}
		}

		WordStatistic wordsInDictionary = new WordStatistic();
		for (DictionaryEntry entryD : dictionary) {
			for (Entry<String, Integer> entry : dataSet) {
				if (entryD.getWord().equals(entry.getKey())) {
					wordsInDictionary.add(entry.getKey(), entry.getValue());
				}
			}
		}

		for (Entry<String, Double> entry : wordsInDictionary.getSet()) {
			double normalizedValue = wordsInDictionary.getStatistic(entry.getKey())
					/ dictionary.get(indexInDictionaryEntry(entry.getKey())).getOccurrence();
			double funcnorm = logisticFunction.calc(normalizedValue);
				inputNeurons[indexInDictionaryEntry(entry.getKey())] = funcnorm;
		}

	}

	// Searches for a String in Dictionary
	private int indexInDictionaryEntry(String s) {
		for (int i = 0; i < dictionary.size(); i++) {
			DictionaryEntry d = dictionary.get(i);
			if (d.getWord().equals(s)) {
				return i;
			}
		}
		return -1;
	}
}
