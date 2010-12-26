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

package psd.parser.imageresource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import psd.parser.PsdInputStream;
import psd.parser.object.PsdDescriptor;
import psd.parser.object.PsdList;
import psd.parser.object.PsdLong;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdAnimation.
 */
public class PsdAnimation {

	/** The frames. */
	private ArrayList<PsdAnimationFrame> frames;

	/**
	 * Instantiates a new psd animation.
	 *
	 * @param st the st
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Gets the frame by id.
	 *
	 * @param id the id
	 * @return the frame by id
	 */
	public PsdAnimationFrame getFrameById(int id) {
		for (PsdAnimationFrame frame : frames) {
			if (frame.getId() == id) {
				return frame;
			}
		}
		return null;
	}

	/**
	 * Gets the frames.
	 *
	 * @return the frames
	 */
	public List<PsdAnimationFrame> getFrames() {
		return Collections.unmodifiableList(frames);
	}

}
