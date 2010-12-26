package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerIdParser implements LayerAdditionalInformationParser {

	public static final String TAG = "lyid";
	private final LayerIdHandler handler;
	
	public LayerIdParser(LayerIdHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int layerId = stream.readInt();
		if (handler != null) {
			handler.layerIdParsed(layerId);
		}
	}

}
