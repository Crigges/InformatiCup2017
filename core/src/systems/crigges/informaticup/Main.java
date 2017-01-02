package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import systems.crigges.informaticup.InputFileReader.Repository;

public class Main {

	private static final String repositoryInputPath = "assets\\Repositorys.txt";
	private static final String testRepositoryInputPath = "assets\\TestRepositorys.txt";
	private static final String dictionaryInputPath = "assets\\dictionary.ser";
	private static final String neuralNetworkInputPath = "assets\\neuralNetwork.nn";

	public static void main(String[] args) throws Exception {
		String testRepositoryInputPath = null;
		if(args.length > 1){
			System.out.println("Invalid Input");
			return;
		}else if(args.length == 0){
			testRepositoryInputPath = Main.testRepositoryInputPath;	
		}else{
			testRepositoryInputPath = args[0];
		}
		ClassifierNN neuralNetwork;
		File fileNN = new File(neuralNetworkInputPath);
		if(fileNN.exists()){
			try {
				neuralNetwork = SerializeHelper.deserialize(neuralNetworkInputPath);
			} catch (Exception e2) {
				neuralNetwork = createNeuralNetwork();
			}
		}else{
			neuralNetwork = createNeuralNetwork();
		}
		
		try{
			File inputFile = new File(testRepositoryInputPath);
			String outputFileName = inputFile.getParentFile().getAbsolutePath() + "/" + inputFile.getName().substring(0, inputFile.getName().indexOf(".")) + "output.txt";
			List<Repository> repositorys = new InputFileReader(new File(testRepositoryInputPath)).getRepositorysAndTypes();
			OutputFileWriter writer = new OutputFileWriter(new File(outputFileName));
			for (Repository rp : repositorys) {
				RepositoryTyp type = neuralNetwork.classify(RepoCacher.get(rp.getName()).getCollectedDataSet());
				writer.write(rp.getName(), type.toString());
				System.out.println(rp.getName() + " " + type.toString());
			}
			writer.close();
		}catch(Exception e2){
			System.out.println("Could not find File for TestRepositorys");
		}
		
	}

	private static ClassifierNN createNeuralNetwork() throws Exception {
		ArrayList<String> dictionary = SerializeHelper.deserialize(dictionaryInputPath);
		List<Repository> repositorys = new InputFileReader(new File(repositoryInputPath)).getRepositorysAndTypes();
		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (Repository rp : repositorys) {
			dataSetAll.add(RepoCacher.get(rp.getName()).getCollectedDataSet());
		}
		return new ClassifierNN(dataSetAll, dictionary);

	}
}
