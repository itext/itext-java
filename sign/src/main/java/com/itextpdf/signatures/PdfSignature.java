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
 *
 * @author Paulo Soares
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

    public PdfSignature(PdfDictionary sigDictionary) {
        super(sigDictionary);
        PdfString contents = getPdfObject().getAsString(PdfName.Contents);
        if (contents != null) {
            contents.markAsUnencryptedObject();
        }
    }

    /**
     * A name that describes the encoding of the signature value and key information in the signature dictionary.
     * @return a {@link PdfName} which usually has a value either {@link PdfName#Adbe_pkcs7_detached} or {@link PdfName#ETSI_CAdES_DETACHED}.
     */
    public PdfName getSubFilter() {
        return getPdfObject().getAsName(PdfName.SubFilter);
    }

    /**
     * The type of PDF object that the wrapped dictionary describes; if present, shall be {@link PdfName#Sig} for a signature
     * dictionary or {@link PdfName#DocTimeStamp} for a timestamp signature dictionary. Shall be not null if it's value
     * is {@link PdfName#DocTimeStamp}. The default value is: {@link PdfName#Sig}.
     * @return a {@link PdfName} that identifies type of the wrapped dictionary, returns null if it is not explicitly specified.
     */
    public PdfName getType() {
        return getPdfObject().getAsName(PdfName.Type);
    }

    /**
     * Sets the /ByteRange.
     *
     * @param range an array of pairs of integers that specifies the byte range used in the digest calculation. A pair consists of the starting byte offset and the length
     */
    public void setByteRange(int[] range) {
        PdfArray array = new PdfArray();

        for (int k = 0; k < range.length; ++k) {
            array.add(new PdfNumber(range[k]));
        }

        put(PdfName.ByteRange, array);
    }

    /**
     * Gets the /ByteRange.
     * @return an array of pairs of integers that specifies the byte range used in the digest calculation. A pair consists of the starting byte offset and the length.
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
     */
    public PdfString getCert() {
        return getPdfObject().getAsString(PdfName.Cert);
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

    public PdfSignature put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

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
