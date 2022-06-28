package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1String;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ASN1StringBC implements IASN1String {
    private final ASN1String asn1String;

    public ASN1StringBC(ASN1String asn1String) {
        this.asn1String = asn1String;
    }

    public ASN1String getAsn1String() {
        return asn1String;
    }

    @Override
    public String getString() {
        return asn1String.getString();
    }
}
