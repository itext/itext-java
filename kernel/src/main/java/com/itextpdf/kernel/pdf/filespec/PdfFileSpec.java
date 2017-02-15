/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.collection.PdfCollectionItem;

import java.io.IOException;
import java.io.InputStream;


public class PdfFileSpec extends PdfObjectWrapper<PdfObject>  {

    private static final long serialVersionUID = 126861971006090239L;

	protected PdfFileSpec(PdfObject pdfObject) {
        super(pdfObject);
    }
    
    public static PdfFileSpec wrapFileSpecObject(PdfObject fileSpecObject) {
	    if (fileSpecObject != null) {
            if (fileSpecObject.isString()) {
                return new PdfStringFS((PdfString) fileSpecObject);
            } else if (fileSpecObject.isDictionary()) {
                return new PdfDictionaryFS((PdfDictionary) fileSpecObject);
            }
        }
        return null;
    }

    public static PdfFileSpec createExternalFileSpec(PdfDocument doc, String filePath, boolean isUnicodeFileName) {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(filePath));
        dict.put(PdfName.UF, new PdfString(filePath, isUnicodeFileName
                ? PdfEncodings.UNICODE_BIG : PdfEncodings.PDF_DOC_ENCODING));
        return (PdfFileSpec) new PdfFileSpec(dict).makeIndirect(doc);
    }

    /**
     * Embed a file to a PdfDocument.
     * @param doc PdfDocument to add the file to
     * @param fileStore byte[] containing the file
     * @param description file description
     * @param fileDisplay actual file name stored in the pdf
     * @param mimeType mime-type of the file
     * @param fileParameter Pdfdictionary containing fil parameters
     * @param afRelationshipValue AFRelationship key value, @see AFRelationshipValue. If <CODE>null</CODE>, @see AFRelationshipValue.Unspecified will be added.
     * @param isUnicodeFileName
     * @return PdfFileSpec containing the file specification of the file as Pdfobject
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, PdfName mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue, boolean isUnicodeFileName) {
        PdfStream stream = new PdfStream(fileStore).makeIndirect(doc);
        PdfDictionary params = new PdfDictionary();

        if (fileParameter != null) {
            params.mergeDifferent(fileParameter);
        }
        if (!params.containsKey(PdfName.ModDate)) {
            params.put(PdfName.ModDate, new PdfDate().getPdfObject());
        }
        if (fileStore != null) {
            params.put(PdfName.Size, new PdfNumber(stream.getBytes().length));
            stream.put(PdfName.Params, params);
        }
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue, isUnicodeFileName);
    }

    /**
     *
     * @param doc
     * @param filePath
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param afRelationshipValue
     * @param isUnicodeFileName
     * @throws IOException
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue, boolean isUnicodeFileName) throws IOException {
        PdfStream stream = new PdfStream(doc, UrlUtil.toURL(filePath).openStream());
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue, isUnicodeFileName);
    }

    /**
     *
     * @param doc
     * @param is
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param afRelationshipValue
     * @param isUnicodeFileName
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, InputStream is, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue, boolean isUnicodeFileName) {
        PdfStream stream = new PdfStream(doc, is);
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue, isUnicodeFileName);
    }

    /**
     *
     * @param doc
     * @param stream
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param afRelationshipValue
     * @param isUnicodeFileName
     */
    private static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, PdfStream stream, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue, boolean isUnicodeFileName) {
        PdfDictionary dict = new PdfDictionary();
        stream.put(PdfName.Type, PdfName.EmbeddedFile);
        if (afRelationshipValue != null) {
            dict.put(PdfName.AFRelationship, afRelationshipValue);
        } else {
            dict.put(PdfName.AFRelationship, PdfName.Unspecified);
        }

        if (mimeType != null) {
            stream.put(PdfName.Subtype, mimeType);
        } else {
            stream.put(PdfName.Subtype, PdfName.ApplicationOctetStream);
        }

        if (description != null) {
            dict.put(PdfName.Desc, new PdfString(description));
        }
        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(fileDisplay));
        dict.put(PdfName.UF, new PdfString(fileDisplay, isUnicodeFileName ? PdfEncodings.UNICODE_BIG : PdfEncodings.PDF_DOC_ENCODING));

        PdfDictionary ef = new PdfDictionary();
        ef.put(PdfName.F, stream);
        ef.put(PdfName.UF, stream);
        dict.put(PdfName.EF, ef);

        return (PdfFileSpec) new PdfFileSpec(dict).makeIndirect(doc);
    }

    public PdfFileSpec setFileIdentifier(PdfArray fileIdentifier){
        return put(PdfName.ID, fileIdentifier);
    }

    public PdfArray getFileIdentifier() {
        return ((PdfDictionary)getPdfObject()).getAsArray(PdfName.ID);
    }

    public PdfFileSpec setVolatile(PdfBoolean isVolatile){
        return put(PdfName.Volatile, isVolatile);
    }

    public PdfBoolean isVolatile() {
        return ((PdfDictionary)getPdfObject()).getAsBoolean(PdfName.Volatile);
    }

    public PdfFileSpec setCollectionItem(PdfCollectionItem item) {
        return put(PdfName.CI, item.getPdfObject());
    }

    public PdfFileSpec put(PdfName key, PdfObject value) {
        ((PdfDictionary)getPdfObject()).put(key, value);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
