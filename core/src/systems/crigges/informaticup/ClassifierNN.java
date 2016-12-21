package systems.crigges.informaticup;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.nnet.Perceptron;

public class ClassifierNN {

	@SuppressWarnings("unchecked")
	public ClassifierNN(){
		// create new perceptron network 
		NeuralNetwork<IterativeLearning> neuralNetwork = new Perceptron(2, 1);
		// create training set 
		DataSet trainingSet = new DataSet(2, 1); 
		// add training data to training set (logical OR function) 
		trainingSet.
		addRow (new DataSetRow (new double[]{0, 0}, 
		 new double[]{0})); 
		trainingSet.
		addRow (new DataSetRow (new double[]{0, 1}, 
		 new double[]{1})); 
		trainingSet.
		addRow (new DataSetRow (new double[]{1, 0}, 
		 new double[]{1})); 
		trainingSet.
		addRow (new DataSetRow (new double[]{1, 1}, 
		 new double[]{1})); 
		// learn the training set 
		neuralNetwork.learn(trainingSet); 
		// save the trained network into file 
		neuralNetwork.save("or_perceptron.nnet"); 
	}
	
	@SuppressWarnings("deprecation")
	public double[] useClassifier(){
		NeuralNetwork<IterativeLearning> neuralNetwork = NeuralNetwork.load("or_perceptron.nnet"); 
				// set network input 
				neuralNetwork.setInput(0, 0); 
				// calculate network 
				neuralNetwork.calculate(); 
				// get network output 
				return neuralNetwork.getOutput();
	}
	
	public static void main(String[] args){
		double[] output = new ClassifierNN().useClassifier();
		for(double d : output){
			System.out.println(d);
		}
	}
}
