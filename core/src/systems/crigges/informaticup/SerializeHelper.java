package systems.crigges.informaticup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

public class SerializeHelper {

	public static void serialize(String pfad,  Object input) throws IOException{
		
		File f = new File(pfad);
		f.createNewFile();
		FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(f));
		out.writeObject(input);
		out.close();
		out.close();
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String pfad){
		T words = null;
		File f = new File(pfad);
		try {
			FSTObjectInput in = new FSTObjectInput(new FileInputStream(f));
			words = (T) in.readObject();
			in.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		return words;
	}
}
