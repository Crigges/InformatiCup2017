package systems.crigges.informaticup.general;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.nnetwork.ClassifierNetwork;

public class NetworkTester {
	
	public static void main(String[] args) throws Exception {
		ClassifierConfiguration config = ClassifierConfiguration.getDefault();
		List<RepositoryDescriptor> repositorys = new InputFileReader(new File("./assets/TrainingRepositoriesSet2.txt"))
				.getRepositorysAndTypes();
		Set<CollectedDataSet> trainingSet = new HashSet<>();
		for (RepositoryDescriptor rp : repositorys) {
			try{
				CollectedDataSet dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
				dataSet.repositoryType = rp.getTyp();
				trainingSet.add(dataSet);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		config.hiddenLayerNeuronCount = 39;
		config.maxError = 0.00022;
		config.learningRate = 0.3;
		
		config.wordDictionaryLogisticValue = 0.52;
		config.fileNameDictionaryLogisticValue = 0.004;
		config.fileEndingDictionaryLogisticValue = 2.5;
		
		//config.wordDictionary = new ArrayList<>();
		
		List<RepositoryDescriptor> goldenData = new InputFileReader(
				new File("./assets/CombinedTestRepositories.txt")).getRepositorysAndTypes();
		Set<CollectedDataSet> evalSet = new HashSet<>();
		for (RepositoryDescriptor rp : goldenData) {
			CollectedDataSet dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
			dataSet.repositoryType = rp.getTyp();
			evalSet.add(dataSet);
		}
		for(int i = 0; i < 30; i++) {
			ClassifierNetwork network = new ClassifierNetwork(trainingSet, config);
			int rights = 0;
			for(CollectedDataSet des : evalSet){
				if(network.classify(des) == des.repositoryType){
					rights++;
				}
			}
			System.out.println("File Ending Dictionary Logistic Value:" + config.fileEndingDictionaryLogisticValue + " " + rights + " / " + evalSet.size());
			config.fileEndingDictionaryLogisticValue *= 0.8;
		}
	}


}
