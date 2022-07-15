package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.KeyPurposeId;

/**
 * Wrapper class for {@link KeyPurposeId}.
 */
public class KeyPurposeIdBCFips extends ASN1EncodableBCFips implements IKeyPurposeId {
    private static final KeyPurposeIdBCFips INSTANCE = new KeyPurposeIdBCFips(null);

    private static final KeyPurposeIdBCFips ID_KP_OCSP_SIGNING = new KeyPurposeIdBCFips(KeyPurposeId.id_kp_OCSPSigning);

    /**
     * Creates new wrapper instance for {@link KeyPurposeId}.
     *
     * @param keyPurposeId {@link KeyPurposeId} to be wrapped
     */
    public KeyPurposeIdBCFips(KeyPurposeId keyPurposeId) {
        super(keyPurposeId);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyPurposeIdBCFips} instance.
     */
    public static KeyPurposeIdBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link KeyPurposeId}.
     */
    public KeyPurposeId getKeyPurposeId() {
        return (KeyPurposeId) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyPurposeId getIdKpOCSPSigning() {
        return ID_KP_OCSP_SIGNING;
    }
}
