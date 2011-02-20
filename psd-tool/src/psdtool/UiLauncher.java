package psdtool;

import javax.swing.*;

public class UiLauncher {
    public static void main(String[] args) {
        startUi();
    }

    private static void startUi() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showFrame();
            }
        });
    }

    private static void showFrame() {
        JFrame frame = new JFrame("Psd tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
