package com.itextpdf.bouncycastlefips;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableVectorBCFips;
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
import com.itextpdf.bouncycastlefips.asn1.ASN1StringBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1TaggedObjectBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERNullBCFips;
import com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERSequenceBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERSetBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERTaggedObjectBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.AttributeBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.AttributeTableBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.ContentInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SigPolicyQualifierInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SigPolicyQualifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SignaturePolicyIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ess.SigningCertificateBCFips;
import com.itextpdf.bouncycastlefips.asn1.ess.SigningCertificateV2BCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.BasicOCSPResponseBCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPObjectIdentifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.pcks.PKCSObjectIdentifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionsBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaX509CertificateConverterBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaX509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.BasicOCSPRespBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.CertificateIDBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPExceptionBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPReqBuilderBCFips;
import com.itextpdf.bouncycastlefips.cms.jcajce.JcaSimpleSignerInfoVerifierBuilderBCFips;
import com.itextpdf.bouncycastlefips.cms.jcajce.JceKeyTransEnvelopedRecipientBCFips;
import com.itextpdf.bouncycastlefips.operator.jcajce.JcaContentVerifierProviderBuilderBCFips;
import com.itextpdf.bouncycastlefips.operator.jcajce.JcaDigestCalculatorProviderBuilderBCFips;
import com.itextpdf.bouncycastlefips.tsp.TSPExceptionBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampTokenBCFips;
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
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DLOutputStream;
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
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

