package psd.parser.layer;

import java.io.IOException;
import java.util.*;

import psd.parser.PsdInputStream;
import psd.parser.layer.additional.PsdLayerMetaInfo;

public class LayerParser {

	private LayerHandler handler;
	private Map<String, LayerAdditionalInformationParser> additionalInformationParsers;
	private LayerAdditionalInformationParser defaultAdditionalInformationParser;

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

	private void parseBounds(PsdInputStream stream) throws IOException {
		int top = stream.readInt();
		int left = stream.readInt();
		int bottom = stream.readInt();
		int right = stream.readInt();
		handler.boundsLoaded(left, top, right, bottom);
	}

	private void parseChannelsInfo(PsdInputStream stream) throws IOException {
		int channelsCount = stream.readShort();
		List<ChannelInfo> channelsInfo = new ArrayList<ChannelInfo>(channelsCount);
		for (int j = 0; j < channelsCount; j++) {
			channelsInfo.add(new ChannelInfo(stream));
		}
		handler.channelsInfoLoaded(channelsInfo);
	}

	private void parseBlendMode(PsdInputStream stream) throws IOException {
		String blendMode = stream.readString(4);
		handler.blendModeLoaded(blendMode);

	}

	private void parseOpacity(PsdInputStream stream) throws IOException {
		int opacity = stream.readByte();
		handler.opacityLoaded(opacity);
	}

	private void parseClipping(PsdInputStream stream) throws IOException {
		boolean clipping = stream.readBoolean();
		handler.clippingLoaded(clipping);
	}

	private void parseVisibility(PsdInputStream stream) throws IOException {
		int flags = stream.readByte();
		boolean visible = ((flags >> 1) & 0x01) == 0;
		handler.visibleLoaded(visible);
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

			LayerAdditionalInformationParser parser = additionalInformationParsers.get(tag);
			if (parser == null) {
				parser = defaultAdditionalInformationParser;
			}
			
			if (parser != null) {
				parser.parse(stream, tag, size);
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
		handler.nameLoaded(name);
	}

	private void parseAdditionalLayerInformation(PsdInputStream stream, String tag, int size) throws IOException {
		if (tag.equals("shmd")) {
			PsdLayerMetaInfo metaInfo = new PsdLayerMetaInfo(stream);
		}
	}

	public void parseImageSection(PsdInputStream psdStream) throws IOException {
	}

}
