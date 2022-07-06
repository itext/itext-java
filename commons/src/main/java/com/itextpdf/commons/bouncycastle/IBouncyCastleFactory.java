package com.itextpdf.commons.bouncycastle;

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
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;
import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public interface IBouncyCastleFactory {
    IASN1ObjectIdentifier createASN1ObjectIdentifier(IASN1Encodable encodable);

    IASN1ObjectIdentifier createASN1ObjectIdentifier(String str);

    IASN1ObjectIdentifier createASN1ObjectIdentifierInstance(Object object);

    IASN1InputStream createASN1InputStream(InputStream stream);

    IASN1InputStream createASN1InputStream(byte[] bytes);

    IASN1OctetString createASN1OctetString(IASN1Primitive primitive);

    IASN1OctetString createASN1OctetString(IASN1Encodable encodable);
    
    IASN1OctetString createASN1OctetString(IASN1TaggedObject taggedObject, boolean b);

    IASN1Sequence createASN1Sequence(Object object);

    IASN1Sequence createASN1Sequence(IASN1Encodable encodable);

    IASN1Sequence createASN1SequenceInstance(Object object);

    IDERSequence createDERSequence(IASN1EncodableVector encodableVector);

    IDERSequence createDERSequence(IASN1Primitive primitive);

    IASN1TaggedObject createASN1TaggedObject(IASN1Encodable encodable);

    IASN1Integer createASN1Integer(IASN1Encodable encodable);

    IASN1Integer createASN1Integer(int i);

    IASN1Integer createASN1Integer(BigInteger i);

    IASN1Set createASN1Set(IASN1Encodable encodable);

    IASN1Set createASN1Set(Object encodable);

    IASN1Set createASN1Set(IASN1TaggedObject taggedObject, boolean b);

    IASN1Set createNullASN1Set();

    IASN1OutputStream createASN1OutputStream(OutputStream stream);

    IASN1OutputStream createASN1OutputStream(OutputStream outputStream, String asn1Encoding);

    IDEROctetString createDEROctetString(byte[] bytes);

    IDEROctetString createDEROctetString(IASN1Encodable encodable);

    IASN1EncodableVector createASN1EncodableVector();

    IDERNull createDERNull();

    IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive);

    IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive);

    IDERSet createDERSet(IASN1EncodableVector encodableVector);

    IDERSet createDERSet(IASN1Primitive primitive);

    IDERSet createDERSet(ISignaturePolicyIdentifier identifier);

    IDERSet createDERSet(IRecipientInfo recipientInfo);

    IASN1Enumerated createASN1Enumerated(int i);

    IASN1Encoding createASN1Encoding();

    IAttributeTable createAttributeTable(IASN1Set unat);

    IPKCSObjectIdentifiers createPKCSObjectIdentifiers();

    IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues);

    IContentInfo createContentInfo(IASN1Sequence sequence);

    IContentInfo createContentInfo(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable);

    ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws AbstractTSPException, IOException;

    ISigningCertificate createSigningCertificate(IASN1Sequence sequence);

    ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence);

    IBasicOCSPResponse createBasicOCSPResponse(IASN1Primitive primitive);

    IBasicOCSPResp createBasicOCSPResp(IBasicOCSPResponse response);

    IBasicOCSPResp createBasicOCSPResp(Object response);

    IOCSPObjectIdentifiers createOCSPObjectIdentifiers();

    IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm);

    IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1Encodable encodable);

    Provider createProvider();

    IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey);

    IJcaContentVerifierProviderBuilder createJcaContentVerifierProviderBuilder();

    IJcaSimpleSignerInfoVerifierBuilder createJcaSimpleSignerInfoVerifierBuilder();

    IJcaX509CertificateConverter createJcaX509CertificateConverter();

    IJcaDigestCalculatorProviderBuilder createJcaDigestCalculatorProviderBuilder();

    ICertificateID createCertificateID(IDigestCalculator digestCalculator, IX509CertificateHolder certificateHolder,
            BigInteger bigInteger) throws AbstractOCSPException;

    ICertificateID createCertificateID();

    IX509CertificateHolder createX509CertificateHolder(byte[] bytes) throws IOException;

    IJcaX509CertificateHolder createJcaX509CertificateHolder(X509Certificate certificate)
            throws CertificateEncodingException;

    IExtension createExtension(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString);

    IExtension createExtension();

    IExtensions createExtensions(IExtension extension);

    IOCSPReqBuilder createOCSPReqBuilder();

    ISigPolicyQualifiers createSigPolicyQualifiers(ISigPolicyQualifierInfo... qualifierInfosBC);

    ISigPolicyQualifierInfo createSigPolicyQualifierInfo(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string);

    IASN1String createASN1String(IASN1Encodable encodable);

    IASN1Primitive createASN1Primitive(IASN1Encodable encodable);
    
    IOCSPResp createOCSPResp(IOCSPResponse ocspResponse);

    IOCSPResp createOCSPResp(byte[] bytes) throws IOException;

    IOCSPResponse createOCSPResponse(IOCSPResponseStatus respStatus, IResponseBytes responseBytes);

    IResponseBytes createResponseBytes(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString);

    IOCSPRespBuilder createOCSPRespBuilder();

    IOCSPResponseStatus createOCSPResponseStatus(int status);

    IOCSPResponseStatus createOCSPResponseStatus();

    ICertificateStatus createCertificateStatus();

    IRevokedStatus createRevokedStatus(ICertificateStatus certificateStatus);

    IASN1Primitive createASN1Primitive(byte[] array) throws IOException;
    
    IDERIA5String createDERIA5String(IASN1TaggedObject taggedObject, boolean b);
    
    IDERIA5String createDERIA5String(String str);

    ICRLDistPoint createCRLDistPoint(Object object);

    IDistributionPointName createDistributionPointName();

    IGeneralNames createGeneralNames(IASN1Encodable encodable);

    IGeneralName createGeneralName();

    IOtherHashAlgAndValue createOtherHashAlgAndValue(IAlgorithmIdentifier algorithmIdentifier,
                                                     IASN1OctetString octetString);

    ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
                                               IOtherHashAlgAndValue algAndValue,
                                               ISigPolicyQualifiers policyQualifiers);

    ISignaturePolicyIdentifier createSignaturePolicyIdentifier(ISignaturePolicyId policyId);

    IEnvelopedData createEnvelopedData(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1);

    IRecipientInfo createRecipientInfo(IKeyTransRecipientInfo keyTransRecipientInfo);

    IEncryptedContentInfo createEncryptedContentInfo(IASN1ObjectIdentifier data,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString);

    ITBSCertificate createTBSCertificate(Object object);

    IIssuerAndSerialNumber createIssuerAndSerialNumber(IX500Name issuer, BigInteger value);

    IRecipientIdentifier createRecipientIdentifier(IIssuerAndSerialNumber issuerAndSerialNumber);

    IKeyTransRecipientInfo createKeyTransRecipientInfo(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString);

    IOriginatorInfo createNullOriginatorInfo();
}

