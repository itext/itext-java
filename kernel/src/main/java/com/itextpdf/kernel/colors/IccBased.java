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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

import java.io.InputStream;

public class IccBased extends Color {

    private static final long serialVersionUID = -2204252409856288615L;

    public IccBased(PdfCieBasedCs.IccBased cs) {
        this(cs, new float[cs.getNumberOfComponents()]); // TODO if zero if outside of the Range, default value should be the nearest to the zero valid value
    }

    public IccBased(PdfCieBasedCs.IccBased cs, float[] value) {
        super(cs, value);
    }

    /**
     * Creates IccBased color.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     */
    public IccBased(InputStream iccStream) {
        this(new PdfCieBasedCs.IccBased(iccStream), null);
        colorValue = new float[getNumberOfComponents()];
        for (int i = 0; i < getNumberOfComponents(); i++)
            colorValue[i] = 0f;
    }

    /**
     * Creates IccBased color.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @param value     color value.
     */
    public IccBased(InputStream iccStream, float[] value) {
        this(new PdfCieBasedCs.IccBased(iccStream), value);
    }

    public IccBased(InputStream iccStream, float[] range, float[] value) {
        this(new PdfCieBasedCs.IccBased(iccStream, range), value);
        if (getNumberOfComponents() * 2 != range.length)
            throw new PdfException(PdfException.InvalidRangeArray, this);
    }
}
