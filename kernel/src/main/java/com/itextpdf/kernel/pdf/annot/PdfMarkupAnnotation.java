/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a super class for the annotations which are defined as markup annotations
 * because they are used primarily to mark up PDF documents. These annotations have
 * text that appears as part of the annotation and may be displayed in other ways
 * by a conforming reader, such as in a Comments pane.
 * See also ISO-320001 12.5.6.2 "Markup Annotations".
 */
public abstract class PdfMarkupAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 239280278775576458L;

    protected PdfAnnotation inReplyTo = null;
    protected PdfPopupAnnotation popup = null;

    protected PdfMarkupAnnotation(Rectangle rect) {
        super(rect);
    }

    protected PdfMarkupAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * The text label that will be displayed in the title bar of the annotation's pop-up window
     * when open and active. This entry shall identify the user who added the annotation.
     * @return {@link PdfString} which value is an annotation text label content
     * or null if text is not specified.
     */
    public PdfString getText() {
        return getPdfObject().getAsString(PdfName.T);
    }

    /**
     * Sets the text label that will be displayed in the title bar of the annotation's pop-up window
     * when open and active. This entry shall identify the user who added the annotation.
     * @param text {@link PdfString} which value is an annotation text label content.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setText(PdfString text) {
        return (PdfMarkupAnnotation) put(PdfName.T, text);
    }

    /**
     * The constant opacity value that will be used in painting the annotation.
     * This value is applied to all visible elements of the annotation in its closed state
     * (including its background and border) but not to the pop-up window that appears when
     * the annotation is opened. Default value: 1.0.
     * @return a {@link PdfNumber} which value is in range between 0 and 1, which specifies the
     * level of opacity. This method returns null if opacity is not specified; in this case default
     * value is used, which is 1.
     */
    public PdfNumber getOpacity() {
        return getPdfObject().getAsNumber(PdfName.CA);
    }

    /**
     * Sets the constant opacity value that will be used in painting the annotation.
     * @param ca a {@link PdfNumber} which value is in range between 0 and 1, which specifies the
     * level of opacity.
     * @return this {@link PdfMarkupAnnotation} instance.
     * @see #getOpacity()
     */
    public PdfMarkupAnnotation setOpacity(PdfNumber ca) {
        return (PdfMarkupAnnotation) put(PdfName.CA, ca);
    }

    /**
     * A rich text string (see ISO-320001 12.7.3.4, “Rich Text Strings”) that
     * shall be displayed in the pop-up window when the annotation is opened.
     * @return text string or text stream that specifies rich text or null if
     * rich text is not specified.
     */
    public PdfObject getRichText() {
        return getPdfObject().get(PdfName.RC);
    }

    /**
     * Sets a rich text string (see ISO-320001 12.7.3.4, “Rich Text Strings”) that
     * shall be displayed in the pop-up window when the annotation is opened.
     * @param richText text string or text stream that specifies rich text.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setRichText(PdfObject richText) {
        return (PdfMarkupAnnotation) put(PdfName.RC, richText);
    }

    /**
     * The date and time when the annotation was created.
     * @return a {@link PdfString} which value should be in the date format specified in (ISO-320001 7.9.4, “Dates”).
     */
    public PdfString getCreationDate() {
        return getPdfObject().getAsString(PdfName.CreationDate);
    }

    /**
     * Sets the date and time when the annotation was created.
     * @param creationDate {@link PdfString} which value should be in the date format
     *                                      specified in (ISO-320001 7.9.4, “Dates”).
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setCreationDate(PdfString creationDate) {
        return (PdfMarkupAnnotation) put(PdfName.CreationDate, creationDate);
    }

    /**
     * An annotation object that this annotation is “in reply to.”
     * Both annotations shall be on the same page of the document.
     * The relationship between the two annotations shall be specified by the RT entry
     * (see {@link PdfMarkupAnnotation#getReplyType()}).
     * @return a {@link PdfDictionary} that represents an annotation that this annotation is “in reply to.”
     */
    public PdfDictionary getInReplyToObject() {
        return getPdfObject().getAsDictionary(PdfName.IRT);
    }

    /**
     * An annotation that this annotation is “in reply to.”
     * Both annotations shall be on the same page of the document.
     * The relationship between the two annotations shall be specified by the RT entry
     * (see {@link PdfMarkupAnnotation#getReplyType()}).
     * @return a {@link PdfAnnotation} that this annotation is “in reply to.”
     */
    public PdfAnnotation getInReplyTo() {
        if (inReplyTo == null) {
            inReplyTo = makeAnnotation(getInReplyToObject());
        }
        return inReplyTo;
    }

    /**
     * Sets an annotation that this annotation is “in reply to.”
     * Both annotations shall be on the same page of the document.
     * The relationship between the two annotations shall be specified by the RT entry
     * (see {@link PdfMarkupAnnotation#getReplyType()}).
     * @param inReplyTo a {@link PdfAnnotation} that this annotation is “in reply to.”
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setInReplyTo(PdfAnnotation inReplyTo) {
        this.inReplyTo = inReplyTo;
        return (PdfMarkupAnnotation) put(PdfName.IRT, inReplyTo.getPdfObject());
    }

    /**
     * Sets a pop-up annotation for entering or editing the text associated with this annotation.
     * Pop-up annotation defines an associated with this annotation pop-up window that may contain text.
     * The Contents (see {@link PdfAnnotation#setContents(PdfString)}) entry of the annotation that has
     * an associated popup specifies the text that shall be displayed when the pop-up window is opened.
     * @param popup an {@link PdfPopupAnnotation} that will be associated with this annotation.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setPopup(PdfPopupAnnotation popup) {
        this.popup = popup;
        popup.setParent(this);
        return (PdfMarkupAnnotation) put(PdfName.Popup, popup.getPdfObject());
    }

    /**
     * An associated pop-up annotation object. See {@link #getPopup()} for more info.
     * @return a {@link PdfDictionary} that represents an associated pop-up annotation,
     * or null if popup annotation is not specified.
     */
    public PdfDictionary getPopupObject() {
        return getPdfObject().getAsDictionary(PdfName.Popup);
    }

    /**
     * An associated pop-up annotation for entering or editing the text associated with this annotation.
     * Pop-up annotation defines an associated with this annotation pop-up window that may contain text.
     * The Contents (see {@link PdfAnnotation#getContents()}) entry of the annotation that has
     * an associated popup specifies the text that shall be displayed when the pop-up window is opened.
     * @return an {@link PdfPopupAnnotation} that is associated with this annotation, or null if there is none.
     */
    public PdfPopupAnnotation getPopup() {
        if (popup == null) {
            PdfDictionary popupObject = getPopupObject();
            if ( popupObject != null ) {
                PdfAnnotation annotation = makeAnnotation(popupObject);
                if (!(annotation instanceof PdfPopupAnnotation)) {
                    Logger logger = LoggerFactory.getLogger(PdfMarkupAnnotation.class);
                    logger.warn(LogMessageConstant.POPUP_ENTRY_IS_NOT_POPUP_ANNOTATION);
                    return null;
                }
                popup = (PdfPopupAnnotation) annotation;
            }
        }
        return popup;
    }

    /**
     * Text representing a short description of the subject being addressed by the annotation.
     * @return a {@link PdfString} which value is a annotation subject.
     */
    public PdfString getSubject() {
        return getPdfObject().getAsString(PdfName.Subj);
    }

    /**
     * Sets the text representing a short description of the subject being addressed by the annotation.
     * @param subject a {@link PdfString} which value is a annotation subject.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setSubject(PdfString subject) {
        return (PdfMarkupAnnotation) put(PdfName.Subj, subject);
    }

    /**
     * A name specifying the relationship (the “reply type”) between this annotation and one specified by IRT entry
     * (see {@link #getInReplyTo()}). Valid values are:
     * <ul>
     *     <li>{@link PdfName#R} - The annotation shall be considered a reply to the annotation specified by IRT.
     *     Conforming readers shall not display replies to an annotation individually but together in the form of
     *     threaded comments.</li>
     *     <li>{@link PdfName#Group} - The annotation shall be grouped with the annotation specified by IRT.</li>
     * </ul>
     * @return a {@link PdfName} specifying relationship with the specified by the IRT entry; or null if reply
     * type is not specified, in this case the default value is {@link PdfName#R}.
     */
    public PdfName getReplyType() {
        return getPdfObject().getAsName(PdfName.RT);
    }

    /**
     * Sets the relationship (the “reply type”) between this annotation and one specified by IRT entry
     * (see {@link #setInReplyTo(PdfAnnotation)}). For valid values see {@link #getInReplyTo()}.
     * @param replyType a {@link PdfName} specifying relationship with the specified by the IRT entry.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setReplyType(PdfName replyType) {
        return (PdfMarkupAnnotation) put(PdfName.RT, replyType);
    }

    /**
     * A name describing the intent of the markup annotation.
     * See {@link #setIntent(PdfName)} for more info.
     * @return a {@link PdfName} describing the intent of the markup annotation, or null if not specified.
     */
    public PdfName getIntent() {
        return getPdfObject().getAsName(PdfName.IT);
    }

    /**
     * Sets a name describing the intent of the markup annotation.
     * Intents allow conforming readers to distinguish between different uses and behaviors
     * of a single markup annotation type. If this entry is not present or its value is the same as the annotation type,
     * the annotation shall have no explicit intent and should behave in a generic manner in a conforming reader.
     * <p>
     * See ISO-320001, free text annotations (Table 174), line annotations (Table 175), polygon annotations (Table 178),
     * and polyline annotations (Table 178) for the specific intent values for those types.
     * </p>
     * @param intent a {@link PdfName} describing the intent of the markup annotation.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setIntent(PdfName intent) {
        return (PdfMarkupAnnotation) put(PdfName.IT, intent);
    }

    /**
     * An external data dictionary specifying data that shall be associated with the annotation.
     * This dictionary contains the following entries:
     * <ul>
     *     <li>{@link PdfName#Type} - (optional) If present, shall be {@link PdfName#ExData}.</li>
     *     <li>{@link PdfName#Subtype} - (required) a name specifying the type of data that the markup annotation
     *     shall be associated with. The only defined value is {@link PdfName#Markup3D}. Table 298 (ISO-320001)
     *     lists the values that correspond to a subtype of Markup3D (See also {@link Pdf3DAnnotation}).</li>
     * </ul>
     * @return An external data {@link PdfDictionary}, or null if not specified.
     */
    public PdfDictionary getExternalData() {
        return getPdfObject().getAsDictionary(PdfName.ExData);
    }

    /**
     * Sets an external data dictionary specifying data that shall be associated with the annotation.
     * This dictionary should contain the following entries:
     * <ul>
     *     <li>{@link PdfName#Type} - (optional) If present, shall be {@link PdfName#ExData}.</li>
     *     <li>{@link PdfName#Subtype} - (required) a name specifying the type of data that the markup annotation
     *     shall be associated with. The only defined value is {@link PdfName#Markup3D}. Table 298 (ISO-320001)
     *     lists the values that correspond to a subtype of Markup3D (See also {@link Pdf3DAnnotation}).</li>
     * </ul>
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    public PdfMarkupAnnotation setExternalData(PdfName exData) {
        return (PdfMarkupAnnotation) put(PdfName.ExData, exData);
    }

    /**
     * A set of four numbers describing the numerical differences between two rectangles:
     * the Rect entry of the annotation and another rectangle within that one, which
     * meaning depends on the type of the annotation:
     * <ul>
     *     <li> for {@link PdfFreeTextAnnotation} the inner rectangle is where the annotation's text should be displayed;</li>
     *     <li>
     *         for {@link PdfSquareAnnotation} and {@link PdfCircleAnnotation} the inner rectangle is the actual boundaries
     *         of the underlying square or circle;
     *     </li>
     *     <li> for {@link PdfCaretAnnotation} the inner rectangle is the actual boundaries of the underlying caret.</li>
     * </ul>
     *
     * @param rect a {@link PdfArray} with four numbers which correspond to the differences in default user space between
     *             the left, top, right, and bottom coordinates of Rect and those of the inner rectangle, respectively.
     *             Each value shall be greater than or equal to 0. The sum of the top and bottom differences shall be
     *             less than the height of Rect, and the sum of the left and right differences shall be less than
     *             the width of Rect.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    @Deprecated
    public PdfMarkupAnnotation setRectangleDifferences(PdfArray rect) {
        return (PdfMarkupAnnotation) put(PdfName.RD, rect);
    }

    /**
     * A set of four numbers describing the numerical differences between two rectangles:
     * the Rect entry of the annotation and another rectangle within that one, which
     * meaning depends on the type of the annotation (see {@link #setRectangleDifferences(PdfArray)}).
     *
     * @return null if not specified, otherwise a {@link PdfArray} with four numbers which correspond to the
     * differences in default user space between the left, top, right, and bottom coordinates of Rect and those
     * of the inner rectangle, respectively.
     */
    @Deprecated
    public PdfArray getRectangleDifferences() {
        return getPdfObject().getAsArray(PdfName.RD);
    }

    /**
     * Some annotations types ({@link PdfSquareAnnotation}, {@link PdfCircleAnnotation}, {@link PdfPolyGeomAnnotation}
     * and {@link PdfFreeTextAnnotation}) may have a {@link PdfName#BE} entry, which is a border effect dictionary that specifies
     * an effect that shall be applied to the border of the annotations.
     * @param borderEffect a {@link PdfDictionary} which contents shall be specified in accordance to ISO-320001, Table 167.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    @Deprecated
    public PdfMarkupAnnotation setBorderEffect(PdfDictionary borderEffect) {
        return (PdfMarkupAnnotation) put(PdfName.BE, borderEffect);
    }

    /**
     * A border effect dictionary that specifies an effect that shall be applied to the border of the annotations.
     * @return a {@link PdfDictionary}, which is a border effect dictionary (see ISO-320001, Table 167).
     */
    @Deprecated
    public PdfDictionary getBorderEffect() {
        return getPdfObject().getAsDictionary(PdfName.BE);
    }

    /**
     * The interior color which is used to fill areas specific for different types of annotation. For {@link PdfLineAnnotation}
     * and polyline annotation ({@link PdfPolyGeomAnnotation} - the annotation's line endings, for {@link PdfSquareAnnotation}
     * and {@link PdfCircleAnnotation} - the annotation's rectangle or ellipse, for {@link PdfRedactAnnotation} - the redacted
     * region after the affected content has been removed.
     * @return {@link Color} of either {@link DeviceGray}, {@link DeviceRgb} or {@link DeviceCmyk} type which defines
     * interior color of the annotation, or null if interior color is not specified.
     */
    @Deprecated
    public Color getInteriorColor() {
        return InteriorColorUtil.parseInteriorColor(getPdfObject().getAsArray(PdfName.IC));
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color which is used to fill areas specific
     * for different types of annotation. For {@link PdfLineAnnotation} and polyline annotation ({@link PdfPolyGeomAnnotation} -
     * the annotation's line endings, for {@link PdfSquareAnnotation} and {@link PdfCircleAnnotation} - the annotation's
     * rectangle or ellipse, for {@link PdfRedactAnnotation} - the redacted region after the affected content has been removed.
     * @param interiorColor a {@link PdfArray} of numbers in the range 0.0 to 1.0. The number of array elements determines
     *                      the colour space in which the colour is defined: 0 - No colour, transparent; 1 - DeviceGray,
     *                      3 - DeviceRGB, 4 - DeviceCMYK. For the {@link PdfRedactAnnotation} number of elements shall be
     *                      equal to 3 (which defines DeviceRGB colour space).
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    @Deprecated
    public PdfMarkupAnnotation setInteriorColor(PdfArray interiorColor) {
        return (PdfMarkupAnnotation) put(PdfName.IC, interiorColor);
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color which is used to fill areas specific
     * for different types of annotation. See {@link #setInteriorColor(PdfArray)} for more info.
     * @param interiorColor an array of floats in the range 0.0 to 1.0.
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    @Deprecated
    public PdfMarkupAnnotation setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }

    /**
     * The name of an icon that is used in displaying the annotation. Possible values are different for different
     * annotation types. See {@link #setIconName(PdfName)}.
     * @return a {@link PdfName} that specifies the icon for displaying annotation, or null if icon name is not specified.
     */
    @Deprecated
    public PdfName getIconName() {
        return getPdfObject().getAsName(PdfName.Name);
    }

    /**
     * The name of an icon that is used in displaying the annotation.
     * @param name a {@link PdfName} that specifies the icon for displaying annotation. Possible values are different
     *             for different annotation types:
     *             <ul>
     *                  <li>{@link PdfTextAnnotation} - Comment, Key, Note, Help, NewParagraph, Paragraph, Insert;</li>
     *                  <li>{@link PdfStampAnnotation} - Approved, Experimental, NotApproved, AsIs, Expired, NotForPublicRelease,
     *                      Confidential, Final, Sold, Departmental, ForComment, TopSecret, Draft, ForPublicRelease.</li>
     *                  <li>{@link PdfFileAttachmentAnnotation} - GraphPushPin, PaperclipTag. Additional names may be supported as well.</li>
     *                  <li>{@link PdfSoundAnnotation} - Speaker and Mic. Additional names may be supported as well.</li>
     *             </ul>
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    @Deprecated
    public PdfMarkupAnnotation setIconName(PdfName name) {
        return (PdfMarkupAnnotation) put(PdfName.Name, name);
    }

    /**
     * The default appearance string that shall be used in formatting the text. See ISO-32001 12.7.3.3, “Variable Text”.
     * @param appearanceString a {@link PdfString} that specifies the default appearance.
     * @return this {@link PdfMarkupAnnotation} instance.
     * @deprecated  DefaultAppearance entry exist only in {@Link PdfFreeTextAnnotation} and {@link PdfRedactAnnotation},
     *              so it will be moved to those two classes in 7.1
     */
    @Deprecated
    public PdfMarkupAnnotation setDefaultAppearance(PdfString appearanceString) {
        return (PdfMarkupAnnotation) put(PdfName.DA, appearanceString);
    }

    /**
     * The default appearance string that shall be used in formatting the text. See ISO-32001 12.7.3.3, “Variable Text”.
     * @return a {@link PdfString} that specifies the default appearance, or null if default appereance is not specified.
     */
    @Deprecated
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * A code specifying the form of quadding (justification) that is used in displaying the annotation's text:
     * 0 - Left-justified, 1 - Centered, 2 - Right-justified. Default value: 0 (left-justified).
     * @return a code specifying the form of quadding (justification), returns the default value if not explicitly specified.
     */
    @Deprecated
    public int getJustification() {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.intValue();
    }

    /**
     * A code specifying the form of quadding (justification) that is used in displaying the annotation's text:
     * 0 - Left-justified, 1 - Centered, 2 - Right-justified. Default value: 0 (left-justified).
     * @param justification a code specifying the form of quadding (justification).
     * @return this {@link PdfMarkupAnnotation} instance.
     */
    @Deprecated
    public PdfMarkupAnnotation setJustification(int justification) {
        return (PdfMarkupAnnotation) put(PdfName.Q, new PdfNumber(justification));
    }
}
