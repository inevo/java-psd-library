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

package psd;

public class PsdAnimationFrame {
	private int delay;
	private int id;
	private int number;
	
	public PsdAnimationFrame(int id, int number, int delay) {
		this.id = id;
		this.number = number;
		this.delay = delay;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getId() {
		return id;
	}
	
	public int getDelay() {
		return delay;
	}
}
