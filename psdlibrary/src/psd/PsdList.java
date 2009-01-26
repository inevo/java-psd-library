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
import java.util.ArrayList;

/**
 * 
 * @author Dmitry Belsky
 *
 */
class PsdList extends ArrayList<Object> {

	private static final long serialVersionUID = 1L;

	public PsdList(PsdInputStream stream) throws IOException {
		int itemsCount = stream.readInt();
		for (int i = 0; i < itemsCount; i++) {
			String type = stream.readString(4);
			if (type.equals("Objc")) {
				add(new PsdDescriptor(stream));
			} else if (type.equals("VlLs")) {
				add(new PsdList(stream));
			} else if (type.equals("doub")) {
				double val = stream.readDouble();
				add(val);
			} else if (type.equals("long")) {
				int val = stream.readInt();
				add(val);
			} else if (type.equals("bool")) {
				boolean val = stream.readBoolean();
				add(val);
			} else {
				throw new IOException("UNKNOWN TYPE");
			}
		}
	}

}
