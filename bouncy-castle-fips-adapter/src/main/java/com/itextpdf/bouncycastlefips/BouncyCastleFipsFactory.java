/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.bouncycastlefips;

import com.itextpdf.bouncycastlefips.asn1.ASN1BitStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableVectorBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EncodingBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1EnumeratedBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1GeneralizedTimeBCFips;
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
import com.itextpdf.bouncycastlefips.asn1.ASN1UTCTimeBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERIA5StringBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERNullBCFips;
import com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERSequenceBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERSetBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERTaggedObjectBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.AttributeBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.AttributeTableBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.ContentInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.EncryptedContentInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.EnvelopedDataBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.IssuerAndSerialNumberBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.KeyTransRecipientInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.OriginatorInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.RecipientIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.cms.RecipientInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.OtherHashAlgAndValueBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SigPolicyQualifierInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SigPolicyQualifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SignaturePolicyIdBCFips;
import com.itextpdf.bouncycastlefips.asn1.esf.SignaturePolicyIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ess.SigningCertificateBCFips;
import com.itextpdf.bouncycastlefips.asn1.ess.SigningCertificateV2BCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.BasicOCSPResponseBCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPObjectIdentifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPResponseBCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPResponseStatusBCFips;
import com.itextpdf.bouncycastlefips.asn1.ocsp.ResponseBytesBCFips;
import com.itextpdf.bouncycastlefips.asn1.pcks.PKCSObjectIdentifiersBCFips;
import com.itextpdf.bouncycastlefips.asn1.tsp.TSTInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.util.ASN1DumpBCFips;
import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.BasicConstraintsBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.CRLDistPointBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.CRLReasonBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.DistributionPointNameBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtendedKeyUsageBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionsBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.GeneralNameBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.GeneralNamesBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.KeyPurposeIdBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.KeyUsageBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.SubjectPublicKeyInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.TBSCertificateBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.cert.X509ExtensionUtilsBCFips;
import com.itextpdf.bouncycastlefips.cert.X509v2CRLBuilderBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaCertStoreBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaX509CertificateConverterBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaX509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaX509v3CertificateBuilderBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.BasicOCSPRespBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.BasicOCSPRespBuilderBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.CertificateIDBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.CertificateStatusBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPExceptionBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPReqBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPReqBuilderBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPRespBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.OCSPRespBuilderBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.RespIDBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.RevokedStatusBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.SingleRespBCFips;
import com.itextpdf.bouncycastlefips.cert.ocsp.UnknownStatusBCFips;
import com.itextpdf.bouncycastlefips.cms.CMSEnvelopedDataBCFips;
import com.itextpdf.bouncycastlefips.cms.CMSExceptionBCFips;
import com.itextpdf.bouncycastlefips.cms.jcajce.JcaSignerInfoGeneratorBuilderBCFips;
import com.itextpdf.bouncycastlefips.cms.jcajce.JcaSimpleSignerInfoVerifierBuilderBCFips;
import com.itextpdf.bouncycastlefips.cms.jcajce.JceKeyTransEnvelopedRecipientBCFips;
import com.itextpdf.bouncycastlefips.operator.jcajce.JcaContentSignerBuilderBCFips;
import com.itextpdf.bouncycastlefips.operator.jcajce.JcaContentVerifierProviderBuilderBCFips;
import com.itextpdf.bouncycastlefips.operator.jcajce.JcaDigestCalculatorProviderBuilderBCFips;
import com.itextpdf.bouncycastlefips.tsp.TSPExceptionBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampRequestBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampRequestGeneratorBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampResponseBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampResponseGeneratorBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampTokenBCFips;
import com.itextpdf.bouncycastlefips.tsp.TimeStampTokenGeneratorBCFips;
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
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
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
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DLOutputStream;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.tsp.TSTInfo;
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
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

/**
 * This class implements {@link IBouncyCastleFactory} and creates bouncy-castle FIPS classes instances.
 */
