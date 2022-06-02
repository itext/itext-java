package com.itextpdf.bouncycastlefips.asn1.pcks;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class PKCSObjectIdentifiersBCFips implements IPKCSObjectIdentifiers {
    private static final PKCSObjectIdentifiersBCFips INSTANCE = new PKCSObjectIdentifiersBCFips();

    private static final ASN1ObjectIdentifierBCFips id_aa_ets_sigPolicyId = new ASN1ObjectIdentifierBCFips(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

    private static final ASN1ObjectIdentifierBCFips id_aa_signatureTimeStampToken = new ASN1ObjectIdentifierBCFips(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

    private PKCSObjectIdentifiersBCFips() {
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
