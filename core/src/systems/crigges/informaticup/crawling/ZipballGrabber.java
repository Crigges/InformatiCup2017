package systems.crigges.informaticup.crawling;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class ZipballGrabber {
	
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
			}else{
				files.add(new VirtualFile(entry.getName(), null, true));
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
		return files;
	}

}
