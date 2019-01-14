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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for optional content states use in Set-OCG-State actions.
 * See {@link PdfAction#createSetOcgState}.
 */
public class PdfActionOcgState {

    /**
     * Can be: {@link PdfName#OFF}, {@link PdfName#ON}, {@link PdfName#Toggle}
     */
    private PdfName state;

    /**
     * Optional content group dictionaries
     */
    private List<PdfDictionary> ocgs;

    /**
     * Constructs an optional content state object.
     *
     * @param state a {@link PdfName} describing the state. Shall be one of the following:
     *              {@link PdfName#OFF}, {@link PdfName#ON}, {@link PdfName#Toggle}
     * @param ocgs  a list of the OCG dictionaries
     */
    public PdfActionOcgState(PdfName state, List<PdfDictionary> ocgs) {
        this.state = state;
        this.ocgs = ocgs;
    }

    /**
     * Gets the state the optional content groups should be switched to
     *
     * @return the state, one of the following: {@link PdfName#OFF}, {@link PdfName#ON}, {@link PdfName#Toggle}
     */
    public PdfName getState() {
        return state;
    }

    /**
     * Gets a list of optional content groups that shall have the state changed
     *
     * @return the list of optional content groups
     */
    public List<PdfDictionary> getOcgs() {
        return ocgs;
    }

    /**
     * Gets a list of {@link PdfObject} that is describing this particular optional content group states.
     *
     * @return a list of {@link PdfObject} for construction of a {@link com.itextpdf.kernel.pdf.PdfArray}
     */
    public List<PdfObject> getObjectList() {
        List<PdfObject> states = new ArrayList<>();
        states.add(state);
        states.addAll(ocgs);
        return states;
    }
}
