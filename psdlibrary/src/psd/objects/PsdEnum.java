/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package psd.objects;

import java.io.IOException;

import psd.PsdInputStream;

/**
 * @author Dmitry Belsky
 * 
 */
public class PsdEnum extends PsdObject {
	private final String typeId;
	private final String value;

	public PsdEnum(PsdInputStream stream) throws IOException {
		stream.skipBytes(4);
		typeId = stream.readString(4);
		int len = stream.readInt();
		value = stream.readString(len);
		logger.finest("PsdEnum.typeId " + typeId + " PsdEnum.value: " + value);
	}

	public String getTypeId() {
		return typeId;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "enum:<" + typeId + ":" + value + ">";
	}

}
