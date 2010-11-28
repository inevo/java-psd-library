package psd.parser.object;

import java.io.IOException;

import psd.parser.PsdInputStream;

public class PsdObjectFactory {

	/**
	 * Load psd object.
	 *
	 * @param stream the stream
	 * @return the psd object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static PsdObject loadPsdObject(PsdInputStream stream)
			throws IOException {
	
		String type = stream.readString(4);
		PsdObject.logger.finest("loadPsdObject.type: " + type);
		if (type.equals("Objc")) {
			return new PsdDescriptor(stream);
		} else if (type.equals("VlLs")) {
			return new PsdList(stream);
		} else if (type.equals("doub")) {
			return new PsdDouble(stream);
		} else if (type.equals("long")) {
			return new PsdLong(stream);
		} else if (type.equals("bool")) {
			return new PsdBoolean(stream);
		} else if (type.equals("UntF")) {
			return new PsdUnitFloat(stream);
		} else if (type.equals("enum")) {
			return new PsdEnum(stream);
		} else if (type.equals("TEXT")) {
			return new PsdText(stream);
		} else if (type.equals("tdta")) {
			return new PsdTextData(stream);
		} else {
			throw new IOException("UNKNOWN TYPE <" + type + ">");
		}
	
	}
}
