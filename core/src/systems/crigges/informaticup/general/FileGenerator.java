package systems.crigges.informaticup.general;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import systems.crigges.informaticup.crawling.RepositoryCrawler;
import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.OutputFileWriter;
import systems.crigges.informaticup.io.RepoCacher;

public class FileGenerator {

	public static void main(String[] args) throws Exception {
//		cacheRepositories();
		genTraining();
//		InputFileReader allR = new InputFileReader(new File("./assets/Repositorys.txt"));
//		List<RepositoryDescriptor> res = new ArrayList<>();
//		for (RepositoryDescriptor des : allR.getRepositorysAndTypes()) {
//			if (!res.contains(des)) {
//				res.add(des);
//			}
//		}
//		InputFileReader givenR = new InputFileReader(new File("./assets/GivenTrainRepositorys.txt"));
//		for (RepositoryDescriptor des : givenR.getRepositorysAndTypes()) {
//			res.remove(des);
//		}
//		List<RepositoryDescriptor> realRes = new ArrayList<>();
//		for (RepositoryTyp type : RepositoryTyp.values()) {
//			int c = 0;
//			for (RepositoryDescriptor des : res) {
//				if (des.getTyp() == type) {
//					realRes.add(des);
//					c++;
//					if (c >= 5) {
//						break;
//					}
//				}
//			}
//		}
//		OutputFileWriter writer = new OutputFileWriter(new File("./assets/RightTestRepositorys.txt"));
//		for (RepositoryDescriptor des : realRes) {
//			writer.write(des);
//		}
//		writer.close();
	}
	
	private static void cacheRepositories() throws Exception{
		RepoCacher.initThreadPool(4);
		InputFileReader allR = new InputFileReader(new File("./assets/Repositorys.txt"));
		for (RepositoryDescriptor des : allR.getRepositorysAndTypes()) {
			RepoCacher.getThreaded(des.getName(), (RepositoryCrawler craw) -> {});
		}
		RepoCacher.shutdownThreadPool();
	}
	
	private static void genTraining() throws Exception{
		InputFileReader allR = new InputFileReader(new File("./assets/Repositorys.txt"));
		List<RepositoryDescriptor> res = new ArrayList<>();
		for (RepositoryDescriptor des : allR.getRepositorysAndTypes()) {
			if (!res.contains(des)) {
				res.add(des);
			}
		}
		InputFileReader givenR = new InputFileReader(new File("./assets/RightTestRepositorys.txt"));
		for (RepositoryDescriptor des : givenR.getRepositorysAndTypes()) {
			res.remove(des);
		}
		OutputFileWriter writer = new OutputFileWriter(new File("./assets/TrainingRepositoriesSet2.txt"));
		for (RepositoryDescriptor des : res) {
			writer.write(des);
		}
		writer.close();
	}

}
