package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.Req;

/**
 * Wrapper class for {@link Req}.
 */
public class ReqBC implements IReq {
    private final Req req;

    /**
     * Creates new wrapper instance for {@link Req}.
     *
     * @param req {@link Req} to be wrapped
     */
    public ReqBC(Req req) {
        this.req = req;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link Req}.
     */
    public Req getReq() {
        return req;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBC(req.getCertID());
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
        ReqBC reqBC = (ReqBC) o;
        return Objects.equals(req, reqBC.req);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(req);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return req.toString();
    }
}
