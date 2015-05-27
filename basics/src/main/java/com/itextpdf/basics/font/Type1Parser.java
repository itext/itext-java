package com.itextpdf.basics.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class Type1Parser {

    public static final HashSet<String> BuiltinFonts14 = new HashSet<String>();

    static {
        BuiltinFonts14.add(FontConstants.COURIER);
        BuiltinFonts14.add(FontConstants.COURIER_BOLD);
        BuiltinFonts14.add(FontConstants.COURIER_BOLDOBLIQUE);
        BuiltinFonts14.add(FontConstants.COURIER_OBLIQUE);
        BuiltinFonts14.add(FontConstants.HELVETICA);
        BuiltinFonts14.add(FontConstants.HELVETICA_BOLD);
        BuiltinFonts14.add(FontConstants.HELVETICA_BOLDOBLIQUE);
        BuiltinFonts14.add(FontConstants.HELVETICA_OBLIQUE);
        BuiltinFonts14.add(FontConstants.SYMBOL);
        BuiltinFonts14.add(FontConstants.TIMES_ROMAN);
        BuiltinFonts14.add(FontConstants.TIMES_BOLD);
        BuiltinFonts14.add(FontConstants.TIMES_BOLDITALIC);
        BuiltinFonts14.add(FontConstants.TIMES_ITALIC);
        BuiltinFonts14.add(FontConstants.ZAPFDINGBATS);
    }

    private String name;
    private byte[] pfb;
    private byte[] afm;
    private boolean isBuiltInFont;

    private static FontsResourceAnchor resourceAnchor;

    /**
     * Creates a new Type1 font file.
     * @param afm the AFM file if the input is made with a <CODE>byte</CODE> array
     * @param pfb the PFB file if the input is made with a <CODE>byte</CODE> array
     * @param name the name of one of the 14 built-in fonts or the location of an AFM file. The file must end in '.afm'
     * @throws PdfException the AFM file is invalid
     * @throws IOException the AFM file could not be read
     */
    public Type1Parser(String name, byte[] afm, byte[] pfb) throws PdfException, IOException {
        this.afm = afm;
        this.pfb = pfb;
        this.name = name;
    }

    public RandomAccessFileOrArray getMetricsFile() throws PdfException, IOException {
        RandomAccessFileOrArray rf;
        InputStream is = null;
        isBuiltInFont = false;
        RandomAccessSourceFactory sourceFactory = new RandomAccessSourceFactory();
        if (BuiltinFonts14.contains(name)) {
            isBuiltInFont = true;
            byte buf[] = new byte[1024];
            try {
                if (resourceAnchor == null)
                    resourceAnchor = new FontsResourceAnchor();
                is = Utilities.getResourceStream(FontConstants.RESOURCE_PATH + "afm/" + name + ".afm", resourceAnchor.getClass().getClassLoader());
                if (is == null) {
                    throw new PdfException("1.not.found.as.resource").setMessageParams(name);
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
        } else if (name.toLowerCase().endsWith(".afm")) {
            if (afm == null) {
                rf = new RandomAccessFileOrArray(sourceFactory.createBestSource(name));
            } else {
                rf = new RandomAccessFileOrArray(sourceFactory.createSource(afm));
            }
            return rf;
        } else if (name.toLowerCase().endsWith(".pfm")) {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            if (afm == null) {
                rf = new RandomAccessFileOrArray(sourceFactory.createBestSource(name));
            } else {
                rf = new RandomAccessFileOrArray(sourceFactory.createSource(afm));
            }
            Pfm2afm.convert(rf, ba);
            rf.close();
            rf = new RandomAccessFileOrArray(sourceFactory.createSource(ba.toByteArray()));
            return rf;
        }
        else {
            throw new PdfException("1.is.not.an.afm.or.pfm.font.file").setMessageParams(name);
        }
    }

    public RandomAccessFileOrArray getPostscriptBinary() throws PdfException, IOException {
        String filePfb = getPfbName();
        RandomAccessSourceFactory sourceFactory = new RandomAccessSourceFactory();
        RandomAccessFileOrArray raf;
        if (pfb == null) {
            raf = new RandomAccessFileOrArray(sourceFactory.createBestSource(filePfb));
        } else {
            raf = new RandomAccessFileOrArray(sourceFactory.createSource(pfb));
        }
        return raf;
    }

    public boolean isBuiltInFont() {
        return isBuiltInFont;
    }

    public String getName() {
        return name;
    }

    public String getPfbName() {
        return name.substring(0, name.length() - 3) + "pfb";
    }

    public byte[] getAfm() {
        return afm;
    }
}
