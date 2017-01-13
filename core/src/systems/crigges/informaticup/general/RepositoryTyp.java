package systems.crigges.informaticup.general;

/**
 * This enum describes the available repository types.
 * 
 * @author Rami Aly & Andre Schurat
 */
public enum RepositoryTyp {
	DEV(0), HW(1), EDU(2), DOCS(3), WEB(4), DATA(5), OTHER(6);

	private int value;

	private RepositoryTyp(int value) {
		this.value = value;
	}

	/**
	 * Returns the type's value which can be used as index
	 * 
	 * @return the type's value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Parses the RepositoryType from the given string and returns it.
	 * 
	 * @param typ
	 *            the RepositoryType's representation as String
	 * @return the parsed RepositoryTyp
	 * @throws Exception
	 *             if the String does not contain a vaild RepositoryTyp
	 */
	public static RepositoryTyp get(String typ) throws Exception {
		switch (typ) {
		case "DEV":
			return DEV;
		case "HW":
			return HW;
		case "EDU":
			return EDU;
		case "DOCS":
			return DOCS;
		case "WEB":
			return WEB;
		case "DATA":
			return DATA;
		case "OTHER":
			return OTHER;
		default:
			throw new Exception("Unknown RepositoryTyp");
		}

	}
}
