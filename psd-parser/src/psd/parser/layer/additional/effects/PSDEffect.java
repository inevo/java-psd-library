package psd.parser.layer.additional.effects;

/**
 * Generic Effect class.
 * Holds the common properties of all the PSD effects.
 */
public abstract class PSDEffect {

    protected String name;
    protected boolean isEnabled;
    protected int version;

    public PSDEffect(String name){
        this.name = name;
    }

    public PSDEffect() {}

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnabled(boolean enabled){
        this.isEnabled = enabled;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
