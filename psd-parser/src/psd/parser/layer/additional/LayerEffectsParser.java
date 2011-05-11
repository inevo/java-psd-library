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

import psd.parser.BlendMode;
import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;
import psd.parser.layer.additional.effects.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the effects applied in this layer.
 * For now it only supports some effects.
 */
public class LayerEffectsParser implements LayerAdditionalInformationParser {

    /**
     * Tag that represents the layer effects section.
     */
    public static final String TAG = "lrFX";

    /**
     * Layer effects handler (will receive the parsed effects).
     */
    private final LayerEffectsHandler handler;

    public LayerEffectsParser(LayerEffectsHandler handler) {
        this.handler = handler;
    }

    public void parse(PsdInputStream stream, String tag, int size) throws IOException {

        List<PSDEffect> effects = new ArrayList<PSDEffect>();

        int version = stream.readShort();
        int numEffects = stream.readShort();
        int remainingSize = 0;

        for (int i = 0; i < numEffects ; i++) {

            //check signature
            String sig = stream.readString(4);
            if (!sig.equals("8BIM")) {
                throw new Error("layer effect information signature error");
            }

            //check effect ID
            String effID = stream.readString(4);

            if (effID.equals("cmnS")) {
                //common state info
                //skip
                /*
                 4 Size of next three items: 7
                 4 Version: 0
                 1 Visible: always true
                 2 Unused: always 0
                 */
                stream.skipBytes(11);
            } else if (effID.equals("dsdw")) {
                //drop shadow
                remainingSize = stream.readInt();
                PSDEffect ef =  parseDropShadow(stream,false);
                effects.add(ef);
            } else if (effID.equals("isdw")) {
                //inner drop shadow
                remainingSize = stream.readInt();
                PSDEffect ef =  parseDropShadow(stream,true);
                effects.add(ef);
            } else if (effID.equals("oglw")) {
                //outer glow
                remainingSize = stream.readInt();
                PSDEffect ef =  parseGlow(stream,false);
                effects.add(ef);
            } else if (effID.equals("iglw")) {
                //inner glow
                remainingSize = stream.readInt();
                PSDEffect ef =  parseGlow(stream,true);
                effects.add(ef);
            } else if (effID.equals("bevl")) {
                //bevel
                remainingSize = stream.readInt();
                PSDEffect ef = parseBevel(stream);
                effects.add(ef);
            } else if (effID.equals("sofi")) {
                //solid fill
                remainingSize = stream.readInt();
                PSDEffect ef = parseSolidFill(stream);
                effects.add(ef);
            } else {
                remainingSize = stream.readInt();
                stream.skipBytes(remainingSize);
                if (handler != null) {
                    handler.handleEffects(effects);
                }
                return;
            }
        }

        if (handler != null) {
            handler.handleEffects(effects);
        }
    }

    /**
     * Parses the stream to create a DropShadow effect (outer and inner).
     * @param stream
     * @param inner
     * @return
     * @throws IOException
     */
    private PSDEffect parseDropShadow(PsdInputStream stream, boolean inner) throws IOException {

        int version = stream.readInt();     //0 (Photoshop 5.0) or 2 (Photoshop 5.5)
        int blur = stream.readShort();      //Blur value in pixels (8)
        int intensity = stream.readInt();   //Intensity as a percent (10?)
        int angle = stream.readInt();       //Angle in degrees		(120)
        int distance = stream.readInt();    //Distance in pixels		(25)

        //2 bytes for space
        stream.skipBytes(4);

        int colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorB = stream.readUnsignedByte();

        Color colorValue = new Color(colorR, colorG, colorB);

        stream.skipBytes(3);

        String blendSig = stream.readString(4);
        if (!blendSig.equals("8BIM")){
            throw new Error("Invalid Blend mode signature for Effect: " + blendSig );
        }

        /*
        4 bytes.
        Blend mode key.
        */
        String blendModeKey = stream.readString(4);
        boolean effectIsEnabled = stream.readBoolean(); //1 Effect enabled
        boolean useInAllEFX = stream.readBoolean();     //1 Use this angle in all of the layer effects
        float alpha = new Float(stream.readUnsignedByte()/255.0);            //1 Opacity as a percent

        //get native color
        stream.skipBytes(4);           //2 bytes for space
        colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        colorB = stream.readUnsignedByte();
        stream.skipBytes(1);

        Color nativeColor = new Color(colorR, colorG, colorB);

        /*create a dropshadow effect*/
        DropShadowEffect effect = new DropShadowEffect(inner);
        effect.setAlpha(alpha);
        effect.setAngle(180-angle);
        effect.setBlur(blur);
        effect.setColor(colorValue);
        effect.setDistance(distance);
        effect.setIntensity(intensity);
        effect.setQuality(4);
        effect.setStrength(1);
        effect.setUseInAllEFX(useInAllEFX);
        effect.setEnabled(effectIsEnabled);
        effect.setBlendMode(BlendMode.getByName(blendModeKey));

        return effect;
    }

