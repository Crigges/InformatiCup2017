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

public class InputFileReader {

	private Scanner sc;
	private List<RepositoryDescriptor> repositorys;

	public InputFileReader(File file) throws Exception {
		sc = new Scanner(file);
		repositorys = new ArrayList<>();
		analyseRepositoryInput();
	}

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
				for (Entry<String, Integer> entry : crawler.getWordCount()) {
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
