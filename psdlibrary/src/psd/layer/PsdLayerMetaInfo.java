package psd.layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import psd.PsdAnimationFrame;
import psd.PsdInputStream;
import psd.objects.PsdBoolean;
import psd.objects.PsdDescriptor;
import psd.objects.PsdList;
import psd.objects.PsdLong;
import psd.objects.PsdObject;

public class PsdLayerMetaInfo {
	
	private HashMap<Integer, PsdLayerFrameInfo> framesInfo;
	ArrayList<PsdLayerFrameInfo> framesInfoList;

	
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
				System.out.println("UNKNOWN KEY: " + key + " size: " + len);
			}

			stream.skipBytes(len - (stream.getPos() - pos));
		}
	}

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
	
	public List<PsdLayerFrameInfo> getLayerFramesInfo() {
		return Collections.unmodifiableList(framesInfoList);
	}
}
