package com.itextpdf.bouncycastle.asn1.pcks;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class PKCSObjectIdentifiersBC implements IPKCSObjectIdentifiers {
    private static final PKCSObjectIdentifiersBC INSTANCE = new PKCSObjectIdentifiersBC();

    private static final ASN1ObjectIdentifierBC id_aa_ets_sigPolicyId = new ASN1ObjectIdentifierBC(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

    private static final ASN1ObjectIdentifierBC id_aa_signatureTimeStampToken = new ASN1ObjectIdentifierBC(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

    private PKCSObjectIdentifiersBC() {
        // Do nothing.
    }

    public static IPKCSObjectIdentifiers getInstance() {
        return INSTANCE;
    }

    @Override
    public IASN1ObjectIdentifier getIdAaSignatureTimeStampToken() {
        return id_aa_signatureTimeStampToken;
    }

    @Override
    public IASN1ObjectIdentifier getIdAaEtsSigPolicyId() {
        return id_aa_ets_sigPolicyId;
    }
}
