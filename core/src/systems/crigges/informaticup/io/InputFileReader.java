package systems.crigges.informaticup.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import systems.crigges.informaticup.crawling.RepositoryCrawler;
import systems.crigges.informaticup.general.RepositoryDescriptor;
import systems.crigges.informaticup.general.RepositoryTyp;

import java.util.Map.Entry;

/**
 * This class allows to read repository input files as a list of
 * {@link RepositoryDescriptor}s.
 * 
 * @author Rami Aly & Andre Schurat
 * @see OutputFileWriter
 * @see RepositoryDescriptor
 */
public class InputFileReader {

	private Scanner sc;
	private List<RepositoryDescriptor> repositorys;

	/**
	 * Attaches a new InputFileReader to the given file.
	 * 
	 * @param file
	 *            the input file to be read
	 * @throws Exception
	 *             if any IO error occurs or the file does not match the format
	 */
	public InputFileReader(File file) throws Exception {
		sc = new Scanner(file);
		repositorys = new ArrayList<>();
		analyseRepositoryInput();
	}

	/**
	 * Analyzes the file line by line using a Scanner.
	 * 
	 * @throws Exception
	 *             if any IO error occurs or the file does not match the format
	 */
	private void analyseRepositoryInput() throws Exception {
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String name;
			RepositoryTyp type = null;
			if (line.contains(" ")) {
				name = line.substring(0, line.indexOf(" "));
				type = RepositoryTyp.get(line.substring(line.indexOf(" ") + 1));
			} else {
				name = line;
			}
			repositorys.add(new RepositoryDescriptor(name, type));
		}
	}

	/**
	 * Returns a list of all {@link RepositoryDescriptor}s contained inside the
	 * file.
	 * 
	 * @return all repository descriptors
	 */
	public List<RepositoryDescriptor> getRepositorysAndTypes() {
		return repositorys;
	}

	
	public static void main(String[] args) {
		List<RepositoryDescriptor> list = null;
		try {
			list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (RepositoryDescriptor t : list) {
			RepositoryCrawler crawler;
			try {
				crawler = new RepositoryCrawler(t.getName());
				for (Entry<String, Integer> entry : crawler.getWordCount().entrySet()) {
					System.out.println(entry);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
