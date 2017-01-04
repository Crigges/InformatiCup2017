package systems.crigges.informaticup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.nnet.Perceptron;

import systems.crigges.informaticup.CDictionary.DictionaryEntry;

public class ClassifierNN {

	private ArrayList<DictionaryEntry> dictionary;
	private DataSet trainingSet;
	
	public ClassifierNN(Set<CollectedDataSet> trainDataSet, ArrayList<DictionaryEntry> dictionary) {
		this.dictionary = dictionary;
		Perceptron neuralNetwork = new Perceptron(Constants.numbersOfNeuronInput, Constants.numberOfNeuronOutput);
		DataSet trainingSet = new DataSet(Constants.numbersOfNeuronInput, Constants.numberOfNeuronOutput);
		for (CollectedDataSet dataSet :  trainDataSet) {
			trainNetwork(dataSet);
		}
		neuralNetwork.learn(trainingSet);
		
		neuralNetwork.save("assets//classifierNN.nnet");
	}

	private void trainNetwork(CollectedDataSet dataSet){
		double[] input = getFormattedInput(dataSet);
		double[] output = new double[7];
		output[dataSet.repositoryType.getValue()] = 1.0;
		trainingSet.addRow(new DataSetRow(input));
	}
	
	private double[] addDoublesToArray(double[] input, double[] array, int startIndex){
		for(double d : array){
			input[startIndex++] = d;
		}
		return input;
	}
	
	private double[] getFormattedInput(CollectedDataSet dataSet){
		RatioDataSet ratioDataSet = new RatioDataSet(dataSet);
		InputDataFormatter formattedInputWords = new InputDataFormatter(dataSet.wordCount, dictionary, 130);
		InputDataFormatter formattedInputEnding = new InputDataFormatter(dataSet.endingCount, dictionary, 130);
		InputDataFormatter formattedInputFolder = new InputDataFormatter(dataSet.fileNameCount, dictionary, 130);
		
		double[] input = new double[Constants.numbersOfNeuronInput];
		
		int count = 0;
		
		double[] list = new double[ratioDataSet.getInputNeurons().size()];
		for(int i = 0; i < ratioDataSet.getInputNeurons().size(); i++){
			Double d = ratioDataSet.getInputNeurons().get(i);
			list[i] = d.doubleValue();
		}
		addDoublesToArray(input, list, count);
		addDoublesToArray(input, formattedInputWords.getInputNeurons(), count += list.length);
		addDoublesToArray(input, formattedInputEnding.getInputNeurons(), count += formattedInputWords.getInputNeurons().length);
		addDoublesToArray(input, formattedInputFolder.getInputNeurons(), count += formattedInputEnding.getInputNeurons().length);	
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

	public RepositoryTyp doubleToRepositoryTyp(double[] output){
		ArrayList<Double> list = new ArrayList<>();
		for(int i = 0; i < output.length; i++){
			list.add(output[i]);
		}
		return RepositoryTyp.values()[list.indexOf(Collections.max(list))];
	}
	
	public static void main(String[] args) {
//		double[] output = new ClassifierNN().useClassifier();
//		for (double d : output) {
//			System.out.println(d);
//		}
	}


}
