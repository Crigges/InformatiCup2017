package systems.crigges.informaticup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;
import systems.crigges.informaticup.InputFileReader.Repository;

public class InputDataFormatter {
	private double[] inputNeurons;
	private Set<Entry<String, Integer>> dataSet;
	private ArrayList<DictionaryEntry> dictionary;

	private LogisticInputCalculator logisticFunction;

	public InputDataFormatter(Set<Entry<String, Integer>> dataSet, ArrayList<DictionaryEntry> dictionary,
			double functionValue) {
		this.dictionary = dictionary;
		this.dataSet = dataSet;
		this.logisticFunction = new LogisticInputCalculator(functionValue);
		this.inputNeurons =  new double[dictionary.size()];
		calculateInput();
	}

	public double[] getInputNeurons() {
		return inputNeurons;
	}

	private void calculateInput() {
		//TODO: Still need to clarify special indexPosition, MISSING!
		//TODO: ChangeValueSet
		for(int i = 0;  i < inputNeurons.length; i++){
			inputNeurons[i] = -1;
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
			double normalizedValue = wordsInDictionary.getStatistic(entry.getKey()) /dictionary.get(indexInDictionaryEntry(entry.getKey())).getOccurence();
			inputNeurons[indexInDictionaryEntry(entry.getKey())] = logisticFunction.calc(normalizedValue);
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
		ArrayList<DictionaryEntry> fileNameDictionary = null;
		ArrayList<DictionaryEntry> fileEndingDictionary = null;
		ArrayList<DictionaryEntry> wordDictionary = null;
		List<Repository> repositorys = null;
		try {
			fileNameDictionary = SerializeHelper.deserialize(Constants.fileNameDictionaryLocation);
			fileEndingDictionary = SerializeHelper.deserialize(Constants.fileEndingDictionaryLocation);
			wordDictionary = SerializeHelper.deserialize(Constants.wordDictionaryLocation);
			repositorys = new InputFileReader(Constants.trainingRepositoryLocation).getRepositorysAndTypes();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (Repository rp : repositorys) {
			CollectedDataSet dataSet = null;
			try {
				dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataSetAll.add(dataSet);
		}
		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.collectedDataSet = dataSetAll;
		configuration.endingDictionary = fileEndingDictionary;
		configuration.fileNameDictionary = fileNameDictionary;
		configuration.wordDictionary = wordDictionary;
		
		new ClassifierNN(dataSetAll, configuration);
	}

}