public class BouncyCastleFipsFactory implements IBouncyCastleFactory {
    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1ObjectIdentifier) {
            return new ASN1ObjectIdentifierBCFips((ASN1ObjectIdentifier) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(String str) {
        return new ASN1ObjectIdentifierBCFips(str);
    }

    @Override
    public IASN1InputStream createASN1InputStream(InputStream stream) {
        return new ASN1InputStreamBCFips(stream);
    }

    @Override
    public IASN1InputStream createASN1InputStream(byte[] bytes) {
        return new ASN1InputStreamBCFips(bytes);
    }

    @Override
    public IASN1OctetString createASN1OctetString(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        if (primitiveBCFips.getPrimitive() instanceof ASN1OctetString) {
            return new ASN1OctetStringBCFips((ASN1OctetString) primitiveBCFips.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1OctetString createASN1OctetString(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1OctetString) {
            return new ASN1OctetStringBCFips((ASN1OctetString) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Sequence createASN1Sequence(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        if (primitiveBCFips.getPrimitive() instanceof ASN1Sequence) {
            return new ASN1SequenceBCFips((ASN1Sequence) primitiveBCFips.getPrimitive());
        }
        return null;
    }

    @Override
    public IASN1Sequence createASN1Sequence(Object object) {
        if (object instanceof ASN1Sequence) {
            return new ASN1SequenceBCFips((ASN1Sequence) object);
        }
        return null;
    }

    @Override
    public IASN1Sequence createASN1Sequence(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1Sequence) {
            return new ASN1SequenceBCFips((ASN1Sequence) encodableBCFips.getEncodable());
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
    public IASN1Sequence createASN1SequenceInstance(Object object) {
        return new ASN1SequenceBCFips(object);
    }

    @Override
    public IASN1TaggedObject createASN1TaggedObject(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1TaggedObject) {
            return new ASN1TaggedObjectBCFips((ASN1TaggedObject) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createASN1Integer(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1Integer) {
            return new ASN1IntegerBCFips((ASN1Integer) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Integer createASN1Integer(int i) {
        return new ASN1IntegerBCFips(i);
    }

    @Override
    public IASN1Integer createASN1Integer(BigInteger i) {
        return new ASN1IntegerBCFips(i);
    }

    @Override
    public IASN1Set createASN1Set(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1Set) {
            return new ASN1SetBCFips((ASN1Set) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Set createASN1Set(Object encodable) {
        return encodable instanceof ASN1EncodableBCFips ?
                createASN1Set((ASN1EncodableBCFips) encodable) : null;
    }

    @Override
    public IASN1Set createASN1SetInstance(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBCFips taggedObjectBCFips = (ASN1TaggedObjectBCFips) taggedObject;
        return new ASN1SetBCFips(taggedObjectBCFips.getTaggedObject(), b);
    }

    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream stream) {
        return new ASN1OutputStreamBCFips(stream);
    }

    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream outputStream, String asn1Encoding) {
        if (asn1Encoding.equals("DER")) {
            return new ASN1OutputStreamBCFips(new DEROutputStream(outputStream));
        } else {
            return new ASN1OutputStreamBCFips((asn1Encoding.equals("DL") ? new DLOutputStream(outputStream)
                    : new ASN1OutputStream(outputStream)));
        }
    }

    @Override
    public IDEROctetString createDEROctetString(byte[] bytes) {
        return new DEROctetStringBCFips(bytes);
    }

    @Override
    public IASN1EncodableVector createASN1EncodableVector() {
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
    public IASN1Enumerated createASN1Enumerated(int i) {
        return new ASN1EnumeratedBCFips(i);
    }

    @Override
    public IASN1Encoding createASN1Encoding() {
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
    public ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws TSPExceptionBCFips, IOException {
        ContentInfoBCFips contentInfoBCFips = (ContentInfoBCFips) contentInfo;
        try {
            return new TimeStampTokenBCFips(new TimeStampToken(contentInfoBCFips.getContentInfo()));
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
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
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm,
            IASN1Encodable encodable) {
        ASN1ObjectIdentifierBCFips algorithmBc = (ASN1ObjectIdentifierBCFips) algorithm;
        ASN1EncodableBCFips encodableBc = (ASN1EncodableBCFips) encodable;
        return new AlgorithmIdentifierBCFips(
                new AlgorithmIdentifier(algorithmBc.getObjectIdentifier(), encodableBc.getEncodable()));
    }

    @Override
    public Provider createProvider() {
        return new BouncyCastleFipsProvider();
    }

    @Override
    public IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey) {
        return new JceKeyTransEnvelopedRecipientBCFips(new JceKeyTransEnvelopedRecipient(privateKey));
    }

    @Override
    public IJcaContentVerifierProviderBuilder createJcaContentVerifierProviderBuilder() {
        return new JcaContentVerifierProviderBuilderBCFips(new JcaContentVerifierProviderBuilder());
    }

    @Override
    public IJcaSimpleSignerInfoVerifierBuilder createJcaSimpleSignerInfoVerifierBuilder() {
        return new JcaSimpleSignerInfoVerifierBuilderBCFips(new JcaSimpleSignerInfoVerifierBuilder());
    }

    @Override
    public IJcaX509CertificateConverter createJcaX509CertificateConverter() {
        return new JcaX509CertificateConverterBCFips(new JcaX509CertificateConverter());
    }

    @Override
    public IJcaDigestCalculatorProviderBuilder createJcaDigestCalculatorProviderBuilder() {
        return new JcaDigestCalculatorProviderBuilderBCFips(new JcaDigestCalculatorProviderBuilder());
    }

    @Override
    public ICertificateID createCertificateID(IDigestCalculator digestCalculator,
            IX509CertificateHolder certificateHolder,
            BigInteger bigInteger) throws OCSPExceptionBCFips {
        return new CertificateIDBCFips(digestCalculator, certificateHolder, bigInteger);
    }

    @Override
    public IX509CertificateHolder createX509CertificateHolder(byte[] bytes) throws IOException {
        return new X509CertificateHolderBCFips(bytes);
    }

    @Override
    public IJcaX509CertificateHolder createJcaX509CertificateHolder(X509Certificate certificate)
            throws CertificateEncodingException {
        return new JcaX509CertificateHolderBCFips(new JcaX509CertificateHolder(certificate));
    }

    @Override
    public IExtension createExtension(IASN1ObjectIdentifier objectIdentifier,
            boolean critical, IASN1OctetString octetString) {
        return new ExtensionBCFips(objectIdentifier, critical, octetString);
    }

    @Override
    public IExtensions createExtensions(IExtension extension) {
        return new ExtensionsBCFips(extension);
    }

    @Override
    public IOCSPReqBuilder createOCSPReqBuilder() {
        return new OCSPReqBuilderBCFips(new OCSPReqBuilder());
    }

    @Override
    public ISigPolicyQualifiers createSigPolicyQualifiers(ISigPolicyQualifierInfo... qualifierInfosBC) {
        SigPolicyQualifierInfo[] qualifierInfos = new SigPolicyQualifierInfo[qualifierInfosBC.length];
        for (int i = 0; i < qualifierInfos.length; ++i) {
            qualifierInfos[i] = ((SigPolicyQualifierInfoBCFips) qualifierInfosBC[i]).getQualifierInfo();
        }
        return new SigPolicyQualifiersBCFips(qualifierInfos);
    }

    @Override
    public IASN1String createASN1String(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1String) {
            return new ASN1StringBCFips((ASN1String) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1Primitive createASN1Primitive(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1Primitive) {
            return new ASN1PrimitiveBCFips((ASN1Primitive) encodableBCFips.getEncodable());
        }
        return null;
    }
}
