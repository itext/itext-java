package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionsBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;

/**
 * Wrapper class for {@link OCSPReqBuilder}.
 */
public class OCSPReqBuilderBCFips implements IOCSPReqBuilder {
    private final OCSPReqBuilder reqBuilder;

    /**
     * Creates new wrapper instance for {@link OCSPReqBuilder}.
     *
     * @param reqBuilder {@link OCSPReqBuilder} to be wrapped
     */
    public OCSPReqBuilderBCFips(OCSPReqBuilder reqBuilder) {
        this.reqBuilder = reqBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPReqBuilder}.
     */
    public OCSPReqBuilder getReqBuilder() {
        return reqBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReqBuilder setRequestExtensions(IExtensions extensions) {
        reqBuilder.setRequestExtensions(((ExtensionsBCFips) extensions).getExtensions());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReqBuilder addRequest(ICertificateID certificateID) {
        reqBuilder.addRequest(((CertificateIDBCFips) certificateID).getCertificateID());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReq build() throws OCSPExceptionBCFips {
        try {
            return new OCSPReqBCFips(reqBuilder.build());
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
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
        OCSPReqBuilderBCFips that = (OCSPReqBuilderBCFips) o;
        return Objects.equals(reqBuilder, that.reqBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(reqBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return reqBuilder.toString();
    }
}
