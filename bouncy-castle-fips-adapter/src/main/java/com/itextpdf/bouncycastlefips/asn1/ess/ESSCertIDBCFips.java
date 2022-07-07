package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;

import org.bouncycastle.asn1.ess.ESSCertID;

public class ESSCertIDBCFips extends ASN1EncodableBCFips implements IESSCertID {
    public ESSCertIDBCFips(ESSCertID essCertID) {
        super(essCertID);
    }

    public ESSCertID getEssCertID() {
        return (ESSCertID) getEncodable();
    }

    @Override
    public byte[] getCertHash() {
        return getEssCertID().getCertHash();
    }
}
