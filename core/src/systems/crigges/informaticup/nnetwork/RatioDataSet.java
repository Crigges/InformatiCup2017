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

/**
 * This class generates a dataset of selected Ratios between two Values of a
 * {@link CollectedDataSet}
 * 
 * @author Rami Aly & Andre Schurat
 * @see CollectedDataSet
 */
public class RatioDataSet {

	private ArrayList<Double> ratiosAll = new ArrayList<Double>();
	private CollectedDataSet dataSet;
	private LogisticFunction logistic;

	/**
	 * Creates a new RatioDataSet out of the given {@link CollectedDataSet}, and
	 * List of average Values used by the {@link CollectedDataSet}
	 * 
	 * @param dataSet
	 *            {@link CollectedDataSet}
	 * @param parameter
	 *            for logistic function to normalize ratios
	 * @param normValues
	 */
	public RatioDataSet(CollectedDataSet dataSet, double value, ArrayList<Double> normValues) {
		this.dataSet = dataSet;
		this.logistic = new LogisticFunction(value);
		calculateInput(normValues);
	}

	public RatioDataSet(CollectedDataSet dataSet) {
		this.dataSet = dataSet;
		calculate();
	}

	/**
	 * @return ArrayList<Double> of normalized ratios previously calculated
	 */
	public ArrayList<Double> getNormalizedRatios() {
		return ratiosAll;
	}

	/**
	 * Calculates three different normalized ratios and stores them
	 * 
	 * @param normValues
	 */
	private void calculateInput(ArrayList<Double> normValues) {

		if (dataSet.fileCount > 0) {
			double d = logistic.calc(((double) (dataSet.repoSize) / dataSet.fileCount) / normValues.get(0));
			if (d > 0) {
				ratiosAll.add(0.);
				ratiosAll.add(d);
			} else {
				ratiosAll.add(Math.abs(d));
				ratiosAll.add(0.);
			}
		}
		if (dataSet.fileCount > 0) {
			double d = logistic.calc(((double) dataSet.mediaCount) / dataSet.fileCount / normValues.get(1));
			if (d > 0) {
				ratiosAll.add(0.);
				ratiosAll.add(d);
			} else {
				ratiosAll.add(Math.abs(d));
				ratiosAll.add(0.);
			}
		}
		if (dataSet.totalWordCount > 0) {
			double d = logistic.calc(((double) dataSet.numberCount) / dataSet.totalWordCount / normValues.get(2));
			if (d > 0) {
				ratiosAll.add(0.);
				ratiosAll.add(logistic.calc(d));
			} else {
				ratiosAll.add(Math.abs(d));
				ratiosAll.add(0.);
			}
		}
	}

	private void calculate() {
		Double averageFileSize = 0.;
		ratiosAll.add(averageFileSize);
		Double mediaDensity = 0.;
		ratiosAll.add(mediaDensity);
		Double numberToWordRatio = 0.;
		ratiosAll.add(numberToWordRatio);
	}

	/**
	 * returns the number of selected ratios which will be calculated
	 * 
	 * @return
	 */
	public static int getDefaultRatioCount() {
		CollectedDataSet dataSet = new CollectedDataSet();
		dataSet.fileCount = 1;
		dataSet.mediaCount = 1;
		dataSet.numberCount = 1;
		dataSet.repositoryType = RepositoryTyp.OTHER;
		dataSet.repoSize = 1;
		dataSet.totalWordCount = 1;
		RatioDataSet defaultSet = new RatioDataSet(dataSet);
		return defaultSet.getNormalizedRatios().size();
	}

	/**
	 * Stores average values of used dataSets of Type {@link CollectedDataSet}
	 * from the default trainingset into a File
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @see SerializeHelper
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ClassifierConfiguration config = ClassifierConfiguration.getDefault();
		List<RepositoryDescriptor> repositorys = null;
		WordStatistic ratios = new WordStatistic();
		try {
			repositorys = new InputFileReader(config.trainingRepositoryLocation).getRepositorysAndTypes();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		for (RepositoryDescriptor r : repositorys) {
			try {
				CollectedDataSet dataSet = RepoCacher.get(r.getName()).getCollectedDataSet();
				if (dataSet.fileCount > 0) {
					ratios.add("learningReposAverageSize", (double) (dataSet.repoSize) / dataSet.fileCount);
					ratios.add("learningReposMediaDensity", ((double) dataSet.mediaCount) / dataSet.fileCount);
				}
				if (dataSet.totalWordCount > 0) {
					ratios.add("learningReposNumberToWordRatio",
							((double) dataSet.numberCount) / dataSet.totalWordCount);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<Double> list = Arrays.asList(ratios.getStatistic("learningReposAverageSize"),
				ratios.getStatistic("learningReposMediaDensity"),
				ratios.getStatistic("learningReposNumberToWordRatio"));
		try {
			SerializeHelper.serialize(config.averageRatioValuesLocation, new ArrayList<>(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
