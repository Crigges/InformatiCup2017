package systems.crigges.informaticup.nnetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import systems.crigges.informaticup.general.CollectedDataSet;
import systems.crigges.informaticup.general.ClassifierConfiguration;
import systems.crigges.informaticup.general.RepositoryDescriptor;
import systems.crigges.informaticup.general.RepositoryTyp;
import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.io.SerializeHelper;
import systems.crigges.informaticup.wordanalytics.WordStatistic;

public class RatioDataSet {

	private ArrayList<Double> inputNeurons = new ArrayList<Double>();
	private CollectedDataSet dataSet;
	private LogisticFunction logistic;

	public RatioDataSet(CollectedDataSet dataSet, double value, ArrayList<Double> normValues) {
		this.dataSet = dataSet;
		this.logistic = new LogisticFunction(value);
		calculateInput(normValues);
	}

	public RatioDataSet(CollectedDataSet dataSet) {
		this.dataSet = dataSet;
		calculate();
	}

	public ArrayList<Double> getInputNeurons() {
		return inputNeurons;
	}

	private void calculateInput(ArrayList<Double> normValues) {
		// double averageFileSize = 0;
		// double mediaDensity = 0;
		// // double subscribeToStaredRatio = 0;
		// double numberToWordRatio = 0;

		if (dataSet.fileCount > 0) {
			double d = logistic.calc(((double) (dataSet.repoSize) / dataSet.fileCount) / normValues.get(0));
			if (d > 0) {
				inputNeurons.add(0.);
				inputNeurons.add(d);
			} else {
				inputNeurons.add(Math.abs(d));
				inputNeurons.add(0.);
			}
		}
System.out.println( normValues.get(0) + " " +  normValues.get(1) + " " +  normValues.get(2));
		if (dataSet.fileCount > 0) {
			double d = logistic.calc(((double) dataSet.mediaCount) / dataSet.fileCount / normValues.get(1));
			if (d > 0) {
				inputNeurons.add(0.);
				inputNeurons.add(d);
			} else {
				inputNeurons.add(Math.abs(d));
				inputNeurons.add(0.);
			}
		}
		// if (dataSet.staredCount > 0) {
		// subscribeToStaredRatio = ((double) dataSet.subscribedCount) /
		// dataSet.staredCount;
		// System.out.println(subscribeToStaredRatio);
		// }
		if (dataSet.totalWordCount > 0) {
			double d = logistic.calc(((double) dataSet.numberCount) / dataSet.totalWordCount / normValues.get(2));
			if (d > 0) {
				inputNeurons.add(0.);
				inputNeurons.add(
						logistic.calc(d));
			} else {
				inputNeurons.add(Math
						.abs(d));
				inputNeurons.add(0.);
			}
		}

		// inputNeurons.add(averageFileSize);
		// inputNeurons.add(mediaDensity);
		// inputNeurons.add(subscribeToStaredRatio);
		// inputNeurons.add(numberToWordRatio);

	}

	private void calculate() {
		Double averageFileSize = 0.;
		inputNeurons.add(averageFileSize);
		Double mediaDensity = 0.;
		inputNeurons.add(mediaDensity);
		Double numberToWordRatio = 0.;
		inputNeurons.add(numberToWordRatio);
	}

	public static int getDefaultRatioCount() {
		CollectedDataSet dataSet = new CollectedDataSet();
		dataSet.fileCount = 1;
		dataSet.mediaCount = 1;
		dataSet.numberCount = 1;
		dataSet.repositoryType = RepositoryTyp.OTHER;
		dataSet.repoSize = 1;
		dataSet.totalWordCount = 1;
		RatioDataSet defaultSet = new RatioDataSet(dataSet);
		System.out.println(defaultSet.getInputNeurons().size());
		return defaultSet.getInputNeurons().size();
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ClassifierConfiguration config = ClassifierConfiguration.getDefault();
		List<RepositoryDescriptor> repositorys = null;
		WordStatistic ratios = new WordStatistic();
		try {
			repositorys = new InputFileReader(config.trainingRepositoryLocation).getRepositorysAndTypes();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (RepositoryDescriptor r : repositorys) {
			try {
				CollectedDataSet dataSet = RepoCacher.get(r.getName()).getCollectedDataSet();
				if (dataSet.fileCount > 0) {
					ratios.add("learningReposAverageSize", (double) (dataSet.repoSize) / dataSet.fileCount);
				}
				if (dataSet.fileCount > 0) {
					ratios.add("learningReposMediaDensity", ((double) dataSet.mediaCount) / dataSet.fileCount);
				}
				// if (dataSet.staredCount > 0) {
				// System.out.println(dataSet.subscribedCount);
				// ratios.add("learningReposSubscribeToStaredRatio",
				// ((double) dataSet.subscribedCount) / dataSet.staredCount);
				// }
				if (dataSet.totalWordCount > 0) {
					ratios.add("learningReposNumberToWordRatio",
							((double) dataSet.numberCount) / dataSet.totalWordCount);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Double> list = Arrays.asList(ratios.getStatistic("learningReposAverageSize"),
				ratios.getStatistic("learningReposMediaDensity"),
				// ratios.getStatistic("learningReposSubscribeToStaredRatio"),
				ratios.getStatistic("learningReposNumberToWordRatio"));
		try {
			SerializeHelper.serialize(config.averageRatioValuesLocation, new ArrayList<>(list));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
