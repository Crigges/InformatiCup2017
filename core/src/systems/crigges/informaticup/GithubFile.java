package systems.crigges.informaticup;

public class GithubFile implements Comparable<GithubFile> {
	private static final String folderMode = "040000";
	
	public String path;
	public String mode;
	public String type;
	public String sha;
	public int size;
	public String url;
	
	
	@Override
	public int compareTo(GithubFile o) {
		if(size == o.size){
			return 0;
		}else if(size > o.size){
			return 1;
		}else{
			return -1;
		}
	}
	
	public boolean isFolder(){
		return mode.equals(folderMode);
	}
}
