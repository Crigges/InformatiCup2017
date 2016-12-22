package systems.crigges.informaticup;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class CollectedDataSet {
	public int repoSize;
	public int fileCount;
	public int mediaCount;
	public long totalWordCount;
	public int subscribedCount;
	public int staredCount;
	
	public Set<Entry<String, Integer>> wordCount;
	public Set<Entry<String, Integer>> endingCount;
	public Set<Entry<String, Integer>> folderNameCount;
	public Set<Entry<String, Integer>> emailEndingCount;

}
