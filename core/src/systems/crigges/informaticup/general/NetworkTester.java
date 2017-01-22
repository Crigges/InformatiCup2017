package systems.crigges.informaticup.general;

import java.io.File;
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
		config.maxError = 0.01;
		config.learningRate = 0.1;
		
		config.wordDictionaryLogisticValue = 0.45599;
		config.fileNameDictionaryLogisticValue = 3.206;
		config.fileEndingDictionaryLogisticValue = 6.14125;
		
//		ClassifierConfiguration dicConfig = ClassifierConfiguration.getOnlyDefaultLocations();
//		dicConfig.wordDictionaryIntersectionStrength = 0.02;
//		dicConfig.wordDictionaryWordCountPerType = 350;
//		dicConfig.wordDictionaryLogisticValue = 0.013;
//		
//		dicConfig.fileNameDictionaryIntersectionStrength = 0.042949;
//		dicConfig.fileNameDictionaryWordCountPerType = 350;
//		dicConfig.fileNameDictionaryLogisticValue = 0.0126;
//
//		dicConfig.fileEndingDictionaryIntersectionStrength = 0.08;
//		dicConfig.fileEndingDictionaryWordCountPerType = 350;
//		dicConfig.fileEndingDictionaryLogisticValue = 0.01;
		
		List<RepositoryDescriptor> goldenData = new InputFileReader(
				new File("./assets/CombinedTestRepositories.txt")).getRepositorysAndTypes();
		Set<CollectedDataSet> evalSet = new HashSet<>();
		
		for (RepositoryDescriptor rp : goldenData) {
			CollectedDataSet dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
			dataSet.repositoryType = rp.getTyp();
			evalSet.add(dataSet);
		}
		
		
		
		for(int i = 0; i < 10; i++) {
			System.gc();
//			new Dictionary(trainingSet, dicConfig, config);
//			config.inputNeuronCount = (config.fileEndingDictionary.size()
//					+ config.fileNameDictionary.size()  + config.wordDictionary.size()
//					+ RatioDataSet.getDefaultRatioCount()) * 2;
			ClassifierNetwork network = new ClassifierNetwork(trainingSet, config);
			int rights = 0;
			for(CollectedDataSet des : evalSet){
				if(network.classify(des) == des.repositoryType){
					rights++;
				}
			}
			System.out.println("config.fileEndingDictionaryLogisticValue: " + config.fileEndingDictionaryLogisticValue + " " + rights + " / " + evalSet.size());
			config.fileEndingDictionaryLogisticValue *= 0.85;
		}
	}


}
