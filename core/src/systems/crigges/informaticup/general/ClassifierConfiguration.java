package systems.crigges.informaticup.general;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import systems.crigges.informaticup.io.SerializeHelper;
import systems.crigges.informaticup.nnetwork.RatioDataSet;
import systems.crigges.informaticup.wordanalytics.DictionaryEntry;

public class ClassifierConfiguration {
	public File wordDictionaryLocation;
	public File fileNameDictionaryLocation;
	public File fileEndingDictionaryLocation;
	public File averageRatioValuesLocation;
	public File trainingRepositoryLocation;
	public File testRepositoryLocation;
	public File neuralNetworkLocation;
	
	public double wordDictionaryIntersectionStrength;
	public int wordDictionaryWordCountPerType;
	
	public double wordDictionarylogisticValue;
	public double fileNameDictionaryIntersectionStrength;
	
	public int fileNameDictionaryWordCountPerType;
	public double fileNameDictionarylogisticValue;
	
	public double fileEndingDictionaryIntersectionStrength;
	public int fileEndingDictionaryWordCountPerType;
	
	public boolean recreateDictionary;
	
	public int readmeInfluenceFactor;

	public double fileEndingDictionaryLogisticValue;
	
	public int numberOfNeuronOutput;
	
	public int hiddenLayerNeuronCount;
	
	public double maxError;
	
	public double learningRate;
	
	public double momentum;
	
	public double ratioLogisticValue;
	
	public ArrayList<DictionaryEntry> wordDictionary;
	public ArrayList<DictionaryEntry> fileEndingDictionary;
	public ArrayList<DictionaryEntry> fileNameDictionary;
	public int inputNeuronCount;
	public ArrayList<Double> normRatioValues;
	

	public static ClassifierConfiguration getDefault() throws ClassNotFoundException, IOException{
		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.wordDictionaryLocation = new File("./assets/wordDictionary.ser");
		configuration.fileNameDictionaryLocation = new File("./assets/fileNameDictionary.ser");
		configuration.fileEndingDictionaryLocation = new File("./assets/fileEndingDictionary.ser");
		configuration.averageRatioValuesLocation = new File("./assets/averageRatioValues");
		configuration.trainingRepositoryLocation = new File("./assets/Repositorys.txt");
		configuration.testRepositoryLocation = new File("./assets/TestRepositorys.txt");
		configuration.neuralNetworkLocation = new File("./assets/classifierNN.nnet");
		
		configuration.wordDictionaryIntersectionStrength = 0.25;
		configuration.wordDictionaryWordCountPerType = 10;
		
		configuration.wordDictionarylogisticValue = 0.3;	
		
		configuration.fileNameDictionaryIntersectionStrength = 0.25;
		configuration.fileNameDictionaryWordCountPerType = 150;
		configuration.fileNameDictionarylogisticValue = 0.2;
		
		configuration.fileEndingDictionaryIntersectionStrength = 0.01;
		configuration.fileEndingDictionaryWordCountPerType = 50;
		
		configuration.hiddenLayerNeuronCount = 20;
		configuration.maxError = 0.01;
		configuration.learningRate = 0.1;
		configuration.momentum = 0.2;
		configuration.recreateDictionary = false;

		configuration.fileEndingDictionaryLogisticValue = 0.3;
		
		configuration.numberOfNeuronOutput = 7;
		
		configuration.readmeInfluenceFactor = 10;
		
		configuration.ratioLogisticValue = 0.0005;
		
		configuration.fileNameDictionary = SerializeHelper.deserialize(configuration.fileNameDictionaryLocation);
		configuration.fileEndingDictionary = SerializeHelper.deserialize(configuration.fileEndingDictionaryLocation);
		configuration.wordDictionary = SerializeHelper.deserialize(configuration.wordDictionaryLocation);
		configuration.normRatioValues = SerializeHelper.deserialize(configuration.averageRatioValuesLocation);
		
		configuration.inputNeuronCount = configuration.fileEndingDictionary.size() * 2 + configuration.fileNameDictionary.size() * 2
				+ configuration.wordDictionary.size() * 2 + 2 * RatioDataSet.getDefaultRatioCount();
		return configuration;
	}
	
	public static ClassifierConfiguration getDefaultWithoutDictionaries() throws ClassNotFoundException, IOException{
		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.wordDictionaryLocation = new File("./assets/wordDictionary.ser");
		configuration.fileNameDictionaryLocation = new File("./assets/fileNameDictionary.ser");
		configuration.fileEndingDictionaryLocation = new File("./assets/fileEndingDictionary.ser");
		configuration.averageRatioValuesLocation = new File("./assets/averageRatioValues");
		configuration.trainingRepositoryLocation = new File("./assets/Repositorys.txt");
		configuration.testRepositoryLocation = new File("./assets/TestRepositorys.txt");
		configuration.neuralNetworkLocation = new File("./assets/classifierNN.nnet");
		
		configuration.wordDictionaryIntersectionStrength = 0.25;
		configuration.wordDictionaryWordCountPerType = 100;
		
		configuration.wordDictionarylogisticValue = 0.3;	
		configuration.fileNameDictionaryIntersectionStrength = 0.25;
		
		configuration.fileNameDictionaryWordCountPerType = 10;
		configuration.fileNameDictionarylogisticValue = 0.3;
		
		configuration.fileEndingDictionaryIntersectionStrength = 0.08;
		configuration.fileEndingDictionaryWordCountPerType = 10;
		
		configuration.recreateDictionary = false;

		configuration.hiddenLayerNeuronCount = 15;
		configuration.maxError = 0.001;
		configuration.learningRate = 0.1;
		configuration.momentum = 0.2;
		
		configuration.fileEndingDictionaryLogisticValue = 0.3;
		
		configuration.numberOfNeuronOutput = 7;
		
		configuration.readmeInfluenceFactor = 10;
		
		configuration.ratioLogisticValue = 0.0005;
		
		configuration.normRatioValues = SerializeHelper.deserialize(configuration.averageRatioValuesLocation);
		
		return configuration;
	}
	
	public static ClassifierConfiguration getOnlyDefaultLocations() throws ClassNotFoundException, IOException{
		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.wordDictionaryLocation = new File("./assets/wordDictionary.ser");
		configuration.fileNameDictionaryLocation = new File("./assets/fileNameDictionary.ser");
		configuration.fileEndingDictionaryLocation = new File("./assets/fileEndingDictionary.ser");
		configuration.averageRatioValuesLocation = new File("./assets/averageRatioValues");
		configuration.trainingRepositoryLocation = new File("./assets/Repositorys.txt");
		configuration.testRepositoryLocation = new File("./assets/TestRepositorys.txt");
		configuration.neuralNetworkLocation = new File("./assets/classifierNN.nnet");
		
		return configuration;
	}

}
