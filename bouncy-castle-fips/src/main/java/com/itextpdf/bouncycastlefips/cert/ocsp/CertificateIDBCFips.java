package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorProviderBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;

import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;

import java.math.BigInteger;

public class CertificateIDBCFips implements ICertificateID {
    private final CertificateID certificateID;

    public CertificateIDBCFips(CertificateID certificateID) {
        this.certificateID = certificateID;
    }

    public CertificateIDBCFips(IDigestCalculator digestCalculator, IX509CertificateHolder certificateHolder,
                               BigInteger bigInteger) throws OCSPExceptionBCFips {
        try {
            this.certificateID = new CertificateID(
                    ((DigestCalculatorBCFips) digestCalculator).getDigestCalculator(),
                    ((X509CertificateHolderBCFips) certificateHolder).getCertificateHolder(),
                    bigInteger);
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }

    public CertificateID getCertificateID() {
        return certificateID;
    }

    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBCFips(certificateID.getHashAlgOID());
    }

    @Override
    public boolean matchesIssuer(IX509CertificateHolder certificateHolder,
                                 IDigestCalculatorProvider provider) throws OCSPExceptionBCFips {
        try {
            return certificateID.matchesIssuer(
                    ((X509CertificateHolderBCFips) certificateHolder).getCertificateHolder(),
                    ((DigestCalculatorProviderBCFips) provider).getCalculatorProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }
}
