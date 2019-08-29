/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfLinkAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 5795613340575331536L;

    private static final Logger logger = LoggerFactory.getLogger(PdfLinkAnnotation.class);

    /**
     * Highlight modes.
     */
    public static final PdfName None = PdfName.N;
    public static final PdfName Invert = PdfName.I;
    public static final PdfName Outline = PdfName.O;
    public static final PdfName Push = PdfName.P;

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfLinkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfLinkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfName getSubtype() {
        return PdfName.Link;
    }

    public PdfObject getDestinationObject() {
        return getPdfObject().get(PdfName.Dest);
    }

    public PdfLinkAnnotation setDestination(PdfObject destination) {
        if (getPdfObject().containsKey(PdfName.A)) {
            getPdfObject().remove(PdfName.A);
            logger.warn(LogMessageConstant.DESTINATION_NOT_PERMITTED_WHEN_ACTION_IS_SET);
        }
        if (destination.isArray() && ((PdfArray)destination).get(0).isNumber())
            LoggerFactory.getLogger(PdfLinkAnnotation.class).warn(LogMessageConstant.INVALID_DESTINATION_TYPE);
        return (PdfLinkAnnotation) put(PdfName.Dest, destination);
    }

    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return setDestination(destination.getPdfObject());
    }

    public PdfLinkAnnotation removeDestination() {
        getPdfObject().remove(PdfName.Dest);
        return this;
    }

    /**
     * An {@link PdfAction} to perform, such as launching an application, playing a sound,
     * changing an annotation’s appearance state etc, when the annotation is activated.
     * @return {@link PdfDictionary} which defines the characteristics and behaviour of an action.
     */
    public PdfDictionary getAction() {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    /**
     * Sets a {@link PdfDictionary} representing action to this annotation which will be performed
     * when the annotation is activated.
     * @param action {@link PdfDictionary} that represents action to set to this annotation.
     * @return this {@link PdfLinkAnnotation} instance.
     */
    public PdfLinkAnnotation setAction(PdfDictionary action) {
        return (PdfLinkAnnotation) put(PdfName.A, action);
    }

    /**
     * Sets a {@link PdfAction} to this annotation which will be performed when the annotation is activated.
     * @param action {@link PdfAction} to set to this annotation.
     * @return this {@link PdfLinkAnnotation} instance.
     */
    public PdfLinkAnnotation setAction(PdfAction action) {
        if (getDestinationObject() != null) {
            removeDestination();
            logger.warn(LogMessageConstant.ACTION_WAS_SET_TO_LINK_ANNOTATION_WITH_DESTINATION);
        }
        return (PdfLinkAnnotation) put(PdfName.A, action.getPdfObject());
    }

    public PdfLinkAnnotation removeAction() {
        getPdfObject().remove(PdfName.A);
        return this;
    }

    public PdfName getHighlightMode() {
        return getPdfObject().getAsName(PdfName.H);
    }

    public PdfLinkAnnotation setHighlightMode(PdfName hlMode) {
        return (PdfLinkAnnotation) put(PdfName.H, hlMode);
    }

    public PdfDictionary getUriActionObject() {
        return getPdfObject().getAsDictionary(PdfName.PA);
    }

    public PdfLinkAnnotation setUriAction(PdfDictionary action) {
        return (PdfLinkAnnotation) put(PdfName.PA, action);
    }

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
