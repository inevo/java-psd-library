package psd.parser.layer.additional.effects;

import psd.parser.BlendMode;

import java.awt.*;

public class DropShadowEffect extends PSDEffect {

    public static String REGULAR = "dsdw";
    public static String INNER = "isdw";

    private boolean inner = false;

    private float alpha;
    private int angle;
    private int blur;
    private Color color;
    private int quality;
    private int distance;
    private int strength;
    private int intensity;
    private boolean useInAllEFX;
    private BlendMode blendMode;

    public DropShadowEffect() {}

    public DropShadowEffect(boolean inner){
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

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
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

    public int getDistance() {
        return distance;
    }

    public BlendMode getBlendMode(){
        return this.blendMode;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public boolean isUseInAllEFX() {
        return useInAllEFX;
    }

    public void setUseInAllEFX(boolean useInAllEFX) {
        this.useInAllEFX = useInAllEFX;
    }

    public void setBlendMode(BlendMode mode){
        this.blendMode = mode;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
