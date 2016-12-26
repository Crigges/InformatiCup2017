package systems.crigges.informaticup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputFileReader {

	private Scanner sc;
	private List<Repository> repositorys;

	public InputFileReader(File file) throws Exception {
		sc = new Scanner(file);
		repositorys = new ArrayList<>();
		analyseRepositoryInput();
	}

	static class Repository {

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
	}

	private void analyseRepositoryInput() throws Exception {
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String typ = null, name;
			if (line.contains(" ")) {
				name = line.substring(0, line.indexOf(" "));
				typ = line.substring(line.indexOf(" "));
			} else {
				name = line;
			}
			repositorys.add(new Repository(name, RepositoryTyp.get(typ)));
		}
	}

	public List<Repository> getRepositorysAndTypes() {
		return repositorys;
	}

}
