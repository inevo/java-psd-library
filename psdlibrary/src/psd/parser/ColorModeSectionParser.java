package psd.parser;

import java.io.IOException;

import psd.base.PsdInputStream;

public class ColorModeSectionParser {
	
	public void parse(PsdInputStream stream) throws IOException {
		int colorMapLength = stream.readInt();
		stream.skipBytes(colorMapLength);
	}
	
}
