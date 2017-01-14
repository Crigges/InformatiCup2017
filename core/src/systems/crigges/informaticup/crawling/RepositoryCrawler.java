package systems.crigges.informaticup.crawling;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import net.sf.jmimemagic.MagicParseException;
import net.sf.jmimemagic.MagicParser;
import systems.crigges.informaticup.general.CollectedDataSet;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.wordanalytics.WordCounter;

/**
 * This class mainly creates a {@link CollectedDataSet} out of a repository url.
 * It's fully serializable and threadsafe. Since it is meant to be used for
 * hundreds of repositories in a row it aims for just a single API request per
 * repository. For a already Multithreaded cached implementation checkout
 * {@link RepoCacher}
 * 
 * @author Rami Aly & Andre Schurat
 * @see CollectedDataSet
 * @see RepoCacher
 * @see ZipballGrabber
 * @see PDFAnalyzer
 * @see PptxAnalyzer
 * @see DocxAnalyzer
 */
public class RepositoryCrawler implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient ArrayList<VirtualFile> fileList;
	private HashMap<String, Integer> wordCount;
	private HashMap<String, Integer> fileEndingCount;
	private HashMap<String, Integer> fileNameCount;

	private long totalWordCount;
	private int mediaCount;
	private String repoName;
	private int repoSize;
	private int fileCount;
	@SuppressWarnings("unused")
	private int subscribedCount;
	@SuppressWarnings("unused")
	private int staredCount;
	private long numberCount;

	/**
	 * Creates a new RepositoryCrawler from the given url. Note: Depending on
	 * your hardware and you Internet connection crawling of big repositories
	 * may take some time. Therefore it's highly recommend to use the
	 * {@link RepoCacher} instead of this direct constructor.
	 * 
	 * @param url
	 *            the url referring to the repository. e.g:
	 *            https://github.com/username/repositoryname
	 * @throws IOException
	 *             if url is malformed and or Internet connection does not work
	 */
	public RepositoryCrawler(String url) throws IOException {
		repoName = getRepoNameFromURL(url);
		fileList = ZipballGrabber.grabVirtual("https://api.github.com/repos/" + repoName + "/zipball");
		analyzeRepo();
	}

	/**
	 * Simple convert function to split the repository name apart from the url
	 * 
	 * @param url
	 *            the url where the name is been taken of
	 * @return the url's repository name
	 */
	private String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}

	/**
	 * Main analyze function containing the general analyzing structure
	 * 
	 * @throws IOException
	 *             if url is malformed and or Internet connection does not work
	 */
	private void analyzeRepo() throws IOException {
		calcRepoSize();
		inflateFileList();
		calcWordCount();
		calcFileEndingCount();
		calcFileNameCount();
	}

	/**
	 * Calculates the repository size by checking the zipball size.
	 * 
	 * @throws IOException
	 */
	private void calcRepoSize() throws IOException {
		URL url = new URL("https://api.github.com/repos/" + repoName + "/zipball");
		URLConnection connection = url.openConnection();
		repoSize = connection.getContentLength();
	}

	/**
	 * Checks for any Zipfiles inside the Zipball. This function does not work
	 * recursive for performance and loophole reasons. It abourts if Zipfiles
	 * are not deflateable.
	 */
	private void inflateFileList() {
		ArrayList<VirtualFile> res = new ArrayList<>();
		res.addAll(fileList);
		for (Iterator<VirtualFile> iterator = fileList.iterator(); iterator.hasNext();) {
			try {
				VirtualFile f = iterator.next();
				if (f.getType() == FileType.Zip) {

					ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(f.getData()));
					ZipEntry entry = zipIn.getNextEntry();
					while (entry != null) {
						String filePath = entry.getName();
						if (!entry.isDirectory()) {
							byte[] data = IOUtils.toByteArray(zipIn);
							res.add(new VirtualFile(new File(filePath).getName(), data, false));
						} else {
							res.add(new VirtualFile(entry.getName(), null, true));
						}
						zipIn.closeEntry();
						entry = zipIn.getNextEntry();
					}
					zipIn.close();
				}
			} catch (Exception e) {
				// Abort deflation in case of an error
			}
		}
		fileList = res;
		fileCount = fileList.size();
	}

	/**
	 * Counts the occurrence of different file endings for all found files
	 */
	private void calcFileEndingCount() {
		WordCounter endingCounter = new WordCounter();
		for (VirtualFile c : fileList) {
			if (c.getType() != FileType.Folder) {
				String name = c.getName();
				String ending;
				if (name.contains(".")) {
					ending = name.substring(name.lastIndexOf("."));
				} else {
					ending = "fileHasNoEnding";
				}
				endingCounter.feed(ending);
			}
		}
		endingCounter.close();
		fileEndingCount = endingCounter.getEntryMap();
	}

	/**
	 * Counts the occurrence of different words for all found Files using the
	 * analyzer classes to extract the raw strings.
	 */
	private void calcWordCount() {
		WordCounter wordCounter = new WordCounter();
		for (VirtualFile f : fileList) {
			try {
				if (f.getName().toLowerCase().equals("readme.md")) {
					StringBuilder builder = new StringBuilder();
					for (int i = 1; i <= 5; i++) {
						builder.append(new String(f.getData()));
					}
					wordCounter.feed(builder.toString());
				} else if (f.getType() == FileType.Text) {
					wordCounter.feed(new String(f.getData()));
				} else if (f.getType() == FileType.Word) {
					DocxAnalyzer ana = new DocxAnalyzer(f.getData());
					wordCounter.feed(ana.getRawText());
					mediaCount += ana.getImages().size();
				} else if (f.getType() == FileType.PDF) {
					PDFAnalyzer ana = new PDFAnalyzer(f.getData());
					wordCounter.feed(ana.getRawText());
					mediaCount += ana.getImageCount();
				} else if (f.getType() == FileType.Image) {
					mediaCount++;
				} else if (f.getType() == FileType.PowerPoint) {
					PptxAnalyzer ana = new PptxAnalyzer(f.getData());
					wordCounter.feed(ana.getRawText());
					mediaCount += ana.getImages().size();
				}
			} catch (Exception e) {
				// any errors are ignored since some files may be damaged or
				// protected
			}
		}
		wordCounter.close();
		wordCount = wordCounter.getEntryMap();
		totalWordCount = wordCounter.getTotalWordCount();
		numberCount = wordCounter.getNumberCount();
	}

	/**
	 * Counts the occurrence of different file names for all found files
	 */
	private void calcFileNameCount() {
		WordCounter nameCounter = new WordCounter();
		for (VirtualFile f : fileList) {
			String name = new File(f.getName()).getName();
			if (name.contains(".")) {
				nameCounter.feed(name.substring(0, name.lastIndexOf(".")));
			} else {
				nameCounter.feed(name);
			}
		}
		nameCounter.close();
		fileNameCount = nameCounter.getEntryMap();
	}

	/**
	 * Returns a deflated list of files contained inside the repository
	 * <strong>Note</strong>: If this instance was obtained by the
	 * {@link RepoCacher} this method will return null.
	 * 
	 * @return all files contained inside the repository.
	 */
	public List<VirtualFile> getFullContent() {
		return fileList;
	}

	/**
	 * Returns the absolute word count of all words found in any file of the
	 * repository
	 * 
	 * @return the repository's word count
	 */
	public HashMap<String, Integer> getWordCount() {
		return wordCount;
	}

	/**
	 * Returns the absolute file ending count of all files found inside the
	 * repository
	 * 
	 * @return the repository's file ending count
	 */
	public HashMap<String, Integer> getFileEndingCount() {
		return fileEndingCount;
	}

	/**
	 * Returns the absolute file name count of all files found inside the
	 * repository
	 * 
	 * @return the repository's file name count
	 */
	public HashMap<String, Integer> getFileNameCount() {
		return fileNameCount;
	}

	/**
	 * Returns the repository's name
	 * 
	 * @return the repository's name
	 */
	public String getRepoName() {
		return repoName;
	}

	/**
	 * Returns the repository's media count across all files found inside the
	 * repository.
	 * 
	 * @return the repository's media count
	 */
	public int getMediaCount() {
		return mediaCount;
	}

	/**
	 * Returns the total word count of all words found in across all
	 * repositories.
	 * 
	 * @return the repository's total word count
	 */
	public long getTotalWordCount() {
		return totalWordCount;
	}

	/**
	 * Returns the repository's file count
	 * 
	 * @return the repository's file count
	 */
	public int getFileCount() {
		return fileCount;
	}

	/**
	 * Returns the repository's size in bytes based on the zipball size
	 * 
	 * @return the repository's size
	 */
	public int getRepoSize() {
		return repoSize;
	}

	/**
	 * Returns the number count of all numbers found in across all repositories.
	 * 
	 * @return the repository's number count
	 */
	public long getNumberCount() {
		return numberCount;
	}

	/**
	 * Returns a {@link CollectedDataSet} which represents a dataclass
	 * containing all information which the crawler can gather.
	 * 
	 * @return the CollectedDataSet of this crawler
	 */
	public CollectedDataSet getCollectedDataSet() {
		CollectedDataSet set = new CollectedDataSet();
		set.endingCount = getFileEndingCount();
		set.fileCount = getFileCount();
		set.fileNameCount = getFileNameCount();
		set.mediaCount = getMediaCount();
		set.repoSize = repoSize;
		set.totalWordCount = getTotalWordCount();
		set.wordCount = getWordCount();
		set.numberCount = getNumberCount();
		return set;
	}
	
	
	public static void main(String[] args)
			throws Exception {
		Field f = MagicParser.class.getDeclaredField("log");
		f.setAccessible(true);
		RepositoryCrawler crawler = RepoCacher.get("https://github.com/Crigges/Clickwars");
		System.out.println(crawler.getCollectedDataSet());
	}

}
