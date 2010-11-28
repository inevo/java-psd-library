package psd_demo;

import psd.image.Layer;

public class NamedPsdLayer {

	private final Layer layer;
	
	private String name;
	
	public NamedPsdLayer(Layer layer) {
		this.layer = layer;
	}
	
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

	/**
	 * @return the layer
	 */
	public Layer getLayer() {
		return layer;
	}

	@Override
	public String toString() {
		if (this.name != null) {
			return this.name;
		}
		else if (this.layer != null) {
			return this.layer.getName();
		}
		else {
			return super.toString();
		}
	}
	
}
