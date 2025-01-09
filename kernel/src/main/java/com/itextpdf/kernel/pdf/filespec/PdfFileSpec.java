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


    protected PdfFileSpec(PdfObject pdfObject) {
        super(pdfObject);
    }

    /**
     * Wrap the passed {@link PdfObject} to the specific {@link PdfFileSpec} object,
     * according to the type of the passed pdf object.
     *
     * @param fileSpecObject object to wrap
     * @return wrapped {@link PdfFileSpec} instance
     */
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

    /**
     * Create an external file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param filePath            file specification string, describing the path to the external file
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document
     *                            that refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
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

    /**
     * Create an external file specification.
     *
     * @param doc      {@link PdfDocument} instance to make this file specification indirect
     * @param filePath file specification string, describing the path to the external file
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
    public static PdfFileSpec createExternalFileSpec(PdfDocument doc, String filePath) {
        return createExternalFileSpec(doc, filePath, null);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param fileStore           byte[] containing the file
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            subtype of the embedded file. The value of this entry shall conform
     *                            to the MIME media type names
     * @param fileParameter       {@link PdfDictionary} containing fil parameters
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document
     *                            that refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
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
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param fileStore           byte[] containing the file
     * @param fileDisplay         actual file name stored in the pdf
     * @param fileParameter       {@link PdfDictionary} containing fil parameters
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document
     *                            that refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @param description         the file description
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, description, fileDisplay, null, fileParameter, afRelationshipValue);
    }


    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param fileStore           byte[] containing the file
     * @param fileDisplay         actual file name stored in the pdf
     * @param fileParameter       {@link PdfDictionary} containing fil parameters
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String fileDisplay, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, null, fileDisplay, null, fileParameter, afRelationshipValue);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param fileStore           byte[] containing the file
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String fileDisplay, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, null, fileDisplay, null, null, afRelationshipValue);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param fileStore           byte[] containing the file
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, fileStore, description, fileDisplay, null, null, afRelationshipValue);
    }


    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param filePath            file specification string, describing the path to the file to embed
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            subtype of the embedded file. The value of this entry shall conform
     *                            to the MIME media type names
     * @param fileParameter       dictionary with file parameters
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     * @throws IOException if there are errors while creating an URL from the passed file path.
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
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param filePath            file specification string, describing the path to the file to embed
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            subtype of the embedded file. The value of this entry shall conform
     *                            to the MIME media type names
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     * @throws IOException if there are errors while creating an URL from the passed file path.
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) throws IOException {
        return createEmbeddedFileSpec(doc, filePath, description, fileDisplay, mimeType, null, afRelationshipValue);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param filePath            file specification string, describing the path to the file to embed
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     * @throws IOException if there are errors while creating an URL from the passed file path.
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, PdfName afRelationshipValue) throws IOException {
        return createEmbeddedFileSpec(doc, filePath, description, fileDisplay, null, null, afRelationshipValue);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param filePath            path to the file to embed
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     * @throws IOException if there are errors while creating an URL from the passed file path.
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String fileDisplay, PdfName afRelationshipValue) throws IOException {
        return createEmbeddedFileSpec(doc, filePath, null, fileDisplay, null, null, afRelationshipValue);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param is                  stream containing the file to embed
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            subtype of the embedded file. The value of this entry shall conform
     *                            to the MIME media type names
     * @param fileParameter       dictionary with file parameters
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
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
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param is                  stream containing the file to embed
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            subtype of the embedded file. The value of this entry shall conform
     *                            to the MIME media type names
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
     */
    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, InputStream is, String description, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) {
        return createEmbeddedFileSpec(doc, is, description, fileDisplay, mimeType, null, afRelationshipValue);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param stream              an embedded file stream dictionary
     * @param description         file description
     * @param fileDisplay         actual file name stored in the pdf
     * @param mimeType            subtype of the embedded file. The value of this entry shall conform
     *                            to the MIME media type names
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
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
        doc.markStreamAsEmbeddedFile(stream);

        return (PdfFileSpec) new PdfFileSpec(dict).makeIndirect(doc);
    }

    /**
     * Create an embedded file specification.
     *
     * @param doc                 {@link PdfDocument} instance to make this file specification indirect
     * @param stream              an embedded file stream dictionary
     * @param fileDisplay         actual file name stored in the pdf
     * @param afRelationshipValue value that represents the relationship between the component of the passed PDF document that
     *                            refers to this file specification and the associated file. If <CODE>null</CODE>,
     *                            {@link PdfName#Unspecified} will be added.
     * @return {@link PdfFileSpec} containing the file specification of the file
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
