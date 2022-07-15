package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorProviderBCFips;
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

/**
 * Wrapper class for {@link CertificateID}.
 */
public class CertificateIDBCFips implements ICertificateID {
    private static final CertificateIDBCFips INSTANCE = new CertificateIDBCFips(null);

    private static final AlgorithmIdentifierBCFips HASH_SHA1 = new AlgorithmIdentifierBCFips(CertificateID.HASH_SHA1);

    private final CertificateID certificateID;

    /**
     * Creates new wrapper instance for {@link CertificateID}.
     *
     * @param certificateID {@link CertificateID} to be wrapped
     */
    public CertificateIDBCFips(CertificateID certificateID) {
        this.certificateID = certificateID;
    }

    /**
     * Creates new wrapper instance for {@link CertificateID}.
     *
     * @param digestCalculator  DigestCalculator wrapper to create {@link CertificateID}
     * @param certificateHolder X509CertificateHolder wrapper to create {@link CertificateID}
     * @param bigInteger        BigInteger to create {@link CertificateID}
     */
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

    /**
     * Gets wrapper instance.
     *
     * @return {@link CertificateIDBCFips} instance.
     */
    public static CertificateIDBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CertificateID}.
     */
    public CertificateID getCertificateID() {
        return certificateID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBCFips(certificateID.getHashAlgOID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashSha1() {
        return HASH_SHA1;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getSerialNumber() {
        return certificateID.getSerialNumber();
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateIDBCFips that = (CertificateIDBCFips) o;
        return Objects.equals(certificateID, that.certificateID);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateID);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateID.toString();
    }
}
