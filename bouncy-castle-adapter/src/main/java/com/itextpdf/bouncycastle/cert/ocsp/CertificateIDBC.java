package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorProviderBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.math.BigInteger;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;

public class CertificateIDBC implements ICertificateID {
    private static final CertificateIDBC INSTANCE = new CertificateIDBC(null);

    private static final AlgorithmIdentifierBC HASH_SHA1 = new AlgorithmIdentifierBC(CertificateID.HASH_SHA1);

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

    public static CertificateIDBC getInstance() {
        return INSTANCE;
    }

    public CertificateID getCertificateID() {
        return certificateID;
    }

    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBC(certificateID.getHashAlgOID());
    }

    @Override
    public IAlgorithmIdentifier getHashSha1() {
        return HASH_SHA1;
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

    @Override
    public BigInteger getSerialNumber() {
        return certificateID.getSerialNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateIDBC that = (CertificateIDBC) o;
        return Objects.equals(certificateID, that.certificateID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateID);
    }

    @Override
    public String toString() {
        return certificateID.toString();
    }
}
