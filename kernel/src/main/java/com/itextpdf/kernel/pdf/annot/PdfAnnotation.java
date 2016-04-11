/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.layer.PdfOCG;

public abstract class PdfAnnotation extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -6555705164241587799L;
	
    /**
     * Annotation flags.
     */
    static public final int Invisible = 1;
    static public final int Hidden = 2;
    static public final int Print = 4;
    static public final int NoZoom = 8;
    static public final int NoRotate = 16;
    static public final int NoView = 32;
    static public final int ReadOnly = 64;
    static public final int Locked = 128;
    static public final int ToggleNoView = 256;
    static public final int LockedContents = 512;

    /**
     * Annotation highlighting modes.
     */
    public static final PdfName HIGHLIGHT_NONE = PdfName.N;
    public static final PdfName HIGHLIGHT_INVERT = PdfName.I;
    public static final PdfName HIGHLIGHT_OUTLINE = PdfName.O;
    public static final PdfName HIGHLIGHT_PUSH = PdfName.P;
    public static final PdfName HIGHLIGHT_TOGGLE = PdfName.T;

    /**
     * Annotation states.
     */
    static public final PdfString Marked = new PdfString("Marked");
    static public final PdfString Unmarked = new PdfString("Unmarked");
    static public final PdfString Accepted = new PdfString("Accepted");
    static public final PdfString Rejected = new PdfString("Rejected");
    static public final PdfString Canceled = new PdfString("Cancelled");
    static public final PdfString Completed = new PdfString("Completed");
    static public final PdfString None = new PdfString("None");

    /**
     * Annotation state models.
     */
    static public final PdfString MarkedModel = new PdfString("Marked");
    static public final PdfString ReviewModel = new PdfString("Review");

    protected PdfPage page;

    public static <T extends PdfAnnotation> T makeAnnotation(PdfObject pdfObject, PdfAnnotation parent) {
        T annotation = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName subtype = dictionary.getAsName(PdfName.Subtype);
            if (PdfName.Link.equals(subtype))
                annotation = (T) new PdfLinkAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Popup.equals(subtype))
                annotation = (T) new PdfPopupAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Widget.equals(subtype))
                annotation = (T) new PdfWidgetAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Screen.equals(subtype))
                annotation = (T) new PdfScreenAnnotation((PdfDictionary) pdfObject);
            else if (PdfName._3D.equals(subtype))
                throw new UnsupportedOperationException();
            else if (PdfName.Highlight.equals(subtype) || PdfName.Underline.equals(subtype) || PdfName.Squiggly.equals(subtype) || PdfName.StrikeOut.equals(subtype))
                annotation = (T) new PdfTextMarkupAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Caret.equals(subtype))
                annotation = (T) new PdfCaretAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Text.equals(subtype))
                annotation = (T) new PdfTextAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Sound.equals(subtype))
                annotation = (T) new PdfSoundAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Stamp.equals(subtype))
                annotation = (T) new PdfStampAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.FileAttachment.equals(subtype))
                annotation = (T) new PdfFileAttachmentAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Ink.equals(subtype))
                annotation = (T) new PdfInkAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.PrinterMark.equals(subtype))
                annotation = (T) new PdfPrinterMarkAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.TrapNet.equals(subtype))
                annotation = (T) new PdfTrapNetworkAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.FreeText.equals(subtype))
                annotation = (T) new PdfFreeTextAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Square.equals(subtype))
                annotation = (T) new PdfSquareAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Circle.equals(subtype))
                annotation = (T) new PdfCircleAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Line.equals(subtype))
                annotation = (T) new PdfLineAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Polygon.equals(subtype) || PdfName.PolyLine.equals(subtype))
                annotation = (T) new PdfPolyGeomAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Redact.equals(subtype))
                annotation = (T) new PdfRedactAnnotation((PdfDictionary) pdfObject);
            else if (PdfName.Watermark.equals(subtype))
                annotation = (T) new PdfWatermarkAnnotation((PdfDictionary) pdfObject);
        }
        if (annotation instanceof PdfMarkupAnnotation) {
            PdfMarkupAnnotation markup = (PdfMarkupAnnotation) annotation;
            PdfDictionary inReplyTo = markup.getInReplyToObject();
            if (inReplyTo != null)
                markup.setInReplyTo(makeAnnotation(inReplyTo));
            PdfDictionary popup = markup.getPopupObject();
            if (popup != null)
                markup.setPopup((PdfPopupAnnotation) makeAnnotation(popup, markup));
        }
        if (annotation instanceof PdfPopupAnnotation) {
            PdfPopupAnnotation popup = (PdfPopupAnnotation) annotation;
            if (parent != null)
                popup.setParent(parent);
        }

        return annotation;
    }

    public PdfAnnotation(Rectangle rect) {
        this(new PdfDictionary());
        put(PdfName.Rect, new PdfArray(rect));
        put(PdfName.Subtype, getSubtype());
    }

    public PdfAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
        markObjectAsIndirect(getPdfObject());
    }

    public abstract PdfName getSubtype();

    /**
     * Sets the layer this annotation belongs to.
     *
     * @param layer the layer this annotation belongs to
     */
    public void setLayer(final PdfOCG layer) {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

    public <T extends PdfAnnotation> T setAction(PdfAction action) {
        return put(PdfName.A, action.getPdfObject());
    }

    public <T extends PdfAnnotation> T setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    public PdfString getContents() {
        return getPdfObject().getAsString(PdfName.Contents);
    }

    public <T extends PdfAnnotation> T setContents(PdfString contents) {
        return put(PdfName.Contents, contents);
    }

    public <T extends PdfAnnotation> T setContents(String contents) {
        return setContents(new PdfString(contents));
    }

    public PdfDictionary getPageObject() {
        return getPdfObject().getAsDictionary(PdfName.P);
    }

    public PdfPage getPage() {
        return page;
    }

    public <T extends PdfAnnotation> T setPage(PdfPage page) {
        this.page = page;
        return put(PdfName.P, page.getPdfObject());
    }

    public PdfString getName() {
        return getPdfObject().getAsString(PdfName.NM);
    }

    public <T extends PdfAnnotation> T setName(PdfString name) {
        return put(PdfName.NM, name);
    }

    public PdfString getDate() {
        return getPdfObject().getAsString(PdfName.M);
    }

    public <T extends PdfAnnotation> T setDate(PdfString date) {
        return put(PdfName.M, date);
    }

    public int getFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.F);
        if (f != null)
            return f.getIntValue();
        else
            return 0;
    }

    public <T extends PdfAnnotation> T setFlags(int flags) {
        return put(PdfName.F, new PdfNumber(flags));
    }

    public <T extends PdfAnnotation> T setFlag(int flag) {
        int flags = getFlags();
        flags = flags | flag;
        return setFlags(flags);
    }

    public <T extends PdfAnnotation> T resetFlag(int flag) {
        int flags = getFlags();
        flags = flags & (~flag & 0xff);
        return setFlags(flags);
    }

    public boolean hasFlag(int flag) {
        int flags = getFlags();
        return (flags & flag) != 0;
    }

    public PdfDictionary getAppearanceDictionary() {
        return getPdfObject().getAsDictionary(PdfName.AP);
    }

    public PdfDictionary getAppearanceObject(PdfName appearanceType) {
        PdfDictionary ap = getAppearanceDictionary();
        if (ap != null) {
            return ap.getAsDictionary(appearanceType);
        }
        return null;
    }

    public PdfDictionary getNormalAppearanceObject() {
        return getAppearanceObject(PdfName.N);
    }

    public PdfDictionary getRolloverAppearanceObject() {
        return getAppearanceObject(PdfName.R);
    }

    public PdfDictionary getDownAppearanceObject() {
        return getAppearanceObject(PdfName.D);
    }

    public <T extends PdfAnnotation> T setAppearance(PdfName appearanceType, PdfDictionary appearance) {
        PdfDictionary ap = getAppearanceDictionary();
        if (ap == null) {
            ap = new PdfDictionary();
            getPdfObject().put(PdfName.AP, ap);
        }
        ap.put(appearanceType, appearance);
        return (T) this;
    }

    public <T extends PdfAnnotation> T setNormalAppearance(PdfDictionary appearance) {
        return setAppearance(PdfName.N, appearance);
    }

    public <T extends PdfAnnotation> T setRolloverAppearance(PdfDictionary appearance) {
        return setAppearance(PdfName.R, appearance);
    }

    public <T extends PdfAnnotation> T setDownAppearance(PdfDictionary appearance) {
        return setAppearance(PdfName.D, appearance);
    }

    public <T extends PdfAnnotation> T setAppearance(PdfName appearanceType, PdfAnnotationAppearance appearance) {
        return setAppearance(appearanceType, appearance.getPdfObject());
    }

    public <T extends PdfAnnotation> T setNormalAppearance(PdfAnnotationAppearance appearance) {
        return setAppearance(PdfName.N, appearance);
    }

    public <T extends PdfAnnotation> T setRolloverAppearance(PdfAnnotationAppearance appearance) {
        return setAppearance(PdfName.R, appearance);
    }

    public <T extends PdfAnnotation> T setDownAppearance(PdfAnnotationAppearance appearance) {
        return setAppearance(PdfName.D, appearance);
    }

    public PdfName getAppearanceState() {
        return getPdfObject().getAsName(PdfName.AS);
    }

    public <T extends PdfAnnotation> T setAppearanceState(PdfName as) {
        return put(PdfName.AS, as);
    }

    public PdfArray getBorder() {
        return getPdfObject().getAsArray(PdfName.Border);
    }

    public <T extends PdfAnnotation> T setBorder(PdfArray border) {
        return put(PdfName.Border, border);
    }

    public PdfArray getColorObject() {
        return getPdfObject().getAsArray(PdfName.C);
    }

    public <T extends PdfAnnotation> T setColor(PdfArray color) {
        return put(PdfName.C, color);
    }

    public <T extends PdfAnnotation> T setColor(float[] color) {
        return setColor(new PdfArray(color));
    }

    public <T extends PdfAnnotation> T setColor(Color color) {
        return setColor(new PdfArray(color.getColorValue()));
    }

    public int getStructParentIndex() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.StructParent);
        if (n == null)
            return -1;
        else
            return n.getIntValue();
    }

    public <T extends PdfAnnotation> T setStructParentIndex(int structParentIndex) {
        return put(PdfName.StructParent, new PdfNumber(structParentIndex));
    }

    public PdfArray getQuadPoints() {
        return getPdfObject().getAsArray(PdfName.QuadPoints);
    }

    public boolean getOpen() {
        PdfBoolean open = getPdfObject().getAsBoolean(PdfName.Open);
        return open == null ? false : open.getValue();
    }

    public <T extends PdfAnnotation> T setOpen(boolean open) {
        return put(PdfName.Open, new PdfBoolean(open));
    }

    public <T extends PdfAnnotation> T setQuadPoints(PdfArray quadPoints) {
        return put(PdfName.QuadPoints, quadPoints);
    }

    public <T extends PdfAnnotation> T setBorderStyle(PdfDictionary borderStyle) {
        return put(PdfName.BS, borderStyle);
    }

    public PdfDictionary getBorderStyle() {
        return getPdfObject().getAsDictionary(PdfName.BS);
    }

    /**
     * Marks annotation to be tagged.
     * Normally it shall be done for link annotations.
     *
     * @param <T>
     * @return annotation itself.
     */
    public <T extends PdfAnnotation> T tag(PdfDocument pdfDocument) {
        return put(PdfName.StructParent, new PdfNumber(pdfDocument.getNextStructParentIndex()));
    }

    static public <T extends PdfAnnotation> T makeAnnotation(PdfObject pdfObject) {
        return makeAnnotation(pdfObject, null);
    }

    public <T extends PdfAnnotation> T setTitle(PdfString title) {
        return put(PdfName.T, title);
    }

    public PdfString getTitle() {
        return getPdfObject().getAsString(PdfName.T);
    }

    public <T extends PdfAnnotation> T setAppearanceCharacteristics(PdfDictionary characteristics) {
        return put(PdfName.MK, characteristics);
    }

    public PdfDictionary getAppearanceCharacteristics() {
        return getPdfObject().getAsDictionary(PdfName.MK);
    }

    public PdfDictionary getAction() {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    public <T extends PdfAnnotation> T setRectangle(PdfArray array){
        return put(PdfName.Rect, array);
    }

    public PdfArray getRectangle() {
        return getPdfObject().getAsArray(PdfName.Rect);
    }

    public <T extends PdfAnnotation> T put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return (T) this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
