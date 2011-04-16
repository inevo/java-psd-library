package psdtool;

import psd.Layer;
import psd.LayersContainer;
import psd.Psd;

import javax.swing.*;
import java.awt.*;

public class PsdView extends JComponent {
	private static final long serialVersionUID = 1L;

	private Psd psd;

    public PsdView() {
        setPreferredSize(new Dimension(400, 400));
    }

    public void setPsd(Psd psd) {
        this.psd = psd;
        setPreferredSize(new Dimension(psd.getWidth(), psd.getHeight()));
        repaint();
        revalidate();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (psd != null) {
            paintLayersContainer((Graphics2D) g, psd, 1.0f);
        }
    }

    private void paintLayersContainer(Graphics2D g, LayersContainer container, float alpha) {
        for (int i = 0; i < container.getLayersCount(); i++) {
            Layer layer = container.getLayer(i);
            if (!layer.isVisible()) {
                continue;
            }

            Composite composite = g.getComposite();
            float layerAlpha = alpha * layer.getAlpha() / 255.0f;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layerAlpha));

            if (layer.getImage() != null) {
                g.drawImage(layer.getImage(), layer.getX(), layer.getY(), null);
            }
            g.setComposite(composite);

            paintLayersContainer(g, layer, layerAlpha);
        }
    }
}
