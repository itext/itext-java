/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.codec.PngWriter;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TiffWriter;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Gray;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Separation;
import com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors.IInputConversionFunction;
import com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors.IOutputConversionFunction;
import com.itextpdf.kernel.pdf.function.IPdfFunction;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject.ImageBytesRetrievalProperties;
import com.itextpdf.kernel.utils.BitmapImagePixels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ImagePdfBytesInfo {

    private static final String TIFFTAG_SOFTWARE_VALUE = "iText\u00ae " +
            ITextCoreProductData.getInstance().getVersion() + " \u00a9" + ITextCoreProductData.getInstance()
            .getSinceCopyrightYear() + "-" + ITextCoreProductData.getInstance().getToCopyrightYear()
            + " Apryse Group NV";
    private final int width;
    private final int height;
    private final List<IPdfFunction> colorTransformations = new ArrayList<IPdfFunction>();
    private final PdfImageXObject imageXObject;
    private final ImageBytesRetrievalProperties properties;
    private double[] decodeArray = null;
    /**
     * The number of color channels in the output
     */
    private int channels;
    /**
     * Is there an alpha channel of an alpha mask in the output
     */
    private boolean alphaChannel;
    /**
     * color depth of output image
     */
    private int colorDepth;
    /**
     * palette information, null when not required
     */
    private Palette palette = null;
    private PdfColorSpace sourceColorSpace;
    private PdfColorSpace targetColorSpace;
    private PdfImageXObject transparencyMask;
    private OutputFileType outputFileType;
    private byte[] iccData;

    public ImagePdfBytesInfo(PdfImageXObject imageXObject, ImageBytesRetrievalProperties properties) {
        this.properties =properties;
        this.imageXObject = imageXObject;
        if (properties.isApplyDecodeArray()
                && imageXObject.getPdfObject().containsKey(PdfName.Decode)) {
            decodeArray = imageXObject.getPdfObject().getAsArray(PdfName.Decode).toDoubleArray();
        }

        extractColorInfo(imageXObject);

        width = (int) imageXObject.getWidth();
        height = (int) imageXObject.getHeight();
    }

    public OutputFileType getImageType() {
        return outputFileType;
    }

    public byte[] getProcessedImageData(byte[] intialBytes) throws IOException {
        if (channels > 1 && colorDepth != 8 && colorDepth != 16) {
            throw new com.itextpdf.io.exceptions.IOException(
                    KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE)
                    .setMessageParams(colorDepth, sourceColorSpace.getName());
        }

        byte[] data = PdfReader.decodeBytes(intialBytes, imageXObject.getPdfObject());

        if ( decodeArray != null && !isNeutralDecodeArray(decodeArray)) {
            data = applyDecoding(data);
        }
        for (IPdfFunction fct : colorTransformations) {
            data = fct.calculateFromByteArray(data, 0, data.length, 1, 1);
        }
        if (transparencyMask != null) {
            data = applytransparency(data);
        }

        ImageProcesser proc;

        if (outputFileType == OutputFileType.PNG) {
            proc = new PngImageProcessor(data, transparencyMask, palette, iccData, targetColorSpace, colorDepth, width,
                    height);
        } else {
            proc = new TiffImageProcessor(data, transparencyMask, palette, iccData, targetColorSpace, colorDepth, width,
                    height);
        }
        return proc.processImage();
    }

    int getPngColorType() {
        if (outputFileType == OutputFileType.PNG) {
            PngImageProcessor proc = new PngImageProcessor(new byte[0], transparencyMask, palette, iccData,
                    targetColorSpace, colorDepth, width, height);
            return proc.getColorTypeFromColorSpace(targetColorSpace).ordinal();
        }
        return -1;
    }

    private boolean isNeutralDecodeArray(double[] decodeArray) {
        for (int i = 0; i <= channels / 2; i++) {
            if (decodeArray[i * 2] > 0.0 && decodeArray[i * 2 + 1] < 1.0) {
                return false;
            }
        }
        return true;
    }

    private void extractColorInfo(PdfImageXObject imageXObject) {
        if (imageXObject.getPdfObject().containsKey(PdfName.BitsPerComponent)) {
            colorDepth = imageXObject.getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue();
        } else {
            colorDepth = 1;
        }
        if (properties.isApplyTransparency() && imageXObject.getPdfObject().containsKey(PdfName.SMask)) {
            alphaChannel = true;
            transparencyMask = new PdfImageXObject(imageXObject.getPdfObject().getAsStream(PdfName.SMask));
        }
        PdfObject colorSpace;

        if (imageXObject.isMask() || imageXObject.isSoftMask()) {
            this.sourceColorSpace = new Gray();
            colorSpace = sourceColorSpace.getPdfObject();
        } else {
            colorSpace = imageXObject.getPdfObject().get(PdfName.ColorSpace);
            this.sourceColorSpace = PdfColorSpace.makeColorSpace(colorSpace);
        }
        this.targetColorSpace = sourceColorSpace;
        outputFileType = OutputFileType.PNG;

        if (colorSpace.isName()) {
            switch (((PdfName) colorSpace).getValue()) {
                case "DeviceGray":
                    channels = 1;
                    break;
                case "DeviceRGB":
                    channels = 3;
                    break;
                case "DeviceCMYK":
                    channels = 4;
                    outputFileType = OutputFileType.TIFF;
                    break;
                default:
                    throw new com.itextpdf.io.exceptions.IOException(
                            KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED)
                            .setMessageParams(((PdfName) colorSpace).getValue());
            }
        } else {
            if (colorSpace.isArray()) {
                PdfArray csArray = (PdfArray) colorSpace;
                switch (((PdfName) csArray.get(0)).getValue()) {
                    case "Indexed":
                        palette = new Palette(csArray, colorDepth);
                        long color0 = isPaletteBlackAndWhite(palette);
                        if (colorDepth == 1 && color0 >= 0) {
                            targetColorSpace = new PdfDeviceCs.Gray();
                            if (color0 == 1 && decodeArray == null) {
                                decodeArray = new double[] {1.0, 0.0};
                            }
                            palette = null;
                            break;
                        }
                        if ((properties.isApplyTransparency() && alphaChannel)
                                || palette.getBaseColorspace().getNumberOfComponents() == 1) {
                            targetColorSpace = palette.getBaseColorspace();
                            colorTransformations.add(new DeIndexingTransformation(palette));
                            palette = null;
                        }
                        break;
                    case "DeviceN":
                    case "NChannel":
                        throw new com.itextpdf.io.exceptions.IOException(
                                KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED)
                                .setMessageParams(csArray.get(0).toString());
                    case "Separation":
                        Separation separationCs = (Separation) this.sourceColorSpace;
                        if (properties.isApplyTintTransformations()) {
                            colorTransformations.add(separationCs.getTintTransformation());
                            targetColorSpace = separationCs.getBaseCs();
                            if (targetColorSpace.getName() != PdfName.DeviceRGB
                                    && targetColorSpace.getName() != PdfName.CalRGB
                            ) {
                                throw new UnsupportedOperationException(
                                        KernelExceptionMessageConstant
                                                .GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB);
                            }
                            if (colorDepth < 8) {
                                throw new com.itextpdf.io.exceptions.IOException(
                                        KernelExceptionMessageConstant
                                                .COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_SEPARATION_ALTERNATE_COLORSPACE)
                                        .setMessageParams(colorDepth, targetColorSpace.getName());
                            }
                        } else {
                            targetColorSpace = new Gray();
                        }
                        break;
                    case "ICCBased":
                        PdfStream iccStream = null;
                        if (csArray.get(1).isIndirectReference()) {
                            iccStream = (PdfStream) ((PdfIndirectReference) csArray.get(1)).getRefersTo();
                        } else {
                            iccStream = (PdfStream) csArray.get(1);
                        }
                        if (targetColorSpace.getNumberOfComponents() > 3) {
                            outputFileType = OutputFileType.TIFF;
                        }
                        int iccComponents = targetColorSpace.getNumberOfComponents();
                        if (iccComponents != 1 && iccComponents != 3 && iccComponents != 4) {
                            throw new com.itextpdf.io.exceptions.IOException(
                                    KernelExceptionMessageConstant.N_VALUE_IS_NOT_SUPPORTED)
                                    .setMessageParams(iccComponents);
                        }
                        iccData = iccStream.getBytes();
                        break;
                    case "CalGray":
                    case "CalRGB":
                        break;
                    default:
                        throw new com.itextpdf.io.exceptions.IOException(
                                KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED)
                                .setMessageParams(csArray.get(0));
                }
                channels = targetColorSpace.getNumberOfComponents();
            }
        }
    }

    private static long isPaletteBlackAndWhite(Palette palette) {
        // more than 2 values
        if (palette.getHiVal() > 1) {
            return -1;
        }
        long color0 = -1;
        for (int c = 0; c < palette.getBaseColorspace().getNumberOfComponents(); c++) {
            for (int i = 0; i < 2; i++) {
                switch ((int) palette.getColor(i)[c]) {
                    case 0:
                        if (i == 0) {
                            color0 = 0;
                        }
                        break;
                    case 0xff:
                        if (i == 0) {
                            color0 = 1;
                        }
                        break;
                    default:
                        return -1;
                }
            }
        }
        return color0;
    }

    private byte[] applytransparency(byte[] imageData) {
        int maskMultiplier = 8 / transparencyMask.getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue();
        byte[] mask = transparencyMask.getImageBytes(false);

        mask = PdfReader.decodeBytes(mask, transparencyMask.getPdfObject());
        byte[] out = new byte[(imageData.length / channels) * (channels + 1)];
        BitmapImagePixels imageInPix = new BitmapImagePixels(this.width, this.height, colorDepth, channels, imageData);
        BitmapImagePixels imageOutPix = new BitmapImagePixels(this.width, this.height, colorDepth,
                channels + 1, out);
        BitmapImagePixels maskPix = new BitmapImagePixels(this.width, this.height, colorDepth, 1, mask);

        long[] nPix = new long[channels + 1];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                long[] oPix = imageInPix.getPixelAsLongs(x, y);

                System.arraycopy(oPix, 0, nPix, 0, channels);
                nPix[channels] = maskPix.getPixelAsLongs(x, y)[0] * maskMultiplier;
                imageOutPix.setPixel(x, y, nPix);
            }
        }
        return imageOutPix.getData();
    }

    private byte[] applyDecoding(byte[] imageData) {
        BitmapImagePixels imagePixels = new BitmapImagePixels(width, height, colorDepth,
                sourceColorSpace.getNumberOfComponents(), imageData);

        double[] factors = new double[sourceColorSpace.getNumberOfComponents()];
        double[] floor = new double[sourceColorSpace.getNumberOfComponents()];
        for (int i = 0; i < sourceColorSpace.getNumberOfComponents(); i++) {
            factors[i] = (decodeArray[i * 2 + 1] - decodeArray[i * 2]);
            floor[i] = decodeArray[i * 2] * ((1 << colorDepth) - 1);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                long[] pix = imagePixels.getPixelAsLongs(x, y);
                for (int c = 0; c < sourceColorSpace.getNumberOfComponents(); c++) {
                    pix[c] = (long) (floor[c] + pix[c] * factors[c]);
                }
                imagePixels.setPixel(x, y, pix);
            }
        }

        return imagePixels.getData();
    }

    public enum OutputFileType {
        TIFF,
        PNG
    }

    public enum PngColorType {
        GRAYSCALE,
        INVALID_1,
        RGB,
        PALETTE,
        GRAYSCALE_ALPHA,
        INVALID_5,
        RGBA
    }

    private static class Palette {
        private final PdfColorSpace baseColorspace;
        private final int hiVal;
        private final int indexBitDepth;
        private final byte[] paletteData;
        private final int paletteChannels;
        public Palette(PdfArray csArray, int indexBitDepth) {
            if (csArray.size() != 4) {
                throw new PdfException(KernelExceptionMessageConstant.PALLET_CONTENT_ERROR);
            }
            this.indexBitDepth = indexBitDepth;
            baseColorspace = PdfColorSpace.makeColorSpace(csArray.get(1));
            paletteChannels = baseColorspace.getNumberOfComponents();
            hiVal = ((PdfNumber) csArray.get(2)).intValue();
            PdfObject data = csArray.get(3);

            if (data.isStream()) {
                paletteData = ((PdfStream) data).getBytes();
            } else if (data.isString()) {
                paletteData = ((PdfString) data).getValueBytes();
            } else {
                paletteData = null;
            }
        }

        public int getIndexBitDepth() {
            return indexBitDepth;
        }

        public byte[] getPaletteData() {
            return paletteData;
        }

        public PdfColorSpace getBaseColorspace() {
            return baseColorspace;
        }

        public long[] getColor(long index) {
            long[] result = new long[paletteChannels];
            for (int c = 0; c < paletteChannels; c++) {
                result[c] = paletteData[(int) index * paletteChannels + c] & 0xff;
            }
            return result;
        }

        public int getHiVal() {
            return hiVal;
        }
    }

    private interface ImageProcesser {
        byte[] processImage() throws IOException;
    }

    private static class TiffImageProcessor implements ImageProcesser {

        private final byte[] imageData;
        private final PdfImageXObject transparencyMask;
        private final PdfColorSpace colorSpace;
        private final int width;
        private final int height;
        private final int colorDepth;
        private final byte[] iccProfile;

        public TiffImageProcessor(byte[] imageData,
                PdfImageXObject transparencyMask,
                Palette palette, byte[] iccProfile,
                PdfColorSpace colorSpace,
                int colorDepth,
                int width, int height) {
            this.imageData = imageData;
            this.transparencyMask = transparencyMask;
            this.iccProfile = iccProfile;
            this.colorSpace = colorSpace;

            this.colorDepth = colorDepth;
            this.width = width;
            this.height = height;
        }

        @Override
        public byte[] processImage() throws IOException {
            java.io.ByteArrayOutputStream ms = new java.io.ByteArrayOutputStream();
            int samples = colorSpace.getNumberOfComponents();
            if (transparencyMask != null) {
                samples++;
            }
            int[] bitsPerSample = new int[samples];
            for (int i = 0; i < samples; i++) {
                bitsPerSample[i] = colorDepth;
            }
            int stride = samples * width;
            TiffWriter wr = new TiffWriter();
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_SAMPLESPERPIXEL,
                    colorSpace.getNumberOfComponents()));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_BITSPERSAMPLE, bitsPerSample));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_PHOTOMETRIC,
                    TIFFConstants.PHOTOMETRIC_SEPARATED));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_IMAGEWIDTH, width));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_IMAGELENGTH, height));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_COMPRESSION, TIFFConstants.COMPRESSION_LZW));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_PREDICTOR,
                    TIFFConstants.PREDICTOR_HORIZONTAL_DIFFERENCING));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_ROWSPERSTRIP, height));
            wr.addField(new TiffWriter.FieldRational(TIFFConstants.TIFFTAG_XRESOLUTION, new int[] {300, 1}));
            wr.addField(new TiffWriter.FieldRational(TIFFConstants.TIFFTAG_YRESOLUTION, new int[] {300, 1}));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_RESOLUTIONUNIT, TIFFConstants.RESUNIT_INCH));
            wr.addField(new TiffWriter.FieldAscii(TIFFConstants.TIFFTAG_SOFTWARE, TIFFTAG_SOFTWARE_VALUE));
            java.io.ByteArrayOutputStream comp = new java.io.ByteArrayOutputStream();
            TiffWriter.compressLZW(comp, 2, imageData, height, samples, stride);
            byte[] buf = comp.toByteArray();
            wr.addField(new TiffWriter.FieldImage(buf));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS, buf.length));
            if (iccProfile != null) {
                wr.addField(new TiffWriter.FieldUndefined(TIFFConstants.TIFFTAG_ICCPROFILE, iccProfile));
            }
            wr.writeFile(ms);
            return ms.toByteArray();
        }
    }

    private static class PngImageProcessor implements ImageProcesser {
        private final byte[] imageData;
        private final PdfImageXObject transparencyMask;
        private final PdfColorSpace colorSpace;
        private final int width;
        private final int height;
        private final int colorDepth;
        private final byte[] iccProfile;
        private final Palette palette;

        public PngImageProcessor(byte[] imageData,
                PdfImageXObject transparencyMask,
                Palette palette, byte[] iccProfile,
                PdfColorSpace colorSpace,
                int colorDepth,
                int width, int height) {
            this.imageData = imageData;
            this.transparencyMask = transparencyMask;
            this.palette = palette;
            this.iccProfile = iccProfile;
            this.colorSpace = colorSpace;

            this.colorDepth = colorDepth;
            this.width = width;
            this.height = height;
        }

        public PngColorType getColorTypeFromColorSpace(PdfColorSpace colorSpace) {
            switch (colorSpace.getNumberOfComponents()) {
                case 1:
                    if (palette == null) {
                        if (transparencyMask == null) {
                            return PngColorType.GRAYSCALE;
                        } else {
                            return PngColorType.GRAYSCALE_ALPHA;
                        }
                    } else {
                        return PngColorType.PALETTE;
                    }
                case 3:
                    if (transparencyMask == null) {
                        return PngColorType.RGB;
                    } else {
                        return PngColorType.RGBA;
                    }
                default:
                    throw new UnsupportedOperationException(MessageFormatUtil.format(
                            KernelExceptionMessageConstant.PNG_CHANNEL_ERROR,
                            colorSpace.getNumberOfComponents()));
            }

        }

        @Override
        public byte[] processImage() throws IOException {
            java.io.ByteArrayOutputStream ms = new java.io.ByteArrayOutputStream();

            PngWriter png = new PngWriter(ms);
            PngColorType colorType = getColorTypeFromColorSpace(colorSpace);
            png.writeHeader(width, height, colorDepth, colorType.ordinal());
            if (iccProfile != null) {
                png.writeIccProfile(iccProfile);
            }
            if (palette != null && palette.getPaletteData() != null) {
                png.writePalette(palette.getPaletteData());
            }
            int stride = (width * colorDepth *
                    (colorSpace.getNumberOfComponents() + (transparencyMask == null ? 0 : 1)) + 7) / 8;
            png.writeData(imageData, stride);

            png.writeEnd();
            return ms.toByteArray();
        }
    }

    private class DeIndexingTransformation implements IPdfFunction {
        private final Palette palette;

        public DeIndexingTransformation(Palette palette) {
            this.palette = palette;
        }

        @Override
        public int getFunctionType() {
            return -1;
        }

        @Override
        public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
            return palette.getBaseColorspace().equals(alternateSpace);
        }

        @Override
        public int getInputSize() {
            return 1;
        }

        @Override
        public int getOutputSize() {
            return palette.getBaseColorspace().getNumberOfComponents();
        }

        @Override
        public double[] getDomain() {
            return new double[] {0, 1};
        }

        @Override
        public void setDomain(double[] value) {
            // not needed because this is not a real PdfFunction
        }

        @Override
        public double[] getRange() {
            double[] range = new double[palette.getBaseColorspace().getNumberOfComponents() * 2];
            for (int i = 0; i < palette.getBaseColorspace().getNumberOfComponents(); i++) {
                range[i * 2] = 0;
                range[i * 2 + 1] = 1;
            }
            return range;
        }

        @Override
        public void setRange(double[] value) {
            // not needed because this is not a real PdfFunction
        }

        @Override
        public double[] calculate(double[] input) {
            return new double[0];
        }

        @Override
        public byte[] calculateFromByteArray(byte[] bytes, int offset, int length, int wordSizeInputLength,
                int wordSizeOutputLength) throws IOException {
            final byte[] output = new byte[palette.getBaseColorspace().getNumberOfComponents()
                    * length * (9 - palette.getIndexBitDepth())];
            BitmapImagePixels indexedPixels = new BitmapImagePixels(width, height, colorDepth, 1, bytes);
            BitmapImagePixels deIndexedPixels = new BitmapImagePixels(width, height, 8,
                    palette.getBaseColorspace().getNumberOfComponents(), output);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    long[] color = palette.getColor(indexedPixels.getPixelAsLongs(x, y)[0]);

                    deIndexedPixels.setPixel(x, y, color);
                }
            }
            return deIndexedPixels.getData();
        }

        @Override
        public byte[] calculateFromByteArray(byte[] bytes, int offset, int length, int wordSizeInputLength,
                int wordSizeOutputLength, IInputConversionFunction inputConvertor,
                IOutputConversionFunction outputConvertor) throws IOException {
            return new byte[0];
        }

        @Override
        public double[] clipInput(double[] input) {
            return new double[0];
        }

        @Override
        public double[] clipOutput(double[] input) {
            return new double[0];
        }

        @Override
        public PdfObject getAsPdfObject() {
            return null;
        }
    }
}

