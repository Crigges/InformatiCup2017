package systems.crigges.informaticup.general;

public enum RepositoryTyp {
	DEV(0), HW(1), EDU(2), DOCS(3), WEB(4), DATA(5), OTHER(6); 
	
	private int value;
	
	private RepositoryTyp(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static RepositoryTyp get(String typ) throws Exception{
		switch(typ){
			case "DEV" : return DEV;
			case "HW" : return HW;
			case "EDU" : return EDU;
			case "DOCS" : return DOCS;
			case "WEB" : return WEB;
			case "DATA" : return DATA;
			case "OTHER" : return OTHER;
			default: throw new Exception("Unknown RepositoryTyp");
		}
		
	}
}
