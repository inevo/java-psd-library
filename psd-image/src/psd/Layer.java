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

import psd.parser.BlendMode;
import psd.parser.layer.*;
import psd.parser.layer.additional.*;
import psd.parser.layer.additional.effects.PSDEffect;
import psd.util.BufferedImageBuilder;

import java.awt.image.*;
import java.util.*;

public class Layer implements LayersContainer {
    private int top = 0;
    private int left = 0;
    private int bottom = 0;
    private int right = 0;

    private int alpha = 255;

    private boolean visible = true;
    private boolean clippingLoaded;

    private String name;

    private BufferedImage image;
    private LayerType type = LayerType.NORMAL;

    private BlendMode layerBlendMode;
    private BlendingRanges layerBlendingRanges;
    private Mask mask;

    private ArrayList<Layer> layers = new ArrayList<Layer>();

    private ArrayList<PSDEffect> layerEffects = new ArrayList<PSDEffect>();

    public Layer(LayerParser parser) {
        parser.setHandler(new LayerHandler() {
            @Override
            public void boundsLoaded(int left, int top, int right, int bottom) {
                Layer.this.left = left;
                Layer.this.top = top;
                Layer.this.right = right;
                Layer.this.bottom = bottom;
            }

            @Override
            public void blendModeLoaded(BlendMode blendMode) {
                Layer.this.setLayerBlendMode(blendMode);
            }

            @Override
            public void blendingRangesLoaded(BlendingRanges ranges) {
                Layer.this.setLayerBlendingRanges(ranges);
            }

            @Override
            public void opacityLoaded(int opacity) {
                Layer.this.alpha = opacity;
            }

            @Override
            public void clippingLoaded(boolean clipping) {
                Layer.this.setClippingLoaded(clipping);
            }

            @Override
            public void flagsLoaded(boolean transparencyProtected, boolean visible, boolean obsolete, boolean isPixelDataIrrelevantValueUseful, boolean pixelDataIrrelevant) {
                Layer.this.visible = visible;
            }

            @Override
            public void nameLoaded(String name) {
                Layer.this.name = name;
            }

            @Override
            public void channelsLoaded(List<Channel> channels) {
                BufferedImageBuilder imageBuilder = new BufferedImageBuilder(channels, getWidth(), getHeight());
                image = imageBuilder.makeImage();
            }

            @Override
            public void maskLoaded(Mask mask) {
                Layer.this.setMask(mask);
            }

        });

        parser.putAdditionalInformationParser(LayerSectionDividerParser.TAG, new LayerSectionDividerParser(new LayerSectionDividerHandler() {
            @Override
            public void sectionDividerParsed(LayerType type) {
                Layer.this.type = type;
            }
        }));

        parser.putAdditionalInformationParser(LayerEffectsParser.TAG, new LayerEffectsParser(new LayerEffectsHandler() {
            @Override
            public void handleEffects(List<PSDEffect> effects) {
                layerEffects.addAll(effects);
            }
        }));

        parser.putAdditionalInformationParser(LayerUnicodeNameParser.TAG, new LayerUnicodeNameParser(new LayerUnicodeNameHandler() {
            @Override
            public void layerUnicodeNameParsed(String unicodeName) {
                name = unicodeName;
            }
        }));
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
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

    public BufferedImage getImage() {
        return image;
    }

    public List<PSDEffect> getEffectsList() {
        return layerEffects;
    }

    public int getX() {
        return left;
    }

    public int getY() {
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

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getAlpha() {
        return alpha;
    }

    public boolean isClippingLoaded() {
        return clippingLoaded;
    }

    public void setClippingLoaded(boolean clippingLoaded) {
        this.clippingLoaded = clippingLoaded;
    }

    public BlendMode getLayerBlendMode() {
        return layerBlendMode;
    }

    public void setLayerBlendMode(BlendMode layerBlendMode) {
        this.layerBlendMode = layerBlendMode;
    }

    public BlendingRanges getLayerBlendingRanges() {
        return layerBlendingRanges;
    }

    public void setLayerBlendingRanges(BlendingRanges layerBlendingRanges) {
        this.layerBlendingRanges = layerBlendingRanges;
    }

    public Mask getMask() {
        return mask;
    }

    public void setMask(Mask mask) {
        this.mask = mask;
    }
}
