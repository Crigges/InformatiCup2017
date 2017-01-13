package systems.crigges.informaticup.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import systems.crigges.informaticup.general.RepositoryDescriptor;

/**
 * This class allows to write multiple {@link RepositoryDescriptor}s to a given
 * File.
 * 
 * @author Rami Aly & Andre Schurat
 * @see RepositoryDescriptor
 */
public class OutputFileWriter {

	private PrintWriter writer = null;

	/**
	 * Attaches a new OutputFileWriter to the given file. Existing file are
	 * overridden.
	 * 
	 * @param writePath
	 *            the target file
	 * @throws FileNotFoundException
	 *             if file can't be accessed
	 * @throws UnsupportedEncodingException
	 *             if the running JVM does not support UTF-8
	 */
	public OutputFileWriter(File writePath) throws FileNotFoundException, UnsupportedEncodingException {
		this.writer = new PrintWriter(writePath, "UTF-8");
	}

	/**
	 * Writes the given repository to the file.
	 * 
	 * @param repo
	 *            to be written
	 */
	public void write(RepositoryDescriptor repo) {
		writer.println(repo.getName() + " " + repo.getTyp());
	}

	/**
	 * Closes the writer and forces all submitted repositories to be written to
	 * the file.
	 */
	public void close() {
		writer.flush();
		writer.close();
	}
}
