package psd.parser.layer.additional.effects;

import psd.parser.BlendMode;

import java.awt.*;

public class GlowEffect extends PSDEffect {

    public static String REGULAR = "oglw";
    public static String INNER = "iglw";

    private boolean inner = false;
    private int version;

    private float alpha;
    private int blur;
    private Color color;
    private int quality;
    private int strength;
    private int intensity;
    private BlendMode blendMode;

    public GlowEffect() {}

    public GlowEffect(boolean inner){
        super();
        this.setInner(inner);
        if (inner){
            setName(INNER);
        } else {
            setName(REGULAR);
        }
    }

    public boolean isInner(){
        return inner;
    }

    public void setInner(boolean inner) {
        this.inner = inner;
        if (inner){
            setName(INNER);
        } else {
            setName(REGULAR);
        }
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getBlur() {
        return blur;
    }

    public void setBlur(int blur) {
        this.blur = blur;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public BlendMode getBlendMode(){
        return this.blendMode;
    }

    public int getStrength() {
        return strength;
    }

    public int getVersion() {
        return version;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setBlendMode(BlendMode mode){
        this.blendMode = mode;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}
