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

package psd_demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import psd.base.PsdImage;
import psd.layer.PsdLayer;
import psd.layer.PsdLayerType;

/**
 * @author Dmitry Belsky, Boris Suska
 * 
 */
public class PsdView extends JPanel {

	private static final long serialVersionUID = 1L;

	private PsdImage psdFile;
	
	private JTree layers;
	
	private PsdRenderer renderer;
	
	private PsdRenderer origRenderer;

	public PsdView() {
		super(new BorderLayout(), true);
		this.psdFile = null;
		this.layers = new JTree(new Object[0]);
		this.layers.setRootVisible(false);
		this.setPreferredSize(new Dimension(640, 480));
		
		JTabbedPane tab = new JTabbedPane();
		
		this.origRenderer = new PsdRenderer();
		this.renderer = new PsdRenderer();
		
		JScrollPane scrollLayers = new JScrollPane(layers);
		scrollLayers.setMinimumSize(new Dimension(150, 480));
		JScrollPane scrollRenderer = new JScrollPane(renderer);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLayers, scrollRenderer);
		split.setContinuousLayout(true);
		split.setDividerLocation(0.2);
		
		tab.addTab("Original image", null, new JScrollPane(origRenderer), "Image is rendered as could be shown (without separate layers).");
		tab.addTab("Image by layers", null, split, "Image is rendered from bottom to upper layer.");
		
		this.add(tab, BorderLayout.CENTER);
	}

	public void setPsdFile(PsdImage psdFile) {
		this.psdFile = psdFile;
		this.layers.setModel(this.createTreeModel(psdFile.getLayers()));
		
		List<PsdLayer> baseLayer = new LinkedList<PsdLayer>();
		baseLayer.add(this.psdFile.getBaseLayer());
		this.origRenderer.setPsd(psdFile.getWidth(), psdFile.getHeight(), baseLayer);
		this.origRenderer.revalidate();
		
		this.renderer.setPsd(psdFile.getWidth(), psdFile.getHeight(), this.psdFile.getLayers());
		this.renderer.revalidate();
	}
	
	private TreeModel createTreeModel(List<PsdLayer> layers) {
		NamedVector<Object> currLevel = new NamedVector<Object>();
		Queue<NamedVector<Object>> levelQueue = new LinkedList<NamedVector<Object>>();
		for (PsdLayer l : layers) {
			if (l.getType() == PsdLayerType.HIDDEN) {
				levelQueue.add(currLevel);
				currLevel = new NamedVector<Object>();
			}
			else if (l.getType() == PsdLayerType.FOLDER) {
				currLevel.setName(l.getName());
				Vector<Object> prevLevel = currLevel;
				currLevel = levelQueue.remove();
				currLevel.add(0, prevLevel);
			}
			else {
				currLevel.add(0, new NamedPsdLayer(l));
			}
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DynamicUtilTreeNode.createChildren(root, currLevel);
		return new DefaultTreeModel(root);
	}

	class PsdRenderer extends JComponent {
		
		private static final long serialVersionUID = 1L;

		List<PsdLayer> layers;
		
		private boolean[] selection;
		
		private int width;
		
		private int height;
		
		public PsdRenderer() {
			this.clear();
		}
		
		public void setPsd(int witdh, int height, List<PsdLayer> layers) {
			this.layers = layers;
			this.selection = new boolean[layers.size()];
			this.width = witdh;
			this.height = height;
			setPreferredSize(new Dimension(witdh+100, height+100));
		}
		
		public void clear() {
			this.layers = null;
			this.selection = null;
			this.width = 0;
			this.height = 0;
		}
		
		public void setSelectedLayer(int layerIndex, boolean value) {
			selection[layerIndex] = value;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if (layers != null) {
				if (psdFile != null) {
					int x = (getWidth() - width) / 2;
					int y = (getHeight() - height) / 2;
					for (int i = 0; i < layers.size(); i++) {
						PsdLayer layer = layers.get(i);
						if (layer.isVisible()) {
							int drawX = layer.getLeft() + x;
							int drawY = layer.getTop() + y;
							g.drawImage(layer.getImage(), drawX, drawY, null);
						}
					}
					g.setColor(Color.RED);
					for (int i = 0; i < layers.size(); i++) {
						if (selection[i]) {
							PsdLayer layer = layers.get(i);
							int drawX = layer.getLeft() + x;
							int drawY = layer.getTop() + y;
							g.drawRect(drawX, drawY, layer.getWidth(), layer.getHeight());
						}
					}
//					g.drawRect(x-1, y-1, psdFile.getWidth()+1, psdFile.getHeight()+1);

					// TODO: paint border by 4 rectangles because clip doesn't work correctly
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(0, 0, this.getWidth(), y);
					g.fillRect(0, y, x, this.getHeight());
					g.fillRect(x, height+y, this.getWidth()-x, y);
					g.fillRect(width+x, y, x, height);

				}
			}
		}
	}
}
