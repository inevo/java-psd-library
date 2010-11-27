package psd.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import psd.layer.PsdLayerMetaInfo;
import psd.layer.PsdLayerType;
import psd.layer.PsdTextLayerTypeTool;
import psd.metadata.ChannelInfo;

public class LayerParser implements Parser {

	@Override
	public void parse(PsdInputStream stream) throws IOException {
		int top = stream.readInt();
		int left = stream.readInt();
		int bottom = stream.readInt();
		int right = stream.readInt();
		int width = right - left;
		int height = bottom - top;

		int numberOfChannels = stream.readShort();
		List<ChannelInfo> channelsInfo = new ArrayList<ChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new ChannelInfo(stream));
		}

		String tag = stream.readString(4);
		if (!tag.equals("8BIM")) {
			throw new IOException("format error");
		}
		stream.skipBytes(4); // blend mode
		int opacity = stream.readByte();
		boolean clipping = stream.readBoolean();
		int flags = stream.readByte();
		boolean visible = ((flags >> 1) & 0x01) == 0;
		int filler = stream.readByte(); // filler. must be zero
		assert filler == 0;
		readExtraData(stream);
	}

	private void readExtraData(PsdInputStream stream) throws IOException, UnsupportedEncodingException {
		String tag;
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

		// Layer name: Pascal string, padded to a multiple of 4 bytes.
		size = stream.readByte() & 0xFF;
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
		int prevPos = stream.getPos();
		while (stream.getPos() - extraPos < extraSize) {
			tag = stream.readString(4);
			if (!tag.equals("8BIM")) {
				throw new IOException("layer information signature error");
			}
			tag = stream.readString(4);

			size = stream.readInt();
			size = (size + 1) & ~0x01;
			prevPos = stream.getPos();
			if (tag.equals("lyid")) {
				int layerId = stream.readInt();
			} else if (tag.equals("shmd")) {
				PsdLayerMetaInfo metaInfo = new PsdLayerMetaInfo(stream);
			} else if (tag.equals("lsct")) {
				readLayerSectionDevider(stream);
			} else if (tag.equals("TySh")) {
				PsdTextLayerTypeTool typeTool = new PsdTextLayerTypeTool(stream, size);
			} else if (tag.equals("luni")) {
				int len = stream.readInt();
				String name2 = "";
				for (int i = 0; i < len; i++) {
					name2 += (char) stream.readShort();
				}
			} else {
				stream.skipBytes(size);
			}

			stream.skipBytes(prevPos + size - stream.getPos());
		}

		stream.skipBytes(extraSize - (stream.getPos() - extraPos));

	}

	private PsdLayerType readLayerSectionDevider(PsdInputStream stream) throws IOException {
		int dividerType = stream.readInt();
		PsdLayerType type = PsdLayerType.NORMAL;
		switch (dividerType) {
		case 1:
		case 2:
			type = PsdLayerType.FOLDER;
			break;
		case 3:
			type = PsdLayerType.HIDDEN;
			break;
		}
		return type;
	}

}
