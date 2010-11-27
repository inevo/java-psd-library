package psd.parser;

import java.io.IOException;


public class HeaderParser implements Parser {
	
	private static final String FILE_SIGNATURE = "8BPS";
	private static final int FILE_VERSION = 1;
	
	private PsdHandler handler;

	public HeaderParser() {
	}
	
	public void setHandler(PsdHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void parse(PsdInputStream psdStream) throws IOException {
		String fileSignature = psdStream.readString(4);
		if (!fileSignature.equals(FILE_SIGNATURE)) {
			throw new IOException("file signature error");
		}

		int ver = psdStream.readShort();
		if (ver != FILE_VERSION) {
			throw new IOException("file version error ");
		}

		psdStream.skipBytes(6); // reserved
		Header header = new Header();
		
		header.channelsCount = psdStream.readShort();
		header.height = psdStream.readInt();
		header.width = psdStream.readInt();
		header.depth = psdStream.readShort();
		int colorModeIndex = psdStream.readShort();
		header.colorMode = ColorMode.values()[colorModeIndex];
		handler.headerLoaded(header);
	}
	
	
}
