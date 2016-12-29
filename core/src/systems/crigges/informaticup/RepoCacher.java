package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import systems.crigges.informaticup.InputFileReader.Repository;

public class RepoCacher {
	private static final String cacheLocation = "./repocache/";
	private static final FSTConfiguration fstConfig = FSTConfiguration.createDefaultConfiguration();
	
	private static String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}
	
	private static void putOld(GithubRepoCrawler crawler){
		try{
			String name = crawler.getRepoName();
			File f = new File(cacheLocation + name.substring(0, name.indexOf("/")) + "/");
			f.mkdir();
			f = new File(cacheLocation + name + ".repo");
			f.delete();
			f.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(crawler);
			out.flush();
			out.close();
		}catch(IOException e){}
	}
	
	private static void put(GithubRepoCrawler crawler){
		try{
			String name = crawler.getRepoName();
			File f = new File(cacheLocation + name.substring(0, name.indexOf("/")) + "/");
			f.mkdir();
			f = new File(cacheLocation + name + ".repo");
			f.delete();
			f.createNewFile();
			FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(f), fstConfig);
			out.writeObject(crawler);
			out.flush();
			out.close();
		}catch(IOException e){}
	}
	
	public static GithubRepoCrawler getOld(String url) throws MalformedURLException, IOException{
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if(f.exists()){
			try{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
				long time = System.currentTimeMillis();
				System.out.println("Loading..");
				GithubRepoCrawler repo = (GithubRepoCrawler) in.readObject();
				System.out.println("Time: " + (System.currentTimeMillis() - time));
				in.close();
				return repo;
			} catch (IOException | ClassNotFoundException e) {
				f.delete();
				GithubRepoCrawler repo = new GithubRepoCrawler(url);
				put(repo);
				return repo;
			}
		}else{
			GithubRepoCrawler repo = new GithubRepoCrawler(url);
			put(repo);
			return new GithubRepoCrawler(url);
		}
	}
	
	public static GithubRepoCrawler get(String url) throws MalformedURLException, IOException{
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if(f.exists()){
			try{
				FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
				long time = System.currentTimeMillis();
				System.out.println("Loading..");
				GithubRepoCrawler repo = (GithubRepoCrawler) in.readObject();
				System.out.println("Time: " + (System.currentTimeMillis() - time));
				in.close();
				return repo;
			} catch (IOException | ClassNotFoundException e) {
				f.delete();
				GithubRepoCrawler repo = new GithubRepoCrawler(url);
				put(repo);
				return repo;
			}
		}else{
			GithubRepoCrawler repo = new GithubRepoCrawler(url);
			put(repo);
			return new GithubRepoCrawler(url);
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<Repository> list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
		for(Repository r : list){
			GithubRepoCrawler crawler = get(r.getName());
			put(crawler);
		}
	}
	
}
