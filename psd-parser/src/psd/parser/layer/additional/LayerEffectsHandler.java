package psd.parser.layer.additional;

import psd.parser.layer.additional.effects.PSDEffect;

import java.util.List;

public interface LayerEffectsHandler {

    public void handleEffects(List<PSDEffect> effects);
}
