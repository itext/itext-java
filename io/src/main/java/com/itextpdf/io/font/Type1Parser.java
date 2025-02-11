/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.constants.FontResources;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.ResourceUtil;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;

class Type1Parser {


    private static final String AFM_HEADER = "StartFontMetrics";

    private String afmPath;
    private String pfbPath;
    private byte[] pfbData;
    private byte[] afmData;
    private boolean isBuiltInFont;

    private RandomAccessSourceFactory sourceFactory = new RandomAccessSourceFactory();

    /**
     * Creates a new Type1 font file.
     * @param afm the AFM file if the input is made with a <CODE>byte</CODE> array
     * @param pfb the PFB file if the input is made with a <CODE>byte</CODE> array
     * @param metricsPath the name of one of the 14 built-in fonts or the location of an AFM file. The file must end in '.afm'
     */
    public Type1Parser(String metricsPath, String binaryPath, byte[] afm, byte[] pfb) {
        this.afmData = afm;
        this.pfbData = pfb;
        this.afmPath = metricsPath;
        this.pfbPath = binaryPath;
    }

    public RandomAccessFileOrArray getMetricsFile() throws java.io.IOException {
        isBuiltInFont = false;
        if (StandardFonts.isStandardFont(afmPath)) {
            isBuiltInFont = true;
            byte[] buf = new byte[1024];
            InputStream resource = null;
            try {
                String resourcePath = FontResources.AFMS + afmPath + ".afm";
                resource = ResourceUtil.getResourceStream(resourcePath);
                if (resource == null) {
                    throw new IOException("{0} was not found as resource.").setMessageParams(resourcePath);
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int read;
                while ((read = resource.read(buf)) >= 0) {
                    stream.write(buf, 0, read);
                }
                buf = stream.toByteArray();
            } finally {
                if (resource != null) {
                    try {
                        resource.close();
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
                throw new IOException(IoExceptionMessageConstant.IS_NOT_AN_AFM_OR_PFM_FONT_FILE).setMessageParams(afmPath);
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
                    throw new IOException("Invalid afm or pfm font file.");
                } finally {
                    rf.close();
                }
                return new RandomAccessFileOrArray(sourceFactory.createSource(ba.toByteArray()));
            }
        } else {
            throw new IOException("Invalid afm or pfm font file.");
        }
    }

    public RandomAccessFileOrArray getPostscriptBinary() throws java.io.IOException {
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

    private boolean isAfmFile(RandomAccessFileOrArray raf) throws java.io.IOException {
        StringBuilder builder = new StringBuilder(AFM_HEADER.length());
        for (int i = 0; i < AFM_HEADER.length(); i++) {
            try {
                builder.append((char)raf.readByte());
            } catch (EOFException e) {
                raf.seek(0);
                return false;
            }
        }
        raf.seek(0);
        return AFM_HEADER.equals(builder.toString());
    }
}
