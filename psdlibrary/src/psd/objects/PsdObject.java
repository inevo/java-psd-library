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

package psd.objects;

import java.io.IOException;
import java.util.logging.Logger;

import psd.PsdInputStream;

/**
 * @author Dmitry Belsky
 * 
 */
public class PsdObject {
	protected static final Logger logger = Logger.getLogger("psd.objects");

	public PsdObject() {

	}

	public static PsdObject loadPsdObject(PsdInputStream stream)
			throws IOException {

		String type = stream.readString(4);
		logger.finest("loadPsdObject.type: " + type);
		if (type.equals("Objc")) {
			return new PsdDescriptor(stream);
		} else if (type.equals("VlLs")) {
			return new PsdList(stream);
		} else if (type.equals("doub")) {
			return new PsdDouble(stream);
		} else if (type.equals("long")) {
			return new PsdLong(stream);
		} else if (type.equals("bool")) {
			return new PsdBoolean(stream);
		} else if (type.equals("UntF")) {
			return new PsdUnitFloat(stream);
		} else if (type.equals("enum")) {
			return new PsdEnum(stream);
		} else {
			throw new IOException("UNKNOWN TYPE <" + type + ">");
		}

	}

}
