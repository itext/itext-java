/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

import com.itextpdf.io.util.MessageFormatUtil;

/**
 * This class is a wrapper of media clip data dictionary that defines the data for a media object that can be played.
 */
public class PdfMediaClipData extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -7030377585169961523L;
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