public class BouncyCastleFipsFactory implements IBouncyCastleFactory {

    private static final String PROVIDER_NAME = new BouncyCastleFipsProvider().getName();
    private static final BouncyCastleFipsTestConstantsFactory BOUNCY_CASTLE_FIPS_TEST_CONSTANTS = new BouncyCastleFipsTestConstantsFactory();


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
    public IASN1ObjectIdentifier createASN1ObjectIdentifierInstance(Object object) {
        return new ASN1ObjectIdentifierBCFips(ASN1ObjectIdentifier.getInstance(object instanceof ASN1EncodableBCFips ?
                ((ASN1EncodableBCFips) object).getEncodable() : object));
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
    public IASN1OctetString createASN1OctetString(IASN1TaggedObject taggedObject, boolean b) {
        return new ASN1OctetStringBCFips(taggedObject, b);
    }

    @Override
    public IASN1OctetString createASN1OctetString(byte[] bytes) {
        return new ASN1OctetStringBCFips(ASN1OctetString.getInstance(bytes));
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
    public IASN1Sequence createASN1Sequence(byte[] array) throws IOException {
        return new ASN1SequenceBCFips((ASN1Sequence) ASN1Sequence.fromByteArray(array));
    }

    @Override
    public IASN1Sequence createASN1SequenceInstance(Object object) {
        return new ASN1SequenceBCFips(object instanceof ASN1EncodableBCFips ?
                ((ASN1EncodableBCFips) object).getEncodable() : object);
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
        return encodable instanceof ASN1Set ? new ASN1SetBCFips((ASN1Set) encodable) : null;
    }

    @Override
    public IASN1Set createASN1Set(IASN1TaggedObject taggedObject, boolean b) {
        ASN1TaggedObjectBCFips taggedObjectBCFips = (ASN1TaggedObjectBCFips) taggedObject;
        return new ASN1SetBCFips(taggedObjectBCFips.getTaggedObject(), b);
    }

    @Override
    public IASN1Set createNullASN1Set() {
        return new ASN1SetBCFips(null);
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
    public IDEROctetString createDEROctetString(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof DEROctetString) {
            return new DEROctetStringBCFips((DEROctetString) encodableBCFips.getEncodable());
        }
        return null;
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
    public IDERSet createDERSet(IRecipientInfo recipientInfo) {
        RecipientInfoBCFips recipientInfoBCFips = (RecipientInfoBCFips) recipientInfo;
        return new DERSetBCFips(recipientInfoBCFips.getRecipientInfo());

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
        return new AttributeTableBCFips(asn1SetBCFips.getASN1Set());
    }

    @Override
    public IPKCSObjectIdentifiers createPKCSObjectIdentifiers() {
        return PKCSObjectIdentifiersBCFips.getInstance();
    }

    @Override
    public IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues) {
        ASN1ObjectIdentifierBCFips attrTypeBCFips = (ASN1ObjectIdentifierBCFips) attrType;
        ASN1SetBCFips attrValuesBCFips = (ASN1SetBCFips) attrValues;
        return new AttributeBCFips(
                new Attribute(attrTypeBCFips.getASN1ObjectIdentifier(), attrValuesBCFips.getASN1Set()));
    }

    @Override
    public IContentInfo createContentInfo(IASN1Sequence sequence) {
        ASN1SequenceBCFips sequenceBCFips = (ASN1SequenceBCFips) sequence;
        return new ContentInfoBCFips(ContentInfo.getInstance(sequenceBCFips.getASN1Sequence()));
    }

    @Override
    public IContentInfo createContentInfo(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        return new ContentInfoBCFips(objectIdentifier, encodable);
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
        return new SigningCertificateBCFips(SigningCertificate.getInstance(sequenceBCFips.getASN1Sequence()));
    }

    @Override
    public ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence) {
        ASN1SequenceBCFips sequenceBCFips = (ASN1SequenceBCFips) sequence;
        return new SigningCertificateV2BCFips(SigningCertificateV2.getInstance(sequenceBCFips.getASN1Sequence()));
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
    public IBasicOCSPResp createBasicOCSPResp(Object response) {
        if (response instanceof BasicOCSPResp) {
            return new BasicOCSPRespBCFips((BasicOCSPResp) response);
        }
        return null;
    }

    @Override
    public IOCSPObjectIdentifiers createOCSPObjectIdentifiers() {
        return OCSPObjectIdentifiersBCFips.getInstance();
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm) {
        ASN1ObjectIdentifierBCFips algorithmBCFips = (ASN1ObjectIdentifierBCFips) algorithm;
        return new AlgorithmIdentifierBCFips(new AlgorithmIdentifier(algorithmBCFips.getASN1ObjectIdentifier(), null));
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm,
            IASN1Encodable encodable) {
        ASN1ObjectIdentifierBCFips algorithmBCFips = (ASN1ObjectIdentifierBCFips) algorithm;
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        return new AlgorithmIdentifierBCFips(
                new AlgorithmIdentifier(algorithmBCFips.getASN1ObjectIdentifier(), encodableBCFips.getEncodable()));
    }

    @Override
    public Provider createProvider() {
        return new BouncyCastleFipsProvider();
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
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
    public ICertificateID createCertificateID() {
        return CertificateIDBCFips.getInstance();
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
    public IExtension createExtension() {
        return ExtensionBCFips.getInstance();
    }

    @Override
    public IExtensions createExtensions(IExtension extension) {
        return new ExtensionsBCFips(extension);
    }

    @Override
    public IExtensions createNullExtensions() {
        return new ExtensionsBCFips((Extensions) null);
    }

    @Override
    public IOCSPReqBuilder createOCSPReqBuilder() {
        return new OCSPReqBuilderBCFips(new OCSPReqBuilder());
    }

    @Override
    public ISigPolicyQualifiers createSigPolicyQualifiers(ISigPolicyQualifierInfo... qualifierInfosBCFips) {
        SigPolicyQualifierInfo[] qualifierInfos = new SigPolicyQualifierInfo[qualifierInfosBCFips.length];
        for (int i = 0; i < qualifierInfos.length; ++i) {
            qualifierInfos[i] = ((SigPolicyQualifierInfoBCFips) qualifierInfosBCFips[i]).getQualifierInfo();
        }
        return new SigPolicyQualifiersBCFips(qualifierInfos);
    }

    @Override
    public ISigPolicyQualifierInfo createSigPolicyQualifierInfo(IASN1ObjectIdentifier objectIdentifier,
            IDERIA5String string) {
        return new SigPolicyQualifierInfoBCFips(objectIdentifier, string);
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

    @Override
    public IOCSPResp createOCSPResp(IOCSPResponse ocspResponse) {
        return new OCSPRespBCFips(ocspResponse);
    }

    @Override
    public IOCSPResp createOCSPResp(byte[] bytes) throws IOException {
        return new OCSPRespBCFips(new OCSPResp(bytes));
    }

    @Override
    public IOCSPResp createOCSPResp() {
        return OCSPRespBCFips.getInstance();
    }

    @Override
    public IOCSPResponse createOCSPResponse(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        return new OCSPResponseBCFips(respStatus, responseBytes);
    }

    @Override
    public IResponseBytes createResponseBytes(IASN1ObjectIdentifier asn1ObjectIdentifier,
            IDEROctetString derOctetString) {
        return new ResponseBytesBCFips(asn1ObjectIdentifier, derOctetString);
    }

    @Override
    public IOCSPRespBuilder createOCSPRespBuilderInstance() {
        return OCSPRespBuilderBCFips.getInstance();
    }

    @Override
    public IOCSPRespBuilder createOCSPRespBuilder() {
        return new OCSPRespBuilderBCFips(new OCSPRespBuilder());
    }

    @Override
    public IOCSPResponseStatus createOCSPResponseStatus(int status) {
        return new OCSPResponseStatusBCFips(new OCSPResponseStatus(status));
    }

    @Override
    public IOCSPResponseStatus createOCSPResponseStatus() {
        return OCSPResponseStatusBCFips.getInstance();
    }

    @Override
    public ICertificateStatus createCertificateStatus() {
        return CertificateStatusBCFips.getInstance();
    }

    @Override
    public IRevokedStatus createRevokedStatus(ICertificateStatus certificateStatus) {
        CertificateStatusBCFips certificateStatusBCFips = (CertificateStatusBCFips) certificateStatus;
        if (certificateStatusBCFips.getCertificateStatus() instanceof RevokedStatus) {
            return new RevokedStatusBCFips((RevokedStatus) certificateStatusBCFips.getCertificateStatus());
        }
        return null;
    }

    @Override
    public IRevokedStatus createRevokedStatus(Date date, int i) {
        return new RevokedStatusBCFips(new RevokedStatus(date, i));
    }

    @Override
    public IASN1Primitive createASN1Primitive(byte[] array) throws IOException {
        return new ASN1PrimitiveBCFips(array);
    }

    @Override
    public IDERIA5String createDERIA5String(IASN1TaggedObject taggedObject, boolean b) {
        return new DERIA5StringBCFips(DERIA5String.getInstance(
                ((ASN1TaggedObjectBCFips) taggedObject).getTaggedObject(), b));
    }

    @Override
    public IDERIA5String createDERIA5String(String str) {
        return new DERIA5StringBCFips(str);
    }

    @Override
    public ICRLDistPoint createCRLDistPoint(Object object) {
        return new CRLDistPointBCFips(CRLDistPoint.getInstance(object instanceof ASN1EncodableBCFips ?
                ((ASN1EncodableBCFips) object).getEncodable() : object));
    }

    @Override
    public IDistributionPointName createDistributionPointName() {
        return DistributionPointNameBCFips.getInstance();
    }

    @Override
    public IGeneralNames createGeneralNames(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof GeneralNames) {
            return new GeneralNamesBCFips((GeneralNames) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IGeneralName createGeneralName() {
        return GeneralNameBCFips.getInstance();
    }

    @Override
    public IOtherHashAlgAndValue createOtherHashAlgAndValue(IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        return new OtherHashAlgAndValueBCFips(algorithmIdentifier, octetString);
    }

    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue) {
        return new SignaturePolicyIdBCFips(objectIdentifier, algAndValue);
    }

    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue,
            ISigPolicyQualifiers policyQualifiers) {
        return new SignaturePolicyIdBCFips(objectIdentifier, algAndValue, policyQualifiers);
    }

    @Override
    public ISignaturePolicyIdentifier createSignaturePolicyIdentifier(ISignaturePolicyId policyId) {
        return new SignaturePolicyIdentifierBCFips(policyId);
    }

    @Override
    public IEnvelopedData createEnvelopedData(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        return new EnvelopedDataBCFips(originatorInfo, set, encryptedContentInfo, set1);
    }

    @Override
    public IRecipientInfo createRecipientInfo(IKeyTransRecipientInfo keyTransRecipientInfo) {
        return new RecipientInfoBCFips(keyTransRecipientInfo);
    }

    @Override
    public IEncryptedContentInfo createEncryptedContentInfo(IASN1ObjectIdentifier data,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        return new EncryptedContentInfoBCFips(data, algorithmIdentifier, octetString);
    }

    @Override
    public ITBSCertificate createTBSCertificate(IASN1Encodable encodable) {
        return new TBSCertificateBCFips(TBSCertificate.getInstance(((ASN1EncodableBCFips) encodable).getEncodable()));
    }

    @Override
    public IIssuerAndSerialNumber createIssuerAndSerialNumber(IX500Name issuer, BigInteger value) {
        return new IssuerAndSerialNumberBCFips(issuer, value);
    }

    @Override
    public IRecipientIdentifier createRecipientIdentifier(IIssuerAndSerialNumber issuerAndSerialNumber) {
        return new RecipientIdentifierBCFips(issuerAndSerialNumber);
    }

    @Override
    public IKeyTransRecipientInfo createKeyTransRecipientInfo(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        return new KeyTransRecipientInfoBCFips(recipientIdentifier, algorithmIdentifier, octetString);
    }

    @Override
    public IOriginatorInfo createNullOriginatorInfo() {
        return new OriginatorInfoBCFips(null);
    }

    @Override
    public ICMSEnvelopedData createCMSEnvelopedData(byte[] bytes) throws CMSExceptionBCFips {
        try {
            return new CMSEnvelopedDataBCFips(new CMSEnvelopedData(bytes));
        } catch (CMSException e) {
            throw new CMSExceptionBCFips(e);
        }
    }

    @Override
    public ITimeStampRequestGenerator createTimeStampRequestGenerator() {
        return new TimeStampRequestGeneratorBCFips(new TimeStampRequestGenerator());
    }

    @Override
    public ITimeStampResponse createTimeStampResponse(byte[] respBytes) throws TSPExceptionBCFips, IOException {
        try {
            return new TimeStampResponseBCFips(new TimeStampResponse(respBytes));
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
    }

    @Override
    public AbstractOCSPException createAbstractOCSPException(Exception e) {
        return new OCSPExceptionBCFips(new OCSPException(e.getMessage()));
    }

    @Override
    public IUnknownStatus createUnknownStatus() {
        return new UnknownStatusBCFips(new UnknownStatus());
    }

    @Override
    public IASN1Dump createASN1Dump() {
        return ASN1DumpBCFips.getInstance();
    }

    @Override
    public IASN1BitString createASN1BitString(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1BitString) {
            return new ASN1BitStringBCFips((ASN1BitString) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1GeneralizedTime createASN1GeneralizedTime(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1GeneralizedTime) {
            return new ASN1GeneralizedTimeBCFips((ASN1GeneralizedTime) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IASN1UTCTime createASN1UTCTime(IASN1Encodable encodable) {
        ASN1EncodableBCFips encodableBCFips = (ASN1EncodableBCFips) encodable;
        if (encodableBCFips.getEncodable() instanceof ASN1UTCTime) {
            return new ASN1UTCTimeBCFips((ASN1UTCTime) encodableBCFips.getEncodable());
        }
        return null;
    }

    @Override
    public IJcaCertStore createJcaCertStore(List<Certificate> certificates) throws CertificateEncodingException {
        return new JcaCertStoreBCFips(new JcaCertStore(certificates));
    }

    @Override
    public ITimeStampResponseGenerator createTimeStampResponseGenerator(ITimeStampTokenGenerator tokenGenerator,
            Set<String> algorithms) {
        return new TimeStampResponseGeneratorBCFips(tokenGenerator, algorithms);
    }

    @Override
    public ITimeStampRequest createTimeStampRequest(byte[] bytes) throws IOException {
        return new TimeStampRequestBCFips(new TimeStampRequest(bytes));
    }

    @Override
    public IJcaContentSignerBuilder createJcaContentSignerBuilder(String algorithm) {
        return new JcaContentSignerBuilderBCFips(new JcaContentSignerBuilder(algorithm));
    }

    @Override
    public IJcaSignerInfoGeneratorBuilder createJcaSignerInfoGeneratorBuilder(
            IDigestCalculatorProvider digestCalcProviderProvider) {
        return new JcaSignerInfoGeneratorBuilderBCFips(digestCalcProviderProvider);
    }

    @Override
    public ITimeStampTokenGenerator createTimeStampTokenGenerator(ISignerInfoGenerator siGen, IDigestCalculator dgCalc,
            IASN1ObjectIdentifier policy) throws TSPExceptionBCFips {
        return new TimeStampTokenGeneratorBCFips(siGen, dgCalc, policy);
    }

    @Override
    public IX500Name createX500Name(X509Certificate certificate) throws CertificateEncodingException, IOException {
        byte[] tbsCertificate = certificate.getTBSCertificate();
        if (tbsCertificate.length != 0) {
            return new X500NameBCFips(X500Name.getInstance(
                    TBSCertificate.getInstance(ASN1Primitive.fromByteArray(tbsCertificate)).getSubject()));
        } else {
            return null;
        }
    }

    @Override
    public IX500Name createX500Name(String s) {
        return new X500NameBCFips(new X500Name(s));
    }

    @Override
    public IRespID createRespID(IX500Name x500Name) {
        return new RespIDBCFips(x500Name);
    }

    @Override
    public IBasicOCSPRespBuilder createBasicOCSPRespBuilder(IRespID respID) {
        return new BasicOCSPRespBuilderBCFips(respID);
    }

    @Override
    public IOCSPReq createOCSPReq(byte[] requestBytes) throws IOException {
        return new OCSPReqBCFips(new OCSPReq(requestBytes));
    }

    @Override
    public IX509v2CRLBuilder createX509v2CRLBuilder(IX500Name x500Name, Date date) {
        return new X509v2CRLBuilderBCFips(x500Name, date);
    }

    @Override
    public IJcaX509v3CertificateBuilder createJcaX509v3CertificateBuilder(X509Certificate signingCert,
            BigInteger certSerialNumber, Date startDate, Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        return new JcaX509v3CertificateBuilderBCFips(signingCert, certSerialNumber, startDate, endDate, subjectDnName,
                publicKey);
    }

    @Override
    public IBasicConstraints createBasicConstraints(boolean b) {
        return new BasicConstraintsBCFips(new BasicConstraints(b));
    }

    @Override
    public IKeyUsage createKeyUsage() {
        return KeyUsageBCFips.getInstance();
    }

    @Override
    public IKeyUsage createKeyUsage(int i) {
        return new KeyUsageBCFips(new KeyUsage(i));
    }

    @Override
    public IKeyPurposeId createKeyPurposeId() {
        return KeyPurposeIdBCFips.getInstance();
    }

    @Override
    public IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId purposeId) {
        return new ExtendedKeyUsageBCFips(purposeId);
    }

    @Override
    public IX509ExtensionUtils createX509ExtensionUtils(IDigestCalculator digestCalculator) {
        return new X509ExtensionUtilsBCFips(digestCalculator);
    }

    @Override
    public ISubjectPublicKeyInfo createSubjectPublicKeyInfo(Object object) {
        return new SubjectPublicKeyInfoBCFips(object instanceof ASN1EncodableBCFips ?
                ((ASN1EncodableBCFips) object).getEncodable() : object);
    }

    @Override
    public ICRLReason createCRLReason() {
        return CRLReasonBCFips.getInstance();
    }

    @Override
    public ITSTInfo createTSTInfo(IContentInfo contentInfo) throws AbstractTSPException, IOException {
        try {
            CMSTypedData content = new CMSSignedData(((ContentInfoBCFips) contentInfo).getContentInfo())
                    .getSignedContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            content.write(out);
            return new TSTInfoBCFips(TSTInfo.getInstance(ASN1Primitive.fromByteArray(out.toByteArray())));
        } catch (CMSException e) {
            throw new TSPExceptionBCFips(new TSPException("TSP parsing error: " + e.getMessage(), e.getCause()));
        }
    }

    @Override
    public ISingleResp createSingleResp(IBasicOCSPResponse basicResp) {
        return new SingleRespBCFips(basicResp);
    }

    @Override
    public X509Certificate createX509Certificate(Object element) {
        return (X509Certificate) element;
    }

    @Override
    public IBouncyCastleTestConstantsFactory getBouncyCastleFactoryTestUtil() {
        return BOUNCY_CASTLE_FIPS_TEST_CONSTANTS;
    }
}
