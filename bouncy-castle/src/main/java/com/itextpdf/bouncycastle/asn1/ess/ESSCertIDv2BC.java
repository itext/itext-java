package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.ess.ESSCertIDv2;

public class ESSCertIDv2BC extends ASN1EncodableBC implements IESSCertIDv2 {
    public ESSCertIDv2BC(ESSCertIDv2 essCertIDv2) {
        super(essCertIDv2);
    }

    public ESSCertIDv2 getEssCertIDv2() {
        return (ESSCertIDv2) getEncodable();
    }

    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBC(getEssCertIDv2().getHashAlgorithm());
    }

    @Override
    public byte[] getCertHash() {
        return getEssCertIDv2().getCertHash();
    }
}
