/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public static final PdfConformance PDF_UA_2 = new PdfConformance(PdfUAConformance.PDF_UA_2);

    public static final PdfConformance WELL_TAGGED_PDF_FOR_ACCESSIBILITY =
            new PdfConformance(Collections.singletonList(WellTaggedPdfConformance.FOR_ACCESSIBILITY));
    public static final PdfConformance WELL_TAGGED_PDF_FOR_REUSE =
            new PdfConformance(Collections.singletonList(WellTaggedPdfConformance.FOR_REUSE));

    public static final PdfConformance PDF_NONE_CONFORMANCE = new PdfConformance();

    private static final int WTPDF_FLAG_NONE = 0;
    private static final int WTPDF_FLAG_ACCESSIBILITY = 1;
    private static final int WTPDF_FLAG_REUSE = 2;
    private static final int WTPDF_FLAG_ACCESSIBILITY_AND_REUSE = WTPDF_FLAG_ACCESSIBILITY | WTPDF_FLAG_REUSE;

    private final PdfAConformance aConformance;
    private final PdfUAConformance uaConformance;
    private int wtpdfFlag = WTPDF_FLAG_NONE;

    /**
     * Creates a new {@link PdfConformance} instance based on PDF/A, PDF/UA and Well Tagged PDF conformance.
     *
     * @param aConformance     the PDF/A conformance
     * @param uaConformance    the PDF/UA conformance
     * @param wtpdfConformance the Well Tagged PDF conformance
     */
    public PdfConformance(PdfAConformance aConformance, PdfUAConformance uaConformance,
            WellTaggedPdfConformance wtpdfConformance) {
        this.aConformance = aConformance;
        this.uaConformance = uaConformance;
        setWtPdfFlag(wtpdfConformance);
    }


    /**
     * Creates a new {@link PdfConformance} instance based on PDF/A, PDF/UA and Well Tagged PDF conformance.
     *
     * @param aConformance         the PDF/A conformance
     * @param uaConformance        the PDF/UA conformance
     * @param wtpdfConformanceList the Well Tagged PDF conformance
     */
    public PdfConformance(PdfAConformance aConformance, PdfUAConformance uaConformance,
            List<WellTaggedPdfConformance> wtpdfConformanceList) {
        this.aConformance = aConformance;
        this.uaConformance = uaConformance;
        setWtPdfFlag(wtpdfConformanceList);
    }

    /**
     * Creates a new {@link PdfConformance} instance based on PDF/A and PDF/UA conformance.
     *
     * @param aConformance  the PDF/A conformance
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
     * Creates a new {@link PdfConformance} instance based on only Well Tagged PDF conformance.
     *
     * @param wtpdfConformance the Well Tagged PDF conformance
     */
    public PdfConformance(List<WellTaggedPdfConformance> wtpdfConformance) {
        setWtPdfFlag(wtpdfConformance);
        this.uaConformance = null;
        this.aConformance = null;
    }


    /**
     * Creates a new {@link PdfConformance} instance based on only Well Tagged PDF conformance.
     *
     * @param wtpdfConformance the Well Tagged PDF conformance
     */
    public PdfConformance(WellTaggedPdfConformance wtpdfConformance) {
        setWtPdfFlag(wtpdfConformance);
        this.uaConformance = null;
        this.aConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance without any conformance.
     */
    public PdfConformance() {
        this.aConformance = null;
        this.uaConformance = null;
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
        final PdfAConformance aLevel = PdfConformanceXmpMetaDataUtil.getAConformance(meta);
        final PdfUAConformance uaLevel = PdfConformanceXmpMetaDataUtil.getUAConformanceFromXmp(meta);
        final List<WellTaggedPdfConformance> wtpdfConformanceList =
                PdfConformanceXmpMetaDataUtil.getWtpdfConformanceFromXmp(
                        meta);

        return new PdfConformance(aLevel, uaLevel, wtpdfConformanceList);
    }

    /**
     * Sets required fields into XMP metadata according to passed PDF conformance.
     *
     * @param xmpMeta     the xmp metadata to which required PDF conformance fields will be set
     * @param conformance the PDF conformance which fields should be set into XMP metadata.
     *
     * @throws XMPException if the file is not well-formed XML or if the parsing fails
     * @deprecated Use {@link #setConformanceToXmp(XMPMeta)} method of {@link PdfConformance} instance instead.
     */
    @Deprecated()
    public static void setConformanceToXmp(XMPMeta xmpMeta, PdfConformance conformance) throws XMPException {
        if (conformance == null) {
            return;
        }
        conformance.setConformanceToXmp(xmpMeta);
    }

    /**
     * Gets an instance of {@link PdfAConformance} based on passed part and level.
     *
     * @param part  the part of PDF/A conformance
     * @param level the level of PDF/A conformance
     *
     * @return the {@link PdfAConformance} instance or {@code null} if there is no PDF/A conformance for passed
     * parameters
     *
     */
    public static PdfAConformance getAConformance(String part, String level) {
        return PdfConformanceXmpMetaDataUtil.getAConformance(part, level);
    }

    /**
     * Sets required fields into XMP metadata according to passed PDF conformance.
     *
     * @param xmpMeta the xmp metadata to which required PDF conformance fields will be set
     *
     * @throws XMPException if the file is not well-formed XML or if the parsing fails
     */
    public void setConformanceToXmp(XMPMeta xmpMeta) throws XMPException {
        PdfConformanceXmpMetaDataUtil.setConformanceToXmp(this, xmpMeta);
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
     * Checks if any Well Tagged PDF conformance is specified.
     *
     * @return {@code true} if Well Tagged PDF conformance is specified, otherwise {@code false}
     */
    public boolean isWtpdf() {
        return wtpdfFlag != 0;
    }

    /**
     * Checks if any of PDF/A, PDF/UA or Well Tagged PDF conformance is specified
     *
     * @return {@code true} if PDF/A, PDF/UA or Well Tagged PDF conformance is specified, otherwise {@code false}
     */
    public boolean conformsToAny() {
        return isPdfA() || isPdfUA() || isWtpdf();
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

    /**
     * Gets the list of {@link WellTaggedPdfConformance} instances if specified.
     *
     * @return the list of specified {@link WellTaggedPdfConformance} instances or empty list.
     */
    public List<WellTaggedPdfConformance> getWtpdfConformances() {
        List<WellTaggedPdfConformance> wtpdfConformanceList = new ArrayList<>();
        if ((wtpdfFlag & WTPDF_FLAG_ACCESSIBILITY) != 0) {
            wtpdfConformanceList.add(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
        }
        if ((wtpdfFlag & WTPDF_FLAG_REUSE) != 0) {
            wtpdfConformanceList.add(WellTaggedPdfConformance.FOR_REUSE);
        }
        return wtpdfConformanceList;
    }

    /**
     * Gets the {@link WellTaggedPdfConformance} instance if specified.
     *
     * @param wtPdfConformance the Well Tagged PDF conformance to check
     *
     * @return the specified {@link WellTaggedPdfConformance} instance or {@code null}.
     */
    public boolean conformsTo(WellTaggedPdfConformance wtPdfConformance) {
        switch (wtPdfConformance) {
            case FOR_ACCESSIBILITY:
                return (wtpdfFlag & WTPDF_FLAG_ACCESSIBILITY) != 0;
            case FOR_REUSE:
                return (wtpdfFlag & WTPDF_FLAG_REUSE) != 0;
            default:
                throw new IllegalArgumentException("Unknown Well Tagged PDF conformance: " + wtPdfConformance);
        }

    }

    /**
     * Checks if specified PDF/UA conformance is present in this {@link PdfConformance} instance.
     *
     * @param uaConformance the PDF/UA conformance to check
     *
     * @return {@code true} if specified PDF/UA conformance is present in this {@link PdfConformance} instance,
     * otherwise
     */
    public boolean conformsTo(PdfUAConformance uaConformance) {
        return this.uaConformance == uaConformance;
    }

    /**
     * Checks if specified PDF/A conformance is present in this {@link PdfConformance} instance.
     *
     * @param aConformance the PDF/A conformance to check
     *
     * @return {@code true} if specified PDF/A conformance is present in this {@link PdfConformance} instance, otherwise
     */
    public boolean conformsTo(PdfAConformance aConformance) {
        return this.aConformance == aConformance;
    }

    /**
     * Checks if any of specified conformance is present in this {@link PdfConformance} instance.
     *
     * @param conformanceList the conformances to check
     *
     * @return {@code true} if any of specified conformances is present in this {@link PdfConformance} instance,
     * otherwise {@code false}
     */
    public boolean conformsTo(PdfConformance... conformanceList) {
        if (conformanceList == null) {
            return false;
        }
        for (Object conformance : conformanceList) {
            if (this.equals(conformance)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any PDF/A or PDF/UA conformance is specified.
     *
     * @return {@code true} if PDF/A or PDF/UA conformance is specified, otherwise {@code false}
     *
     * @deprecated Use {@link #conformsToAny()} instead, which also checks for Well Tagged PDF conformance.
     */
    @Deprecated
    public boolean isPdfAOrUa() {
        return isPdfA() || isPdfUA();
    }

    @Override
    public int hashCode() {
        int result = aConformance != null ? aConformance.hashCode() : 0;
        result = 31 * result + (uaConformance != null ? uaConformance.hashCode() : 0);
        result = 31 * result + wtpdfFlag;
        return result;
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
        boolean checkConformance = aConformance == that.aConformance && uaConformance == that.uaConformance;
        if (!checkConformance) {
            return false;
        }
        if (this.wtpdfFlag != that.wtpdfFlag) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Conformance:");
        if (isPdfA()) {
            sb.append(" A-").append(aConformance.getPart());
            if (aConformance.getLevel() != null) {
                sb.append(aConformance.getLevel());
            }
        }
        if (isPdfUA()) {
            sb.append(" UA-").append(uaConformance.getPart());
        }
        if (isWtpdf()) {
            sb.append(" WTPDF-");
            switch (wtpdfFlag) {
                case WTPDF_FLAG_ACCESSIBILITY:
                    sb.append("FOR_ACCESSIBILITY");
                    break;
                case WTPDF_FLAG_REUSE:
                    sb.append("FOR_REUSE");
                    break;
                case WTPDF_FLAG_ACCESSIBILITY_AND_REUSE:
                    sb.append("FOR_ACCESSIBILITY_AND_REUSE");
                    break;
                default:
                    sb.append("UNKNOWN");
            }

        }
        return sb.toString().trim();
    }

    private void setWtPdfFlag(List<WellTaggedPdfConformance> wtpdfConformanceList) {
        if (wtpdfConformanceList == null) {
            throw new PdfException("Well Tagged PDF conformance list cannot be null");
        }
        for (WellTaggedPdfConformance wtpdfConformance : wtpdfConformanceList) {
            setWtPdfFlag(wtpdfConformance);
        }
    }

    private void setWtPdfFlag(WellTaggedPdfConformance wtpdfConformance) {
        if (wtpdfConformance == null) {
            throw new PdfException("Well Tagged PDF conformance list cannot be null");
        }
        switch (wtpdfConformance) {
            case FOR_ACCESSIBILITY:
                wtpdfFlag |= WTPDF_FLAG_ACCESSIBILITY;
                break;
            case FOR_REUSE:
                wtpdfFlag |= WTPDF_FLAG_REUSE;
                break;
            default:
                throw new IllegalArgumentException("Unknown Well Tagged PDF conformance: " + wtpdfConformance);
        }
    }
}
