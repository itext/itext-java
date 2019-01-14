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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the most common properties of color spaces.
 */
public abstract class PdfColorSpace extends PdfObjectWrapper<PdfObject> {

    public static final Set<PdfName> directColorSpaces = new HashSet<>(Arrays.asList(PdfName.DeviceGray, PdfName.DeviceRGB, PdfName.DeviceCMYK, PdfName.Pattern));

    private static final long serialVersionUID = 2553991039779429813L;

    protected PdfColorSpace(PdfObject pdfObject) {
        super(pdfObject);
    }

    public abstract int getNumberOfComponents();

    public static PdfColorSpace makeColorSpace(PdfObject pdfObject) {
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isArray() && ((PdfArray) pdfObject).size() == 1)
            pdfObject = ((PdfArray) pdfObject).get(0);
        if (PdfName.DeviceGray.equals(pdfObject))
            return new PdfDeviceCs.Gray();
        else if (PdfName.DeviceRGB.equals(pdfObject))
            return new PdfDeviceCs.Rgb();
        else if (PdfName.DeviceCMYK.equals(pdfObject))
            return new PdfDeviceCs.Cmyk();
        else if (PdfName.Pattern.equals(pdfObject))
            return new PdfSpecialCs.Pattern();
        else if (pdfObject.isArray()) {
            PdfArray array = (PdfArray) pdfObject;
            PdfName csType = array.getAsName(0);
            if (PdfName.CalGray.equals(csType))
                return new PdfCieBasedCs.CalGray(array);
            else if (PdfName.CalRGB.equals(csType))
                return new PdfCieBasedCs.CalRgb(array);
            else if (PdfName.Lab.equals(csType))
                return new PdfCieBasedCs.Lab(array);
            else if (PdfName.ICCBased.equals(csType))
                return new PdfCieBasedCs.IccBased(array);
            else if (PdfName.Indexed.equals(csType))
                return new PdfSpecialCs.Indexed(array);
            else if (PdfName.Separation.equals(csType))
                return new PdfSpecialCs.Separation(array);
            else if (PdfName.DeviceN.equals(csType))
                return array.size() == 4 ? new PdfSpecialCs.DeviceN(array) : new PdfSpecialCs.NChannel(array);
            else if (PdfName.Pattern.equals(csType))
                return new PdfSpecialCs.UncoloredTilingPattern(array);
        }
        return null;
    }

}
