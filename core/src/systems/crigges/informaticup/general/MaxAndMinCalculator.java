package systems.crigges.informaticup.general;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neuroph.core.data.DataSet;

import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.io.SerializeHelper;
import systems.crigges.informaticup.nnetwork.InputDataFormatter;
import systems.crigges.informaticup.nnetwork.RatioDataSet;

public class MaxAndMinCalculator {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ClassifierConfiguration config = ClassifierConfiguration.getDefault();
		List<RepositoryDescriptor> repositorys = null;
		try {
			repositorys = new InputFileReader(config.trainingRepositoryLocation).getRepositorysAndTypes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (RepositoryDescriptor rp : repositorys) {
			CollectedDataSet dataSet = null;

			try {
				dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
				dataSet.repositoryType = rp.getTyp();
				dataSetAll.add(dataSet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		double[] max = new double[config.inputNeuronCount];
		double[] min = new double[config.inputNeuronCount];

		for (CollectedDataSet dataSet : dataSetAll) {
			RatioDataSet ratioDataSet = new RatioDataSet(dataSet);
			InputDataFormatter formattedInputWords = new InputDataFormatter(dataSet.wordCount, config.wordDictionary);
			InputDataFormatter formattedInputEnding = new InputDataFormatter(dataSet.endingCount,
					config.fileEndingDictionary);
			InputDataFormatter formattedInputFolder = new InputDataFormatter(dataSet.fileNameCount,
					config.fileNameDictionary);

			int countMax = 0;

			double[] list = new double[ratioDataSet.getNormalizedRatios().size()];
			for (int i = 0; i < ratioDataSet.getNormalizedRatios().size(); i++) {
				Double d = ratioDataSet.getNormalizedRatios().get(i);
				list[i] = d.doubleValue();
				// System.out.println(d.doubleValue());
			}
			addDoublesToArrayMax(max, list, countMax);
			addDoublesToArrayMax(max, formattedInputWords.getInputNeurons(), countMax += list.length);
			addDoublesToArrayMax(max, formattedInputEnding.getInputNeurons(),
					countMax += formattedInputWords.getInputNeurons().length);
			addDoublesToArrayMax(max, formattedInputFolder.getInputNeurons(),
					countMax += formattedInputEnding.getInputNeurons().length);

		}

		for (CollectedDataSet dataSet : dataSetAll) {
			int countMin = 0;
			RatioDataSet ratioDataSet = new RatioDataSet(dataSet);
			InputDataFormatter formattedInputWords = new InputDataFormatter(dataSet.wordCount, config.wordDictionary);
			InputDataFormatter formattedInputEnding = new InputDataFormatter(dataSet.endingCount,
					config.fileEndingDictionary);
			InputDataFormatter formattedInputFolder = new InputDataFormatter(dataSet.fileNameCount,
					config.fileNameDictionary);

			double[] list = new double[ratioDataSet.getNormalizedRatios().size()];
			for (int i = 0; i < ratioDataSet.getNormalizedRatios().size(); i++) {
				Double d = ratioDataSet.getNormalizedRatios().get(i);
				list[i] = d.doubleValue();
				// System.out.println(d.doubleValue());
			}

			addDoublesToArrayMin(min, list, countMin);
			addDoublesToArrayMin(min, formattedInputWords.getInputNeurons(), countMin += list.length);
			addDoublesToArrayMin(min, formattedInputEnding.getInputNeurons(),
					countMin += formattedInputWords.getInputNeurons().length);
			addDoublesToArrayMin(min, formattedInputFolder.getInputNeurons(),
					countMin += formattedInputEnding.getInputNeurons().length);
		}

		double[][] output = new double[2][config.inputNeuronCount];
		output[0] = min;
		output[1] = max;

		for (int i = 0; i < max.length; i++) {
			System.out.println(output[0][i] + " " + output[1][i]);
		}

		SerializeHelper.serialize(config.minMaxLocation, output);

	}

	private static double[] addDoublesToArrayMax(double[] input, double[] array, int startIndex) {
		for (double d : array) {
			if (d > input[startIndex]) {
				input[startIndex] = d;
				startIndex++;
			} else {
				startIndex++;
			}
		}
		return input;
	}

	private static double[] addDoublesToArrayMin(double[] input, double[] array, int startIndex) {
		for (double d : array) {
			if (d < input[startIndex]) {
				input[startIndex] = d;
				startIndex++;
			} else {
				startIndex++;
			}
		}
		return input;
	}
}
