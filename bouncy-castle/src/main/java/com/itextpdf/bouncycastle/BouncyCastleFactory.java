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
import com.itextpdf.bouncycastle.asn1.cms.AttributeBC;
import com.itextpdf.bouncycastle.asn1.cms.AttributeTableBC;
import com.itextpdf.bouncycastle.asn1.cms.ContentInfoBC;
import com.itextpdf.bouncycastle.asn1.esf.SignaturePolicyIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ess.SigningCertificateBC;
import com.itextpdf.bouncycastle.asn1.ess.SigningCertificateV2BC;
import com.itextpdf.bouncycastle.asn1.ocsp.BasicOCSPResponseBC;
import com.itextpdf.bouncycastle.asn1.ocsp.OCSPObjectIdentifiersBC;
import com.itextpdf.bouncycastle.asn1.pcks.PKCSObjectIdentifiersBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.bouncycastle.cert.ocsp.BasicOCSPRespBC;
import com.itextpdf.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipientBC;
import com.itextpdf.bouncycastle.tsp.TimeStampTokenBC;
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
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Provider;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.tsp.TimeStampToken;

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
    public IDERSet createDERSet(ISignaturePolicyIdentifier identifier) {
        SignaturePolicyIdentifierBC identifierBC = (SignaturePolicyIdentifierBC) identifier;
        return new DERSetBC(identifierBC.getSignaturePolicyIdentifier());
    }

    @Override
    public IASN1Enumerated createEnumerated(int i) {
        return new ASN1EnumeratedBC(i);
    }

    @Override
    public IASN1Encoding createEncoding() {
        return ASN1EncodingBC.getInstance();
    }

    @Override
    public IAttributeTable createAttributeTable(IASN1Set unat) {
        ASN1SetBC asn1SetBC = (ASN1SetBC) unat;
        return new AttributeTableBC(asn1SetBC.getSet());
    }

    @Override
    public IPKCSObjectIdentifiers createPKCSObjectIdentifiers() {
        return PKCSObjectIdentifiersBC.getInstance();
    }

    @Override
    public IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues) {
        ASN1ObjectIdentifierBC attrTypeBc = (ASN1ObjectIdentifierBC) attrType;
        ASN1SetBC attrValuesBc = (ASN1SetBC) attrValues;
        return new AttributeBC(new Attribute(attrTypeBc.getObjectIdentifier(), attrValuesBc.getSet()));
    }

    @Override
    public IContentInfo createContentInfo(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new ContentInfoBC(ContentInfo.getInstance(sequenceBC.getSequence()));
    }

    @Override
    public ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws Exception {
        ContentInfoBC contentInfoBC = (ContentInfoBC) contentInfo;
        return new TimeStampTokenBC(new TimeStampToken(contentInfoBC.getContentInfo()));
    }

    @Override
    public ISigningCertificate createSigningCertificate(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new SigningCertificateBC(SigningCertificate.getInstance(sequenceBC));
    }

    @Override
    public ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new SigningCertificateV2BC(SigningCertificateV2.getInstance(sequenceBC));
    }

    @Override
    public IBasicOCSPResponse createBasicOCSPResponse(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new BasicOCSPResponseBC(BasicOCSPResponse.getInstance(primitiveBC.getPrimitive()));
    }

    @Override
    public IBasicOCSPResp createBasicOCSPResp(IBasicOCSPResponse response) {
        BasicOCSPResponseBC responseBC = (BasicOCSPResponseBC) response;
        return new BasicOCSPRespBC(new BasicOCSPResp(responseBC.getBasicOCSPResponse()));
    }

    @Override
    public IOCSPObjectIdentifiers createOCSPObjectIdentifiers() {
        return OCSPObjectIdentifiersBC.getInstance();
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1EncodableWrapper encodable) {
        ASN1ObjectIdentifierBC algorithmBc = (ASN1ObjectIdentifierBC) algorithm;
        ASN1EncodableWrapperBC encodableBc = (ASN1EncodableWrapperBC) encodable;
        return new AlgorithmIdentifierBC(new AlgorithmIdentifier(algorithmBc.getObjectIdentifier(), encodableBc.getEncodable()));
    }
    
    @Override
    public Provider createProvider() {
        return new BouncyCastleProvider();
    }

    @Override
    public IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey){
        return new JceKeyTransEnvelopedRecipientBC(new JceKeyTransEnvelopedRecipient(privateKey));
    }
}