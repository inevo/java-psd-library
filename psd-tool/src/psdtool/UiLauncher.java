package psdtool;

import javax.swing.*;
import java.io.PrintStream;
import java.util.logging.*;

public class UiLauncher {

    public static void main(String[] args) {
        setupLogging();
        setupOutputStream();
        initializeSystemProperties();
        setupLookAndFeel();
        startUi();
    }

    private static void setupLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.CONFIG);
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        rootLogger.addHandler(new ConsoleHandler() {
            @Override
            public void publish(LogRecord rec) {
                System.out.println(rec.getLevel() + ": " + rec.getMessage());
            }
        });
    }

    private static void setupOutputStream() {
        try {
            System.setOut(new PrintStream(System.out, true, "utf-8"));
        } catch (java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    private static void initializeSystemProperties() {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Psd Tool");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        MainFrame frame = new MainFrame();
        frame.getFrame().setVisible(true);
    }

}
