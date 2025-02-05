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
