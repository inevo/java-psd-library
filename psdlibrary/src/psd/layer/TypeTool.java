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

import psd.*;
import psd.util.*;
import psd.objects.*;
import java.io.*;
import java.util.logging.*;

public class TypeTool {

	private static Logger logger = Logger.getLogger("psd.layer");

	private Matrix transform;
	private PsdDescriptor descriptor;
	
	public TypeTool(PsdInputStream stream, int size) throws IOException {
		byte[] data = new byte[size];
		stream.readBytes(data, size);

		PsdInputStream dataStream = new PsdInputStream(new ByteArrayInputStream(data));
		int version = dataStream.readShort();
		assert version == 1;
		transform = new Matrix(dataStream);
		int descriptorVersion = dataStream.readShort();
		logger.info("descriptorVersion: " + descriptorVersion);
		if (descriptorVersion == 50) {
			int xTextDescriptorVersion = dataStream.readInt();
			logger.info("xTextDescriptorVersion: " + xTextDescriptorVersion);
			descriptor = new PsdDescriptor(new PsdInputStream(dataStream));
		} else {
			assert false;
		}
	}

	public PsdObject get(String key) {
		return descriptor.get(key);
	}
}