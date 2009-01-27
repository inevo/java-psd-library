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
import java.util.ArrayList;
import java.util.Iterator;

import psd.PsdInputStream;

/**
 * 
 * @author Dmitry Belsky
 * 
 */
public class PsdList extends PsdObject implements Iterable<PsdObject> {

	private ArrayList<PsdObject> objects = new ArrayList<PsdObject>();

	public PsdList(PsdInputStream stream) throws IOException {
		int itemsCount = stream.readInt();
		logger.finest("PsdList.itemsCount: " + itemsCount);
		for (int i = 0; i < itemsCount; i++) {
			objects.add(PsdObject.loadPsdObject(stream));
		}
	}

	@Override
	public Iterator<PsdObject> iterator() {
		return objects.iterator();
	}

	public int size() {
		return objects.size();
	}

	public PsdObject get(int i) {
		return objects.get(i);
	}

	@Override
	public String toString() {
		return "VlLs:" + objects.toString();
	}

}
