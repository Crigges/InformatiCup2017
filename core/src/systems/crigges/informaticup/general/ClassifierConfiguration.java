package systems.crigges.informaticup.general;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

import systems.crigges.informaticup.io.SerializeHelper;
import systems.crigges.informaticup.nnetwork.LogisticFunction;
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

	/**
	 * Defines the intersection strength between all repositories of the same
	 * type that's needed for a word to be kept for further analysis for the
	 * word count.
	 * 
	 */
	public double wordDictionaryIntersectionStrength;

	
	/**
	 * Words per {@link RepositoryTyp} included into the dictionary for the word
	 * count. Duplicates does count.
	 */
	public int wordDictionaryWordCountPerType;

	/**
	 * constant of proportionality k for {@link LogisticFunction} to normalize
	 * values for dictionary of word count.
	 */
	public double wordDictionarylogisticValue;

	/**
	 * Defines the intersection strength between all repositories of the same
	 * type that's needed for a word to be kept for further analysis for the
	 * file- and foldernames dictionary
	 */
	public double fileNameDictionaryIntersectionStrength;

	/**
	 * Words per {@link RepositoryTyp} included into the dictionary for the
	 * file- and foldernames. Duplicates does count.
	 */
	public int fileNameDictionaryWordCountPerType;

	/**
	 * constant of proportionality k for {@link LogisticFunction} to normalize
	 * values for dictionary of file- and foldernames.
	 */
	public double fileNameDictionarylogisticValue;

	/**
	 * Defines the intersection strength between all repositories of the same
	 * type that's needed for a word to be kept for further analysis for the
	 * file- and folderending Dictionary
	 */
	public double fileEndingDictionaryIntersectionStrength;

	/**
	 * Words per {@link RepositoryTyp} included into the dictionary for file-
	 * and folderending. Duplicates does count.
	 */
	public int fileEndingDictionaryWordCountPerType;

	/**
	 * constant of proportionality k for {@link LogisticFunction} to normalize
	 * values for dictionary of file- and folderending.
	 */
	public double fileEndingDictionaryLogisticValue;

	/**
	 * recreate every Dictionary for next program start
	 */
	public boolean recreateDictionary;

	/**
	 * recreate NeuralNetwork for next program start
	 */
	public boolean recreateNeuralNetwork;
	/**
	 * the weight of every word in the readme file for the dictionaries.
	 */
	public int readmeInfluenceFactor;

	/**
	 * number of output neurons
	 */
	public int numberOfNeuronOutput;

	/**
	 * number of neurons for the hidden-Layer
	 */
	public int hiddenLayerNeuronCount;

	/**
	 * the minimal deviation between calculated and expected value to finish
	 * training
	 */
	public double maxError;

	/**
	 * the factor the weights of the edges between neurons change with each iteration of training
	 */
	public double learningRate;

	/**
	 * the additional factor to the learningRate to minimize probability of local minimum
	 */
	public double momentum;

	/**
	 *  * constant of proportionality k for {@link LogisticFunction} to normalize
	 * values for the ratios of {@link CollectedDataSet}
	 */
	public double ratioLogisticValue;

	/**
	 * Dictionary for words, file- and folderendings and file- and foldernames
	 */
	public ArrayList<DictionaryEntry> wordDictionary;
	public ArrayList<DictionaryEntry> fileEndingDictionary;
	public ArrayList<DictionaryEntry> fileNameDictionary;
	
	/**
	 * count of input neurons
	 */
	public int inputNeuronCount;
	
	/**
	 * Average values of every Training-Repository in {@link CollectedDataSet}
	 */
	public ArrayList<Double> normRatioValues;

	/**
	 * the default values for every parameter
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static ClassifierConfiguration getDefault() throws ClassNotFoundException, IOException {
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
		configuration.fileEndingDictionaryLogisticValue = 0.3;
		
		configuration.hiddenLayerNeuronCount = 10;
		configuration.maxError = 0.01;
		configuration.learningRate = 0.03;
		configuration.momentum = 0.2;
		configuration.recreateDictionary = false;
		configuration.recreateNeuralNetwork = false;



		configuration.numberOfNeuronOutput = 7;

		configuration.readmeInfluenceFactor = 10;

		configuration.ratioLogisticValue = 0.0005;

		configuration.fileNameDictionary = SerializeHelper.deserialize(configuration.fileNameDictionaryLocation);
		configuration.fileEndingDictionary = SerializeHelper.deserialize(configuration.fileEndingDictionaryLocation);
		configuration.wordDictionary = SerializeHelper.deserialize(configuration.wordDictionaryLocation);
		configuration.normRatioValues = SerializeHelper.deserialize(configuration.averageRatioValuesLocation);

		configuration.inputNeuronCount = (configuration.fileEndingDictionary.size()
				+ configuration.fileNameDictionary.size()  + configuration.wordDictionary.size()
				+ RatioDataSet.getDefaultRatioCount()) * 2;
		return configuration;
	}

	/**
	 * {@link ClassifierConfiguration} with every default parameter but without any {@link Dictionary's
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @see Dictionary
	 */
	public static ClassifierConfiguration getDefaultWithoutDictionaries() throws ClassNotFoundException, IOException {
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
		configuration.recreateNeuralNetwork = false;
		
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

	/**
	 * {@link ClassifierConfiguration} only sets default value for locations of files
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static ClassifierConfiguration getOnlyDefaultLocations() throws ClassNotFoundException, IOException {
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
