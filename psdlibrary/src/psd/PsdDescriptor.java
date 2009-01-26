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

import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author Dmitry Belsky
 * 
 */
public class PsdDescriptor extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public PsdDescriptor(PsdInputStream stream) throws IOException {

		// Unicode string: name from classID
		int nameLen = stream.readInt() * 2;
		stream.skipBytes(nameLen); // unicode name

		// classID: 4 bytes (length), followed either by string or (if length is
		// zero) 4-
		// byte classID
		int size = stream.readInt();
		if (size == 0) {
			stream.skipBytes(4); // classId
		} else {
			stream.skipBytes(size);
		}

		// Number of items in descriptor
		int itemsCount = stream.readInt();

		for (int i = 0; i < itemsCount; i++) {
			// Key: 4 bytes ( length) followed either by string or (if length is
			// zero) 4-byte
			// key
			size = stream.readInt();
			String key = "";
			if (size == 0) {
				key = stream.readString(4);
			} else {
				key = stream.readString(size);
			}

			String type = stream.readString(4);
			if (type.equals("obj")) {
				// psd_stream_get_object_reference(context);
			} else if (type.equals("Objc")) {
				put(key, new PsdDescriptor(stream));
			} else if (type.equals("VlLs")) {
				put(key, new PsdList(stream));
			} else if (type.equals("doub")) {
				double val = stream.readDouble();
				put(key, val);
			} else if (type.equals("long")) {
				int val = stream.readInt();
				put(key, val);
			} else if (type.equals("bool")) {
				boolean val = stream.readBoolean();
				put(key, val);
			} else if (type.equals("UntF")) {
				String unit = stream.readString(4);
				double val = stream.readDouble();
				put(key + " " + unit, val);
			} else if (type.equals("enum")) {
				stream.skipBytes(4);
				/* String typeId= */stream.readString(4);
				int len = stream.readInt();
				/* String val = */stream.readString(len);
			} else {
				throw new IOException("UNKNOWN TYPE *" + key + "*" + type + "*"
						+ " ");
			}
		}
	}

}
