/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.codec.PngWriter;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TiffWriter;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.image.RawImageData;
import com.itextpdf.io.image.RawImageHelper;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.filters.DoNothingFilter;
import com.itextpdf.kernel.pdf.filters.FilterHandlers;
import com.itextpdf.kernel.pdf.filters.IFilterHandler;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for Image XObject. ISO 32000-1, 8.9 Images.
 */
public class PdfImageXObject extends PdfXObject {

    private static final long serialVersionUID = -205889576153966580L;

    private float width;
    private float height;
    private boolean mask;
    private boolean softMask;
    private int pngColorType = -1;
    private int pngBitDepth;
    private int bpc;
    private byte[] palette;
    private byte[] icc;
    private int stride;

    /**
     * Creates Image XObject by image.
     *
     * @param image {@link ImageData} with actual image data.
     */
    public PdfImageXObject(ImageData image) {
        this(image, null);
    }

    /**
     * Creates Image XObject by image.
     *
     * @param image     {@link ImageData} with actual image data.
     * @param imageMask {@link PdfImageXObject} with image mask.
     */
    public PdfImageXObject(ImageData image, PdfImageXObject imageMask) {
        this(createPdfStream(checkImageType(image), imageMask));
        mask = image.isMask();
        softMask = image.isSoftMask();
    }

    /**
     * Create {@link PdfImageXObject} instance by {@link PdfStream}.
     * Note, this constructor doesn't perform any additional checks
     *
     * @param pdfStream {@link PdfStream} with Image XObject.
     * @see PdfXObject#makeXObject(PdfStream)
     */
    public PdfImageXObject(PdfStream pdfStream) {
        super(pdfStream);
    }

    /**
     * Gets width of image, {@code Width} key.
     *
     * @return float value.
     */
    @Override
    public float getWidth() {
        if (!isFlushed()) {
            return getPdfObject().getAsNumber(PdfName.Width).floatValue();
        } else {
            return width;
        }
    }

    /**
     * Gets height of image, {@code Height} key.
     *
     * @return float value.
     */
    @Override
    public float getHeight() {
        if (!isFlushed()) {
            return getPdfObject().getAsNumber(PdfName.Height).floatValue();
        } else {
            return height;
        }
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note, that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        if (!isFlushed()) {
            width = getPdfObject().getAsNumber(PdfName.Width).floatValue();
            height = getPdfObject().getAsNumber(PdfName.Height).floatValue();
            super.flush();
        }
    }

    /**
     * Copy Image XObject to the specified document.
     *
     * @param document target document
     * @return just created instance of {@link PdfImageXObject}.
     */
    public PdfImageXObject copyTo(PdfDocument document) {
        PdfImageXObject image = new PdfImageXObject((PdfStream) getPdfObject().copyTo(document));
        image.width = width;
        image.height = height;
        image.mask = mask;
        image.softMask = softMask;
        return image;
    }

    /**
     * Gets image bytes, wrapped with buffered image.
     *
     * @return {@link java.awt.image.BufferedImage} image.
     * @throws IOException if an error occurs during reading.
     */
    public java.awt.image.BufferedImage getBufferedImage() throws IOException {
        byte[] img = getImageBytes();
        return ImageIO.read(new ByteArrayInputStream(img));
    }

    /**
     * Gets decoded image bytes.
     *
     * @return byte array.
     */
    public byte[] getImageBytes() {
        return getImageBytes(true);
    }

    /**
     * Gets image bytes.
     * Note, {@link PdfName#DCTDecode}, {@link PdfName#JBIG2Decode} and {@link PdfName#JPXDecode}
     * filters will be ignored.
     *
     * @param decoded if {@code true}, decodes stream bytes.
     * @return byte array.
     */
    public byte[] getImageBytes(boolean decoded) {
        byte[] bytes;
        bytes = getPdfObject().getBytes(false);
        if (decoded) {
            Map<PdfName, IFilterHandler> filters = new HashMap<>(FilterHandlers.getDefaultFilterHandlers());
            DoNothingFilter stubFilter = new DoNothingFilter();
            filters.put(PdfName.DCTDecode, stubFilter);
            filters.put(PdfName.JBIG2Decode, stubFilter);
            filters.put(PdfName.JPXDecode, stubFilter);
            bytes = PdfReader.decodeBytes(bytes, getPdfObject(), filters);

            if (stubFilter.getLastFilterName() == null) {
                try {
                    bytes = decodeTiffAndPngBytes(bytes);
                } catch (IOException e) {
                    throw new RuntimeException("IO exception in PdfImageXObject", e);
                }
            }
        }
        return bytes;
    }

