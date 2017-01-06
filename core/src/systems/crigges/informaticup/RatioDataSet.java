package systems.crigges.informaticup;

import java.util.ArrayList;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;

public class RatioDataSet {
	private ArrayList<Double> inputNeurons = new ArrayList<Double>();
	private CollectedDataSet dataSet;

	public RatioDataSet(CollectedDataSet dataSet) {
		this.dataSet = dataSet;
		calculateInput();
	}

	public ArrayList<Double> getInputNeurons() {
		return inputNeurons;
	}

	private void calculateInput() {
		double averageFileSize = 0;
		double mediaDensity = 0;
		double subscribeToStaredRatio = 0;
		double numberToWordRatio = 0;

		if (dataSet.fileCount > 0) {
			averageFileSize = (double) (dataSet.repoSize) / dataSet.fileCount;
		}
		if (dataSet.fileCount > 0) {
			mediaDensity = ((double) dataSet.mediaCount) / dataSet.fileCount;
		}
		if (dataSet.staredCount > 0) {
			subscribeToStaredRatio = ((double) dataSet.subscribedCount) / dataSet.staredCount;
		}
		if (dataSet.totalWordCount > 0) {
			numberToWordRatio = ((double) dataSet.numberCount / dataSet.totalWordCount);
		}

		inputNeurons.add(averageFileSize);
		inputNeurons.add(mediaDensity);
		inputNeurons.add(subscribeToStaredRatio);
		inputNeurons.add(numberToWordRatio);
	}

	public static int getDefaultRatioCount() {
		CollectedDataSet dataSet = new CollectedDataSet();
		dataSet.fileCount = 1;
		dataSet.mediaCount = 1;
		dataSet.numberCount = 1;
		dataSet.repositoryType = RepositoryTyp.OTHER;
		dataSet.repoSize = 1;
		dataSet.staredCount = 1;
		dataSet.subscribedCount = 1;
		dataSet.totalWordCount = 1;
		RatioDataSet defaultSet = new RatioDataSet(dataSet);
		return defaultSet.getInputNeurons().size();
	}

}
