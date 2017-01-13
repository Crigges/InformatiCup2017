package systems.crigges.informaticup.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * Simple wrapper class for FST to support fast (de-)serialization
 * 
 * @author Rami Aly & Andre Schurat
 */
public class SerializeHelper {

	/**
	 * Serializes the given object into the target file using FST. Existing
	 * files get overridden.
	 * 
	 * @param f the target {@link File}
	 * @param input the object to be serialized
	 * @throws IOException if any IO error occurs
	 */
	public static void serialize(File f, Object input) throws IOException {
		f.createNewFile();
		FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(f));
		out.writeObject(input);
		out.close();
	}

	/**
	 * Deserializes the given File into a Object of the given type.
	 * 
	 * @param f the target {@link File}
	 * @throws IOException if any IO error occurs
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(File f) throws FileNotFoundException, IOException, ClassNotFoundException {
		T words = null;
		FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
		words = (T) in.readObject();
		in.close();
		return words;
	}
}
