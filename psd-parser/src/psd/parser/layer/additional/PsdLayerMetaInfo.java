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

package psd.parser.layer.additional;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;

import psd.parser.PsdInputStream;
import psd.parser.object.*;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdLayerMetaInfo.
 */
public class PsdLayerMetaInfo {

	/** The logger. */
	private Logger logger = Logger.getLogger("psd.layer");
	
	/** The frames info. */
	private HashMap<Integer, PsdLayerFrameInfo> framesInfo;
	
	/** The frames info list. */
	ArrayList<PsdLayerFrameInfo> framesInfoList;

	
	/**
	 * Instantiates a new psd layer meta info.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PsdLayerMetaInfo(PsdInputStream stream) throws IOException {
		
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
				readFramesInfo(stream);
			} else {
				logger.warning("PsdLayerMetaInfo.UnknownKey: " + key + " size: " + len);
			}

			stream.skipBytes(len - (stream.getPos() - pos));
		}
	}

	/**
	 * Read frames info.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void readFramesInfo(PsdInputStream stream)
			throws IOException {
		stream.skipBytes(4); // ???
		PsdDescriptor animDescriptor = new PsdDescriptor(stream);
		PsdList list = (PsdList) animDescriptor.get("LaSt");
		framesInfo = new HashMap<Integer, PsdLayerFrameInfo>();
		framesInfoList = new ArrayList<PsdLayerFrameInfo>();
		for (int i = 0; i < list.size(); i++) {
			PsdDescriptor desc = (PsdDescriptor) list.get(i);
			PsdList framesList = (PsdList) desc.get("FrLs");
			for (PsdObject v : framesList) {
				int id = ((PsdLong) v).getValue();
				Boolean visible = null;
				Integer xOffset = null;
				Integer yOffset = null;
				if (desc.containsKey("enab")) {
					visible = ((PsdBoolean) desc.get("enab")).getValue();
				}
				if (desc.containsKey("Ofst")) {
					PsdDescriptor ofst = (PsdDescriptor) desc.get("Ofst");
					xOffset = ((PsdLong) ofst.get("Hrzn")).getValue();
					yOffset = ((PsdLong) ofst.get("Vrtc")).getValue();
				}
				
				PsdLayerFrameInfo info = new PsdLayerFrameInfo(id, xOffset, yOffset, visible);
				framesInfo.put(id, info);
				framesInfoList.add(info);
			}
		}
		
	}
	
	/**
	 * Gets the layer frames info.
	 *
	 * @return the layer frames info
	 */
	public List<PsdLayerFrameInfo> getLayerFramesInfo() {
		return Collections.unmodifiableList(framesInfoList);
	}
}
