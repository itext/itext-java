/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.codec.PngWriter;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TiffWriter;
import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import java.io.IOException;

class ImagePdfBytesInfo {
    private int pngColorType;
    private int pngBitDepth;
    private int bpc;
    private byte[] palette;
    private byte[] icc;
    private int stride;
    private int width;
    private int height;
    private PdfObject colorspace;
    private PdfArray decode;

    public ImagePdfBytesInfo(PdfImageXObject imageXObject) {
        pngColorType = -1;
        bpc = imageXObject.getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue();
        pngBitDepth = bpc;

        palette = null;
        icc = null;
        stride = 0;
        width = (int) imageXObject.getWidth();
        height = (int) imageXObject.getHeight();
        colorspace = imageXObject.getPdfObject().get(PdfName.ColorSpace);
        decode = imageXObject.getPdfObject().getAsArray(PdfName.Decode);
        findColorspace(colorspace, true);
    }

    public int getPngColorType() {
        return pngColorType;
    }

    public byte[] decodeTiffAndPngBytes(byte[] imageBytes) throws IOException {
        java.io.ByteArrayOutputStream ms = new java.io.ByteArrayOutputStream();
        if (pngColorType < 0) {
            if (bpc != 8)
                throw new com.itextpdf.io.IOException(com.itextpdf.io.IOException.ColorDepthIsNotSupported).setMessageParams(bpc);

            if (colorspace instanceof PdfArray) {
                PdfArray ca = (PdfArray) colorspace;
                PdfObject tyca = ca.get(0);
                if (!PdfName.ICCBased.equals(tyca))
                    throw new com.itextpdf.io.IOException(com.itextpdf.io.IOException.ColorSpaceIsNotSupported).setMessageParams(tyca.toString());
                PdfStream pr = (PdfStream) ca.get(1);
                int n = pr.getAsNumber(PdfName.N).intValue();
                if (n != 4) {
                    throw new com.itextpdf.io.IOException(com.itextpdf.io.IOException.NValueIsNotSupported).setMessageParams(n);
                }
                icc = pr.getBytes();
            } else if (!PdfName.DeviceCMYK.equals(colorspace)) {
                throw new com.itextpdf.io.IOException(com.itextpdf.io.IOException.ColorSpaceIsNotSupported).setMessageParams(colorspace.toString());
            }
            stride = 4 * width;
            TiffWriter wr = new TiffWriter();
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_SAMPLESPERPIXEL, 4));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_BITSPERSAMPLE, new int[]{8, 8, 8, 8}));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_PHOTOMETRIC, TIFFConstants.PHOTOMETRIC_SEPARATED));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_IMAGEWIDTH, (int) width));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_IMAGELENGTH, (int) height));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_COMPRESSION, TIFFConstants.COMPRESSION_LZW));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_PREDICTOR, TIFFConstants.PREDICTOR_HORIZONTAL_DIFFERENCING));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_ROWSPERSTRIP, (int) height));
            wr.addField(new TiffWriter.FieldRational(TIFFConstants.TIFFTAG_XRESOLUTION, new int[]{300, 1}));
            wr.addField(new TiffWriter.FieldRational(TIFFConstants.TIFFTAG_YRESOLUTION, new int[]{300, 1}));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_RESOLUTIONUNIT, TIFFConstants.RESUNIT_INCH));
            wr.addField(new TiffWriter.FieldAscii(TIFFConstants.TIFFTAG_SOFTWARE, Version.getInstance().getVersion()));
            java.io.ByteArrayOutputStream comp = new java.io.ByteArrayOutputStream();
            TiffWriter.compressLZW(comp, 2, imageBytes, (int) height, 4, stride);
            byte[] buf = comp.toByteArray();
            wr.addField(new TiffWriter.FieldImage(buf));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS, buf.length));
            if (icc != null) {
                wr.addField(new TiffWriter.FieldUndefined(TIFFConstants.TIFFTAG_ICCPROFILE, icc));
            }
            wr.writeFile(ms);

            imageBytes = ms.toByteArray();
            return imageBytes;
        } else {
            PngWriter png = new PngWriter(ms);
            if (decode != null) {
                if (pngBitDepth == 1) {
                    // if the decode array is 1,0, then we need to invert the image
                    if (decode.getAsNumber(0).intValue() == 1 && decode.getAsNumber(1).intValue() == 0) {
                        int len = imageBytes.length;
                        for (int t = 0; t < len; ++t) {
                            imageBytes[t] ^= 0xff;
                        }
                    } else {
                        // if the decode array is 0,1, do nothing.  It's possible that the array could be 0,0 or 1,1 - but that would be silly, so we'll just ignore that case
                    }
                } else {
                    // todo: add decode transformation for other depths
                }
            }
            png.writeHeader(width, height, pngBitDepth, pngColorType);
            if (icc != null) {
                png.writeIccProfile(icc);
            }
            if (palette != null) {
                png.writePalette(palette);
            }
            png.writeData(imageBytes, stride);
            png.writeEnd();
            imageBytes = ms.toByteArray();
            return imageBytes;
        }
    }

    /**
     * Sets state of this object according to the color space
     *
     * @param csObj   the colorspace to use
     * @param allowIndexed whether indexed color spaces will be resolved (used for recursive call)
     * @param width
     * @param height
     * @throws IOException if there is a problem with reading from the underlying stream
     */
    private void findColorspace(PdfObject csObj, boolean allowIndexed) {
        if (PdfName.DeviceGray.equals(csObj) || (csObj == null && bpc == 1)) { // handle imagemasks
            stride = (width * bpc + 7) / 8;
            pngColorType = 0;
        } else if (PdfName.DeviceRGB.equals(csObj)) {
            if (bpc == 8 || bpc == 16) {
                stride = (width * bpc * 3 + 7) / 8;
                pngColorType = 2;
            }
        } else if (csObj instanceof PdfArray) {
            PdfArray ca = (PdfArray) csObj;
            PdfObject tyca = ca.get(0);
            if (PdfName.CalGray.equals(tyca)) {
                stride = (width * bpc + 7) / 8;
                pngColorType = 0;
            } else if (PdfName.CalRGB.equals(tyca)) {
                if (bpc == 8 || bpc == 16) {
                    stride = (width * bpc * 3 + 7) / 8;
                    pngColorType = 2;
                }
            } else if (PdfName.ICCBased.equals(tyca)) {
                PdfStream pr = (PdfStream) ca.get(1);
                int n = pr.getAsNumber(PdfName.N).intValue();
                if (n == 1) {
                    stride = (width * bpc + 7) / 8;
                    pngColorType = 0;
                    icc = pr.getBytes();
                } else if (n == 3) {
                    stride = (width * bpc * 3 + 7) / 8;
                    pngColorType = 2;
                    icc = pr.getBytes();
                }
            } else if (allowIndexed && PdfName.Indexed.equals(tyca)) {
                findColorspace(ca.get(1), false);
                if (pngColorType == 2) {
                    PdfObject id2 = ca.get(3);
                    if (id2 instanceof PdfString) {
                        palette = ((PdfString) id2).getValueBytes();
                    } else if (id2 instanceof PdfStream) {
                        palette = ((PdfStream) id2).getBytes();
                    }
                    stride = (width * bpc + 7) / 8;
                    pngColorType = 3;
                }
            }
        }
    }
}
