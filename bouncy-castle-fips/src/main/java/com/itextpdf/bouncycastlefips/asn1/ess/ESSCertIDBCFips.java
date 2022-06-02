package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;

import org.bouncycastle.asn1.ess.ESSCertID;

public class ESSCertIDBCFips implements IESSCertID {
    private final ESSCertID essCertID;

    public ESSCertIDBCFips(ESSCertID essCertID) {
        this.essCertID = essCertID;
    }

    public ESSCertID getEssCertIDBC() {
        return essCertID;
    }

    @Override
    public byte[] getCertHash() {
        return essCertID.getCertHash();
    }
}
