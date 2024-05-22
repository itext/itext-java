/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Represents image data from a PDF.
 */
public class ImageRenderInfo extends AbstractRenderInfo {
    /**
     * The coordinate transformation matrix that was in effect when the image was rendered.
     */
    private final Matrix ctm;
    private final PdfImageXObject image;
    /**
     * The color space dictionary from resources which are associated with the image.
     */
    private final PdfDictionary colorSpaceDictionary;
    /**
     * Defines if the encountered image was inline.
     */
    private final boolean isInline;
    private final PdfName resourceName;

    /**
     * Hierarchy of nested canvas tags for the text from the most inner (nearest to text) tag to the most outer.
     */
    private final List<CanvasTag> canvasTagHierarchy;

    /**
     * Creates an ImageRenderInfo.
     *
     * @param canvasTagHierarchy   the hierarchy of nested canvas tags for the text from the most
     *                             inner (nearest to text) tag to the most outer
     * @param gs                   the {@link CanvasGraphicsState canvas graphics state}
     * @param ctm                  the coordinate transformation matrix at the time the image is rendered
     * @param imageStream          the image stream object
     * @param resourceName         the {@link PdfName name} of the image resource
     * @param colorSpaceDictionary the color space dictionary from resources which are associated with the image
     * @param isInline             defines if the encountered image was inline
     */
    public ImageRenderInfo(Stack<CanvasTag> canvasTagHierarchy, CanvasGraphicsState gs, Matrix ctm,
            PdfStream imageStream, PdfName resourceName, PdfDictionary colorSpaceDictionary, boolean isInline) {
        super(gs);
        this.canvasTagHierarchy = Collections.<CanvasTag>unmodifiableList(new ArrayList<>(canvasTagHierarchy));
        this.resourceName = resourceName;
        this.ctm = ctm;
        this.image = new PdfImageXObject(imageStream);
        this.colorSpaceDictionary = colorSpaceDictionary;
        this.isInline = isInline;
    }

    /**
     * Gets the image wrapped in ImageXObject.
     * You can:
     * <ul>
     * <li>get image bytes with {@link PdfImageXObject#getImageBytes(boolean)}, these image bytes
     * represent native image, i.e you can write these bytes to disk and get just an usual image;
     * <li>obtain PdfStream object which contains image dictionary with {@link PdfImageXObject#getPdfObject()} method;
     * <li>convert image to {@link java.awt.image.BufferedImage} with {@link PdfImageXObject#getBufferedImage()};
     * </ul>
     *
     * @return the {@link PdfImageXObject image}
     */
    public PdfImageXObject getImage() {
        return image;
    }

    /**
     * Gets the name of the image resource.
     *
     * @return the {@link PdfName name} of the image resource
     */
    public PdfName getImageResourceName() {
        return resourceName;
    }

    /**
     * Gets the vector in User space representing the start point of the image.
     *
     * @return the {@link Vector vector} in User space representing the start point of the image
     */
    public Vector getStartPoint() {
        return new Vector(0, 0, 1).cross(ctm);
    }

    /**
     * Gets the coordinate transformation matrix in User space which was active when this image was rendered.
     *
     * @return the coordinate transformation matrix in User space which was active when this image
     * was rendered
     */
    public Matrix getImageCtm() {
        return ctm;
    }

    /**
     * Gets the size of the image in User space units.
     *
     * @return the size of the image, in User space units
     */
    public float getArea() {
        // the image space area is 1, so we multiply that by the determinant of the CTM to get the transformed area
        return ctm.getDeterminant();
    }

    /**
     * Gets the inline flag.
     *
     * @return {@code true} if image was inlined in original stream
     */
    public boolean isInline() {
        return isInline;
    }

    /**
     * Gets the color space dictionary of the image.
     *
     * @return the color space dictionary from resources which are associated with the image
     */
    public PdfDictionary getColorSpaceDictionary() {
        return colorSpaceDictionary;
    }

    /**
     * Gets hierarchy of the canvas tags that wraps given text.
     *
     * @return list of the wrapping canvas tags. The first tag is the innermost (nearest to the text).
     */
    public List<CanvasTag> getCanvasTagHierarchy() {
        return canvasTagHierarchy;
    }

    /**
     * Gets the marked-content identifier associated with this {@link ImageRenderInfo} instance.
     *
     * @return associated marked-content identifier or -1 in case content is unmarked
     */
    public int getMcid() {
        for (CanvasTag tag : canvasTagHierarchy) {
            if (tag.hasMcid()) {
                return tag.getMcid();
            }
        }
        return -1;
    }

    /**
     * Checks if this {@link ImageRenderInfo} instance is associated with a marked content sequence
     * with a given mcid.
     *
     * @param mcid a marked content id
     * @return {@code true} if the image is marked with this id, {@code false} otherwise
     */
    public boolean hasMcid(int mcid) {
        return hasMcid(mcid, false);
    }

    /**
     * Checks if this {@link ImageRenderInfo} instance is associated with a marked content sequence
     * with a given mcid.
     *
     * @param mcid                     a marked content id
     * @param checkTheTopmostLevelOnly indicates whether to check the topmost level of marked content stack only
     * @return {@code true} if this {@link ImageRenderInfo} instance is marked with this id, {@code false} otherwise
     */
    public boolean hasMcid(int mcid, boolean checkTheTopmostLevelOnly) {
        if (checkTheTopmostLevelOnly) {
            if (canvasTagHierarchy != null) {
                final int infoMcid = getMcid();
                return infoMcid != -1 && infoMcid == mcid;
            }
        } else {
            for (final CanvasTag tag : canvasTagHierarchy) {
                if (tag.hasMcid() && (tag.getMcid() == mcid)) {
                    return true;
                }
            }
        }
        return false;
    }
}
