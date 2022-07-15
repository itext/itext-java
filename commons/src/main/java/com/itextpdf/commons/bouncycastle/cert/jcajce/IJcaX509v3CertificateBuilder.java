package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.AbstractCertIOException;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

public interface IJcaX509v3CertificateBuilder {
    IX509CertificateHolder build(IContentSigner contentSigner);

    IJcaX509v3CertificateBuilder addExtension(IASN1ObjectIdentifier extensionOID, boolean critical,
            IASN1Encodable extensionValue) throws AbstractCertIOException;
}
