package systems.crigges.informaticup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import systems.crigges.informaticup.InputFileReader.Repository;

public class Dictionary {

	File inputFile;
	ArrayList<String> words;

	public Dictionary(File inputFile) {
		this.inputFile = inputFile;
		generateWords();
	}
	
	public ArrayList<String> getWords() {
		return words;
	}
	
	private void generateWords(){
		List<Repository> repositorys = null;
		try {
			repositorys = new InputFileReader(inputFile).getRepositorysAndTypes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String>[] wordGroups = genreateSetsAccordingToRepositoryType(repositorys);
		for(List<String> grouop : wordGroups){
		}
		
		
	}
	
	
	private List<String>[] genreateSetsAccordingToRepositoryType(List<Repository> repositorys){
		ArrayList<String>[] wordGroups = (ArrayList<String>[]) new ArrayList[7];
		for(int i = 0; i < wordGroups.length; i++){
			wordGroups[i] = new ArrayList<String>();
		}
		for(Repository t : repositorys){
			wordGroups[t.getTyp().getValue()].add(t.getName());
		}
		return wordGroups;
	}
	
}
