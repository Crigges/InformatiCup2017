package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import systems.crigges.informaticup.InputFileReader.Repository;

public class ClassifierNN {

	private ClassifierConfiguration configuration;
	private DataSet trainingSet;
	private MultiLayerPerceptron neuralNetwork;
	
	private int hiddenLayerNeuronCount = 9;
	private double maxError = 0.005;
	private double learningRate = 0.1;
	private final double momentum = 0.2;

	public ClassifierNN(Set<CollectedDataSet> trainDataSet, ClassifierConfiguration configuration) {
		this.configuration = configuration;
		createNeuronStructure();

		trainingSet = new DataSet(configuration.inputNeuronCount, Constants.numberOfNeuronOutput);
		for (CollectedDataSet dataSet : trainDataSet) {
			trainNetwork(dataSet);
		}
		System.out.println("Creating Training-Data Finished");
		BackPropagation learningRule = neuralNetwork.getLearningRule();
		neuralNetwork.learnInNewThread(trainingSet);
		int i = learningRule.getCurrentIteration();
		while (neuralNetwork.getLearningThread().isAlive()) {
			if (learningRule.getCurrentIteration() != i) {
				System.out.println(neuralNetwork.getLearningRule().getCurrentIteration() + " "
						+ neuralNetwork.getLearningRule().getPreviousEpochError());
				i = learningRule.getCurrentIteration();
			}
		}

		neuralNetwork.save(Constants.neuralNetworkLocation.getAbsolutePath());
	}
	
	public ClassifierNN(MultiLayerPerceptron neuralNetwork, ClassifierConfiguration configuration){
		this.neuralNetwork = neuralNetwork;
		this.configuration = configuration;
	}

	private void createNeuronStructure() {
		neuralNetwork = new MultiLayerPerceptron(
				Arrays.asList(configuration.inputNeuronCount, hiddenLayerNeuronCount, Constants.numberOfNeuronOutput),
				TransferFunctionType.SIGMOID);
		MomentumBackpropagation bp = new MomentumBackpropagation();
		bp.setLearningRate(learningRate);
		bp.setMaxError(maxError);
		bp.setMomentum(momentum);
		neuralNetwork.setLearningRule(bp);
	}

	private void trainNetwork(CollectedDataSet dataSet) {
		double[] input = getFormattedInput(dataSet);
		double[] output = new double[7];
		output[dataSet.repositoryType.getValue()] = 1.0;
		trainingSet.addRow(new DataSetRow(input, output));
	}

	private double[] addDoublesToArray(double[] input, double[] array, int startIndex) {
		for (double d : array) {
			input[startIndex++] = d;
		}
		return input;
	}

	private double[] getFormattedInput(CollectedDataSet dataSet) {
		RatioDataSet ratioDataSet = new RatioDataSet(dataSet, Constants.ratioLogisticValue, configuration.normRatioValues);
		InputDataFormatter formattedInputWords = new InputDataFormatter(dataSet.wordCount, configuration.wordDictionary,
				Constants.wordDictionarylogisticValue);
		InputDataFormatter formattedInputEnding = new InputDataFormatter(dataSet.endingCount,
				configuration.endingDictionary, Constants.fileEndingDictionaryLogisticValue);
		InputDataFormatter formattedInputFolder = new InputDataFormatter(dataSet.fileNameCount,
				configuration.fileNameDictionary, Constants.fileNameDictionarylogisticValue);

		double[] input = new double[configuration.inputNeuronCount];

		int count = 0;

		double[] list = new double[ratioDataSet.getInputNeurons().size()];
		for (int i = 0; i < ratioDataSet.getInputNeurons().size(); i++) {
			Double d = ratioDataSet.getInputNeurons().get(i);
			list[i] = d.doubleValue();
			System.out.println(d.doubleValue());
		}
		addDoublesToArray(input, list, count);
		addDoublesToArray(input, formattedInputWords.getInputNeurons(), count += list.length);
		addDoublesToArray(input, formattedInputEnding.getInputNeurons(),
				count += formattedInputWords.getInputNeurons().length);
		addDoublesToArray(input, formattedInputFolder.getInputNeurons(),
				count += formattedInputEnding.getInputNeurons().length);
		return input;
	}

	public RepositoryTyp classify(CollectedDataSet collectedDataSet) {
		// set network input
		neuralNetwork.setInput(getFormattedInput(collectedDataSet));
		// calculate network
		neuralNetwork.calculate();
		// get network output
		return doubleToRepositoryTyp(neuralNetwork.getOutput());
	}

	public RepositoryTyp doubleToRepositoryTyp(double[] output) {
		ArrayList<Double> list = new ArrayList<>();
		for (int i = 0; i < output.length; i++) {
			list.add(output[i]);
			System.out.println(i + " " + output[i]);
		}
		return RepositoryTyp.values()[list.indexOf(Collections.max(list))];
	}

	public static void main(String[] args) {
		List<Repository> repositorys = null;
		try {
			repositorys = new InputFileReader(Constants.trainingRepositoryLocation).getRepositorysAndTypes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Set<CollectedDataSet> dataSetAll = new HashSet<>();
		for (Repository rp : repositorys) {
			CollectedDataSet dataSet = null;
			try {
				dataSet = RepoCacher.get(rp.getName()).getCollectedDataSet();
				dataSet.repositoryType = rp.getTyp();
				dataSetAll.add(dataSet);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			new ClassifierNN(dataSetAll, ClassifierConfiguration.getDefaultConfiguration());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ClassifierNN loadFromFile(File neuralnetworklocation) throws ClassNotFoundException, IOException {
		return new ClassifierNN((MultiLayerPerceptron) NeuralNetwork.createFromFile(neuralnetworklocation), ClassifierConfiguration.getDefaultConfiguration());
	}

}
