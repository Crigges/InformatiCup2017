package systems.crigges.informaticup.crawling;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class VirtualFile{
	private String name;
	private byte[] data;
	private SuperMimeType type;
	private String mimeType;
	private boolean isFolder;

	public VirtualFile(String name, byte[] data, boolean folder) {
		this.name = name;
		this.data = data;
		isFolder = folder;
		if (isFolder) {
			type = SuperMimeType.Folder;
		} else if (data == null) {
			type = SuperMimeType.Binary;
		} else {
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
			} else if (name.endsWith("zip") || mimeType.endsWith("zip")) {
				type = SuperMimeType.Zip;
			} else if (mimeType.startsWith("text") || name.endsWith("txt") || name.endsWith("md")) {
				type = SuperMimeType.Text;
			} else {
				type = SuperMimeType.Binary;
				data = null;
			}
		}
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getName() {
		return name;
	}
	
	public SuperMimeType getType() {
		return type;
	}

	/**
	 * Guess whether given file is binary.
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