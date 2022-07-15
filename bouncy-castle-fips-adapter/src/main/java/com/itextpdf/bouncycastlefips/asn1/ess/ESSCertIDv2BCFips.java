package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.ess.ESSCertIDv2;

/**
 * Wrapper class for {@link ESSCertIDv2}.
 */
public class ESSCertIDv2BCFips extends ASN1EncodableBCFips implements IESSCertIDv2 {
    /**
     * Creates new wrapper instance for {@link ESSCertIDv2}.
     *
     * @param essCertIDv2 {@link ESSCertIDv2} to be wrapped
     */
    public ESSCertIDv2BCFips(ESSCertIDv2 essCertIDv2) {
        super(essCertIDv2);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ESSCertIDv2}.
     */
    public ESSCertIDv2 getEssCertIDv2() {
        return (ESSCertIDv2) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBCFips(getEssCertIDv2().getHashAlgorithm());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getCertHash() {
        return getEssCertIDv2().getCertHash();
    }
}
