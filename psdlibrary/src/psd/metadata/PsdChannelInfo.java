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

package psd.metadata;

import java.io.IOException;

import psd.base.PsdInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdChannelInfo.
 *
 * @author Dmitry Belsky
 */
public class PsdChannelInfo {
	
	/** The id. */
	private int id;
	
	/** The data length. */
	private int dataLength;

	/**
	 * Instantiates a new psd channel info.
	 *
	 * @param stream the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PsdChannelInfo(PsdInputStream stream) throws IOException {
		id = stream.readShort();
		dataLength = stream.readInt();
	}

	/**
	 * Instantiates a new psd channel info.
	 *
	 * @param id the id
	 */
	public PsdChannelInfo(int id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the data length.
	 *
	 * @return the data length
	 */
	public int getDataLength() {
		return dataLength;
	}

}
