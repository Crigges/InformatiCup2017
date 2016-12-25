package systems.crigges.informaticup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Set;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class GithubRepoCrawler {
	

	private ArrayList<VirtualFile> fileList;
	private Gson gson = new Gson();

	public GithubRepoCrawler(String url) throws MalformedURLException, IOException  {
		fileList = ZipballGrabber.grabVirtual("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/zipball");
		inflateFileList();
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
	
	private void inflateFileList(){
		for (Iterator<VirtualFile> iterator = fileList.iterator(); iterator.hasNext();) {
			VirtualFile f = iterator.next();
			if(f.type == SuperMimeType.Rar){
				//TODO add virtual unrar
			}else if(f.type == SuperMimeType.Zip){
				try{
					ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(f.data));
					ZipEntry entry = zipIn.getNextEntry();
					while (entry != null) {
						String filePath = entry.getName();
						if (!entry.isDirectory()) {
							byte[] data = new byte[(int) entry.getSize()];
							zipIn.read(data);
							fileList.add(new VirtualFile(new File(filePath).getName(), data, false));
						}else{
							fileList.add(new VirtualFile(entry.getName(), null, true));
						}
						zipIn.closeEntry();
						entry = zipIn.getNextEntry();
					}
					zipIn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public Set<Entry<String, Integer>> getSortedEndingCount(){
		WordCounter endingCounter = new WordCounter();
		for(VirtualFile c: fileList){
			if(c.type != SuperMimeType.Folder){
				String name = c.name;
				String ending;
				if(name.contains(".")){
					ending = name.substring(name.lastIndexOf("."));
				}else{
					ending = "fileHasNoEnding";
				}
				endingCounter.feed(ending);
			}
		}
		endingCounter.close();
		return endingCounter.getSortedEntrys();
	}
	
	public Set<Entry<String, Integer>> getSortedWordEndings(){
		WordCounter wordCounter = new WordCounter();
		for(VirtualFile f : fileList){
			if(f.type == SuperMimeType.Text){
				wordCounter.feed(new String(f.data));
			}else if(f.type == SuperMimeType.Word){
				WordprocessingMLPackage doc = null;
				try {
					doc = WordprocessingMLPackage.load(new ByteArrayInputStream(f.data));
				} catch (Docx4JException e) {
					e.printStackTrace();
				}
				doc.getMainDocumentPart().getXML();
			}
		}
		return null;
	}
	
	
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
