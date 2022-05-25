package com.itextpdf.commons.bouncycastle;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public interface IBouncyCastleFactory {
    IASN1ObjectIdentifier createObjectIdentifier(IASN1EncodableWrapper encodable);

    IASN1ObjectIdentifier createObjectIdentifier(String str);

    IASN1InputStream createInputStream(InputStream stream);

    IASN1InputStream createInputStream(byte[] bytes);

    IASN1OctetString createOctetString(IASN1Primitive primitive);

    IASN1OctetString createOctetString(IASN1EncodableWrapper encodableWrapper);

    IASN1Sequence createSequence(IASN1Primitive primitive);

    IASN1Sequence createSequence(Object object);

    IASN1Sequence createSequence(IASN1EncodableWrapper encodableWrapper);

    IDERSequence createDERSequence(IASN1EncodableVector encodableVector);

    IDERSequence createDERSequence(IASN1Primitive primitive);

    IASN1Sequence createSequenceInstance(Object object);

    IASN1TaggedObject createTaggedObject(IASN1EncodableWrapper encodableWrapper);

    IASN1Integer createInteger(IASN1EncodableWrapper encodableWrapper);

    IASN1Integer createInteger(int i);

    IASN1Integer createInteger(BigInteger i);

    IASN1Set createSet(IASN1EncodableWrapper encodableWrapper);

    IASN1Set createSetInstance(IASN1TaggedObject taggedObject, boolean b);

    IASN1OutputStream createOutputStream(OutputStream stream);

    IDEROctetString createDEROctetString(byte[] bytes);

    IASN1EncodableVector createEncodableVector();

    IDERNull createDERNull();

    IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive);

    IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive);

    IDERSet createDERSet(IASN1EncodableVector encodableVector);

    IDERSet createDERSet(IASN1Primitive primitive);

    IASN1Enumerated createEnumerated(int i);

    IASN1Encoding createEncoding();
}
