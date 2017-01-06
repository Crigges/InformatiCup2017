package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

public class InputFileReader {

	private Scanner sc;
	private List<Repository> repositorys;

	public InputFileReader(File file) throws Exception {
		sc = new Scanner(file);
		repositorys = new ArrayList<>();
		analyseRepositoryInput();
	}

	static class Repository implements Comparable<Repository>{

		private String name;
		private RepositoryTyp typ;

		public Repository(String name, RepositoryTyp typ) {
			this.name = name;
			this.typ = typ;
		}

		public String getName() {
			return name;
		}

		public RepositoryTyp getTyp() {
			return typ;
		}

		public int getTypeIndex() {
			return typ.getValue();
		}

		@Override
		public boolean equals(Object obj) {
			return name.equals(((Repository) obj).name);
		}

		@Override
		public int compareTo(Repository o) {
			return name.compareTo(o.name);
		}
		
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
			repositorys.add(new Repository(name, type));
		}
	}

	public List<Repository> getRepositorysAndTypes() {
		return repositorys;
	}

	public static void main(String[] args) {
		List<Repository> list = null;
		try {
			list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Repository t : list) {
			GithubRepoCrawler crawler;
			try {
				crawler = new GithubRepoCrawler(t.getName());
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
