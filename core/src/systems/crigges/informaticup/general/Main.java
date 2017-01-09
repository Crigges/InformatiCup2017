package systems.crigges.informaticup.general;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.OutputFileWriter;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.nnetwork.ClassifierConfiguration;
import systems.crigges.informaticup.nnetwork.ClassifierNetwork;

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
		List<RepositoryDescriptor> repositorys;
		if (testRepositoryLocation.exists()) {
			try {
				repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
			} catch (Exception e2) {
				testRepositoryLocation = Constants.testRepositoryLocation;
				repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
			}
		} else {
			testRepositoryLocation = Constants.testRepositoryLocation;
			repositorys = new InputFileReader(testRepositoryLocation).getRepositorysAndTypes();
		}
		ClassifierNetwork neuralNetwork;
		if (Constants.neuralNetworkLocation.exists()) {
			try {
				neuralNetwork = ClassifierNetwork.loadFromFile(Constants.neuralNetworkLocation);
			} catch (Exception e2) {
				neuralNetwork = createNeuralNetwork();
			}
		} else {
			neuralNetwork = createNeuralNetwork();
		}

		try {
			String outputFileName = testRepositoryLocation.getParentFile().getAbsolutePath() + "/"
					+ testRepositoryLocation.getName().substring(0, testRepositoryLocation.getName().indexOf(".")) + "output.txt";
			System.out.println(outputFileName);
			
			OutputFileWriter writer = new OutputFileWriter(new File(outputFileName));
			for (RepositoryDescriptor rp : repositorys) {
				try{
				RepositoryTyp type = neuralNetwork.classify(RepoCacher.get(rp.getName()).getCollectedDataSet());
				rp.setType(type);
				writer.write(rp);
				System.out.println(rp.getName() + " " + type.toString());
				}catch(Exception e){}
			}
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	private static ClassifierNetwork createNeuralNetwork() throws Exception {
		List<RepositoryDescriptor> repositorys = new InputFileReader(Constants.trainingRepositoryLocation)
				.getRepositorysAndTypes();
		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (RepositoryDescriptor rp : repositorys) {
			try {
				CollectedDataSet dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
				dataSet.repositoryType = rp.getTyp();
				dataSetAll.add(dataSet);
			} catch (Exception e2) {}
		}

		return new ClassifierNetwork(dataSetAll, ClassifierConfiguration.getDefaultConfiguration());

	}
}
