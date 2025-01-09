/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import java.util.Collection;

/**
 * {@link PdfTextArray} defines an array with displacements and {@link PdfString}-objects.
 * <P>
 * A {@link PdfTextArray} is used with the operator TJ in {@link PdfCanvas}.
 * The first object in this array has to be a {@link PdfString};
 * see reference manual version 1.3 section 8.7.5, pages 346-347.
 *       OR
 * see reference manual version 1.6 section 5.3.2, pages 378-379.
 * To emit a more efficient array, we consolidate repeated numbers or strings into single array entries.
 * For example: "add( 50 ); add( -50 );" will REMOVE the combined zero from the array.
 */
public class PdfTextArray extends PdfArray {


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
                    lastNumber = Float.NaN;
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
