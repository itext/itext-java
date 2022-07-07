package com.itextpdf.bouncycastlefips.cert;

import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.bouncycastlefips.operator.ContentSignerBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder;
import com.itextpdf.commons.bouncycastle.cert.IX509v2CRLBuilder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.cert.X509v2CRLBuilder;

public class X509v2CRLBuilderBCFips implements IX509v2CRLBuilder {
    private final X509v2CRLBuilder builder;

    public X509v2CRLBuilderBCFips(X509v2CRLBuilder builder) {
        this.builder = builder;
    }

    public X509v2CRLBuilderBCFips(IX500Name x500Name, Date date) {
        this(new X509v2CRLBuilder(((X500NameBCFips) x500Name).getX500Name(), date));
    }

    public X509v2CRLBuilder getBuilder() {
        return builder;
    }

    @Override
    public IX509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, int i) {
        builder.addCRLEntry(bigInteger, date, i);
        return this;
    }

    @Override
    public IX509v2CRLBuilder setNextUpdate(Date nextUpdate) {
        builder.setNextUpdate(nextUpdate);
        return this;
    }

    @Override
    public IX509CRLHolder build(IContentSigner signer) {
        return new X509CRLHolderBCFips(builder.build(((ContentSignerBCFips) signer).getContentSigner()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509v2CRLBuilderBCFips that = (X509v2CRLBuilderBCFips) o;
        return Objects.equals(builder, that.builder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(builder);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
