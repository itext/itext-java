package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1OutputStream;

public class ASN1OutputStreamBC implements IASN1OutputStream {
    private final ASN1OutputStream stream;

    public ASN1OutputStreamBC(OutputStream stream) {
        this.stream = ASN1OutputStream.create(stream);
    }

    public ASN1OutputStreamBC(ASN1OutputStream stream) {
        this.stream = stream;
    }

    public ASN1OutputStream getASN1OutputStream() {
        return stream;
    }

    @Override
    public void writeObject(IASN1Primitive primitive) throws IOException {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        stream.writeObject(primitiveBC.getPrimitive());
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
        ASN1OutputStreamBC that = (ASN1OutputStreamBC) o;
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
