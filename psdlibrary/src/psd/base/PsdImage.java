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

import java.io.*;
import java.util.*;

import psd.metadata.*;
import psd.parser.*;
import psd.parser.header.Header;
import psd.parser.header.HeaderSectionHandler;
import psd.parser.imageresource.ImageResourceSectionHandler;
import psd.parser.layer.LayerParser;
import psd.parser.layer.LayersSectionHandler;

public class PsdImage {
	private Header header;
	private ArrayList<Layer> layers;
	private Layer baseLayer;

	private PsdAnimation animation;

	public PsdImage(File psdFile) throws IOException {
		PsdFileParser parser = new PsdFileParser();
		parser.getHeaderSectionParser().setHandler(new HeaderSectionHandler() {
			@Override
			public void headerLoaded(Header header) {
				PsdImage.this.header = header;
			}
		});
		
		parser.getLayersSectionParser().setHandler(new LayersSectionHandler() {
			@Override
			public void createLayer(LayerParser parser) {
				Layer layer = new Layer();
				layers.add(layer);
				parser.setHandler(layer);
			}
		});

		parser.getImageResourceSectionParser().setHandler(new ImageResourceSectionHandler() {
			@Override
			public void animationLoaded(PsdAnimation animation) {
			}
		});
		
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(psdFile));
		parser.parse(stream);
		stream.close();
		
		Layer parentLayer = null;
		for (int i = getLayers().size() - 1; i >= 0; i--) {
			Layer layer = getLayer(i);

			switch (layer.getType()) {
			case NORMAL:
				layer.setParent(parentLayer);
				break;
			case FOLDER:
				layer.setParent(parentLayer);
				parentLayer = layer;
				break;
			case HIDDEN:
				if (parentLayer != null) {
					parentLayer = parentLayer.getParent();
				}
				break;
			}
		}
	}
	
	public List<Layer> getLayers() {
		if(this.layers == null){
			this.layers = new ArrayList<Layer>();
			layers.add(this.baseLayer);
		}
		return Collections.unmodifiableList(layers);
	}
	
	public Layer getLayer(int index) {
		return layers.get(index);
	}

	public PsdAnimation getAnimation() {
		return animation;
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

}
