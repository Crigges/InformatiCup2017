package systems.crigges.informaticup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;


public class InputDataFormatter {

	double[] inputNeurons;
	CollectedDataSet dataSet;

	public InputDataFormatter(CollectedDataSet dataSet) {
		this.dataSet = dataSet;
		inputNeurons = normalizeInput(calculateInput());	
	}

	private double[] normalizeInput(ArrayList<Double> calculateInput) {
		//Testen!
		double[] normalizedNeurons = new double[calculateInput.size()];
		double erwartungswertA = 0;
		double varianzA = 0;
		double partVarianz = 0;
		for(Double inputNeuron : calculateInput){
			erwartungswertA += inputNeuron;
		}
		erwartungswertA /= calculateInput.size();
		for(Double inputNeuron : calculateInput){
			partVarianz += Math.pow((inputNeuron - erwartungswertA), 2);
		}
		varianzA = Math.sqrt(partVarianz / calculateInput.size());
		for(int i = 0; i < normalizedNeurons.length; i++){
			normalizedNeurons[i] = (calculateInput.get(i) - erwartungswertA) / varianzA;
		}
		return normalizedNeurons;
	}

	public double[] getInputNeurons(){
		return inputNeurons;
	}
	
	private ArrayList<Double> calculateInput(){
		//TODO: Still need to clarify special indexPosition, MISSING!
		//TODO: ChangeValueSet
		ArrayList<Double> input = new ArrayList<Double>();
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

		//Also filter?
		ArrayList<Double> averageEmailEndingOccurrence =  new ArrayList<Double>();
		for(Entry<String, Integer> entry : dataSet.emailEndingCount){
			averageEmailEndingOccurrence.add(((double) entry.getValue()) / dataSet.fileCount);
		}
		input.add(averageFileSize);
		input.add(mediaDensity);
		input.add(subscribeToStaredRatio);
		input.addAll(averageEndingOccurrence);
		input.addAll(averageWordOccurrence);
		input.addAll(averagefolderNameOccurrence);
		input.addAll(averageEmailEndingOccurrence);
		
		return input;
		
	}

}
