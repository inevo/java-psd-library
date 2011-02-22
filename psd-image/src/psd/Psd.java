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

import java.io.*;
import java.util.*;

import psd.parser.*;
import psd.parser.header.*;
import psd.parser.layer.*;
import psd.parser.layer.additional.*;

public class Psd implements LayersContainer {
	private Header header;
	private List<Layer> layers = new ArrayList<Layer>();
    private Layer baseLayer;

	public Psd(File psdFile) throws IOException {
		PsdFileParser parser = new PsdFileParser();
		parser.getHeaderSectionParser().setHandler(new HeaderSectionHandler() {
            @Override
            public void headerLoaded(Header header) {
                Psd.this.header = header;
            }
        });

        final List<Layer> fullLayersList = new ArrayList<Layer>();
		parser.getLayersSectionParser().setHandler(new LayersSectionHandler() {
            @Override
            public void createLayer(LayerParser parser) {
                fullLayersList.add(new Layer(parser));
            }

            @Override
            public void createBaseLayer(LayerParser parser) {
                baseLayer = new Layer(parser);
                if (fullLayersList.isEmpty()) {
                    fullLayersList.add(baseLayer);
                }
            }
        });

		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(psdFile));
		parser.parse(stream);
		stream.close();

        layers = makeLayersHierarchy(fullLayersList);
	}
	
    private List<Layer> makeLayersHierarchy(List<Layer> layers) {
        LinkedList<LinkedList<Layer>> layersStack = new LinkedList<LinkedList<Layer>>();
        ArrayList<Layer> rootLayers = new ArrayList<Layer>();
        for (Layer layer : layers) {
            switch (layer.getType()) {
            case HIDDEN: {
                layersStack.addFirst(new LinkedList<Layer>());
                break;
            }
            case FOLDER: {
                assert !layersStack.isEmpty();
                LinkedList<Layer> folderLayers = layersStack.removeFirst();
                for (Layer l : folderLayers) {
                    layer.addLayer(l);
                }
            }
                // break isn't needed
            case NORMAL: {
                if (layersStack.isEmpty()) {
                    rootLayers.add(layer);
                } else {
                    layersStack.getFirst().add(layer);
                }
                break;
            }
            default:
                assert false;
            }
        }
        return rootLayers;
    }


    @Override
    public Layer getLayer(int index) {
        return layers.get(index);
    }

    @Override
    public int indexOfLayer(Layer layer) {
        return layers.indexOf(layer);
    }

    @Override
    public int getLayersCount() {
        return layers.size();
    }
}
