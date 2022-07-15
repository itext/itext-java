package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.Req;

/**
 * Wrapper class for {@link OCSPReq}.
 */
public class OCSPReqBCFips implements IOCSPReq {
    private final OCSPReq ocspReq;

    /**
     * Creates new wrapper instance for {@link OCSPReq}.
     *
     * @param ocspReq {@link OCSPReq} to be wrapped
     */
    public OCSPReqBCFips(OCSPReq ocspReq) {
        this.ocspReq = ocspReq;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPReq}.
     */
    public OCSPReq getOcspReq() {
        return ocspReq;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return ocspReq.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IReq[] getRequestList() {
        Req[] reqs = ocspReq.getRequestList();
        IReq[] reqsBCFips = new IReq[reqs.length];
        for (int i = 0; i < reqs.length; ++i) {
            reqsBCFips[i] = new ReqBCFips(reqs[i]);
        }
        return reqsBCFips;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtension getExtension(IASN1ObjectIdentifier objectIdentifier) {
        return new ExtensionBCFips(ocspReq.getExtension(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier()));
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
        OCSPReqBCFips that = (OCSPReqBCFips) o;
        return Objects.equals(ocspReq, that.ocspReq);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ocspReq);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return ocspReq.toString();
    }
}
