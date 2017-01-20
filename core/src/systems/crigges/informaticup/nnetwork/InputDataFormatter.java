package systems.crigges.informaticup.nnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.neuroph.util.data.norm.MaxMinNormalizer;

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
	private HashMap<String, Integer> dataMap;
	private ArrayList<DictionaryEntry> dictionary;
	private double[][] minMaxValues;
	private int startindex;
	private HashMap<String, Integer> indexMap;
	private int dataMapSize;

	/**
	 * Creates new InputDataFormatter
	 * 
	 * @param dataSet
	 * @param dictionary
	 * @param minMaxValues
	 * @param startindices
	 * @see Dictionary
	 */
	public InputDataFormatter(HashMap<String, Integer> dataMap, ArrayList<DictionaryEntry> dictionary,
			double[][] minMaxValues, int startindices) {
		this.dictionary = dictionary;
		this.dataMap = dataMap;
		startindex = startindices;
		this.minMaxValues = minMaxValues;
		this.inputNeurons = new double[dictionary.size()*2];
		for (Entry<String, Integer> e : dataMap.entrySet()) {
			dataMapSize += e.getValue();
		}
		calculateInput();
	}

	public InputDataFormatter(HashMap<String, Integer> dataMap, ArrayList<DictionaryEntry> dictionary) {
		this.dictionary = dictionary;
		this.dataMap = dataMap;
		this.inputNeurons = new double[dictionary.size()];
		for (Entry<String, Integer> e : dataMap.entrySet()) {
			dataMapSize += e.getValue();
		}
		calculateInputNotNormalized();
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
			if (count != null && count != 0) {
				wordsInDictionary.add(entryD.getWord(), count);
			}
			indexMap.put(entryD.getWord(), i++);
		}
		for (Entry<String, Double> entry : wordsInDictionary.getSet()) {
			int index = indexMap.get(entry.getKey());
//			inputNeurons[index] = wordsInDictionary.getStatistic(entry.getKey());
			double value =  wordsInDictionary.getStatistic(entry.getKey())
					/ dictionary.get(index).getOccurrence();
			// System.out.println(funcnorm);
//			double normalizedValue = wordsInDictionary.getStatistic(entry.getKey()) - dictionary.get(index).getOccurrence();
			double value1 = (value - minMaxValues[0][startindex + index])
					/ (minMaxValues[1][startindex + index] - minMaxValues[0] [startindex + index]);
//			inputNeurons[index] = value;
			if (value - 1 < 0) {
				inputNeurons[index * 2] = Math.abs(value1);
			} else {
				inputNeurons[index * 2 + 1] = value1;
				inputNeurons[index * 2] = 0;
			}
		}

	}

//	private int calculateDictionaryWords(ArrayList<DictionaryEntry> list){
//		for(DictionaryEntry d : list){
//			count + = d.get
//		}
//	}
	
	private void calculateInputNotNormalized() {
		indexMap = new HashMap<>();
		WordStatistic wordsInDictionary = new WordStatistic();
		int i = 0;
		for (DictionaryEntry entryD : dictionary) {
			Integer count = dataMap.get(entryD.getWord());
			if (count != null && count != 0) {
				wordsInDictionary.add(entryD.getWord(), count);
			}
			indexMap.put(entryD.getWord(), i++);
		}
		for (Entry<String, Double> entry : wordsInDictionary.getSet()) {
			int index = indexMap.get(entry.getKey());
			double normalizedValue =
					// wordsInDictionary.getStatistic(entry.getKey())
					// / dictionary.get(index).getOccurrence();
//					(wordsInDictionary.getAbsoluteCount(entry.getKey()) / dataMapSize) ;
					wordsInDictionary.getStatistic(entry.getKey())
					/ dictionary.get(index).getOccurrence();
			// System.out.println(funcnorm);
			inputNeurons[index] = normalizedValue;
		}
	}
}
