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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the most common properties of color spaces.
 */
public abstract class PdfColorSpace extends PdfObjectWrapper<PdfObject> {
    public static final Set<PdfName> DIRECT_COLOR_SPACES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(PdfName.DeviceGray, PdfName.DeviceRGB, PdfName.DeviceCMYK, PdfName.Pattern)
    ));

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
                //TODO DEVSIX-4205 Fix colorspace creation
                return array.size() == 4 ? new PdfSpecialCs.DeviceN(array) : new PdfSpecialCs.NChannel(array);
            else if (PdfName.Pattern.equals(csType))
                return new PdfSpecialCs.UncoloredTilingPattern(array);
        }
        return null;
    }

}
