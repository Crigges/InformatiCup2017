package systems.crigges.informaticup.general;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.OutputFileWriter;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.io.SerializeHelper;
import systems.crigges.informaticup.nnetwork.ClassifierNetwork;
import systems.crigges.informaticup.wordanalytics.DictionaryEntry;

public class Main {

	/**
	 * Loads repository from argument path and classifies each repository and creates an output file for results.
	 * Eventually prints out precision and recall for results
	 * @see InputFileReader
	 * @param path to test repository
	 */
	public static void main(String[] args) {
		ClassifierConfiguration config;
		File testRepositoryLocation = null;
		ClassifierNetwork neuralNetwork = null;
		List<RepositoryDescriptor> repositorys = null;
		try {
			config = ClassifierConfiguration.getDefault();
			testRepositoryLocation = null;
			if (args.length > 1) {
				System.out.println("Invalid Input");
				return;
			} else if (args.length == 0) {
				testRepositoryLocation = config.testRepositoryLocation;
			} else {
				testRepositoryLocation = new File(args[0]);
			}
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
			if (config.neuralNetworkLocation.exists() && !config.recreateNeuralNetwork) {
				try {
					neuralNetwork = ClassifierNetwork.loadFromFile(config.neuralNetworkLocation);
				} catch (Exception e2) {
					neuralNetwork = createNeuralNetwork(config);
				}
			} else {
				neuralNetwork = createNeuralNetwork(config);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		try {
			String outputFileName = testRepositoryLocation.getParentFile().getAbsolutePath() + "/"
					+ testRepositoryLocation.getName().substring(0, testRepositoryLocation.getName().indexOf("."))
					+ "output.txt";
			System.out.println(outputFileName);
			
			HashMap<RepositoryTyp, Integer> evaluationData = new HashMap<>();
			HashMap<RepositoryTyp, Integer> correctData = new HashMap<>();
			List<RepositoryDescriptor> goldenData = new InputFileReader(
					new File("./assets/TestRepositorysEvaluated.txt")).getRepositorysAndTypes();
			int[] goldenValuesForType = new int[7];
			OutputFileWriter writer = new OutputFileWriter(new File(outputFileName));
			for (RepositoryDescriptor rp : repositorys) {
				try {

					RepositoryTyp type = neuralNetwork.classify(RepoCacher.get(rp.getName()).getCollectedDataSet());
					rp.setType(type);
					if (evaluationData.containsKey(type)) {
						evaluationData.put(type, evaluationData.get(type) + 1);
					} else {
						evaluationData.put(type, 1);
					}
					if (correctClassified(rp, goldenData)) {
						System.out.println("correct"  + rp.getName());
						if (correctData.get(rp.getTyp()) != null) {
							correctData.put(rp.getTyp(), correctData.get(rp.getTyp()) + 1);
						} else {
							correctData.put(rp.getTyp(), 1);
						}
					}
					writer.write(rp);
					System.out.println(rp.getName() + " " + type.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (RepositoryDescriptor rd : goldenData) {
				goldenValuesForType[rd.getTypeIndex()]++;
			}
			double[] ausbeute = new double[7];
			int count = 0;
			System.out.println("Ausbeute");
			for (Entry<RepositoryTyp, Integer> entry : correctData.entrySet()) {
				int index = entry.getKey().getValue();
				count += entry.getValue();
				ausbeute[index] = entry.getValue() / goldenValuesForType[index];
				System.out.println(entry.getKey().toString() + " " + (double)(entry.getValue()) / goldenValuesForType[index]);
			}
			System.out.println("Präzision");
			for (Entry<RepositoryTyp, Integer> entry : correctData.entrySet()) {
				int index = entry.getKey().getValue();
				ausbeute[index] = entry.getValue() / goldenValuesForType[index];
				System.out.println(entry.getKey().toString() + " " + (double)(entry.getValue()) / evaluationData.get(entry.getKey()));
			}
			
			System.out.println("Overall " + count + " classifications correct" +" so ," + ((double) (count)/ 31));
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	private static boolean correctClassified(RepositoryDescriptor classified, List<RepositoryDescriptor> golden) {
		for (RepositoryDescriptor rep : golden) {
			if (classified.getTyp().equals(rep.getTyp()) & classified.getName().equals(rep.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * creates new NeuralNetwork with values set in {@link ClassifierConfiguration}
	 * Uses default Configuration of {@link ClassifierConfiguration}
	 * @param config
	 * @return
	 * @throws Exception
	 */
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
		ClassifierConfiguration conf = ClassifierConfiguration.getDefault();
		ArrayList<DictionaryEntry> list = SerializeHelper.deserialize(conf.fileNameDictionaryLocation);
		for (DictionaryEntry entry : list) {
			System.out.println(entry.getWord());
		}
		return new ClassifierNetwork(dataSetAll, ClassifierConfiguration.getDefault());

	}
}
