package psd.parser.layer;

import java.util.List;

public interface LayerHandler {
	public void boundsLoaded(int left, int top, int right, int bottom);

	public void blendModeLoaded(String blendMode);

	public void opacityLoaded(int opacity);

	public void clippingLoaded(boolean clipping);

	public void visibleLoaded(boolean visible);

	public void nameLoaded(String name);
	
	public void channelsLoaded(List<Channel> channels);
}
