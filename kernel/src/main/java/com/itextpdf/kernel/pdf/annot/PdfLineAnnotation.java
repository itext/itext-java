/*

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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

public class PdfLineAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -6047928061827404283L;

	public PdfLineAnnotation(Rectangle rect, float[] line) {
        super(rect);
        put(PdfName.L, new PdfArray(line));
    }

    public PdfLineAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Line;
    }

    public PdfArray getLine() {
        return getPdfObject().getAsArray(PdfName.L);
    }

    public PdfArray getLineEndingStyles() {
        return getPdfObject().getAsArray(PdfName.LE);
    }

    public PdfLineAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return (PdfLineAnnotation) put(PdfName.LE, lineEndingStyles);
    }

    public float getLeaderLine() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LE);
        return n == null ? 0 : n.floatValue();
    }

    public PdfLineAnnotation setLeaderLine(float leaderLine) {
        return (PdfLineAnnotation) put(PdfName.LE, new PdfNumber(leaderLine));
    }

    public float getLeaderLineExtension() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LLE);
        return n == null ? 0 : n.floatValue();
    }

    public PdfLineAnnotation setLeaderLineExtension(float leaderLineExtension) {
        return (PdfLineAnnotation) put(PdfName.LLE, new PdfNumber(leaderLineExtension));
    }

    public float getLeaderLineOffset() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LLO);
        return n == null ? 0 : n.floatValue();
    }

    public PdfLineAnnotation setLeaderLineOffset(float leaderLineOffset) {
        return (PdfLineAnnotation) put(PdfName.LLO, new PdfNumber(leaderLineOffset));
    }

    public boolean getContentsAsCaption() {
        PdfBoolean b = getPdfObject().getAsBoolean(PdfName.Cap);
        return b != null && b.getValue();
    }

    public PdfLineAnnotation setContentsAsCaption(boolean contentsAsCaption) {
        return (PdfLineAnnotation) put(PdfName.Cap, new PdfBoolean(contentsAsCaption));
    }

    public PdfName getCaptionPosition() {
        return getPdfObject().getAsName(PdfName.CP);
    }

    public PdfLineAnnotation setCaptionPosition(PdfName captionPosition) {
        return (PdfLineAnnotation) put(PdfName.CP, captionPosition);
    }

    public PdfDictionary getMeasure() {
        return getPdfObject().getAsDictionary(PdfName.Measure);
    }

    public PdfLineAnnotation setMeasure(PdfDictionary measure) {
        return (PdfLineAnnotation) put(PdfName.Measure, measure);
    }

    public PdfArray getCaptionOffset() {
        return getPdfObject().getAsArray(PdfName.CO);
    }

    public PdfLineAnnotation setCaptionOffset(PdfArray captionOffset) {
        return (PdfLineAnnotation) put(PdfName.CO, captionOffset);
    }

    public PdfLineAnnotation setCaptionOffset(float[] captionOffset) {
        return setCaptionOffset(new PdfArray(captionOffset));
    }

}
