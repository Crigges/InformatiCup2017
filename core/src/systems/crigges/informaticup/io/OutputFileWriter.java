package systems.crigges.informaticup.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import systems.crigges.informaticup.general.RepositoryDescriptor;

public class OutputFileWriter {

	private PrintWriter writer = null;
	
	public OutputFileWriter(File writePath) throws FileNotFoundException, UnsupportedEncodingException{
		this.writer = new PrintWriter(writePath, "UTF-8");
	}
	
	public void write(RepositoryDescriptor repo){
		 writer.println(repo.getName() + " " + repo.getTyp());
	}
	
	public void close(){
		writer.flush();
		writer.close();
	}
}
