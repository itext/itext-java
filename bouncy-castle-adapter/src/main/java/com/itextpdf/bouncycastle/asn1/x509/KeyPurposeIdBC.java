package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.KeyPurposeId;

/**
 * Wrapper class for {@link KeyPurposeId}.
 */
public class KeyPurposeIdBC extends ASN1EncodableBC implements IKeyPurposeId {
    private static final KeyPurposeIdBC INSTANCE = new KeyPurposeIdBC(null);

    private static final KeyPurposeIdBC ID_KP_OCSP_SIGNING = new KeyPurposeIdBC(KeyPurposeId.id_kp_OCSPSigning);

    /**
     * Creates new wrapper instance for {@link KeyPurposeId}.
     *
     * @param keyPurposeId {@link KeyPurposeId} to be wrapped
     */
    public KeyPurposeIdBC(KeyPurposeId keyPurposeId) {
        super(keyPurposeId);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyPurposeIdBC} instance.
     */
    public static KeyPurposeIdBC getInstance() {
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
