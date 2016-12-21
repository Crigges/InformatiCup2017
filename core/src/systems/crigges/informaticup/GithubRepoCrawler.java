package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

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
	private Gson gson = new Gson();

	public GithubRepoCrawler(String url) throws MalformedURLException, IOException  {
		URLConnection zipConnection = new URL("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/zipball").openConnection();
		//Decide if grabbing the Zipball is faster than Analyzing the tree
		if(false && zipConnection.getContentLength() < Constants.MaxZipSize){
			fileList = ZipballGrabber.grabVirtual("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/zipball");
		}else{
			fileList = getFilesFromTree("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/git/trees/master?recursive=1");
		}
	}
	
	private String getRepoNameFromURL(String url){
		return url.replace("https://github.com/", "");
	}
	
	private ArrayList<VirtualFile> getFilesFromTree(String treeUrl) throws IOException{
		URL u = new URL(treeUrl);
		URLConnection fileTreeConnection = u.openConnection();
		InputStream in = fileTreeConnection.getInputStream();	
		//Create tree from response
		GithubFileTree tree = gson.fromJson(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8)), GithubFileTree.class);
		System.out.println("tree " + tree.tree.size());
		Collections.sort(tree.tree);
		ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();
		for(GithubFile file : tree.tree){
			String name =  new File(file.path).getName();
			if(file.isFolder()){
				files.add(new VirtualFile(name, null, true));
			}else{
				if(file.size < Constants.MaxFileSize){
					files.add(getFileFromUrl(name, file.url));
				}else{
					files.add(new VirtualFile(name, null, false, file.size));
				}
			}
		}
		return files;
	}
	
	private VirtualFile getFileFromUrl(String name, String url) throws IOException{
		URL u = new URL(url);
		URLConnection fileConnection = u.openConnection();
		InputStream in = fileConnection.getInputStream();
		GithubFileContent file = gson.fromJson(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8)), GithubFileContent.class);
		file.genByteContent();
		return new VirtualFile(name, file.byteContent, false);
	}
	
//	public Set<Entry<String, Integer>> getSortedWordEndings(){
//		WordCounter endingCounter = new WordCounter();
//		List<VirtualFile> content = getFullVirtualContent();
//		for(VirtualFile c: content){
//			String name = c.name;
//			String ending;
//			if(name.contains(".")){
//				ending = name.substring(name.lastIndexOf("."));
//			}else{
//				ending = "fileHasNoEnding";
//			}
//			endingCounter.feed(ending);
//		}
//		endingCounter.close();
//		return endingCounter.getSortedEntrys();
//	}
	
	
	public List<VirtualFile> getFullContent(){
		return fileList;
	}
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		long milis = System.currentTimeMillis();
		GithubRepoCrawler crawler = new GithubRepoCrawler("https://github.com/DataScienceSpecialization/courses");
		System.out.println("time: " + (System.currentTimeMillis() - milis));
		
		System.out.println("___________________");
		
		milis = System.currentTimeMillis();		
		WordCounter totalCounter = new WordCounter();
		for(VirtualFile f : crawler.getFullContent()){
			System.out.println("name: " + f.name + "   | size:" + f.size);
//			if(f.type == SuperMimeType.Text){
////				String s = new String(f.data, StandardCharsets.UTF_8);
////				totalCounter.feed(s);
//			}else if(f.type == SuperMimeType.PDF){
//				try{
//					PDFAnalyzer anal = new PDFAnalyzer(f.data);
//					totalCounter.feed(anal.getRawText());
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				
//			}
		}
//		
		for(Entry<String, Integer> entry: totalCounter.getSortedEntrys()){
			System.out.println(entry);
		}
		//System.out.println(javaCounter.getSortedEntrys().size());
		System.out.println("time: " + (System.currentTimeMillis() - milis));
		
	}

}
