package systems.crigges.informaticup;

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
	
	public VirtualFile(String name, byte[] data) {
		this.name = name;
		this.data = data;
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
		}else if(name.endsWith("xls") || name.endsWith("xlsx")){
			type = SuperMimeType.Excel;
		}else if(name.endsWith("ppt") || name.endsWith("pptx")){
			type = SuperMimeType.PowerPoint;
		}else if(name.endsWith("png") || name.endsWith("jpg")){
			type = SuperMimeType.Image;
		}else if(name.endsWith("rar")){
			type = SuperMimeType.Rar;
		}else if(name.endsWith("zip") || mimeType.endsWith("zip")){
			type = SuperMimeType.Zip;
		}else if(mimeType.startsWith("text")){
			type = SuperMimeType.Zip;
		}else{
			type = SuperMimeType.Binary;
		}
	}
}
