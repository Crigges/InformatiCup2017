package systems.crigges.informaticup;

import java.util.List;
import java.util.Map.Entry;

public class CollectedDataSet {
	public int repoSize;
	public int fileCount;
	public int mediaCount;
	public long totalWordCount;
	public int subscribedCount;
	public int staredCount;
	
	public List<Entry<String, Integer>> wordCount;
	public List<Entry<String, Integer>> endingCount;
	public List<Entry<String, Integer>> folderNameCount;
	public List<Entry<String, Integer>> emailEndingCount;

}
