package systems.crigges.informaticup.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import systems.crigges.informaticup.crawling.RepositoryCrawler;

/**
 * This class provides functions to cache repositories. If once a repository was
 * analyzed it is automatically written into a Hard drive cache, from this point
 * on the repository dosen't needs to be analyzed again. But that also means any
 * changes to repository aren't recognized.
 * 
 * @author Rami Aly & Andre Schurat
 * @see RepositoryCrawler
 */
public class RepoCacher {
	private static final String cacheLocation = "./repocache/";
	private static final FSTConfiguration fstConfig = FSTConfiguration.createDefaultConfiguration();
	private static ExecutorService executor;

	/**
	 * Splits the repository name apart from the given url.
	 * 
	 * @param url
	 *            the repository's url
	 * @return the url's repository name
	 */
	private static String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}

	/**
	 * Writes the given analyzed repository into the specified cache location
	 * 
	 * @param crawler
	 *            the repo which should be cached
	 */
	private static void put(RepositoryCrawler crawler) {
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

	/**
	 * Returns the url's RepositoryCrawler. If the crawler is not found inside
	 * the cache a new one will be created and automatically cached afterwards.
	 * 
	 * @param url
	 *            the repository's url
	 * @return the repository's crawler
	 * @throws Exception
	 *             if the url is invaild or the repository could not be read
	 * @see RepositoryCrawler
	 */
	public static RepositoryCrawler get(String url) throws Exception {
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if (f.exists()) {
			try {
				FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
				long time = System.currentTimeMillis();
				System.out.print("Loading: " + name + "\t\t\t\t");
				RepositoryCrawler repo = (RepositoryCrawler) in.readObject();
				System.out.println("Time: " + (System.currentTimeMillis() - time));
				in.close();
				return repo;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				f.delete();
				RepositoryCrawler repo = new RepositoryCrawler(url);
				put(repo);
				return repo;
			}
		} else {
			RepositoryCrawler repo = new RepositoryCrawler(url);
			put(repo);
			return new RepositoryCrawler(url);
		}
	}

	/**
	 * Starts a threadpool with the given threadcount which is used to for
	 * multithreaded repository loading in.
	 * {@link RepoCacher#getThreaded(String, RepoLoadAction)}
	 * 
	 * @param threads
	 *            the threadpool's thread count
	 */
	public static void initThreadPool(int threads) {
		executor = Executors.newFixedThreadPool(threads);
	}

	/**
	 * Same behavior as described in {@link RepoCacher#get(String)} just
	 * multithreaded. The loaded crawler is passed in the
	 * {@link RepoLoadAction#loaded(RepositoryCrawler)} method. Note:
	 * {@link RepoCacher#initThreadPool(int)} needs to be called first!
	 * 
	 * @param url
	 *            the repository's url
	 * @param whenLoaded
	 *            the load action where the loaded repository is being passed
	 * @throws Exception
	 *             if the url is invaild or the repository could not be read
	 */
	public static void getThreaded(String url, RepoLoadAction whenLoaded) throws Exception {
		String name = getRepoNameFromURL(url);
		File f = new File(cacheLocation + name + ".repo");
		if (f.exists()) {
			try {
				FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
				long time = System.currentTimeMillis();
				System.out.print("Loading: " + name + "\t\t\t\t");
				RepositoryCrawler repo = (RepositoryCrawler) in.readObject();
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

	/**
	 * Shutdowns the loading Threadpool, blocks until all requested repositorys
	 * have been loaded.
	 */
	public static void shutdownThreadPool() {
		executor.shutdown();
	}

	/**
	 * Simple interface to define custom load action for multithreaded
	 * Repository loading
	 */
	public interface RepoLoadAction {

		void loaded(RepositoryCrawler crawler);

	}

	/**
	 * Wrapper function to submit a new {@link AnalyzeNewTask} to the executor
	 */
	private static void analyzeNew(String url, RepoLoadAction whenLoaded)
			throws InterruptedException, ExecutionException {
		executor.submit(new AnalyzeNewTask(url, whenLoaded));
	}

	/**
	 * Simple helper class to support Multithreaded repository analyzing
	 */
	private static class AnalyzeNewTask implements Callable<RepositoryCrawler> {
		private String url;
		private RepoLoadAction whenLoaded;

		public AnalyzeNewTask(String url, RepoLoadAction whenLoaded) {
			this.url = url;
			this.whenLoaded = whenLoaded;
		}

		@Override
		public RepositoryCrawler call() throws Exception {
			RepositoryCrawler repo = new RepositoryCrawler(url);
			whenLoaded.loaded(repo);
			put(repo);
			return repo;
		}

	}

}
