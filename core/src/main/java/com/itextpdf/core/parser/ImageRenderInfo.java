package com.itextpdf.core.parser;

import com.itextpdf.core.color.Color;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.io.IOException;

/**
 * Represents image data from a PDF
 * @since 5.0.1
 */
public class ImageRenderInfo implements EventData {
    /** The coordinate transformation matrix that was in effect when the image was rendered */
    private final Matrix ctm;
    /** A reference to the image XObject */
    private final PdfStream stream;
    /** A reference to an inline image */
    private final InlineImageInfo inlineImageInfo;
    /** the color space associated with the image */
    private final PdfDictionary colorSpaceDictionary;
    /** the image object to be rendered, if it has been parsed already.  Null otherwise. */
    private PdfImageObject imageObject = null;

    private ImageRenderInfo(Matrix ctm, PdfStream stream, PdfDictionary colorSpaceDictionary) {
        this.ctm = ctm;
        this.stream = stream;
        this.inlineImageInfo = null;
        this.colorSpaceDictionary = colorSpaceDictionary;
    }

    private ImageRenderInfo(Matrix ctm, InlineImageInfo inlineImageInfo, PdfDictionary colorSpaceDictionary) {
        this.ctm = ctm;
        this.stream = null;
        this.inlineImageInfo = inlineImageInfo;
        this.colorSpaceDictionary = colorSpaceDictionary;
    }

    /**
     * Create an ImageRenderInfo object based on an XObject (this is the most common way of including an image in PDF)
     * @param ctm the coordinate transformation matrix at the time the image is rendered
     * @param ref a reference to the image XObject
     * @return the ImageRenderInfo representing the rendered XObject
     * @since 5.0.1
     */
    public static ImageRenderInfo createForXObject(Matrix ctm, PdfStream stream, PdfDictionary colorSpaceDictionary){
        return new ImageRenderInfo(ctm, stream, colorSpaceDictionary);
    }

    /**
     * Create an ImageRenderInfo object based on inline image data.  This is nowhere near completely thought through
     * and really just acts as a placeholder.
     * @param ctm the coordinate transformation matrix at the time the image is rendered
     * @param imageObject the image object representing the inline image
     * @return the ImageRenderInfo representing the rendered embedded image
     * @since 5.0.1
     */
    protected static ImageRenderInfo createForEmbeddedImage(Matrix ctm, InlineImageInfo inlineImageInfo, PdfDictionary colorSpaceDictionary){
        ImageRenderInfo renderInfo = new ImageRenderInfo(ctm, inlineImageInfo, colorSpaceDictionary);
        return renderInfo;
    }

    /**
     * Gets an object containing the image dictionary and bytes.
     * @return an object containing the image dictionary and byte[]
     * @since 5.0.2
     */
    public PdfImageObject getImage() throws IOException {
        prepareImageObject();
        return imageObject;
    }

    private void prepareImageObject() throws IOException{
        if (imageObject != null)
            return;

        if (stream != null){
            imageObject = new PdfImageObject(stream, colorSpaceDictionary);
        } else if (inlineImageInfo != null){
            imageObject = new PdfImageObject(inlineImageInfo.getImageDictionary(), inlineImageInfo.getSamples(), colorSpaceDictionary);
        }
    }

    /**
     * @return a vector in User space representing the start point of the xobject
     */
    public Vector getStartPoint(){
        return new Vector(0, 0, 1).cross(ctm);
    }

    /**
     * @return The coordinate transformation matrix active when this image was rendered.  Coordinates are in User space.
     * @since 5.0.3
     */
    public Matrix getImageCTM(){
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
     * @return an indirect reference to the image // TODO: correct the doc
     * @since 5.0.2
     */
    public PdfStream getStream() {
        return stream;
    }
}
