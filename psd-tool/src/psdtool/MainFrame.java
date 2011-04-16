package psdtool;

import psd.Psd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class MainFrame {

    private JFrame frame;
    private TreeLayerModel treeLayerModel = new TreeLayerModel();
    private PsdView psdView;

    public MainFrame() {
        frame = new JFrame("Psd Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTree tree = new JTree(treeLayerModel);
        tree.setBorder(null);
        tree.setPreferredSize(new Dimension(300, 400));

        psdView = new PsdView();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(new JScrollPane(tree));
        split.setRightComponent(new JScrollPane(psdView));
        frame.getContentPane().add(split);
        frame.setJMenuBar(buildMenu());

        frame.pack();

    }

    public JFrame getFrame() {
        return frame;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new OpenFileAction()).setAccelerator(KeyStroke.getKeyStroke("meta O"));
        bar.add(fileMenu);

        return bar;
    }

    private class OpenFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public OpenFileAction() {
            super("Open file");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FileDialog fileDialog = new FileDialog(frame, "Open psd file", FileDialog.LOAD);
            fileDialog.setDirectory("~/Downloads");
            fileDialog.setFilenameFilter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".psd");
                }
            });

            fileDialog.setVisible(true);
            if (fileDialog.getFile() != null) {
                File directory = new File(fileDialog.getDirectory());
                File psdFile = new File(directory, fileDialog.getFile());
                try {
                    Psd psd = new Psd(psdFile);
                    treeLayerModel.setPsd(psd);
                    psdView.setPsd(psd);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
