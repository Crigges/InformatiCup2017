package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import com.google.common.io.Files;

public class GithubRepoCrawler {
	
	private GitHub git;
	private GHRepository repo;
	private List<GHContent> contentCache = null;
	private File target;

	public GithubRepoCrawler(String url) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		git = GitHub.connectAnonymously();
		repo = git.getRepository(getRepoNameFromURL(url));
		target = Files.createTempDir();
		target.deleteOnExit();
		CloneCommand cloneCommand = Git.cloneRepository();
		cloneCommand.setDirectory(target);
		cloneCommand.setURI( url + ".git");
		Set<String> defaultBranch = new TreeSet<>();
		defaultBranch.add(repo.getDefaultBranch());
		cloneCommand.setBranchesToClone(defaultBranch);
		cloneCommand.call();
	}
	
	public String getRepoNameFromURL(String url){
		return url.replace("https://github.com/", "");
	}
	
	public String getHomepage(){
		return repo.getHomepage();
	}
	
	public List<GHContent> getFullContent(){
		try {
			if(contentCache == null){
				contentCache = getFullContentRecursive(repo.getDirectoryContent("./"), new ArrayList<>());
			}
			return contentCache;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<GHContent> getFullContentRecursive(List<GHContent> source, List<GHContent> target) throws IOException{
		ArrayList<GHContent> folders = new ArrayList<>();
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
	
	public Set<Entry<String, Integer>> getSortedWordEndings(){
		WordCounter endingCounter = new WordCounter();
		List<File> content = getFullLocalContent();
		for(File c: content){
			String name = c.getName();
			String ending;
			if(name.contains(".")){
				ending = name.substring(name.lastIndexOf("."));
			}else{
				ending = "fileHasNoEnding";
			}
			endingCounter.feed(ending);
		}
		endingCounter.close();
		return endingCounter.getSortedEntrys();
	}
	
	public ArrayList<File> getFullLocalContent() {
		ArrayList<File> list = new ArrayList<>();
		getFullLocalContentCallback(target ,list);
		return list;
	}
	
	public void getFullLocalContentCallback(File dir, ArrayList<File> files) {
	    File[] fList = dir.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	        	getFullLocalContentCallback(file, files);
	        }
	    }
	}
	
	public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		GithubRepoCrawler crawler = new GithubRepoCrawler("https://github.com/spring-projects/spring-boot");
//		for (File f : crawler.getFullLocalContent()) {
//			System.out.println(f.getName());
//		}
		for(Entry<String, Integer> entry : crawler.getSortedWordEndings()){
			System.out.println(entry);
		}
	}

}
