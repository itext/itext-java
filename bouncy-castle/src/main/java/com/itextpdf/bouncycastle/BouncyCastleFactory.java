package com.itextpdf.bouncycastle;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableVectorBC;
import com.itextpdf.bouncycastle.asn1.ASN1EncodableWrapperBC;
import com.itextpdf.bouncycastle.asn1.ASN1EncodingBC;
import com.itextpdf.bouncycastle.asn1.ASN1EnumeratedBC;
import com.itextpdf.bouncycastle.asn1.ASN1InputStreamBC;
import com.itextpdf.bouncycastle.asn1.ASN1IntegerBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.bouncycastle.asn1.ASN1OutputStreamBC;
import com.itextpdf.bouncycastle.asn1.ASN1PrimitiveBC;
import com.itextpdf.bouncycastle.asn1.ASN1SequenceBC;
import com.itextpdf.bouncycastle.asn1.ASN1SetBC;
import com.itextpdf.bouncycastle.asn1.ASN1TaggedObjectBC;
import com.itextpdf.bouncycastle.asn1.DERNullBC;
import com.itextpdf.bouncycastle.asn1.DEROctetStringBC;
import com.itextpdf.bouncycastle.asn1.DERSequenceBC;
import com.itextpdf.bouncycastle.asn1.DERSetBC;
import com.itextpdf.bouncycastle.asn1.DERTaggedObjectBC;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableWrapper;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDERNull;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class BouncyCastleFactory implements IBouncyCastleFactory {

    @Override
    public IASN1ObjectIdentifier createObjectIdentifier(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBC encodableWrapperBC = (ASN1EncodableWrapperBC) encodableWrapper;
        if (encodableWrapperBC.getEncodable() instanceof ASN1ObjectIdentifier) {
            return new ASN1ObjectIdentifierBC((ASN1ObjectIdentifier) encodableWrapperBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1ObjectIdentifier createObjectIdentifier(String str) {
        return new ASN1ObjectIdentifierBC(str);
    }

    @Override
    public IASN1InputStream createInputStream(InputStream stream) {
        return new ASN1InputStreamBC(stream);
    }

    @Override
    public IASN1InputStream createInputStream(byte[] bytes) {
        return new ASN1InputStreamBC(bytes);
    }

    @Override
    public IASN1OctetString createOctetString(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        if (primitiveBC.getPrimitive() instanceof ASN1OctetString) {
            return new ASN1OctetStringBC((ASN1OctetString) primitiveBC.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1OctetString createOctetString(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBC encodableWrapperBC = (ASN1EncodableWrapperBC) encodableWrapper;
        if (encodableWrapperBC.getEncodable() instanceof ASN1OctetString) {
            return new ASN1OctetStringBC((ASN1OctetString) encodableWrapperBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Sequence createSequence(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        if (primitiveBC.getPrimitive() instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) primitiveBC.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1Sequence createSequence(Object object) {
        if (object instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) object);
        }
        return null;
    }

    @Override
    public IASN1Sequence createSequence(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBC encodableWrapperBC = (ASN1EncodableWrapperBC) encodableWrapper;
        if (encodableWrapperBC.getEncodable() instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) encodableWrapperBC.getEncodable());
        }
        return null;
    }

    @Override
    public IDERSequence createDERSequence(IASN1EncodableVector encodableVector) {
        ASN1EncodableVectorBC vectorBC = (ASN1EncodableVectorBC) encodableVector;
        return new DERSequenceBC(vectorBC.getEncodableVector());
    }

    @Override
    public IDERSequence createDERSequence(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERSequenceBC(primitiveBC.getPrimitive());
    }

    @Override
    public IASN1Sequence createSequenceInstance(Object object) {
        return new ASN1SequenceBC(object);
    }

    @Override
    public IASN1TaggedObject createTaggedObject(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBC encodableWrapperBC = (ASN1EncodableWrapperBC) encodableWrapper;
        if (encodableWrapperBC.getEncodable() instanceof ASN1TaggedObject) {
            return new ASN1TaggedObjectBC((ASN1TaggedObject) encodableWrapperBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createInteger(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBC encodableWrapperBC = (ASN1EncodableWrapperBC) encodableWrapper;
        if (encodableWrapperBC.getEncodable() instanceof ASN1Integer) {
            return new ASN1IntegerBC((ASN1Integer) encodableWrapperBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createInteger(int i) {
        return new ASN1IntegerBC(i);
    }

    @Override
    public IASN1Integer createInteger(BigInteger i) {
        return new ASN1IntegerBC(i);
    }

    @Override
    public IASN1Set createSet(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBC encodableWrapperBC = (ASN1EncodableWrapperBC) encodableWrapper;
        if (encodableWrapperBC.getEncodable() instanceof ASN1Set) {
            return new ASN1SetBC((ASN1Set) encodableWrapperBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Set createSetInstance(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBC taggedObjectBC = (ASN1TaggedObjectBC) taggedObject;
        return new ASN1SetBC(taggedObjectBC.getTaggedObject(), b);
    }

    @Override
    public IASN1OutputStream createOutputStream(OutputStream stream) {
        return new ASN1OutputStreamBC(stream);
    }

    @Override
    public IDEROctetString createDEROctetString(byte[] bytes) {
        return new DEROctetStringBC(bytes);
    }

    @Override
    public IASN1EncodableVector createEncodableVector() {
        return new ASN1EncodableVectorBC();
    }

    @Override
    public IDERNull createDERNull() {
        return DERNullBC.INSTANCE;
    }

    @Override
    public IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERTaggedObjectBC(i, primitiveBC.getPrimitive());
    }

    @Override
    public IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERTaggedObjectBC(b, i, primitiveBC.getPrimitive());
    }

    @Override
    public IDERSet createDERSet(IASN1EncodableVector encodableVector) {
        ASN1EncodableVectorBC encodableVectorBC = (ASN1EncodableVectorBC) encodableVector;
        return new DERSetBC(encodableVectorBC.getEncodableVector());
    }

    @Override
    public IDERSet createDERSet(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERSetBC(primitiveBC.getPrimitive());
    }

    @Override
    public IASN1Enumerated createEnumerated(int i) {
        return new ASN1EnumeratedBC(i);
    }

    @Override
    public IASN1Encoding createEncoding() {
        return ASN1EncodingBC.getInstance();
    }
}