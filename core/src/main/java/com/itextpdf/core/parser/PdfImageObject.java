package com.itextpdf.core.parser;

import com.itextpdf.basics.codec.TIFFConstants;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.Version;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.filters.FilterHandler;
import com.itextpdf.core.pdf.filters.FilterHandlers;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An object that contains an image dictionary and image bytes.
 * @since 5.0.2
 */
public class PdfImageObject {

    /**
     * Different types of data that can be stored in the bytes of a {@link PdfImageObject}
     * @since 5.0.4
     */
    public static enum ImageBytesType{
        PNG("png"), // the stream contains png encoded data
        JPG("jpg"), // the stream contains jpg encoded data
        JP2("jp2"), // the stream contains jp2 encoded data
        CCITT("tif"), // the stream contains ccitt encoded data
        JBIG2("jbig2") // the stream contains JBIG2 encoded data
        ;

        /**
         * the recommended file extension for streams of this type
         */
        private final String fileExtension;

        /**
         * @param fileExtension the recommended file extension for use with data of this type (for example, if the bytes were just saved to a file, what extension should the file have)
         */
        private ImageBytesType(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        /**
         * @return the file extension registered when this type was created
         */
        public String getFileExtension() {
            return fileExtension;
        }
    }

    /**
     * A filter that does nothing, but keeps track of the filter type that was used
     * @since 5.0.4
     */
    private static class TrackingFilter implements FilterHandler {
        public PdfName lastFilterName = null;

        public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
            lastFilterName = filterName;
            return b;
        }

    }

    /** The image dictionary. */
    private PdfDictionary dictionary;
    /** The decoded image bytes (after applying filters), or the raw image bytes if unable to decode */
    private byte[] imageBytes;
    private PdfDictionary colorSpaceDic;

    private int pngColorType = -1;
    private int pngBitDepth;
    private int width;
    private int height;
    private int bpc;
    private byte[] palette;
    private byte[] icc;
    private int stride;

    /**
     * Tracks the type of data that is actually stored in the streamBytes member
     */
    private ImageBytesType streamContentType = null;

    public String getFileType() {
        return streamContentType.getFileExtension();
    }

    /**
     * @return the type of image data that is returned by getImageBytes()
     */
    public ImageBytesType getImageBytesType(){
        return streamContentType;
    }

    /**
     * Creates a PdfImage object.
     * @param stream a PRStream
     * @throws IOException
     */
    public PdfImageObject(PdfStream stream) throws IOException {
        this(stream, stream.getBytes(false), null);
    }

    /**
     * Creates a PdfImage object.
     * @param stream a PRStream
     * @param colorSpaceDic	a color space dictionary
     * @throws IOException
     */
    public PdfImageObject(PdfStream stream, PdfDictionary colorSpaceDic) throws IOException {
        this(stream, stream.getBytes(false), colorSpaceDic);
    }



    /**
     * Creats a PdfImage object using an explicitly provided dictionary and image bytes
     * @param dictionary the dictionary for the image
     * @param samples the samples
     * @param colorSpaceDic	a color space dictionary
     * @since 5.0.3
     */
    protected PdfImageObject(PdfDictionary dictionary, byte[] samples, PdfDictionary colorSpaceDic) throws IOException {
        this.dictionary = dictionary;
        this.colorSpaceDic = colorSpaceDic;
        TrackingFilter trackingFilter = new TrackingFilter();
        Map<PdfName, FilterHandler> handlers = new HashMap<PdfName, FilterHandler>(FilterHandlers.getDefaultFilterHandlers());
        handlers.put(PdfName.JBIG2Decode, trackingFilter);
        handlers.put(PdfName.DCTDecode, trackingFilter);
        handlers.put(PdfName.JPXDecode, trackingFilter);

        imageBytes = PdfReader.decodeBytes(samples, dictionary, handlers);

        if (trackingFilter.lastFilterName != null){
            if (PdfName.JBIG2Decode.equals(trackingFilter.lastFilterName))
                streamContentType = ImageBytesType.JBIG2;
            else if (PdfName.DCTDecode.equals(trackingFilter.lastFilterName))
                streamContentType = ImageBytesType.JPG;
            else if (PdfName.JPXDecode.equals(trackingFilter.lastFilterName))
                streamContentType = ImageBytesType.JP2;
        } else {
            decodeImageBytes();
        }
    }

