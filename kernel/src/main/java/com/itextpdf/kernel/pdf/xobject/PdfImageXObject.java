package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.image.Image;
import com.itextpdf.io.image.RawImage;
import com.itextpdf.io.image.RawImageHelper;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filters.DoNothingFilter;
import com.itextpdf.kernel.pdf.filters.FilterHandler;
import com.itextpdf.kernel.pdf.filters.FilterHandlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class PdfImageXObject extends PdfXObject {

    private float width;
    private float height;
    private boolean mask;
    private boolean softMask;

    public PdfImageXObject(Image image) {
        this(image, null);
    }

    public PdfImageXObject(Image image, PdfImageXObject imageMask) {
        this(createPdfStream(image, imageMask));
        mask = image.isMask();
        softMask = image.isSoftMask();
    }

    public PdfImageXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    @Override
    public Float getWidth() {
        if (!isFlushed())
            return getPdfObject().getAsNumber(PdfName.Width).getFloatValue();
        else
            return width;
    }

    @Override
    public Float getHeight() {
        if (!isFlushed())
            return getPdfObject().getAsNumber(PdfName.Height).getFloatValue();
        else
            return height;
    }

    @Override
    public void flush() {
        width = getPdfObject().getAsNumber(PdfName.Width).getFloatValue();
        height = getPdfObject().getAsNumber(PdfName.Height).getFloatValue();
        super.flush();
    }

    // TODO probably remove it. or may be not. images copying sounds not so bad.
    public PdfImageXObject copyTo(PdfDocument document) {
        PdfImageXObject image = new PdfImageXObject(getPdfObject().copyTo(document));
        image.width = width;
        image.height = height;
        image.mask = mask;
        image.softMask = softMask;
        return image;
    }

    public java.awt.image.BufferedImage getBufferedImage() throws IOException {
        byte[] img = getImageBytes();
        return ImageIO.read(new ByteArrayInputStream(img));
    }

    public byte[] getImageBytes() {
        return getImageBytes(true);
    }

    public byte[] getImageBytes(boolean decoded) {
        byte[] bytes;
        bytes = getPdfObject().getBytes(false);
        if (decoded) {
            Map<PdfName, FilterHandler> filters = new HashMap<>(FilterHandlers.getDefaultFilterHandlers());
            DoNothingFilter stubfilter = new DoNothingFilter();
            filters.put(PdfName.DCTDecode, stubfilter);
            filters.put(PdfName.JBIG2Decode, stubfilter);
            filters.put(PdfName.JPXDecode, stubfilter);
            bytes = PdfReader.decodeBytes(bytes, getPdfObject(), filters);
        }
        return bytes;
    }

    protected static PdfStream createPdfStream(Image image, PdfImageXObject imageMask) {
        PdfStream stream;
        if (image.getOriginalType() == Image.RAW) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RawImageHelper.updateImageAttributes((RawImage) image, null, baos);
            stream = new PdfStream(baos.toByteArray());
        } else {
            stream = new PdfStream(image.getData());
        }
        String filter = image.getFilter();
        if (filter != null && filter.equals("JPXDecode") && image.getColorSpace() <= 0) {
            stream.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
            image.setBpc(0);
        }
        stream.put(PdfName.Type, PdfName.XObject);
        stream.put(PdfName.Subtype, PdfName.Image);
        PdfDictionary decodeParms = createDictionaryFromMap(stream, image.getDecodeParms());
        if (decodeParms != null) {
            stream.put(PdfName.DecodeParms, decodeParms);
        }


        PdfName colorSpace;
        switch (image.getColorSpace()) {
            case 1:
                colorSpace = PdfName.DeviceGray;
                break;
            case 3:
                colorSpace = PdfName.DeviceRGB;
                break;
            default:
                colorSpace = PdfName.DeviceCMYK;
        }
        stream.put(PdfName.ColorSpace, colorSpace);

        if (image.getBpc() != 0) {
            stream.put(PdfName.BitsPerComponent, new PdfNumber(image.getBpc()));
        }

        if (image.getFilter() != null) {
            stream.put(PdfName.Filter, new PdfName(image.getFilter()));
        }
//TODO: return to this later
//        if (image.getLayer() != null)
//            put(PdfName.OC, image.getLayer().getRef());


        if (image.getColorSpace() == -1) {
            stream.remove(PdfName.ColorSpace);
        }

        PdfDictionary additional = createDictionaryFromMap(stream, image.getImageAttributes());
        if (additional != null) {
            stream.putAll(additional);
        }


        if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 0xff))
            stream.put(PdfName.ImageMask, PdfBoolean.PdfTrue);

        if (imageMask != null) {
            if (imageMask.softMask)
                stream.put(PdfName.SMask, imageMask.getPdfObject());
            else if (imageMask.mask)
                stream.put(PdfName.Mask, imageMask.getPdfObject());
        }

        Image mask = image.getImageMask();
        if (mask != null) {
            if (mask.isSoftMask())
                stream.put(PdfName.SMask, new PdfImageXObject(image.getImageMask()).getPdfObject());
            else if (mask.isMask())
                stream.put(PdfName.Mask, new PdfImageXObject(image.getImageMask()).getPdfObject());
        }

        if (image.getDecode() != null) {
            stream.put(PdfName.Decode, new PdfArray(image.getDecode()));
        }
        if (image.isMask() && image.isInverted())
            stream.put(PdfName.Decode, new PdfArray(new float[]{1, 0}));
        if (image.isInterpolation())
            stream.put(PdfName.Interpolate, PdfBoolean.PdfTrue);
        // deal with transparency
        int[] transparency = image.getTransparency();
        if (transparency != null && !image.isMask() && imageMask == null) {
            PdfArray t = new PdfArray();
            for (int transparencyItem : transparency)
                t.add(new PdfNumber(transparencyItem));
            stream.put(PdfName.Mask, t);
        }

        stream.put(PdfName.Width, new PdfNumber(image.getWidth()));
        stream.put(PdfName.Height, new PdfNumber(image.getHeight()));
        return stream;
    }

    private static PdfDictionary createDictionaryFromMap(PdfStream stream, Map<String, Object> parms) {
        if (parms != null) {
            PdfDictionary dictionary = new PdfDictionary();
            for (Map.Entry<String, Object> entry : parms.entrySet()) {
                Object value = entry.getValue();
                String key = entry.getKey();
                if (value instanceof Integer) {
                    dictionary.put(new PdfName(key), new PdfNumber((Integer) value));
                } else if (value instanceof Float) {
                    dictionary.put(new PdfName(key), new PdfNumber((Float) value));
                } else if (value instanceof String) {
                    if (value.equals("Mask")) {
                        dictionary.put(PdfName.Mask, new PdfLiteral((String) value));
                    } else {
                        String str = (String) value;
                        if (str.indexOf('/') == 0) {
                            dictionary.put(new PdfName(key), new PdfName(str.substring(1)));
                        } else {
                            dictionary.put(new PdfName(key), new PdfString(str));
                        }
                    }
                } else if (value instanceof byte[]) {
                    //@TODO Check inline images
                    PdfStream globalsStream = new PdfStream();
                    globalsStream.getOutputStream().writeBytes((byte[]) value);
                    dictionary.put(PdfName.JBIG2Globals, globalsStream);
                } else if (value instanceof Boolean) {
                    dictionary.put(new PdfName(key), new PdfBoolean((Boolean) value));
                } else if (value instanceof Object[]) {
                    dictionary.put(new PdfName(key), createArray(stream, (Object[]) value));
                } else if (value instanceof float[]) {
                    dictionary.put(new PdfName(key), new PdfArray((float[]) value));
                }
            }
            return dictionary;
        }
        return null;
    }

    private static PdfArray createArray(PdfStream stream, Object[] objects) {
        PdfArray array = new PdfArray();
        for(Object object : objects) {
            if (object instanceof String) {
                String str = (String) object;
                if (str.indexOf('/') == 0) {
                    array.add(new PdfName(str.substring(1)));
                } else {
                    array.add(new PdfString(str));
                }
            } else if (object instanceof Integer) {
                array.add(new PdfNumber((Integer) object));
            } else if (object instanceof Float) {
                array.add(new PdfNumber((Double) object));
            } else if (object instanceof Map) {
                array.add(createDictionaryFromMap(stream, (Map<String, Object>) object));
            }
        }
        return array;
    }
}
