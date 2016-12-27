package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.naming.directory.InvalidAttributeIdentifierException;

public class RepoCacher {
	private static final String cacheLocation = "./repocache/";
	private static HashMap<String, GithubRepoCrawler> loadedCache = new HashMap<>();
	
	private static String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}
	
	public static void put(GithubRepoCrawler crawler) throws FileNotFoundException, IOException{
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
	}
	
	public static GithubRepoCrawler get(String url) throws MalformedURLException, IOException{
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if(f.exists()){
			try{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
				GithubRepoCrawler repo = (GithubRepoCrawler) in.readObject();
				in.close();
				return repo;
			} catch (IOException | ClassNotFoundException e) {
				f.delete();
				return new GithubRepoCrawler(url);
			}
		}else{
			return new GithubRepoCrawler(url);
		}
	}
	
}
