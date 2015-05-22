package com.itextpdf.core.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.CCITTG4Encoder;
import com.itextpdf.basics.codec.TIFFFaxDecoder;
import com.itextpdf.basics.image.RawImage;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

public final class RawImageHelper {

    public static void updatePdfStream(RawImage image, PdfDictionary additional, PdfStream stream) throws PdfException {
        if (!image.isRawImage())
            throw new IllegalArgumentException("Raw image expected.");
        // will also have the CCITT parameters
        int colorSpace = image.getColorSpace();
        byte[] imgBytes = image.getRawData();
        stream.getOutputStream().assignBytes(imgBytes, imgBytes.length);
        int bpc = image.getBpc();
        if (bpc > 0xff) {
            if (!image.isMask())
                stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
            stream.put(PdfName.BitsPerComponent, new PdfNumber(1));
            stream.put(PdfName.Filter, PdfName.CCITTFaxDecode);
            int k = bpc - RawImage.CCITTG3_1D;
            PdfDictionary decodeparms = new PdfDictionary();
            if (k != 0)
                decodeparms.put(PdfName.K, new PdfNumber(k));
            if ((colorSpace & RawImage.CCITT_BLACKIS1) != 0)
                decodeparms.put(PdfName.BlackIs1, PdfBoolean.PdfTrue);
            if ((colorSpace & RawImage.CCITT_ENCODEDBYTEALIGN) != 0)
                decodeparms.put(PdfName.EncodedByteAlign, PdfBoolean.PdfTrue);
            if ((colorSpace & RawImage.CCITT_ENDOFLINE) != 0)
                decodeparms.put(PdfName.EndOfLine, PdfBoolean.PdfTrue);
            if ((colorSpace & RawImage.CCITT_ENDOFBLOCK) != 0)
                decodeparms.put(PdfName.EndOfBlock, PdfBoolean.PdfFalse);
            decodeparms.put(PdfName.Columns, new PdfNumber(image.getWidth()));
            decodeparms.put(PdfName.Rows, new PdfNumber(image.getHeight()));
            stream.put(PdfName.DecodeParms, decodeparms);
        } else {
            switch (colorSpace) {
                case 1:
                    stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
                    if (image.isInverted())
                        stream.put(PdfName.Decode, new PdfArray(new float[]{1, 0}));
                    break;
                case 3:
                    stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
                    if (image.isInverted())
                        stream.put(PdfName.Decode, new PdfArray(new float[]{1, 0, 1, 0, 1, 0}));
                    break;
                case 4:
                default:
                    stream.put(PdfName.ColorSpace, PdfName.DeviceCMYK);
                    if (image.isInverted())
                        stream.put(PdfName.Decode, new PdfArray(new float[]{1, 0, 1, 0, 1, 0, 1, 0}));
            }
            if (additional != null) {
                stream.putAll(additional);
            }
            if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8))
                stream.remove(PdfName.ColorSpace);
            stream.put(PdfName.BitsPerComponent, new PdfNumber(image.getBpc()));
            if (image.isDeflated()) {
                stream.put(PdfName.Filter, PdfName.FlateDecode);
            }
            if (image.getImageMask() != null){
                stream.put(PdfName.SMask, new PdfImageXObject(stream.getDocument(), image.getImageMask()).getPdfObject());
            }
        }
    }

    /**
     * Update original image with Raw Image parameters.
     *
     * @param width the exact width of the image
     * @param height the exact height of the image
     * @param components 1,3 or 4 for GrayScale, RGB and CMYK
     * @param bpc bits per component. Must be 1,2,4 or 8
     * @param data the image data
     * @throws com.itextpdf.basics.PdfException on error
     */
    protected static void updateRawImageParameters(RawImage image, int width, int height, int components,
                                                   int bpc, byte[] data) throws PdfException {
        image.setHeight(height);
        image.setWidth(width);
        if (components != 1 && components != 3 && components != 4)
            throw new PdfException(PdfException.ComponentsMustBe1_3Or4);
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8)
            throw new PdfException(PdfException.BitsPerComponentMustBe1_2_4or8);
        image.setColorSpace(components);
        image.setBpc(bpc);
        image.setRawData(data);
    }

    protected static void updateRawImageParameters(RawImage image, int width, int height, int components,
                                                int bpc, byte[] data, int[] transparency) throws PdfException {
        if (transparency != null && transparency.length != components * 2)
            throw new PdfException(PdfException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (components == 1 && bpc == 1) {
            byte g4[] = CCITTG4Encoder.compress(data, width, height);
            updateRawImageParameters(image, width, height, false, RawImage.CCITTG4,
                    RawImage.CCITT_BLACKIS1, g4, transparency);
        } else {
            updateRawImageParameters(image, width, height, components, bpc, data);
            image.setTransparency(transparency);
        }
    }

    protected static void updateRawImageParameters(RawImage image, int width, int height, boolean reverseBits,
                                                int typeCCITT, int parameters, byte[] data, int transparency[]) throws PdfException {
        if (transparency != null && transparency.length != 2)
            throw new PdfException(PdfException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        updateCcittImageParameters(image, width, height, reverseBits, typeCCITT, parameters, data);
        image.setTransparency(transparency);
    }

    protected static void updateCcittImageParameters(RawImage image, int width, int height, boolean reverseBits, int typeCcitt, int parameters, byte[] data) throws PdfException {
        if (typeCcitt != RawImage.CCITTG4 && typeCcitt != RawImage.CCITTG3_1D && typeCcitt != RawImage.CCITTG3_2D)
            throw new PdfException(PdfException.CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        image.setHeight(height);
        image.setWidth(width);
        image.setColorSpace(parameters);
        image.setTypeCcitt(typeCcitt);
        image.setRawData(data);
    }
}
