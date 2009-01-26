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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Dmitry Belsky
 */
public class PsdFile {

	private static Logger logger = Logger.getLogger("psd.PsdFile");

	private int numberOfChannels;
	private int width;
	private int height;
	private int depth;
	private PsdColorMode colorMode;
	private ArrayList<PsdLayer> layers;
	private int[] framesDelays;
	private HashMap<Integer, Integer> framesIds;
	private PsdLayer baseLayer;

	public PsdFile(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException(
					"Param inputStream must be not null.");
		}
		logger.fine("psd-file started parsing");
		PsdInputStream stream = new PsdInputStream(inputStream);
		readHeaderSection(stream);
		readColorModeSection(stream);
		readImageResourceSection(stream);
		readLayersSection(stream);
		setupLayersGroups();
		logger.fine("psd-file success parsed");
	}

	public int getLayersCount() {
		return layers.size();
	}

	public PsdLayer getLayer(int n) {
		return layers.get(n);
	}

	public int getFramesCount() {
		return framesDelays == null ? 0 : framesDelays.length;
	}

	public int getFrameDelay(int frame) {
		return framesDelays == null ? 0 : framesDelays[frame];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public PsdColorMode getColorMode() {
		return colorMode;
	}

	public int getDepth() {
		return depth;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	int getFrameNum(int frameId) {
		return framesIds.get(frameId);
	}

	private void readHeaderSection(PsdInputStream stream) throws IOException {
		String sig = stream.readString(4);
		if (!sig.equals("8BPS")) {
			throw new IOException("file signature error");
		}

		int ver = stream.readShort();
		if (ver != 1) {
			throw new IOException("file version error ");
		}

		stream.skipBytes(6); // reserved
		numberOfChannels = stream.readShort();
		height = stream.readInt();
		width = stream.readInt();
		depth = stream.readShort();
		if (depth != 8) {
			throw new IOException(
					"unsupported color depth: color depth must be 8");
		}
		int cm = stream.readShort();
		colorMode = PsdColorMode.values()[cm];
		if (colorMode != PsdColorMode.RGB) {
			throw new IOException("unsupported color mode: must be RGB");
		}
	}

	private void readColorModeSection(PsdInputStream input) throws IOException {
		// skip
		int colorMapLength = input.readInt();
		input.skipBytes(colorMapLength);
	}

	private void readImageResourceSection(PsdInputStream stream)
			throws IOException {
		int length = stream.readInt();
		int pos = stream.getPos();
		while (length > 0) {
			String tag = stream.readString(4);
			if (!tag.equals("8BIM") && !tag.equals("MeSa")) {
				throw new IOException(
						"Format error: Invalid image resources section.: "
								+ tag);
			}
			length -= 4;
			int id = stream.readShort();
			length -= 2;
			int sizeOfName = stream.readByte() & 0xFF;
			if ((sizeOfName & 0x01) == 0)
				sizeOfName++;
			/* String name = */stream.readString(sizeOfName);
			length -= sizeOfName + 1;
			int sizeOfData = stream.readInt();
			length -= 4;
			if ((sizeOfData & 0x01) == 1)
				sizeOfData++;
			length -= sizeOfData;
			int storePos = stream.getPos();
			if (sizeOfData > 0 && tag.equals("8BIM")) {
				// System.out.println("ID: " + id + " " + sizeOfData + " " +
				// name);
				if (id >= 4000 && id < 5000) {
					readAnimationFramesInformation(stream, id, sizeOfData);
				}
			}
			stream.skipBytes(sizeOfData - (stream.getPos() - storePos));

		}
		int skipSize = length - (stream.getPos() - pos);
		stream.skipBytes(skipSize);
	}

	private void readAnimationFramesInformation(PsdInputStream stream, int id,
			int sizeOfData) throws IOException {
		byte[] data = new byte[sizeOfData];
		stream.read(data);

		PsdInputStream st = new PsdInputStream(new ByteArrayInputStream(data));
		String key = st.readString(4);
		if (key.equals("mani")) {
			st.skipBytes(12 + 12);
			PsdDescriptor desc = new PsdDescriptor(st);
			PsdList delaysList = (PsdList) desc.get("FrIn");
			HashMap<Integer, Integer> delays = new HashMap<Integer, Integer>();
			for (Object o : delaysList) {
				PsdDescriptor frDesc = (PsdDescriptor) o;
				delays.put((Integer) frDesc.get("FrID"), (Integer) frDesc
						.get("FrDl"));
			}

			PsdList framesSets = (PsdList) desc.get("FSts");
			PsdDescriptor frameSet = (PsdDescriptor) framesSets.get(0);
			// int activeFrame = (Integer) frameSet.get("AFrm");
			PsdList framesList = (PsdList) frameSet.get("FsFr");
			framesIds = new HashMap<Integer, Integer>();
			framesDelays = new int[framesList.size()];
			for (int i = 0; i < framesList.size(); i++) {
				int frameId = (Integer) framesList.get(i);
				Integer delay = delays.get(frameId);
				framesDelays[i] = (delay == null ? 10 : delay) * 10;
				framesIds.put(frameId, i);
			}
		}
	}

	private void readLayersSection(PsdInputStream input) throws IOException {
		// read layer header info
		int length = input.readInt();
		int pos = input.getPos();
		if (length > 0) {

			int size = input.readInt();
			if ((size & 0x01) != 0) {
				size++;
			}
			if (size > 0) {
				int layersCount = input.readShort();
				if (layersCount < 0) {
					layersCount = -layersCount;
				}
				layers = new ArrayList<PsdLayer>(layersCount);
				for (int i = 0; i < layersCount; i++) {
					PsdLayer layer = new PsdLayer(this, input);
					layers.add(layer);
				}
				for (PsdLayer layer : layers) {
					layer.readImage(input);
				}
			}

			int maskSize = length - (input.getPos() - pos);
			input.skipBytes(maskSize);
		}

		baseLayer = new PsdLayer(this);
		boolean rle = input.readShort() == 1;
		if (rle) {
			int nLines = baseLayer.getHeight()
					* baseLayer.getNumberOfChannels();
			short[] lineLengths = new short[nLines];
			for (int i = 0; i < nLines; i++) {
				lineLengths[i] = input.readShort();
			}
			baseLayer.readImage(input, false, lineLengths);
		} else {
			baseLayer.readImage(input, false, null);
		}

		if (layers == null) {
			layers = new ArrayList<PsdLayer>(1);
			layers.add(baseLayer);
		}
	}

	private void setupLayersGroups() {
		PsdLayer parentLayer = null;
		for (int i = layers.size() - 1; i >= 0; i--) {
			PsdLayer layer = layers.get(i);
			// System.out.println("psdLanyer: " + layer.getName());

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
