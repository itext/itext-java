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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAnnotationBorder;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.layer.IPdfOCG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a super class for the annotation dictionary wrappers. Derived classes represent
 * different standard types of annotations. See ISO-320001 12.5.6, "Annotation Types."
 */
public abstract class PdfAnnotation extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -6555705164241587799L;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int INVISIBLE = 1;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int HIDDEN = 2;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int PRINT = 4;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int NO_ZOOM = 8;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int NO_ROTATE = 16;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int NO_VIEW = 32;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int READ_ONLY = 64;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int LOCKED = 128;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int TOGGLE_NO_VIEW = 256;

    /**
     * Annotation flag.
     * See also {@link PdfAnnotation#setFlag(int)} and ISO-320001, table 165.
     */
    public static final int LOCKED_CONTENTS = 512;


    /**
     * Widget annotation highlighting mode. See ISO-320001, Table 188 (H key).
     * Also see {@link PdfWidgetAnnotation#setHighlightMode(PdfName)}.
     */
    public static final PdfName HIGHLIGHT_NONE = PdfName.N;

    /**
     * Widget annotation highlighting mode. See ISO-320001, Table 188 (H key).
     * Also see {@link PdfWidgetAnnotation#setHighlightMode(PdfName)}.
     */
    public static final PdfName HIGHLIGHT_INVERT = PdfName.I;

    /**
     * Widget annotation highlighting mode. See ISO-320001, Table 188 (H key).
     * Also see {@link PdfWidgetAnnotation#setHighlightMode(PdfName)}.
     */
    public static final PdfName HIGHLIGHT_OUTLINE = PdfName.O;

    /**
     * Widget annotation highlighting mode. See ISO-320001, Table 188 (H key).
     * Also see {@link PdfWidgetAnnotation#setHighlightMode(PdfName)}.
     */
    public static final PdfName HIGHLIGHT_PUSH = PdfName.P;

    /**
     * Widget annotation highlighting mode. See ISO-320001, Table 188 (H key).
     * Also see {@link PdfWidgetAnnotation#setHighlightMode(PdfName)}.
     */
    public static final PdfName HIGHLIGHT_TOGGLE = PdfName.T;


    /**
     * Annotation border style. See ISO-320001, Table 166 (S key).
     */
    public static final PdfName STYLE_SOLID = PdfName.S;

    /**
     * Annotation border style. See ISO-320001, Table 166 (S key).
     */
    public static final PdfName STYLE_DASHED = PdfName.D;

    /**
     * Annotation border style. See ISO-320001, Table 166 (S key).
     */
    public static final PdfName STYLE_BEVELED = PdfName.B;

    /**
     * Annotation border style. See ISO-320001, Table 166 (S key).
     */
    public static final PdfName STYLE_INSET = PdfName.I;

    /**
     * Annotation border style. See ISO-320001, Table 166 (S key).
     */
    public static final PdfName STYLE_UNDERLINE = PdfName.U;


    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString Marked = new PdfString("Marked");

    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString Unmarked = new PdfString("Unmarked");

    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString Accepted = new PdfString("Accepted");

    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString Rejected = new PdfString("Rejected");

    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString Canceled = new PdfString("Cancelled");

    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString Completed = new PdfString("Completed");

    /**
     * Annotation state. See ISO-320001 12.5.6.3 "Annotation States" and Table 171 in particular.
     * Also see {@link PdfTextAnnotation#setState(PdfString)}.
     */
    public static final PdfString None = new PdfString("None");


    /**
     * Annotation state model. See ISO-320001, Table 172 (StateModel key).
     * Also see {@link PdfTextAnnotation#setStateModel(PdfString)}.
     */
    public static final PdfString MarkedModel = new PdfString("Marked");

    /**
     * Annotation state model. See ISO-320001, Table 172 (StateModel key).
     * Also see {@link PdfTextAnnotation#setStateModel(PdfString)}.
     */
    public static final PdfString ReviewModel = new PdfString("Review");

    protected PdfPage page;

    /**
     * Factory method that creates the type specific {@link PdfAnnotation} from the given {@link PdfObject}
     * that represents annotation object. This method is useful for property reading in reading mode or
     * modifying in stamping mode. See derived classes of this class to see possible specific annotation types
     * created.
     *
     * @param pdfObject a {@link PdfObject} that represents annotation in the document.
     * @return created {@link PdfAnnotation}.
     */
    public static PdfAnnotation makeAnnotation(PdfObject pdfObject) {
        PdfAnnotation annotation = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName subtype = dictionary.getAsName(PdfName.Subtype);
            if (PdfName.Link.equals(subtype)) {
                annotation = new PdfLinkAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Popup.equals(subtype)) {
                annotation = new PdfPopupAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Widget.equals(subtype)) {
                annotation = new PdfWidgetAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Screen.equals(subtype)) {
                annotation = new PdfScreenAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName._3D.equals(subtype)) {
                annotation = new Pdf3DAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Highlight.equals(subtype) || PdfName.Underline.equals(subtype) || PdfName.Squiggly.equals(subtype) || PdfName.StrikeOut.equals(subtype)) {
                annotation = new PdfTextMarkupAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Caret.equals(subtype)) {
                annotation = new PdfCaretAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Text.equals(subtype)) {
                annotation = new PdfTextAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Sound.equals(subtype)) {
                annotation = new PdfSoundAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Stamp.equals(subtype)) {
                annotation = new PdfStampAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.FileAttachment.equals(subtype)) {
                annotation = new PdfFileAttachmentAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Ink.equals(subtype)) {
                annotation = new PdfInkAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.PrinterMark.equals(subtype)) {
                annotation = new PdfPrinterMarkAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.TrapNet.equals(subtype)) {
                annotation = new PdfTrapNetworkAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.FreeText.equals(subtype)) {
                annotation = new PdfFreeTextAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Square.equals(subtype)) {
                annotation = new PdfSquareAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Circle.equals(subtype)) {
                annotation = new PdfCircleAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Line.equals(subtype)) {
                annotation = new PdfLineAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Polygon.equals(subtype) || PdfName.PolyLine.equals(subtype)) {
                annotation = new PdfPolyGeomAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Redact.equals(subtype)) {
                annotation = new PdfRedactAnnotation((PdfDictionary) pdfObject);
            } else if (PdfName.Watermark.equals(subtype)) {
                annotation = new PdfWatermarkAnnotation((PdfDictionary) pdfObject);
            }
        }
        return annotation;
    }

    protected PdfAnnotation(Rectangle rect) {
        this(new PdfDictionary());
        put(PdfName.Rect, new PdfArray(rect));
        put(PdfName.Subtype, getSubtype());
    }

    protected PdfAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
        markObjectAsIndirect(getPdfObject());
    }

    /**
     * Gets a {@link PdfName} which value is a subtype of this annotation.
     * See ISO-320001 12.5.6, "Annotation Types" for the reference to the possible types.
     *
     * @return subtype of this annotation.
     */
    public abstract PdfName getSubtype();

    /**
     * Sets the layer this annotation belongs to.
     *
     * @param layer the layer this annotation belongs to
     */
    public void setLayer(IPdfOCG layer) {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

    /**
     * Gets the text that shall be displayed for the annotation or, if this type of annotation does not display text,
     * an alternate description of the annotation’s contents in human-readable form.
     *
     * @return annotation text content.
     */
    public PdfString getContents() {
        return getPdfObject().getAsString(PdfName.Contents);
    }

    /**
     * Sets the text that shall be displayed for the annotation or, if this type of annotation does not display text,
     * an alternate description of the annotation’s contents in human-readable form.
     *
     * @param contents a {@link PdfString} containing text content to be set to the annotation.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setContents(PdfString contents) {
        return put(PdfName.Contents, contents);
    }

    /**
     * Sets the text that shall be displayed for the annotation or, if this type of annotation does not display text,
     * an alternate description of the annotation’s contents in human-readable form.
     *
     * @param contents a java {@link String} containing text content to be set to the annotation.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setContents(String contents) {
        return setContents(new PdfString(contents, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Gets a {@link PdfDictionary} that represents a page of the document on which annotation is placed,
     * i.e. which has this annotation in it's /Annots array.
     *
     * @return {@link PdfDictionary} that is a page pdf object or null if annotation is not added to the page yet.
     */
    public PdfDictionary getPageObject() {
        return getPdfObject().getAsDictionary(PdfName.P);
    }

    /**
     * Gets a {@link PdfPage} on which annotation is placed.
     *
     * @return {@link PdfPage} on which annotation is placed or null if annotation is not placed yet.
     */
    public PdfPage getPage() {
        PdfIndirectReference annotationIndirectReference;
        if (page == null && (annotationIndirectReference = getPdfObject().getIndirectReference()) != null) {
            PdfDocument doc = annotationIndirectReference.getDocument();

            PdfDictionary pageDictionary = getPageObject();
            if (pageDictionary != null) {
                page = doc.getPage(pageDictionary);
            } else {
                for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                    PdfPage docPage = doc.getPage(i);
                    if (!docPage.isFlushed()) {
                        for (PdfAnnotation annot : docPage.getAnnotations()) {
                            if (annotationIndirectReference.equals(annot.getPdfObject().getIndirectReference())) {
                                page = docPage;
                                break;
                            }
                        }
                    }
                }
            }


        }
        return page;
    }

    /**
     * Method that modifies annotation page property, which defines to which page annotation belongs.
     * Keep in mind that this doesn't actually add an annotation to the page,
     * it should be done via {@link PdfPage#addAnnotation(PdfAnnotation)}.
     * Also you don't need to set this property manually, this is done automatically on addition to the page.
     *
     * @param page the {@link PdfPage} to which annotation will be added.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setPage(PdfPage page) {
        this.page = page;
        return put(PdfName.P, page.getPdfObject());
    }

    /**
     * Gets the annotation name, a text string uniquely identifying it among all the
     * annotations on its page.
     *
     * @return a {@link PdfString} with annotation name as it's value or null if name
     * is not specified.
     */
    public PdfString getName() {
        return getPdfObject().getAsString(PdfName.NM);
    }

    /**
     * Sets the annotation name, a text string uniquely identifying it among all the
     * annotations on its page.
     *
     * @param name a {@link PdfString} to be set as annotation name.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setName(PdfString name) {
        return put(PdfName.NM, name);
    }

    /**
     * The date and time when the annotation was most recently modified.
     * This is an optional property of the annotation.
     *
     * @return a {@link PdfString} with the modification date as it's value or null if date is not specified.
     */
    public PdfString getDate() {
        return getPdfObject().getAsString(PdfName.M);
    }

    /**
     * The date and time when the annotation was most recently modified.
     *
     * @param date a {@link PdfString} with date. The format should be a date string as described
     *             in ISO-320001 7.9.4, "Dates".
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setDate(PdfString date) {
        return put(PdfName.M, date);
    }

    /**
     * A set of flags specifying various characteristics of the annotation (see ISO-320001 12.5.3, "Annotation Flags").
     * For specific annotation flag constants see {@link PdfAnnotation#setFlag(int)}.
     * Default value: 0.
     *
     * @return an integer interpreted as one-bit flags specifying various characteristics of the annotation.
     */
    public int getFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.F);
        if (f != null)
            return f.intValue();
        else
            return 0;
    }

    /**
     * Sets a set of flags specifying various characteristics of the annotation (see ISO-320001 12.5.3, "Annotation Flags").
     * On the contrary from {@link PdfAnnotation#setFlag(int)}, this method sets a complete set of enabled and disabled flags at once.
     * If not set specifically the default value is 0.
     *
     * @param flags an integer interpreted as set of one-bit flags specifying various characteristics of the annotation.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setFlags(int flags) {
        return put(PdfName.F, new PdfNumber(flags));
    }

    /**
     * Sets a flag that specifies a characteristic of the annotation to enabled state (see ISO-320001 12.5.3, "Annotation Flags").
     * On the contrary from {@link PdfAnnotation#setFlags(int)}, this method sets only specified flags to enabled state,
     * but doesn't disable other flags.
     * Possible flags:
     * <ul>
     * <li>{@link PdfAnnotation#INVISIBLE} - If set, do not display the annotation if it does not belong to one of the
     * standard annotation types and no annotation handler is available. If clear, display such unknown annotation
     * using an appearance stream specified by its appearance dictionary, if any.
     * </li>
     * <li>{@link PdfAnnotation#HIDDEN} - If set, do not display or print the annotation or allow it to interact with
     * the user, regardless of its annotation type or whether an annotation handler is available.
     * </li>
     * <li>{@link PdfAnnotation#PRINT} - If set, print the annotation when the page is printed. If clear, never print
     * the annotation, regardless of whether it is displayed on the screen.
     * </li>
     * <li>{@link PdfAnnotation#NO_ZOOM} - If set, do not scale the annotation’s appearance to match the magnification of
     * the page. The location of the annotation on the page (defined by the upper-left corner of its annotation
     * rectangle) shall remain fixed, regardless of the page magnification.}
     * </li>
     * <li>{@link PdfAnnotation#NO_ROTATE} - If set, do not rotate the annotation’s appearance to match the rotation
     * of the page. The upper-left corner of the annotation rectangle shall remain in a fixed location on the page,
     * regardless of the page rotation.
     * </li>
     * <li>{@link PdfAnnotation#NO_VIEW} - If set, do not display the annotation on the screen or allow it to interact
     * with the user. The annotation may be printed (depending on the setting of the Print flag) but should be considered
     * hidden for purposes of on-screen display and user interaction.
     * </li>
     * <li>{@link PdfAnnotation#READ_ONLY} -  If set, do not allow the annotation to interact with the user. The annotation
     * may be displayed or printed (depending on the settings of the NoView and Print flags) but should not respond to mouse
     * clicks or change its appearance in response to mouse motions.
     * </li>
     * <li>{@link PdfAnnotation#LOCKED} -  If set, do not allow the annotation to be deleted or its properties
     * (including position and size) to be modified by the user. However, this flag does not restrict changes to
     * the annotation’s contents, such as the value of a form field.
     * </li>
     * <li>{@link PdfAnnotation#TOGGLE_NO_VIEW} - If set, invert the interpretation of the NoView flag for certain events.
     * </li>
     * <li>{@link PdfAnnotation#LOCKED_CONTENTS} - If set, do not allow the contents of the annotation to be modified
     * by the user. This flag does not restrict deletion of the annotation or changes to other annotation properties,
     * such as position and size.
     * </li>
     * </ul>
     *
     * @param flag - an integer interpreted as set of one-bit flags which will be enabled for this annotation.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setFlag(int flag) {
        int flags = getFlags();
        flags = flags | flag;
        return setFlags(flags);
    }

    /**
     * Resets a flag that specifies a characteristic of the annotation to disabled state (see ISO-320001 12.5.3, "Annotation Flags").
     *
     * @param flag an integer interpreted as set of one-bit flags which will be reset to disabled state.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation resetFlag(int flag) {
        int flags = getFlags();
        flags = flags & ~flag;
        return setFlags(flags);
    }

    /**
     * Checks if the certain flag that specifies a characteristic of the annotation
     * is in enabled state (see ISO-320001 12.5.3, "Annotation Flags").
     * This method allows only one flag to be checked at once, use constants listed in {@link PdfAnnotation#setFlag(int)}.
     *
     * @param flag an integer interpreted as set of one-bit flags. Only one bit must be set in this integer, otherwise
     *             exception is thrown.
     * @return true if the given flag is in enabled state.
     */
    public boolean hasFlag(int flag) {
        if (flag == 0) {
            return false;
        }
        if ((flag & flag - 1) != 0) {
            throw new IllegalArgumentException("Only one flag must be checked at once.");
        }

        int flags = getFlags();
        return (flags & flag) != 0;
    }

    /**
     * An appearance dictionary specifying how the annotation shall be presented visually on the page during its
     * interactions with the user (see ISO-320001 12.5.5, "Appearance Streams"). An appearance dictionary is a dictionary
     * containing one or several appearance streams or subdictionaries.
     *
     * @return an appearance {@link PdfDictionary} or null if it is not specified.
     */
    public PdfDictionary getAppearanceDictionary() {
        return getPdfObject().getAsDictionary(PdfName.AP);
    }

    /**
     * Specific appearance object corresponding to the specific appearance type. This object might be either an appearance
     * stream or an appearance subdictionary. In the latter case, the subdictionary defines multiple appearance streams
     * corresponding to different appearance states of the annotation. See ISO-320001 12.5.5, "Appearance Streams".
     *
     * @param appearanceType a {@link PdfName} specifying appearance type. Possible types are {@link PdfName#N Normal},
     *                       {@link PdfName#R Rollover} and {@link PdfName#D Down}.
     * @return null if their is no such appearance type or an appearance object which might be either
     * an appearance stream or an appearance subdictionary.
     */
    public PdfDictionary getAppearanceObject(PdfName appearanceType) {
        PdfDictionary ap = getAppearanceDictionary();
        if (ap != null) {
            PdfObject apObject = ap.get(appearanceType);
            if (apObject instanceof PdfDictionary) {
                return (PdfDictionary) apObject;
            }
        }
        return null;
    }

    /**
     * The normal appearance is used when the annotation is not interacting with the user.
     * This appearance is also used for printing the annotation.
     * See also {@link PdfAnnotation#getAppearanceObject(PdfName)}.
     *
     * @return an appearance object which might be either an appearance stream or an appearance subdictionary.
     */
    public PdfDictionary getNormalAppearanceObject() {
        return getAppearanceObject(PdfName.N);
    }

    /**
     * The rollover appearance is used when the user moves the cursor into the annotation’s active area
     * without pressing the mouse button. If not specified normal appearance is used.
     * See also {@link PdfAnnotation#getAppearanceObject(PdfName)}.
     *
     * @return null if rollover appearance is not specified or an appearance object which might be either
     * an appearance stream or an appearance subdictionary.
     */
    public PdfDictionary getRolloverAppearanceObject() {
        return getAppearanceObject(PdfName.R);
    }

    /**
     * The down appearance is used when the mouse button is pressed or held down within the annotation’s active area.
     * If not specified normal appearance is used.
     * See also {@link PdfAnnotation#getAppearanceObject(PdfName)}.
     *
     * @return null if down appearance is not specified or an appearance object which might be either
     * an appearance stream or an appearance subdictionary.
     */
    public PdfDictionary getDownAppearanceObject() {
        return getAppearanceObject(PdfName.D);
    }

    /**
     * Sets a specific type of the appearance. See {@link PdfAnnotation#getAppearanceObject(PdfName)} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearanceType a {@link PdfName} specifying appearance type. Possible types are {@link PdfName#N Normal},
     *                       {@link PdfName#R Rollover} and {@link PdfName#D Down}.
     * @param appearance     an appearance object which might be either an appearance stream or an appearance subdictionary.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setAppearance(PdfName appearanceType, PdfDictionary appearance) {
        PdfDictionary ap = getAppearanceDictionary();
        if (ap == null) {
            ap = new PdfDictionary();
            getPdfObject().put(PdfName.AP, ap);
        }
        ap.put(appearanceType, appearance);
        return this;
    }

    /**
     * Sets normal appearance. See {@link PdfAnnotation#getNormalAppearanceObject()} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearance an appearance object which might be either an appearance stream or an appearance subdictionary.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setNormalAppearance(PdfDictionary appearance) {
        return setAppearance(PdfName.N, appearance);
    }

    /**
     * Sets rollover appearance. See {@link PdfAnnotation#getRolloverAppearanceObject()} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearance an appearance object which might be either an appearance stream or an appearance subdictionary.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setRolloverAppearance(PdfDictionary appearance) {
        return setAppearance(PdfName.R, appearance);
    }

    /**
     * Sets down appearance. See {@link PdfAnnotation#getDownAppearanceObject()} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearance an appearance object which might be either an appearance stream or an appearance subdictionary.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setDownAppearance(PdfDictionary appearance) {
        return setAppearance(PdfName.D, appearance);
    }

    /**
     * Sets a specific type of the appearance using {@link PdfAnnotationAppearance} wrapper.
     * This method is used to set only an appearance subdictionary. See {@link PdfAnnotation#getAppearanceObject(PdfName)}
     * and {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearanceType a {@link PdfName} specifying appearance type. Possible types are {@link PdfName#N Normal},
     *                       {@link PdfName#R Rollover} and {@link PdfName#D Down}.
     * @param appearance     an appearance subdictionary wrapped in {@link PdfAnnotationAppearance}.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setAppearance(PdfName appearanceType, PdfAnnotationAppearance appearance) {
        return setAppearance(appearanceType, appearance.getPdfObject());
    }

    /**
     * Sets normal appearance using {@link PdfAnnotationAppearance} wrapper. This method is used to set only
     * appearance subdictionary. See {@link PdfAnnotation#getNormalAppearanceObject()} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearance an appearance subdictionary wrapped in {@link PdfAnnotationAppearance}.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setNormalAppearance(PdfAnnotationAppearance appearance) {
        return setAppearance(PdfName.N, appearance);
    }

    /**
     * Sets rollover appearance using {@link PdfAnnotationAppearance} wrapper. This method is used to set only
     * appearance subdictionary. See {@link PdfAnnotation#getRolloverAppearanceObject()} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearance an appearance subdictionary wrapped in {@link PdfAnnotationAppearance}.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setRolloverAppearance(PdfAnnotationAppearance appearance) {
        return setAppearance(PdfName.R, appearance);
    }

    /**
     * Sets down appearance using {@link PdfAnnotationAppearance} wrapper. This method is used to set only
     * appearance subdictionary. See {@link PdfAnnotation#getDownAppearanceObject()} and
     * {@link PdfAnnotation#getAppearanceDictionary()} for more info.
     *
     * @param appearance an appearance subdictionary wrapped in {@link PdfAnnotationAppearance}.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setDownAppearance(PdfAnnotationAppearance appearance) {
        return setAppearance(PdfName.D, appearance);
    }

    /**
     * The annotation’s appearance state, which selects the applicable appearance stream
     * from an appearance subdictionary if there is such. See {@link PdfAnnotation#getAppearanceObject(PdfName)}
     * for more info.
     *
     * @return a {@link PdfName} which defines selected appearance state.
     */
    public PdfName getAppearanceState() {
        return getPdfObject().getAsName(PdfName.AS);
    }

    /**
     * Sets the annotation’s appearance state, which selects the applicable appearance stream
     * from an appearance subdictionary. See {@link PdfAnnotation#getAppearanceObject(PdfName)}
     * for more info.
     *
     * @param as a {@link PdfName} which defines appearance state to be selected.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setAppearanceState(PdfName as) {
        return put(PdfName.AS, as);
    }

    /**
     * <p>
     * An array specifying the characteristics of the annotation’s border.
     * The array consists of three numbers defining the horizontal corner radius,
     * vertical corner radius, and border width, all in default user space units.
     * If the corner radii are 0, the border has square (not rounded) corners; if
     * the border width is 0, no border is drawn.
     * <p>
     * The array may have a fourth element, an optional dash array (see ISO-320001 8.4.3.6, "Line Dash Pattern").
     *
     * @return an {@link PdfArray} specifying the characteristics of the annotation’s border.
     */
    public PdfArray getBorder() {
        return getPdfObject().getAsArray(PdfName.Border);
    }

    /**
     * Sets the characteristics of the annotation’s border.
     *
     * @param border an {@link PdfAnnotationBorder} specifying the characteristics of the annotation’s border.
     *               See {@link PdfAnnotation#getBorder()} for more detailes.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setBorder(PdfAnnotationBorder border) {
        return put(PdfName.Border, border.getPdfObject());
    }

    /**
     * Sets the characteristics of the annotation’s border.
     *
     * @param border an {@link PdfArray} specifying the characteristics of the annotation’s border.
     *               See {@link PdfAnnotation#getBorder()} for more detailes.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setBorder(PdfArray border) {
        return put(PdfName.Border, border);
    }

    /**
     * An array of numbers in the range 0.0 to 1.0, representing a colour used for the following purposes:
     * <ul>
     * <li>The background of the annotation’s icon when closed</li>
     * <li>The title bar of the annotation’s pop-up window</li>
     * <li>The border of a link annotation</li>
     * </ul>
     * The number of array elements determines the colour space in which the colour shall be defined:
     * <ul>
     * <li>0 - No colour; transparent</li>
     * <li>1 - DeviceGray</li>
     * <li>3 - DeviceRGB</li>
     * <li>4 - DeviceCMYK</li>
     * </ul>
     *
     * @return An array of numbers in the range 0.0 to 1.0, representing an annotation colour.
     */
    public PdfArray getColorObject() {
        return getPdfObject().getAsArray(PdfName.C);
    }

    /**
     * Sets an annotation color. For more details on annotation color purposes and the format
     * of the passing {@link PdfArray} see {@link PdfAnnotation#getColorObject()}.
     *
     * @param color an array of numbers in the range 0.0 to 1.0, specifying color.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setColor(PdfArray color) {
        return put(PdfName.C, color);
    }

    /**
     * Sets an annotation color. For more details on annotation color purposes and the format
     * of the passing array see {@link PdfAnnotation#getColorObject()}.
     *
     * @param color an array of numbers in the range 0.0 to 1.0, specifying color.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setColor(float[] color) {
        return setColor(new PdfArray(color));
    }

    /**
     * Sets an annotation color. For more details on annotation color purposes
     * see {@link PdfAnnotation#getColorObject()}.
     *
     * @param color {@link Color} object of the either {@link DeviceGray},
     *              {@link DeviceRgb} or  {@link DeviceCmyk} type.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setColor(Color color) {
        return setColor(new PdfArray(color.getColorValue()));
    }

    /**
     * The integer key of the annotation’s entry in the structural parent tree
     * (see ISO-320001 14.7.4.4, "Finding Structure Elements from Content Items").
     *
     * @return integer key in structural parent tree or -1 if annotation is not tagged.
     */
    public int getStructParentIndex() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.StructParent);
        if (n == null)
            return -1;
        else
            return n.intValue();
    }

    /**
     * Sets he integer key of the annotation’s entry in the structural parent tree
     * (see ISO-320001 14.7.4.4, "Finding Structure Elements from Content Items").
     * Note: Normally, there is no need to take care of this manually, struct parent index is set automatically
     * if annotation is added to the tagged document's page.
     *
     * @param structParentIndex integer which is to be the key of the annotation's entry
     *                          in structural parent tree.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setStructParentIndex(int structParentIndex) {
        return put(PdfName.StructParent, new PdfNumber(structParentIndex));
    }

    /**
     * Sets annotation title. This property affects not all annotation types.
     *
     * @param title a {@link PdfString} which value is to be annotation title.
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setTitle(PdfString title) {
        return put(PdfName.T, title);
    }

    /**
     * Annotation title. For example for markup annotations, the title is the text label that shall be displayed in the
     * title bar of the annotation’s pop-up window when open and active. For movie annotation Movie actions
     * (ISO-320001 12.6.4.9, "Movie Actions") may use this title to reference the movie annotation.
     *
     * @return {@link PdfString} which value is an annotation title or null if it isn't specifed.
     */
    public PdfString getTitle() {
        return getPdfObject().getAsString(PdfName.T);
    }

    /**
     * The annotation rectangle, defining the location of the annotation on the page in default user space units.
     *
     * @param array a {@link PdfArray} which specifies a rectangle by two diagonally opposite corners.
     *              Typically, the array is of form [llx lly urx ury].
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation setRectangle(PdfArray array) {
        return put(PdfName.Rect, array);
    }

    /**
     * The annotation rectangle, defining the location of the annotation on the page in default user space units.
     *
     * @return a {@link PdfArray} which specifies a rectangle by two diagonally opposite corners.
     * Typically, the array is of form [llx lly urx ury].
     */
    public PdfArray getRectangle() {
        return getPdfObject().getAsArray(PdfName.Rect);
    }

    /**
     * PDF 2.0. A language identifier overriding the document’s language identifier to
     * specify the natural language for all text in the annotation except where overridden by
     * other explicit language specifications
     *
     * @return the lang entry
     */
    public String getLang() {
        PdfString lang = getPdfObject().getAsString(PdfName.Lang);
        return lang != null ? lang.toUnicodeString() : null;
    }

    /**
     * PDF 2.0. A language identifier overriding the document’s language identifier to
     * specify the natural language for all text in the annotation except where overridden by
     * other explicit language specifications
     *
     * @param lang language identifier
     * @return this {@link PdfAnnotation} instance
     */
    public PdfAnnotation setLang(String lang) {
        return put(PdfName.Lang, new PdfString(lang, PdfEncodings.UNICODE_BIG));
    }

    /**
     * PDF 2.0. The blend mode that shall be used when painting the annotation onto the page
     *
     * @return the blend mode
     */
    public PdfName getBlendMode() {
        return getPdfObject().getAsName(PdfName.BM);
    }

    /**
     * PDF 2.0. The blend mode that shall be used when painting the annotation onto the page
     *
     * @param blendMode blend mode
     * @return this {@link PdfAnnotation} instance
     */
    public PdfAnnotation setBlendMode(PdfName blendMode) {
        return put(PdfName.BM, blendMode);
    }

    /**
     * PDF 2.0. When regenerating the annotation's appearance stream, this is the
     * opacity value that shall be used for all nonstroking
     * operations on all visible elements of the annotation in its closed state (including its
     * background and border) but not the popup window that appears when the annotation is
     * opened.
     *
     * @return opacity value for nonstroking operations. Returns 1.0 (default value) if entry is not present
     */
    public float getNonStrokingOpacity() {
        PdfNumber nonStrokingOpacity = getPdfObject().getAsNumber(PdfName.ca);
        return nonStrokingOpacity != null ? nonStrokingOpacity.floatValue() : 1;
    }

    /**
     * PDF 2.0. When regenerating the annotation's appearance stream, this is the
     * opacity value that shall be used for all nonstroking
     * operations on all visible elements of the annotation in its closed state (including its
     * background and border) but not the popup window that appears when the annotation is
     * opened.
     *
     * @param nonStrokingOpacity opacity for nonstroking operations
     * @return this {@link PdfAnnotation} instance
     */
    public PdfAnnotation setNonStrokingOpacity(float nonStrokingOpacity) {
        return put(PdfName.ca, new PdfNumber(nonStrokingOpacity));
    }

    /**
     * PDF 2.0. When regenerating the annotation's appearance stream, this is the
     * opacity value that shall be used for stroking all visible
     * elements of the annotation in its closed state, including its background and border, but
     * not the popup window that appears when the annotation is opened.
     *
     * @return opacity for stroking operations, including background and border
     */
    public float getStrokingOpacity() {
        PdfNumber strokingOpacity = getPdfObject().getAsNumber(PdfName.CA);
        return strokingOpacity != null ? strokingOpacity.floatValue() : 1;
    }

    /**
     * PDF 2.0. When regenerating the annotation's appearance stream, this is the
     * opacity value that shall be used for stroking all visible
     * elements of the annotation in its closed state, including its background and border, but
     * not the popup window that appears when the annotation is opened.
     *
     * @param strokingOpacity opacity for stroking operations, including background and border
     * @return this {@link PdfAnnotation} object
     */
    public PdfAnnotation setStrokingOpacity(float strokingOpacity) {
        return put(PdfName.CA, new PdfNumber(strokingOpacity));
    }

    /**
     * Inserts the value into into the underlying {@link PdfDictionary} of this {@link PdfAnnotation} and associates it
     * with the specified key. If the key is already present in this {@link PdfAnnotation}, this method will override
     * the old value with the specified one.
     *
     * @param key   key to insert or to override
     * @param value the value to associate with the specified key
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    /**
     * Removes the specified key from the underlying {@link PdfDictionary} of this {@link PdfAnnotation}.
     *
     * @param key key to be removed
     * @return this {@link PdfAnnotation} instance.
     */
    public PdfAnnotation remove(PdfName key) {
        getPdfObject().remove(key);
        return this;
    }

    /**
     * <p>
     * Adds file associated with PDF annotation and identifies the relationship between them.
     * </p>
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the annotation dictionary.
     * </p>
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     * </p>
     *
     * @param fs file specification dictionary of associated file
     */
    public void addAssociatedFile(PdfFileSpec fs) {
        if (null == ((PdfDictionary) fs.getPdfObject()).get(PdfName.AFRelationship)) {
            Logger logger = LoggerFactory.getLogger(PdfAnnotation.class);
            logger.error(LogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
        }
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = new PdfArray();
            put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());
    }

    /**
     * Returns files associated with PDF annotation.
     *
     * @param create iText will create AF array if it doesn't exist and create value is true
     * @return associated files array.
     */
    public PdfArray getAssociatedFiles(boolean create) {
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null && create) {
            afArray = new PdfArray();
            put(PdfName.AF, afArray);
        }
        return afArray;
    }


    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
