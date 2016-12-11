package systems.crigges.informaticup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

public class GithubRepoCrawler {
	
	private GitHub git;
	private GHRepository repo;

	public GithubRepoCrawler(String url) {
		try {
			git = GitHub.connectAnonymously();
			repo = git.getRepository(getRepoNameFromURL(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRepoNameFromURL(String url){
		return url.replace("https://github.com/", "");
	}
	
	public String getHomepage(){
		return repo.getHomepage();
	}
	
	public List<GHContent> getFullContent(){
		try {
			return getFullContentRecursive(repo.getDirectoryContent("./"), new ArrayList<>());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<GHContent> getFullContentRecursive(List<GHContent> source, List<GHContent> target) throws IOException{
		for(GHContent content : source){
			if(content.isDirectory()){
				getFullContentRecursive(content.listDirectoryContent().asList(), target);
			}else{
				target.add(content);
			}
		}
		return target;
	}
	
	public Set<GHUser> getCollaboators(){
		try {
			return repo.getCollaborators();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		GithubRepoCrawler crawler = new GithubRepoCrawler("https://github.com/Crigges/Clickwars");
		for(GHContent gh : crawler.getFullContent()){
			System.out.println(gh.getName());
		}
	}

}
