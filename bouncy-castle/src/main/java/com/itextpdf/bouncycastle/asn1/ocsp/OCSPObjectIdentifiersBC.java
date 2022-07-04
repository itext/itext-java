package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

public class OCSPObjectIdentifiersBC implements IOCSPObjectIdentifiers {
    private static final OCSPObjectIdentifiersBC INSTANCE = new OCSPObjectIdentifiersBC();

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_BASIC =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_basic);
    
    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NONCE =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NOCHECK =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck);

    private OCSPObjectIdentifiersBC() {
        // Do nothing.
    }

    public static OCSPObjectIdentifiersBC getInstance() {
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

    @Override
    public IASN1ObjectIdentifier getIdPkixOcspNoCheck() {
        return ID_PKIX_OCSP_NOCHECK;
    }
}
