package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;
import systems.crigges.informaticup.InputFileReader.Repository;

public class Main {

	public static void main(String[] args) throws Exception {
		File testRepositoryLocation = null;
		if (args.length > 1) {
			System.out.println("Invalid Input");
			return;
		} else if (args.length == 0) {
			testRepositoryLocation = Constants.testRepositoryLocation;
		} else {
			testRepositoryLocation = new File(args[0]);
		}
		ClassifierNN neuralNetwork;
		if (testRepositoryLocation.exists()) {
			try {
				neuralNetwork = SerializeHelper.deserialize(Constants.neuralNetworkLocation);
			} catch (Exception e2) {
				neuralNetwork = createNeuralNetwork();
			}
		} else {
			neuralNetwork = createNeuralNetwork();
		}

		try {
			File inputFile = Constants.trainingRepositoryLocation;
			String outputFileName = inputFile.getParentFile().getAbsolutePath() + "/"
					+ inputFile.getName().substring(0, inputFile.getName().indexOf(".")) + "output.txt";
			System.out.println(outputFileName);
			List<Repository> repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
			OutputFileWriter writer = new OutputFileWriter(new File(outputFileName));
			for (Repository rp : repositorys) {
				RepositoryTyp type = neuralNetwork.classify(RepoCacher.get(rp.getName()).getCollectedDataSet());
				writer.write(rp.getName(), type.toString());
				System.out.println(rp.getName() + " " + type.toString());
			}
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	private static ClassifierNN createNeuralNetwork() throws Exception {
		ArrayList<DictionaryEntry> fileNameDictionary = SerializeHelper
				.deserialize(Constants.fileNameDictionaryLocation);
		ArrayList<DictionaryEntry> fileEndingDictionary = SerializeHelper
				.deserialize(Constants.fileEndingDictionaryLocation);
		ArrayList<DictionaryEntry> wordDictionary = SerializeHelper.deserialize(Constants.wordDictionaryLocation);
		List<Repository> repositorys = new InputFileReader(Constants.trainingRepositoryLocation)
				.getRepositorysAndTypes();
		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (Repository rp : repositorys) {
			try {
				CollectedDataSet dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
				dataSetAll.add(dataSet);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.collectedDataSet = dataSetAll;
		configuration.endingDictionary = fileEndingDictionary;
		configuration.fileNameDictionary = fileNameDictionary;
		configuration.wordDictionary = wordDictionary;

		return new ClassifierNN(dataSetAll, configuration);

	}
}
