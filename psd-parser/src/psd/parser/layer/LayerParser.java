package psd.parser.layer;

import java.io.IOException;
import java.util.*;

import psd.parser.PsdInputStream;

public class LayerParser {

	private ArrayList<Channel> channels;
	private LayerHandler handler;
	private Map<String, LayerAdditionalInformationParser> additionalInformationParsers;
	private LayerAdditionalInformationParser defaultAdditionalInformationParser;
	private int width = -1;
	private int height = -1;

	public LayerParser() {
		handler = null;
		additionalInformationParsers = new HashMap<String, LayerAdditionalInformationParser>();
		defaultAdditionalInformationParser = null;
	}

	public void putAdditionalInformationParser(String tag, LayerAdditionalInformationParser parser) {
		additionalInformationParsers.put(tag, parser);
	}

	public void setDefaultAdditionalInformationParser(LayerAdditionalInformationParser parser) {
		defaultAdditionalInformationParser = parser;
	}

	public void setHandler(LayerHandler handler) {
		this.handler = handler;
	}

	public void parse(PsdInputStream stream) throws IOException {
		parseBounds(stream);
		parseChannelsInfo(stream);

		String tag = stream.readString(4);
		if (!tag.equals("8BIM")) {
			throw new IOException("format error");
		}
		parseBlendMode(stream);
		parseOpacity(stream);
		parseClipping(stream);
		parseVisibility(stream);
		int filler = stream.readByte(); // filler. must be zero
		assert filler == 0;
		parseExtraData(stream);
	}

	public void fireBoundsChanged(int left, int top, int right, int bottom) {
		if (handler != null) {
			handler.boundsLoaded(left, top, right, bottom);
		}
	}

	public void fireChannelsLoaded(List<Channel> channels) {
		if (handler != null) {
			handler.channelsLoaded(channels);
		}
	}

	private void parseBounds(PsdInputStream stream) throws IOException {
		int top = stream.readInt();
		int left = stream.readInt();
		int bottom = stream.readInt();
		int right = stream.readInt();
		width = right - left;
		height = bottom - top;
		if (handler != null) {
			handler.boundsLoaded(left, top, right, bottom);
		}
	}

	private void parseChannelsInfo(PsdInputStream stream) throws IOException {
		int channelsCount = stream.readShort();
		channels = new ArrayList<Channel>();
		for (int j = 0; j < channelsCount; j++) {
			channels.add(new Channel(stream));
		}
	}

	private void parseBlendMode(PsdInputStream stream) throws IOException {
		String blendMode = stream.readString(4);
		if (handler != null) {
			handler.blendModeLoaded(blendMode);
		}
	}

	private void parseOpacity(PsdInputStream stream) throws IOException {
		int opacity = stream.readByte();
		if (handler != null) {
			handler.opacityLoaded(opacity);
		}
	}

	private void parseClipping(PsdInputStream stream) throws IOException {
		boolean clipping = stream.readBoolean();
		if (handler != null) {
			handler.clippingLoaded(clipping);
		}
	}

	private void parseVisibility(PsdInputStream stream) throws IOException {
		int flags = stream.readByte();
		boolean visible = ((flags >> 1) & 0x01) == 0;
		if (handler != null) {
			handler.visibleLoaded(visible);
		}
	}

	private void parseExtraData(PsdInputStream stream) throws IOException {
		int extraSize = stream.readInt();
		int extraPos = stream.getPos();
		int size;

		// LAYER MASK / ADJUSTMENT LAYER DATA
		// Size of the data: 36, 20, or 0. If zero, the following fields are not
		// present
		size = stream.readInt();
		stream.skipBytes(size);

		// LAYER BLENDING RANGES DATA
		// Length of layer blending ranges data
		size = stream.readInt();
		stream.skipBytes(size);

		parseName(stream);

		int prevPos = stream.getPos();
		while (stream.getPos() - extraPos < extraSize) {
			String tag = stream.readString(4);
			if (!tag.equals("8BIM")) {
				throw new IOException("layer information signature error");
			}
			tag = stream.readString(4);

			size = stream.readInt();
			size = (size + 1) & ~0x01;
			prevPos = stream.getPos();

			LayerAdditionalInformationParser additionalParser = additionalInformationParsers.get(tag);
			if (additionalParser == null) {
				additionalParser = defaultAdditionalInformationParser;
			}

			if (additionalParser != null) {
				additionalParser.parse(stream, tag, size);
			}

			stream.skipBytes(prevPos + size - stream.getPos());
		}

		stream.skipBytes(extraSize - (stream.getPos() - extraPos));
	}

	private void parseName(PsdInputStream stream) throws IOException {
		// Layer name: Pascal string, padded to a multiple of 4 bytes.
		int size = stream.readByte() & 0xFF;
		size = ((size + 1 + 3) & ~0x03) - 1;
		byte[] str = new byte[size];
		int strSize = str.length;
		stream.read(str);
		for (int i = 0; i < str.length; i++) {
			if (str[i] == 0) {
				strSize = i;
				break;
			}
		}
		String name = new String(str, 0, strSize, "ISO-8859-1");
		if (handler != null) {
			handler.nameLoaded(name);
		}
	}

	public void parseImageSection(PsdInputStream stream) throws IOException {
		ImagePlaneParser planeParser = new ImagePlaneParser(stream);
		int planeNum = 0;
		for (Channel channel : channels) {
			switch (channel.getId()) {
			case Channel.ALPHA:
			case Channel.RED:
			case Channel.GREEN:
			case Channel.BLUE:
				channel.setData(planeParser.readPlane(width, height));
				break;
			default:
				stream.skipBytes(channel.getDataLength());
				// layer mask
			}
			planeNum++;
		}
		if (handler != null) {
			handler.channelsLoaded(channels);
		}
	}
}