    /**
     * Identifies the type of the image that is stored in the bytes of this {@link PdfImageXObject}.
     * Note that this has nothing to do with the original type of the image. For instance, the return value
     * of this method will never be {@link ImageType#PNG} as we loose this information when converting a
     * PNG image into something that can be put into a PDF file.
     * The possible values are: {@link ImageType#JPEG}, {@link ImageType#JPEG2000}, {@link ImageType#JBIG2},
     * {@link ImageType#TIFF}, {@link ImageType#PNG}
     *
     * @return the identified type of image
     */
    public ImageType identifyImageType() {
        PdfObject filter = getPdfObject().get(PdfName.Filter);
        PdfArray filters = new PdfArray();
        if (filter != null) {
            if (filter.getType() == PdfObject.NAME) {
                filters.add(filter);
            } else if (filter.getType() == PdfObject.ARRAY) {
                filters = ((PdfArray) filter);
            }
        }
        for (int i = filters.size() - 1; i >= 0; i--) {
            PdfName filterName = (PdfName) filters.get(i);
            if (PdfName.DCTDecode.equals(filterName)) {
                return ImageType.JPEG;
            } else if (PdfName.JBIG2Decode.equals(filterName)) {
                return ImageType.JBIG2;
            } else if (PdfName.JPXDecode.equals(filterName)) {
                return ImageType.JPEG2000;
            }
        }

        // None of the previous types match
        PdfObject colorspace = getPdfObject().get(PdfName.ColorSpace);
        prepareAndFindColorspace(colorspace);
        if (pngColorType < 0) {
            return ImageType.TIFF;
        } else {
            return ImageType.PNG;
        }
    }

    /**
     * Identifies recommended file extension to store the bytes of this {@link PdfImageXObject}.
     * Possible values are: 'png', 'jpg', 'jp2', 'tif', 'jbig2'.
     * This extension can later be used together with the result of {@link #getImageBytes()}.
     *
     * @return a {@link String} with recommended file extension
     * @see #identifyImageType()
     */
    public String identifyImageFileExtension() {
        ImageType bytesType = identifyImageType();
        switch (bytesType) {
            case PNG:
                return "png";
            case JPEG:
                return "jpg";
            case JPEG2000:
                return "jp2";
            case TIFF:
                return "tif";
            case JBIG2:
                return "jbig2";
            default:
                throw new IllegalStateException("Should have never happened. This type of image is not allowed for ImageXObject");
        }
    }

    /**
     * Puts the value into Image XObject dictionary and associates it with the specified key.
     * If the key is already present, it will override the old value with the specified one.
     *
     * @param key   key to insert or to override
     * @param value the value to associate with the specified key
     * @return object itself.
     */
    public PdfImageXObject put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    private static PdfStream createPdfStream(ImageData image, PdfImageXObject imageMask) {
        PdfStream stream;
        if (image.getOriginalType() == ImageType.RAW) {
            RawImageHelper.updateImageAttributes((RawImageData) image, null);
        }
        stream = new PdfStream(image.getData());
        String filter = image.getFilter();
        if (filter != null && filter.equals("JPXDecode") && image.getColorSpace() <= 0) {
            stream.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
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

        IccProfile iccProfile = image.getProfile();
        if (iccProfile != null) {
            PdfStream iccProfileStream = PdfCieBasedCs.IccBased.getIccProfileStream(iccProfile);
            PdfArray iccBasedColorSpace = new PdfArray();
            iccBasedColorSpace.add(PdfName.ICCBased);
            iccBasedColorSpace.add(iccProfileStream);
            PdfObject colorSpaceObject = stream.get(PdfName.ColorSpace);
            boolean iccProfileShouldBeApplied = true;
            if (colorSpaceObject != null) {
                PdfColorSpace cs = PdfColorSpace.makeColorSpace(colorSpaceObject);
                if (cs == null) {
                    LoggerFactory.getLogger(PdfImageXObject.class).error(LogMessageConstant.IMAGE_HAS_INCORRECT_OR_UNSUPPORTED_COLOR_SPACE_OVERRIDDEN_BY_ICC_PROFILE);
                } else if (cs instanceof PdfSpecialCs.Indexed) {
                    PdfColorSpace baseCs = ((PdfSpecialCs.Indexed) cs).getBaseCs();
                    if (baseCs == null) {
                        LoggerFactory.getLogger(PdfImageXObject.class).error(LogMessageConstant.IMAGE_HAS_INCORRECT_OR_UNSUPPORTED_BASE_COLOR_SPACE_IN_INDEXED_COLOR_SPACE_OVERRIDDEN_BY_ICC_PROFILE);
                    } else if (baseCs.getNumberOfComponents() != iccProfile.getNumComponents()) {
                        LoggerFactory.getLogger(PdfImageXObject.class).error(LogMessageConstant.IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS_COMPARED_TO_BASE_COLOR_SPACE_IN_INDEXED_COLOR_SPACE);
                        iccProfileShouldBeApplied = false;
                    } else {
                        iccProfileStream.put(PdfName.Alternate, baseCs.getPdfObject());
                    }
                    if (iccProfileShouldBeApplied) {
                        ((PdfArray) colorSpaceObject).set(1, iccBasedColorSpace);
                        iccProfileShouldBeApplied = false;
                    }
                } else if (cs.getNumberOfComponents() != iccProfile.getNumComponents()) {
                    LoggerFactory.getLogger(PdfImageXObject.class).error(LogMessageConstant.IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS_COMPARED_TO_COLOR_SPACE);
                    iccProfileShouldBeApplied = false;
                } else {
                    iccProfileStream.put(PdfName.Alternate, colorSpaceObject);
                }
            }
            if (iccProfileShouldBeApplied) {
                stream.put(PdfName.ColorSpace, iccBasedColorSpace);
            }
        }

        if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 0xff))
            stream.put(PdfName.ImageMask, PdfBoolean.TRUE);

