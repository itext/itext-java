package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;

import java.util.Objects;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

public class OCSPObjectIdentifiersBC implements IOCSPObjectIdentifiers {
    private static final OCSPObjectIdentifiersBC INSTANCE = new OCSPObjectIdentifiersBC(null);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_BASIC =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_basic);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NONCE =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NOCHECK =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck);

    private final OCSPObjectIdentifiers ocspObjectIdentifiers;

    public OCSPObjectIdentifiersBC(OCSPObjectIdentifiers ocspObjectIdentifiers) {
        this.ocspObjectIdentifiers = ocspObjectIdentifiers;
    }

    public static OCSPObjectIdentifiersBC getInstance() {
        return INSTANCE;
    }

    public OCSPObjectIdentifiers getOCSPObjectIdentifiers() {
        return ocspObjectIdentifiers;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPObjectIdentifiersBC that = (OCSPObjectIdentifiersBC) o;
        return Objects.equals(ocspObjectIdentifiers, that.ocspObjectIdentifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ocspObjectIdentifiers);
    }

    @Override
    public String toString() {
        return ocspObjectIdentifiers.toString();
    }
}
