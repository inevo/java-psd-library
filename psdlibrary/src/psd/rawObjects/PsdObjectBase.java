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

import java.io.IOException;
import java.util.logging.Logger;

import psd.parser.PsdInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdObject.
 *
 * @author Dmitry Belsky
 */
public class PsdObjectBase {
	
	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger("psd.objects");

	/**
	 * Instantiates a new psd object.
	 */
	public PsdObjectBase() {

	}

	/**
	 * Load psd object.
	 *
	 * @param stream the stream
	 * @return the psd object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static PsdObjectBase loadPsdObject(PsdInputStream stream)
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
		} else if (type.equals("TEXT")) {
			return new PsdText(stream);
		} else if (type.equals("tdta")) {
			return new PsdTextData(stream);
		} else {
			throw new IOException("UNKNOWN TYPE <" + type + ">");
		}

	}

}
