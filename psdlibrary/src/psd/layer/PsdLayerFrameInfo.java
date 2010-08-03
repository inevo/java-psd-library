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

package psd.layer;

public class PsdLayerFrameInfo {

	private int id;
	private Boolean visible;
	private Integer xOffset;
	private Integer yOffset;
	
	public PsdLayerFrameInfo(int id, Integer xOffset, Integer yOffset, Boolean visible) {
		this.id = id;
		this.visible = visible;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public int getId() {
		return id;
	}
	
	public Boolean isVisible() {
		return visible;
	}
	
	public Integer getXOffset() {
		return xOffset;
	}
	
	public Integer getYOffset() {
		return yOffset;
	}
}
