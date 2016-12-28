package systems.crigges.informaticup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;


public class InputDataFormatter {

	private double[] inputNeurons;
	private CollectedDataSet dataSet;
	
	private final int inputNeuronSize = 60003;

	public InputDataFormatter(CollectedDataSet dataSet) {
		this.dataSet = dataSet;
		inputNeurons = normalizeInput(calculateInput());	
	}

	private double[] normalizeInput(double[] ds) {
		//Testen!
		double[] normalizedNeurons = new double[ds.length];
		double erwartungswertA = 0;
		double varianzA = 0;
		double partVarianz = 0;
		for(Double inputNeuron : ds){
			erwartungswertA += inputNeuron;
		}
		erwartungswertA /= ds.length;
		for(Double inputNeuron : ds){
			partVarianz += Math.pow((inputNeuron - erwartungswertA), 2);
		}
		varianzA = Math.sqrt(partVarianz / ds.length);
		for(int i = 0; i < normalizedNeurons.length; i++){
			normalizedNeurons[i] = (ds[i] - erwartungswertA) / varianzA;
		}
		return normalizedNeurons;
	}

	public double[] getInputNeurons(){
		return inputNeurons;
	}
	
	private double[] calculateInput(){
		//TODO: Still need to clarify special indexPosition, MISSING!
		//TODO: ChangeValueSet
		double[] input = new double[inputNeuronSize];
		double averageFileSize = ((double) dataSet.repoSize) / dataSet.fileCount;
		double mediaDensity  = ((double) dataSet.mediaCount) / dataSet.fileCount;
		double subscribeToStaredRatio = ((double) dataSet.subscribedCount) / dataSet.staredCount;
		
		ArrayList<Double> averageEndingOccurrence =  new ArrayList<Double>();
		for(Entry<String, Integer> entry : dataSet.endingCount){
			averageEndingOccurrence.add(((double) entry.getValue()) / dataSet.fileCount);
		}

		ArrayList<Double> averageWordOccurrence =  new ArrayList<Double>();
		for(Entry<String, Integer> entry : dataSet.wordCount){
			averageWordOccurrence.add(((double) entry.getValue()) / dataSet.fileCount);
		}

		//Needs filter
		ArrayList<Double> averagefolderNameOccurrence =  new ArrayList<Double>();
		for(Entry<String, Integer> entry : dataSet.folderNameCount){
			averagefolderNameOccurrence.add(((double) entry.getValue()) / dataSet.fileCount);
		}

		
		input[0] = averageFileSize;
		input[1] = mediaDensity;
		input[2] = subscribeToStaredRatio;
//		input.addAll(averageEndingOccurrence);
//		input.addAll(averageWordOccurrence);
//		input.addAll(averagefolderNameOccurrence);
		
		return input;
		
	}

}
