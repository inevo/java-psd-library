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

import psd.parser.PsdInputStream;
import psd.rawObjects.*;
import psd.util.*;
import psd.base.*;
import java.io.*;
import java.util.logging.*;
import java.util.*;

/**
 * contains meta data of a text layer like font type, font size, raw text ...) 
 */
public class PsdTextLayerTypeTool {

	/** The logger. */
	private static Logger logger = Logger.getLogger("psd.layer");

	/** The transformation matrix for the text. */
	private Matrix transform;
	
	/** The descriptor. */
	private PsdDescriptor descriptor;
	
	/**
	 * Instantiates a new type tool.
	 *
	 * @param stream the stream
	 * @param size the size
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PsdTextLayerTypeTool(PsdInputStream stream, int size) throws IOException {
		byte[] data = new byte[size];
		stream.readBytes(data, size);

		PsdInputStream dataStream = new PsdInputStream(new ByteArrayInputStream(data));
		int version = dataStream.readShort();
		assert version == 1;
		this.transform = new Matrix(dataStream);
		int descriptorVersion = dataStream.readShort();
		logger.info("descriptorVersion: " + descriptorVersion);
		if (descriptorVersion == 50) {
			int xTextDescriptorVersion = dataStream.readInt();
			logger.info("xTextDescriptorVersion: " + xTextDescriptorVersion);
			this.descriptor = new PsdDescriptor(new PsdInputStream(dataStream));
		} else {
			assert false;
		}
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the psd object
	 */
	public PsdObjectBase get(String key) {
		return this.descriptor.get(key);
	}

	/**
	 * Gets the objects.
	 *
	 * @return the objects
	 */
	public Map<String, PsdObjectBase> getObjects() {
		return this.descriptor.getObjects();
	}
}