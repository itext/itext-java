package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorProviderBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;

import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculatorProvider;

import java.math.BigInteger;

public class CertificateIDBC implements ICertificateID {
    private final CertificateID certificateID;

    public CertificateIDBC(CertificateID certificateID) {
        this.certificateID = certificateID;
    }
    
    public CertificateIDBC(IDigestCalculator digestCalculator,
                           IX509CertificateHolder certificateHolder, BigInteger bigInteger) throws OCSPExceptionBC {
        try {
            this.certificateID = new CertificateID(
                    ((DigestCalculatorBC) digestCalculator).getDigestCalculator(),
                    ((X509CertificateHolderBC) certificateHolder).getCertificateHolder(),
                    bigInteger);
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    public CertificateID getCertificateID() {
        return certificateID;
    }

    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBC(certificateID.getHashAlgOID());
    }
    
    @Override
    public boolean matchesIssuer(IX509CertificateHolder certificateHolder,
                                 IDigestCalculatorProvider provider) throws OCSPExceptionBC {
        try {
            return certificateID.matchesIssuer(
                    ((X509CertificateHolderBC) certificateHolder).getCertificateHolder(),
                    ((DigestCalculatorProviderBC) provider).getCalculatorProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }
}
