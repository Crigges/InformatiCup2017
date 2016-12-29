package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;

public class RepoCacher {
	private static final String cacheLocation = "./repocache/";
	
	private static String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}
	
	private static void put(GithubRepoCrawler crawler){
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
	
}
