package systems.crigges.informaticup.general;

public class RepositoryDescriptor implements Comparable<RepositoryDescriptor> {

	private String name;
	private RepositoryTyp type;

	public RepositoryDescriptor(String name, RepositoryTyp typ) {
		this.name = name;
		this.type = typ;
	}

	public String getName() {
		return name;
	}

	public RepositoryTyp getTyp() {
		return type;
	}

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

	public void setType(RepositoryTyp type) {
		this.type = type;
	}
}
