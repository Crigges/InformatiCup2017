package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class VirtualFile{
	String name;
	byte[] data;
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
		if(isFolder){
			type = SuperMimeType.Folder;
			size = 0;
		}else if(data == null){
			type = SuperMimeType.Binary;
			size = orgSize;
		}else{
//			try {
//				Files.write(new File("./test/" + name).toPath(), data, StandardOpenOption.CREATE);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			size = data.length;
			MagicMatch match;
			try {
				match = Magic.getMagicMatch(data);
				mimeType = match.getMimeType();
			} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
				mimeType = "unknown";
			}
			name = name.toLowerCase();
			if(name.endsWith("pdf")){
				type = SuperMimeType.PDF;
			}else if(name.endsWith("doc") || name.endsWith("docx")){
				type = SuperMimeType.Word;
			}else if(name.endsWith("ppt") || name.endsWith("pptx")){
				type = SuperMimeType.PowerPoint;
			}else if(name.endsWith("png") || name.endsWith("jpg") || name.startsWith("image")){
				type = SuperMimeType.Image;
			}else if(name.endsWith("rar")){
				type = SuperMimeType.Rar;
			}else if(name.endsWith("zip") || mimeType.endsWith("zip")){
				type = SuperMimeType.Zip;
			}else if(mimeType.startsWith("text")){
				type = SuperMimeType.Text;
			}else{
				type = SuperMimeType.Binary;
			}
		}
	}
}
