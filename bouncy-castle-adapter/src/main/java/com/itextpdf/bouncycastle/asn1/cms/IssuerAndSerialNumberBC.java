package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

import java.math.BigInteger;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

/**
 * Wrapper class for {@link IssuerAndSerialNumber}.
 */
public class IssuerAndSerialNumberBC extends ASN1EncodableBC implements IIssuerAndSerialNumber {
    /**
     * Creates new wrapper instance for {@link IssuerAndSerialNumber}.
     *
     * @param issuerAndSerialNumber {@link IssuerAndSerialNumber} to be wrapped
     */
    public IssuerAndSerialNumberBC(IssuerAndSerialNumber issuerAndSerialNumber) {
        super(issuerAndSerialNumber);
    }

    /**
     * Creates new wrapper instance for {@link IssuerAndSerialNumber}.
     *
     * @param issuer X500Name wrapper to create {@link IssuerAndSerialNumber}
     * @param value  BigInteger to create {@link IssuerAndSerialNumber}
     */
    public IssuerAndSerialNumberBC(IX500Name issuer, BigInteger value) {
        super(new IssuerAndSerialNumber(((X500NameBC) issuer).getX500Name(), value));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link IssuerAndSerialNumber}.
     */
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return (IssuerAndSerialNumber) getEncodable();
    }
}
