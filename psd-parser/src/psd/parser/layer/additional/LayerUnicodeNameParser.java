package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerUnicodeNameParser implements LayerAdditionalInformationParser {
	
	public static final String TAG = "luni";
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int len = stream.readInt();
		String name = "";
		for (int i = 0; i < len; i++) {
			name += (char) stream.readShort();
		}
	}

}
