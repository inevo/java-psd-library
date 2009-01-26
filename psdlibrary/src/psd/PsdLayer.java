/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package psd;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 
 * @author Dmitry Belsky
 * 
 */
public class PsdLayer {
	private int top;
	private int left;
	private int bottom;
	private int right;
	private int width;
	private int height;
	private int numberOfChannels;
	private ArrayList<PsdChannelInfo> channelsInfo;
	private int opacity;

	private boolean clipping;

	private boolean visible;
	private String name;

	private BufferedImage image;
	private int layerId;
	private PsdDescriptor animDescriptor;
	private PsdLayerFrameInfo[] frames;

	private PsdLayerType type;

	private PsdLayer parent;

	public PsdLayer(PsdFile psdFile, PsdInputStream stream) throws IOException {
		parent = null;
		type = PsdLayerType.NORMAL;

		top = stream.readInt();
		left = stream.readInt();
		bottom = stream.readInt();
		right = stream.readInt();
		width = right - left;
		height = bottom - top;

		numberOfChannels = stream.readShort();

		channelsInfo = new ArrayList<PsdChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new PsdChannelInfo(stream));
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
		readExtraData(psdFile, stream);
	}

	public PsdLayer(PsdFile psd) {
		parent = null;
		type = PsdLayerType.NORMAL;

		left = 0;
		top = 0;
		width = psd.getWidth();
		height = psd.getHeight();
		right = left + width;
		bottom = top + height;
		numberOfChannels = psd.getNumberOfChannels();

		channelsInfo = new ArrayList<PsdChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new PsdChannelInfo(j == 3 ? -1 : j));
		}
		visible = true;
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getFramesCount() {
		return frames == null ? 1 : frames.length;
	}

	public boolean isVisible(int frameNum) {
		if (frames == null) {
			return isVisible();
		} else {
			return frames[frameNum].isVisible();
		}
	}

	public int getX(int frameNum) {
		if (frames == null) {
			return getLeft();
		} else {
			return frames[frameNum].getX();
		}
	}

	public int getY(int frameNum) {
		if (frames == null) {
			return getTop();
		} else {
			return frames[frameNum].getY();
		}
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

	@Override
	public String toString() {
		return "Layer: name=" + name + " left=" + left + " top=" + top
				+ " vis=" + visible + " [group=" + parent + "]";
	}

	void readImage(PsdInputStream input) throws IOException {
		readImage(input, true, null);
	}

	void readImage(PsdInputStream input, boolean needReadPlaneInfo,
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
				break;
			default:
				readPlane(input, getWidth(), getHeight(), lineLengths,
						needReadPlaneInfo, j);
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

	private void readExtraData(PsdFile psdFile, PsdInputStream stream)
			throws IOException, UnsupportedEncodingException {
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
				readMetadata(psdFile, stream);
			} else if (tag.equals("lsct")) {
				readLayerSectionDevider(stream);
			} else {
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

	private void readMetadata(PsdFile psdFile, PsdInputStream stream)
			throws IOException {
		int countOfMetaData = stream.readInt();
		for (int i = 0; i < countOfMetaData; i++) {
			String tag = stream.readString(4);
			if (!tag.equals("8BIM")) {
				throw new IOException(
						"layer information animation signature error");
			}
			String key = stream.readString(4);
			stream.readByte(); // int copyOnSheetDuplication =
			stream.skipBytes(3); // padding
			int len = stream.readInt();
			int pos = stream.getPos();
			if (key.equals("mlst")) {
				readAnimation(psdFile, stream);
			} else {
				System.out.println("UNKNOWN KEY: " + key + " size: " + len);
			}

			stream.skipBytes(len - (stream.getPos() - pos));
		}
	}

	private void readAnimation(PsdFile psdFile, PsdInputStream stream)
			throws IOException {
		stream.skipBytes(4); // ???
		animDescriptor = new PsdDescriptor(stream);
		PsdList list = (PsdList) animDescriptor.get("LaSt");
		frames = new PsdLayerFrameInfo[list.size()];
		PsdLayerFrameInfo info = null;
		for (int i = 0; i < list.size(); i++) {
			PsdDescriptor desc = (PsdDescriptor) list.get(i);
			PsdList framesList = (PsdList) desc.get("FrLs");
			for (Object v : framesList) {
				boolean visibleFrame = true;
				int x = this.left;
				int y = this.top;

				if (desc.containsKey("enab")) {
					visibleFrame = (Boolean) desc.get("enab");
				} else {
					if (info != null) {
						visibleFrame = info.isVisible();
					}
				}

				if (desc.containsKey("Ofst")) {
					PsdDescriptor ofst = (PsdDescriptor) desc.get("Ofst");
					x += (Integer) ofst.get("Hrzn");
					y += (Integer) ofst.get("Vrtc");
				} else {
					if (info != null) {
						x = info.getX();
						y = info.getY();
					}

				}
				info = new PsdLayerFrameInfo(x, y, visibleFrame);
				frames[psdFile.getFrameNum((Integer) v)] = info;
			}

		}
	}

	private byte[] readPlane(PsdInputStream input, int w, int h,
			short[] lineLengths, boolean needReadPlaneInfo, int planeNum)
			throws IOException {
		// read a single color plane
		byte[] b = null;
		int size = w * h;
		// get RLE compression info for channel

		boolean rleEncoded;

		if (needReadPlaneInfo) {
			short encoding = input.readShort();
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
			b = readPlaneCompressed(input, w, h, lineLengths, planeNum);
		} else {
			b = new byte[size];
			input.readBytes(b, size);
		}

		return b;

	}

	private byte[] readPlaneCompressed(PsdInputStream input, int w, int h,
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
			try {
				int ac = a[j] & 0xff;
				int rc = r[j] & 0xff;
				int gc = g[j] & 0xff;
				int bc = b[j] & 0xff;
				data[j] = (((((ac << 8) | rc) << 8) | gc) << 8) | bc;
			} catch (Exception e) {
			}
			j++;
		}
		return im;
	}

}