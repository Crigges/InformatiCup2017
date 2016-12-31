package systems.crigges.informaticup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Set;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.nnet.Perceptron;

public class ClassifierNN {

	public ClassifierNN(Set<CollectedDataSet> dataSetAll) {
		// create new perceptron network
		ArrayList<String> dictionary = deserializeDictionary();
		Perceptron neuralNetwork = new Perceptron(dictionary.size() + 3, 7);
		DataSet trainingSet = new DataSet(dictionary.size() + 3, 7);
		for (CollectedDataSet dataSet : dataSetAll) {
			InputDataFormatter formattedInput = new InputDataFormatter(dataSet, dictionary);
			double[] output = new double[7];
			output[dataSet.repositoryType.getValue()] = 1.0;
			trainingSet.addRow(new DataSetRow(formattedInput.getInputNeurons(), output));
		}
		// create training set
		// add training data to training set (logical OR function)
//		trainingSet.addRow(new DataSetRow(new double[] { 0, 0 }, new double[] { 0 }));
//		trainingSet.addRow(new DataSetRow(new double[] { 0, 1 }, new double[] { 1 }));
//		trainingSet.addRow(new DataSetRow(new double[] { 1, 0 }, new double[] { 1 }));
//		trainingSet.addRow(new DataSetRow(new double[] { 1, 1 }, new double[] { 1 }));
		// learn the training set
		neuralNetwork.learn(trainingSet);
		// save the trained network into file
		neuralNetwork.save("or_perceptron.nnet");
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> deserializeDictionary() {
		Object words = null;
		try {
			FileInputStream fileIn = new FileInputStream("/assets/dictionary.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			words = in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("DictionaryClass not found");
			c.printStackTrace();
		}
		return (ArrayList<String>) words;
	}

	@SuppressWarnings("deprecation")
	public double[] useClassifier() {
		@SuppressWarnings("unchecked")
		NeuralNetwork<IterativeLearning> neuralNetwork = NeuralNetwork.load("or_perceptron.nnet");
		// set network input
		neuralNetwork.setInput(1, 0);
		// calculate network
		neuralNetwork.calculate();
		// get network output
		return neuralNetwork.getOutput();
	}

	public static void main(String[] args) {
//		double[] output = new ClassifierNN().useClassifier();
//		for (double d : output) {
//			System.out.println(d);
//		}
	}
}
