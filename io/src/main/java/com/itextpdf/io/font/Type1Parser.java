/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.constants.FontResources;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.ResourceUtil;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.Serializable;

class Type1Parser implements Serializable {

    private static final long serialVersionUID = -8484541242371901414L;

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
     * @the AFM file is invalid
     * @throws java.io.IOException the AFM file could not be read
     */
    public Type1Parser(String metricsPath, String binaryPath, byte[] afm, byte[] pfb) throws java.io.IOException {
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
                throw new IOException(IOException._1IsNotAnAfmOrPfmFontFile).setMessageParams(afmPath);
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
