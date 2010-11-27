package psd.parser;

import java.io.*;

import psd.parser.header.HeaderSectionParser;
import psd.parser.layer.LayersSectionParser;

public class PsdFileParser {
	private HeaderSectionParser headerParser;
	private ColorModeSectionParser colorModeSectionParser;
	private ImageResourceSectionParser imageResourceSectionParser;
	private LayersSectionParser layersSectionParser;
	
	public PsdFileParser() {
		headerParser = new HeaderSectionParser();
		colorModeSectionParser = new ColorModeSectionParser();
		imageResourceSectionParser = new ImageResourceSectionParser();
		layersSectionParser = new LayersSectionParser();
	}
	
	public HeaderSectionParser getHeaderParser() {
		return headerParser;
	}
	
	public void setPsdHandler(PsdHandler handler) {
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
