package com.itextpdf.bouncycastlefips;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableVectorBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableWrapperBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EncodingBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EnumeratedBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1InputStreamBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1IntegerBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OutputStreamBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1PrimitiveBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1SequenceBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1TaggedObjectBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERNullBCFips;
import com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERSequenceBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERSetBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERTaggedObjectBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.AttributeBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.AttributeTableBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.ContentInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SignaturePolicyIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ess.SigningCertificateBCFips;
import com.itextpdf.bouncycastlefips.asn1.ess.SigningCertificateV2BCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.BasicOCSPResponseBCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPObjectIdentifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.pcks.PKCSObjectIdentifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.BasicOCSPRespBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampTokenBCFips;
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
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
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
import org.bouncycastle.tsp.TimeStampToken;

public class BouncyCastleFipsFactory implements IBouncyCastleFactory {
    @Override
    public IASN1ObjectIdentifier createObjectIdentifier(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBCFips encodableWrapperBCFips = (ASN1EncodableWrapperBCFips) encodableWrapper;
        if (encodableWrapperBCFips.getEncodable() instanceof ASN1ObjectIdentifier) {
            return new ASN1ObjectIdentifierBCFips((ASN1ObjectIdentifier) encodableWrapperBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1ObjectIdentifier createObjectIdentifier(String str) {
        return new ASN1ObjectIdentifierBCFips(str);
    }

    @Override
    public IASN1InputStream createInputStream(InputStream stream) {
        return new ASN1InputStreamBCFips(stream);
    }

    @Override
    public IASN1InputStream createInputStream(byte[] bytes) {
        return new ASN1InputStreamBCFips(bytes);
    }

    @Override
    public IASN1OctetString createOctetString(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        if (primitiveBCFips.getPrimitive() instanceof ASN1OctetString) {
            return new ASN1OctetStringBCFips((ASN1OctetString) primitiveBCFips.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1OctetString createOctetString(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBCFips encodableWrapperBCFips = (ASN1EncodableWrapperBCFips) encodableWrapper;
        if (encodableWrapperBCFips.getEncodable() instanceof ASN1OctetString) {
            return new ASN1OctetStringBCFips((ASN1OctetString) encodableWrapperBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Sequence createSequence(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        if (primitiveBCFips.getPrimitive() instanceof ASN1Sequence) {
            return new ASN1SequenceBCFips((ASN1Sequence) primitiveBCFips.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1Sequence createSequence(Object object) {
        if (object instanceof ASN1Sequence) {
            return new ASN1SequenceBCFips((ASN1Sequence) object);
        }
        return null;
    }

    @Override
    public IASN1Sequence createSequence(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBCFips encodableWrapperBCFips = (ASN1EncodableWrapperBCFips) encodableWrapper;
        if (encodableWrapperBCFips.getEncodable() instanceof ASN1Sequence) {
            return new ASN1SequenceBCFips((ASN1Sequence) encodableWrapperBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IDERSequence createDERSequence(IASN1EncodableVector encodableVector) {
        ASN1EncodableVectorBCFips vectorBCFips = (ASN1EncodableVectorBCFips) encodableVector;
        return new DERSequenceBCFips(vectorBCFips.getEncodableVector());
    }

    @Override
    public IDERSequence createDERSequence(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        return new DERSequenceBCFips(primitiveBCFips.getPrimitive());
    }

    @Override
    public IASN1Sequence createSequenceInstance(Object object) {
        return new ASN1SequenceBCFips(object);
    }

    @Override
    public IASN1TaggedObject createTaggedObject(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBCFips encodableWrapperBCFips = (ASN1EncodableWrapperBCFips) encodableWrapper;
        if (encodableWrapperBCFips.getEncodable() instanceof ASN1TaggedObject) {
            return new ASN1TaggedObjectBCFips((ASN1TaggedObject) encodableWrapperBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createInteger(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBCFips encodableWrapperBCFips = (ASN1EncodableWrapperBCFips) encodableWrapper;
        if (encodableWrapperBCFips.getEncodable() instanceof ASN1Integer) {
            return new ASN1IntegerBCFips((ASN1Integer) encodableWrapperBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createInteger(int i) {
        return new ASN1IntegerBCFips(i);
    }

    @Override
    public IASN1Integer createInteger(BigInteger i) {
        return new ASN1IntegerBCFips(i);
    }

    @Override
    public IASN1Set createSet(IASN1EncodableWrapper encodableWrapper) {
        ASN1EncodableWrapperBCFips encodableWrapperBCFips = (ASN1EncodableWrapperBCFips) encodableWrapper;
        if (encodableWrapperBCFips.getEncodable() instanceof ASN1Set) {
            return new ASN1SetBCFips((ASN1Set) encodableWrapperBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Set createSetInstance(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBCFips taggedObjectBCFips = (ASN1TaggedObjectBCFips) taggedObject;
        return new ASN1SetBCFips(taggedObjectBCFips.getTaggedObject(), b);
    }

    @Override
    public IASN1OutputStream createOutputStream(OutputStream stream) {
        return new ASN1OutputStreamBCFips(stream);
    }

    @Override
    public IDEROctetString createDEROctetString(byte[] bytes) {
        return new DEROctetStringBCFips(bytes);
    }

    @Override
    public IASN1EncodableVector createEncodableVector() {
        return new ASN1EncodableVectorBCFips();
    }

    @Override
    public IDERNull createDERNull() {
        return DERNullBCFips.INSTANCE;
    }

    @Override
    public IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        return new DERTaggedObjectBCFips(i, primitiveBCFips.getPrimitive());
    }

    @Override
    public IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        return new DERTaggedObjectBCFips(b, i, primitiveBCFips.getPrimitive());
    }

    @Override
    public IDERSet createDERSet(IASN1EncodableVector encodableVector) {
        ASN1EncodableVectorBCFips encodableVectorBCFips = (ASN1EncodableVectorBCFips) encodableVector;
        return new DERSetBCFips(encodableVectorBCFips.getEncodableVector());
    }

    @Override
    public IDERSet createDERSet(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        return new DERSetBCFips(primitiveBCFips.getPrimitive());
    }

    @Override
    public IDERSet createDERSet(ISignaturePolicyIdentifier identifier) {
        SignaturePolicyIdentifierBCFips identifierBCFips = (SignaturePolicyIdentifierBCFips) identifier;
        return new DERSetBCFips(identifierBCFips.getSignaturePolicyIdentifier());
    }

    @Override
    public IASN1Enumerated createEnumerated(int i) {
        return new ASN1EnumeratedBCFips(i);
    }

    @Override
    public IASN1Encoding createEncoding() {
        return ASN1EncodingBCFips.getInstance();
    }

    @Override
    public IAttributeTable createAttributeTable(IASN1Set unat) {
        ASN1SetBCFips asn1SetBCFips = (ASN1SetBCFips) unat;
        return new AttributeTableBCFips(asn1SetBCFips.getSet());
    }

    @Override
    public IPKCSObjectIdentifiers createPKCSObjectIdentifiers() {
        return PKCSObjectIdentifiersBCFips.getInstance();
    }

    @Override
    public IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues) {
        ASN1ObjectIdentifierBCFips attrTypeBc = (ASN1ObjectIdentifierBCFips) attrType;
        ASN1SetBCFips attrValuesBc = (ASN1SetBCFips) attrValues;
        return new AttributeBCFips(new Attribute(attrTypeBc.getObjectIdentifier(), attrValuesBc.getSet()));
    }

    @Override
    public IContentInfo createContentInfo(IASN1Sequence sequence) {
        ASN1SequenceBCFips sequenceBCFips = (ASN1SequenceBCFips) sequence;
        return new ContentInfoBCFips(ContentInfo.getInstance(sequenceBCFips.getSequence()));
    }

    @Override
    public ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws Exception {
        ContentInfoBCFips contentInfoBCFips = (ContentInfoBCFips) contentInfo;
        return new TimeStampTokenBCFips(new TimeStampToken(contentInfoBCFips.getContentInfo()));
    }

    @Override
    public ISigningCertificate createSigningCertificate(IASN1Sequence sequence) {
        ASN1SequenceBCFips sequenceBCFips = (ASN1SequenceBCFips) sequence;
        return new SigningCertificateBCFips(SigningCertificate.getInstance(sequenceBCFips));
    }

    @Override
    public ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence) {
        ASN1SequenceBCFips sequenceBCFips = (ASN1SequenceBCFips) sequence;
        return new SigningCertificateV2BCFips(SigningCertificateV2.getInstance(sequenceBCFips));
    }

    @Override
    public IBasicOCSPResponse createBasicOCSPResponse(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        return new BasicOCSPResponseBCFips(BasicOCSPResponse.getInstance(primitiveBCFips.getPrimitive()));
    }

    @Override
    public IBasicOCSPResp createBasicOCSPResp(IBasicOCSPResponse response) {
        BasicOCSPResponseBCFips responseBCFips = (BasicOCSPResponseBCFips) response;
        return new BasicOCSPRespBCFips(new BasicOCSPResp(responseBCFips.getBasicOCSPResponse()));
    }

    @Override
    public IOCSPObjectIdentifiers createOCSPObjectIdentifiers() {
        return OCSPObjectIdentifiersBCFips.getInstance();
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1EncodableWrapper encodable) {
        ASN1ObjectIdentifierBCFips algorithmBc = (ASN1ObjectIdentifierBCFips) algorithm;
        ASN1EncodableWrapperBCFips encodableBc = (ASN1EncodableWrapperBCFips) encodable;
        return new AlgorithmIdentifierBCFips(new AlgorithmIdentifier(algorithmBc.getObjectIdentifier(), encodableBc.getEncodable()));
    }
}
