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
package com.itextpdf.bouncycastleconnector;

import com.itextpdf.bouncycastleconnector.logs.BouncyCastleLogMessageConstant;
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
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponseGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Default bouncy-castle factory which is expected to be used when no other factories can be created.
 */
class BouncyCastleDefaultFactory implements IBouncyCastleFactory {

    BouncyCastleDefaultFactory() {
        // Empty constructor
    }

    @Override
    public String getAlgorithmOid(String name) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public String getDigestAlgorithmOid(String name) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public String getAlgorithmName(String oid) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifier(String str) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1ObjectIdentifier createASN1ObjectIdentifierInstance(Object object) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1InputStream createASN1InputStream(InputStream stream) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1InputStream createASN1InputStream(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1OctetString createASN1OctetString(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1OctetString createASN1OctetString(IASN1TaggedObject taggedObject, boolean b) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1OctetString createASN1OctetString(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Sequence createASN1Sequence(Object object) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Sequence createASN1Sequence(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Sequence createASN1Sequence(byte[] array) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Sequence createASN1SequenceInstance(Object object) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERSequence createDERSequence(IASN1EncodableVector encodableVector) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERSequence createDERSequence(IASN1Primitive primitive) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1TaggedObject createASN1TaggedObject(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Integer createASN1Integer(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Integer createASN1Integer(int i) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Integer createASN1Integer(BigInteger i) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Set createASN1Set(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Set createASN1Set(Object encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Set createASN1Set(IASN1TaggedObject taggedObject, boolean b) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Set createNullASN1Set() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream stream) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1OutputStream createASN1OutputStream(OutputStream outputStream, String asn1Encoding) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDEROctetString createDEROctetString(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDEROctetString createDEROctetString(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1EncodableVector createASN1EncodableVector() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERNull createDERNull() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERSet createDERSet(IASN1EncodableVector encodableVector) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERSet createDERSet(IASN1Primitive primitive) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERSet createDERSet(ISignaturePolicyIdentifier identifier) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERSet createDERSet(IRecipientInfo recipientInfo) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Enumerated createASN1Enumerated(int i) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Enumerated createASN1Enumerated(IASN1Encodable object) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Encoding createASN1Encoding() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IAttributeTable createAttributeTable(IASN1Set unat) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IPKCSObjectIdentifiers createPKCSObjectIdentifiers() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IContentInfo createContentInfo(IASN1Sequence sequence) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IContentInfo createContentInfo(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITimeStampToken createTimeStampToken(IContentInfo contentInfo) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISigningCertificate createSigningCertificate(IASN1Sequence sequence) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicOCSPResponse createBasicOCSPResponse(IASN1Primitive primitive) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicOCSPResponse createBasicOCSPResponse(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicOCSPResp createBasicOCSPResp(IBasicOCSPResponse response) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicOCSPResp createBasicOCSPResp(Object response) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPObjectIdentifiers createOCSPObjectIdentifiers() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRSASSAPSSParams createRSASSAPSSParams(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRSASSAPSSParams createRSASSAPSSParamsWithMGF1(IASN1ObjectIdentifier digestAlgoOid, int saltLen,
                                                          int trailerField) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public Provider getProvider() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public String getProviderName() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJceKeyAgreeEnvelopedRecipient createJceKeyAgreeEnvelopedRecipient(PrivateKey privateKey) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaContentVerifierProviderBuilder createJcaContentVerifierProviderBuilder() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaSimpleSignerInfoVerifierBuilder createJcaSimpleSignerInfoVerifierBuilder() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaX509CertificateConverter createJcaX509CertificateConverter() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaDigestCalculatorProviderBuilder createJcaDigestCalculatorProviderBuilder() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ICertificateID createCertificateID(IDigestCalculator digestCalculator,
            IX509CertificateHolder certificateHolder, BigInteger bigInteger) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ICertificateID createCertificateID() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IX509CertificateHolder createX509CertificateHolder(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaX509CertificateHolder createJcaX509CertificateHolder(X509Certificate certificate) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtension createExtension(IASN1ObjectIdentifier objectIdentifier, boolean critical,
            IASN1OctetString octetString) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtension createExtension() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtensions createExtensions(IExtension extension) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtensions createExtensions(IExtension[] extension) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtensions createNullExtensions() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPReqBuilder createOCSPReqBuilder() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISigPolicyQualifierInfo createSigPolicyQualifierInfo(IASN1ObjectIdentifier objectIdentifier,
            IDERIA5String string) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1String createASN1String(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Primitive createASN1Primitive(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Primitive createASN1Primitive(byte[] array) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPResp createOCSPResp(IOCSPResponse ocspResponse) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPResp createOCSPResp(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPResp createOCSPResp() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPResponse createOCSPResponse(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IResponseBytes createResponseBytes(IASN1ObjectIdentifier asn1ObjectIdentifier,
            IDEROctetString derOctetString) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPRespBuilder createOCSPRespBuilderInstance() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPRespBuilder createOCSPRespBuilder() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPResponseStatus createOCSPResponseStatus(int status) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPResponseStatus createOCSPResponseStatus() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ICertificateStatus createCertificateStatus() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRevokedStatus createRevokedStatus(ICertificateStatus certificateStatus) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRevokedStatus createRevokedStatus(Date date, int i) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERIA5String createDERIA5String(IASN1TaggedObject taggedObject, boolean b) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDERIA5String createDERIA5String(String str) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ICRLDistPoint createCRLDistPoint(Object object) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IIssuingDistributionPoint createIssuingDistributionPoint(Object point) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IIssuingDistributionPoint createIssuingDistributionPoint(IDistributionPointName distributionPoint,
                                boolean onlyContainsUserCerts, boolean onlyContainsCACerts,IReasonFlags onlySomeReasons,
                                boolean indirectCRL, boolean onlyContainsAttrCerts) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IReasonFlags createReasonFlags(int reasons) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDistributionPointName createDistributionPointName() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IDistributionPointName createDistributionPointName(IGeneralNames generalNames) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IGeneralNames createGeneralNames(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IGeneralName createGeneralName() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOtherHashAlgAndValue createOtherHashAlgAndValue(IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue, ISigPolicyQualifierInfo... policyQualifiers) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISignaturePolicyIdentifier createSignaturePolicyIdentifier(ISignaturePolicyId policyId) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IEnvelopedData createEnvelopedData(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRecipientInfo createRecipientInfo(IKeyTransRecipientInfo keyTransRecipientInfo) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IEncryptedContentInfo createEncryptedContentInfo(IASN1ObjectIdentifier data,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITBSCertificate createTBSCertificate(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITBSCertificate createTBSCertificate(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }
    @Override
    public IIssuerAndSerialNumber createIssuerAndSerialNumber(IX500Name issuer, BigInteger value) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRecipientIdentifier createRecipientIdentifier(IIssuerAndSerialNumber issuerAndSerialNumber) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IKeyTransRecipientInfo createKeyTransRecipientInfo(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOriginatorInfo createNullOriginatorInfo() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ICMSEnvelopedData createCMSEnvelopedData(byte[] valueBytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITimeStampRequestGenerator createTimeStampRequestGenerator() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITimeStampResponse createTimeStampResponse(byte[] respBytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public AbstractOCSPException createAbstractOCSPException(Exception e) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IUnknownStatus createUnknownStatus() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1Dump createASN1Dump() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1BitString createASN1BitString(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1GeneralizedTime createASN1GeneralizedTime(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1GeneralizedTime createASN1GeneralizedTime(Date date) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IASN1UTCTime createASN1UTCTime(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaCertStore createJcaCertStore(List<Certificate> certificates) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITimeStampResponseGenerator createTimeStampResponseGenerator(ITimeStampTokenGenerator tokenGenerator,
            Set<String> algorithms) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITimeStampRequest createTimeStampRequest(byte[] bytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaContentSignerBuilder createJcaContentSignerBuilder(String algorithm) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaSignerInfoGeneratorBuilder createJcaSignerInfoGeneratorBuilder(
            IDigestCalculatorProvider digestCalcProviderProvider) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITimeStampTokenGenerator createTimeStampTokenGenerator(ISignerInfoGenerator siGen, IDigestCalculator dgCalc,
            IASN1ObjectIdentifier policy) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IX500Name createX500Name(X509Certificate certificate) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IX500Name createX500Name(String s) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IX500Name createX500Name(IASN1Sequence s) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IRespID createRespID(IX500Name x500Name) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicOCSPRespBuilder createBasicOCSPRespBuilder(IRespID respID) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IOCSPReq createOCSPReq(byte[] requestBytes) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IX509v2CRLBuilder createX509v2CRLBuilder(IX500Name x500Name, Date thisUpdate) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaX509v3CertificateBuilder createJcaX509v3CertificateBuilder(X509Certificate signingCert,
            BigInteger certSerialNumber, Date startDate, Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicConstraints createBasicConstraints(boolean b) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBasicConstraints createBasicConstraints(int pathLength) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IKeyUsage createKeyUsage() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IKeyUsage createKeyUsage(int i) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IKeyPurposeId createKeyPurposeId() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IKeyPurposeId createKeyPurposeId(IASN1ObjectIdentifier objectIdentifier) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId purposeId) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId[] purposeIds) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IX509ExtensionUtils createX509ExtensionUtils(IDigestCalculator digestCalculator) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISubjectPublicKeyInfo createSubjectPublicKeyInfo(Object obj) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ICRLReason createCRLReason() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITSTInfo createTSTInfo(IContentInfo contentInfo) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITSTInfo createTSTInfo(IASN1Primitive contentInfo) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ISingleResp createSingleResp(IBasicOCSPResponse basicResp) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public X509Certificate createX509Certificate(Object element) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IBouncyCastleTestConstantsFactory getBouncyCastleFactoryTestUtil() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public CRL createNullCrl() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IPEMParser createPEMParser(Reader reader) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJceOpenSSLPKCS8DecryptorProviderBuilder createJceOpenSSLPKCS8DecryptorProviderBuilder() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IJcaPEMKeyConverter createJcaPEMKeyConverter() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITime createTime(Date date) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public ITime createEndDate(X509Certificate certificate) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public boolean isNullExtension(IExtension extNonce) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public boolean isNull(IASN1Encodable encodable) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public SecureRandom getSecureRandom() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public boolean isInApprovedOnlyMode() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public byte[] createCipherBytes(X509Certificate x509certificate, byte[] abyte0,
            IAlgorithmIdentifier algorithmIdentifier) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public void isEncryptionFeatureSupported(int encryptionType, boolean withCertificate) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public byte[] generateHKDF(byte[] inputKey, byte[] salt, byte[] info) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public byte[] generateHMACSHA256Token(byte[] key, byte[] data) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public byte[] generateEncryptedKeyWithAES256NoPad(byte[] key, byte[] kek) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public byte[] generateDecryptedKeyWithAES256NoPad(byte[] key, byte[] kek) {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }

    @Override
    public IGCMBlockCipher createGCMBlockCipher() {
        throw new UnsupportedOperationException(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
    }
}
