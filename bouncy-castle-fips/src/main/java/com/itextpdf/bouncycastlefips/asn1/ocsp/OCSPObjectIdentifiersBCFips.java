package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

public class OCSPObjectIdentifiersBCFips implements IOCSPObjectIdentifiers {
    private static final OCSPObjectIdentifiersBCFips INSTANCE = new OCSPObjectIdentifiersBCFips();

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_BASIC =
            new ASN1ObjectIdentifierBCFips(OCSPObjectIdentifiers.id_pkix_ocsp_basic);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NONCE =
            new ASN1ObjectIdentifierBCFips(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);

    private OCSPObjectIdentifiersBCFips() {
        // Do nothing.
    }

    public static OCSPObjectIdentifiersBCFips getInstance() {
        return INSTANCE;
    }

    @Override
    public IASN1ObjectIdentifier getIdPkixOcspBasic() {
        return ID_PKIX_OCSP_BASIC;
    }

    @Override
    public IASN1ObjectIdentifier getIdPkixOcspNonce() {
        return ID_PKIX_OCSP_NONCE;
    }
}
