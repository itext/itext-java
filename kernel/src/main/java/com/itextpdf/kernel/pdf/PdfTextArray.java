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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.font.PdfFont;
import java.util.Collection;

/**
 * <CODE>PdfTextArray</CODE> defines an array with displacements and <CODE>PdfString</CODE>-objects.
 * <P>
 * A <CODE>PdfTextArray</CODE> is used with the operator <VAR>TJ</VAR> in <CODE>PdfCanvas</CODE>.
 * The first object in this array has to be a <CODE>PdfString</CODE>;
 * see reference manual version 1.3 section 8.7.5, pages 346-347.
 *       OR
 * see reference manual version 1.6 section 5.3.2, pages 378-379.
 * To emit a more efficient array, we consolidate repeated numbers or strings into single array entries.
 * "add( 50 ); add( -50 );" will REMOVE the combined zero from the array.
 */
public class PdfTextArray extends PdfArray {

    private static final long serialVersionUID = 2555632135770071680L;
	
    private float lastNumber = Float.NaN;
    private StringBuilder lastString;

    @Override
    public void add(PdfObject pdfObject) {
        if (pdfObject.isNumber()) {
            add(((PdfNumber)pdfObject).floatValue());
        } else if (pdfObject instanceof PdfString) {
            add(((PdfString)pdfObject).getValueBytes());
        }
    }

    /**
     * Adds content of the {@code PdfArray}.
     *
     * @param a the {@code PdfArray} to be added
     * @see java.util.List#addAll(java.util.Collection)
     */
    public void addAll(PdfArray a) {
        if (a != null) {
            addAll(a.list);
        }
    }

    /**
     * Adds the Collection of PdfObjects.
     *
     * @param c the Collection of PdfObjects to be added
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public void addAll(Collection<PdfObject> c) {
        for (PdfObject obj : c) {
            add(obj);
        }
    }

    public boolean add(float number) {
        // adding zero doesn't modify the TextArray at all
        if (number != 0) {
            if (!Float.isNaN(lastNumber)) {
                lastNumber = number + lastNumber;
                if (lastNumber != 0) {
                    set(size() - 1, new PdfNumber(lastNumber));
                } else {
                    remove(size() - 1);
                }
            } else {
                lastNumber = number;
                super.add(new PdfNumber(lastNumber));
            }
            lastString = null;
            return true;
        }
        return false;
    }

    public boolean add(String text, PdfFont font) {
        // adding an empty string doesn't modify the TextArray at all
        return add(font.convertToBytes(text));
    }

    public boolean add(byte[] text) {
        return add(new PdfString(text).getValue());
    }

    protected boolean add(String text) {
        if (text.length() > 0) {
            if (lastString != null) {
                lastString.append(text);
                set(size() - 1, new PdfString(lastString.toString()));
            } else {
                lastString = new StringBuilder(text);
                super.add(new PdfString(lastString.toString()));
            }
            lastNumber = Float.NaN;
            return true;
        }
        return false;
    }

}
