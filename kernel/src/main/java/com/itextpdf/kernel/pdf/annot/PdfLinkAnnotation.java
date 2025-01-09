/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A link annotation represents either a hypertext link to a destination elsewhere in the document
 * or an {@link PdfAction} to be performed. See also ISO-320001 12.5.6.5, "Link Annotations".
 */
public class PdfLinkAnnotation extends PdfAnnotation {


    private static final Logger logger = LoggerFactory.getLogger(PdfLinkAnnotation.class);

    /**
     * Highlight modes.
     */
    public static final PdfName None = PdfName.N;
    public static final PdfName Invert = PdfName.I;
    public static final PdfName Outline = PdfName.O;
    public static final PdfName Push = PdfName.P;

    /**
     * Creates a new {@link PdfLinkAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfLinkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a new {@link PdfLinkAnnotation} instance based on {@link Rectangle}
     * instance, that define the location of the annotation on the page in default user space units.
     *
     * @param rect the {@link Rectangle} that define the location of the annotation
     */
    public PdfLinkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfName getSubtype() {
        return PdfName.Link;
    }

    /**
     * Gets the annotation destination as {@link PdfObject} instance.
     *
     * <p>
     * Destination shall be displayed when the annotation is activated. See also ISO-320001, Table 173.
     *
     * @return the annotation destination as {@link PdfObject} instance
     */
    public PdfObject getDestinationObject() {
        return getPdfObject().get(PdfName.Dest);
    }

