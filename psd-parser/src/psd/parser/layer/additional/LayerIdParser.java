package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerIdParser implements LayerAdditionalInformationParser {

	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int layerId = stream.readInt();
	}

}
