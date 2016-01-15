package com.itextpdf.signatures;

import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDate;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfString;

/**
 * Represents the signature dictionary.
 *
 * @author Paulo Soares
 */
public class PdfSignature extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates new PdfSignature.
     *
     * @param filter PdfName of the signature handler to use when validating this signature
     * @param subFilter PdfName that describes the encoding of the signature
     */
    public PdfSignature(PdfName filter, PdfName subFilter) {
        super(new PdfDictionary());
        put(PdfName.Type, PdfName.Sig);
        put(PdfName.Filter, filter);
        put(PdfName.SubFilter, subFilter);
    }

    /**
     * Sets the /ByteRange.
     *
     * @param range an array of pairs of integers that specifies the byte range used in the digest calculation. A pair consists of the starting byte offset and the length
     */
    public void setByteRange(int range[]) {
        PdfArray array = new PdfArray();

        for (int k = 0; k < range.length; ++k) {
            array.add(new PdfNumber(range[k]));
        }

        put(PdfName.ByteRange, array);
    }

    /**
     * Sets the /Contents value to the specified byte[].
     *
     * @param contents a byte[] representing the digest
     */
    public void setContents(byte[] contents) {
        put(PdfName.Contents, new PdfString(contents).setHexWriting(true));
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
     * Sets the /Name of the person signing the document.
     *
     * @param name name of the person signing the document
     */
    public void setName(String name) {
        put(PdfName.Name, new PdfString(name, PdfEncodings.UnicodeBig));
    }

    /**
     * Sets the /M value. Should only be used if the time of signing is not available in the signature.
     *
     * @param date time of signing
     */
    public void setDate(PdfDate date) {
        put(PdfName.M, date);
    }

    /**
     * Sets the /Location value.
     *
     * @param location physical location of signing
     */
    public void setLocation(String location) {
        put(PdfName.Location, new PdfString(location, PdfEncodings.UnicodeBig));
    }

    /**
     * Sets the /Reason value.
     *
     * @param reason reason for signing
     */
    public void setReason(String reason) {
        put(PdfName.Reason, new PdfString(reason, PdfEncodings.UnicodeBig));
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

    /**
     * Sets the /ContactInfo value.
     *
     * @param contactInfo information to contact the person who signed this document
     */
    public void setContact(String contactInfo) {
        put(PdfName.ContactInfo, new PdfString(contactInfo, PdfEncodings.UnicodeBig));
    }
}