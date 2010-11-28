package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;
import psd.parser.layer.LayerType;

public class LayerSectionDeviderParser implements LayerAdditionalInformationParser {

	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int dividerType = stream.readInt();
		LayerType type = LayerType.NORMAL;
		switch (dividerType) {
		case 1:
		case 2:
			type = LayerType.FOLDER;
			break;
		case 3:
			type = LayerType.HIDDEN;
			break;
		}
	}

}
