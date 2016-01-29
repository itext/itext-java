package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

/**
 * Represents image data from a PDF
 * @since 5.0.1
 */
public class ImageRenderInfo implements EventData {
    /** The coordinate transformation matrix that was in effect when the image was rendered */
    private Matrix ctm;
    private PdfImageXObject image;
    /** the color space dictionary from resources which are associated with the image */
    private PdfDictionary colorSpaceDictionary;
    /** defines if the encountered image was inline */
    private boolean isInline;

    /**
     * Create an ImageRenderInfo
     * @param ctm the coordinate transformation matrix at the time the image is rendered
     * @param stream image stream object
     * @param colorSpaceDictionary the color space dictionary from resources which are associated with the image
     * @param isInline defines if the encountered image was inline
     */
    public ImageRenderInfo(Matrix ctm, PdfStream stream, PdfDictionary colorSpaceDictionary, boolean isInline) {
        this.ctm = ctm;
        this.image = new PdfImageXObject(stream);
        this.colorSpaceDictionary = colorSpaceDictionary;
        this.isInline = isInline;
    }

    /**
     * Gets an image wrapped in ImageXObject.
     * You can:
     * <ul>
     *     <li>get image bytes with {@link PdfImageXObject#getImageBytes(boolean)};</li>
     *     <li>obtain PdfStream object which contains image dictionary with {@link PdfImageXObject#getPdfObject()} method;</li>
     *     <li>convert image to {@link java.awt.image.BufferedImage} with {@link PdfImageXObject#getBufferedImage()};</li>
     *     //TODO: correct this when something like convertToNativeImage method is implemented
     *     <li>convert this image to native image with PdfImageXObject#convertToNativeImage;</li>
     * </ul>
     */
    public PdfImageXObject getImage() {
        return image;
    }

    /**
     * @return a vector in User space representing the start point of the image
     */
    public Vector getStartPoint(){
        return new Vector(0, 0, 1).cross(ctm);
    }

    /**
     * @return The coordinate transformation matrix which was active when this image was rendered. Coordinates are in User space.
     * @since 5.0.3
     */
    public Matrix getImageCtm(){
        return ctm;
    }

    /**
     * @return the size of the image, in User space units
     * @since 5.0.3
     */
    public float getArea(){
        // the image space area is 1, so we multiply that by the determinant of the CTM to get the transformed area
        return ctm.getDeterminant();
    }

    /**
     * @return true if image was inlined in original stream.
     */
    public boolean isInline() {
        return isInline;
    }

    /**
     * @return the color space dictionary from resources which are associated with the image
     */
    public PdfDictionary getColorSpaceDictionary() {
        return colorSpaceDictionary;
    }
}
