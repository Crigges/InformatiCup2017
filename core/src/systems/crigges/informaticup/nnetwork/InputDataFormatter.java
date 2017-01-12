package systems.crigges.informaticup.nnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.general.CollectedDataSet;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.io.SerializeHelper;
import systems.crigges.informaticup.wordanalytics.Dictionary;
import systems.crigges.informaticup.wordanalytics.DictionaryEntry;
import systems.crigges.informaticup.wordanalytics.WordStatistic;

public class InputDataFormatter {
	private double[] inputNeurons;
	private Set<Entry<String, Integer>> dataSet;
	private ArrayList<DictionaryEntry> dictionary;

	private LogisticFunction logisticFunction;

	/**
	 * This class is responsible for calculating, normalizing and formatting the occurrences of Words in a DataSet in Relation to a Dictionary.
	 * @param dataSet
	 * @param dictionary
	 * @param functionValue
	 */
	public InputDataFormatter(Set<Entry<String, Integer>> dataSet, ArrayList<DictionaryEntry> dictionary,
			double functionValue) {
		this.dictionary = dictionary;
		this.dataSet = dataSet;
		this.logisticFunction = new LogisticFunction(functionValue);
		this.inputNeurons = new double[2 * dictionary.size()];
		calculateInput();
	}

	public double[] getInputNeurons() {
		return inputNeurons;
	}

	private void calculateInput() {
		// TODO: Still need to clarify special indexPosition, MISSING!
		// TODO: ChangeValueSet
		for (int i = 0; i < inputNeurons.length; i++) {
			if (i % 2 == 0) {
				inputNeurons[i] = 1;
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
			if (funcnorm < 0) {
				inputNeurons[indexInDictionaryEntry(entry.getKey()) * 2] = Math.abs(funcnorm);
			} else {
				inputNeurons[indexInDictionaryEntry(entry.getKey()) * 2 + 1] = funcnorm;
				inputNeurons[indexInDictionaryEntry(entry.getKey()) * 2] = 0;
			}
//			System.out.println(funcnorm + "smaller" + "   " + indexInDictionaryEntry(entry.getKey()) * 2 );
//			System.out.println(funcnorm + "bigger" + "  " + indexInDictionaryEntry(entry.getKey()) * 2 + 1);
//			inputNeurons[indexInDictionaryEntry(entry.getKey())] = logisticFunction.calc(normalizedValue);
//			System.out.println(logisticFunction.calc(normalizedValue));
		}

	}

	private int indexInDictionaryEntry(String s) {
		for (int i = 0; i < dictionary.size(); i++) {
			DictionaryEntry d = dictionary.get(i);
			if (d.getWord().equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		CollectedDataSet dataSet = null;
		ArrayList<DictionaryEntry> dictionary = null;
		try {
			dataSet = RepoCacher.get("https://github.com/ericfischer/housing-inventory").getCollectedDataSet();
			dictionary = SerializeHelper.deserialize(new File("assets\\dictionary.ser"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		InputDataFormatter ds = new InputDataFormatter(dataSet.wordCount, dictionary, 0.2);
		for (int i = 0; i < dictionary.size(); i++) {

			System.out.println(dictionary.get(i).getWord() + " " + ds.getInputNeurons()[i *2] + " " + ds.getInputNeurons()[i * 2 +1]);
		}
	}

}
