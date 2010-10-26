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

import psd.base.*;
import psd.metadata.PsdChannelInfo;


/**
 * The Class PsdLayer contains all data of a layer.
 *
 * @author Dmitry Belsky
 */
public class PsdLayer {

	/** The logger. */
	private static Logger logger = Logger.getLogger("psd.layer");
	
	/** The top border (y1). */
	private int top;
	
	/** The left border (x1). */
	private int left;
	
	/** The bottom border (y2). */
	private int bottom;
	
	/** The right border (x2). */
	private int right;
	
	/** The width of the whole layer. */
	private int width;
	
	/** The height of the whole layer. */
	private int height;
	
	/** The number of channels of the layer. */
	private int numberOfChannels;
	
	/** The channels info for all channels. */
	private ArrayList<PsdChannelInfo> channelsInfo;
	
	/** The opacity value of the layer. */
	private int opacity;

	/** The clipping of the layer. */
	private boolean clipping;

	/** The visibility of the layer. */
	private boolean visible;
	
	/** The name of the layer. */
	private String name;

	/** The image of the layer. */
	private BufferedImage image;
	
	/** The layer id. */
	private int layerId;

	/** The type of the layer. */
	private PsdLayerType type;

	/** The parent layer of the layer. */
	private PsdLayer parent;
	
	/** The meta info of the layer. */
	private PsdLayerMetaInfo metaInfo;
	
	/** The type tool. */
	private PsdTextLayerTypeTool typeTool;

	/**
	 * Instantiates a new psd layer by reading directly from file.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PsdLayer(PsdInputStream stream) throws IOException {
		logger.setLevel(Level.OFF);
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

		channelsInfo = new ArrayList<PsdChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new PsdChannelInfo(j == 3 ? -1 : j));
		}
		visible = true;
	}

	/**
	 * Gets the meta info.
	 *
	 * @return the meta info
	 */
	public PsdLayerMetaInfo getMetaInfo() {
		return metaInfo;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Gets the left.
	 *
	 * @return the left
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * Gets the top.
	 *
	 * @return the top
	 */
	public int getTop() {
		return top;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public PsdLayerType getType() {
		return type;
	}

	/**
	 * Gets the number of channels.
	 *
	 * @return the number of channels
	 */
	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	/**
	 * Gets the opacity.
	 *
	 * @return the opacity
	 */
	public int getOpacity() {
		return opacity;
	}

	/**
	 * Gets the layer id.
	 *
	 * @return the layer id
	 */
	public int getLayerId() {
		return layerId;
	}

	/**
	 * Checks if is clipping.
	 *
	 * @return true, if is clipping
	 */
	public boolean isClipping() {
		return clipping;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public PsdLayer getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(PsdLayer parent) {
		this.parent = parent;
	}

	/**
	 * Gets the type tool.
	 *
	 * @return the type tool
	 */
	public PsdTextLayerTypeTool getTypeTool() {
		return typeTool;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Layer: name=" + name + " left=" + left + " top=" + top
				+ " vis=" + visible + " [group=" + parent + "]";
	}

	/**
	 * Gets the channel info by id.
	 *
	 * @param id the id
	 * @return the channel info by id
	 */
	private PsdChannelInfo getChannelInfoById(int id) {
		for (PsdChannelInfo info : channelsInfo) {
			if (info.getId() == id) {
				return info;
			}
		}
		throw new RuntimeException("channel info for id " + id + " not found.");
	}

	/**
	 * Read image.
	 *
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void readImage(PsdInputStream input) throws IOException {
		readImage(input, true, null);
	}

	/**
	 * Read image from psd stream.
	 *
	 * @param input the input stream
	 * @param needReadPlaneInfo says if method needs to read the plane info
	 * @param lineLengths array of line lengths
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Read extra data.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
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
			} else {
				logger.warning("skipping tag:"  + tag);
				stream.skipBytes(size);
			}

			stream.skipBytes(prevPos + size - stream.getPos());
		}

		stream.skipBytes(extraSize - (stream.getPos() - extraPos));

	}

	/**
	 * Read layer section devider.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Read plane.
	 *
	 * @param input the input
	 * @param w the w
	 * @param h the h
	 * @param lineLengths the line lengths
	 * @param needReadPlaneInfo the need read plane info
	 * @param planeNum the plane num
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Read plane compressed.
	 *
	 * @param input the input
	 * @param w the w
	 * @param h the h
	 * @param lineLengths the line lengths
	 * @param planeNum the plane num
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Decode rle.
	 *
	 * @param src the src
	 * @param sindex the sindex
	 * @param slen the slen
	 * @param dst the dst
	 * @param dindex the dindex
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Fill bytes.
	 *
	 * @param size the size
	 * @param value the value
	 * @return the byte[]
	 */
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

	/**
	 * Make image.
	 *
	 * @param w the w
	 * @param h the h
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 * @return the buffered image
	 */
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