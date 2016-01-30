package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.io.File;

/**
 * Use this class for working with true type collection font (*.ttc)
 */
public class TrueTypeCollection {

    protected RandomAccessFileOrArray raf;
    String encoding;
    int TTCSize = 0;
    String ttcPath;
    byte[] ttc;
    boolean cached = false;

    public TrueTypeCollection(byte[] ttc, String encoding) throws java.io.IOException {
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(ttc));
        this.ttc = ttc;
        this.encoding = encoding;
        initFontSize();
    }

    public TrueTypeCollection(String ttcPath, String encoding) throws java.io.IOException {
        checkFilePath(ttcPath);
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(ttcPath));
        this.ttcPath = ttcPath;
        this.encoding = encoding;
        initFontSize();
    }

    /**
     * method return TrueTypeFont by ttc index
     *
     * @param ttcIndex the index for the TTC font
     * @return TrueTypeFont
     */
    public FontProgram getFontByTccIndex(int ttcIndex) throws java.io.IOException {
        if (ttcIndex > TTCSize - 1) {
            throw new IOException(IOException.TTCIndexDoesNotExistInFile);
        }

        if (ttcPath != null) {
            return FontFactory.createFont(ttcPath, ttcIndex, cached);
        } else {
            return FontFactory.createFont(ttc, ttcIndex, cached);
        }
    }

    /**
     * returns the number of fonts in True Type Collection (file or bytes array)
     *
     * @return returns the number of fonts
     */
    public int getTTCSize() {
        return TTCSize;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    private void initFontSize() throws java.io.IOException {
        String mainTag = raf.readString(4, PdfEncodings.WINANSI);
        if (!mainTag.equals("ttcf")) {
            throw new IOException(IOException.InvalidTTCFile);
        }
        raf.skipBytes(4);
        TTCSize = raf.readInt();
    }

    protected  void checkFilePath(String path){
        if(path != null) {
            File f = new File(path);
            if ((!f.exists() || !f.isFile())) {
                throw new IOException(IOException.FontFile1NotFound).setMessageParams(path);
            }
        }
    }
}
