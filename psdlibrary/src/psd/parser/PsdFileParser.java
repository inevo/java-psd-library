package psd.parser;

import java.io.*;

public class PsdFileParser {
	private HeaderParser headerParser;
	private ColorModeSectionParser colorModeSectionParser;
	private ImageResourceSectionParser imageResourceSectionParser;
	private LayersSectionParser layersSectionParser;
	
	public PsdFileParser() {
		headerParser = new HeaderParser();
		colorModeSectionParser = new ColorModeSectionParser();
		imageResourceSectionParser = new ImageResourceSectionParser();
		layersSectionParser = new LayersSectionParser();
	}
	
	public void setPsdHandler(PsdHandler handler) {
		headerParser.setHandler(handler);
		imageResourceSectionParser.setHandler(handler);
		layersSectionParser.setHandler(handler);
	}

	public void parse(InputStream inputStream) throws IOException {
		PsdInputStream stream = new PsdInputStream(inputStream);
		headerParser.parse(stream);
		colorModeSectionParser.parse(stream);
		imageResourceSectionParser.parse(stream);
		layersSectionParser.parse(stream);
	}
}