    /**
     * Returns an entry from the image dictionary.
     * @param key a key
     * @return the value
     */
    public PdfObject get(PdfName key) {
        return dictionary.get(key);
    }

    /**
     * Returns the image dictionary.
     * @return the dictionary
     */
    public PdfDictionary getDictionary() {
        return dictionary;
    }

    /**
     * Sets state of this object according to the color space
     * @param colorspace the colorspace to use
     * @param allowIndexed whether indexed color spaces will be resolved (used for recursive call)
     * @throws IOException if there is a problem with reading from the underlying stream
     */
    private void findColorspace(PdfObject colorspace, boolean allowIndexed) throws IOException {
        if (colorspace == null && bpc == 1){ // handle imagemasks
            stride = (width*bpc + 7) / 8;
            pngColorType = 0;
        }
        else if (PdfName.DeviceGray.equals(colorspace)) {
            stride = (width * bpc + 7) / 8;
            pngColorType = 0;
        }
        else if (PdfName.DeviceRGB.equals(colorspace)) {
            if (bpc == 8 || bpc == 16) {
                stride = (width * bpc * 3 + 7) / 8;
                pngColorType = 2;
            }
        }
        else if (colorspace instanceof PdfArray) {
            PdfArray ca = (PdfArray)colorspace;
            PdfObject tyca = ca.get(0);
            if (PdfName.CalGray.equals(tyca)) {
                stride = (width * bpc + 7) / 8;
                pngColorType = 0;
            }
            else if (PdfName.CalRGB.equals(tyca)) {
                if (bpc == 8 || bpc == 16) {
                    stride = (width * bpc * 3 + 7) / 8;
                    pngColorType = 2;
                }
            }
            else if (PdfName.ICCBased.equals(tyca)) {
                PdfStream pr = ca.getAsStream(1);
                int n = pr.getAsNumber(PdfName.N).getIntValue();
                if (n == 1) {
                    stride = (width * bpc + 7) / 8;
                    pngColorType = 0;
                    icc = pr.getBytes();
                }
                else if (n == 3) {
                    stride = (width * bpc * 3 + 7) / 8;
                    pngColorType = 2;
                    icc = pr.getBytes();
                }
            }
            else if (allowIndexed && PdfName.Indexed.equals(tyca)) {
                findColorspace(ca.get(1), false);
                if (pngColorType == 2) {
                    PdfObject id2 = ca.get(3);
                    if (id2 instanceof PdfString) {
                        palette = PdfEncodings.convertToBytes(((PdfString) id2).getValue(), null); // TODO is it correct? (analog of itext5 getBytes)
                    }
                    else if (id2 instanceof PdfStream) {
                        palette = ((PdfStream) id2).getBytes();
                    }
                    stride = (width * bpc + 7) / 8;
                    pngColorType = 3;
                }
            }
        }
    }

