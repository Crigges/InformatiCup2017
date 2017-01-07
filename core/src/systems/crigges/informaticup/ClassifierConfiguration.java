package systems.crigges.informaticup;

import java.io.IOException;
import java.util.ArrayList;
import systems.crigges.informaticup.CDictionary.DictionaryEntry;

public class ClassifierConfiguration {
	
	public ArrayList<DictionaryEntry> wordDictionary;
	public ArrayList<DictionaryEntry> endingDictionary;
	public ArrayList<DictionaryEntry> fileNameDictionary;
	public int inputNeuronCount;

	
	public static ClassifierConfiguration getDefaultConfiguration() throws ClassNotFoundException, IOException{
		ArrayList<DictionaryEntry> fileNameDictionary = null;
		ArrayList<DictionaryEntry> fileEndingDictionary = null;
		ArrayList<DictionaryEntry> wordDictionary = null;
		fileNameDictionary = SerializeHelper.deserialize(Constants.fileNameDictionaryLocation);
		fileEndingDictionary = SerializeHelper.deserialize(Constants.fileEndingDictionaryLocation);
		wordDictionary = SerializeHelper.deserialize(Constants.wordDictionaryLocation);
		int inputNeuronCount = fileEndingDictionary.size() * 2 + fileNameDictionary.size() * 2
				+ wordDictionary.size() * 2 + RatioDataSet.getDefaultRatioCount();;

		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.endingDictionary = fileEndingDictionary;
		configuration.fileNameDictionary = fileNameDictionary;
		configuration.wordDictionary = wordDictionary;
		configuration.inputNeuronCount = inputNeuronCount;
		return configuration;
	}
}
