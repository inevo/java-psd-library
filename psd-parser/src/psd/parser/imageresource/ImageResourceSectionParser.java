package psd.parser.imageresource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import psd.parser.PsdInputStream;

public class ImageResourceSectionParser {
	private static final String PSD_TAG = "8BIM";
	private ImageResourceSectionHandler handler;
	
	public void setHandler(ImageResourceSectionHandler handler) {
		this.handler = handler;
	}

	public void parse(PsdInputStream stream) throws IOException {
		int length = stream.readInt();
		int pos = stream.getPos();
		while (length > 0) {
			String tag = stream.readString(4);
			if (!tag.equals(PSD_TAG) && !tag.equals("MeSa")) {
				throw new IOException("Format error: Invalid image resources section.: " + tag);
			}
			length -= 4;
			int id = stream.readShort();
			length -= 2;
			int sizeOfName = stream.readByte() & 0xFF;
			if ((sizeOfName & 0x01) == 0)
				sizeOfName++;
			/* String name = */stream.readString(sizeOfName);
			length -= sizeOfName + 1;
			int sizeOfData = stream.readInt();
			length -= 4;
			if ((sizeOfData & 0x01) == 1)
				sizeOfData++;
			length -= sizeOfData;
			int storePos = stream.getPos();

			// TODO FIXME Is id correct?
			if (sizeOfData > 0 && tag.equals(PSD_TAG) && id >= 4000 && id < 5000) {
				byte[] data = new byte[sizeOfData];
				stream.read(data);

				PsdInputStream st = new PsdInputStream(new ByteArrayInputStream(data));
				String key = st.readString(4);
				if (key.equals("mani")) {
					handler.animationLoaded(new PsdAnimation(st));
				}
			}
			stream.skipBytes(sizeOfData - (stream.getPos() - storePos));

		}
		int skipSize = length - (stream.getPos() - pos);
		stream.skipBytes(skipSize);
	}
}
