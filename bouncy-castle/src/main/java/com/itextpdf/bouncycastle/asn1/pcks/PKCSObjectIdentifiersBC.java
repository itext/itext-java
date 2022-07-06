package com.itextpdf.bouncycastle.asn1.pcks;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class PKCSObjectIdentifiersBC implements IPKCSObjectIdentifiers {
    private static final PKCSObjectIdentifiersBC INSTANCE = new PKCSObjectIdentifiersBC();

    private static final ASN1ObjectIdentifierBC ID_AA_ETS_SIG_POLICY_ID = new ASN1ObjectIdentifierBC(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

    private static final ASN1ObjectIdentifierBC ID_AA_SIGNATURE_TIME_STAMP_TOKEN = new ASN1ObjectIdentifierBC(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
    
    private static final ASN1ObjectIdentifierBC ID_SPQ_ETS_URI =
            new ASN1ObjectIdentifierBC(PKCSObjectIdentifiers.id_spq_ets_uri);

    private static final ASN1ObjectIdentifierBC ENVELOPED_DATA = new ASN1ObjectIdentifierBC(
            PKCSObjectIdentifiers.envelopedData);

    private static final ASN1ObjectIdentifierBC DATA = new ASN1ObjectIdentifierBC(
            PKCSObjectIdentifiers.data);

    private PKCSObjectIdentifiersBC() {
        // Do nothing.
    }

    public static IPKCSObjectIdentifiers getInstance() {
        return INSTANCE;
    }

    @Override
    public IASN1ObjectIdentifier getIdAaSignatureTimeStampToken() {
        return ID_AA_SIGNATURE_TIME_STAMP_TOKEN;
    }

    @Override
    public IASN1ObjectIdentifier getIdAaEtsSigPolicyId() {
        return ID_AA_ETS_SIG_POLICY_ID;
    }

    @Override
    public IASN1ObjectIdentifier getIdSpqEtsUri() {
        return ID_SPQ_ETS_URI;
    }

    @Override
    public IASN1ObjectIdentifier getEnvelopedData() {
        return ENVELOPED_DATA;
    }

    @Override
    public IASN1ObjectIdentifier getData() {
        return DATA;
    }
}
