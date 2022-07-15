package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;

import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;

/**
 * Wrapper class for {@link OCSPResponseStatus}.
 */
public class OCSPResponseStatusBCFips extends ASN1EncodableBCFips implements IOCSPResponseStatus {
    private static final OCSPResponseStatusBCFips INSTANCE = new OCSPResponseStatusBCFips(null);

    private static final int SUCCESSFUL = OCSPResponseStatus.SUCCESSFUL;

    /**
     * Creates new wrapper instance for {@link OCSPResponseStatus}.
     *
     * @param ocspResponseStatus {@link OCSPResponseStatus} to be wrapped
     */
    public OCSPResponseStatusBCFips(OCSPResponseStatus ocspResponseStatus) {
        super(ocspResponseStatus);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPResponseStatusBCFips} instance.
     */
    public static OCSPResponseStatusBCFips getInstance() {
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
