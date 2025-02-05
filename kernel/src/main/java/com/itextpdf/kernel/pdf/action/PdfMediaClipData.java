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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

/**
 * This class is a wrapper of media clip data dictionary that defines the data for a media object that can be played.
 */
public class PdfMediaClipData extends PdfObjectWrapper<PdfDictionary> {

    private static final PdfString TEMPACCESS = new PdfString("TEMPACCESS");

    /**
     * Constructs a new {@link PdfMediaClipData} wrapper using an existing dictionary.
     *
     * @param pdfObject the dictionary to construct the wrapper from
     */
    public PdfMediaClipData(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructs a new {@link PdfMediaClipData} wrapper around a newly created dictionary.
     *
     * @param file     the name of the file to create a media clip for
     * @param fs       a file specification that specifies the actual media data
     * @param mimeType an ASCII string identifying the type of data
     */
    public PdfMediaClipData(String file, PdfFileSpec fs, String mimeType) {
        this(new PdfDictionary());
        PdfDictionary dic = new PdfDictionary();
        markObjectAsIndirect(dic);
        dic.put(PdfName.TF, TEMPACCESS);
        getPdfObject().put(PdfName.Type, PdfName.MediaClip);
        getPdfObject().put(PdfName.S, PdfName.MCD);
        getPdfObject().put(PdfName.N, new PdfString(MessageFormatUtil.format("Media clip for {0}", file)));
        getPdfObject().put(PdfName.CT, new PdfString(mimeType));
        getPdfObject().put(PdfName.P, dic);
        getPdfObject().put(PdfName.D, fs.getPdfObject());
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
