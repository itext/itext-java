/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.PdfFunction;

public class Separation extends Color {

    private static final long serialVersionUID = 5995354549050682283L;

    public Separation(PdfSpecialCs.Separation cs) {
        this(cs, 1f);
    }

    public Separation(PdfSpecialCs.Separation cs, float value) {
        super(cs, new float[]{value});
    }

    public Separation(String name, PdfColorSpace alternateCs, PdfFunction tintTransform, float value) {
        this(new PdfSpecialCs.Separation(name, alternateCs, tintTransform), value);
    }

}
