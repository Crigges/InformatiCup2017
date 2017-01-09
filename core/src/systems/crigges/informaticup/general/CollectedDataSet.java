package systems.crigges.informaticup.general;

import java.util.Map.Entry;
import java.util.Set;

public class CollectedDataSet {
	public int repoSize;
	public int fileCount;
	public int mediaCount;
	public long totalWordCount;
	public int subscribedCount;
	public int staredCount;
	public long numberCount;
	public RepositoryTyp repositoryType;
	
	public Set<Entry<String, Integer>> wordCount;
	public Set<Entry<String, Integer>> endingCount;
	public Set<Entry<String, Integer>> fileNameCount;
	
	@Override
	public String toString() {
		return "CollectedDataSet" +  System.lineSeparator() +  "[repoSize=" + repoSize + ", fileCount=" + fileCount + ", mediaCount=" + mediaCount
				+ ", totalWordCount=" + totalWordCount + ", subscribedCount=" + subscribedCount + ", staredCount="
				+ staredCount + System.lineSeparator() + "wordCount=" + wordCount + System.lineSeparator() + "endingCount=" + endingCount +  System.lineSeparator() + "fileNameCount="
				+ fileNameCount + "]";
	}
	
}
