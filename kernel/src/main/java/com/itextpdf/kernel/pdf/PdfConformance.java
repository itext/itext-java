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
 * The class represents possible PDF document conformance.
 */
public class PdfConformance {
    public static final String PDF_A_4_REVISION = "2020";

    public static final PdfConformance PDF_A_1A = new PdfConformance(PdfAConformance.PDF_A_1A);
    public static final PdfConformance PDF_A_1B = new PdfConformance(PdfAConformance.PDF_A_1B);
    public static final PdfConformance PDF_A_2A = new PdfConformance(PdfAConformance.PDF_A_2A);
    public static final PdfConformance PDF_A_2B = new PdfConformance(PdfAConformance.PDF_A_2B);
    public static final PdfConformance PDF_A_2U = new PdfConformance(PdfAConformance.PDF_A_2U);
    public static final PdfConformance PDF_A_3A = new PdfConformance(PdfAConformance.PDF_A_3A);
    public static final PdfConformance PDF_A_3B = new PdfConformance(PdfAConformance.PDF_A_3B);
    public static final PdfConformance PDF_A_3U = new PdfConformance(PdfAConformance.PDF_A_3U);
    public static final PdfConformance PDF_A_4 = new PdfConformance(PdfAConformance.PDF_A_4);
    public static final PdfConformance PDF_A_4E = new PdfConformance(PdfAConformance.PDF_A_4E);
    public static final PdfConformance PDF_A_4F = new PdfConformance(PdfAConformance.PDF_A_4F);

    public static final PdfConformance PDF_UA_1 = new PdfConformance(PdfUAConformance.PDF_UA_1);

    public static final PdfConformance PDF_NONE_CONFORMANCE = new PdfConformance();

    private final PdfAConformance aConformance;
    private final PdfUAConformance uaConformance;

    /**
     * Creates a new {@link PdfConformance} instance based on PDF/A and PDF/UA conformance.
     *
     * @param aConformance the PDF/A conformance
     * @param uaConformance the PDF/UA conformance
     */
    public PdfConformance(PdfAConformance aConformance, PdfUAConformance uaConformance) {
        this.aConformance = aConformance;
        this.uaConformance = uaConformance;
    }

    /**
     * Creates a new {@link PdfConformance} instance based on only PDF/A conformance.
     *
     * @param aConformance the PDF/A conformance
     */
    public PdfConformance(PdfAConformance aConformance) {
        this.aConformance = aConformance;
        this.uaConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance based on only PDF/UA conformance.
     *
     * @param uaConformance the PDF/UA conformance
     */
    public PdfConformance(PdfUAConformance uaConformance) {
        this.uaConformance = uaConformance;
        this.aConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance without PDF/A or PDF/UA conformance.
     */
    public PdfConformance() {
        this.aConformance = null;
        this.uaConformance = null;
    }

    /**
     * Checks if any PDF/A conformance is specified.
     *
     * @return {@code true} if PDF/A conformance is specified, otherwise {@code false}
     */
    public boolean isPdfA() {
        return aConformance != null;
    }

    /**
     * Checks if any PDF/UA conformance is specified.
     *
     * @return {@code true} if PDF/UA conformance is specified, otherwise {@code false}
     */
    public boolean isPdfUA() {
        return uaConformance != null;
    }

    /**
     * Checks if any PDF/A or PDF/UA conformance is specified.
     *
     * @return {@code true} if PDF/A or PDF/UA conformance is specified, otherwise {@code false}
     */
    public boolean isPdfAOrUa() {
        return isPdfA() || isPdfUA();
    }

    /**
     * Gets the {@link PdfAConformance} instance if specified.
     *
     * @return the specified {@link PdfAConformance} instance or {@code null}.
     */
    public PdfAConformance getAConformance() {
        return aConformance;
    }

    /**
     * Gets the {@link PdfUAConformance} instance if specified.
     *
     * @return the specified {@link PdfUAConformance} instance or {@code null}.
     */
    public PdfUAConformance getUAConformance() {
        return uaConformance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PdfConformance that = (PdfConformance) o;
        return aConformance == that.aConformance && uaConformance == that.uaConformance;
    }

    @Override
    public int hashCode() {
        int result = aConformance == null ? 0 : aConformance.hashCode();
        result = 31 * result + (uaConformance == null ? 0 : uaConformance.hashCode());
        return result;
    }

    /**
     * Gets {@link PdfConformance} instance from {@link XMPMeta}.
     *
     * @param meta the meta data to parse
     *
     * @return the {@link PdfConformance} instance
     */
    public static PdfConformance getConformance(XMPMeta meta) {
        if (meta == null) {
            return PdfConformance.PDF_NONE_CONFORMANCE;
        }
        XMPProperty conformanceAXmpProperty = null;
        XMPProperty partAXmpProperty = null;
        PdfAConformance aLevel = null;
        try {
            conformanceAXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE);
            partAXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART);
        } catch (XMPException ignored) {
        }
        if (partAXmpProperty != null && (conformanceAXmpProperty != null || "4".equals(partAXmpProperty.getValue()))) {
            aLevel = getAConformance(partAXmpProperty.getValue(),
                    conformanceAXmpProperty == null ? null : conformanceAXmpProperty.getValue());
        }

        XMPProperty partUAXmpProperty = null;
        PdfUAConformance uaLevel = null;
        try {
            partUAXmpProperty = meta.getProperty(XMPConst.NS_PDFUA_ID, XMPConst.PART);
        } catch (XMPException ignored) {
        }
        if (partUAXmpProperty != null) {
            uaLevel = getUAConformance(partUAXmpProperty.getValue());
        }

        return new PdfConformance(aLevel, uaLevel);
    }

    /**
     * Gets an instance of {@link PdfAConformance} based on passed part and level.
     *
     * @param part the part of PDF/A conformance
     * @param level the level of PDF/A conformance
     *
     * @return the {@link PdfAConformance} instance or {@code null} if there is no PDF/A conformance for passed parameters
     */
    public static PdfAConformance getAConformance(String part, String level) {
        String lowLetter = level == null ? null : level.toUpperCase();
        boolean aLevel = "A".equals(lowLetter);
        boolean bLevel = "B".equals(lowLetter);
        boolean uLevel = "U".equals(lowLetter);
        boolean eLevel = "E".equals(lowLetter);
        boolean fLevel = "F".equals(lowLetter);

        switch (part) {
            case "1":
                if (aLevel) {
                    return PdfAConformance.PDF_A_1A;
                }
                if (bLevel) {
                    return PdfAConformance.PDF_A_1B;
                }
                break;
            case "2":
                if (aLevel) {
                    return PdfAConformance.PDF_A_2A;
                }
                if (bLevel) {
                    return PdfAConformance.PDF_A_2B;
                }
                if (uLevel) {
                    return PdfAConformance.PDF_A_2U;
                }
                break;
            case "3":
                if (aLevel) {
                    return PdfAConformance.PDF_A_3A;
                }
                if (bLevel) {
                    return PdfAConformance.PDF_A_3B;
                }
                if (uLevel) {
                    return PdfAConformance.PDF_A_3U;
                }
                break;
            case "4":
                if (eLevel) {
                    return PdfAConformance.PDF_A_4E;
                }
                if (fLevel) {
                    return PdfAConformance.PDF_A_4F;
                }
                return PdfAConformance.PDF_A_4;
        }
        return null;
    }

    private static PdfUAConformance getUAConformance(String part) {
        if ("1".equals(part)) {
            return PdfUAConformance.PDF_UA_1;
        }
        return null;
    }
}
