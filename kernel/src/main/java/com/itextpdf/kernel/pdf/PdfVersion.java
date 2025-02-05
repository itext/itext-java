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

import com.itextpdf.commons.utils.MessageFormatUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents all official PDF versions.
 */
public class PdfVersion implements Comparable<PdfVersion> {


    private static final List<PdfVersion> values = new ArrayList<>();

    public static final PdfVersion PDF_1_0 = createPdfVersion(1, 0);
    public static final PdfVersion PDF_1_1 = createPdfVersion(1, 1);
    public static final PdfVersion PDF_1_2 = createPdfVersion(1, 2);
    public static final PdfVersion PDF_1_3 = createPdfVersion(1, 3);
    public static final PdfVersion PDF_1_4 = createPdfVersion(1, 4);
    public static final PdfVersion PDF_1_5 = createPdfVersion(1, 5);
    public static final PdfVersion PDF_1_6 = createPdfVersion(1, 6);
    public static final PdfVersion PDF_1_7 = createPdfVersion(1, 7);
    public static final PdfVersion PDF_2_0 = createPdfVersion(2, 0);

    private int major;
    private int minor;

    /**
     * Creates a PdfVersion class.
     * @param major major version number
     * @param minor minor version number
     */
    private PdfVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    @Override
    public String toString() {
        return MessageFormatUtil.format("PDF-{0}.{1}", major, minor);
    }

    public PdfName toPdfName() {
        return new PdfName(MessageFormatUtil.format("{0}.{1}", major, minor));
    }

    /**
     * Creates a PdfVersion class from a String object if the specified version
     * can be found.
     *
     * @param value version number
     * @return PdfVersion of the specified version
     */
    public static PdfVersion fromString(String value) {
        for (PdfVersion version : values) {
            if (version.toString().equals(value)) {
                return version;
            }
        }
        throw new IllegalArgumentException("The provided pdf version was not found.");
    }

    /**
     * Creates a PdfVersion class from a {@link PdfName} object if the specified version
     * can be found.
     *
     * @param name version number
     * @return PdfVersion of the specified version
     */
    public static PdfVersion fromPdfName(PdfName name) {
        for (PdfVersion version : values) {
            if (version.toPdfName().equals(name)) {
                return version;
            }
        }
        throw new IllegalArgumentException("The provided pdf version was not found.");
    }

    @Override
    public int compareTo(PdfVersion o) {
        int majorResult = Integer.compare(major, o.major);
        if (majorResult != 0) {
            return majorResult;
        } else {
            return Integer.compare(minor, o.minor);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && compareTo((PdfVersion) obj) == 0;
    }

    private static PdfVersion createPdfVersion(int major, int minor) {
        PdfVersion pdfVersion = new PdfVersion(major, minor);
        values.add(pdfVersion);
        return pdfVersion;
    }
}
