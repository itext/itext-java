package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.CCITTG4Encoder;
import com.itextpdf.basics.codec.TIFFFaxDecoder;
import com.itextpdf.basics.io.ByteArrayOutputStream;

import java.util.HashMap;

public final class RawImageHelper {

    public static void updateImageAttributes(RawImage image, HashMap<String, Object> additional, ByteArrayOutputStream baos) {
        if (!image.isRawImage())
            throw new IllegalArgumentException("Raw image expected.");
        // will also have the CCITT parameters
        int colorSpace = image.getColorSpace();
        byte[] imgBytes = image.getData();
        baos.assignBytes(imgBytes, imgBytes.length);
        int typeCCITT = image.getTypeCcitt();
        if (typeCCITT > 0xff) {
            if (!image.isMask())
                image.setColorSpace(1);
            image.setBpc(1);
            image.setFilter("CCITTFaxDecode");
            int k = typeCCITT - RawImage.CCITTG3_1D;
            HashMap<String, Object> decodeparms = new HashMap<>();
            if (k != 0)
                decodeparms.put("K", k);
            if ((colorSpace & RawImage.CCITT_BLACKIS1) != 0)
                decodeparms.put("BlackIs1", true);
            if ((colorSpace & RawImage.CCITT_ENCODEDBYTEALIGN) != 0)
                decodeparms.put("EncodedByteAlign", true);
            if ((colorSpace & RawImage.CCITT_ENDOFLINE) != 0)
                decodeparms.put("EndOfLine", true);
            if ((colorSpace & RawImage.CCITT_ENDOFBLOCK) != 0)
                decodeparms.put("EndOfBlock", false);
            decodeparms.put("Columns", image.getWidth());
            decodeparms.put("Rows", image.getHeight());
            image.decodeParms = decodeparms;
        } else {
            switch (colorSpace) {
                case 1:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0};
                    break;
                case 3:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0, 1, 0, 1, 0};
                    break;
                case 4:
                default:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0, 1, 0, 1, 0, 1, 0};
            }
            if (additional != null) {
                image.setImageAttributes(additional);
            }
            if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8))
                image.setColorSpace(-1);
            if (image.isDeflated()) {
                image.setFilter("FlateDecode");
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
                                                   int bpc, byte[] data) {
        image.setHeight(height);
        image.setWidth(width);
        if (components != 1 && components != 3 && components != 4)
            throw new PdfException(PdfException.ComponentsMustBe1_3Or4);
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8)
            throw new PdfException(PdfException.BitsPerComponentMustBe1_2_4or8);
        image.setColorSpace(components);
        image.setBpc(bpc);
        image.data = data;
    }

    protected static void updateRawImageParameters(RawImage image, int width, int height, int components,
                                                int bpc, byte[] data, int[] transparency) {
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
                                                int typeCCITT, int parameters, byte[] data, int transparency[]) {
        if (transparency != null && transparency.length != 2)
            throw new PdfException(PdfException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        updateCcittImageParameters(image, width, height, reverseBits, typeCCITT, parameters, data);
        image.setTransparency(transparency);
    }

    protected static void updateCcittImageParameters(RawImage image, int width, int height, boolean reverseBits, int typeCcitt, int parameters, byte[] data) {
        if (typeCcitt != RawImage.CCITTG4 && typeCcitt != RawImage.CCITTG3_1D && typeCcitt != RawImage.CCITTG3_2D)
            throw new PdfException(PdfException.CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        image.setHeight(height);
        image.setWidth(width);
        image.setColorSpace(parameters);
        image.setTypeCcitt(typeCcitt);
        image.data = data;
    }
}
