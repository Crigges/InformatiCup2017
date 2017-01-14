package systems.crigges.informaticup.general;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.crawling.RepositoryCrawler;

/**
 * This class is represents a public data class containing all Features. It's
 * generated by the {@link RepositoryCrawler}.
 * 
 * @author Andre Schurat & Rami Aly
 */
public class CollectedDataSet {
	public int repoSize;
	public int fileCount;
	public int mediaCount;
	public long totalWordCount;
	public long numberCount;
	public RepositoryTyp repositoryType;

	public HashMap<String, Integer> wordCount;
	public HashMap<String, Integer> endingCount;
	public HashMap<String, Integer> fileNameCount;

	@Override
	public String toString() {
		return "CollectedDataSet" + System.lineSeparator() + "[repoSize=" + repoSize + ", fileCount=" + fileCount
				+ ", mediaCount=" + mediaCount + ", totalWordCount=" + totalWordCount + System.lineSeparator()
				+ "wordCount=" + wordCount + System.lineSeparator() + "endingCount=" + endingCount
				+ System.lineSeparator() + "fileNameCount=" + fileNameCount + "]";
	}

}
