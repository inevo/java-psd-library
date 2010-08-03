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

package psd.objects;

import java.io.IOException;

import psd.PsdInputStream;

/**
 * @author Dmitry Belsky
 * 
 */
public class PsdBoolean extends PsdObject {

	private final boolean value;

	public PsdBoolean(PsdInputStream stream) throws IOException {
		value = stream.readBoolean();
		logger.finest("PsdBoolean.value: " + value );
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "bool:" + value;
	}

}
