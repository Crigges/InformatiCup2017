package systems.crigges.informaticup.general;

import java.io.File;
import java.io.IOException;
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
		config.hiddenLayerNeuronCount = 29;
		config.maxError = 0.0085;
		
		config.wordDictionaryLogisticValue = 3.0;
		config.fileNameDictionaryLogisticValue = 0.004;
		config.fileEndingDictionaryLogisticValue = 0.0085;
		
		List<RepositoryDescriptor> goldenData = new InputFileReader(
				new File("./assets/RightTestRepositorys.txt")).getRepositorysAndTypes();
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
			System.out.println("Word Dictionary Logistic Value:" + config.wordDictionaryLogisticValue + " " + rights + " / " + evalSet.size());
			config.wordDictionaryLogisticValue *= 0.85;
		}
	}


}
