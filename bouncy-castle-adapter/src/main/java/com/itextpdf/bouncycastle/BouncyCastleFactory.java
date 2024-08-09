/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import com.itextpdf.bouncycastle.asn1.pcks.RSASSAPSSParamsBC;
import com.itextpdf.bouncycastle.asn1.tsp.TSTInfoBC;
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
import com.itextpdf.bouncycastle.asn1.x509.IssuingDistributionPointBC;
import com.itextpdf.bouncycastle.asn1.x509.KeyPurposeIdBC;
import com.itextpdf.bouncycastle.asn1.x509.KeyUsageBC;
import com.itextpdf.bouncycastle.asn1.x509.ReasonFlagsBC;
import com.itextpdf.bouncycastle.asn1.x509.SubjectPublicKeyInfoBC;
import com.itextpdf.bouncycastle.asn1.x509.TBSCertificateBC;
import com.itextpdf.bouncycastle.asn1.x509.TimeBC;
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
import com.itextpdf.bouncycastle.cert.ocsp.SingleRespBC;
import com.itextpdf.bouncycastle.cert.ocsp.UnknownStatusBC;
import com.itextpdf.bouncycastle.cms.CMSEnvelopedDataBC;
import com.itextpdf.bouncycastle.cms.CMSExceptionBC;
import com.itextpdf.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilderBC;
import com.itextpdf.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilderBC;
import com.itextpdf.bouncycastle.cms.jcajce.JceKeyAgreeEnvelopedRecipientBC;
import com.itextpdf.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipientBC;
import com.itextpdf.bouncycastle.crypto.modes.GCMBlockCipherBC;
import com.itextpdf.bouncycastle.openssl.PEMParserBC;
import com.itextpdf.bouncycastle.openssl.jcajce.JcaPEMKeyConverterBC;
import com.itextpdf.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilderBC;
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
import com.itextpdf.commons.bouncycastle.IBouncyCastleTestConstantsFactory;
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
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IRSASSAPSSParams;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
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
import com.itextpdf.commons.bouncycastle.asn1.x509.IIssuingDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IReasonFlags;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITime;
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
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;
import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSignerInfoGeneratorBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyAgreeEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.crypto.modes.IGCMBlockCipher;
import com.itextpdf.commons.bouncycastle.openssl.IPEMParser;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJcaPEMKeyConverter;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJceOpenSSLPKCS8DecryptorProviderBuilder;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentSignerBuilder;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;
import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponseGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Enumerated;
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
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
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
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceKeyAgreeEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

/**
 * This class implements {@link IBouncyCastleFactory} and creates bouncy-castle classes instances.
 */
public class BouncyCastleFactory implements IBouncyCastleFactory {

    private static final Provider PROVIDER = new BouncyCastleProvider();
    private static final String PROVIDER_NAME = PROVIDER.getName();
    private static final BouncyCastleTestConstantsFactory BOUNCY_CASTLE_TEST_CONSTANTS =
            new BouncyCastleTestConstantsFactory();

