package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;

import org.bouncycastle.asn1.ess.ESSCertID;

/**
 * Wrapper class for {@link ESSCertID}.
 */
public class ESSCertIDBC extends ASN1EncodableBC implements IESSCertID {
    /**
     * Creates new wrapper instance for {@link ESSCertID}.
     *
     * @param essCertID {@link ESSCertID} to be wrapped
     */
    public ESSCertIDBC(ESSCertID essCertID) {
        super(essCertID);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ESSCertID}.
     */
    public ESSCertID getEssCertID() {
        return (ESSCertID) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getCertHash() {
        return getEssCertID().getCertHash();
    }
}
