package com.itextpdf.signatures.validation.report.pades;

import java.util.HashMap;
import java.util.Map;

/**
 * This report gathers PAdES level information about all signatures in a document
 * as well as an overall PAdES level.
 */
public class DocumentPAdESLevelReport {

    private final Map<String, PAdESLevelReport> signatureReports = new HashMap<String, PAdESLevelReport>();

    /**
     * Creates a new instance.
     */
    public DocumentPAdESLevelReport() {
        // Empty constructor
    }

    /**
     * Adds a signature validation report.
     *
     * @param report a signature validation report
     */
    public void addPAdESReport(PAdESLevelReport report) {
        signatureReports.put(report.getSignatureName(), report);
    }

    /**
     * Returns the overall document PAdES level, the lowest level off all signatures.
     *
     * @return the overall document PAdES level
     */
    public PAdESLevel getDocumentLevel() {
        if (signatureReports.isEmpty()) {
            return PAdESLevel.NONE;
        }

        PAdESLevel result = PAdESLevel.B_LTA;
        for (PAdESLevelReport rep : signatureReports.values()) {
            if (rep.getLevel().compareTo(result) < 0) {
                result = rep.getLevel();
            }
        }

        return result;
    }

    /**
     * Returns the individual PAdES level report for a signature by name.
     *
     * @param signatureName the signature name to retrieve the report for
     *
     * @return the individual PAdES level report for the signature
     */

    public PAdESLevelReport getSignatureReport(String signatureName) {
        return signatureReports.get(signatureName);
    }

    /**
     * Returns a map for all signatures PAdES reports.
     *
     * @return a map for all signatures PAdES reports
     */
    public Map<String, PAdESLevelReport> getSignatureReports() {
        return signatureReports;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(70);
        sb.append("Document highest PAdES level: ").append(getDocumentLevel()).append('\n');
        for (PAdESLevelReport report : this.signatureReports.values()) {
            sb.append(report).append('\n');
        }
        return sb.toString();
    }
}
