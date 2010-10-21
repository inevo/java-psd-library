package psd_demo;

public class NamedVector<K> extends java.util.Vector<K> {

	/** Serial version UID */
	private static final long serialVersionUID = 1L;
	
	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public synchronized String toString() {
		if (this.name != null) {
			return this.name;
		}
		else {
			return super.toString();
		}
	}

}
