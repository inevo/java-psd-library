package psd.parser.layer;

import java.io.IOException;

import psd.parser.PsdInputStream;

public interface LayerAdditionalInformationParser {
	public void parse(PsdInputStream stream, String tag, int size) throws IOException;
}
