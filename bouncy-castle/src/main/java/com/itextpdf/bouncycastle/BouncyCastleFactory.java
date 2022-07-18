package com.itextpdf.bouncycastle;

import com.itextpdf.bouncycastle.asn1.ASN1BitStringBC;
import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1EncodableVectorBC;
import com.itextpdf.bouncycastle.asn1.ASN1EncodingBC;
import com.itextpdf.bouncycastle.asn1.ASN1EnumeratedBC;
import com.itextpdf.bouncycastle.asn1.ASN1GeneralizedTimeBC;
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
import com.itextpdf.bouncycastle.asn1.ASN1UTCTimeBC;
import com.itextpdf.bouncycastle.asn1.DERIA5StringBC;
import com.itextpdf.bouncycastle.asn1.DERNullBC;
import com.itextpdf.bouncycastle.asn1.DEROctetStringBC;
import com.itextpdf.bouncycastle.asn1.DERSequenceBC;
import com.itextpdf.bouncycastle.asn1.DERSetBC;
import com.itextpdf.bouncycastle.asn1.DERTaggedObjectBC;
import com.itextpdf.bouncycastle.asn1.cms.AttributeBC;
import com.itextpdf.bouncycastle.asn1.cms.AttributeTableBC;
import com.itextpdf.bouncycastle.asn1.cms.ContentInfoBC;
import com.itextpdf.bouncycastle.asn1.cms.EncryptedContentInfoBC;
import com.itextpdf.bouncycastle.asn1.cms.EnvelopedDataBC;
import com.itextpdf.bouncycastle.asn1.cms.IssuerAndSerialNumberBC;
import com.itextpdf.bouncycastle.asn1.cms.KeyTransRecipientInfoBC;
import com.itextpdf.bouncycastle.asn1.cms.OriginatorInfoBC;
import com.itextpdf.bouncycastle.asn1.cms.RecipientIdentifierBC;
import com.itextpdf.bouncycastle.asn1.cms.RecipientInfoBC;
import com.itextpdf.bouncycastle.asn1.esf.OtherHashAlgAndValueBC;
import com.itextpdf.bouncycastle.asn1.esf.SigPolicyQualifierInfoBC;
import com.itextpdf.bouncycastle.asn1.esf.SigPolicyQualifiersBC;
import com.itextpdf.bouncycastle.asn1.esf.SignaturePolicyIdBC;
import com.itextpdf.bouncycastle.asn1.esf.SignaturePolicyIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ess.SigningCertificateBC;
import com.itextpdf.bouncycastle.asn1.ess.SigningCertificateV2BC;
import com.itextpdf.bouncycastle.asn1.ocsp.BasicOCSPResponseBC;
import com.itextpdf.bouncycastle.asn1.ocsp.OCSPObjectIdentifiersBC;
import com.itextpdf.bouncycastle.asn1.ocsp.OCSPResponseBC;
import com.itextpdf.bouncycastle.asn1.ocsp.OCSPResponseStatusBC;
import com.itextpdf.bouncycastle.asn1.ocsp.ResponseBytesBC;
import com.itextpdf.bouncycastle.asn1.pcks.PKCSObjectIdentifiersBC;
import com.itextpdf.bouncycastle.asn1.util.ASN1DumpBC;
import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.BasicConstraintsBC;
import com.itextpdf.bouncycastle.asn1.x509.CRLDistPointBC;
import com.itextpdf.bouncycastle.asn1.x509.CRLReasonBC;
import com.itextpdf.bouncycastle.asn1.x509.DistributionPointNameBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtendedKeyUsageBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtensionBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtensionsBC;
import com.itextpdf.bouncycastle.asn1.x509.GeneralNameBC;
import com.itextpdf.bouncycastle.asn1.x509.GeneralNamesBC;
import com.itextpdf.bouncycastle.asn1.x509.KeyPurposeIdBC;
import com.itextpdf.bouncycastle.asn1.x509.KeyUsageBC;
import com.itextpdf.bouncycastle.asn1.x509.SubjectPublicKeyInfoBC;
import com.itextpdf.bouncycastle.asn1.x509.TBSCertificateBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.cert.X509ExtensionUtilsBC;
import com.itextpdf.bouncycastle.cert.X509v2CRLBuilderBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaCertStoreBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaX509CertificateConverterBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaX509CertificateHolderBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilderBC;
import com.itextpdf.bouncycastle.cert.ocsp.BasicOCSPRespBC;
import com.itextpdf.bouncycastle.cert.ocsp.BasicOCSPRespBuilderBC;
import com.itextpdf.bouncycastle.cert.ocsp.CertificateIDBC;
import com.itextpdf.bouncycastle.cert.ocsp.CertificateStatusBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPExceptionBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPReqBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPReqBuilderBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPRespBC;
import com.itextpdf.bouncycastle.cert.ocsp.OCSPRespBuilderBC;
import com.itextpdf.bouncycastle.cert.ocsp.RespIDBC;
import com.itextpdf.bouncycastle.cert.ocsp.RevokedStatusBC;
import com.itextpdf.bouncycastle.cert.ocsp.UnknownStatusBC;
import com.itextpdf.bouncycastle.cms.CMSEnvelopedDataBC;
import com.itextpdf.bouncycastle.cms.CMSExceptionBC;
import com.itextpdf.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilderBC;
import com.itextpdf.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilderBC;
import com.itextpdf.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipientBC;
import com.itextpdf.bouncycastle.operator.jcajce.JcaContentSignerBuilderBC;
import com.itextpdf.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilderBC;
import com.itextpdf.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilderBC;
import com.itextpdf.bouncycastle.tsp.TSPExceptionBC;
import com.itextpdf.bouncycastle.tsp.TimeStampRequestBC;
import com.itextpdf.bouncycastle.tsp.TimeStampRequestGeneratorBC;
import com.itextpdf.bouncycastle.tsp.TimeStampResponseBC;
import com.itextpdf.bouncycastle.tsp.TimeStampResponseGeneratorBC;
import com.itextpdf.bouncycastle.tsp.TimeStampTokenBC;
import com.itextpdf.bouncycastle.tsp.TimeStampTokenGeneratorBC;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1BitString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;
import com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime;
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
import com.itextpdf.commons.bouncycastle.asn1.IASN1UTCTime;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.IDERNull;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEnvelopedData;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IBasicConstraints;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.IX509ExtensionUtils;
import com.itextpdf.commons.bouncycastle.cert.IX509v2CRLBuilder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509v3CertificateBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPRespBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;
import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSignerInfoGeneratorBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentSignerBuilder;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponseGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.OCSPRespBuilder;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

