package systems.crigges.informaticup.gui;

import systems.crigges.informaticup.crawling.FileType;

public interface CrawlerListener {
	
	void setMaxDownloadProgress(long l);
	
	void setCurrentDownloadProgres(long current);
	
	void extractedEntryFromZipBall(String name, FileType fileType);
	
	void downloadStarted();
	
	void downloadFinished();
	
	void wordCountStarted();
	
	void wordCountFinished();
	
	void wordAdded(String w);
	
}
