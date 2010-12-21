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

package psd.parser.layer;

import java.io.IOException;

import psd.parser.PsdInputStream;

public class Channel {
	public static final int ALPHA = -1;
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;

	private int id;
	private int dataLength;
	private byte[] data;

	public Channel(PsdInputStream stream) throws IOException {
		id = stream.readShort();
		dataLength = stream.readInt();
	}

	public Channel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getDataLength() {
		return dataLength;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}

}
