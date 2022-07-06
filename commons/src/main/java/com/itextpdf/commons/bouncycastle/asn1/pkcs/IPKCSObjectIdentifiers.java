package com.itextpdf.commons.bouncycastle.asn1.pkcs;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface IPKCSObjectIdentifiers {
    IASN1ObjectIdentifier getIdAaSignatureTimeStampToken();

    IASN1ObjectIdentifier getIdAaEtsSigPolicyId();

    IASN1ObjectIdentifier getIdSpqEtsUri();

    IASN1ObjectIdentifier getEnvelopedData();

    IASN1ObjectIdentifier getData();
}
