package psd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import psd.objects.PsdDescriptor;
import psd.objects.PsdList;
import psd.objects.PsdLong;

public class PsdAnimation {

	private ArrayList<PsdAnimationFrame> frames;

	public PsdAnimation(PsdInputStream st) throws IOException {
		st.skipBytes(12 + 12);
		PsdDescriptor desc = new PsdDescriptor(st);
		PsdList delaysList = (PsdList) desc.get("FrIn");
		HashMap<Integer, Integer> delays = new HashMap<Integer, Integer>();
		for (Object o : delaysList) {
			PsdDescriptor frDesc = (PsdDescriptor) o;

			int id = ((PsdLong) frDesc.get("FrID")).getValue();
			int delay = 0;
			if (frDesc.containsKey("FrDl")) {
				delay = ((PsdLong) frDesc.get("FrDl")).getValue();
			}
			delays.put(id, delay);
		}

		PsdList framesSets = (PsdList) desc.get("FSts");
		PsdDescriptor frameSet = (PsdDescriptor) framesSets.get(0);
		// int activeFrame = (Integer) frameSet.get("AFrm");
		PsdList framesList = (PsdList) frameSet.get("FsFr");
		frames = new ArrayList<PsdAnimationFrame>();
		for (int i = 0; i < framesList.size(); i++) {
			int frameId = ((PsdLong) framesList.get(i)).getValue();
			Integer delay = delays.get(frameId);
			frames.add(new PsdAnimationFrame(frameId, i, delay));
		}
	}

	public PsdAnimationFrame getFrameById(int id) {
		for (PsdAnimationFrame frame : frames) {
			if (frame.getId() == id) {
				return frame;
			}
		}
		return null;
	}

	public List<PsdAnimationFrame> getFrames() {
		return Collections.unmodifiableList(frames);
	}

}
