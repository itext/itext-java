package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1InputStream;

public class ASN1InputStreamBCFips implements IASN1InputStream {
    private final ASN1InputStream stream;

    public ASN1InputStreamBCFips(ASN1InputStream asn1InputStream) {
        this.stream = asn1InputStream;
    }

    public ASN1InputStreamBCFips(byte[] bytes) {
        this.stream = new ASN1InputStream(bytes);
    }

    public ASN1InputStreamBCFips(InputStream stream) {
        this.stream = new ASN1InputStream(stream);
    }

    public ASN1InputStream getASN1InputStream() {
        return stream;
    }

    @Override
    public IASN1Primitive readObject() throws IOException {
        return new ASN1PrimitiveBCFips(stream.readObject());
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1InputStreamBCFips that = (ASN1InputStreamBCFips) o;
        return Objects.equals(stream, that.stream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stream);
    }

    @Override
    public String toString() {
        return stream.toString();
    }
}
