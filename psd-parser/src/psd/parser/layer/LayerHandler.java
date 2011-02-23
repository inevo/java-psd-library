package psd.parser.layer;

import psd.parser.BlendMode;

import java.util.List;

public interface LayerHandler {
	public void boundsLoaded(int left, int top, int right, int bottom);

	public void blendModeLoaded(BlendMode blendMode);

	public void opacityLoaded(int opacity);

	public void clippingLoaded(boolean clipping);

	public void flagsLoaded(boolean transparencyProtected, boolean visible, boolean obsolete, boolean isPixelDataIrrelevantValueUseful, boolean pixelDataIrrelevant);

	public void nameLoaded(String name);
	
	public void channelsLoaded(List<Channel> channels);

    public void maskLoaded(Mask mask);
}
