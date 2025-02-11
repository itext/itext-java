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
package com.itextpdf.commons.actions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that stores namespaces of iText open source products.
 */
public final class NamespaceConstant {
    
    public static final String ITEXT = "com.itextpdf";

    //Core
    public static final String CORE_EVENTS = ITEXT + ".events";
    public static final String CORE_IO = ITEXT + ".io";
    public static final String CORE_KERNEL = ITEXT + ".kernel";
    public static final String CORE_LAYOUT = ITEXT + ".layout";
    public static final String CORE_BARCODES = ITEXT + ".barcodes";
    public static final String CORE_PDFA = ITEXT + ".pdfa";
    public static final String CORE_PDFUA = ITEXT + ".pdfua";
    public static final String CORE_SIGN = ITEXT + ".signatures";
    public static final String CORE_FORMS = ITEXT + ".forms";
    public static final String CORE_SXP = ITEXT + ".styledxmlparser";
    public static final String CORE_SVG = ITEXT + ".svg";
    
    //Addons
    public static final String PDF_HTML = ITEXT + ".html2pdf";
    public static final String PDF_SWEEP = ITEXT + ".pdfcleanup";
    public static final String PDF_OCR = ITEXT + ".pdfocr";
    public static final String PDF_OCR_TESSERACT4 = PDF_OCR + ".tesseract4";

    public static final List<String> ITEXT_CORE_NAMESPACES = Collections.unmodifiableList(Arrays.asList(
            NamespaceConstant.CORE_EVENTS,
            NamespaceConstant.CORE_IO,
            NamespaceConstant.CORE_KERNEL,
            NamespaceConstant.CORE_LAYOUT,
            NamespaceConstant.CORE_BARCODES,
            NamespaceConstant.CORE_PDFA,
            NamespaceConstant.CORE_PDFUA,
            NamespaceConstant.CORE_SIGN,
            NamespaceConstant.CORE_FORMS,
            NamespaceConstant.CORE_SXP,
            NamespaceConstant.CORE_SVG)
    );

    private NamespaceConstant() {
    }
}
