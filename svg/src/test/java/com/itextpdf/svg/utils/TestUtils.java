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
package com.itextpdf.svg.utils;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.svg.converter.SvgConverter;

import java.io.IOException;

public class TestUtils {

    public static void convertSVGtoPDF(String pdfFilePath, String svgFilePath, int PageNo, PageSize pageSize) throws IOException {

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(pdfFilePath),
                new WriterProperties().setCompressionLevel(0)));
        PageSize format = new PageSize(pageSize);
        pdfDocument.addNewPage(format.rotate());
        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(svgFilePath), pdfDocument, PageNo);

        pdfDocument.close();
    }
}
