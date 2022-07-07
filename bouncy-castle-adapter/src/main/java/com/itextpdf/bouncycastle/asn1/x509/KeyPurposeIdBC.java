package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.KeyPurposeId;

public class KeyPurposeIdBC extends ASN1EncodableBC implements IKeyPurposeId {
    private static final KeyPurposeIdBC INSTANCE = new KeyPurposeIdBC(null);

    private static final KeyPurposeIdBC ID_KP_OCSP_SIGNING = new KeyPurposeIdBC(KeyPurposeId.id_kp_OCSPSigning);

    public KeyPurposeIdBC(KeyPurposeId KeyPurposeId) {
        super(KeyPurposeId);
    }

    public static KeyPurposeIdBC getInstance() {
        return INSTANCE;
    }

    public KeyPurposeId getKeyPurposeId() {
        return (KeyPurposeId) getEncodable();
    }

    @Override
    public IKeyPurposeId getIdKpOCSPSigning() {
        return ID_KP_OCSP_SIGNING;
    }
}
