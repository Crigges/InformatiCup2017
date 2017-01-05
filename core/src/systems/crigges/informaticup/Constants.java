package systems.crigges.informaticup;

import java.io.File;

public class Constants {
	
	public static final double wordDictionaryIntersectionStrength = 0.25;
	public static final int wordDictionaryWordCountPerType = 30;
	public static final File wordDictionaryLocation = new File("./assets/wordDictionary.ser");
	
	public static final double fileNameDictionaryIntersectionStrength = 0.25;
	public static final int fileNameDictionaryWordCountPerType = 30;
	public static final File fileNameDictionaryLocation = new File("./assets/fileNameDictionary.ser");
	
	public static final double fileEndingDictionaryIntersectionStrength = 0.08;
	public static final int fileEndingDictionaryWordCountPerType = 30;
	public static final File fileEndingDictionaryLocation = new File("./assets/fileEndingDictionary.ser");

	
	public static final int numberOfConsideredPropotions = 3;
	public static final int numberOfWordCountTypes = 3;
	
	public static final int numberOfNeuronOutput = 7;
	
	public static final File trainingRepositoryLocation = new File("./assets/Repositorys.txt");
	public static final File testRepositoryLocation = new File("./assets/TestRepositorys.txt");
	public static final File neuralNetworkLocation = new File("./assets/classifierNN.nnet");
	

}
