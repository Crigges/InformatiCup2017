package systems.crigges.informaticup.crawling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import systems.crigges.informaticup.gui.CrawlerListener;
import systems.crigges.informaticup.gui.RepositoryInfo;

/**
 * This class access the Github API to gather the Zipball for a given
 * repository. It supports on the fly deflating and stores the file Virtually
 * for faster processing.
 * 
 * @author Rami Aly & Andre Schurat
 * @see VirtualFile
 */
public class ZipballGrabber {

	/**
	 * Grabs the Zipball for the given repository and deflates it into a list of
	 * {@link VirtualFile}s
	 * 
	 * @param hostUrl
	 *            the host url to the repository
	 * @return a list of {@link VirtualFile}s contained inside the zipball
	 * @throws IOException
	 *             if any connection problems occur or the url is invaild
	 */
	public static ArrayList<VirtualFile> grabVirtual(String repoName, CrawlerListener listener) throws IOException {
		URL url = new URL("https://api.github.com/repos/" + repoName + "/zipball");
		URLConnection connection = url.openConnection();
		RepositoryInfo info = null;
		long total = 0;
		if(listener != null){
			URL infoUrl = new URL("https://api.github.com/repos/" + repoName);
			URLConnection infoConnection = infoUrl.openConnection();
			Gson gson = new Gson();
			info = gson.fromJson(new BufferedReader(new InputStreamReader(infoConnection.getInputStream())), RepositoryInfo.class);
			infoConnection.getInputStream();
			listener.downloadStarted();
			listener.setMaxDownloadProgress(info.size * 1024L);
		}
		ArrayList<VirtualFile> files = new ArrayList<>();
		ZipInputStream zipIn = new ZipInputStream(connection.getInputStream());
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = "." + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				byte[] data = IOUtils.toByteArray(zipIn);
				files.add(new VirtualFile(new File(filePath).getName(), data, false));
			} else {
				files.add(new VirtualFile(entry.getName(), null, true));
			}
			if(listener != null){
				total+= entry.getSize();
				listener.setCurrentDownloadProgres(total);
				listener.extractedEntryFromZipBall(new File(filePath).getName(), files.get(files.size() - 1).getType());
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
		if(listener != null){
			listener.setCurrentDownloadProgres(info.size * 1024L);
			listener.downloadFinished();
		}
		return files;
	}
	
}
