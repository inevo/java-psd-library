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

package psd.parser.layer.additional;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdLayerFrameInfo.
 */
public class PsdLayerFrameInfo {

	/** The id. */
	private int id;
	
	/** The visible. */
	private Boolean visible;
	
	/** The x offset. */
	private Integer xOffset;
	
	/** The y offset. */
	private Integer yOffset;
	
	/**
	 * Instantiates a new psd layer frame info.
	 *
	 * @param id the id
	 * @param xOffset the x offset
	 * @param yOffset the y offset
	 * @param visible the visible
	 */
	public PsdLayerFrameInfo(int id, Integer xOffset, Integer yOffset, Boolean visible) {
		this.id = id;
		this.visible = visible;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
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
	 * Checks if is visible.
	 *
	 * @return the boolean
	 */
	public Boolean isVisible() {
		return visible;
	}
	
	/**
	 * Gets the x offset.
	 *
	 * @return the x offset
	 */
	public Integer getXOffset() {
		return xOffset;
	}
	
	/**
	 * Gets the y offset.
	 *
	 * @return the y offset
	 */
	public Integer getYOffset() {
		return yOffset;
	}
}
