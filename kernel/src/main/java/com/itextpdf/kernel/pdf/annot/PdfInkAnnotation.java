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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

public class PdfInkAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = 9110158515957708155L;

	public PdfInkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfInkAnnotation(Rectangle rect, PdfArray inkList) {
        this(rect);
        put(PdfName.InkList, inkList);
    }

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfInkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Ink;
    }

    /**
     * The dictionaries for some annotation types (such as free text and polygon annotations) can include the BS entry.
     * That entry specifies a border style dictionary that has more settings than the array specified for the Border
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
     * @return this {@link PdfInkAnnotation} instance.
     */
    public PdfInkAnnotation setBorderStyle(PdfDictionary borderStyle) {
        return (PdfInkAnnotation) put(PdfName.BS, borderStyle);
    }

    /**
     * Setter for the annotation's preset border style. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#STYLE_SOLID} - A solid rectangle surrounding the annotation.</li>
     *     <li>{@link PdfAnnotation#STYLE_DASHED} - A dashed rectangle surrounding the annotation.</li>
     *     <li>{@link PdfAnnotation#STYLE_BEVELED} - A simulated embossed rectangle that appears to be raised above the surface of the page.</li>
     *     <li>{@link PdfAnnotation#STYLE_INSET} - A simulated engraved rectangle that appears to be recessed below the surface of the page.</li>
     *     <li>{@link PdfAnnotation#STYLE_UNDERLINE} - A single line along the bottom of the annotation rectangle.</li>
     * </ul>
     * See also ISO-320001, Table 166.
     * @param style The new value for the annotation's border style.
     * @return this {@link PdfInkAnnotation} instance.
     * @see #getBorderStyle()
     */
    public PdfInkAnnotation setBorderStyle(PdfName style) {
        return setBorderStyle(BorderStyleUtil.setStyle(getBorderStyle(), style));
    }

    /**
     * Setter for the annotation's preset dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for the annotation border style (see {@link #setBorderStyle(PdfName)}.
     * See ISO-320001 8.4.3.6, “Line Dash Pattern” for the format in which dash pattern shall be specified.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return this {@link PdfInkAnnotation} instance.
     */
    public PdfInkAnnotation setDashPattern(PdfArray dashPattern) {
        return setBorderStyle(BorderStyleUtil.setDashPattern(getBorderStyle(), dashPattern));
    }
}
