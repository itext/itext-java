package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

import java.math.BigInteger;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

public class IssuerAndSerialNumberBCFips extends ASN1EncodableBCFips implements IIssuerAndSerialNumber {
    public IssuerAndSerialNumberBCFips(IssuerAndSerialNumber issuerAndSerialNumber) {
        super(issuerAndSerialNumber);
    }

    public IssuerAndSerialNumberBCFips(IX500Name issuer, BigInteger value) {
        super(new IssuerAndSerialNumber(((X500NameBCFips) issuer).getX500Name(), value));
    }

    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return (IssuerAndSerialNumber) getEncodable();
    }
}
