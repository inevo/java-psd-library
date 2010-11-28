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

import psd.layer.PsdLayer;
import psd.metadata.*;
import psd.parser.*;
import psd.parser.header.Header;
import psd.parser.header.HeaderSectionHandler;
import psd.parser.layer.LayerHandler;
import psd.parser.layer.LayerParser;
import psd.parser.layer.LayerSectionHandler;

public class PsdImage {
	private Header header;
	private ArrayList<PsdLayer> layers;
	private PsdLayer baseLayer;

	private PsdAnimation animation;

	public PsdImage(File psdFile) throws IOException {
		PsdFileParser parser = new PsdFileParser();
		parser.getHeaderSectionParser().setHandler(new HeaderSectionHandler() {
			@Override
			public void headerLoaded(Header header) {
				PsdImage.this.header = header;
			}
		});
		
		parser.getLayersSectionParser().setHandler(new LayerSectionHandler() {
			@Override
			public void createLayer(LayerParser parser) {
				parser.setHandler(new LayerHandler() {

					@Override
					public void boundsLoaded(int left, int top, int right, int bottom) {
					}
				});
			}
		});

		parser.setPsdHandler(new PsdHandler() {
			
			@Override
			public void setAnimation(PsdAnimation animation) {
				PsdImage.this.animation = animation;
			}
		});
		
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(psdFile));
		parser.parse(stream);
		stream.close();
		
		PsdLayer parentLayer = null;
		for (int i = getLayers().size() - 1; i >= 0; i--) {
			PsdLayer layer = getLayer(i);

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
	
	public List<PsdLayer> getLayers() {
		if(this.layers == null){
			this.layers = new ArrayList<PsdLayer>();
			layers.add(this.baseLayer);
		}
		return Collections.unmodifiableList(layers);
	}
	
	public PsdLayer getLayer(int index) {
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

	public PsdLayer getBaseLayer() {
		return baseLayer;
	}

}
