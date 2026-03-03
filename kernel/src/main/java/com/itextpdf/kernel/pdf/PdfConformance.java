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

import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
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
    public static final PdfConformance PDF_UA_2 = new PdfConformance(PdfUAConformance.PDF_UA_2);

    public static final PdfConformance WELL_TAGGED_PDF_FOR_ACCESSIBILITY =
            new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
    public static final PdfConformance WELL_TAGGED_PDF_FOR_REUSE =
            new PdfConformance(WellTaggedPdfConformance.FOR_REUSE);

    public static final PdfConformance PDF_NONE_CONFORMANCE = new PdfConformance();

    private final PdfAConformance aConformance;
    private final PdfUAConformance uaConformance;
    private final WellTaggedPdfConformance wtpdfConformance;

    /**
     * Creates a new {@link PdfConformance} instance based on PDF/A, PDF/UA and Well Tagged PDF conformance.
     *
     * @param aConformance the PDF/A conformance
     * @param uaConformance the PDF/UA conformance
     * @param wtpdfConformance the Well Tagged PDF conformance
     */
    public PdfConformance(PdfAConformance aConformance, PdfUAConformance uaConformance,
                          WellTaggedPdfConformance wtpdfConformance) {
        this.aConformance = aConformance;
        this.uaConformance = uaConformance;
        this.wtpdfConformance = wtpdfConformance;
    }

    /**
     * Creates a new {@link PdfConformance} instance based on PDF/A and PDF/UA conformance.
     *
     * @param aConformance the PDF/A conformance
     * @param uaConformance the PDF/UA conformance
     */
    public PdfConformance(PdfAConformance aConformance, PdfUAConformance uaConformance) {
        this.aConformance = aConformance;
        this.uaConformance = uaConformance;
        this.wtpdfConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance based on only PDF/A conformance.
     *
     * @param aConformance the PDF/A conformance
     */
    public PdfConformance(PdfAConformance aConformance) {
        this.aConformance = aConformance;
        this.uaConformance = null;
        this.wtpdfConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance based on only PDF/UA conformance.
     *
     * @param uaConformance the PDF/UA conformance
     */
    public PdfConformance(PdfUAConformance uaConformance) {
        this.uaConformance = uaConformance;
        this.aConformance = null;
        this.wtpdfConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance based on only Well Tagged PDF conformance.
     *
     * @param wtpdfConformance the Well Tagged PDF conformance
     */
    public PdfConformance(WellTaggedPdfConformance wtpdfConformance) {
        this.wtpdfConformance = wtpdfConformance;
        this.uaConformance = null;
        this.aConformance = null;
    }

    /**
     * Creates a new {@link PdfConformance} instance without any conformance.
     */
    public PdfConformance() {
        this.aConformance = null;
        this.uaConformance = null;
        this.wtpdfConformance = null;
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
        return wtpdfConformance != null;
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
     * Checks if any of PDF/A, PDF/UA or Well Tagged PDF conformance is specified
     *
     * @return {@code true} if PDF/A, PDF/UA or Well Tagged PDF conformance is specified, otherwise {@code false}
     */
    public boolean isPdfAOrUaOrWtpdf() {
        return isPdfAOrUa() || isWtpdf();
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
     * Gets the {@link WellTaggedPdfConformance} instance if specified.
     *
     * @return the specified {@link WellTaggedPdfConformance} instance or {@code null}.
     */
    public WellTaggedPdfConformance getWtpdfConformance() {
        return wtpdfConformance;
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
        return aConformance == that.aConformance && uaConformance == that.uaConformance &&
                wtpdfConformance == that.wtpdfConformance;
    }

    @Override
    public int hashCode() {
        int result = aConformance == null ? 0 : aConformance.hashCode();
        result = 31 * result + (uaConformance == null ? 0 : uaConformance.hashCode());
        result = 31 * result + (wtpdfConformance == null ? 0 : wtpdfConformance.hashCode());
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
        WellTaggedPdfConformance wellTaggedPdfConformance = null;
        XMPProperty wtpdfProperty = null;
        try {
            wtpdfProperty = meta.getProperty(
                    XMPConst.NS_DECLARATIONS, XMPConst.DECLARATIONS + "/[1]/" + XMPConst.CONFORMS_TO);
        } catch (Exception ignored) {
        }
        if (wtpdfProperty != null) {
            if (XMPConst.NS_WTPDF_ACCESSIBILITY_ID.equals(wtpdfProperty.getValue())) {
                wellTaggedPdfConformance = WellTaggedPdfConformance.FOR_ACCESSIBILITY;
            } else if (XMPConst.NS_WTPDF_REUSE_ID.equals(wtpdfProperty.getValue())) {
                wellTaggedPdfConformance = WellTaggedPdfConformance.FOR_REUSE;
            }
        }

        return new PdfConformance(aLevel, uaLevel, wellTaggedPdfConformance);
    }

    /**
     * Sets required fields into XMP metadata according to passed PDF conformance.
     *
     * @param xmpMeta the xmp metadata to which required PDF conformance fields will be set
     * @param conformance the PDF conformance according to which XMP will be updated
     *
     * @throws XMPException if the file is not well-formed XML or if the parsing fails
     */
    public static void setConformanceToXmp(XMPMeta xmpMeta, PdfConformance conformance) throws XMPException {
        if (conformance == null) {
            return;
        }
        // Don't set any property if property value was set, so if
        // smth was invalid in source document, it will be left as is.
        // But if e.g. for PDF/A-4 revision wasn't specified, we will fix it.
        if (conformance.isPdfUA()) {
            if (xmpMeta.getProperty(XMPConst.NS_PDFUA_ID, XMPConst.PART) == null) {
                xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART,
                        Integer.parseInt(conformance.getUAConformance().getPart()),
                        new PropertyOptions(PropertyOptions.SEPARATE_NODE));
            }
            if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_2 &&
                    xmpMeta.getProperty(XMPConst.NS_PDFUA_ID, XMPConst.REV) == null) {
                xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.REV, 2024);
            }
        }
        if (xmpMeta.getProperty(
                XMPConst.NS_DECLARATIONS, XMPConst.DECLARATIONS + "/[1]/" + XMPConst.CONFORMS_TO) == null) {
            if (conformance.getWtpdfConformance() == WellTaggedPdfConformance.FOR_ACCESSIBILITY ||
                    conformance.getUAConformance() == PdfUAConformance.PDF_UA_2) {
                XMPMeta wtpdfMeta = XMPMetaFactory.parseFromString(WELL_TAGGED_FOR_ACCESSIBILITY_SCHEMA);
                XMPUtils.appendProperties(wtpdfMeta, xmpMeta, true, false, true);
            } else if (conformance.getWtpdfConformance() == WellTaggedPdfConformance.FOR_REUSE) {
                XMPMeta wtpdfMeta = XMPMetaFactory.parseFromString(WELL_TAGGED_FOR_REUSE_SCHEMA);
                XMPUtils.appendProperties(wtpdfMeta, xmpMeta, true, false, true);
            }
        }
        if (conformance.isPdfA()) {
            final PdfAConformance aLevel = conformance.getAConformance();
            if (xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART) == null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, aLevel.getPart());
            }
            if (aLevel.getLevel() != null && xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE) == null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, aLevel.getLevel());
            }
            if ("4".equals(aLevel.getPart()) && xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.REV) == null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.REV, PdfConformance.PDF_A_4_REVISION);
            }

            if (xmpMeta.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART) != null) {
                XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PDF_UA_EXTENSION);
                XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
            }
        }
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
        String lowLetter = StringNormalizer.toUpperCase(level);
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
        if ("2".equals(part)) {
            return PdfUAConformance.PDF_UA_2;
        }
        return null;
    }

    private static final String WELL_TAGGED_FOR_ACCESSIBILITY_SCHEMA =
            " <x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n" +
                    "  <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                    "   <rdf:Description rdf:about=\"\" xmlns:pdfd=\"http://pdfa.org/declarations/\">\n" +
                    "    <pdfd:declarations>\n" +
                    "     <rdf:Bag>\n" +
                    "      <rdf:li rdf:parseType=\"Resource\">\n" +
                    "       <pdfd:conformsTo>http://pdfa.org/declarations/wtpdf#accessibility1.0</pdfd:conformsTo>\n" +
                    "      </rdf:li>\n" +
                    "     </rdf:Bag>\n" +
                    "    </pdfd:declarations>\n" +
                    "   </rdf:Description>\n" +
                    "  </rdf:RDF>\n" +
                    " </x:xmpmeta>";
    private static final String WELL_TAGGED_FOR_REUSE_SCHEMA =
            " <x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n" +
                    "  <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                    "   <rdf:Description rdf:about=\"\" xmlns:pdfd=\"http://pdfa.org/declarations/\">\n" +
                    "    <pdfd:declarations>\n" +
                    "     <rdf:Bag>\n" +
                    "      <rdf:li rdf:parseType=\"Resource\">\n" +
                    "       <pdfd:conformsTo>http://pdfa.org/declarations/wtpdf#reuse1.0</pdfd:conformsTo>\n" +
                    "      </rdf:li>\n" +
                    "     </rdf:Bag>\n" +
                    "    </pdfd:declarations>\n" +
                    "   </rdf:Description>\n" +
                    "  </rdf:RDF>\n" +
                    " </x:xmpmeta>";

    private static final String PDF_UA_EXTENSION =
            "    <x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n" +
                    "      <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                    "        <rdf:Description rdf:about=\"\" xmlns:pdfaExtension=\"http://www.aiim.org/pdfa/ns/extension/\" xmlns:pdfaSchema=\"http://www.aiim.org/pdfa/ns/schema#\" xmlns:pdfaProperty=\"http://www.aiim.org/pdfa/ns/property#\">\n" +
                    "          <pdfaExtension:schemas>\n" +
                    "            <rdf:Bag>\n" +
                    "              <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                <pdfaSchema:namespaceURI rdf:resource=\"http://www.aiim.org/pdfua/ns/id/\"/>\n" +
                    "                <pdfaSchema:prefix>pdfuaid</pdfaSchema:prefix>\n" +
                    "                <pdfaSchema:schema>PDF/UA identification schema</pdfaSchema:schema>\n" +
                    "                <pdfaSchema:property>\n" +
                    "                  <rdf:Seq>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA version identifier</pdfaProperty:description>\n" +
                    "                      <pdfaProperty:name>part</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Integer</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA amendment identifier</pdfaProperty:description>\n" +
                    "                      <pdfaProperty:name>amd</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Text</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA corrigenda identifier</pdfaProperty:description>\n" +
                    "                      <pdfaProperty:name>corr</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Text</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                  </rdf:Seq>\n" +
                    "                </pdfaSchema:property>\n" +
                    "              </rdf:li>\n" +
                    "            </rdf:Bag>\n" +
                    "          </pdfaExtension:schemas>\n" +
                    "        </rdf:Description>\n" +
                    "      </rdf:RDF>\n" +
                    "    </x:xmpmeta>";

}
