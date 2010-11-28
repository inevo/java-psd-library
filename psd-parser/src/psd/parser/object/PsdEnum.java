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

package psd.parser.object;

import java.io.IOException;

import psd.parser.PsdInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdEnum.
 *
 * @author Dmitry Belsky
 */
public class PsdEnum extends PsdObject {
	
	/** The type id. */
	private final String typeId;
	
	/** The value. */
	private final String value;

	/**
	 * Instantiates a new psd enum.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PsdEnum(PsdInputStream stream) throws IOException {
		
		typeId = stream.readPsdString();
		value = stream.readPsdString();
		logger.finest("PsdEnum.typeId " + typeId + " PsdEnum.value: " + value);
	}

	/**
	 * Gets the type id.
	 *
	 * @return the type id
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "enum:<" + typeId + ":" + value + ">";
	}

}
