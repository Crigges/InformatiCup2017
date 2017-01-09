package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import systems.crigges.informaticup.InputFileReader.Repository;

public class RepoCacher {
	private static final String cacheLocation = "./repocache/";
	private static final FSTConfiguration fstConfig = FSTConfiguration.createDefaultConfiguration();
	private static ExecutorService executor;

	private static String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}

	private static void put(GithubRepoCrawler crawler) {
		try {
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
		} catch (IOException e) {
		}
	}

	public static GithubRepoCrawler get(String url) throws MalformedURLException, IOException, InterruptedException, ExecutionException {
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if (f.exists()) {
			try {
				FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
				long time = System.currentTimeMillis();
				System.out.print("Loading: " + name + "\t\t\t\t");
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
		} else {
			GithubRepoCrawler repo = new GithubRepoCrawler(url);
			put(repo);
			return new GithubRepoCrawler(url);
		}
	}
	
	public static void initThreadPool(int threads){
		executor = Executors.newFixedThreadPool(threads);
	}

	public static void getThreaded(String url, RepoLoadAction whenLoaded) throws MalformedURLException, IOException, InterruptedException, ExecutionException {
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if (f.exists()) {
			try {
				FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
				long time = System.currentTimeMillis();
				System.out.print("Loading: " + name + "\t\t\t\t");
				GithubRepoCrawler repo = (GithubRepoCrawler) in.readObject();
				System.out.println("Time: " + (System.currentTimeMillis() - time));
				in.close();
				whenLoaded.loaded(repo);
			} catch (IOException | ClassNotFoundException e) {
				f.delete();
				analyzeNew(url, whenLoaded);
			}
		} else {
			analyzeNew(url, whenLoaded);
		}
	}
	
	public static void shutdownThreadPool(){
		executor.shutdown();
	}
	
	public interface RepoLoadAction{
		
		void loaded(GithubRepoCrawler crawler);
		
	}
	
	private static void analyzeNew(String url, RepoLoadAction whenLoaded) throws InterruptedException, ExecutionException{
		executor.submit(new AnalyzeNewTask(url, whenLoaded));
	}

	private static class AnalyzeNewTask implements Callable<GithubRepoCrawler> {
		private String url;
		private RepoLoadAction whenLoaded;

		public AnalyzeNewTask(String url, RepoLoadAction whenLoaded) {
			this.url = url;
			this.whenLoaded = whenLoaded;
		}

		@Override
		public GithubRepoCrawler call() throws Exception {
			GithubRepoCrawler repo = new GithubRepoCrawler(url);
			whenLoaded.loaded(repo);
			put(repo);
			return  repo;
		}

	}

	public static void main(String[] args) throws Exception {
		List<Repository> list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
		for (Repository r : list) {
			GithubRepoCrawler crawler = get(r.getName());
			put(crawler);
		}
	}

}
