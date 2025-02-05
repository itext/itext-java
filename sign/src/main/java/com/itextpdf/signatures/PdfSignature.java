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
package com.itextpdf.signatures;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * Represents the signature dictionary.
 */
public class PdfSignature extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates new PdfSignature.
     */
    public PdfSignature() {
        super(new PdfDictionary());
        put(PdfName.Type, PdfName.Sig);
    }
    /**
     * Creates new PdfSignature.
     *
     * @param filter PdfName of the signature handler to use when validating this signature
     * @param subFilter PdfName that describes the encoding of the signature
     */
    public PdfSignature(PdfName filter, PdfName subFilter) {
        this();
        put(PdfName.Filter, filter);
        put(PdfName.SubFilter, subFilter);
    }

    /**
     * Creates new {@link PdfSignature} instance from the provided {@link PdfDictionary}.
     *
     * @param sigDictionary {@link PdfDictionary} to create new {@link PdfSignature} instance from
     */
    public PdfSignature(PdfDictionary sigDictionary) {
        super(sigDictionary);
        PdfString contents = getPdfObject().getAsString(PdfName.Contents);
        if (contents != null) {
            contents.markAsUnencryptedObject();
        }
    }

    /**
     * A name that describes the encoding of the signature value and key information in the signature dictionary.
     * @return a {@link PdfName} which usually has a value either {@link PdfName#Adbe_pkcs7_detached}
     * or {@link PdfName#ETSI_CAdES_DETACHED}.
     */
    public PdfName getSubFilter() {
        return getPdfObject().getAsName(PdfName.SubFilter);
    }

    /**
     * The type of PDF object that the wrapped dictionary describes; if present, shall be {@link PdfName#Sig} for a signature
     * dictionary or {@link PdfName#DocTimeStamp} for a timestamp signature dictionary. Shall be not null if it's value
     * is {@link PdfName#DocTimeStamp}. The default value is: {@link PdfName#Sig}.
     * @return a {@link PdfName} that identifies type of the wrapped dictionary,
     * returns null if it is not explicitly specified.
     */
    public PdfName getType() {
        return getPdfObject().getAsName(PdfName.Type);
    }

    /**
     * Sets the /ByteRange.
     *
     * @param range an array of pairs of integers that specifies the byte range used in the digest calculation.
     *              A pair consists of the starting byte offset and the length
     */
    public void setByteRange(int[] range) {
        PdfArray array = new PdfArray();
        for (int i : range) {
            array.add(new PdfNumber(i));
        }
        put(PdfName.ByteRange, array);
    }

    /**
     * Gets the /ByteRange.
     * @return an array of pairs of integers that specifies the byte range used in the digest calculation.
     * A pair consists of the starting byte offset and the length.
     */
    public PdfArray getByteRange() {
        return getPdfObject().getAsArray(PdfName.ByteRange);
    }

    /**
     * Sets the /Contents value to the specified byte[].
     *
     * @param contents a byte[] representing the digest
     */
    public void setContents(byte[] contents) {
        PdfString contentsString = new PdfString(contents).setHexWriting(true);
        contentsString.markAsUnencryptedObject();
        put(PdfName.Contents, contentsString);
    }

    /**
     * Gets the /Contents entry value.
     * See ISO 32000-1 12.8.1, Table 252 – Entries in a signature dictionary.
     *
     * @return the signature content
     */
    public PdfString getContents() {
        return getPdfObject().getAsString(PdfName.Contents);
    }

    /**
     * Sets the /Cert value of this signature.
     *
     * @param cert the byte[] representing the certificate chain
     */
    public void setCert(byte[] cert) {
        put(PdfName.Cert, new PdfString(cert));
    }

    /**
     * Gets the /Cert entry value of this signature.
     * See ISO 32000-1 12.8.1, Table 252 – Entries in a signature dictionary.
     *
     * @return the signature cert
     */
    public PdfString getCert() {
        return getPdfObject().getAsString(PdfName.Cert);
    }

    /**
     * Gets the /Cert entry value of this signature.
     * /Cert entry required when SubFilter is adbe.x509.rsa_sha1. May be array or byte string.
     *
     * @return the signature cert value
     */
    public PdfObject getCertObject() {
        PdfString certAsStr = getPdfObject().getAsString(PdfName.Cert);
        PdfArray certAsArray = getPdfObject().getAsArray(PdfName.Cert);

        if (certAsStr != null) {
            return certAsStr;
        } else {
            return certAsArray;
        }
    }

    /**
     * Sets the /Name of the person signing the document.
     *
     * @param name name of the person signing the document
     */
    public void setName(String name) {
        put(PdfName.Name, new PdfString(name, PdfEncodings.UNICODE_BIG));
    }

    /**
     * gets the /Name of the person signing the document.
     * @return name of the person signing the document.
     */
    public String getName() {
        PdfString nameStr = getPdfObject().getAsString(PdfName.Name);
        PdfName nameName = getPdfObject().getAsName(PdfName.Name);
        if (nameStr != null) {
            return nameStr.toUnicodeString();
        } else {
            return nameName != null ? nameName.getValue() : null;
        }
    }

    /**
     * Sets the /M value. Should only be used if the time of signing is not available in the signature.
     *
     * @param date time of signing
     */
    public void setDate(PdfDate date) {
        put(PdfName.M, date.getPdfObject());
    }

    /**
     * Gets the /M value. Should only be used if the time of signing is not available in the signature.
     * @return {@link PdfString} which denotes time of signing.
     */
    public PdfString getDate() {
        return getPdfObject().getAsString(PdfName.M);
    }

    /**
     * Sets the /Location value.
     *
     * @param location physical location of signing
     */
    public void setLocation(String location) {
        put(PdfName.Location, new PdfString(location, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Gets the /Location entry value.
     * @return physical location of signing.
     */
    public String getLocation() {
        PdfString locationStr = getPdfObject().getAsString(PdfName.Location);
        return locationStr != null ? locationStr.toUnicodeString() : null;
    }

    /**
     * Sets the /Reason value.
     *
     * @param reason reason for signing
     */
    public void setReason(String reason) {
        put(PdfName.Reason, new PdfString(reason, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Gets the /Reason value.
     *
     * @return reason for signing
     */
    public String getReason() {
        PdfString reasonStr = getPdfObject().getAsString(PdfName.Reason);
        return reasonStr != null ? reasonStr.toUnicodeString() : null;
    }

    /**
     * Sets the signature creator name in the
     * {@link PdfSignatureBuildProperties} dictionary.
     *
     * @param signatureCreator name of the signature creator
     */
    public void setSignatureCreator(String signatureCreator) {
        if (signatureCreator != null) {
            getPdfSignatureBuildProperties().setSignatureCreator(signatureCreator);
        }
    }

    /**
     * Sets the /ContactInfo value.
     *
     * @param contactInfo information to contact the person who signed this document
     */
    public void setContact(String contactInfo) {
        put(PdfName.ContactInfo, new PdfString(contactInfo, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Add new key-value pair to the signature dictionary.
     *
     * @param key {@link PdfName} to be added as a key
     * @param value {@link PdfObject} to be added as a value
     *
     * @return the same {@link PdfSignature} instance
     */
    public PdfSignature put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Gets the {@link PdfSignatureBuildProperties} instance if it exists, if
     * not it adds a new one and returns this.
     *
     * @return {@link PdfSignatureBuildProperties}
     */
    private PdfSignatureBuildProperties getPdfSignatureBuildProperties() {
        PdfDictionary buildPropDict = getPdfObject().getAsDictionary(PdfName.Prop_Build);
        if (buildPropDict == null) {
            buildPropDict = new PdfDictionary();
            put(PdfName.Prop_Build, buildPropDict);
        }
        return new PdfSignatureBuildProperties(buildPropDict);
    }
}
