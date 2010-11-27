package psd.parser;

import java.io.IOException;
import java.util.ArrayList;

import psd.layer.PsdLayer;

public class LayersSectionParser {
	
	private PsdHandler handler;
	private int psdWidth;
	private int psdHeight;
	private int channelsCount;
	
	public void setHandler(PsdHandler handler) {
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
	
	public void parse(PsdInputStream psdStream) throws IOException {
		// read layer header info
		int length = psdStream.readInt();
		int pos = psdStream.getPos();
		ArrayList<PsdLayer> tmpLayers=null;
		PsdLayer tmpBaseLayer;
		
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
				
				tmpLayers=new ArrayList<PsdLayer>(layersCount);
				for (int i = 0; i < layersCount; i++) {
					PsdLayer layer = new PsdLayer(psdStream);
					tmpLayers.add(layer);
				}
				
				for (PsdLayer layer : tmpLayers) {
					layer.readImage(psdStream);
				}
				handler.setLayers(tmpLayers);
			}

			int maskSize = length - (psdStream.getPos() - pos);
			psdStream.skipBytes(maskSize);
		}
		
		tmpBaseLayer = new PsdLayer(psdWidth, psdHeight, channelsCount);
		
		//run-length-encoding
		boolean rle = psdStream.readShort() == 1;
		if (rle) {
			int nLines = tmpBaseLayer.getHeight() * tmpBaseLayer.getNumberOfChannels();
			short[] lineLengths = new short[nLines];
			
			for (int i = 0; i < nLines; i++) {
				lineLengths[i] = psdStream.readShort();
			}
			
			tmpBaseLayer.readImage(psdStream, false, lineLengths);
		} else {
			tmpBaseLayer.readImage(psdStream, false, null);
		}

		if (tmpLayers == null) {
			tmpLayers = new ArrayList<PsdLayer>(1);
			tmpLayers.add(tmpBaseLayer);
		}
		handler.setBaseLayer(tmpBaseLayer);
	}
}