    /**
     * decodes the bytes currently captured in the streamBytes and replaces it with an image representation of the bytes
     * (this will either be a png or a tiff, depending on the color depth of the image)
     * @throws IOException
     */
    private void decodeImageBytes() throws IOException{
        if (streamContentType != null)
            throw new IllegalStateException(/*MessageLocalization.getComposedMessage(*/"Decoding.can't.happen.on.this.type.of.stream.(.1.)"/*, streamContentType)*/); // TODO: correct the message

        pngColorType = -1;
        PdfArray decode = dictionary.getAsArray(PdfName.Decode);
        width = dictionary.getAsNumber(PdfName.Width).getIntValue();
        height = dictionary.getAsNumber(PdfName.Height).getIntValue();
        bpc = dictionary.getAsNumber(PdfName.BitsPerComponent).getIntValue();
        pngBitDepth = bpc;
        PdfObject colorspace = dictionary.get(PdfName.ColorSpace);
        if (colorspace instanceof PdfName && colorSpaceDic != null){
            PdfObject csLookup = colorSpaceDic.get((PdfName) colorspace);
            if (csLookup != null)
                colorspace = csLookup;
        }

        palette = null;
        icc = null;
        stride = 0;
        findColorspace(colorspace, true);
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        /*if (pngColorType < 0) {
            if (bpc != 8)
                throw new RuntimeException(*//*MessageLocalization.getComposedMessage(*//*"the.color.depth.1.is.not.supported"*//*, bpc)*//*); // TODO: correct the message

            if (PdfName.DeviceCMYK.equals(colorspace)) {
            }
            else if (colorspace instanceof PdfArray) {
                PdfArray ca = (PdfArray)colorspace;
                PdfObject tyca = ca.get(0);
                if (!PdfName.ICCBased.equals(tyca))
                    throw new RuntimeException(*//*MessageLocalization.getComposedMessage(*//*"the.color.space.1.is.not.supported"*//*, colorspace)*//*); // TODO: correct the message
                PdfStream pr = ca.getAsStream(1);
                int n = pr.getAsNumber(PdfName.N).getIntValue();
                if (n != 4) {
                    throw new RuntimeException(*//*MessageLocalization.getComposedMessage(*//*"N.value.1.is.not.supported"*//*, n)*//*); // TODO: correct the message
                }
                icc = PdfReader.getStreamBytes(pr);
            }
            else
                throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("the.color.space.1.is.not.supported", colorspace));
            stride = 4 * width;
            TiffWriter wr = new TiffWriter();
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_SAMPLESPERPIXEL, 4));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_BITSPERSAMPLE, new int[]{8,8,8,8}));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_PHOTOMETRIC, TIFFConstants.PHOTOMETRIC_SEPARATED));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_IMAGEWIDTH, width));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_IMAGELENGTH, height));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_COMPRESSION, TIFFConstants.COMPRESSION_LZW));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_PREDICTOR, TIFFConstants.PREDICTOR_HORIZONTAL_DIFFERENCING));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_ROWSPERSTRIP, height));
            wr.addField(new TiffWriter.FieldRational(TIFFConstants.TIFFTAG_XRESOLUTION, new int[]{300,1}));
            wr.addField(new TiffWriter.FieldRational(TIFFConstants.TIFFTAG_YRESOLUTION, new int[]{300,1}));
            wr.addField(new TiffWriter.FieldShort(TIFFConstants.TIFFTAG_RESOLUTIONUNIT, TIFFConstants.RESUNIT_INCH));
            wr.addField(new TiffWriter.FieldAscii(TIFFConstants.TIFFTAG_SOFTWARE, Version.getInstance().getVersion()));
            ByteArrayOutputStream comp = new ByteArrayOutputStream();
            TiffWriter.compressLZW(comp, 2, imageBytes, height, 4, stride);
            byte[] buf = comp.toByteArray();
            wr.addField(new TiffWriter.FieldImage(buf));
            wr.addField(new TiffWriter.FieldLong(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS, buf.length));
            if (icc != null)
                wr.addField(new TiffWriter.FieldUndefined(TIFFConstants.TIFFTAG_ICCPROFILE, icc));
            wr.writeFile(ms);
            streamContentType = ImageBytesType.CCITT;
            imageBytes = ms.toByteArray();
            return;
        } else {
            PngWriter png = new PngWriter(ms);
            if (decode != null){
                if (pngBitDepth == 1){
                    // if the decode array is 1,0, then we need to invert the image
                    if(decode.getAsNumber(0).getIntValue() == 1 && decode.getAsNumber(1).getIntValue() == 0){
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
            if (icc != null)
                png.writeIccProfile(icc);
            if (palette != null)
                png.writePalette(palette);
            png.writeData(imageBytes, stride);
            png.writeEnd();
            streamContentType = ImageBytesType.PNG;
            imageBytes = ms.toByteArray();
        }*/
    }

    /**
     * @return the bytes of the image (the format will be as specified in {@link PdfImageObject#getImageBytesType()}
     * @throws IOException
     * @since 5.0.4
     */
    public byte[] getImageAsBytes() {
        return imageBytes;
    }

    // AWT related methods (remove this if you port to Android / GAE)

    /**
     * @since 5.0.3 renamed from getAwtImage()
     */
    public java.awt.image.BufferedImage getBufferedImage() throws IOException {
        byte[] img = getImageAsBytes();
        if (img == null)
            return null;
        return ImageIO.read(new ByteArrayInputStream(img));
    }
}