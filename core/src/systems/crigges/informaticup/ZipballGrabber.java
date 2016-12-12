package systems.crigges.informaticup;

import java.io.File;
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

import org.apache.commons.io.FileUtils;

import jdk.nashorn.internal.ir.CatchNode;

public class ZipballGrabber {

	public static File grabZipball(String hostUrl) throws IOException {
		File zip = new File("zip.zip");//File.createTempFile("wurst", "kuchen");
		FileUtils.copyURLToFile(new URL(hostUrl), zip);
		File extractFolder = com.google.common.io.Files.createTempDir();
		UnzipUtility.unzip(zip.getAbsolutePath(), extractFolder.getAbsolutePath());
		zip.delete();
		extractFolder.deleteOnExit();
		return extractFolder;
	}
	
	public static void main(String[] args) throws IOException {
		grabZipball("https://api.github.com/repos/spring-projects/spring-boot/zipball");
	}

}
