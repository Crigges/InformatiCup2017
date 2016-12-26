package systems.crigges.informaticup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.sf.jmimemagic.MagicParseException;
import net.sf.jmimemagic.MagicParser;

public class GithubRepoCrawler {

	private ArrayList<VirtualFile> fileList;
	private Gson gson = new Gson();
	private WordCounter wordCounter;
	private int imageCount;

	public GithubRepoCrawler(String url) throws MalformedURLException, IOException {
		fileList = ZipballGrabber.grabVirtual("https://api.github.com/repos/" + getRepoNameFromURL(url) + "/zipball");
		inflateFileList();
		analyzeRepo();
	}
	
	private String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}

	private ArrayList<VirtualFile> getFilesFromTree(String treeUrl) throws IOException {
		URL u = new URL(treeUrl);
		URLConnection fileTreeConnection = u.openConnection();
		InputStream in = fileTreeConnection.getInputStream();
		// Create tree from response
		GithubFileTree tree = gson.fromJson(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8)),
				GithubFileTree.class);
		System.out.println("tree " + tree.tree.size());
		Collections.sort(tree.tree);
		ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();
		for (GithubFile file : tree.tree) {
			String name = new File(file.path).getName();
			if (file.isFolder()) {
				files.add(new VirtualFile(name, null, true));
			} else {
				if (file.size < Constants.MaxFileSize) {
					files.add(getFileFromUrl(name, file.url));
				} else {
					files.add(new VirtualFile(name, null, false, file.size));
				}
			}
		}
		return files;
	}

	private VirtualFile getFileFromUrl(String name, String url) throws IOException {
		URL u = new URL(url);
		URLConnection fileConnection = u.openConnection();
		InputStream in = fileConnection.getInputStream();
		GithubFileContent file = gson.fromJson(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8)),
				GithubFileContent.class);
		file.genByteContent();
		return new VirtualFile(name, file.byteContent, false);
	}

	private void inflateFileList() {
		ArrayList<VirtualFile> res = new ArrayList<>();
		res.addAll(fileList);
		for (Iterator<VirtualFile> iterator = fileList.iterator(); iterator.hasNext();) {
			try {
				VirtualFile f = iterator.next();
				if (f.type == SuperMimeType.Rar) {
//					RARFile rar = new RARFile(new ByteArrayInputStream(f.data));
//					Enumeration<RAREntry> entries = rar.entries();
//					while (entries.hasMoreElements()) {
//						RAREntry entry = (RAREntry) entries.nextElement();
//						if (!entry.isDirectory()) {
//							entry.
//						} else {
//
//						}
//					}
				} else if (f.type == SuperMimeType.Zip) {

					ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(f.data));
					ZipEntry entry = zipIn.getNextEntry();
					while (entry != null) {
						String filePath = entry.getName();
						if (!entry.isDirectory()) {
							byte[] data = IOUtils.toByteArray(zipIn);
							res.add(new VirtualFile(new File(filePath).getName(), data, false));
						} else {
							res.add(new VirtualFile(entry.getName(), null, true));
						}
						zipIn.closeEntry();
						entry = zipIn.getNextEntry();
					}
					zipIn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fileList = res;
	}

	public Set<Entry<String, Integer>> getSortedEndingCount() {
		WordCounter endingCounter = new WordCounter();
		for (VirtualFile c : fileList) {
			if (c.type != SuperMimeType.Folder) {
				String name = c.name;
				String ending;
				if (name.contains(".")) {
					ending = name.substring(name.lastIndexOf("."));
				} else {
					ending = "fileHasNoEnding";
				}
				endingCounter.feed(ending);
			}
		}
		endingCounter.close();
		return endingCounter.getSortedEntrys();
	}

	private void analyzeRepo() {
		wordCounter = new WordCounter();
		for (VirtualFile f : fileList) {
			try {
				if (f.type == SuperMimeType.Text) {
					wordCounter.feed(new String(f.data));
				} else if (f.type == SuperMimeType.Word) {
					DocxAnalyzer ana = new DocxAnalyzer(f.data);
					wordCounter.feed(ana.getRawText());
					imageCount += ana.getImages().size();
				} else if (f.type == SuperMimeType.PDF) {
					PDFAnalyzer ana = new PDFAnalyzer(f.data);
					wordCounter.feed(ana.getRawText());
					imageCount += ana.getImages().size();
				} else if (f.type == SuperMimeType.Image) {
					imageCount++;
				} else if (f.type == SuperMimeType.PowerPoint) {
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		wordCounter.close();
	}

	public List<VirtualFile> getFullContent() {
		return fileList;
	}
	
	public void getWordCount() {
		// TODO Auto-generated method stub
		
	}


	public static void main(String[] args) throws MalformedURLException, IOException, MagicParseException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = MagicParser.class.getDeclaredField("log");
		f.setAccessible(true);
		GithubRepoCrawler crawler = new GithubRepoCrawler("https://github.com/Raldir/test01");
		crawler.getWordCount();
		
	
	}

	
}
