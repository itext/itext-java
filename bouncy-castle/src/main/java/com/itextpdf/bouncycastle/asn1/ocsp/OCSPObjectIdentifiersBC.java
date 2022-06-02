package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

public class OCSPObjectIdentifiersBC implements IOCSPObjectIdentifiers {
    private static final OCSPObjectIdentifiersBC INSTANCE = new OCSPObjectIdentifiersBC();

    private static final IASN1ObjectIdentifier id_pkix_ocsp_basic =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_basic);

    private OCSPObjectIdentifiersBC() {
        // Do nothing.
    }

    public static OCSPObjectIdentifiersBC getInstance() {
        return INSTANCE;
    }

    @Override
    public IASN1ObjectIdentifier getIdPkixOcspBasic() {
        return id_pkix_ocsp_basic;
    }
}
