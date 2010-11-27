package psd.parser;

import java.util.List;

import psd.layer.PsdLayer;
import psd.metadata.PsdAnimation;

public interface PsdHandler {
	public void setAnimation(PsdAnimation animation);
	public void setLayers(List<PsdLayer> layers);
	public void setBaseLayer(PsdLayer baseLayer);
}
