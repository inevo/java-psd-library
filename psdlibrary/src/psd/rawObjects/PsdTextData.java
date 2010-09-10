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

package psd.rawObjects;

import java.io.*;
import java.util.*;

import psd.base.PsdInputStream;
import psd.base.PsdObjectBase;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdTextData.
 */
public class PsdTextData extends PsdObjectBase {

	/** The properties. */
	private Map<String, Object> properties;
	
	/**
	 * Instantiates a new psd text data.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PsdTextData(PsdInputStream stream) throws IOException {
		int size = stream.readInt();
		int startPos = stream.getPos();

		for (int i = 0; i < 2; i++) {
			byte ch = stream.readByte();
			assert ch == 10;
		}
		properties = readMap(stream, 0);
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Read map.
	 *
	 * @param stream the stream
	 * @param level the level
	 * @return the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Map<String, Object> readMap(PsdInputStream stream, int level) throws IOException {
		skipTabs(stream, level);
		char c = (char) stream.readByte();
		if (c == ']') {
			return null;
		} else if (c == '<') {
			skipString(stream, "<");
		}
		skipEndLine(stream);
		HashMap<String, Object> map = new HashMap<String, Object>();
		while (true) {
			skipTabs(stream, level);
			c = (char) stream.readByte();
			if (c == '>') {
				skipString(stream, ">");
				return map;
			} else {
				assert c == 9;
				c = (char) stream.readByte();
				assert c == '/' : "unknown char: " + c + " on level: " + level;
				String name = "";
				while (true) {
					c = (char) stream.readByte();
					if (c == ' ' || c == 10) {
						break;
					}
					name += c;
				}
				if (c == 10) {
					map.put(name, readMap(stream, level + 1));
					skipEndLine(stream);
				} else if (c == ' ') {
					map.put(name, readValue(stream, level + 1));
				} else {
					assert false;
				}
			}
		}
	}

	/**
	 * Read value.
	 *
	 * @param stream the stream
	 * @param level the level
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Object readValue(PsdInputStream stream, int level) throws IOException {
		char c = (char) stream.readByte();
		if (c == ']') {
			return null;
		} else if (c == '(') {
			// unicode string
			String string = "";
			int stringSignature = stream.readShort() & 0xFFFF;
			assert stringSignature == 0xFEFF;
			while (true) {
				byte b1 = stream.readByte();
				if (b1 == ')') {
					skipEndLine(stream);
					return string;
				}
				byte b2 = stream.readByte();
				if (b2 == '\\') {
					b2 = stream.readByte();
				}
				if (b2 == 13) {
					string += '\n';
				} else {
					string += (char) ((b1 << 8) | b2);
				}
			}
		} else if (c == '[') {
			ArrayList<Object> list = new ArrayList<Object>();
			// array
			c = (char) stream.readByte();
			while (true) {
				if (c == ' ') {
					Object val = readValue(stream, level);
					if (val == null) {
						skipEndLine(stream);
						return list;
					} else {
						list.add(val);
					}
				} else if (c == 10) {
					Object val = readMap(stream, level);
					skipEndLine(stream);
					if (val == null) {
						return list;
					} else {
						list.add(val);
					}
				} else {
					assert false;
				}
			}
		} else {
			String val = "";
			do {
				val += c;
				c = (char) stream.readByte();
			} while (c != 10 && c != ' ');
			if (val.equals("true") || val.equals("false")) {
				return Boolean.valueOf(val);
			} else {
				return Double.valueOf(val);
			}
		}
	}

	/**
	 * Skip tabs.
	 *
	 * @param stream the stream
	 * @param count the count
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void skipTabs(PsdInputStream stream, int count) throws IOException {
		for (int i = 0; i < count; i++) {
			byte tabCh = stream.readByte();
			assert tabCh == 9 : "must be tab: " + tabCh;
		}
	}

	/**
	 * Skip end line.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void skipEndLine(PsdInputStream stream) throws IOException {
		byte newLineCh = stream.readByte();
		assert newLineCh == 10;
	}

	/**
	 * Skip string.
	 *
	 * @param stream the stream
	 * @param string the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void skipString(PsdInputStream stream, String string) throws IOException {
		for (int i = 0; i < string.length(); i++) {
			char streamCh = (char) stream.readByte();
			assert streamCh == string.charAt(i) : "char " + streamCh + " mustBe " + string.charAt(i);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return properties.toString();
	}
}