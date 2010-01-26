package psd.layer;

public class PsdLayerFrameInfo {

	private Boolean visible;
	private Integer xOffset;
	private Integer yOffset;
	
	public PsdLayerFrameInfo(Integer xOffset, Integer yOffset, Boolean visible) {
		this.visible = visible;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
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
