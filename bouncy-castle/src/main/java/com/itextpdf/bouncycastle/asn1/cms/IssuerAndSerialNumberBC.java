package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

import java.math.BigInteger;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

public class IssuerAndSerialNumberBC extends ASN1EncodableBC implements IIssuerAndSerialNumber {
    public IssuerAndSerialNumberBC(IssuerAndSerialNumber issuerAndSerialNumber) {
        super(issuerAndSerialNumber);
    }

    public IssuerAndSerialNumberBC(IX500Name issuer, BigInteger value) {
        super(new IssuerAndSerialNumber(((X500NameBC) issuer).getX500Name(), value));
    }

    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return (IssuerAndSerialNumber) getEncodable();
    }
}
