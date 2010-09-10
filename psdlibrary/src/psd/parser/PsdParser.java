package psd.parser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import psd.base.PsdImage;
import psd.base.PsdInputStream;
import psd.layer.PsdLayer;
import psd.metadata.PsdAnimation;
import psd.metadata.PsdColorMode;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdParser parses a psd file and fills a PsdFile Object accordingly.
 */
public class PsdParser {
	/** The logger. */
	private static Logger logger = Logger.getLogger("psd");

	/** The psd object that shall be filled. */
	private PsdImage psdObject;

	/** The psd file input stream. */
	private PsdInputStream psdStream;
	
	/**
	 * Instantiates a new psd parser.
	 *
	 * @param psdObject the psd object
	 */
	public PsdParser(PsdImage psdObject, InputStream inputStream) {
		super();
		this.psdObject = psdObject;
		this.psdStream=new PsdInputStream(inputStream);
	}

	/**
	 * Parse the psd file and store all data found.
	 *
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	public void loadPsd() throws IOException {
		double start, end;
		start=System.currentTimeMillis();
		
		if (this.psdStream == null) {
			throw new IllegalArgumentException("Param inputStream must be not null.");
		}
		logger.info("PsdFile: parse header section");
		
		this.parseHeaderSection();
		logger.info("PsdFile: parse color mode section");
		this.parseColorModeSection();
		logger.info("PsdFile: parse image resource section");
		this.parseImageResourceSection();
		logger.info("PsdFile: parse layers section");
		this.parseLayersSection();
		this.setupLayersGroups();
		end=System.currentTimeMillis();
		logger.info("PsdFile: parsing completed within "+( (end-start)/1000)+" seconds.");
		//System.exit(1);
	}
	
	/**
	 * Parse header section.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	private void parseHeaderSection() throws IOException {
		String sig = this.psdStream.readString(4);
		int depth;
		
		if (!sig.equals("8BPS")) {
			throw new IOException("file signature error");
		}

		int ver = this.psdStream.readShort();
		if (ver != 1) {
			throw new IOException("file version error ");
		}

		this.psdStream.skipBytes(6); // reserved
		this.psdObject.setNumberOfChannels(this.psdStream.readShort());
		this.psdObject.setHeight(this.psdStream.readInt());
		this.psdObject.setWidth(this.psdStream.readInt());
		this.psdObject.setDepth(this.psdStream.readShort());
		if (this.psdObject.getDepth() != 8) {
			throw new IOException("unsupported color depth: color depth must be 8");
		}
		int cm = this.psdStream.readShort();
		this.psdObject.setColorMode(PsdColorMode.values()[cm]);
		if (this.psdObject.getColorMode() != PsdColorMode.RGB) {
			throw new IOException("unsupported color mode: must be RGB");
		}
	}

	/**
	 * Parse color mode section.
	 *
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	private void parseColorModeSection() throws IOException {
		// skip
		int colorMapLength = this.psdStream.readInt();
		this.psdStream.skipBytes(colorMapLength);
	}

	/**
	 * Parse image resource section.
	 *
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	private void parseImageResourceSection() throws IOException {
		int length = this.psdStream.readInt();
		int pos = this.psdStream.getPos();
		while (length > 0) {
			String tag = this.psdStream.readString(4);
			if (!tag.equals("8BIM") && !tag.equals("MeSa")) {
				throw new IOException("Format error: Invalid image resources section.: " + tag);
			}
			length -= 4;
			int id = this.psdStream.readShort();
			length -= 2;
			int sizeOfName = this.psdStream.readByte() & 0xFF;
			if ((sizeOfName & 0x01) == 0)
				sizeOfName++;
			/* String name = */this.psdStream.readString(sizeOfName);
			length -= sizeOfName + 1;
			int sizeOfData = this.psdStream.readInt();
			length -= 4;
			if ((sizeOfData & 0x01) == 1)
				sizeOfData++;
			length -= sizeOfData;
			int storePos = this.psdStream.getPos();
			if (sizeOfData > 0 && tag.equals("8BIM") && id >= 4000 && id < 5000) { // TODO FIXME Is id correct ?
				byte[] data = new byte[sizeOfData];
				this.psdStream.read(data);

				PsdInputStream st = new PsdInputStream(new ByteArrayInputStream(data));
				String key = st.readString(4);
				if (key.equals("mani")) {
					this.psdObject.setAnimation(new PsdAnimation(st));
				}
			}
			this.psdStream.skipBytes(sizeOfData - (this.psdStream.getPos() - storePos));

		}
		int skipSize = length - (this.psdStream.getPos() - pos);
		this.psdStream.skipBytes(skipSize);
	}

	/**
	 * Parse layers section and store all layer data in this.layers.
	 *
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	private void parseLayersSection() throws IOException {
		// read layer header info
		int length = this.psdStream.readInt();
		int pos = this.psdStream.getPos();
		ArrayList<PsdLayer> tmpLayers=null;
		PsdLayer tmpBaseLayer;
		
		if (length > 0) {

			int size = this.psdStream.readInt();
			if ((size & 0x01) != 0) {
				size++;
			}
			if (size > 0) {
				int layersCount = this.psdStream.readShort();
				if (layersCount < 0) {
					layersCount = -layersCount;
				}
				
				tmpLayers=new ArrayList<PsdLayer>(layersCount);
				for (int i = 0; i < layersCount; i++) {
					PsdLayer layer = new PsdLayer(this.psdStream);
					tmpLayers.add(layer);
				}
				
				for (PsdLayer layer : tmpLayers) {
					layer.readImage(this.psdStream);
				}
				this.psdObject.setLayers(tmpLayers);
			}

			int maskSize = length - (this.psdStream.getPos() - pos);
			this.psdStream.skipBytes(maskSize);
		}
		
		tmpBaseLayer=new PsdLayer(	this.psdObject.getWidth(), this.psdObject.getHeight(), this.psdObject.getNumberOfChannels());
		
		//run-length-encoding
		boolean rle = this.psdStream.readShort() == 1;
		if (rle) {
			int nLines = tmpBaseLayer.getHeight() * tmpBaseLayer.getNumberOfChannels();
			short[] lineLengths = new short[nLines];
			
			for (int i = 0; i < nLines; i++) {
				lineLengths[i] = this.psdStream.readShort();
			}
			
			tmpBaseLayer.readImage(this.psdStream, false, lineLengths);
		} else {
			tmpBaseLayer.readImage(this.psdStream, false, null);
		}

		if (tmpLayers == null) {
			tmpLayers = new ArrayList<PsdLayer>(1);
			tmpLayers.add(tmpBaseLayer);
		}
		this.psdObject.setBaseLayer(tmpBaseLayer);
	}

	/**
	 * Build layer tree. The following layer types exist:<br>
	 * <p>normal (contained layer or not nested)<br>folder (container layer) <br>hidden layers (can be contained layer, too)</p>
	 */
	private void setupLayersGroups() {
		PsdLayer parentLayer = null;
		for (int i = this.psdObject.getLayers().size() - 1; i >= 0; i--) {
			PsdLayer layer = this.psdObject.getLayer(i);

			switch (layer.getType()) {
			case NORMAL:
				layer.setParent(parentLayer);
				break;
			case FOLDER:
				layer.setParent(parentLayer);
				parentLayer = layer;
				break;
			case HIDDEN:
				if (parentLayer != null) {
					parentLayer = parentLayer.getParent();
				}
				break;
			}
		}
	}
}
