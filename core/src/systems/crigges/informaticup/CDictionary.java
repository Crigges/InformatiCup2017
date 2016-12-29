package systems.crigges.informaticup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import systems.crigges.informaticup.InputFileReader.Repository;

public class CDictionary {
	private static final double defaultUnifierStrength = 0.5;
	private HashMap<RepositoryTyp, WordUnifier> unifiedGroupDictonary = new HashMap<>();
	private HashMap<RepositoryTyp, WordStatistic> groupWordStatistic = new HashMap<>();
	private WordStatistic naturalWordStatistic = new WordStatistic();
	private List<Repository> repositorys;
	
	public CDictionary(List<Repository> repositorys) {
		this.repositorys = repositorys;
		for(RepositoryTyp type : RepositoryTyp.values()){
			unifiedGroupDictonary.put(type, new WordUnifier());
		}
		generate();
	}
	
	private void generate(){
		for(Repository r : repositorys){
			try {
				GithubRepoCrawler crawler = RepoCacher.get(r.getName());
				unifiedGroupDictonary.get(r.getTyp()).add(crawler.getWordCount());
				naturalWordStatistic.add(crawler.getWordCount());
			} catch (IOException e) {
				e.printStackTrace(); //skip for now
			}
		}
		for(RepositoryTyp type : RepositoryTyp.values()){
			WordUnifier unifier = unifiedGroupDictonary.get(type);
			unifier.finish(defaultUnifierStrength);
			groupWordStatistic.put(type, unifier.getUnifiedStatistic());
		}
	}
}
