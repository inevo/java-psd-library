/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package psd_demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import psd.PsdFile;

/**
 * 
 * @author Dmitry Belsky
 * 
 */
public class PsdDemo extends JFrame {

	private final class OpenAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(PsdDemo.this) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = fileChooser.getSelectedFile();
					FileInputStream stream = new FileInputStream(file);
					PsdFile psdFile = new PsdFile(stream);
					view.setPsdFile(psdFile);
					stream.close();
				} catch (IOException ex) {
					logger.log(Level.SEVERE, "can't load psd-file", ex);
				}
			}
		}
	}

	private final class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("psd_demo.PsdDemo");
	private final PsdView view = new PsdView();

	public PsdDemo() {
		super("java-psd-library demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setJMenuBar(createMenu());
		getContentPane().add(view);
		pack();
	}

	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("file");
		fileMenu.setMnemonic('f');
		menuBar.add(fileMenu);

		JMenuItem openItem = new JMenuItem("open", 'o');
		openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		openItem.addActionListener(new OpenAction());
		fileMenu.add(openItem);

		JMenuItem exitItem = new JMenuItem("exit", 'x');
		exitItem.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
		exitItem.addActionListener(new ExitAction());
		fileMenu.add(exitItem);

		return menuBar;
	}

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.ALL);

		Runnable startupRunnable = new Runnable() {
			public void run() {
				PsdDemo psdDemo = new PsdDemo();
				psdDemo.setVisible(true);
			}
		};
		SwingUtilities.invokeLater(startupRunnable);
	}
}
