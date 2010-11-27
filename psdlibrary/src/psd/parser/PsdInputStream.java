/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package psd.parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class PsdInputStream.
 *
 * @author Dmitry Belsky
 */
public class PsdInputStream extends InputStream {

	/** The pos. */
	private int pos;
	
	/** The marked position. */
	private int markPos;
	
	/** The in. */
	private final InputStream in;

	/**
	 * Instantiates a new psd input stream.
	 *
	 * @param in the in
	 */
	public PsdInputStream(InputStream in) {
		this.in = in;
		pos = 0;
		markPos = 0;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return in.available();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
		markPos = pos;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		in.reset();
		pos = markPos;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int res = in.read(b, off, len);
		if (res != -1) {
			pos += res;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int res = in.read(b);
		if (res != -1) {
			pos += res;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int res = in.read();
		if (res != -1) {
			pos++;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		long skip = in.skip(n);
		pos += skip;
		return skip;
	}

	/**
	 * Read string.
	 *
	 * @param len the len
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readString(int len) throws IOException {
		// read string of specified length
		byte[] bytes = new byte[len];
		read(bytes);
		return new String(bytes, "ISO-8859-1");
	}

	/**
	 * Read psd string.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readPsdString() throws IOException {
		int size = readInt();
		if (size == 0) {
			size = 4;
		}
		return readString(size);
	}

	/**
	 * Read bytes.
	 *
	 * @param bytes the bytes
	 * @param n the n
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int readBytes(byte[] bytes, int n) throws IOException {
		// read multiple bytes from input
		if (bytes == null)
			return 0;
		int r = 0;
		r = read(bytes, 0, n);
		if (r < n) {
			throw new IOException("format error. readed=" + r + " needed=" + n);
		}
		return r;
	}

	/**
	 * Read byte.
	 *
	 * @return the byte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte readByte() throws IOException {
		int ch = read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (byte) (ch);
	}

	/**
	 * Read short.
	 *
	 * @return the short
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public short readShort() throws IOException {
		int ch1 = read();
		int ch2 = read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	/**
	 * Read int.
	 *
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int readInt() throws IOException {
		int ch1 = read();
		int ch2 = read();
		int ch3 = read();
		int ch4 = read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	/**
	 * Read boolean.
	 *
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean readBoolean() throws IOException {
		int ch = read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (ch != 0);
	}

	/**
	 * Read long.
	 *
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final long readLong() throws IOException {
		int c1 = read();
		int c2 = read();
		int c3 = read();
		int c4 = read();
		int c5 = read();
		int c6 = read();
		int c7 = read();
		int c8 = read();
		return (((long) c1 << 56) + ((long) (c2 & 255) << 48)
				+ ((long) (c3 & 255) << 40) + ((long) (c4 & 255) << 32)
				+ ((long) (c5 & 255) << 24) + ((c6 & 255) << 16)
				+ ((c7 & 255) << 8) + ((c8 & 255) << 0));
	}

	/**
	 * Read double.
	 *
	 * @return the double
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * Skip bytes.
	 *
	 * @param n the n
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int skipBytes(int n) throws IOException {
		int total = 0;
		int cur = 0;
		while ((total < n) && ((cur = (int) skip(n - total)) > 0)) {
			total += cur;
		}
		return total;
	}

	/**
	 * Gets the pos.
	 *
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}

}
