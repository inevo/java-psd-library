package analizer;

import psd.*;
import psd.layer.*;
import psd.objects.*;
import java.io.*;
import javax.imageio.*;

import java.util.*;
import java.util.logging.*;

public class PsdAnalizer {
	private static Logger logger = Logger.getLogger("app");
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("java -jar psd-analizer.jar source.psd dest.dir");
			return;
		}
		configureLogs();
		
		long startTime = System.currentTimeMillis();
		logger.info("reading: " + args[0]);
			
		try {
			processPsd(new File(args[0]), new File(args[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}

		long finishTime = System.currentTimeMillis();
		long time = finishTime - startTime;
		String timeStr = "" + (time / 1000) + "." + (time % 1000);
		logger.info("Time: " + timeStr + " sec.");
	}

	private static void configureLogs() {
		Logger psdLogger = Logger.getLogger("");
		psdLogger.setLevel(Level.ALL);
		Handler[] handlers = psdLogger.getHandlers();
		for (Handler handler : handlers) {
			psdLogger.removeHandler(handler);
		}

		psdLogger.addHandler(new ConsoleHandler() {
			public void publish(LogRecord rec) {
				System.out.println(rec.getLevel() + ": " + rec.getMessage());
			}
		});
	}

	private static void processPsd(File inputFile, File outputDir) throws IOException {
		PsdFile psdFile = new PsdFile(inputFile);
		outputDir.mkdirs();

		int num = 0;
		int total = psdFile.getLayers().size();
		for (PsdLayer layer : psdFile.getLayers()) {
			logger.info("processing: " + layer.getName() + " - " + (num * 100 / total) + "%");
			writeLayer(psdFile, layer, outputDir);
			num++;
		}
	}
	
	private static void writeLayer(PsdFile psd, PsdLayer layer, File baseDir) throws IOException {
		if (layer.getType() == PsdLayerType.NORMAL) {
			String path = getPath(layer);
			File outFile = new File(baseDir, path + ".png");
			outFile.getParentFile().mkdirs();
			if (layer.getImage() != null) {
				ImageIO.write(layer.getImage(), "png", outFile);
			} else {
				logger.warning("!!!!NULL layer: " + layer.getName());
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(baseDir, path + ".txt")));
			writer.write("psd"); writer.newLine();
			writer.write("width: " + psd.getWidth()); writer.newLine();
			writer.write("height: " + psd.getHeight()); writer.newLine();
			writer.newLine();
			writer.write("layer"); writer.newLine();
			writer.write("left: " + layer.getLeft()); writer.newLine();
			writer.write("top: " + layer.getTop()); writer.newLine();
			writer.newLine();
			writer.write("right: " + (layer.getLeft() + layer.getWidth())); writer.newLine();
			writer.write("bottom: " + (layer.getTop() + layer.getHeight())); writer.newLine();
			writer.newLine();
			writer.write("width: " + layer.getWidth()); writer.newLine();
			writer.write("height: " + layer.getHeight()); writer.newLine();

			if (layer.getTypeTool() != null) {
				writeTypeTool(writer, layer.getTypeTool());
			}
			writer.close();
		}		
	}

	private static void writeTypeTool(BufferedWriter writer, TypeTool typeTool) throws IOException {
		writer.newLine();
		writer.newLine();
		writer.write("-*- text layer -*-");
		writer.newLine();

		writer.write("TEXT: " + typeTool.get("Txt "));
		writer.newLine();
		writer.write("METRICS: ");
		writer.newLine();

		PsdTextData textData = (PsdTextData) typeTool.get("EngineData");
		Map<String, Object> properties = textData.getProperties();
		writeMap(writer, properties, 0);
	}

	private static void writeMap(BufferedWriter writer, Map<String, Object> map, int level) throws IOException {
		writeTabs(writer, level); writer.write("{"); writer.newLine();
		
		for (String key : map.keySet()) {
			writeTabs(writer, level + 1); writer.write(key + ": ");
			Object value = map.get(key);
			if (value instanceof Map) {
				writer.newLine();
				writeMap(writer, (Map) value, level + 1);
			} else {
				writer.write(String.valueOf(value));
				writer.newLine();
			}
		}
		writeTabs(writer, level); writer.write("}"); writer.newLine();
	}

	private static void writeTabs(BufferedWriter writer, int tabsCount) throws IOException {
		while (tabsCount > 0) {
			writer.write("\t");
			tabsCount--;
		}
	}

	private static String getPath(PsdLayer layer) {
		String dir = "";
		if (layer.getParent() != null) {
			dir = getPath(layer.getParent()) + "/";
		}
		return dir + layer.getName();
	}
	
}
