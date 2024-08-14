/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.properties.XMPProperty;

/**
 * Enumeration of all the PDF/A conformance levels.
 */
public class PdfAConformanceLevel implements IConformanceLevel {

    public static final PdfAConformanceLevel PDF_A_1A = new PdfAConformanceLevel("1", "A");
    public static final PdfAConformanceLevel PDF_A_1B = new PdfAConformanceLevel("1", "B");
    public static final PdfAConformanceLevel PDF_A_2A = new PdfAConformanceLevel("2", "A");
    public static final PdfAConformanceLevel PDF_A_2B = new PdfAConformanceLevel("2", "B");
    public static final PdfAConformanceLevel PDF_A_2U = new PdfAConformanceLevel("2", "U");
    public static final PdfAConformanceLevel PDF_A_3A = new PdfAConformanceLevel("3", "A");
    public static final PdfAConformanceLevel PDF_A_3B = new PdfAConformanceLevel("3", "B");
    public static final PdfAConformanceLevel PDF_A_3U = new PdfAConformanceLevel("3", "U");
    public static final PdfAConformanceLevel PDF_A_4 = new PdfAConformanceLevel("4", null);
    public static final PdfAConformanceLevel PDF_A_4E = new PdfAConformanceLevel("4", "E");
    public static final PdfAConformanceLevel PDF_A_4F = new PdfAConformanceLevel("4", "F");
    public static final String PDF_A_4_REVISION = "2020";

    private final String conformance;
    private final String part;

    private PdfAConformanceLevel(String part, String conformance) {
        this.conformance = conformance;
        this.part = part;
    }

    public String getConformance() {
        return conformance;
    }

    public String getPart() {
        return part;
    }

    public static PdfAConformanceLevel getConformanceLevel(String part, String conformance) {
        String lowLetter = conformance == null ? null : conformance.toUpperCase();
        boolean aLevel = "A".equals(lowLetter);
        boolean bLevel = "B".equals(lowLetter);
        boolean uLevel = "U".equals(lowLetter);
        boolean eLevel = "E".equals(lowLetter);
        boolean fLevel = "F".equals(lowLetter);

        switch (part) {
            case "1":
                if (aLevel)
                    return PdfAConformanceLevel.PDF_A_1A;
                if (bLevel)
                    return PdfAConformanceLevel.PDF_A_1B;
                break;
            case "2":
                if (aLevel)
                    return PdfAConformanceLevel.PDF_A_2A;
                if (bLevel)
                    return PdfAConformanceLevel.PDF_A_2B;
                if (uLevel)
                    return PdfAConformanceLevel.PDF_A_2U;
                break;
            case "3":
                if (aLevel)
                    return PdfAConformanceLevel.PDF_A_3A;
                if (bLevel)
                    return PdfAConformanceLevel.PDF_A_3B;
                if (uLevel)
                    return PdfAConformanceLevel.PDF_A_3U;
                break;
            case "4":
                if (eLevel)
                    return PdfAConformanceLevel.PDF_A_4E;
                if (fLevel)
                    return PdfAConformanceLevel.PDF_A_4F;
                return PdfAConformanceLevel.PDF_A_4;
        }
        return null;
    }

    public static PdfAConformanceLevel getConformanceLevel(XMPMeta meta) {
        XMPProperty conformanceXmpProperty = null;
        XMPProperty partXmpProperty = null;
        try {
            conformanceXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE);
            partXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART);
        } catch (XMPException ignored) {
        }
        if (partXmpProperty == null || (conformanceXmpProperty == null && !"4".equals(partXmpProperty.getValue()))) {
            return null;
        } else {
            return getConformanceLevel(partXmpProperty.getValue(),
                    conformanceXmpProperty == null ? null : conformanceXmpProperty.getValue());
        }
    }
}