        if (imageMask != null) {
            if (imageMask.softMask)
                stream.put(PdfName.SMask, imageMask.getPdfObject());
            else if (imageMask.mask)
                stream.put(PdfName.Mask, imageMask.getPdfObject());
        }

        ImageData mask = image.getImageMask();
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
            stream.put(PdfName.Interpolate, PdfBoolean.TRUE);
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
                    dictionary.put(new PdfName(key), new PdfNumber((int) value));
                } else if (value instanceof Float) {
                    dictionary.put(new PdfName(key), new PdfNumber((float) value));
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
                    //TODO Check inline images
                    PdfStream globalsStream = new PdfStream();
                    globalsStream.getOutputStream().writeBytes((byte[]) value);
                    dictionary.put(PdfName.JBIG2Globals, globalsStream);
                } else if (value instanceof Boolean) {
                    dictionary.put(new PdfName(key), PdfBoolean.valueOf((boolean) value));
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
        for (Object obj : objects) {
            if (obj instanceof String) {
                String str = (String) obj;
                if (str.indexOf('/') == 0) {
                    array.add(new PdfName(str.substring(1)));
                } else {
                    array.add(new PdfString(str));
                }
            } else if (obj instanceof Integer) {
                array.add(new PdfNumber((int) obj));
            } else if (obj instanceof Float) {
                array.add(new PdfNumber((float) obj));
            } else if (obj instanceof Object[]) {
                array.add(createArray(stream, (Object[]) obj));
            } else {
                //TODO instance of was removed due to autoport
                array.add(createDictionaryFromMap(stream, (Map<String, Object>) obj));
            }
        }
        return array;
    }

    private void prepareAndFindColorspace(PdfObject colorspace) {
        pngColorType = -1;
        width = getPdfObject().getAsNumber(PdfName.Width).intValue();
        height = getPdfObject().getAsNumber(PdfName.Height).intValue();
        bpc = getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue();
        pngBitDepth = bpc;

        palette = null;
        icc = null;
        stride = 0;
        findColorspace(colorspace, true);
    }

    private byte[] decodeTiffAndPngBytes(byte[] imageBytes) throws IOException {
        PdfObject colorspace = getPdfObject().get(PdfName.ColorSpace);
        prepareAndFindColorspace(colorspace);
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
            stride = (int) (4 * width);
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
            PdfArray decode = getPdfObject().getAsArray(PdfName.Decode);
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
            png.writeHeader((int) width, (int) height, pngBitDepth, pngColorType);
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
     * @param colorspace   the colorspace to use
     * @param allowIndexed whether indexed color spaces will be resolved (used for recursive call)
     * @throws IOException if there is a problem with reading from the underlying stream
     */
    private void findColorspace(PdfObject colorspace, boolean allowIndexed) {
        if (colorspace == null && bpc == 1) { // handle imagemasks
            stride = (int) ((width * bpc + 7) / 8);
            pngColorType = 0;
        } else if (PdfName.DeviceGray.equals(colorspace)) {
            stride = (int) ((width * bpc + 7) / 8);
            pngColorType = 0;
        } else if (PdfName.DeviceRGB.equals(colorspace)) {
            if (bpc == 8 || bpc == 16) {
                stride = (int) ((width * bpc * 3 + 7) / 8);
                pngColorType = 2;
            }
        } else if (colorspace instanceof PdfArray) {
            PdfArray ca = (PdfArray) colorspace;
            PdfObject tyca = ca.get(0);
            if (PdfName.CalGray.equals(tyca)) {
                stride = (int) ((width * bpc + 7) / 8);
                pngColorType = 0;
            } else if (PdfName.CalRGB.equals(tyca)) {
                if (bpc == 8 || bpc == 16) {
                    stride = (int) ((width * bpc * 3 + 7) / 8);
                    pngColorType = 2;
                }
            } else if (PdfName.ICCBased.equals(tyca)) {
                PdfStream pr = (PdfStream) ca.get(1);
                int n = pr.getAsNumber(PdfName.N).intValue();
                if (n == 1) {
                    stride = (int) ((width * bpc + 7) / 8);
                    pngColorType = 0;
                    icc = pr.getBytes();
                } else if (n == 3) {
                    stride = (int) ((width * bpc * 3 + 7) / 8);
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
                        palette = (((PdfStream) id2)).getBytes();
                    }
                    stride = (int) ((width * bpc + 7) / 8);
                    pngColorType = 3;
                }
            }
        }
    }

    private static ImageData checkImageType(ImageData image) {
        if (image instanceof WmfImageData) {
            throw new PdfException(PdfException.CannotCreatePdfImageXObjectByWmfImage);
        }
        return image;
    }
}
