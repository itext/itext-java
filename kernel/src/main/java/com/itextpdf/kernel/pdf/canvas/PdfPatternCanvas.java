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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;

/**
 * A PdfCanvas instance with an inherent tiling pattern.
 */
public class PdfPatternCanvas extends PdfCanvas {

    private static final long serialVersionUID = -8325687042148621178L;
    private final PdfPattern.Tiling tilingPattern;

    /**
     * Creates PdfPatternCanvas from content stream of page, form XObject, pattern etc.
     *
     * @param contentStream The content stream
     * @param resources     The resources, a specialized dictionary that can be used by PDF instructions in the content stream
     * @param document      The document that the resulting content stream will be written to
     */
    public PdfPatternCanvas(PdfStream contentStream, PdfResources resources, PdfDocument document) {
        super(contentStream, resources, document);
        this.tilingPattern = new PdfPattern.Tiling(contentStream);
    }

    /**
     * Creates PdfPatternCanvas for a document from a provided Tiling pattern
     * @param pattern   The Tiling pattern must be colored
     * @param document  The document that the resulting content stream will be written to
     */
    public PdfPatternCanvas(PdfPattern.Tiling pattern, PdfDocument document) {
        super((PdfStream) pattern.getPdfObject(), pattern.getResources(), document);
        this.tilingPattern = pattern;
    }

    @Override
    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern, boolean fill) {
        checkNoColor();
        return super.setColor(colorSpace, colorValue, pattern, fill);
    }

    private void checkNoColor() {
        if (!tilingPattern.isColored()) {
            throw new PdfException(PdfException.ContentStreamMustNotInvokeOperatorsThatSpecifyColorsOrOtherColorRelatedParameters);
        }
    }
}
