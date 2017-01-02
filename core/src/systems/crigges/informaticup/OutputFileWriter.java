package systems.crigges.informaticup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class OutputFileWriter {

	private PrintWriter writer = null;
	
	public OutputFileWriter(File writePath) throws FileNotFoundException, UnsupportedEncodingException{
		this.writer = new PrintWriter(writePath, "UTF-8");
	}
	
	public void write(String repositoryName, String type){
		 writer.println(repositoryName + " " + type);
	}
	
	public void close(){
		writer.close();
	}
}
