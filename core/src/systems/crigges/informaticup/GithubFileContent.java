package systems.crigges.informaticup;

import org.apache.commons.codec.binary.Base64;

public class GithubFileContent {

	public String sha;
	public int size;
	public String url;
	public String content;
	public String encoding;
	public byte[] byteContent;
	
	public void genByteContent(){
		byteContent = Base64.decodeBase64(content);	
	}


}