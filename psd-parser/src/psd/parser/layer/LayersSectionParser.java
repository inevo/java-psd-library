package psd.parser.layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import psd.parser.*;
import psd.parser.header.Header;

public class LayersSectionParser {

	private LayersSectionHandler handler;
	private final Header header;

	public LayersSectionParser(Header header) {
		this.header = header;
	}

	public void setHandler(LayersSectionHandler handler) {
		this.handler = handler;
	}

	public void parse(PsdInputStream stream) throws IOException {
		// read layer header info
		int length = stream.readInt();
		int pos = stream.getPos();

		if (length > 0) {
			int size = stream.readInt();
			if ((size & 0x01) != 0) {
				size++;
			}
			if (size > 0) {
				int layersCount = stream.readShort();
				if (layersCount < 0) {
					layersCount = -layersCount;
				}

				List<LayerParser> parsers = new ArrayList<LayerParser>(layersCount);
				for (int i = 0; i < layersCount; i++) {
					LayerParser layerParser = new LayerParser();
					parsers.add(layerParser);
					if (handler != null) {
						handler.createLayer(layerParser);
					}
					layerParser.parse(stream);
				}

				for (LayerParser layerParser : parsers) {
					layerParser.parseImageSection(stream);
				}
			}

			int maskSize = length - (stream.getPos() - pos);
			stream.skipBytes(maskSize);
		}

		parseBaseLayer(stream);
	}

	private void parseBaseLayer(PsdInputStream stream) throws IOException {
		LayerParser baseLayerParser = new LayerParser();
		if (handler != null) {
			handler.createBaseLayer(baseLayerParser);
		}
		baseLayerParser.fireBoundsChanged(0, 0, header.getWidth(), header.getHeight());

		ArrayList<Channel> channels = new ArrayList<Channel>(header.getChannelsCount());
		for (int j = 0; j < header.getChannelsCount(); j++) {
			channels.add(new Channel(j == 3 ? -1 : j));
		}
		//run-length-encoding
		boolean rle = stream.readShort() == 1;
		short[] lineLengths = null;
		if (rle) {
			int nLines = header.getHeight() * header.getChannelsCount();
			lineLengths = new short[nLines];

			for (int i = 0; i < nLines; i++) {
				lineLengths[i] = stream.readShort();
			}
		}

		ImagePlaneParser planeParser = new ImagePlaneParser(stream);
		int planeNumber = 0;
		for (Channel c : channels) {
			c.setData(planeParser.readPlane(header.getWidth(), header.getHeight(), lineLengths, planeNumber));
			planeNumber++;
		}
		baseLayerParser.fireChannelsLoaded(channels);
	}
}