    /**
     * Creates {@link IBouncyCastleFactory} for usual bouncy-castle module.
     */
    public BouncyCastleFactory() {
        // Empty constructor.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlgorithmOid(String name) {
        try {
            AlgorithmIdentifier algorithmIdentifier = new DefaultSignatureAlgorithmIdentifierFinder().find(name);
            return algorithmIdentifier.getAlgorithm().getId();
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDigestAlgorithmOid(String name) {
        try {
            AlgorithmIdentifier algorithmIdentifier = new DefaultDigestAlgorithmIdentifierFinder().find(name);
            if (algorithmIdentifier != null) {
                return algorithmIdentifier.getAlgorithm().getId();
            }
        } catch (IllegalArgumentException ignored) {
            // Do nothing.
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlgorithmName(String oid) {
        return new DefaultAlgorithmNameFinder().getAlgorithmName(new ASN1ObjectIdentifier(oid));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1ObjectIdentifier) {
            return new ASN1ObjectIdentifierBC((ASN1ObjectIdentifier) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(String str) {
        return new ASN1ObjectIdentifierBC(str);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifierInstance(Object object) {
        return new ASN1ObjectIdentifierBC(ASN1ObjectIdentifier.getInstance(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1InputStream createASN1InputStream(InputStream stream) {
        return new ASN1InputStreamBC(stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1InputStream createASN1InputStream(byte[] bytes) {
        return new ASN1InputStreamBC(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1OctetString createASN1OctetString(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1OctetString) {
            return new ASN1OctetStringBC((ASN1OctetString) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1OctetString createASN1OctetString(IASN1TaggedObject taggedObject, boolean b) {
        return new ASN1OctetStringBC(taggedObject, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1OctetString createASN1OctetString(byte[] bytes) {
        return new ASN1OctetStringBC(ASN1OctetString.getInstance(bytes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Sequence createASN1Sequence(Object object) {
        if (object instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) object);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Sequence createASN1Sequence(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Sequence) {
            return new ASN1SequenceBC((ASN1Sequence) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Sequence createASN1Sequence(byte[] array) throws IOException {
        return new ASN1SequenceBC((ASN1Sequence) ASN1Primitive.fromByteArray(array));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Sequence createASN1SequenceInstance(Object object) {
        return new ASN1SequenceBC(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERSequence createDERSequence(IASN1EncodableVector encodableVector) {
        ASN1EncodableVectorBC vectorBC = (ASN1EncodableVectorBC) encodableVector;
        return new DERSequenceBC(vectorBC.getEncodableVector());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERSequence createDERSequence(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERSequenceBC(primitiveBC.getPrimitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1TaggedObject createASN1TaggedObject(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1TaggedObject) {
            return new ASN1TaggedObjectBC((ASN1TaggedObject) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Integer createASN1Integer(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Integer) {
            return new ASN1IntegerBC((ASN1Integer) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Integer createASN1Integer(int i) {
        return new ASN1IntegerBC(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Integer createASN1Integer(BigInteger i) {
        return new ASN1IntegerBC(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Set createASN1Set(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Set) {
            return new ASN1SetBC((ASN1Set) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Set createASN1Set(Object encodable) {
        return encodable instanceof ASN1Set ? new ASN1SetBC((ASN1Set) encodable) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Set createASN1Set(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBC taggedObjectBC = (ASN1TaggedObjectBC) taggedObject;
        return new ASN1SetBC(taggedObjectBC.getASN1TaggedObject(), b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Set createNullASN1Set() {
        return new ASN1SetBC(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream stream) {
        return new ASN1OutputStreamBC(stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream outputStream, String asn1Encoding) {
        return new ASN1OutputStreamBC(ASN1OutputStream.create(outputStream, asn1Encoding));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDEROctetString createDEROctetString(byte[] bytes) {
        return new DEROctetStringBC(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDEROctetString createDEROctetString(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof DEROctetString) {
            return new DEROctetStringBC((DEROctetString) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1EncodableVector createASN1EncodableVector() {
        return new ASN1EncodableVectorBC();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERNull createDERNull() {
        return DERNullBC.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERTaggedObjectBC(i, primitiveBC.getPrimitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERTaggedObjectBC(b, i, primitiveBC.getPrimitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERSet createDERSet(IASN1EncodableVector encodableVector) {
        ASN1EncodableVectorBC encodableVectorBC = (ASN1EncodableVectorBC) encodableVector;
        return new DERSetBC(encodableVectorBC.getEncodableVector());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERSet createDERSet(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new DERSetBC(primitiveBC.getPrimitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERSet createDERSet(ISignaturePolicyIdentifier identifier) {
        SignaturePolicyIdentifierBC identifierBC = (SignaturePolicyIdentifierBC) identifier;
        return new DERSetBC(identifierBC.getSignaturePolicyIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERSet createDERSet(IRecipientInfo recipientInfo) {
        RecipientInfoBC recipientInfoBC = (RecipientInfoBC) recipientInfo;
        return new DERSetBC(recipientInfoBC.getRecipientInfo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Enumerated createASN1Enumerated(int i) {
        return new ASN1EnumeratedBC(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Enumerated createASN1Enumerated(IASN1Encodable object) {
        ASN1EncodableBC encodable = (ASN1EncodableBC) object;
        if (encodable.getEncodable() instanceof ASN1Enumerated) {
            return new ASN1EnumeratedBC((ASN1Enumerated) encodable.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encoding createASN1Encoding() {
        return ASN1EncodingBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAttributeTable createAttributeTable(IASN1Set unat) {
        ASN1SetBC asn1SetBC = (ASN1SetBC) unat;
        return new AttributeTableBC(asn1SetBC.getASN1Set());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPKCSObjectIdentifiers createPKCSObjectIdentifiers() {
        return PKCSObjectIdentifiersBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues) {
        ASN1ObjectIdentifierBC attrTypeBc = (ASN1ObjectIdentifierBC) attrType;
        ASN1SetBC attrValuesBc = (ASN1SetBC) attrValues;
        return new AttributeBC(new Attribute(attrTypeBc.getASN1ObjectIdentifier(), attrValuesBc.getASN1Set()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContentInfo createContentInfo(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new ContentInfoBC(ContentInfo.getInstance(sequenceBC.getASN1Sequence()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContentInfo createContentInfo(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        return new ContentInfoBC(objectIdentifier, encodable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws TSPExceptionBC, IOException {
        ContentInfoBC contentInfoBC = (ContentInfoBC) contentInfo;
        try {
            return new TimeStampTokenBC(new TimeStampToken(contentInfoBC.getContentInfo()));
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISigningCertificate createSigningCertificate(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new SigningCertificateBC(SigningCertificate.getInstance(sequenceBC.getASN1Sequence()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence) {
        ASN1SequenceBC sequenceBC = (ASN1SequenceBC) sequence;
        return new SigningCertificateV2BC(SigningCertificateV2.getInstance(sequenceBC.getASN1Sequence()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPResponse createBasicOCSPResponse(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        return new BasicOCSPResponseBC(BasicOCSPResponse.getInstance(primitiveBC.getPrimitive()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPResponse createBasicOCSPResponse(byte[] bytes) {
        return new BasicOCSPResponseBC(BasicOCSPResponse.getInstance(bytes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPResp createBasicOCSPResp(IBasicOCSPResponse response) {
        BasicOCSPResponseBC responseBC = (BasicOCSPResponseBC) response;
        return new BasicOCSPRespBC(new BasicOCSPResp(responseBC.getBasicOCSPResponse()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPResp createBasicOCSPResp(Object response) {
        if (response instanceof BasicOCSPResp) {
            return new BasicOCSPRespBC((BasicOCSPResp) response);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPObjectIdentifiers createOCSPObjectIdentifiers() {
        return OCSPObjectIdentifiersBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm) {
        ASN1ObjectIdentifierBC algorithmBc = (ASN1ObjectIdentifierBC) algorithm;
        return new AlgorithmIdentifierBC(new AlgorithmIdentifier(algorithmBc.getASN1ObjectIdentifier(), null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1Encodable parameters) {
        ASN1ObjectIdentifierBC algorithmBc = (ASN1ObjectIdentifierBC) algorithm;
        ASN1EncodableBC encodableBc = (ASN1EncodableBC) parameters;
        return new AlgorithmIdentifierBC(
                new AlgorithmIdentifier(algorithmBc.getASN1ObjectIdentifier(), encodableBc.getEncodable()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRSASSAPSSParams createRSASSAPSSParams(IASN1Encodable encodable) {
        if (encodable == null) {
            throw new IllegalArgumentException("Expected non-null RSASSA-PSS parameter data");
        }
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        return new RSASSAPSSParamsBC(RSASSAPSSparams.getInstance(encodableBC.getEncodable()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRSASSAPSSParams createRSASSAPSSParamsWithMGF1(IASN1ObjectIdentifier digestAlgoOid, int saltLen,
                                                          int trailerField) {
        ASN1ObjectIdentifier oid = ((ASN1ObjectIdentifierBC) digestAlgoOid).getASN1ObjectIdentifier();
        AlgorithmIdentifier digestAlgo = new AlgorithmIdentifier(oid);
        AlgorithmIdentifier mgf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, digestAlgo);
        RSASSAPSSparams params = new RSASSAPSSparams(digestAlgo, mgf, new ASN1Integer(saltLen),
                new ASN1Integer(trailerField));
        return new RSASSAPSSParamsBC(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Provider getProvider() {
        return PROVIDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey) {
        return new JceKeyTransEnvelopedRecipientBC(new JceKeyTransEnvelopedRecipient(privateKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJceKeyAgreeEnvelopedRecipient createJceKeyAgreeEnvelopedRecipient(PrivateKey privateKey) {
        return new JceKeyAgreeEnvelopedRecipientBC(new JceKeyAgreeEnvelopedRecipient(privateKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaContentVerifierProviderBuilder createJcaContentVerifierProviderBuilder() {
        return new JcaContentVerifierProviderBuilderBC(new JcaContentVerifierProviderBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaSimpleSignerInfoVerifierBuilder createJcaSimpleSignerInfoVerifierBuilder() {
        return new JcaSimpleSignerInfoVerifierBuilderBC(new JcaSimpleSignerInfoVerifierBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaX509CertificateConverter createJcaX509CertificateConverter() {
        final IJcaX509CertificateConverter converter =
                new JcaX509CertificateConverterBC(new JcaX509CertificateConverter());
        converter.setProvider(PROVIDER);
        return converter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaDigestCalculatorProviderBuilder createJcaDigestCalculatorProviderBuilder() {
        return new JcaDigestCalculatorProviderBuilderBC(new JcaDigestCalculatorProviderBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateID createCertificateID(IDigestCalculator digestCalculator,
            IX509CertificateHolder certificateHolder,
            BigInteger bigInteger) throws OCSPExceptionBC {
        return new CertificateIDBC(digestCalculator, certificateHolder, bigInteger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateID createCertificateID() {
        return CertificateIDBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX509CertificateHolder createX509CertificateHolder(byte[] bytes) throws IOException {
        return new X509CertificateHolderBC(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaX509CertificateHolder createJcaX509CertificateHolder(X509Certificate certificate)
            throws CertificateEncodingException {
        return new JcaX509CertificateHolderBC(new JcaX509CertificateHolder(certificate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtension createExtension(IASN1ObjectIdentifier objectIdentifier,
            boolean critical, IASN1OctetString octetString) {
        return new ExtensionBC(new Extension(((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                critical, ((ASN1OctetStringBC) octetString).getASN1OctetString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtension createExtension() {
        return ExtensionBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtensions createExtensions(IExtension extension) {
        return new ExtensionsBC(extension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtensions createExtensions(IExtension[] extensions) {
        Extension[] exts = new Extension[extensions.length];
        for (int i = 0; i < extensions.length; ++i) {
            exts[i] = ((ExtensionBC) extensions[i]).getExtension();
        }
        return new ExtensionsBC(new Extensions(exts));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtensions createNullExtensions() {
        return new ExtensionsBC((Extensions) null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReqBuilder createOCSPReqBuilder() {
        return new OCSPReqBuilderBC(new OCSPReqBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISigPolicyQualifierInfo createSigPolicyQualifierInfo(IASN1ObjectIdentifier objectIdentifier,
            IDERIA5String string) {
        return new SigPolicyQualifierInfoBC(objectIdentifier, string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1String createASN1String(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1String) {
            return new ASN1StringBC((ASN1String) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive createASN1Primitive(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1Primitive) {
            return new ASN1PrimitiveBC((ASN1Primitive) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive createASN1Primitive(byte[] array) throws IOException {
        return new ASN1PrimitiveBC(array);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResp createOCSPResp(IOCSPResponse ocspResponse) {
        return new OCSPRespBC(ocspResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResp createOCSPResp(byte[] bytes) throws IOException {
        return new OCSPRespBC(new OCSPResp(bytes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResp createOCSPResp() {
        return OCSPRespBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResponse createOCSPResponse(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        return new OCSPResponseBC(respStatus, responseBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IResponseBytes createResponseBytes(IASN1ObjectIdentifier asn1ObjectIdentifier,
            IDEROctetString derOctetString) {
        return new ResponseBytesBC(asn1ObjectIdentifier, derOctetString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPRespBuilder createOCSPRespBuilderInstance() {
        return OCSPRespBuilderBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPRespBuilder createOCSPRespBuilder() {
        return new OCSPRespBuilderBC(new OCSPRespBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResponseStatus createOCSPResponseStatus(int status) {
        return new OCSPResponseStatusBC(new OCSPResponseStatus(status));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResponseStatus createOCSPResponseStatus() {
        return OCSPResponseStatusBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateStatus createCertificateStatus() {
        return CertificateStatusBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRevokedStatus createRevokedStatus(ICertificateStatus certificateStatus) {
        CertificateStatusBC certificateStatusBC = (CertificateStatusBC) certificateStatus;
        if (certificateStatusBC.getCertificateStatus() instanceof RevokedStatus) {
            return new RevokedStatusBC((RevokedStatus) certificateStatusBC.getCertificateStatus());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRevokedStatus createRevokedStatus(Date date, int i) {
        return new RevokedStatusBC(new RevokedStatus(date, i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERIA5String createDERIA5String(IASN1TaggedObject taggedObject, boolean b) {
        return new DERIA5StringBC(
                (DERIA5String)DERIA5String.getInstance(((ASN1TaggedObjectBC) taggedObject).getASN1TaggedObject(), b));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDERIA5String createDERIA5String(String str) {
        return new DERIA5StringBC(str);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICRLDistPoint createCRLDistPoint(Object object) {
        return new CRLDistPointBC(CRLDistPoint.getInstance(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IIssuingDistributionPoint createIssuingDistributionPoint(Object point) {
        return new IssuingDistributionPointBC(IssuingDistributionPoint.getInstance(point instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) point).getEncodable() : point));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IIssuingDistributionPoint createIssuingDistributionPoint(IDistributionPointName distributionPoint,
                                                                    boolean onlyContainsUserCerts,
                                                                    boolean onlyContainsCACerts,
                                                                    IReasonFlags onlySomeReasons, boolean indirectCRL,
                                                                    boolean onlyContainsAttributeCerts) {
        return new IssuingDistributionPointBC(new IssuingDistributionPoint(distributionPoint == null ? null :
                ((DistributionPointNameBC) distributionPoint).getDistributionPointName(), onlyContainsUserCerts,
                onlyContainsCACerts, onlySomeReasons == null ? null :
                ((ReasonFlagsBC) onlySomeReasons).getReasonFlags(), indirectCRL, onlyContainsAttributeCerts));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IReasonFlags createReasonFlags(int reasons) {
        return new ReasonFlagsBC(new ReasonFlags(reasons));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDistributionPointName createDistributionPointName() {
        return DistributionPointNameBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDistributionPointName createDistributionPointName(IGeneralNames generalNames) {
        return new DistributionPointNameBC(new DistributionPointName(((GeneralNamesBC)generalNames).getGeneralNames()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGeneralNames createGeneralNames(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof GeneralNames) {
            return new GeneralNamesBC((GeneralNames) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGeneralName createGeneralName() {
        return GeneralNameBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOtherHashAlgAndValue createOtherHashAlgAndValue(IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        return new OtherHashAlgAndValueBC(algorithmIdentifier, octetString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue) {
        return new SignaturePolicyIdBC(objectIdentifier, algAndValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue,
            ISigPolicyQualifierInfo... policyQualifiers) {
        SigPolicyQualifierInfo[] qualifierInfos = new SigPolicyQualifierInfo[policyQualifiers.length];
        for (int i = 0; i < qualifierInfos.length; ++i) {
            qualifierInfos[i] = ((SigPolicyQualifierInfoBC) policyQualifiers[i]).getSigPolicyQualifierInfo();
        }
        return new SignaturePolicyIdBC(objectIdentifier, algAndValue, qualifierInfos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISignaturePolicyIdentifier createSignaturePolicyIdentifier(ISignaturePolicyId policyId) {
        return new SignaturePolicyIdentifierBC(policyId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEnvelopedData createEnvelopedData(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        return new EnvelopedDataBC(originatorInfo, set, encryptedContentInfo, set1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientInfo createRecipientInfo(IKeyTransRecipientInfo keyTransRecipientInfo) {
        return new RecipientInfoBC(keyTransRecipientInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEncryptedContentInfo createEncryptedContentInfo(IASN1ObjectIdentifier data,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        return new EncryptedContentInfoBC(data, algorithmIdentifier, octetString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITBSCertificate createTBSCertificate(IASN1Encodable encodable) {
        return new TBSCertificateBC(TBSCertificate.getInstance(((ASN1EncodableBC) encodable).getEncodable()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITBSCertificate createTBSCertificate(byte[] bytes) {
        return new TBSCertificateBC(TBSCertificate.getInstance((bytes)));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IIssuerAndSerialNumber createIssuerAndSerialNumber(IX500Name issuer, BigInteger value) {
        return new IssuerAndSerialNumberBC(issuer, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientIdentifier createRecipientIdentifier(IIssuerAndSerialNumber issuerAndSerialNumber) {
        return new RecipientIdentifierBC(issuerAndSerialNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyTransRecipientInfo createKeyTransRecipientInfo(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        return new KeyTransRecipientInfoBC(recipientIdentifier, algorithmIdentifier, octetString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOriginatorInfo createNullOriginatorInfo() {
        return new OriginatorInfoBC(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICMSEnvelopedData createCMSEnvelopedData(byte[] bytes) throws CMSExceptionBC {
        try {
            return new CMSEnvelopedDataBC(new CMSEnvelopedData(bytes));
        } catch (CMSException e) {
            throw new CMSExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampRequestGenerator createTimeStampRequestGenerator() {
        return new TimeStampRequestGeneratorBC(new TimeStampRequestGenerator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampResponse createTimeStampResponse(byte[] respBytes) throws TSPExceptionBC, IOException {
        try {
            return new TimeStampResponseBC(new TimeStampResponse(respBytes));
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractOCSPException createAbstractOCSPException(Exception e) {
        return new OCSPExceptionBC(new OCSPException(e.getMessage()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IUnknownStatus createUnknownStatus() {
        return new UnknownStatusBC(new UnknownStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Dump createASN1Dump() {
        return ASN1DumpBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1BitString createASN1BitString(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1BitString) {
            return new ASN1BitStringBC((ASN1BitString) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1GeneralizedTime createASN1GeneralizedTime(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1GeneralizedTime) {
            return new ASN1GeneralizedTimeBC((ASN1GeneralizedTime) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1GeneralizedTime createASN1GeneralizedTime(Date date) {
        return new ASN1GeneralizedTimeBC(new ASN1GeneralizedTime(date));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1UTCTime createASN1UTCTime(IASN1Encodable encodable) {
        ASN1EncodableBC encodableBC = (ASN1EncodableBC) encodable;
        if (encodableBC.getEncodable() instanceof ASN1UTCTime) {
            return new ASN1UTCTimeBC((ASN1UTCTime) encodableBC.getEncodable());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaCertStore createJcaCertStore(List<Certificate> certificates) throws CertificateEncodingException {
        return new JcaCertStoreBC(new JcaCertStore(certificates));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampResponseGenerator createTimeStampResponseGenerator(ITimeStampTokenGenerator tokenGenerator,
            Set<String> algorithms) {
        return new TimeStampResponseGeneratorBC(tokenGenerator, algorithms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampRequest createTimeStampRequest(byte[] bytes) throws IOException {
        return new TimeStampRequestBC(new TimeStampRequest(bytes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaContentSignerBuilder createJcaContentSignerBuilder(String algorithm) {
        return new JcaContentSignerBuilderBC(new JcaContentSignerBuilder(algorithm));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaSignerInfoGeneratorBuilder createJcaSignerInfoGeneratorBuilder(
            IDigestCalculatorProvider digestCalcProviderProvider) {
        return new JcaSignerInfoGeneratorBuilderBC(digestCalcProviderProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampTokenGenerator createTimeStampTokenGenerator(ISignerInfoGenerator siGen, IDigestCalculator dgCalc,
            IASN1ObjectIdentifier policy) throws TSPExceptionBC {
        return new TimeStampTokenGeneratorBC(siGen, dgCalc, policy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX500Name createX500Name(X509Certificate certificate) throws CertificateEncodingException {
        byte[] tbsCertificate = certificate.getTBSCertificate();
        if (tbsCertificate.length != 0) {
            try {
                return new X500NameBC(X500Name.getInstance(
                        TBSCertificate.getInstance(ASN1Primitive.fromByteArray(tbsCertificate)).getSubject()));
            } catch (IOException ignored) {
                // Not expected to be thrown
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX500Name createX500Name(String s) {
        return new X500NameBC(new X500Name(s));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRespID createRespID(IX500Name x500Name) {
        return new RespIDBC(x500Name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPRespBuilder createBasicOCSPRespBuilder(IRespID respID) {
        return new BasicOCSPRespBuilderBC(respID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReq createOCSPReq(byte[] requestBytes) throws IOException {
        return new OCSPReqBC(new OCSPReq(requestBytes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX509v2CRLBuilder createX509v2CRLBuilder(IX500Name x500Name, Date date) {
        return new X509v2CRLBuilderBC(x500Name, date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaX509v3CertificateBuilder createJcaX509v3CertificateBuilder(X509Certificate signingCert,
            BigInteger certSerialNumber, Date startDate, Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        return new JcaX509v3CertificateBuilderBC(signingCert, certSerialNumber, startDate, endDate, subjectDnName,
                publicKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicConstraints createBasicConstraints(boolean b) {
        return new BasicConstraintsBC(new BasicConstraints(b));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicConstraints createBasicConstraints(int pathLength) {
        return new BasicConstraintsBC(new BasicConstraints(pathLength));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyUsage createKeyUsage() {
        return KeyUsageBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyUsage createKeyUsage(int i) {
        return new KeyUsageBC(new KeyUsage(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyPurposeId createKeyPurposeId() {
        return KeyPurposeIdBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyPurposeId createKeyPurposeId(IASN1ObjectIdentifier objectIdentifier) {
        return new KeyPurposeIdBC(KeyPurposeId.getInstance(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId purposeId) {
        return new ExtendedKeyUsageBC(purposeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId[] purposeIds) {
        return new ExtendedKeyUsageBC(purposeIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX509ExtensionUtils createX509ExtensionUtils(IDigestCalculator digestCalculator) {
        return new X509ExtensionUtilsBC(digestCalculator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISubjectPublicKeyInfo createSubjectPublicKeyInfo(Object object) {
        return new SubjectPublicKeyInfoBC(object instanceof ASN1EncodableBC ?
                ((ASN1EncodableBC) object).getEncodable() : object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICRLReason createCRLReason() {
        return CRLReasonBC.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITSTInfo createTSTInfo(IContentInfo contentInfo) throws AbstractTSPException, IOException {
        try {
            CMSTypedData content = new CMSSignedData(((ContentInfoBC) contentInfo).getContentInfo())
                    .getSignedContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            content.write(out);
            return new TSTInfoBC(TSTInfo.getInstance(ASN1Primitive.fromByteArray(out.toByteArray())));
        } catch (CMSException e) {
            throw new TSPExceptionBC(new TSPException("TSP parsing error: " + e.getMessage(), e.getCause()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleResp createSingleResp(IBasicOCSPResponse basicResp) {
        return new SingleRespBC(basicResp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate createX509Certificate(Object element) {
        return (X509Certificate) element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBouncyCastleTestConstantsFactory getBouncyCastleFactoryTestUtil() {
        return BOUNCY_CASTLE_TEST_CONSTANTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CRL createNullCrl() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPEMParser createPEMParser(Reader reader) {
        return new PEMParserBC(new PEMParser(reader));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJceOpenSSLPKCS8DecryptorProviderBuilder createJceOpenSSLPKCS8DecryptorProviderBuilder() {
        return new JceOpenSSLPKCS8DecryptorProviderBuilderBC(new JceOpenSSLPKCS8DecryptorProviderBuilder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaPEMKeyConverter createJcaPEMKeyConverter() {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        converter.setProvider(PROVIDER);
        return new JcaPEMKeyConverterBC(converter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITime createTime(Date date) {
        return new TimeBC(new Time(date));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITime createEndDate(X509Certificate certificate) {
        return createTime(certificate.getNotAfter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNullExtension(IExtension ext) {
        return ((ExtensionBC) ext).getExtension() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNull(IASN1Encodable encodable) {
        return ((ASN1EncodableBC) encodable).getEncodable() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecureRandom getSecureRandom() {
        return new SecureRandom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInApprovedOnlyMode() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] createCipherBytes(X509Certificate x509certificate, byte[] abyte0,
            IAlgorithmIdentifier algorithmIdentifier)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithmIdentifier.getAlgorithm().getId());
        try {
            cipher.init(Cipher.ENCRYPT_MODE, x509certificate);
        } catch (InvalidKeyException e) {
            cipher.init(Cipher.ENCRYPT_MODE, x509certificate.getPublicKey());
        }
        return cipher.doFinal(abyte0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void isEncryptionFeatureSupported(int encryptionType, boolean withCertificate) {
        //All features supported
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] generateHKDF(byte[] inputKey, byte[] salt, byte[] info) {
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
        HKDFParameters hkdfParameters = new HKDFParameters(inputKey, salt, info);
        hkdfBytesGenerator.init(hkdfParameters);
        byte[] hkdf = new byte[32];
        hkdfBytesGenerator.generateBytes(hkdf, 0, 32);

        return hkdf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] generateHMACSHA256Token(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HMacSHA256", this.getProvider());
        mac.init(new SecretKeySpec(key, "RawBytes"));
        return mac.doFinal(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] generateEncryptedKeyWithAES256NoPad(byte[] key, byte[] kek) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AESWrap", this.getProvider());
        cipher.init(Cipher.WRAP_MODE, new SecretKeySpec(kek, "AESWrap"));
        return cipher.wrap(new SecretKeySpec(key, "AESWrap"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGCMBlockCipher createGCMBlockCipher() {
        GCMBlockCipher cipher = (GCMBlockCipher) GCMBlockCipher.newInstance(AESEngine.newInstance());
        return new GCMBlockCipherBC(cipher);
    }
}
