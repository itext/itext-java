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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.codec.PngWriter;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TiffWriter;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Separation;
import com.itextpdf.kernel.pdf.function.IPdfFunction;
import com.itextpdf.kernel.pdf.function.PdfFunctionFactory;

import java.io.IOException;

class ImagePdfBytesInfo {

    private static final String TIFFTAG_SOFTWARE_VALUE = "iText\u00ae " +
            ITextCoreProductData.getInstance().getVersion() + " \u00a9" + ITextCoreProductData.getInstance()
            .getSinceCopyrightYear() + "-" + ITextCoreProductData.getInstance().getToCopyrightYear()
            + " Apryse Group NV";
    private final int bpc;
    private final int width;
    private final int height;
    private final PdfObject colorspace;
    private final PdfArray decode;
    private int pngColorType;
    private int pngBitDepth;
    private byte[] palette;
    private byte[] icc;
    private int stride;

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
        if (pngColorType < 0) {
            if (bpc != 8)
                throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED).setMessageParams(bpc);

            if (colorspace instanceof PdfArray) {
                PdfArray ca = (PdfArray) colorspace;
                PdfObject tyca = ca.get(0);
                if (!PdfName.ICCBased.equals(tyca))
                    throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED).setMessageParams(tyca.toString());

                PdfStream pr = (PdfStream) ca.get(1);
                int n = pr.getAsNumber(PdfName.N).intValue();
                if (n != 4) {
                    throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.N_VALUE_IS_NOT_SUPPORTED).setMessageParams(n);
                }
                icc = pr.getBytes();
            } else if (!PdfName.DeviceCMYK.equals(colorspace)) {
                throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED).setMessageParams(colorspace.toString());
            }
            java.io.ByteArrayOutputStream ms = new java.io.ByteArrayOutputStream();

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
            wr.addField(new TiffWriter.FieldAscii(TIFFConstants.TIFFTAG_SOFTWARE, TIFFTAG_SOFTWARE_VALUE));
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
            if (colorspace instanceof PdfArray) {
                PdfArray ca = (PdfArray) colorspace;
                PdfObject tyca = ca.get(0);
                if (PdfName.Separation.equals(tyca)) {
                    return processSeperationColor(imageBytes, ca);
                }
            }
            return processPng(imageBytes, pngBitDepth, pngColorType);
        }
    }

    private byte[] processSeperationColor(byte[] imageBytes, PdfArray colorSpaceArray) throws IOException {
        Separation scs = new Separation(colorSpaceArray);

        byte[] newImageBytes = scs.getTintTransformation().calculateFromByteArray(imageBytes, 0,
                imageBytes.length, 8, 8
        );
        // TODO DEVSIX-6757 switch top tiff for CMYK
        // TODO DEVSIX-6757 verify RGBA is working
        if (scs.getBaseCs().getNumberOfComponents() > 3) {
            throw new UnsupportedOperationException(KernelExceptionMessageConstant.
                    GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB);
        }


        stride = (width * bpc * 3 + 7) / 8;
        return processPng(newImageBytes, pngBitDepth, 2);

    }

    private byte[] processPng(byte[] imageBytes, int pngBitDepth, int pngColorType) throws IOException {
        java.io.ByteArrayOutputStream ms = new java.io.ByteArrayOutputStream();

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
                // TODO DEVSIX-7015 add decode transformation for other depths
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

    /**
     * Sets state of this object according to the color space
     *
     * @param csObj   the colorspace to use
     * @param allowIndexed whether indexed color spaces will be resolved (used for recursive call)
     * @throws IOException if there is a problem with reading from the underlying stream
     */
    private void findColorspace(PdfObject csObj, boolean allowIndexed) {
        if (PdfName.DeviceGray.equals(csObj) || (csObj == null && bpc == 1)) {
            // handle imagemasks
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
            } else if (PdfName.Separation.equals(tyca)) {
                IPdfFunction fct = PdfFunctionFactory.create(ca.get(3));
                int components = fct.getOutputSize();
                pngColorType = components == 1? 1: 2;
                pngBitDepth = 8;
            }
        }
    }
}

