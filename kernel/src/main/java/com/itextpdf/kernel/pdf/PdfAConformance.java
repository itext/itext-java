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
package com.itextpdf.kernel.pdf;

/**
 * PDF/A is a special variant of PDF designed specifically for long-term document preservation (the “A” stands for archive).
 *
 * <p>
 * The class contains an enumeration of all the PDF/A conformance currently supported by iText.
 */
public enum PdfAConformance {
    /** PDF/A-1A **/
    PDF_A_1A("1", "A"),
    /** PDF/A-1B **/
    PDF_A_1B("1", "B"),
    /** PDF/A-2A **/
    PDF_A_2A("2", "A"),
    /** PDF/A-2B **/
    PDF_A_2B("2", "B"),
    /** PDF/A-2U **/
    PDF_A_2U("2", "U"),
    /** PDF/A-3A **/
    PDF_A_3A("3", "A"),
    /** PDF/A-3B **/
    PDF_A_3B("3", "B"),
    /** PDF/A-3U **/
    PDF_A_3U("3", "U"),
    /** PDF/A-4 **/
    PDF_A_4("4", null),
    /** PDF/A-4E **/
    PDF_A_4E("4", "E"),
    /** PDF/A-4F **/
    PDF_A_4F("4", "F");

    private final String part;
    private final String level;

    /**
     * Creates a new {@link PdfAConformance} instance.
     *
     * @param part the part of the PDF/A conformance
     * @param level the level of the PDF/A conformance
     */
    PdfAConformance(String part, String level) {
        this.part = part;
        this.level = level;
    }

    /**
     * Get the part of the PDF/A conformance.
     *
     * @return the part of the PDF/A conformance
     */
    public String getPart() {
        return this.part;
    }

    /**
     * Get the level of the PDF/A conformance.
     *
     * @return the level of the PDF/A conformance
     */
    public String getLevel() {
        return this.level;
    }
}
