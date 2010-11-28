package psd.parser;

import java.io.IOException;

public class ColorModeSectionParser implements Parser {
	
	@Override
	public void parse(PsdInputStream stream) throws IOException {
		int colorMapLength = stream.readInt();
		stream.skipBytes(colorMapLength);
	}
	
}