    /**
     * Parses the stream to create a Glow effect (outer and inner).
     * @param stream
     * @param inner
     * @return
     * @throws IOException
     */
    private PSDEffect parseGlow(PsdInputStream stream, boolean inner) throws IOException {

        int version = stream.readInt();     //0 (Photoshop 5.0) or 2 (Photoshop 5.5)
        int blur = stream.readShort();      //Blur value in pixels (8)
        int intensity = stream.readInt();   //Intensity as a percent (10?) (not working)

        //2 bytes for space
        stream.skipBytes(4);
        int colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorB = stream.readUnsignedByte();

        Color colorValue = new Color(colorR, colorG, colorB);

        stream.skipBytes(3);

        String blendSig = stream.readString(4);
        if (!blendSig.equals("8BIM")){
            throw new Error("Invalid Blend mode signature for Effect: " + blendSig );
        }

        /*
        4 bytes.
        Blend mode key.
        */
        String blendModeKey = stream.readString(4);
        boolean effectIsEnabled = stream.readBoolean(); //1 Effect enabled
        float alpha = new Float(stream.readUnsignedByte()/255.0);            //1 Opacity as a percent

        if (version == 2){

            boolean invert = false;
            if (inner)  invert = stream.readBoolean();

            //get native color
            stream.skipBytes(3);
            colorR = stream.readUnsignedByte();
            stream.skipBytes(1);
            colorG = stream.readUnsignedByte();
            stream.skipBytes(1);
            colorB = stream.readUnsignedByte();
            stream.skipBytes(2);

            Color nativeColor = new Color(colorR, colorG, colorB);
        }

        /*Create the glow effect*/
        GlowEffect effect = new GlowEffect(inner);
        effect.setAlpha(alpha);
        effect.setBlendMode(BlendMode.getByName(blendModeKey));
        effect.setBlur(blur);
        effect.setColor(colorValue);
        effect.setEnabled(effectIsEnabled);
        effect.setQuality(4);
        effect.setStrength(1);
        effect.setVersion(version);
        effect.setIntensity(intensity);

        return effect;
    }

