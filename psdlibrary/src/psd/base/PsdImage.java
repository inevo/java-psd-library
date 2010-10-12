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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import psd.layer.PsdLayer;
import psd.metadata.PsdAnimation;
import psd.metadata.PsdColorMode;
import psd.parser.PsdParser;

/**
 * Loads a PSD file and holds all data of the loaded PSD file.
 * @author Dmitry Belsky
 */
public class PsdImage {

	
	/** The number of channels. */
	private int numberOfChannels;
	
	/** The width of the whole PSD. */
	private int width;
	
	/** The height of the whole PSD. */
	private int height;
	
	/** The used color depth. */
	private int depth;
	
	/** The used color mode. */
	private PsdColorMode colorMode;
	
	/** The layers of the PSD file. */
	private ArrayList<PsdLayer> layers;
	
	/** The base layer. */
	private PsdLayer baseLayer;

	/** The animation generated from the layer list. */
	private PsdAnimation animation;

	/**
	 * Instantiates a new PsdFile class.
	 *
	 * @param file the psd file handle
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	public PsdImage(File file) throws IOException {
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
		new PsdParser(this, stream).loadPsd();
		stream.close();
	}
	
	/**
	 * Instantiates a new psd file directly from an InputStream.
	 *
	 * @param inputStream the psd file input stream
	 * @throws IOException Signals that an I/O exception has occurred while reading the psd file.
	 */
	public PsdImage(InputStream inputStream) throws IOException {
		new PsdParser(this, inputStream).loadPsd();
	}


	/**
	 * Gets all the layers as an unmodifiable list to provide consistency.
	 *
	 * @return the layers
	 */
	public List<PsdLayer> getLayers() {
		if(this.layers == null){
			this.layers = new ArrayList<PsdLayer>();
			layers.add(this.baseLayer);
		}
		return Collections.unmodifiableList(layers);
	}
	
	/**
	 * Gets one specific layer
	 *
	 * @return the layers
	 */
	public PsdLayer getLayer(int index) {
		return layers.get(index);
	}

	/**
	 * Gets the animation.
	 *
	 * @return the animation
	 */
	public PsdAnimation getAnimation() {
		return animation;
	}

	/**
	 * Gets the width of the psd.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the psd.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the color mode of the psd.
	 *
	 * @return the color mode
	 */
	public PsdColorMode getColorMode() {
		return colorMode;
	}

	/**
	 * Gets the depth of the psd.
	 *
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Gets the number of channels in the psd.
	 *
	 * @return the number of channels
	 */
	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	/**
	 * Gets the base layer.
	 *
	 * @return the base layer
	 */
	public PsdLayer getBaseLayer() {
		return baseLayer;
	}

	/**
	 * Sets the base layer.
	 *
	 * @param baseLayer the new base layer
	 */
	public void setBaseLayer(PsdLayer baseLayer) {
		this.baseLayer = baseLayer;
	}

	/**
	 * Sets the number of channels.
	 *
	 * @param numberOfChannels the new number of channels
	 */
	public void setNumberOfChannels(int numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}

	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets the depth.
	 *
	 * @param depth the new depth
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Sets the color mode.
	 *
	 * @param colorMode the new color mode
	 */
	public void setColorMode(PsdColorMode colorMode) {
		this.colorMode = colorMode;
	}

	/**
	 * Sets the layers.
	 *
	 * @param layers the new layers
	 */
	public void setLayers(ArrayList<PsdLayer> layers) {
		this.layers = layers;
	}

	/**
	 * Sets the animation.
	 *
	 * @param animation the new animation
	 */
	public void setAnimation(PsdAnimation animation) {
		this.animation = animation;
	}

	/**
	 * adds a layer to the layer list
	 * @param layer layer to add
	 */
	public void addLayer(PsdLayer layer) {
		this.layers.add(layer);
	}
}
