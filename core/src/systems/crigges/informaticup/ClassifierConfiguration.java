package systems.crigges.informaticup;

import java.io.IOException;
import java.util.ArrayList;
import systems.crigges.informaticup.CDictionary.DictionaryEntry;

public class ClassifierConfiguration {
	
	public ArrayList<DictionaryEntry> wordDictionary;
	public ArrayList<DictionaryEntry> endingDictionary;
	public ArrayList<DictionaryEntry> fileNameDictionary;

	
	public static ClassifierConfiguration getDefaultConfiguration() throws ClassNotFoundException, IOException{
		ArrayList<DictionaryEntry> fileNameDictionary = null;
		ArrayList<DictionaryEntry> fileEndingDictionary = null;
		ArrayList<DictionaryEntry> wordDictionary = null;
		fileNameDictionary = SerializeHelper.deserialize(Constants.fileNameDictionaryLocation);
		fileEndingDictionary = SerializeHelper.deserialize(Constants.fileEndingDictionaryLocation);
		wordDictionary = SerializeHelper.deserialize(Constants.wordDictionaryLocation);

		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.endingDictionary = fileEndingDictionary;
		configuration.fileNameDictionary = fileNameDictionary;
		configuration.wordDictionary = wordDictionary;
		return configuration;
	}
}
