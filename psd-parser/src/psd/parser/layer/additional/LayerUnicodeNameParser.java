package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerUnicodeNameParser implements LayerAdditionalInformationParser {

	public static final String TAG = "luni";
	private final LayerUnicodeNameHandler handler;

	public LayerUnicodeNameParser(LayerUnicodeNameHandler handler) {
		this.handler = handler;
	}

	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int len = stream.readInt();
		StringBuilder name = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			name.append((char) stream.readShort());
		}
		if (handler != null) {
			handler.layerUnicodeNameParsed(name.toString());
		}
	}
}
