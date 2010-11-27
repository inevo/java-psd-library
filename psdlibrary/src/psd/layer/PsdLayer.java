/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package psd.layer;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import psd.metadata.ChannelInfo;
import psd.parser.PsdInputStream;

public class PsdLayer {
	private static Logger logger = Logger.getLogger("psd.layer");
	
	private int top;
	private int left;
	private int bottom;
	private int right;
	
	private int width;
	private int height;
	
	private int numberOfChannels;
	
	private ArrayList<ChannelInfo> channelsInfo;
	
	private int opacity;
	private boolean clipping;

	private boolean visible;
	
	private String name;

	private BufferedImage image;
	
	private int layerId;

	private PsdLayerType type;

	private PsdLayer parent;
	
	private PsdLayerMetaInfo metaInfo;
	
	private PsdTextLayerTypeTool typeTool;

	public PsdLayer(PsdInputStream stream) throws IOException {
		parent = null;
		typeTool = null;
		metaInfo = null;
		type = PsdLayerType.NORMAL;

		top = stream.readInt();
		left = stream.readInt();
		bottom = stream.readInt();
		right = stream.readInt();
		width = right - left;
		height = bottom - top;

		numberOfChannels = stream.readShort();

		channelsInfo = new ArrayList<ChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new ChannelInfo(stream));
		}
		String tag = stream.readString(4);
		if (!tag.equals("8BIM")) {
			throw new IOException("format error");
		}
		stream.skipBytes(4); // blend mode
		opacity = stream.readByte();
		clipping = stream.readBoolean();
		int flags = stream.readByte();
		visible = ((flags >> 1) & 0x01) == 0;
		stream.readByte(); // filler. must be zero
		readExtraData(stream);
	}

	/**
	 * Instantiates a new psd layer with given values.
	 *
	 * @param width the width
	 * @param height the height
	 * @param numberOfChannels the number of channels
	 */
	public PsdLayer(int width, int height, int numberOfChannels) {
		parent = null;
		type = PsdLayerType.NORMAL;

		left = 0;
		top = 0;
		this.width = width;
		this.height = height;
		right = left + width;
		bottom = top + height;
		this.numberOfChannels = numberOfChannels;

		channelsInfo = new ArrayList<ChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new ChannelInfo(j == 3 ? -1 : j));
		}
		visible = true;
	}

	public PsdLayerMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public PsdLayerType getType() {
		return type;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public int getOpacity() {
		return opacity;
	}

	public int getLayerId() {
		return layerId;
	}

	public boolean isClipping() {
		return clipping;
	}

	public PsdLayer getParent() {
		return parent;
	}

	public void setParent(PsdLayer parent) {
		this.parent = parent;
	}

	public PsdTextLayerTypeTool getTypeTool() {
		return typeTool;
	}

	@Override
	public String toString() {
		return "Layer: name=" + name + " left=" + left + " top=" + top
				+ " vis=" + visible + " [group=" + parent + "]";
	}

	private ChannelInfo getChannelInfoById(int id) {
		for (ChannelInfo info : channelsInfo) {
			if (info.getId() == id) {
				return info;
			}
		}
		throw new RuntimeException("channel info for id " + id + " not found.");
	}

	public void readImage(PsdInputStream input) throws IOException {
		readImage(input, true, null);
	}

	public void readImage(PsdInputStream input, boolean needReadPlaneInfo,
			short[] lineLengths) throws IOException {
		byte[] r = null, g = null, b = null, a = null;
		for (int j = 0; j < numberOfChannels; j++) {
			int id = channelsInfo.get(j).getId();
			switch (id) {
			case 0:
				r = readPlane(input, getWidth(), getHeight(), lineLengths,
						needReadPlaneInfo, j);
				break;
			case 1:
				g = readPlane(input, getWidth(), getHeight(), lineLengths,
						needReadPlaneInfo, j);
				break;
			case 2:
				b = readPlane(input, getWidth(), getHeight(), lineLengths,
						needReadPlaneInfo, j);
				break;
			case -1:
				a = readPlane(input, getWidth(), getHeight(), lineLengths,
						needReadPlaneInfo, j);
				if (this.opacity != -1) {
					double opacity = (this.opacity & 0xff)/256d;
					for (int i = 0; i<a.length; i++) {
						a[i] = (byte) ((a[i] & 0xff)*opacity);
					}
				}
				break;
			default:
				// layer mask
				input.skipBytes(getChannelInfoById(id).getDataLength());
			}
		}
		int n = getWidth() * getHeight();
		if (r == null)
			r = fillBytes(n, 0);
		if (g == null)
			g = fillBytes(n, 0);
		if (b == null)
			b = fillBytes(n, 0);
		if (a == null)
			a = fillBytes(n, 255);

		image = makeImage(getWidth(), getHeight(), r, g, b, a);
	}

	private void readExtraData(PsdInputStream stream) throws IOException,
			UnsupportedEncodingException {
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
		name = new String(str, 0, strSize, "ISO-8859-1");
		logger.fine("reading layer name: " + name);
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
				layerId = stream.readInt();
			} else if (tag.equals("shmd")) {
				metaInfo = new PsdLayerMetaInfo(stream);
			} else if (tag.equals("lsct")) {
				readLayerSectionDevider(stream);
			} else if (tag.equals("TySh")) {
				typeTool = new PsdTextLayerTypeTool(stream, size);
			} else if (tag.equals("luni")) {
				int len = stream.readInt();
				name = "";
				for (int i = 0; i < len; i++) {
					name += (char) stream.readShort();
				}
			} else {
				logger.warning("skipping tag:"  + tag);
				stream.skipBytes(size);
			}

			stream.skipBytes(prevPos + size - stream.getPos());
		}

		stream.skipBytes(extraSize - (stream.getPos() - extraPos));

	}

	private void readLayerSectionDevider(PsdInputStream stream)
			throws IOException {
		int dividerType = stream.readInt();
		switch (dividerType) {
		case 1:
		case 2:
			type = PsdLayerType.FOLDER;
			break;
		case 3:
			type = PsdLayerType.HIDDEN;
			break;
		}
	}

	private byte[] readPlane(PsdInputStream input, int w, int h,
			short[] lineLengths, boolean needReadPlaneInfo, int planeNum)
			throws IOException {
		// read a single color plane
		// get RLE compression info for channel

		boolean rleEncoded;

		if (needReadPlaneInfo) {
			short encoding = input.readShort();
			if (encoding != 0 && encoding != 1) {
				throw new IOException("invalid encoding: " + encoding);
			}
			rleEncoded = encoding == 1;
			if (rleEncoded) {
				if (lineLengths == null) {
					lineLengths = new short[h];
					for (int i = 0; i < h; i++) {
						lineLengths[i] = input.readShort();
					}
				}
			}
			planeNum = 0;
		} else {
			rleEncoded = lineLengths != null;
		}

		if (rleEncoded) {
			return parsePlaneCompressed(input, w, h, lineLengths, planeNum);
		} else {
			int size = w * h;
			byte[] b = new byte[size];
			input.readBytes(b, size);
			return b;
		}
	}

	private byte[] parsePlaneCompressed(PsdInputStream input, int w, int h,
									   short[] lineLengths, int planeNum) throws IOException {

		byte[] b = new byte[w * h];
		byte[] s = new byte[w * 2];
		int pos = 0;
		int lineIndex = planeNum * h;
		for (int i = 0; i < h; i++) {
			int len = lineLengths[lineIndex++];
			input.readBytes(s, len);
			decodeRLE(s, 0, len, b, pos);
			pos += w;
		}
		return b;
	}

	private void decodeRLE(byte[] src, int sindex, int slen, byte[] dst,
			int dindex) throws IOException {
		try {
			int max = sindex + slen;
			while (sindex < max) {
				byte b = src[sindex++];
				int n = (int) b;
				if (n < 0) {
					n = 1 - n;
					b = src[sindex++];
					for (int i = 0; i < n; i++) {
						dst[dindex++] = b;
					}
				} else {
					n = n + 1;
					System.arraycopy(src, sindex, dst, dindex, n);
					dindex += n;
					sindex += n;
				}
			}
		} catch (Exception e) {
			throw new IOException("format error " + e);
		}
	}

	private byte[] fillBytes(int size, int value) {
		byte[] b = new byte[size];
		if (value != 0) {
			byte v = (byte) value;
			for (int i = 0; i < size; i++) {
				b[i] = v;
			}
		}
		return b;
	}

	private BufferedImage makeImage(int w, int h, byte[] r, byte[] g, byte[] b,
			byte[] a) {
		if (w == 0 || h == 0) {
			return null;
		}
		BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] data = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
		int n = w * h;
		int j = 0;
		while (j < n) {
			int ac = a[j] & 0xff;
			int rc = r[j] & 0xff;
			int gc = g[j] & 0xff;
			int bc = b[j] & 0xff;
			data[j] = (((((ac << 8) | rc) << 8) | gc) << 8) | bc;
			j++;
		}
		return im;
	}

	public void setVisible(boolean value){
		this.visible=true;
	}
}
