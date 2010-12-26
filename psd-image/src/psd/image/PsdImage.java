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

import java.io.*;
import java.util.*;

import psd.parser.*;
import psd.parser.header.*;
import psd.parser.layer.*;
import psd.parser.layer.additional.*;

public class PsdImage implements HeaderSectionHandler, LayersSectionHandler {
	private Header header;
	private ArrayList<Layer> layers;
	private Layer baseLayer;

	public PsdImage(File psdFile) throws IOException {
		PsdFileParser parser = new PsdFileParser();
		parser.getHeaderSectionParser().setHandler(this);
		parser.getLayersSectionParser().setHandler(this);

		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(psdFile));
		parser.parse(stream);
		stream.close();
	}
	
	public List<Layer> getLayers() {
		if (this.layers == null) {
			this.layers = new ArrayList<Layer>();
			layers.add(this.baseLayer);
		}
		return Collections.unmodifiableList(layers);
	}

	public Layer getLayer(int index) {
		return layers.get(index);
	}

	public int getWidth() {
		return header.getWidth();
	}

	public int getHeight() {
		return header.getHeight();
	}

	public ColorMode getColorMode() {
		return header.getColorMode();
	}

	public int getDepth() {
		return header.getDepth();
	}

	public int getChannelsCount() {
		return header.getChannelsCount();
	}

	public Layer getBaseLayer() {
		return baseLayer;
	}

	@Override
	public void headerLoaded(Header header) {
		this.header = header;
	}

	@Override
	public void createLayer(LayerParser parser) {
		Layer layer = new Layer();
		parser.putAdditionalInformationParser(LayerSectionDividerParser.TAG, new LayerSectionDividerParser(layer));
		parser.putAdditionalInformationParser(LayerUnicodeNameParser.TAG, new LayerUnicodeNameParser(layer));
		
		layers.add(layer);
		parser.setHandler(layer);
	}

	@Override
	public void createBaseLayer(LayerParser parser) {
		if (layers.isEmpty()) {
			createLayer(parser);
		}
	}
}
