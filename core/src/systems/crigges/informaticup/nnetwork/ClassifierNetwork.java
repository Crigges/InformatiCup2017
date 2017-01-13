package systems.crigges.informaticup.nnetwork;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.RangeRandomizer;

import systems.crigges.informaticup.general.CollectedDataSet;
import systems.crigges.informaticup.general.ClassifierConfiguration;
import systems.crigges.informaticup.general.RepositoryDescriptor;
import systems.crigges.informaticup.general.RepositoryTyp;
import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.RepoCacher;

/**
 * This class generates a multilayerperceptron network, using
 * momentumbackpropagation as learning rule, Sigmoid function as activiation
 * function and with one hidden Layer. The parameters of the
 * {@link ClassifierConfiguration} are used.
 * 
 * @author Rami Aly & Andre Schurat
 * @see ClassifierConfiguration
 */
public class ClassifierNetwork {

	private ClassifierConfiguration configuration;
	private DataSet trainingSet;
	private MultiLayerPerceptron neuralNetwork;

	/**
	 * Creates a new Neural Network with parameters of
	 * {@link ClassifierConfiguration}. It trains the network with the given Set
	 * of CollectedDataSets.
	 * 
	 * @param trainDataSet
	 * @param configuration
	 * @see CollectedDataSet
	 * @see ClassifierConfiguration
	 */
	public ClassifierNetwork(Set<CollectedDataSet> trainDataSet, ClassifierConfiguration configuration) {
		this.configuration = configuration;
		createNeuronStructure();

		trainingSet = new DataSet(configuration.inputNeuronCount, configuration.numberOfNeuronOutput);
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

		neuralNetwork.save(configuration.neuralNetworkLocation.getAbsolutePath());
	}

	/**
	 * Creates ClassifierNetwork with given MultiLayerPerceptron and
	 * {@link ClassifierConfiguration}
	 * 
	 * @param neuralNetwork
	 * @param configuration
	 */
	public ClassifierNetwork(MultiLayerPerceptron neuralNetwork, ClassifierConfiguration configuration) {
		this.neuralNetwork = neuralNetwork;
		this.configuration = configuration;
	}

	/**
	 * Setup of neural network and its parameters, learning rule and randomizing
	 * of weights
	 */
	private void createNeuronStructure() {
		neuralNetwork = new MultiLayerPerceptron(Arrays.asList(configuration.inputNeuronCount,
				configuration.hiddenLayerNeuronCount, configuration.numberOfNeuronOutput),
				TransferFunctionType.TANH);
		MomentumBackpropagation bp = new MomentumBackpropagation();
		bp.setLearningRate(configuration.learningRate);
		bp.setMaxError(configuration.maxError);
		bp.setMomentum(configuration.momentum);
		RangeRandomizer random = new RangeRandomizer(-0.7, 0.7);
		random.setRandomGenerator(new Random(1));
		neuralNetwork.randomizeWeights(random);
		neuralNetwork.setLearningRule(bp);
	}

	/**
	 * Trains the network with given dataSet and expected output
	 * 
	 * @param dataSet of training repository
	 */
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

	/**
	 * retrieve normalized value of {@link InputDataFormatter} values for every
	 * dictionary and {@link RatioDataSet} values, size always matches with
	 * number of neurons.
	 * 
	 * @param dataSet
	 * @return array of normalized input
	 */
	private double[] getFormattedInput(CollectedDataSet dataSet) {
		RatioDataSet ratioDataSet = new RatioDataSet(dataSet, configuration.ratioLogisticValue,
				configuration.normRatioValues);
		InputDataFormatter formattedInputWords = new InputDataFormatter(dataSet.wordCount, configuration.wordDictionary,
				configuration.wordDictionarylogisticValue);
		InputDataFormatter formattedInputEnding = new InputDataFormatter(dataSet.endingCount,
				configuration.fileEndingDictionary, configuration.fileEndingDictionaryLogisticValue);
		InputDataFormatter formattedInputFolder = new InputDataFormatter(dataSet.fileNameCount,
				configuration.fileNameDictionary, configuration.fileNameDictionarylogisticValue);

		double[] input = new double[configuration.inputNeuronCount];

		int count = 0;

		double[] list = new double[ratioDataSet.getNormalizedRatios().size()];
		for (int i = 0; i < ratioDataSet.getNormalizedRatios().size(); i++) {
			Double d = ratioDataSet.getNormalizedRatios().get(i);
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

	/**
	 * classify normalized input generated of collectedDataSet and returns best
	 * matching {@link RepositoryTyp}
	 * 
	 * @param CollectedDataSet of repository to be classified
	 * @return {@link RepositoryTyp}
	 */
	public RepositoryTyp classify(CollectedDataSet collectedDataSet) {
		neuralNetwork.setInput(getFormattedInput(collectedDataSet));
		neuralNetwork.calculate();
		return doubleToRepositoryTyp(neuralNetwork.getOutput());
	}

	// Searches for the biggest outputneuron value
	private RepositoryTyp doubleToRepositoryTyp(double[] output) {
		ArrayList<Double> list = new ArrayList<>();
		for (int i = 0; i < output.length; i++) {
			list.add(output[i]);
			System.out.println(i + " " + output[i]);
		}
		return RepositoryTyp.values()[list.indexOf(Collections.max(list))];
	}

	/**
	 * Used to create new Neural Network with default parameters set in
	 * {@link ClassifierConfiguration} and save it into File.
	 * 
	 * @param args are not used
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws Exception
	 * @see {@link ClassifierConfiguration}
	 */
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

		new ClassifierNetwork(dataSetAll, config);
	}

	/**
	 * Loads the saved Network with default parameters in
	 * {@link ClassifierConfiguration}
	 * 
	 * @param File of a neural network
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @see ClassifierConfiguration
	 */
	public static ClassifierNetwork loadFromFile(File neuralnetworklocation)
			throws ClassNotFoundException, IOException {
		return new ClassifierNetwork((MultiLayerPerceptron) NeuralNetwork.createFromFile(neuralnetworklocation),
				ClassifierConfiguration.getDefault());
	}

}
