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
package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.collection.PdfCollectionItem;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import java.io.IOException;
import java.io.InputStream;


public class PdfFileSpec extends PdfObjectWrapper<PdfObject> {

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

    public static PdfFileSpec createExternalFileSpec(PdfDocument doc, String filePath, PdfName afRelationshipValue) {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(filePath));
        dict.put(PdfName.UF, new PdfString(filePath, PdfEncodings.UNICODE_BIG));
        if (afRelationshipValue != null) {
            dict.put(PdfName.AFRelationship, afRelationshipValue);
        } else {
            dict.put(PdfName.AFRelationship, PdfName.Unspecified);
        }
        return (PdfFileSpec) new PdfFileSpec(dict).makeIndirect(doc);
    }

    public static PdfFileSpec createExternalFileSpec(PdfDocument doc, String filePath) {
        return createExternalFileSpec(doc, filePath, null);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            mime-type of the file
     * @param fileParameter       Pdfdictionary containing fil parameters
     * @param afRelationshipValue AFRelationship key value, @see AFRelationshipValue. If <CODE>null</CODE>, @see AFRelationshipValue.Unspecified will be added.
     * @return PdfFileSpec containing the file specification of the file as Pdfobject
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, PdfName mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        PdfStream stream = (PdfStream)new PdfStream(fileStore).makeIndirect(doc);
        PdfDictionary params = new PdfDictionary();
        if (fileParameter != null) {
            params.mergeDifferent(fileParameter);
        }
        if (!params.containsKey(PdfName.ModDate)) {
            params.put(PdfName.ModDate, new PdfDate().getPdfObject());
        }
        if (fileStore != null) {
            params.put(PdfName.Size, new PdfNumber(stream.getBytes().length));
        }
        stream.put(PdfName.Params, params);
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param fileDisplay         actual file name stored in the pdf
     * @param fileParameter       Pdfdictionary containing fil parameters
     * @param afRelationshipValue AFRelationship key value, @see AFRelationshipValue. If <CODE>null</CODE>, @see AFRelationshipValue.Unspecified will be added.
     * @return PdfFileSpec containing the file specification of the file as Pdfobject
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, description, fileDisplay, null, fileParameter, afRelationshipValue);
    }


    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param fileDisplay         actual file name stored in the pdf
     * @param fileParameter       Pdfdictionary containing fil parameters
     * @param afRelationshipValue AFRelationship key value, @see AFRelationshipValue. If <CODE>null</CODE>, @see AFRelationshipValue.Unspecified will be added.
     * @return PdfFileSpec containing the file specification of the file as Pdfobject
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String fileDisplay, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, null, fileDisplay, null, fileParameter, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue AFRelationship key value, @see AFRelationshipValue. If <CODE>null</CODE>, @see AFRelationshipValue.Unspecified will be added.
     * @return PdfFileSpec containing the file specification of the file as Pdfobject
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String fileDisplay, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, null, fileDisplay, null, null, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue AFRelationship key value, @see AFRelationshipValue. If <CODE>null</CODE>, @see AFRelationshipValue.Unspecified will be added.
     * @return PdfFileSpec containing the file specification of the file as Pdfobject
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, description, fileDisplay, null, null, afRelationshipValue);
    }


    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param filePath
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param fileParameter
     * @param afRelationshipValue
     * @throws IOException
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, PdfName mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue) throws IOException {
        PdfStream stream = new PdfStream(doc, UrlUtil.toURL(filePath).openStream());
        PdfDictionary params = new PdfDictionary();
        if (fileParameter != null) {
            params.mergeDifferent(fileParameter);
        }
        if (!params.containsKey(PdfName.ModDate)) {
            params.put(PdfName.ModDate, new PdfDate().getPdfObject());
        }
        stream.put(PdfName.Params, params);
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param filePath
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param afRelationshipValue
     * @throws IOException
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) throws IOException {
        return createEmbeddedFileSpec(doc, filePath, description, fileDisplay, mimeType, null, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param filePath
     * @param description
     * @param fileDisplay
     * @param afRelationshipValue
     * @throws IOException
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, PdfName afRelationshipValue) throws IOException {
        return createEmbeddedFileSpec(doc, filePath, description, fileDisplay, null, null, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param filePath
     * @param fileDisplay
     * @param afRelationshipValue
     * @throws IOException
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String fileDisplay, PdfName afRelationshipValue) throws IOException {
        return createEmbeddedFileSpec(doc, filePath, null, fileDisplay, null, null, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param is
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param fileParameter
     * @param afRelationshipValue
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, InputStream is, String description, String fileDisplay, PdfName mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        PdfStream stream = new PdfStream(doc, is);
        PdfDictionary params = new PdfDictionary();
        if (fileParameter != null) {
            params.mergeDifferent(fileParameter);
        }
        if (!params.containsKey(PdfName.ModDate)) {
            params.put(PdfName.ModDate, new PdfDate().getPdfObject());
        }
        stream.put(PdfName.Params, params);
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param is
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param afRelationshipValue
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, InputStream is, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, is, description, fileDisplay, mimeType, null, afRelationshipValue);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param stream
     * @param description
     * @param fileDisplay
     * @param mimeType
     * @param afRelationshipValue
     */
    private static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, PdfStream stream, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) {
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
        dict.put(PdfName.UF, new PdfString(fileDisplay, PdfEncodings.UNICODE_BIG));

        PdfDictionary ef = new PdfDictionary();
        ef.put(PdfName.F, stream);
        ef.put(PdfName.UF, stream);
        dict.put(PdfName.EF, ef);

        return (PdfFileSpec) new PdfFileSpec(dict).makeIndirect(doc);
    }

    /**
     * Embed a file to a PdfDocument.
     *
     * @param doc
     * @param stream
     * @param fileDisplay
     * @param afRelationshipValue
     */
    private static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, PdfStream stream, String description, String fileDisplay, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, null, afRelationshipValue);
    }

    public PdfFileSpec setFileIdentifier(PdfArray fileIdentifier) {
        return put(PdfName.ID, fileIdentifier);
    }

    public PdfArray getFileIdentifier() {
        return ((PdfDictionary) getPdfObject()).getAsArray(PdfName.ID);
    }

    public PdfFileSpec setVolatile(PdfBoolean isVolatile) {
        return put(PdfName.Volatile, isVolatile);
    }

    public PdfBoolean isVolatile() {
        return ((PdfDictionary) getPdfObject()).getAsBoolean(PdfName.Volatile);
    }

    public PdfFileSpec setCollectionItem(PdfCollectionItem item) {
        return put(PdfName.CI, item.getPdfObject());
    }

    /**
     * PDF 2.0. Sets a stream object defining the thumbnail image for the file specification.
     *
     * @param thumbnailImage image used as a thumbnail
     * @return this {@link PdfFileSpec} instance
     */
    public PdfFileSpec setThumbnailImage(PdfImageXObject thumbnailImage) {
        return put(PdfName.Thumb, thumbnailImage.getPdfObject());
    }

    /**
     * PDF 2.0. Gets a stream object defining the thumbnail image for the file specification.
     *
     * @return image used as a thumbnail, or <code>null</code> if it is not set
     */
    public PdfImageXObject getThumbnailImage() {
        PdfStream thumbnailStream = ((PdfDictionary) getPdfObject()).getAsStream(PdfName.Thumb);
        return thumbnailStream != null ? new PdfImageXObject(thumbnailStream) : null;
    }

    public PdfFileSpec put(PdfName key, PdfObject value) {
        ((PdfDictionary) getPdfObject()).put(key, value);
        setModified();
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
