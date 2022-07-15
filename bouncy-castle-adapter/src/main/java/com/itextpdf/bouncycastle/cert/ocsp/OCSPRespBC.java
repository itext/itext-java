package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ocsp.OCSPResponseBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;

/**
 * Wrapper class for {@link OCSPResp}.
 */
public class OCSPRespBC implements IOCSPResp {
    private static final OCSPRespBC INSTANCE = new OCSPRespBC((OCSPResp) null);

    private static final int SUCCESSFUL = OCSPResp.SUCCESSFUL;

    private final OCSPResp ocspResp;

    /**
     * Creates new wrapper instance for {@link OCSPResp}.
     *
     * @param ocspResp {@link OCSPResp} to be wrapped
     */
    public OCSPRespBC(OCSPResp ocspResp) {
        this.ocspResp = ocspResp;
    }

    /**
     * Creates new wrapper instance for {@link OCSPResp}.
     *
     * @param ocspResponse OCSPResponse wrapper
     */
    public OCSPRespBC(IOCSPResponse ocspResponse) {
        this(new OCSPResp(((OCSPResponseBC) ocspResponse).getOcspResponse()));
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPRespBC} instance.
     */
    public static OCSPRespBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPResp}.
     */
    public OCSPResp getOcspResp() {
        return ocspResp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return ocspResp.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return ocspResp.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getResponseObject() throws OCSPExceptionBC {
        try {
            return ocspResp.getResponseObject();
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
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
        OCSPRespBC that = (OCSPRespBC) o;
        return Objects.equals(ocspResp, that.ocspResp);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ocspResp);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return ocspResp.toString();
    }
}
