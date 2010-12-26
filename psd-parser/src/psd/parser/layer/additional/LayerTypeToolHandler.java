package psd.parser.layer.additional;

import psd.parser.object.PsdDescriptor;

public interface LayerTypeToolHandler {

	public void typeToolTransformParsed(Matrix transform);
	public void typeToolDescriptorParsed(int version, PsdDescriptor descriptor);

}
