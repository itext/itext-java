package com.itextpdf.io.font.cmap;

import com.itextpdf.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author psoares
 */
public class CMapByteCid extends AbstractCMap {

    protected static class Cursor {

        public int offset;
        public int length;

        public Cursor(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }

    private List<char[]> planes = new ArrayList<>();

    public CMapByteCid() {
        planes.add(new char[256]);
    }

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            encodeSequence(decodeStringToByte(mark), (char)code.getValue());
        }
    }

    /**
     *
     * @param cidBytes
     * @param offset
     * @param length
     * @return
     */
    public String decodeSequence(byte[] cidBytes, final int offset, final int length) {
        StringBuilder sb = new StringBuilder();
        Cursor cursor = new Cursor(offset, length);
        int cid;
        while ((cid = decodeSingle(cidBytes, cursor)) >= 0) {
            sb.append((char)cid);
        }
        return sb.toString();
    }

    protected int decodeSingle(byte[] cidBytes, Cursor cursor) {
        int end = cursor.offset + cursor.length;
        int currentPlane = 0;
        while (cursor.offset < end) {
            int one = cidBytes[cursor.offset++] & 0xff;
            cursor.length--;
            char[] plane = planes.get(currentPlane);
            int cid = plane[one];
            if ((cid & 0x8000) == 0) {
                return cid;
            } else {
                currentPlane = cid & 0x7fff;
            }
        }
        return -1;
    }

    private void encodeSequence(byte seq[], char cid) {
        int size = seq.length - 1;
        int nextPlane = 0;
        for (int idx = 0; idx < size; ++idx) {
            char plane[] = planes.get(nextPlane);
            int one = seq[idx] & 0xff;
            char c = plane[one];
            if (c != 0 && (c & 0x8000) == 0)
                throw new IOException("inconsistent.mapping");
            if (c == 0) {
                planes.add(new char[256]);
                c = (char)(planes.size() - 1 | 0x8000);
                plane[one] = c;
            }
            nextPlane = c & 0x7fff;
        }
        char plane[] = planes.get(nextPlane);
        int one = seq[size] & 0xff;
        char c = plane[one];
        if ((c & 0x8000) != 0)
            throw new IOException("inconsistent.mapping");
        plane[one] = cid;
    }
}
