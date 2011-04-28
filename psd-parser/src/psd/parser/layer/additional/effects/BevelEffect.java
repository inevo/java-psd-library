package psd.parser.layer.additional.effects;

import psd.parser.BlendMode;

import java.awt.*;

public class BevelEffect extends PSDEffect {

    private int angle;
    private int strength;
    private int blur;
    private BlendMode blendMode;
    private BlendMode blendShadowMode;
    private Color highlightColor;
    private Color shadowColor;
    private int bevelStyle;
    private float highlightOpacity;
    private float shadowOpacity;
    private boolean useInAllLayerEffects;
    private int direction;
    private Color realHighlightColor;
    private Color realShadowColor;

    public BevelEffect(){
        super("bevl");
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getBlur() {
        return blur;
    }

    public void setBlur(int blur) {
        this.blur = blur;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    public BlendMode getBlendShadowMode() {
        return blendShadowMode;
    }

    public void setBlendShadowMode(BlendMode blendShadowMode) {
        this.blendShadowMode = blendShadowMode;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getBevelStyle() {
        return bevelStyle;
    }

    public void setBevelStyle(int bevelStyle) {
        this.bevelStyle = bevelStyle;
    }

    public float getHighlightOpacity() {
        return highlightOpacity;
    }

    public void setHighlightOpacity(float highlightOpacity) {
        this.highlightOpacity = highlightOpacity;
    }

    public float getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(float shadowOpacity) {
        this.shadowOpacity = shadowOpacity;
    }

    public boolean isUseInAllLayerEffects() {
        return useInAllLayerEffects;
    }

    public void setUseInAllLayerEffects(boolean useInAllLayerEffects) {
        this.useInAllLayerEffects = useInAllLayerEffects;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Color getRealHighlightColor() {
        return realHighlightColor;
    }

    public void setRealHighlightColor(Color realHighlightColor) {
        this.realHighlightColor = realHighlightColor;
    }

    public Color getRealShadowColor() {
        return realShadowColor;
    }

    public void setRealShadowColor(Color realShadowColor) {
        this.realShadowColor = realShadowColor;
    }
}
