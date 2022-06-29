package com.itextpdf.bouncycastle;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1EncodableVectorBC;
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
import com.itextpdf.bouncycastle.asn1.ASN1StringBC;
import com.itextpdf.bouncycastle.asn1.ASN1TaggedObjectBC;
import com.itextpdf.bouncycastle.asn1.DERNullBC;
import com.itextpdf.bouncycastle.asn1.DEROctetStringBC;
import com.itextpdf.bouncycastle.asn1.DERSequenceBC;
import com.itextpdf.bouncycastle.asn1.DERSetBC;
import com.itextpdf.bouncycastle.asn1.DERTaggedObjectBC;
import com.itextpdf.bouncycastle.asn1.cms.AttributeBC;
import com.itextpdf.bouncycastle.asn1.cms.AttributeTableBC;
import com.itextpdf.bouncycastle.asn1.cms.ContentInfoBC;
import com.itextpdf.bouncycastle.asn1.esf.SigPolicyQualifierInfoBC;
import com.itextpdf.bouncycastle.asn1.esf.SigPolicyQualifiersBC;
import com.itextpdf.bouncycastle.asn1.esf.SignaturePolicyIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ess.SigningCertificateBC;
import com.itextpdf.bouncycastle.asn1.ess.SigningCertificateV2BC;
import com.itextpdf.bouncycastle.asn1.ocsp.BasicOCSPResponseBC;
import com.itextpdf.bouncycastle.asn1.ocsp.OCSPObjectIdentifiersBC;
import com.itextpdf.bouncycastle.asn1.pcks.PKCSObjectIdentifiersBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtensionBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtensionsBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaX509CertificateConverterBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaX509CertificateHolderBC;
import com.itextpdf.bouncycastle.cert.ocsp.BasicOCSPRespBC;
import com.itextpdf.bouncycastle.cert.ocsp.CertificateIDBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPExceptionBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPReqBuilderBC;
import com.itextpdf.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilderBC;
import com.itextpdf.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipientBC;
import com.itextpdf.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilderBC;
import com.itextpdf.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilderBC;
import com.itextpdf.bouncycastle.tsp.TSPExceptionBC;
import com.itextpdf.bouncycastle.tsp.TimeStampTokenBC;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
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
import com.itextpdf.commons.bouncycastle.asn1.IASN1String;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDERNull;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

