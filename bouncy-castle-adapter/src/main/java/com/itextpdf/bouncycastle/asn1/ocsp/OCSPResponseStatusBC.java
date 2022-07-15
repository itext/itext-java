package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;

import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;

/**
 * Wrapper class for {@link OCSPResponseStatus}.
 */
public class OCSPResponseStatusBC extends ASN1EncodableBC implements IOCSPResponseStatus {
    private static final OCSPResponseStatusBC INSTANCE = new OCSPResponseStatusBC(null);

    private static final int SUCCESSFUL = OCSPResponseStatus.SUCCESSFUL;

    /**
     * Creates new wrapper instance for {@link OCSPResponseStatus}.
     *
     * @param ocspResponseStatus {@link OCSPResponseStatus} to be wrapped
     */
    public OCSPResponseStatusBC(OCSPResponseStatus ocspResponseStatus) {
        super(ocspResponseStatus);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPResponseStatusBC} instance.
     */
    public static OCSPResponseStatusBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPResponseStatus}.
     */
    public OCSPResponseStatus getOcspResponseStatus() {
        return (OCSPResponseStatus) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
    }
}
