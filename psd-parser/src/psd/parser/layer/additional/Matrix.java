package psd.parser.layer.additional;

import java.io.*;

public class Matrix {

	private double m11, m12, m13;
	private double m21, m22, m23;

	public Matrix() {
		m11 = 1;
		m12 = 0;
		m13 = 0;
		m21 = 0;
		m22 = 1;
		m23 = 0;
	}

	public Matrix(InputStream stream) throws IOException {
		DataInputStream dataStream;
		if (stream instanceof DataInputStream) {
			dataStream = (DataInputStream) stream;
		} else {
			dataStream = new DataInputStream(stream);
		}
		m11 = dataStream.readDouble();
		m12 = dataStream.readDouble();
		m13 = dataStream.readDouble();
		m21 = dataStream.readDouble();
		m22 = dataStream.readDouble();
		m23 = dataStream.readDouble();
	}

	public double m11() {
		return m11;
	}

	public double m12() {
		return m12;
	}

	public double m13() {
		return m13;
	}

	public double m21() {
		return m21;
	}

	public double m22() {
		return m22;
	}

	public double m23() {
		return m23;
	}
}