public class BouncyCastleFactory implements IBouncyCastleFactory {

    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1ObjectIdentifier) {
            return new ASN1ObjectIdentifierBC((ASN1ObjectIdentifier) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(String str) {
        return new ASN1ObjectIdentifierBC(str);
    }

    @Override
    public IASN1InputStream createASN1InputStream(InputStream stream) {
        return new ASN1InputStreamBC(stream);
    }

    @Override
    public IASN1InputStream createASN1InputStream(byte[] bytes) {
        return new ASN1InputStreamBC(bytes);
    }

    @Override
    public IASN1OctetString createASN1OctetString(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        if (primitiveBC.getPrimitive() instanceof ASN1OctetString) {
            return new ASN1OctetStringBC((ASN1OctetString) primitiveBC.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1OctetString createASN1OctetString(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1OctetString) {
            return new ASN1OctetStringBC((ASN1OctetString) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Sequence createASN1Sequence(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        if (primitiveBC.getPrimitive() instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) primitiveBC.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1Sequence createASN1Sequence(Object object) {
        if (object instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) object);
        }
        return null;
    }

    @Override
    public IASN1Sequence createASN1Sequence(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) encodableBC.getEncodable());
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
    public IASN1Sequence createASN1SequenceInstance(Object object) {
        return new ASN1SequenceBC(object);
    }

    @Override
    public IASN1TaggedObject createASN1TaggedObject(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1TaggedObject) {
            return new ASN1TaggedObjectBC((ASN1TaggedObject) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createASN1Integer(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Integer) {
            return new ASN1IntegerBC((ASN1Integer) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createASN1Integer(int i) {
        return new ASN1IntegerBC(i);
    }

    @Override
    public IASN1Integer createASN1Integer(BigInteger i) {
        return new ASN1IntegerBC(i);
    }

    @Override
    public IASN1Set createASN1Set(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Set) {
            return new ASN1SetBC((ASN1Set) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Set createASN1Set(Object encodable) {
        return encodable instanceof ASN1EncodableBC ?
                createASN1Set((ASN1EncodableBC) encodable) : null;
    }

    @Override
    public IASN1Set createASN1SetInstance(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBC taggedObjectBC = (ASN1TaggedObjectBC) taggedObject;
        return new ASN1SetBC(taggedObjectBC.getTaggedObject(), b);
    }

    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream stream) {
        return new ASN1OutputStreamBC(stream);
    }

    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream outputStream, String asn1Encoding) {
        return new ASN1OutputStreamBC(ASN1OutputStream.create(outputStream, asn1Encoding));
    }

    @Override
    public IDEROctetString createDEROctetString(byte[] bytes) {
        return new DEROctetStringBC(bytes);
    }

    @Override
    public IASN1EncodableVector createASN1EncodableVector() {
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
    public IASN1Enumerated createASN1Enumerated(int i) {
        return new ASN1EnumeratedBC(i);
    }

    @Override
    public IASN1Encoding createASN1Encoding() {
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
    public ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws TSPExceptionBC, IOException {
        ContentInfoBC contentInfoBC = (ContentInfoBC) contentInfo;
        try {
            return new TimeStampTokenBC(new TimeStampToken(contentInfoBC.getContentInfo()));
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
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
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1Encodable encodable) {
        ASN1ObjectIdentifierBC algorithmBc = (ASN1ObjectIdentifierBC) algorithm;
        ASN1EncodableBC encodableBc = (ASN1EncodableBC) encodable;
        return new AlgorithmIdentifierBC(
                new AlgorithmIdentifier(algorithmBc.getObjectIdentifier(), encodableBc.getEncodable()));
    }

    @Override
    public Provider createProvider() {
        return new BouncyCastleProvider();
    }

    @Override
    public IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey) {
        return new JceKeyTransEnvelopedRecipientBC(new JceKeyTransEnvelopedRecipient(privateKey));
    }

    @Override
    public IJcaContentVerifierProviderBuilder createJcaContentVerifierProviderBuilder() {
        return new JcaContentVerifierProviderBuilderBC(new JcaContentVerifierProviderBuilder());
    }

    @Override
    public IJcaSimpleSignerInfoVerifierBuilder createJcaSimpleSignerInfoVerifierBuilder() {
        return new JcaSimpleSignerInfoVerifierBuilderBC(new JcaSimpleSignerInfoVerifierBuilder());
    }

    @Override
    public IJcaX509CertificateConverter createJcaX509CertificateConverter() {
        return new JcaX509CertificateConverterBC(new JcaX509CertificateConverter());
    }

    @Override
    public IJcaDigestCalculatorProviderBuilder createJcaDigestCalculatorProviderBuilder() {
        return new JcaDigestCalculatorProviderBuilderBC(new JcaDigestCalculatorProviderBuilder());
    }

    @Override
    public ICertificateID createCertificateID(IDigestCalculator digestCalculator,
            IX509CertificateHolder certificateHolder,
            BigInteger bigInteger) throws OCSPExceptionBC {
        return new CertificateIDBC(digestCalculator, certificateHolder, bigInteger);
    }

    @Override
    public IX509CertificateHolder createX509CertificateHolder(byte[] bytes) throws IOException {
        return new X509CertificateHolderBC(bytes);
    }

    @Override
    public IJcaX509CertificateHolder createJcaX509CertificateHolder(X509Certificate certificate)
            throws CertificateEncodingException {
        return new JcaX509CertificateHolderBC(new JcaX509CertificateHolder(certificate));
    }

    @Override
    public IExtension createExtension(IASN1ObjectIdentifier objectIdentifier,
            boolean critical, IASN1OctetString octetString) {
        return new ExtensionBC(objectIdentifier, critical, octetString);
    }

    @Override
    public IExtensions createExtensions(IExtension extension) {
        return new ExtensionsBC(extension);
    }

    @Override
    public IOCSPReqBuilder createOCSPReqBuilder() {
        return new OCSPReqBuilderBC(new OCSPReqBuilder());
    }

    @Override
    public ISigPolicyQualifiers createSigPolicyQualifiers(ISigPolicyQualifierInfo... qualifierInfosBC) {
        SigPolicyQualifierInfo[] qualifierInfos = new SigPolicyQualifierInfo[qualifierInfosBC.length];
        for (int i = 0; i < qualifierInfos.length; ++i) {
            qualifierInfos[i] = ((SigPolicyQualifierInfoBC) qualifierInfosBC[i]).getQualifierInfo();
        }
        return new SigPolicyQualifiersBC(qualifierInfos);
    }

    @Override
    public IASN1String createASN1String(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1String) {
            return new ASN1StringBC((ASN1String) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Primitive createASN1Primitive(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBCFips = (ASN1EncodableBC) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1Primitive) {
            return new ASN1PrimitiveBC((ASN1Primitive) encodableBCFips.getEncodable());
        }
        return null;
    }
}