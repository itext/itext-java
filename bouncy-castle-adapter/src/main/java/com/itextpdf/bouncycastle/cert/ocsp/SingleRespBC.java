package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ocsp.BasicOCSPResponseBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import java.util.Date;
import java.util.Objects;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.cert.ocsp.SingleResp;

/**
 * Wrapper class for {@link SingleResp}.
 */
public class SingleRespBC implements ISingleResp {
    private final SingleResp singleResp;

    /**
     * Creates new wrapper instance for {@link SingleResp}.
     *
     * @param singleResp {@link SingleResp} to be wrapped
     */
    public SingleRespBC(SingleResp singleResp) {
        this.singleResp = singleResp;
    }

    /**
     * Creates new wrapper instance for {@link SingleResp}.
     *
     * @param basicResp {@link IBasicOCSPResponse} wrapper to get {@link SingleResp}
     */
    public SingleRespBC(IBasicOCSPResponse basicResp) {
        this(new SingleResp(SingleResponse.getInstance(((BasicOCSPResponseBC) basicResp).getBasicOCSPResponse()
                .getTbsResponseData().getResponses().getObjectAt(0))));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SingleResp}.
     */
    public SingleResp getSingleResp() {
        return singleResp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBC(singleResp.getCertID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateStatus getCertStatus() {
        return new CertificateStatusBC(singleResp.getCertStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getNextUpdate() {
        return singleResp.getNextUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getThisUpdate() {
        return singleResp.getThisUpdate();
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
        SingleRespBC that = (SingleRespBC) o;
        return Objects.equals(singleResp, that.singleResp);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(singleResp);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return singleResp.toString();
    }
}
