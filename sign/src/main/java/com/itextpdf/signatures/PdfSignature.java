package com.itextpdf.signatures;

import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.pdf.*;

/**
 * Represents the signature dictionary.
 *
 * @author Paulo Soares
 */
public class PdfSignature extends PdfObjectWrapper<PdfDictionary> {

    /** Creates new PdfSignature */
    public PdfSignature(PdfName filter, PdfName subFilter) {
        super(new PdfDictionary());
        put(PdfName.Type, PdfName.Sig);
        put(PdfName.Filter, filter);
        put(PdfName.SubFilter, subFilter);
    }

    public void setByteRange(int range[]) {
        PdfArray array = new PdfArray();

        for (int k = 0; k < range.length; ++k) {
            array.add(new PdfNumber(range[k]));
        }

        put(PdfName.ByteRange, array);
    }

    public void setContents(byte[] contents) {
        put(PdfName.Contents, new PdfString(contents).setHexWriting(true));
    }

    public void setCert(byte[] cert) {
        put(PdfName.Cert, new PdfString(cert));
    }

    public void setName(String name) {
        put(PdfName.Name, new PdfString(name, PdfEncodings.UnicodeBig));
    }

    public void setDate(PdfDate date) {
        put(PdfName.M, date);
    }

    public void setLocation(String name) {
        put(PdfName.Location, new PdfString(name, PdfEncodings.UnicodeBig));
    }

    public void setReason(String name) {
        put(PdfName.Reason, new PdfString(name, PdfEncodings.UnicodeBig));
    }

    /**
     * Sets the signature creator name in the
     * {@link PdfSignatureBuildProperties} dictionary.
     *
     * @param name
     */
    public void setSignatureCreator(String name) {
        if (name != null) {
            getPdfSignatureBuildProperties().setSignatureCreator(name);
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

    public void setContact(String name) {
        put(PdfName.ContactInfo, new PdfString(name, PdfEncodings.UnicodeBig));
    }
}