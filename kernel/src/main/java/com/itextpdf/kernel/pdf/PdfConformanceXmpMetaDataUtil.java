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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for extracting and setting PDF conformance information in XMP metadata.
 *
 * <p>This class handles the mapping between {@link PdfConformance} instances and
 * their XMP metadata representations for PDF/A, PDF/UA, and Well Tagged PDF (WTPDF)
 * conformance levels.
 */
final class PdfConformanceXmpMetaDataUtil {

    private PdfConformanceXmpMetaDataUtil() {
        // Utility class, no need to create an instance.
    }

    /**
     * XMP property path for the first conformsTo declaration inside the declarations bag.
     */
    private static final String FIRST_CONFORMS_TO_PATH =
            XMPConst.DECLARATIONS + "/[1]/" + XMPConst.CONFORMS_TO;

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
                    "        <rdf:Description rdf:about=\"\" xmlns:pdfaExtension=\"http://www.aiim"
                    + ".org/pdfa/ns/extension/\" xmlns:pdfaSchema=\"http://www.aiim.org/pdfa/ns/schema#\" "
                    + "xmlns:pdfaProperty=\"http://www.aiim.org/pdfa/ns/property#\">\n"
                    +
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
                    "                      <pdfaProperty:description>PDF/UA version "
                    + "identifier</pdfaProperty:description>\n"
                    +
                    "                      <pdfaProperty:name>part</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Integer</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA amendment "
                    + "identifier</pdfaProperty:description>\n"
                    +
                    "                      <pdfaProperty:name>amd</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Text</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA corrigenda "
                    + "identifier</pdfaProperty:description>\n"
                    +
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