    /**
     * Sets the annotation destination as {@link PdfObject} instance.
     *
     * <p>
     * Destination shall be displayed when the annotation is activated. See also ISO-320001, Table 173.
     *
     * @param destination the destination to be set as {@link PdfObject} instance
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setDestination(PdfObject destination) {
        if (getPdfObject().containsKey(PdfName.A)) {
            getPdfObject().remove(PdfName.A);
            logger.warn(IoLogMessageConstant.DESTINATION_NOT_PERMITTED_WHEN_ACTION_IS_SET);
        }
        if (destination.isArray() && ((PdfArray)destination).get(0).isNumber())
            LoggerFactory.getLogger(PdfLinkAnnotation.class).warn(IoLogMessageConstant.INVALID_DESTINATION_TYPE);
        return (PdfLinkAnnotation) put(PdfName.Dest, destination);
    }

    /**
     * Sets the annotation destination as {@link PdfDestination} instance.
     *
     * <p>
     * Destination shall be displayed when the annotation is activated. See also ISO-320001, Table 173.
     *
     * @param destination the destination to be set as {@link PdfDestination} instance
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return setDestination(destination.getPdfObject());
    }

    /**
     * Removes the annotation destination.
     *
     * <p>
     * Destination shall be displayed when the annotation is activated. See also ISO-320001, Table 173.
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation removeDestination() {
        getPdfObject().remove(PdfName.Dest);
        return this;
    }

    /**
     * An {@link PdfAction} to perform, such as launching an application, playing a sound,
     * changing an annotation’s appearance state etc, when the annotation is activated.
     *
     * @return {@link PdfDictionary} which defines the characteristics and behaviour of an action
     */
    public PdfDictionary getAction() {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    /**
     * Sets a {@link PdfDictionary} representing action to this annotation which will be performed
     * when the annotation is activated.
     *
     * @param action {@link PdfDictionary} that represents action to set to this annotation
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setAction(PdfDictionary action) {
        return (PdfLinkAnnotation) put(PdfName.A, action);
    }

    /**
     * Sets a {@link PdfAction} to this annotation which will be performed when the annotation is activated.
     *
     * @param action {@link PdfAction} to set to this annotation
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setAction(PdfAction action) {
        if (getDestinationObject() != null) {
            removeDestination();
            logger.warn(IoLogMessageConstant.ACTION_WAS_SET_TO_LINK_ANNOTATION_WITH_DESTINATION);
        }
        return (PdfLinkAnnotation) put(PdfName.A, action.getPdfObject());
    }

    /**
     * Removes a {@link PdfAction} from this annotation.
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation removeAction() {
        getPdfObject().remove(PdfName.A);
        return this;
    }

    /**
     * Gets the annotation highlight mode.
     *
     * <p>
     * The annotation’s highlighting mode is the visual effect that shall be used when the mouse
     * button is pressed or held down inside its active area. See also ISO-320001, Table 173.
     *
     * @return the name of visual effect
     */
    public PdfName getHighlightMode() {
        return getPdfObject().getAsName(PdfName.H);
    }


    /**
     * Sets the annotation highlight mode.
     *
     * <p>
     * The annotation’s highlighting mode is the visual effect that shall be used when the mouse
     * button is pressed or held down inside its active area. See also ISO-320001, Table 173.
     *
     * @param hlMode the name of visual effect to be set
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setHighlightMode(PdfName hlMode) {
        return (PdfLinkAnnotation) put(PdfName.H, hlMode);
    }

    /**
     * Gets the annotation URI action as {@link PdfDictionary}.
     *
     * <p>
     * When Web Capture (see ISO-320001 14.10, “Web Capture”) changes an annotation from a URI to a
     * go-to action, it uses this entry to save the data from the original URI action so that it can
     * be changed back in case the target page for the go-to action is subsequently deleted. See also
     * ISO-320001, Table 173.
     *
     * @return the URI action as pdfDictionary
     */
    public PdfDictionary getUriActionObject() {
        return getPdfObject().getAsDictionary(PdfName.PA);
    }

    /**
     * Sets the annotation URI action as {@link PdfDictionary} instance.
     *
     * <p>
     * When Web Capture (see ISO-320001 14.10, “Web Capture”) changes an annotation from a URI to a
     * go-to action, it uses this entry to save the data from the original URI action so that it can
     * be changed back in case the target page for the go-to action is subsequently deleted. See also
     * ISO-320001, Table 173.
     *
     * @param action the action to be set
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setUriAction(PdfDictionary action) {
        return (PdfLinkAnnotation) put(PdfName.PA, action);
    }

    /**
     * Sets the annotation URI action as {@link PdfAction} instance.
     *
     * <p>
     * A URI action (see ISO-320001 12.6.4.7, “URI Actions”) formerly associated with this annotation.
     * When Web Capture (see ISO-320001 14.10, “Web Capture”) changes an annotation from a URI to a
     * go-to action, it uses this entry to save the data from the original URI action so that it can
     * be changed back in case the target page for the go-to action is subsequently deleted. See also
     * ISO-320001, Table 173.
     *
     * @param action the action to be set
     *
     * @return this {@link PdfLinkAnnotation} instance
     */
    public PdfLinkAnnotation setUriAction(PdfAction action) {
        return (PdfLinkAnnotation) put(PdfName.PA, action.getPdfObject());
    }

    /**
     * An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     * Quadrilaterals are used to define regions inside annotation rectangle
     * in which the link annotation should be activated.
     *
     *
     * @return an {@link PdfArray} of 8 × n numbers specifying the coordinates of n quadrilaterals.
     */
    public PdfArray getQuadPoints() {
        return getPdfObject().getAsArray(PdfName.QuadPoints);
    }

    /**
     * Sets n quadrilaterals in default user space by passing an {@link PdfArray} of 8 × n numbers.
     * Quadrilaterals are used to define regions inside annotation rectangle
     * in which the link annotation should be activated.
     *
     * @param quadPoints an {@link PdfArray} of 8 × n numbers specifying the coordinates of n quadrilaterals.
     * @return this {@link PdfLinkAnnotation} instance.
     */
    public PdfLinkAnnotation setQuadPoints(PdfArray quadPoints) {
        return (PdfLinkAnnotation) put(PdfName.QuadPoints, quadPoints);
    }

    /**
     * BS entry specifies a border style dictionary that has more settings than the array specified for the Border
     * entry (see {@link PdfAnnotation#getBorder()}). If an annotation dictionary includes the BS entry, then the Border
     * entry is ignored. If annotation includes AP (see {@link PdfAnnotation#getAppearanceDictionary()}) it takes
     * precedence over the BS entry. For more info on BS entry see ISO-320001, Table 166.
     * @return {@link PdfDictionary} which is a border style dictionary or null if it is not specified.
     */
    public PdfDictionary getBorderStyle() {
        return getPdfObject().getAsDictionary(PdfName.BS);
    }

    /**
     * Sets border style dictionary that has more settings than the array specified for the Border entry ({@link PdfAnnotation#getBorder()}).
     * See ISO-320001, Table 166 and {@link #getBorderStyle()} for more info.
     * @param borderStyle a border style dictionary specifying the line width and dash pattern that shall be used
     *                    in drawing the annotation’s border.
     * @return this {@link PdfLinkAnnotation} instance.
     */
    public PdfLinkAnnotation setBorderStyle(PdfDictionary borderStyle) {
        return (PdfLinkAnnotation) put(PdfName.BS, borderStyle);
    }

    /**
     * Setter for the annotation's preset border style. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#STYLE_SOLID} - A solid rectangle surrounding the annotation.
     *     <li>{@link PdfAnnotation#STYLE_DASHED} - A dashed rectangle surrounding the annotation.
     *     <li>{@link PdfAnnotation#STYLE_BEVELED} - A simulated embossed rectangle that appears to be raised above the surface of the page.
     *     <li>{@link PdfAnnotation#STYLE_INSET} - A simulated engraved rectangle that appears to be recessed below the surface of the page.
     *     <li>{@link PdfAnnotation#STYLE_UNDERLINE} - A single line along the bottom of the annotation rectangle.
     * </ul>
     * See also ISO-320001, Table 166.
     * @param style The new value for the annotation's border style.
     * @return this {@link PdfLinkAnnotation} instance.
     * @see #getBorderStyle()
     */
    public PdfLinkAnnotation setBorderStyle(PdfName style) {
        return setBorderStyle(BorderStyleUtil.setStyle(getBorderStyle(), style));
    }

    /**
     * Setter for the annotation's preset dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for the annotation border style (see {@link #setBorderStyle(PdfName)}.
     * See ISO-320001 8.4.3.6, "Line Dash Pattern" for the format in which dash pattern shall be specified.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return this {@link PdfLinkAnnotation} instance.
     */
    public PdfLinkAnnotation setDashPattern(PdfArray dashPattern) {
        return setBorderStyle(BorderStyleUtil.setDashPattern(getBorderStyle(), dashPattern));
    }
}
