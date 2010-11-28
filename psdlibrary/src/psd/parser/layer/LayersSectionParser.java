package psd.parser.layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import psd.parser.*;

public class LayersSectionParser implements Parser {
	
	private LayerSectionHandler handler;
	private int psdWidth;
	private int psdHeight;
	private int channelsCount;
	
	public void setHandler(LayerSectionHandler handler) {
		this.handler = handler;
	}
	
	public void setPsdWidth(int psdWidth) {
		this.psdWidth = psdWidth;
	}
	
	public void setPsdHeight(int psdHeight) {
		this.psdHeight = psdHeight;
	}
	
	public void setChannelsCount(int channelsCount) {
		this.channelsCount = channelsCount;
	}
	
	@Override
	public void parse(PsdInputStream psdStream) throws IOException {
		// read layer header info
		int length = psdStream.readInt();
		int pos = psdStream.getPos();

		if (length > 0) {

			int size = psdStream.readInt();
			if ((size & 0x01) != 0) {
				size++;
			}
			if (size > 0) {
				int layersCount = psdStream.readShort();
				if (layersCount < 0) {
					layersCount = -layersCount;
				}
				
				List<LayerParser> parsers = new ArrayList<LayerParser>(layersCount);
				for (int i = 0; i < layersCount; i++) {
					LayerParser layerParser = new LayerParser();
					parsers.add(layerParser);
					handler.createLayer(layerParser);
					layerParser.parse(psdStream);
				}
				
				for (LayerParser parser : parsers) {
					parser.parseImageSection(psdStream);
				}
			}

			int maskSize = length - (psdStream.getPos() - pos);
			psdStream.skipBytes(maskSize);
		}
		
//		tmpBaseLayer = new PsdLayer(psdWidth, psdHeight, channelsCount);
//		this.numberOfChannels = numberOfChannels;
//
//		channelsInfo = new ArrayList<ChannelInfo>(numberOfChannels);
//		for (int j = 0; j < numberOfChannels; j++) {
//			channelsInfo.add(new ChannelInfo(j == 3 ? -1 : j));
//		}
//		
//		//run-length-encoding
//		boolean rle = psdStream.readShort() == 1;
//		if (rle) {
//			int nLines = tmpBaseLayer.getHeight() * tmpBaseLayer.getNumberOfChannels();
//			short[] lineLengths = new short[nLines];
//			
//			for (int i = 0; i < nLines; i++) {
//				lineLengths[i] = psdStream.readShort();
//			}
//			
//			// TODO tmpBaseLayer.readImage(psdStream, false, lineLengths);
//		} else {
//			// TODO tmpBaseLayer.readImage(psdStream, false, null);
//		}
//
//		if (tmpLayers == null) {
//			tmpLayers = new ArrayList<PsdLayer>(1);
//			tmpLayers.add(tmpBaseLayer);
//		}
//		// TODO handler.setBaseLayer(tmpBaseLayer);
	}
}
