package systems.crigges.informaticup.crawling;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

/**
 * This class is used to save files virtually inside the heap to improve the
 * performance of further processing. Additionally it tries to figure out the
 * file's {@link FileType} using the Internet Media Type and analyzing file
 * endings.
 * 
 * @author Rami Aly & Andre Schurat
 * @see FileType
 * @see ZipballGrabber
 */
public class VirtualFile {
	private String name;
	private byte[] data;
	private FileType type;
	private String mimeType;
	private boolean isFolder;

	/**
	 * Creates a new VirtualFile with the given name. The {@link FileType} is
	 * guess automatically. If the FileType reveals that no further processing
	 * is possible the file's content is removed from the heap.
	 * 
	 * @param name
	 *            the name of the file or folder
	 * @param data
	 *            the data of the file, may be null if it is a folder
	 * @param folder
	 *            weather it is a folder or not
	 */
	public VirtualFile(String name, byte[] data, boolean folder) {
		this.name = name;
		this.data = data;
		isFolder = folder;
		if (isFolder) {
			type = FileType.Folder;
		} else if (data == null) {
			type = FileType.Binary;
		} else {
			MagicMatch match;
			try {
				match = Magic.getMagicMatch(data);
				mimeType = match.getMimeType();
			} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
				if (isFileBinary()) {
					mimeType = "unknown";
				} else {
					mimeType = "text";
				}
			}
			name = name.toLowerCase();
			if (name.endsWith("pdf")) {
				type = FileType.PDF;
			} else if (name.endsWith("docx")) {
				type = FileType.Word;
			} else if (name.endsWith("pptx")) {
				type = FileType.PowerPoint;
			} else if (name.endsWith("png") || name.endsWith("jpg") || name.startsWith("image")) {
				type = FileType.Image;
				data = null;
			} else if (name.endsWith("zip") || mimeType.endsWith("zip")) {
				type = FileType.Zip;
			} else if (mimeType.startsWith("text") || name.endsWith("txt") || name.endsWith("md")) {
				type = FileType.Text;
			} else {
				type = FileType.Binary;
				data = null;
			}
		}
	}

	/**
	 * Returns the file's content as byte array. May be null if {@link FileType}
	 * is binary or this represents a folder.
	 * 
	 * @return the file's content as byte array
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Returns the file's name
	 * @return the file's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the file's type
	 * @return the file's type
	 */
	public FileType getType() {
		return type;
	}

	/**
	 * Additionally guess to determinate if file is binary.
	 * @return whether the file is guessed binary
	 */
	private boolean isFileBinary() {
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
		if (other == 0) {
			return false;
		}
		return 100 * other / (ascii + other) > 95;
	}
}
