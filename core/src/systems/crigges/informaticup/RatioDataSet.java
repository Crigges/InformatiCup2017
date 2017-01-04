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
			double averageFileSize = ((double) dataSet.repoSize) / dataSet.fileCount;
			double mediaDensity = ((double) dataSet.mediaCount) / dataSet.fileCount;
			double subscribeToStaredRatio = ((double) dataSet.subscribedCount) / dataSet.staredCount;

			inputNeurons.add(averageFileSize);
			inputNeurons.add(mediaDensity);
			inputNeurons.add(subscribeToStaredRatio);
	}

}
