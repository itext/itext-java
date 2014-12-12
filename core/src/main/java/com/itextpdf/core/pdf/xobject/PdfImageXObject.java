package com.itextpdf.core.pdf.xobject;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.CcittImage;
import com.itextpdf.basics.image.Image;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfBoolean;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfImageXObject extends PdfXObject {

    private static final int transferSize = 64 * 1024;

    private float width;
    private float height;
    private boolean mask;
    private boolean softMask;

    public PdfImageXObject(PdfDocument document, Image image) throws PdfException {
        this(document, image, null);
    }

    public PdfImageXObject(PdfDocument document, Image image, PdfImageXObject imageMask) throws PdfException {
        this(createPdfStream(document, image, imageMask), document);
        mask = image.isMask();
        softMask = image.isSoftMask();
    }

    public PdfImageXObject(PdfStream pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    public float getWidth() throws PdfException {
        if (!isFlushed())
            return getPdfObject().getAsNumber(PdfName.Width).getFloatValue();
        else
            return width;
    }

    public float getHeight() throws PdfException {
        if (!isFlushed())
            return getPdfObject().getAsNumber(PdfName.Height).getFloatValue();
        else
            return height;
    }

    @Override
    public void flush() throws PdfException {
        width = getPdfObject().getAsNumber(PdfName.Width).getFloatValue();
        height = getPdfObject().getAsNumber(PdfName.Height).getFloatValue();
        super.flush();
    }

    @Override
    public PdfImageXObject copy(PdfDocument document) throws PdfException {
        PdfImageXObject image = new PdfImageXObject((PdfStream) getPdfObject().copy(document), document);
        image.width = width;
        image.height = height;
        image.mask = mask;
        image.softMask = softMask;
        return image;
    }

    static protected PdfStream createPdfStream(PdfDocument document, Image image, PdfImageXObject imageMask) throws PdfException {
        PdfStream stream = new PdfStream(document);
        stream.put(PdfName.Type, PdfName.XObject);
        stream.put(PdfName.Subtype, PdfName.Image);
        stream.put(PdfName.Width, new PdfNumber(image.getWidth()));
        stream.put(PdfName.Height, new PdfNumber(image.getHeight()));

//TODO: return to this later
//        if (image.getLayer() != null)
//            put(PdfName.OC, image.getLayer().getRef());

        if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 0xff))
            stream.put(PdfName.ImageMask, PdfBoolean.PdfTrue);

        if (imageMask != null) {
            if (imageMask.softMask)
                stream.put(PdfName.SMask, imageMask.getPdfObject());
            else if (imageMask.mask)
                stream.put(PdfName.Mask, imageMask.getPdfObject());
        }

        if (image.isMask() && image.isInverted())
            stream.put(PdfName.Decode, new PdfArray(new float[] {1, 0}));
        if (image.isInterpolation())
            stream.put(PdfName.Interpolate, PdfBoolean.PdfTrue);


        InputStream is = null;
        try {
            // deal with transparency
            int transparency[] = image.getTransparency();
            if (transparency != null && !image.isMask() && imageMask == null) {
                PdfArray t = new PdfArray();
                for (int transparencyItem : transparency)
                    t.add(new PdfNumber(transparencyItem));
                stream.put(PdfName.Mask, t);
            }
            // Raw Image data
            if (image.getType() == Image.RAW) {
                // will also have the CCITT parameters
                int colorspace = image.getColorSpace();
                byte[] imgBytes = image.getRawData();
                stream.getOutputStream().assignBytes(imgBytes, imgBytes.length);
                int bpc = image.getBpc();
                if (bpc > 0xff) {
                    if (!image.isMask())
                        stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
                    stream.put(PdfName.BitsPerComponent, new PdfNumber(1));
                    stream.put(PdfName.Filter, PdfName.CCITTFaxDecode);
                    int k = bpc - CcittImage.CCITTG3_1D;
                    PdfDictionary decodeparms = new PdfDictionary();
                    if (k != 0)
                        decodeparms.put(PdfName.K, new PdfNumber(k));
                    if ((colorspace & CcittImage.CCITT_BLACKIS1) != 0)
                        decodeparms.put(PdfName.BlackIs1, PdfBoolean.PdfTrue);
                    if ((colorspace & CcittImage.CCITT_ENCODEDBYTEALIGN) != 0)
                        decodeparms.put(PdfName.EncodedByteAlign, PdfBoolean.PdfTrue);
                    if ((colorspace & CcittImage.CCITT_ENDOFLINE) != 0)
                        decodeparms.put(PdfName.EndOfLine, PdfBoolean.PdfTrue);
                    if ((colorspace & CcittImage.CCITT_ENDOFBLOCK) != 0)
                        decodeparms.put(PdfName.EndOfBlock, PdfBoolean.PdfFalse);
                    decodeparms.put(PdfName.Columns, new PdfNumber(image.getWidth()));
                    decodeparms.put(PdfName.Rows, new PdfNumber(image.getHeight()));
                    stream.put(PdfName.DecodeParms, decodeparms);
                } else {
                    switch (colorspace) {
                        case 1:
                            stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
                            if (image.isInverted())
                                stream.put(PdfName.Decode, new PdfArray(new float[] {1, 0}));
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
                    Image.IAdditional additional = image.getAdditional();
                    if (additional instanceof Image.Indexed) {
                        PdfArray indexed = new PdfArray();
                        indexed.add(PdfName.Indexed);
                        indexed.add(PdfName.DeviceRGB);
                        indexed.add(new PdfNumber(((Image.Indexed) additional).getColor()));
                        indexed.add(new PdfString(((Image.Indexed) additional).getPalette()));
                        stream.put(PdfName.ColorSpace, indexed);
                    }
                    if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8))
                        stream.remove(PdfName.ColorSpace);
                    stream.put(PdfName.BitsPerComponent, new PdfNumber(image.getBpc()));
                    if (image.isDeflated()) {
                        stream.put(PdfName.Filter, PdfName.FlateDecode);
                    }
                }
                return stream;
            }
            // GIF, JPEG or PNG
            String errorID;
            if (image.getRawData() == null) {
                is = image.getUrl().openStream();
                errorID = image.getUrl().toString();
            } else {
                is = new java.io.ByteArrayInputStream(image.getRawData());
                errorID = "Byte array";
            }
            switch (image.getType()) {
                case Image.JPEG:
                    stream.put(PdfName.Filter, PdfName.DCTDecode);
                    if (image.getColorTransform() == 0) {
                        PdfDictionary decodeparms = new PdfDictionary();
                        decodeparms.put(PdfName.ColorTransform, new PdfNumber(0));
                        stream.put(PdfName.DecodeParms, decodeparms);
                    }
                    switch (image.getColorSpace()) {
                        case 1:
                            stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
                            break;
                        case 3:
                            stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
                            break;
                        default:
                            stream.put(PdfName.ColorSpace, PdfName.DeviceCMYK);
                            if (image.isInverted()) {
                                stream.put(PdfName.Decode, new PdfArray(new float[]{1, 0, 1, 0, 1, 0, 1, 0}));
                            }
                    }
                    stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
                    if (image.getRawData() != null) {
                        byte[] imgBytes = image.getRawData();
                        stream.getOutputStream().assignBytes(imgBytes, imgBytes.length);
                        return stream;
                    }
                    transferBytes(is, stream.getOutputStream());
                    break;
                case Image.JPEG2000:
                    stream.put(PdfName.Filter, PdfName.JPXDecode);
                    if (image.getColorSpace() > 0) {
                        switch (image.getColorSpace()) {
                            case 1:
                                stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
                                break;
                            case 3:
                                stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
                                break;
                            default:
                                stream.put(PdfName.ColorSpace, PdfName.DeviceCMYK);
                        }
                        stream.put(PdfName.BitsPerComponent, new PdfNumber(image.getBpc()));
                    }
                    if (image.getRawData() != null) {
                        byte[] imgBytes = image.getRawData();
                        stream.getOutputStream().assignBytes(imgBytes, imgBytes.length);
                        return stream;
                    }
                    transferBytes(is, stream.getOutputStream());
                    break;
                case Image.JBIG2:
                    stream.put(PdfName.Filter, PdfName.JBIG2Decode);
                    stream.put(PdfName.ColorSpace, PdfName.DeviceGray);
                    stream.put(PdfName.BitsPerComponent, new PdfNumber(1));
                    if (image.getRawData() != null) {
                        byte[] imgBytes = image.getRawData();
                        stream.getOutputStream().assignBytes(imgBytes, imgBytes.length);
                        return stream;
                    }
                    transferBytes(is, stream.getOutputStream());
                    break;
                default:
                    throw new PdfException(PdfException._1IsAnUnknownImageFormat).setMessageParams(errorID);
            }
        } catch (IOException ioe) {
            throw new PdfException(ioe.getMessage(), ioe);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ee) {
                    // empty on purpose
                }
            }
        }

        return stream;
    }

    private static void transferBytes(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[transferSize];
        for (; ; ) {
            int len = in.read(buffer, 0, transferSize);
            if (len > 0)
                out.write(buffer, 0, len);
            else
                break;
        }
    }


}
