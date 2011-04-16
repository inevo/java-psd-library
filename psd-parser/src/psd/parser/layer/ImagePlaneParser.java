package psd.parser.layer;

import java.io.IOException;

import psd.parser.PsdInputStream;

public class ImagePlaneParser {

    private final PsdInputStream stream;

    public ImagePlaneParser(PsdInputStream stream) {
        this.stream = stream;
    }

    public byte[] readPlane(int w, int h) throws IOException {
        int compression = stream.readShort();
        switch (compression) {
            case 0:
                return readPlaneUncompressed(w, h);
            case 1:
                return parsePlaneRleCompressed(w, h, readLineLengths(h), 0);
            default:
                throw new IOException("invalid compression: " + compression);
        }
    }

    private short[] readLineLengths(int h) throws IOException {
        short[] lineLengths = new short[h];
        for (int i = 0; i < h; i++) {
            lineLengths[i] = stream.readShort();
        }
        return lineLengths;
    }

    public byte[] readPlane(int w, int h, short[] lineLengths, int planeNum) throws IOException {
        boolean rleEncoded = lineLengths != null;

        if (rleEncoded) {
            return parsePlaneRleCompressed(w, h, lineLengths, planeNum);
        } else {
            return readPlaneUncompressed(w, h);
        }
    }

    private byte[] parsePlaneRleCompressed(int w, int h, short[] lineLengths, int planeNum) throws IOException {

        byte[] b = new byte[w * h];
        byte[] s = new byte[w * 2];
        int pos = 0;
        int lineIndex = planeNum * h;
        for (int i = 0; i < h; i++) {
            int len = lineLengths[lineIndex++];
            stream.readBytes(s, len);
            decodeRLE(s, 0, len, b, pos);
            pos += w;
        }
        return b;
    }

    private void decodeRLE(byte[] src, int srcIndex, int slen, byte[] dst, int dstIndex) throws IOException {
        int sIndex = srcIndex;
        int dIndex = dstIndex;
        try {
            int max = sIndex + slen;
            while (sIndex < max) {
                byte b = src[sIndex++];
                int n = (int) b;
                if (n < 0) {
                    n = 1 - n;
                    b = src[sIndex++];
                    for (int i = 0; i < n; i++) {
                        dst[dIndex++] = b;
                    }
                } else {
                    n = n + 1;
                    System.arraycopy(src, sIndex, dst, dIndex, n);
                    dIndex += n;
                    sIndex += n;
                }
            }
        } catch (Exception e) {
            throw new IOException("format error " + e);
        }
    }

    private byte[] readPlaneUncompressed(int w, int h) throws IOException {
        int size = w * h;
        byte[] b = new byte[size];
        stream.readBytes(b, size);
        return b;
    }


}
