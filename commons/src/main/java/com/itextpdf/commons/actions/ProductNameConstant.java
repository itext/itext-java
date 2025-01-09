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
import java.util.HashSet;
import java.util.Set;

/**
 * Class which contains open source product name constants.
 */
public final class ProductNameConstant {
    /**
     * itext-core constant.
     */
    public static final String ITEXT_CORE = "itext-core";
    /**
     * itext-core sign module constant.
     */
    public static final String ITEXT_CORE_SIGN = "itext-core-sign";
    /**
     * pdfhtml constant.
     */
    public static final String PDF_HTML = "pdfHtml";
    /**
     * pdfsweep constant.
     */
    public static final String PDF_SWEEP = "pdfSweep";
    /**
     * pdfocr-tesseract4 constant.
     */
    public static final String PDF_OCR_TESSERACT4 = "pdfOcr-tesseract4";
    /**
     * set of product names.
     */
    public static final Set<String> PRODUCT_NAMES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    ProductNameConstant.ITEXT_CORE,
                    ProductNameConstant.PDF_HTML,
                    ProductNameConstant.PDF_SWEEP,
                    ProductNameConstant.PDF_OCR_TESSERACT4
            )));

    private ProductNameConstant() {}
}
