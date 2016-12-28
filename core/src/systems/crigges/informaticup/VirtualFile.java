package systems.crigges.informaticup;

import java.io.Serializable;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class VirtualFile implements Serializable{
	private static final long serialVersionUID = 1L;
	String name;
	transient byte[] data;
	SuperMimeType type;
	String mimeType;
	int size;
	boolean isFolder;

	public VirtualFile(String name, byte[] data, boolean folder) {
		this(name, data, folder, 0);
	}

	public VirtualFile(String name, byte[] data, boolean folder, int orgSize) {
		this.name = name;
		this.data = data;
		isFolder = folder;
		if (isFolder) {
			type = SuperMimeType.Folder;
			size = 0;
		} else if (data == null) {
			type = SuperMimeType.Binary;
			size = orgSize;
		} else {
			// try {
			// Files.write(new File("./test/" + name).toPath(), data,
			// StandardOpenOption.CREATE);
			// } catch (IOException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			size = data.length;
			MagicMatch match;
			try {
				match = Magic.getMagicMatch(data);
				mimeType = match.getMimeType();
			} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
				if (isFileBinary(data)) {
					mimeType = "unknown";
				}else{
					mimeType = "text";
				}	
			}
			name = name.toLowerCase();
			if (name.endsWith("pdf")) {
				type = SuperMimeType.PDF;
			} else if (name.endsWith("docx")) {
				type = SuperMimeType.Word;
			} else if (name.endsWith("pptx")) {
				type = SuperMimeType.PowerPoint;
			} else if (name.endsWith("png") || name.endsWith("jpg") || name.startsWith("image")) {
				type = SuperMimeType.Image;
				data = null;
			} else if (name.endsWith("rar")) {
				type = SuperMimeType.Rar;
				data = null;
			} else if (name.endsWith("zip") || mimeType.endsWith("zip")) {
				type = SuperMimeType.Zip;
			} else if (mimeType.startsWith("text") || name.endsWith("txt")) {
				type = SuperMimeType.Text;
			} else {
				type = SuperMimeType.Binary;
				data = null;
			}
		}
	}

	/**
	 * Guess whether given file is binary. Just checks for anything under 0x09.
	 */
	public static boolean isFileBinary(byte[] data) {
		int size = data.length;
		if (size > 1024) {
			size = 1024;
		}
		int ascii = 0;
		int other = 0;
		for (int i = 0; i < size; i++) {
			byte b = data[i];
			if (b < 0x09)
				return true;

			if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
				ascii++;
			else if (b >= 0x20 && b <= 0x7E)
				ascii++;
			else
				other++;
		}
		if (other == 0){
			return false;
		}
		return 100 * other / (ascii + other) > 95;
	}
}
