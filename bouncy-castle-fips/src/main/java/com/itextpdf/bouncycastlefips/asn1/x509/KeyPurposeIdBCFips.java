package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.KeyPurposeId;

public class KeyPurposeIdBCFips extends ASN1EncodableBCFips implements IKeyPurposeId {
    private static final KeyPurposeIdBCFips INSTANCE = new KeyPurposeIdBCFips(null);

    private static final KeyPurposeIdBCFips ID_KP_OCSP_SIGNING = new KeyPurposeIdBCFips(KeyPurposeId.id_kp_OCSPSigning);

    public KeyPurposeIdBCFips(KeyPurposeId KeyPurposeId) {
        super(KeyPurposeId);
    }

    public static KeyPurposeIdBCFips getInstance() {
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
