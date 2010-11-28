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

package psd.base;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import psd.layer.PsdLayerMetaInfo;
import psd.layer.PsdLayerType;
import psd.layer.PsdTextLayerTypeTool;
import psd.metadata.ChannelInfo;
import psd.parser.PsdInputStream;

public class Layer {
	private int top;
	private int left;
	private int bottom;
	private int right;
	
	private int width;
	private int height;
	
	private int numberOfChannels;
	
	private ArrayList<ChannelInfo> channelsInfo;
	
	private int opacity;
	private boolean clipping;

	private boolean visible;
	
	private String name;

	private BufferedImage image;
	
	private int layerId;

	private PsdLayerType type;

	private Layer parent;
	
	private PsdLayerMetaInfo metaInfo;
	
	private PsdTextLayerTypeTool typeTool;

	public Layer(PsdInputStream stream) throws IOException {
		parent = null;
		typeTool = null;
		metaInfo = null;
		type = PsdLayerType.NORMAL;
	}

	public Layer(int width, int height, int numberOfChannels) {
		parent = null;
		type = PsdLayerType.NORMAL;

		left = 0;
		top = 0;
		this.width = width;
		this.height = height;
		right = left + width;
		bottom = top + height;
		this.numberOfChannels = numberOfChannels;

		channelsInfo = new ArrayList<ChannelInfo>(numberOfChannels);
		for (int j = 0; j < numberOfChannels; j++) {
			channelsInfo.add(new ChannelInfo(j == 3 ? -1 : j));
		}
		visible = true;
	}

	public PsdLayerMetaInfo getMetaInfo() {
		return metaInfo;
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
		return width;
	}

	public int getHeight() {
		return height;
	}

	public PsdLayerType getType() {
		return type;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public int getOpacity() {
		return opacity;
	}

	public int getLayerId() {
		return layerId;
	}

	public boolean isClipping() {
		return clipping;
	}

	public Layer getParent() {
		return parent;
	}

	public void setParent(Layer parent) {
		this.parent = parent;
	}

	public PsdTextLayerTypeTool getTypeTool() {
		return typeTool;
	}

	@Override
	public String toString() {
		return "Layer: name=" + name + " left=" + left + " top=" + top
				+ " vis=" + visible + " [group=" + parent + "]";
	}

	public void setVisible(boolean value){
		this.visible=true;
	}
}
