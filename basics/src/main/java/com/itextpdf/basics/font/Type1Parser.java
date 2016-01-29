package com.itextpdf.basics.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.util.Utilities;
import com.itextpdf.basics.source.RandomAccessFileOrArray;
import com.itextpdf.basics.source.RandomAccessSourceFactory;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class Type1Parser {

    private static final String AfmHeader = "StartFontMetrics";

    private String afmPath;
    private String pfbPath;
    private byte[] pfbData;
    private byte[] afmData;
    private boolean isBuiltInFont;

    private static FontsResourceAnchor resourceAnchor;
    private RandomAccessSourceFactory sourceFactory = new RandomAccessSourceFactory();

    /**
     * Creates a new Type1 font file.
     * @param afm the AFM file if the input is made with a <CODE>byte</CODE> array
     * @param pfb the PFB file if the input is made with a <CODE>byte</CODE> array
     * @param metricsPath the name of one of the 14 built-in fonts or the location of an AFM file. The file must end in '.afm'
     * @the AFM file is invalid
     * @throws IOException the AFM file could not be read
     */
    public Type1Parser(String metricsPath, String binaryPath, byte[] afm, byte[] pfb) throws IOException {
        this.afmData = afm;
        this.pfbData = pfb;
        this.afmPath = metricsPath;
        this.pfbPath = binaryPath;
    }

    public RandomAccessFileOrArray getMetricsFile() throws IOException {
        InputStream is = null;
        isBuiltInFont = false;

        if (FontConstants.BUILTIN_FONTS_14.contains(afmPath)) {
            isBuiltInFont = true;
            byte[] buf = new byte[1024];
            try {
                if (resourceAnchor == null) {
                    resourceAnchor = new FontsResourceAnchor();
                }
                String resourcePath = FontConstants.RESOURCE_PATH + "afm/" + afmPath + ".afm";
                is = Utilities.getResourceStream(resourcePath, resourceAnchor.getClass().getClassLoader());
                if (is == null) {
                    throw new PdfException("1.not.found.as.resource").setMessageParams(resourcePath);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int read;
                while ((read = is.read(buf)) >= 0) {
                    out.write(buf, 0, read);
                }
                buf = out.toByteArray();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception ignore) { }
                }
            }
            return new RandomAccessFileOrArray(sourceFactory.createSource(buf));
        } else if (afmPath != null) {
            if (afmPath.toLowerCase().endsWith(".afm")) {
                return new RandomAccessFileOrArray(sourceFactory.createBestSource(afmPath));
            } else if (afmPath.toLowerCase().endsWith(".pfm")) {
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                RandomAccessFileOrArray rf = new RandomAccessFileOrArray(sourceFactory.createBestSource(afmPath));
                Pfm2afm.convert(rf, ba);
                rf.close();
                return new RandomAccessFileOrArray(sourceFactory.createSource(ba.toByteArray()));
            } else {
                throw new PdfException(PdfException._1IsNotAnAFMorPfmFontFile).setMessageParams(afmPath);
            }
        } else if (afmData != null) {
            RandomAccessFileOrArray rf = new RandomAccessFileOrArray(sourceFactory.createSource(afmData));
            if (isAfmFile(rf)) {
                return rf;
            } else {
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                try {
                    Pfm2afm.convert(rf, ba);
                } catch (Exception ignored) {
                    throw new PdfException("invalid.afm.or.pfm.font.file");
                } finally {
                    rf.close();
                }
                return new RandomAccessFileOrArray(sourceFactory.createSource(ba.toByteArray()));
            }
        } else {
            throw new PdfException("invalid.afm.or.pfm.font.file");
        }
    }

    public RandomAccessFileOrArray getPostscriptBinary() throws IOException {
        if (pfbData != null) {
            return new RandomAccessFileOrArray(sourceFactory.createSource(pfbData));
        } else if (pfbPath != null && pfbPath.toLowerCase().endsWith(".pfb")) {
            return new RandomAccessFileOrArray(sourceFactory.createBestSource(pfbPath));
        } else {
            pfbPath = afmPath.substring(0, afmPath.length() - 3) + "pfb";
            return new RandomAccessFileOrArray(sourceFactory.createBestSource(pfbPath));
        }
    }

    public boolean isBuiltInFont() {
        return isBuiltInFont;
    }

    public String getAfmPath() {
        return afmPath;
    }

    private boolean isAfmFile(RandomAccessFileOrArray raf) throws IOException {
        StringBuilder builder = new StringBuilder(AfmHeader.length());
        for (int i = 0; i < AfmHeader.length(); i++) {
            try {
                builder.append((char)raf.readByte());
            } catch (EOFException e) {
                raf.seek(0);
                return false;
            }
        }
        raf.seek(0);
        return AfmHeader.equals(builder.toString());
    }
}
