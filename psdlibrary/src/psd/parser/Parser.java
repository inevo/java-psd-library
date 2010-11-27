package psd.parser;

import java.io.IOException;

public interface Parser {
	public void parse(PsdInputStream stream) throws IOException;
}
