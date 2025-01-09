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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * This a wrapper around a rendition dictionary. See ISO 32000-1 sections 13.2.3.2, 13.2.3.3.
 */
public class PdfRendition extends PdfObjectWrapper<PdfDictionary> {


    /**
     * Creates a new wrapper around an existing {@link PdfDictionary}
     *
     * @param pdfObject a rendition object to create a wrapper for
     */
    public PdfRendition(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a new wrapper around a newly created media rendition dictionary object.
     *
     * @param file     a text string specifying the name of the file to display
     * @param fs       a file specification that specifies the actual media data
     * @param mimeType an ASCII string identifying the type of data
     */
    public PdfRendition(String file, PdfFileSpec fs, String mimeType) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.S, PdfName.MR);
        getPdfObject().put(PdfName.N, new PdfString(MessageFormatUtil.format("Rendition for {0}", file)));
        getPdfObject().put(PdfName.C, new PdfMediaClipData(file, fs, mimeType).getPdfObject());
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

}
