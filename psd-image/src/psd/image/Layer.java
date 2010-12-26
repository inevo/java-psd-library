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

package psd.image;

import java.awt.image.*;
import java.util.*;

import psd.parser.layer.Channel;
import psd.parser.layer.LayerHandler;
import psd.parser.layer.LayerType;
import psd.parser.layer.additional.LayerSectionDividerHandler;
import psd.parser.layer.additional.LayerUnicodeNameHandler;
import psd.util.BufferedImageBuilder;

public class Layer implements LayerHandler, LayerUnicodeNameHandler, LayerSectionDividerHandler {
	private int top;
	private int left;
	private int bottom;
	private int right;

	private int numberOfChannels;

	private int opacity;

	private boolean clipping;

	private boolean visible;

	private String name;

	private BufferedImage image;

	private LayerType type;

	public Layer() {
		left = 0;
		top = 0;
		right = 0;
		bottom = 0;
		visible = true;
		opacity = -1;
		type = LayerType.NORMAL;

	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return right - left;
	}

	public int getHeight() {
		return bottom - top;
	}

	public LayerType getType() {
		return type;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public int getOpacity() {
		return opacity;
	}

	public boolean isClipping() {
		return clipping;
	}

	@Override
	public String toString() {
		return "Layer: name=" + name + " left=" + left + " top=" + top + " vis=" + visible;
	}

	@Override
	public void boundsLoaded(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	@Override
	public void blendModeLoaded(String blendMode) {
		System.out.println("blendMode: " + blendMode);
	}

	@Override
	public void opacityLoaded(int opacity) {
		this.opacity = opacity;
	}

	@Override
	public void clippingLoaded(boolean clipping) {
		this.clipping = clipping;
	}

	@Override
	public void visibleLoaded(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void nameLoaded(String name) {
		this.name = name;
	}

	@Override
	public void channelsLoaded(List<Channel> channels) {
		BufferedImageBuilder imageBuilder = new BufferedImageBuilder(channels, getWidth(), getHeight());
		imageBuilder.setOpacity(opacity);
		image = imageBuilder.makeImage();
	}

	@Override
	public void layerUnicodeNameParsed(String unicodeName) {
		this.name = unicodeName;
	}

	@Override
	public void sectionDividerParsed(LayerType type) {
		this.type = type;
	}

}
