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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

public class PdfTransparencyGroup extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 753843601750097627L;

	public PdfTransparencyGroup() {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.S, PdfName.Transparency);
    }

    /**
     * Determining the initial backdrop against which its stack is composited.
     * @param isolated
     */
    public void setIsolated(boolean isolated) {
        if (isolated) {
            getPdfObject().put(PdfName.I, PdfBoolean.TRUE);
        } else {
            getPdfObject().remove(PdfName.I);
        }
    }

    /**
     * Determining whether the objects within the stack are composited with one another or only with the group's backdrop.
     * @param knockout
     */
    public void setKnockout(boolean knockout) {
        if (knockout) {
            getPdfObject().put(PdfName.K, PdfBoolean.TRUE);
        } else {
            getPdfObject().remove(PdfName.K);
        }
    }

    public void setColorSpace(PdfName colorSpace) {
        getPdfObject().put(PdfName.CS, colorSpace);
    }

    public void setColorSpace(PdfArray colorSpace) {
        getPdfObject().put(PdfName.CS, colorSpace);
    }

    public PdfTransparencyGroup put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