public class BouncyCastleFactory implements IBouncyCastleFactory {

    private static final String PROVIDER_NAME = new BouncyCastleProvider().getName();

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
    public IASN1ObjectIdentifier createASN1ObjectIdentifierInstance(Object object) {
        return new ASN1ObjectIdentifierBC(ASN1ObjectIdentifier.getInstance(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object));
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
    public IASN1OctetString createASN1OctetString(IASN1TaggedObject taggedObject, boolean b) {
        return new ASN1OctetStringBC(taggedObject, b);
    }

    @Override
    public IASN1OctetString createASN1OctetString(byte[] bytes) {
        return new ASN1OctetStringBC(ASN1OctetString.getInstance(bytes));
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
    public IASN1Sequence createASN1Sequence(byte[] array) throws IOException {
        return new ASN1SequenceBC((ASN1Sequence) ASN1Sequence.fromByteArray(array));
    }

    @Override
    public IASN1Sequence createASN1SequenceInstance(Object object) {
        return new ASN1SequenceBC(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object);
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
        return encodable instanceof ASN1Set ? new ASN1SetBC((ASN1Set) encodable) : null;
    }

    @Override
    public IASN1Set createASN1Set(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBC taggedObjectBC = (ASN1TaggedObjectBC) taggedObject;
        return new ASN1SetBC(taggedObjectBC.getASN1TaggedObject(), b);
    }

    @Override
    public IASN1Set createNullASN1Set() {
        return new ASN1SetBC(null);
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
    public IDEROctetString createDEROctetString(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof DEROctetString) {
            return new DEROctetStringBC((DEROctetString) encodableBC.getEncodable());
        }
        return null;
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
    public IDERSet createDERSet(IRecipientInfo recipientInfo) {
        RecipientInfoBC recipientInfoBC = (RecipientInfoBC) recipientInfo;
        return new DERSetBC(recipientInfoBC.getRecipientInfo());
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
        return new AttributeTableBC(asn1SetBC.getASN1Set());
    }

    @Override
    public IPKCSObjectIdentifiers createPKCSObjectIdentifiers() {
        return PKCSObjectIdentifiersBC.getInstance();
    }

    @Override
    public IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues) {
        ASN1ObjectIdentifierBC attrTypeBc = (ASN1ObjectIdentifierBC) attrType;
        ASN1SetBC attrValuesBc = (ASN1SetBC) attrValues;
        return new AttributeBC(new Attribute(attrTypeBc.getASN1ObjectIdentifier(), attrValuesBc.getASN1Set()));
    }

    @Override
    public IContentInfo createContentInfo(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new ContentInfoBC(ContentInfo.getInstance(sequenceBC.getASN1Sequence()));
    }

    @Override
    public IContentInfo createContentInfo(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        return new ContentInfoBC(objectIdentifier, encodable);
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
        return new SigningCertificateBC(SigningCertificate.getInstance(sequenceBC.getASN1Sequence()));
    }

    @Override
    public ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new SigningCertificateV2BC(SigningCertificateV2.getInstance(sequenceBC.getASN1Sequence()));
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
    public IBasicOCSPResp createBasicOCSPResp(Object response) {
        if (response instanceof BasicOCSPResp) {
            return new BasicOCSPRespBC((BasicOCSPResp) response);
        }
        return null;
    }

    @Override
    public IOCSPObjectIdentifiers createOCSPObjectIdentifiers() {
        return OCSPObjectIdentifiersBC.getInstance();
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm) {
        ASN1ObjectIdentifierBC algorithmBc = (ASN1ObjectIdentifierBC) algorithm;
        return new AlgorithmIdentifierBC(new AlgorithmIdentifier(algorithmBc.getASN1ObjectIdentifier(), null));
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1Encodable encodable) {
        ASN1ObjectIdentifierBC algorithmBc = (ASN1ObjectIdentifierBC) algorithm;
        ASN1EncodableBC encodableBc = (ASN1EncodableBC) encodable;
        return new AlgorithmIdentifierBC(
                new AlgorithmIdentifier(algorithmBc.getASN1ObjectIdentifier(), encodableBc.getEncodable()));
    }

    @Override
    public Provider createProvider() {
        return new BouncyCastleProvider();
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
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
    public ICertificateID createCertificateID() {
        return CertificateIDBC.getInstance();
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
    public IExtension createExtension() {
        return ExtensionBC.getInstance();
    }

    @Override
    public IExtensions createExtensions(IExtension extension) {
        return new ExtensionsBC(extension);
    }

    @Override
    public IExtensions createNullExtensions() {
        return new ExtensionsBC((Extensions) null);
    }

    @Override
    public IOCSPReqBuilder createOCSPReqBuilder() {
        return new OCSPReqBuilderBC(new OCSPReqBuilder());
    }

    @Override
    public ISigPolicyQualifiers createSigPolicyQualifiers(ISigPolicyQualifierInfo... qualifierInfosBC) {
        SigPolicyQualifierInfo[] qualifierInfos = new SigPolicyQualifierInfo[qualifierInfosBC.length];
        for (int i = 0; i < qualifierInfos.length; ++i) {
            qualifierInfos[i] = ((SigPolicyQualifierInfoBC) qualifierInfosBC[i]).getSigPolicyQualifierInfo();
        }
        return new SigPolicyQualifiersBC(qualifierInfos);
    }

    @Override
    public ISigPolicyQualifierInfo createSigPolicyQualifierInfo(IASN1ObjectIdentifier objectIdentifier,
            IDERIA5String string) {
        return new SigPolicyQualifierInfoBC(objectIdentifier, string);
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
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Primitive) {
            return new ASN1PrimitiveBC((ASN1Primitive) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IOCSPResp createOCSPResp(IOCSPResponse ocspResponse) {
        return new OCSPRespBC(ocspResponse);
    }

    @Override
    public IOCSPResp createOCSPResp(byte[] bytes) throws IOException {
        return new OCSPRespBC(new OCSPResp(bytes));
    }

    @Override
    public IOCSPResp createOCSPResp() {
        return OCSPRespBC.getInstance();
    }

    @Override
    public IOCSPResponse createOCSPResponse(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        return new OCSPResponseBC(respStatus, responseBytes);
    }

    @Override
    public IResponseBytes createResponseBytes(IASN1ObjectIdentifier asn1ObjectIdentifier,
            IDEROctetString derOctetString) {
        return new ResponseBytesBC(asn1ObjectIdentifier, derOctetString);
    }

    @Override
    public IOCSPRespBuilder createOCSPRespBuilderInstance() {
        return OCSPRespBuilderBC.getInstance();
    }

    @Override
    public IOCSPRespBuilder createOCSPRespBuilder() {
        return new OCSPRespBuilderBC(new OCSPRespBuilder());
    }

    @Override
    public IOCSPResponseStatus createOCSPResponseStatus(int status) {
        return new OCSPResponseStatusBC(new OCSPResponseStatus(status));
    }

    @Override
    public IOCSPResponseStatus createOCSPResponseStatus() {
        return OCSPResponseStatusBC.getInstance();
    }

    @Override
    public ICertificateStatus createCertificateStatus() {
        return CertificateStatusBC.getInstance();
    }

    @Override
    public IRevokedStatus createRevokedStatus(ICertificateStatus certificateStatus) {
        CertificateStatusBC certificateStatusBC = (CertificateStatusBC) certificateStatus;
        if (certificateStatusBC.getCertificateStatus() instanceof RevokedStatus) {
            return new RevokedStatusBC((RevokedStatus) certificateStatusBC.getCertificateStatus());
        }
        return null;
    }

    @Override
    public IRevokedStatus createRevokedStatus(Date date, int i) {
        return new RevokedStatusBC(new RevokedStatus(date, i));
    }

    @Override
    public IASN1Primitive createASN1Primitive(byte[] array) throws IOException {
        return new ASN1PrimitiveBC(array);
    }

    @Override
    public IDERIA5String createDERIA5String(IASN1TaggedObject taggedObject, boolean b) {
        return new DERIA5StringBC(
                DERIA5String.getInstance(((ASN1TaggedObjectBC) taggedObject).getASN1TaggedObject(), b));
    }

    @Override
    public IDERIA5String createDERIA5String(String str) {
        return new DERIA5StringBC(str);
    }

    @Override
    public ICRLDistPoint createCRLDistPoint(Object object) {
        return new CRLDistPointBC(CRLDistPoint.getInstance(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object));
    }

    @Override
    public IDistributionPointName createDistributionPointName() {
        return DistributionPointNameBC.getInstance();
    }

    @Override
    public IGeneralNames createGeneralNames(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof GeneralNames) {
            return new GeneralNamesBC((GeneralNames) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IGeneralName createGeneralName() {
        return GeneralNameBC.getInstance();
    }

    @Override
    public IOtherHashAlgAndValue createOtherHashAlgAndValue(IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        return new OtherHashAlgAndValueBC(algorithmIdentifier, octetString);
    }

    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue) {
        return new SignaturePolicyIdBC(objectIdentifier, algAndValue);
    }

    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue,
            ISigPolicyQualifiers policyQualifiers) {
        return new SignaturePolicyIdBC(objectIdentifier, algAndValue, policyQualifiers);
    }

    @Override
    public ISignaturePolicyIdentifier createSignaturePolicyIdentifier(ISignaturePolicyId policyId) {
        return new SignaturePolicyIdentifierBC(policyId);
    }

    @Override
    public IEnvelopedData createEnvelopedData(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        return new EnvelopedDataBC(originatorInfo, set, encryptedContentInfo, set1);
    }

    @Override
    public IRecipientInfo createRecipientInfo(IKeyTransRecipientInfo keyTransRecipientInfo) {
        return new RecipientInfoBC(keyTransRecipientInfo);
    }

    @Override
    public IEncryptedContentInfo createEncryptedContentInfo(IASN1ObjectIdentifier data,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        return new EncryptedContentInfoBC(data, algorithmIdentifier, octetString);
    }

    @Override
    public ITBSCertificate createTBSCertificate(IASN1Encodable encodable) {
        return new TBSCertificateBC(TBSCertificate.getInstance(((ASN1EncodableBC) encodable).getEncodable()));
    }

    @Override
    public IIssuerAndSerialNumber createIssuerAndSerialNumber(IX500Name issuer, BigInteger value) {
        return new IssuerAndSerialNumberBC(issuer, value);
    }

    @Override
    public IRecipientIdentifier createRecipientIdentifier(IIssuerAndSerialNumber issuerAndSerialNumber) {
        return new RecipientIdentifierBC(issuerAndSerialNumber);
    }

    @Override
    public IKeyTransRecipientInfo createKeyTransRecipientInfo(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        return new KeyTransRecipientInfoBC(recipientIdentifier, algorithmIdentifier, octetString);
    }

    @Override
    public IOriginatorInfo createNullOriginatorInfo() {
        return new OriginatorInfoBC(null);
    }

    @Override
    public ICMSEnvelopedData createCMSEnvelopedData(byte[] bytes) throws CMSExceptionBC {
        try {
            return new CMSEnvelopedDataBC(new CMSEnvelopedData(bytes));
        } catch (CMSException e) {
            throw new CMSExceptionBC(e);
        }
    }

    @Override
    public ITimeStampRequestGenerator createTimeStampRequestGenerator() {
        return new TimeStampRequestGeneratorBC(new TimeStampRequestGenerator());
    }

    @Override
    public ITimeStampResponse createTimeStampResponse(byte[] respBytes) throws TSPExceptionBC, IOException {
        try {
            return new TimeStampResponseBC(new TimeStampResponse(respBytes));
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    @Override
    public AbstractOCSPException createAbstractOCSPException(Exception e) {
        return new OCSPExceptionBC(new OCSPException(e.getMessage()));
    }

    @Override
    public IUnknownStatus createUnknownStatus() {
        return new UnknownStatusBC(new UnknownStatus());
    }

    @Override
    public IASN1Dump createASN1Dump() {
        return ASN1DumpBC.getInstance();
    }

    @Override
    public IASN1BitString createASN1BitString(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1BitString) {
            return new ASN1BitStringBC((ASN1BitString) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1GeneralizedTime createASN1GeneralizedTime(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1GeneralizedTime) {
            return new ASN1GeneralizedTimeBC((ASN1GeneralizedTime) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1UTCTime createASN1UTCTime(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1UTCTime) {
            return new ASN1UTCTimeBC((ASN1UTCTime) encodableBC.getEncodable());
        }
        return null;
    }

    @Override
    public IJcaCertStore createJcaCertStore(List<Certificate> certificates) throws CertificateEncodingException {
        return new JcaCertStoreBC(new JcaCertStore(certificates));
    }

    @Override
    public ITimeStampResponseGenerator createTimeStampResponseGenerator(ITimeStampTokenGenerator tokenGenerator,
            Set<String> algorithms) {
        return new TimeStampResponseGeneratorBC(tokenGenerator, algorithms);
    }

    @Override
    public ITimeStampRequest createTimeStampRequest(byte[] bytes) throws IOException {
        return new TimeStampRequestBC(new TimeStampRequest(bytes));
    }

    @Override
    public IJcaContentSignerBuilder createJcaContentSignerBuilder(String algorithm) {
        return new JcaContentSignerBuilderBC(new JcaContentSignerBuilder(algorithm));
    }

    @Override
    public IJcaSignerInfoGeneratorBuilder createJcaSignerInfoGeneratorBuilder(
            IDigestCalculatorProvider digestCalcProviderProvider) {
        return new JcaSignerInfoGeneratorBuilderBC(digestCalcProviderProvider);
    }

    @Override
    public ITimeStampTokenGenerator createTimeStampTokenGenerator(ISignerInfoGenerator siGen, IDigestCalculator dgCalc,
            IASN1ObjectIdentifier policy) throws TSPExceptionBC {
        return new TimeStampTokenGeneratorBC(siGen, dgCalc, policy);
    }

    @Override
    public IX500Name createX500Name(X509Certificate certificate) throws CertificateEncodingException, IOException {
        return new X500NameBC(X500Name.getInstance(
                TBSCertificate.getInstance(ASN1Primitive.fromByteArray(certificate.getTBSCertificate())).getSubject()));
    }

    @Override
    public IX500Name createX500Name(String s) {
        return new X500NameBC(new X500Name(s));
    }

    @Override
    public IRespID createRespID(IX500Name x500Name) {
        return new RespIDBC(x500Name);
    }

    @Override
    public IBasicOCSPRespBuilder createBasicOCSPRespBuilder(IRespID respID) {
        return new BasicOCSPRespBuilderBC(respID);
    }

    @Override
    public IOCSPReq createOCSPReq(byte[] requestBytes) throws IOException {
        return new OCSPReqBC(new OCSPReq(requestBytes));
    }

    @Override
    public IX509v2CRLBuilder createX509v2CRLBuilder(IX500Name x500Name, Date date) {
        return new X509v2CRLBuilderBC(x500Name, date);
    }

    @Override
    public IJcaX509v3CertificateBuilder createJcaX509v3CertificateBuilder(X509Certificate signingCert,
            BigInteger certSerialNumber, Date startDate, Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        return new JcaX509v3CertificateBuilderBC(signingCert, certSerialNumber, startDate, endDate, subjectDnName,
                publicKey);
    }

    @Override
    public IBasicConstraints createBasicConstraints(boolean b) {
        return new BasicConstraintsBC(new BasicConstraints(b));
    }

    @Override
    public IKeyUsage createKeyUsage() {
        return KeyUsageBC.getInstance();
    }

    @Override
    public IKeyUsage createKeyUsage(int i) {
        return new KeyUsageBC(new KeyUsage(i));
    }

    @Override
    public IKeyPurposeId createKeyPurposeId() {
        return KeyPurposeIdBC.getInstance();
    }

    @Override
    public IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId purposeId) {
        return new ExtendedKeyUsageBC(purposeId);
    }

    @Override
    public IX509ExtensionUtils createX509ExtensionUtils(IDigestCalculator digestCalculator) {
        return new X509ExtensionUtilsBC(digestCalculator);
    }

    @Override
    public ISubjectPublicKeyInfo createSubjectPublicKeyInfo(Object object) {
        return new SubjectPublicKeyInfoBC(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object);
    }
    
    @Override
    public ICRLReason createCRLReason() {
        return CRLReasonBC.getInstance();
    }
}
