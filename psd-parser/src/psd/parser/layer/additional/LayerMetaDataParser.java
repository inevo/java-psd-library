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

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;
import psd.parser.object.*;

public class LayerMetaDataParser implements LayerAdditionalInformationParser {

	public static final String TAG = "shmd";

	private HashMap<Integer, PsdLayerFrameInfo> framesInfo;
	private ArrayList<PsdLayerFrameInfo> framesInfoList;

	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int countOfMetaData = stream.readInt();
		for (int i = 0; i < countOfMetaData; i++) {
			String dataTag = stream.readString(4);
			if (!dataTag.equals("8BIM")) {
				throw new IOException("layer information animation signature error");
			}
			String key = stream.readString(4);
			int copyOnSheetDuplication = stream.readByte();
			stream.skipBytes(3); // padding
			int len = stream.readInt();
			int pos = stream.getPos();
			if (key.equals("mlst")) {
				readFramesInfo(stream);
			} else {
			}

			stream.skipBytes(len - (stream.getPos() - pos));
		}
	}
	
	private void readFramesInfo(PsdInputStream stream) throws IOException {
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

}
