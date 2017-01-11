package systems.crigges.informaticup.general;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.OutputFileWriter;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.nnetwork.ClassifierNetwork;

public class DifferentNeuralNetworkTests {

	private static final int testSize = 5;

	public static void main(String[] args) throws Exception {
		int[] hiddenNeuronsTestSet = { 8, 12, 16, 20, 24 };
		double[] learningSpeedTestSet = { 0, 01, 0.1, 0.3, 0.5, 0.7 };
		double[] maxErrorTestSet = { 0.1, 0.07, 0.01, 0.005, 0.003 };
		double[] momentumTestSet = { 0.1, 0.3, 0.5, 0.7, 0.9 };
		double[] wordDictionaryIntersectionStrengthTestSet = { 0.1, 0.25, 0.4, 0.5, 0.7 };
		double[] fileNameIntersectionStrengthTestSet = { 0.1, 0.25, 0.4, 0.5, 0.7 };
		double[] fileEndingIntersectionStrengthTestSet = { 0.1, 0.25, 0.4, 0.5, 0.7 };
		double[] fileNameDictionaryWordCountPerTypeTestSet = { 10, 20, 30, 40, 50 };
		double[] wordDictionaryDictionaryWordCountPerTypeTestSet = { 10, 20, 30, 40, 50 };
		double[] fileEndingDictionaryWordCountPerTypeTestSet = { 10, 20, 30, 40, 50 };
		double[] wordDictionarylogisticValueTestSet = { 0.05, 0.1, 0.3, 0.5, 0.8 };
		double[] fileEndinglogisticValueTestSet = { 0.05, 0.1, 0.3, 0.5, 0.8 };
		double[] fileNamelogisticValueTestSet = { 0.05, 0.1, 0.3, 0.5, 0.8 };
		double[] ratiologisticValueTestSet = { 0.05, 0.1, 0.3, 0.5, 0.8 };
		ClassifierConfiguration config = ClassifierConfiguration.getOnlyDefaultLocations();
		
		for (int i = 0; i < 5; i++) {

		}

		File testRepositoryLocation = null;
		if (args.length > 1) {
			System.out.println("Invalid Input");
			return;
		} else if (args.length == 0) {
			testRepositoryLocation = config.testRepositoryLocation;
		} else {
			testRepositoryLocation = new File(args[0]);
		}
		List<RepositoryDescriptor> repositorys;
		if (testRepositoryLocation.exists()) {
			try {
				repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
			} catch (Exception e2) {
				testRepositoryLocation = config.testRepositoryLocation;
				repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
			}
		} else {
			testRepositoryLocation = config.testRepositoryLocation;
			repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
		}
		ClassifierNetwork neuralNetwork;
		if (config.neuralNetworkLocation.exists()) {
			try {
				neuralNetwork = ClassifierNetwork.loadFromFile(config.neuralNetworkLocation);
			} catch (Exception e2) {
				neuralNetwork = createNeuralNetwork(config);
			}
		} else {
			neuralNetwork = createNeuralNetwork(config);
		}

		try {
			String outputFileName = testRepositoryLocation.getParentFile().getAbsolutePath() + "/"
					+ testRepositoryLocation.getName().substring(0, testRepositoryLocation.getName().indexOf("."))
					+ "output.txt";
			System.out.println(outputFileName);

			OutputFileWriter writer = new OutputFileWriter(new File(outputFileName));
			for (RepositoryDescriptor rp : repositorys) {
				try {
					RepositoryTyp type = neuralNetwork.classify(RepoCacher.get(rp.getName()).getCollectedDataSet());
					rp.setType(type);
					writer.write(rp);
					System.out.println(rp.getName() + " " + type.toString());
				} catch (Exception e) {
				}
			}
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	private static ClassifierNetwork createNeuralNetwork(ClassifierConfiguration config) throws Exception {
		List<RepositoryDescriptor> repositorys = new InputFileReader(config.trainingRepositoryLocation)
				.getRepositorysAndTypes();
		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (RepositoryDescriptor rp : repositorys) {
			try {
				CollectedDataSet dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
				dataSet.repositoryType = rp.getTyp();
				dataSetAll.add(dataSet);
			} catch (Exception e2) {
			}
		}

		return new ClassifierNetwork(dataSetAll, config);

	}
}