    /**
     * Sets the required XMP metadata properties for the given PDF conformance.
     *
     * <p>Existing property values are preserved; only missing properties are populated.
     * This ensures that if something was invalid in the source document, it is left as-is.
     * However, if a required property is absent (e.g. revision for PDF/A-4), it will be added.
     *
     * @param conformance the conformance whose properties should be written
     * @param xmpMeta     the XMP metadata instance to update
     *
     * @throws XMPException if the XMP metadata cannot be parsed or modified
     */
    static void setConformanceToXmp(PdfConformance conformance, XMPMeta xmpMeta) throws XMPException {
        if (conformance.isPdfUA()) {
            PdfUAConformance uaConformance = conformance.getUAConformance();
            if (xmpMeta.getProperty(XMPConst.NS_PDFUA_ID, XMPConst.PART) == null) {
                xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART,
                        Integer.parseInt(uaConformance.getPart()),
                        new PropertyOptions(PropertyOptions.SEPARATE_NODE));
            }
            if (conformance.conformsTo(PdfUAConformance.PDF_UA_2)
                    && xmpMeta.getProperty(XMPConst.NS_PDFUA_ID, XMPConst.REV) == null) {
                xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.REV, 2024);
            }
        }
        boolean missingConformsTo =
                xmpMeta.getProperty(XMPConst.NS_DECLARATIONS, FIRST_CONFORMS_TO_PATH) == null;
        if (missingConformsTo) {
            if (conformance.conformsTo(WellTaggedPdfConformance.FOR_ACCESSIBILITY) || conformance.conformsTo(
                    PdfUAConformance.PDF_UA_2)) {
                XMPMeta wtpdfMeta = XMPMetaFactory.parseFromString(WELL_TAGGED_FOR_ACCESSIBILITY_SCHEMA);
                XMPUtils.appendProperties(wtpdfMeta, xmpMeta, true, false, true);
            }
            if (conformance.conformsTo(WellTaggedPdfConformance.FOR_REUSE)) {
                XMPMeta wtpdfMeta = XMPMetaFactory.parseFromString(WELL_TAGGED_FOR_REUSE_SCHEMA);
                XMPUtils.appendProperties(wtpdfMeta, xmpMeta, true, false, true);
            }
        }
        if (conformance.isPdfA()) {
            PdfAConformance aConformance = conformance.getAConformance();
            if (xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART) == null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, aConformance.getPart());
            }
            if (aConformance.getLevel() != null
                    && xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE) == null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, aConformance.getLevel());
            }
            if ("4".equals(aConformance.getPart()) && xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.REV) == null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.REV, PdfConformance.PDF_A_4_REVISION);
            }

            if (xmpMeta.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART) != null) {
                XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PDF_UA_EXTENSION);
                XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
            }
        }
    }

    /**
     * Extracts all Well Tagged PDF conformance levels from the XMP metadata declarations bag.
     *
     * <p>The declarations bag may contain multiple entries (e.g. both accessibility and reuse),
     * so this method iterates over all items in the bag.
     *
     * @param meta the XMP metadata to inspect
     *
     * @return a list of {@link WellTaggedPdfConformance} values found; never {@code null}, may be empty
     */
    static List<WellTaggedPdfConformance> getWtpdfConformanceFromXmp(XMPMeta meta) {
        final List<WellTaggedPdfConformance> wtpdfConformanceList = new ArrayList<>();
        try {
            int itemCount = meta.countArrayItems(XMPConst.NS_DECLARATIONS, XMPConst.DECLARATIONS);
            for (int i = 1; i <= itemCount; i++) {
                String path = XMPConst.DECLARATIONS + "/[" + i + "]/" + XMPConst.CONFORMS_TO;
                XMPProperty wtpdfProperty = meta.getProperty(XMPConst.NS_DECLARATIONS, path);
                if (wtpdfProperty == null) {
                    continue;
                }
                if (XMPConst.NS_WTPDF_ACCESSIBILITY_ID.equals(wtpdfProperty.getValue())) {
                    wtpdfConformanceList.add(WellTaggedPdfConformance.FOR_ACCESSIBILITY);
                } else if (XMPConst.NS_WTPDF_REUSE_ID.equals(wtpdfProperty.getValue())) {
                    wtpdfConformanceList.add(WellTaggedPdfConformance.FOR_REUSE);
                }
            }
        } catch (XMPException ignored) {
            // If the declarations property is absent or malformed, return an empty list.
        }
        return wtpdfConformanceList;
    }

    /**
     * Extracts the PDF/A conformance level from the XMP metadata.
     *
     * @param meta the XMP metadata to inspect
     *
     * @return the {@link PdfAConformance} found, or {@code null} if none is present
     */
    static PdfAConformance getAConformance(XMPMeta meta) {
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
        return aLevel;

    }

    /**
     * Extracts the PDF/UA conformance level from the XMP metadata.
     *
     * @param meta the XMP metadata to inspect
     *
     * @return the {@link PdfUAConformance} found, or {@code null} if none is present
     */
    static PdfUAConformance getUAConformanceFromXmp(XMPMeta meta) {
        XMPProperty partUAXmpProperty = null;
        PdfUAConformance uaLevel = null;
        try {
            partUAXmpProperty = meta.getProperty(XMPConst.NS_PDFUA_ID, XMPConst.PART);
        } catch (XMPException ignored) {
        }
        if (partUAXmpProperty != null) {
            uaLevel = getUAConformance(partUAXmpProperty.getValue());
        }
        return uaLevel;
    }

    /**
     * Maps a PDF/A part and level string to the corresponding {@link PdfAConformance} enum constant.
     *
     * @param part  the PDF/A part (e.g. {@code "1"}, {@code "2"}, {@code "3"}, or {@code "4"})
     * @param level the PDF/A conformance level (e.g. {@code "A"}, {@code "B"}, {@code "U"}, {@code "E"}, or
     *              {@code "F"}); may be {@code null} for part 4
     *
     * @return the matching {@link PdfAConformance}, or {@code null} if the combination is not recognised
     */
    static PdfAConformance getAConformance(String part, String level) {
        String upperLevel = StringNormalizer.toUpperCase(level);
        boolean aLevel = "A".equals(upperLevel);
        boolean bLevel = "B".equals(upperLevel);
        boolean uLevel = "U".equals(upperLevel);
        boolean eLevel = "E".equals(upperLevel);
        boolean fLevel = "F".equals(upperLevel);

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

    /**
     * Maps a PDF/UA part string to the corresponding {@link PdfUAConformance} enum constant.
     *
     * @param part the PDF/UA part (e.g. {@code "1"} or {@code "2"})
     *
     * @return the matching {@link PdfUAConformance}, or {@code null} if the part is not recognised
     */
    static PdfUAConformance getUAConformance(String part) {
        if ("1".equals(part)) {
            return PdfUAConformance.PDF_UA_1;
        }
        if ("2".equals(part)) {
            return PdfUAConformance.PDF_UA_2;
        }
        return null;
    }

}
