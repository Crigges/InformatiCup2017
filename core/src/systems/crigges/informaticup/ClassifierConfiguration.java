package systems.crigges.informaticup;

import java.util.ArrayList;
import java.util.Set;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;

public class ClassifierConfiguration {
	
	public ArrayList<DictionaryEntry> wordDictionary;
	public ArrayList<DictionaryEntry> endingDictionary;
	public ArrayList<DictionaryEntry> fileNameDictionary;
	public Set<CollectedDataSet> collectedDataSet;
}
