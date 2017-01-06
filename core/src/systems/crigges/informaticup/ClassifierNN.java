package systems.crigges.informaticup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;
import systems.crigges.informaticup.InputFileReader.Repository;

public class ClassifierNN {

	private ClassifierConfiguration configuration;
	private int inputNeuronCount;
	private DataSet trainingSet;
	private MultiLayerPerceptron neuralNetwork;
	
	private int hiddenLayerNeuronCount = 3;
	private double maxError = 0.42;
	private double learningRate = 0.2;
	private final double momentum = 0.7;

	public ClassifierNN(Set<CollectedDataSet> trainDataSet, ClassifierConfiguration configuration) {
		this.configuration = configuration;
		inputNeuronCount = configuration.endingDictionary.size() * 2 + configuration.fileNameDictionary.size() * 2
				+ configuration.wordDictionary.size() * 2 + RatioDataSet.getDefaultRatioCount();
		System.out.println(inputNeuronCount);
		createNeuronStructure();

		trainingSet = new DataSet(inputNeuronCount, Constants.numberOfNeuronOutput);
		for (CollectedDataSet dataSet : trainDataSet) {
			trainNetwork(dataSet);
			System.out.println("A DataSettrainingConstruction finished");
		}
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

		neuralNetwork.save("assets//classifierNN.nnet");
	}

	private void createNeuronStructure() {
		neuralNetwork = new MultiLayerPerceptron(
				Arrays.asList(inputNeuronCount, hiddenLayerNeuronCount, Constants.numberOfNeuronOutput),
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
		RatioDataSet ratioDataSet = new RatioDataSet(dataSet);
		InputDataFormatter formattedInputWords = new InputDataFormatter(dataSet.wordCount, configuration.wordDictionary,
				Constants.wordDictionarylogisticValue);
		InputDataFormatter formattedInputEnding = new InputDataFormatter(dataSet.endingCount,
				configuration.endingDictionary, Constants.fileEndingDictionaryLogisticValue);
		InputDataFormatter formattedInputFolder = new InputDataFormatter(dataSet.fileNameCount,
				configuration.fileNameDictionary, Constants.fileNameDictionarylogisticValue);

		double[] input = new double[inputNeuronCount];

		int count = 0;

		double[] list = new double[ratioDataSet.getInputNeurons().size()];
		for (int i = 0; i < ratioDataSet.getInputNeurons().size(); i++) {
			Double d = ratioDataSet.getInputNeurons().get(i);
			list[i] = d.doubleValue();
		}
		addDoublesToArray(input, list, count);
		addDoublesToArray(input, formattedInputWords.getInputNeurons(), count += list.length);
		addDoublesToArray(input, formattedInputEnding.getInputNeurons(),
				count += formattedInputWords.getInputNeurons().length);
		addDoublesToArray(input, formattedInputFolder.getInputNeurons(),
				count += formattedInputEnding.getInputNeurons().length);
		return input;
	}

	@SuppressWarnings("deprecation")
	public RepositoryTyp classify(CollectedDataSet collectedDataSet) {
		@SuppressWarnings("unchecked")
		NeuralNetwork<IterativeLearning> neuralNetwork = NeuralNetwork.load("assets//classifier.nnet");
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
		}
		return RepositoryTyp.values()[list.indexOf(Collections.max(list))];
	}

	public static void main(String[] args) {
		ArrayList<DictionaryEntry> fileNameDictionary = null;
		ArrayList<DictionaryEntry> fileEndingDictionary = null;
		ArrayList<DictionaryEntry> wordDictionary = null;
		List<Repository> repositorys = null;
		try {
			fileNameDictionary = SerializeHelper.deserialize(Constants.fileNameDictionaryLocation);
			fileEndingDictionary = SerializeHelper.deserialize(Constants.fileEndingDictionaryLocation);
			wordDictionary = SerializeHelper.deserialize(Constants.wordDictionaryLocation);
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
		ClassifierConfiguration configuration = new ClassifierConfiguration();
		configuration.collectedDataSet = dataSetAll;
		configuration.endingDictionary = fileEndingDictionary;
		configuration.fileNameDictionary = fileNameDictionary;
		configuration.wordDictionary = wordDictionary;

		new ClassifierNN(dataSetAll, configuration);
	}

}
