package systems.crigges.informaticup.general;

/**
 * This class describes a repository by it's url and {@link RepositoryTyp}
 * 
 * @author Rami Aly & Andre Schurat
 * @see RepositoryTyp
 */
public class RepositoryDescriptor implements Comparable<RepositoryDescriptor> {

	private String name;
	private RepositoryTyp type;

	/**
	 * Creates new RepositoryDescriptor out of the given url and type. The type
	 * may be null if it is unknown.
	 * 
	 * @param name
	 *            the repository's url
	 * @param typ
	 *            the repository's type
	 */
	public RepositoryDescriptor(String name, RepositoryTyp typ) {
		this.name = name;
		this.type = typ;
	}

	/**
	 * Returns the repository's url
	 * @return the repository's url
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the repository's type may be null if the type is unknown.
	 * @return the repository's type
	 */
	public RepositoryTyp getTyp() {
		return type;
	}

	/**
	 * Return the repository type's index
	 * @return the type's index
	 */
	public int getTypeIndex() {
		return type.getValue();
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof RepositoryDescriptor)) {
			return false;
		}
		return name.equals(((RepositoryDescriptor) obj).name);
	}
	
	@Override
	public int compareTo(RepositoryDescriptor o) {
		return name.compareTo(o.name);
	}

	/**
	 * Sets the {@link RepositoryTyp} for this repository 
	 * @param type the new type
	 */
	public void setType(RepositoryTyp type) {
		this.type = type;
	}
}
