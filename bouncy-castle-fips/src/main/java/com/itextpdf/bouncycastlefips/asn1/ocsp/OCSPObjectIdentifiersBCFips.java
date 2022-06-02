package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

public class OCSPObjectIdentifiersBCFips implements IOCSPObjectIdentifiers {
    private static final OCSPObjectIdentifiersBCFips INSTANCE = new OCSPObjectIdentifiersBCFips();

    private static final IASN1ObjectIdentifier id_pkix_ocsp_basic =
            new ASN1ObjectIdentifierBCFips(OCSPObjectIdentifiers.id_pkix_ocsp_basic);

    private OCSPObjectIdentifiersBCFips() {
        // Do nothing.
    }

    public static OCSPObjectIdentifiersBCFips getInstance() {
        return INSTANCE;
    }

    @Override
    public IASN1ObjectIdentifier getIdPkixOcspBasic() {
        return id_pkix_ocsp_basic;
    }
}