    /**
     * Parses the stream to create a Bevel effect
     * @param stream
     * @return
     * @throws IOException
     */
    private PSDEffect parseBevel(PsdInputStream stream) throws IOException {

        int version = stream.readInt();     //0 (Photoshop 5.0) or 2 (Photoshop 5.5)
        int angle = stream.readShort();       //Angle in degrees
        int strength = stream.readInt();    //Strength. Depth in pixels
        int blur = stream.readInt();        //Blur value in pixels

        stream.skipBytes(2);

        //	Highlight blend mode: 4 bytes for signature and 4 bytes for the key
        String blendSig = stream.readString(4);
        if (!blendSig.equals("8BIM")){
            throw new Error("Invalid Blend mode signature for Effect: " + blendSig );
        }

        /*
        4 bytes.
        Blend mode key.
        */
        String blendModeKey = stream.readString(4);

        //Shadow blend mode: 4 bytes for signature and 4 bytes for the key
        String blendSigShadow = stream.readString(4);
        if (!blendSigShadow.equals("8BIM")){
            throw new Error("Invalid Blend mode signature for Effect: " + blendSigShadow );
        }

        /*
        4 bytes.
        Blend mode Shadow key.
        */
        String blendModeShadowKey = stream.readString(4);

        //Highlight color: 2 bytes for space followed by 4 * 2 byte color component
        stream.skipBytes(3);

        int colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorB = stream.readUnsignedByte();

        stream.skipBytes(2);

        Color highlightColor = new Color(colorR, colorG, colorB);

        //Shadow color: 2 bytes for space followed by 4 * 2 byte color component
        stream.skipBytes(3);

        colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        colorB = stream.readUnsignedByte();

        Color shadowColor = new Color(colorR, colorG, colorB);

        stream.skipBytes(2);

        //Bevel style
        int bevelStyle = stream.readUnsignedByte();

        //Hightlight opacity as a percent
        float highlightOpacity = new Float(stream.readUnsignedByte()/255.0);

        //Shadow opacity as a percent
        float shadowOpacity = new Float(stream.readUnsignedByte()/255.0);

        //Effect enabled
        boolean isEffectEnabled = stream.readBoolean();

        //Use this angle in all of the layer effects
        boolean useInAllLayerEffects = stream.readBoolean();

        //Bevel Up or down
        int direction = stream.readUnsignedByte();

        Color realHighlightColor = null, realShadowColor = null;
        if (version == 2) {
            //Real Highlight color: 2 bytes for space followed by 4 * 2 byte color component
            stream.skipBytes(3);

            int realColorR = stream.readUnsignedByte();
            stream.skipBytes(1);
            int realColorG = stream.readUnsignedByte();
            stream.skipBytes(1);
            int realColorB = stream.readUnsignedByte();

            realHighlightColor = new Color(realColorR, realColorG, realColorB);

            stream.skipBytes(2);

            //Real Shadow color: 2 bytes for space followed by 4 * 2 byte color component
            stream.skipBytes(3);

            int realShadowColorR = stream.readUnsignedByte();
            stream.skipBytes(1);
            int realShadowColorG = stream.readUnsignedByte();
            stream.skipBytes(1);
            int realShadowColorB = stream.readUnsignedByte();

            realShadowColor = new Color(realShadowColorR, realShadowColorG, realShadowColorB);

            stream.skipBytes(2);
        }

        BevelEffect effect = new BevelEffect();
        effect.setAngle(angle);
        effect.setVersion(version);
        effect.setUseInAllLayerEffects(useInAllLayerEffects);
        effect.setStrength(strength);
        effect.setBevelStyle(bevelStyle);
        effect.setBlendMode(BlendMode.getByName(blendModeKey));
        effect.setBlendShadowMode(BlendMode.getByName(blendModeShadowKey));
        effect.setBlur(blur);
        effect.setDirection(direction);
        effect.setEnabled(isEffectEnabled);
        effect.setHighlightColor(highlightColor);
        effect.setHighlightOpacity(highlightOpacity);
        effect.setShadowColor(shadowColor);
        effect.setShadowOpacity(shadowOpacity);

        if (realHighlightColor != null) {
            effect.setRealHighlightColor(realHighlightColor);
            effect.setRealShadowColor(realShadowColor);
        }

        return effect;
    }

    /**
     * Parses the stream to create a SolidFill effect.
     * @param stream
     * @return
     * @throws IOException
     */
    private PSDEffect parseSolidFill(PsdInputStream stream) throws IOException {

        int version = stream.readInt(); //Version: 2

        String blendSig = stream.readString(4);
        if (!blendSig.equals("8BIM")){
            throw new Error("Invalid Blend mode signature for Effect: " + blendSig );
        }

        String blendModeKey = stream.readString(4);

        //Highlight color: 2 bytes for space followed by 4 * 2 byte color component
        stream.skipBytes(3);

        int colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        int colorB = stream.readUnsignedByte();

        Color highlightColor = new Color(colorR, colorG, colorB);

        stream.skipBytes(2);

        float opacity = new Float(stream.readUnsignedByte()/255.0);

        boolean effectEnabled = stream.readBoolean();

        //Highlight color: 2 bytes for space followed by 4 * 2 byte color component
        stream.skipBytes(3);

        colorR = stream.readUnsignedByte();
        stream.skipBytes(1);
        colorG = stream.readUnsignedByte();
        stream.skipBytes(1);
        colorB = stream.readUnsignedByte();

        Color nativeColor = new Color(colorR, colorG, colorB);

        stream.skipBytes(2);

        SolidFillEffect effect = new SolidFillEffect();
        effect.setVersion(version);
        effect.setBlendMode(BlendMode.getByName(blendModeKey));
        effect.setEnabled(effectEnabled);
        effect.setHighlightColor(highlightColor);
        effect.setOpacity(opacity);
        effect.setNativeColor(nativeColor);

        return effect;
    }
}
