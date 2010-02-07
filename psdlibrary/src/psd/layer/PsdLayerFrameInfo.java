package psd.layer;

public class PsdLayerFrameInfo {

	private int id;
	private Boolean visible;
	private Integer xOffset;
	private Integer yOffset;
	
	public PsdLayerFrameInfo(int id, Integer xOffset, Integer yOffset, Boolean visible) {
		this.id = id;
		this.visible = visible;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public int getId() {
		return id;
	}
	
	public Boolean isVisible() {
		return visible;
	}
	
	public Integer getXOffset() {
		return xOffset;
	}
	
	public Integer getYOffset() {
		return yOffset;
	}
}
