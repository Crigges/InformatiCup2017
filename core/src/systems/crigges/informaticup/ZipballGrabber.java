package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import jdk.nashorn.internal.ir.CatchNode;

public class ZipballGrabber {

//	public static File grabZipball(String hostUrl) throws IOException {
//		File zip = File.createTempFile("wurst", "kuchen");
//		FileUtils.copyURLToFile(new URL(hostUrl), zip);
//		File extractFolder = com.google.common.io.Files.createTempDir();
//		UnzipUtility.unzip(zip.getAbsolutePath(), extractFolder.getAbsolutePath());
//		zip.delete();
//		extractFolder.deleteOnExit();
//		return extractFolder;
//	}
//	
	
//	public static File grab(String hostUrl) throws IOException {
//		File extractFolder = com.google.common.io.Files.createTempDir();
//		URL url = new URL(hostUrl);
//		URLConnection connection = url.openConnection();
//		ZipInputStream zipIn = new ZipInputStream(connection.getInputStream());
//		ZipEntry entry = zipIn.getNextEntry();
//		while (entry != null) {
//			String filePath = extractFolder.getAbsolutePath() + File.separator + entry.getName();
//			if (!entry.isDirectory()) {
//				Files.copy(zipIn, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
//			} else {
//				new File(filePath).mkdir();
//			}
//			zipIn.closeEntry();
//			entry = zipIn.getNextEntry();
//		}
//		zipIn.close();
//		extractFolder.deleteOnExit();
//		return extractFolder;
//	}
	
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
	
	public static void main(String[] args) throws IOException {
		//grab("https://api.github.com/repos/spring-projects/spring-boot/zipball");
	}

}
