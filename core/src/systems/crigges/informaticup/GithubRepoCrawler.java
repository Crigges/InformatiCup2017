package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class GithubRepoCrawler {
	
	private GitHub git;
	private GHRepository repo;
	private List<GHContent> contentCache = null;
	private File target;
	private ArrayList<VirtualFile> fileList;

	public GithubRepoCrawler(String url) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		URL u = new URL("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/git/trees/master?recursive=1");
		URLConnection connection = u.openConnection();
		InputStream in = connection.getInputStream();
		Gson gson = new Gson();
		GithubFileTree tree = gson.fromJson(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8)), GithubFileTree.class);
//		System.out.println(tree.url);
//		for(GithubFile file : tree.tree){
//			System.out.println(file.path);
//		}
//		git = GitHub.connectAnonymously();
//		repo = git.getRepository(getRepoNameFromURL(url));
//		target = ZipballGrabber.grab("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/zipball");
//		fileList = ZipballGrabber.grabVirtual("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/zipball");
//		target = Files.createTempDir();
//		target.deleteOnExit();
//		CloneCommand cloneCommand = Git.cloneRepository();
//		cloneCommand.setDirectory(target);
//		cloneCommand.setURI( url + ".git");
//		Set<String> defaultBranch = new TreeSet<>();
//		defaultBranch.add(repo.getDefaultBranch());
//		cloneCommand.setBranchesToClone(defaultBranch);
//		cloneCommand.call();
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
		List<VirtualFile> content = getFullVirtualContent();
		for(VirtualFile c: content){
			String name = c.name;
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
	
	public List<VirtualFile> getFullVirtualContent(){
		return fileList;
	}
	
	public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		long milis = System.currentTimeMillis();
		GithubRepoCrawler crawler = new GithubRepoCrawler("https://github.com/DataScienceSpecialization/courses");
		System.out.println("time: " + (System.currentTimeMillis() - milis));
		
		System.out.println("___________________");
		
		milis = System.currentTimeMillis();		
		WordCounter totalCounter = new WordCounter();
		for(VirtualFile f : crawler.getFullVirtualContent()){
			if(f.type == SuperMimeType.Text){
//				String s = new String(f.data, StandardCharsets.UTF_8);
//				totalCounter.feed(s);
			}else if(f.type == SuperMimeType.PDF){
				try{
					PDFAnalyzer anal = new PDFAnalyzer(f.data);
					totalCounter.feed(anal.getRawText());
				}catch(Exception e){}
				
			}
		}
//		
		for(Entry<String, Integer> entry: totalCounter.getSortedEntrys()){
			System.out.println(entry);
		}
		//System.out.println(javaCounter.getSortedEntrys().size());
		System.out.println("time: " + (System.currentTimeMillis() - milis));
		
	}

}
