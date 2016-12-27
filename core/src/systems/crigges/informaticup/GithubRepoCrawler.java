package systems.crigges.informaticup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import java.util.Set;

import net.sf.jmimemagic.MagicParseException;
import net.sf.jmimemagic.MagicParser;

public class GithubRepoCrawler implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<VirtualFile> fileList;
	private HashMap<String, Integer> wordCount;
	private long totalWordCount;
	private int imageCount;
	private String repoName;

	public GithubRepoCrawler(String url) throws MalformedURLException, IOException {
		repoName = getRepoNameFromURL(url);
		fileList = ZipballGrabber.grabVirtual("https://api.github.com/repos/" + repoName + "/zipball");
		inflateFileList();
		analyzeRepo();
	}

	private String getRepoNameFromURL(String url) {
		return url.replace("https://github.com/", "");
	}

	private void inflateFileList() {
		ArrayList<VirtualFile> res = new ArrayList<>();
		res.addAll(fileList);
		for (Iterator<VirtualFile> iterator = fileList.iterator(); iterator.hasNext();) {
			try {
				VirtualFile f = iterator.next();
				if (f.type == SuperMimeType.Rar) {
					// RARFile rar = new RARFile(new
					// ByteArrayInputStream(f.data));
					// Enumeration<RAREntry> entries = rar.entries();
					// while (entries.hasMoreElements()) {
					// RAREntry entry = (RAREntry) entries.nextElement();
					// if (!entry.isDirectory()) {
					// entry.
					// } else {
					//
					// }
					// }
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
		WordCounter wordCounter = new WordCounter();
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
					PptxAnalyzer ana = new PptxAnalyzer(f.data);
					wordCounter.feed(ana.getRawText());
					imageCount += ana.getImages().size();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		wordCounter.close();
		wordCount = wordCounter.getEntryMap();
		totalWordCount = wordCounter.getTotalWordCount();
	}

	public List<VirtualFile> getFullContent() {
		return fileList;
	}

	public Set<Entry<String, Integer>> getWordCount() {
		return wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
				.entrySet();
	}

	public String getRepoName() {
		return repoName;
	}

	public int getImageCount() {
		return imageCount;
	}

	public long getTotalWordCount() {
		return totalWordCount;
	}

	public static void main(String[] args) throws MalformedURLException, IOException, MagicParseException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = MagicParser.class.getDeclaredField("log");
		f.setAccessible(true);
		f.set(null, new NoLog());
		GithubRepoCrawler crawler = RepoCacher.get("https://github.com/Raldir/test01");//new GithubRepoCrawler("https://github.com/Raldir/test01");
		//RepoCacher.put(crawler);
		for(Entry<String, Integer> entry : crawler.getWordCount()){
			System.out.println(entry);
		}

	}

}
