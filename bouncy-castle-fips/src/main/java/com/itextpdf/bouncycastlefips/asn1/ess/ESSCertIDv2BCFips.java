package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.ess.ESSCertIDv2;

public class ESSCertIDv2BCFips implements IESSCertIDv2 {
    private final ESSCertIDv2 essCertIDv2;

    public ESSCertIDv2BCFips(ESSCertIDv2 essCertIDv2) {
        this.essCertIDv2 = essCertIDv2;
    }

    public ESSCertIDv2 getEssCertIDv2() {
        return essCertIDv2;
    }

    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBCFips(essCertIDv2.getHashAlgorithm());
    }

    @Override
    public byte[] getCertHash() {
        return essCertIDv2.getCertHash();
    }
}
