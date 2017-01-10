package systems.crigges.informaticup.crawling;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

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
	public static ArrayList<VirtualFile> grabVirtual(String hostUrl) throws IOException {
		URL url = new URL(hostUrl);
		URLConnection connection = url.openConnection();

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
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
		return files;
	}
	
}
