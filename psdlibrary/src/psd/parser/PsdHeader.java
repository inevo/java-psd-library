package psd.parser;


public class PsdHeader {
	int channelsCount;
	int width;
	int height;
	int depth;
	PsdColorMode colorMode;
	
	public int getChannelsCount() {
		return channelsCount;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public PsdColorMode getColorMode() {
		return colorMode;
	}
}
