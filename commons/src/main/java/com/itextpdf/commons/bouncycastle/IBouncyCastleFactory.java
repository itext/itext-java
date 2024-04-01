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
package com.itextpdf.commons.bouncycastle;

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
import com.itextpdf.commons.bouncycastle.cms.AbstractCMSException;
import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSignerInfoGeneratorBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyAgreeEnvelopedRecipient;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
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

/**
 * {@link IBouncyCastleFactory} contains methods required for bouncy-classes objects creation. Implementation will be
 * selected depending on a bouncy-castle dependency specified by the user.
 */
public interface IBouncyCastleFactory {

    /**
     * Get signing algorithm oid from its name.
     * 
     * @param name name of the algorithm
     * 
     * @return algorithm oid
     */
    String getAlgorithmOid(String name);

    /**
     * Get hash algorithm oid from its name.
     *
     * @param name name of the algorithm
     *
     * @return algorithm oid
     */
    String getDigestAlgorithmOid(String name);

    /**
     * Get signing algorithm name from its oid.
     * 
     * @param oid oid of the algorithm
     * 
     * @return algorithm name
     */
    String getAlgorithmName(String oid);
    
    /**
     * Cast ASN1 encodable wrapper to the ASN1 object identifier wrapper.
     *
     * @param encodable wrapper to be cast
     *
     * @return casted wrapper
     */
    IASN1ObjectIdentifier createASN1ObjectIdentifier(IASN1Encodable encodable);

    /**
     * Create ASN1 Object identifier wrapper from {@link String}.
     *
     * @param str {@link String} to create object identifier from
     *
     * @return created object identifier
     */
    IASN1ObjectIdentifier createASN1ObjectIdentifier(String str);

    /**
     * Create ASN1 Object identifier wrapper from {@link Object} using {@code getInstance} method call.
     *
     * @param object {@link Object} to create object identifier from
     *
     * @return created object identifier
     */
    IASN1ObjectIdentifier createASN1ObjectIdentifierInstance(Object object);

    /**
     * Create ASN1 Input stream wrapper from {@link InputStream}.
     *
     * @param stream {@link InputStream} to create ASN1 Input stream from
     *
     * @return created ASN1 Input stream
     */
    IASN1InputStream createASN1InputStream(InputStream stream);

    /**
     * Create ASN1 Input stream wrapper from {@code byte[]}.
     *
     * @param bytes {@code byte[]} to create ASN1 Input stream from
     *
     * @return created ASN1 Input stream
     */
    IASN1InputStream createASN1InputStream(byte[] bytes);

    /**
     * Cast ASN1 Encodable wrapper to the ASN1 Octet string wrapper.
     *
     * @param encodable to be casted to ASN1 Octet string wrapper
     *
     * @return casted ASN1 Octet string wrapper
     */
    IASN1OctetString createASN1OctetString(IASN1Encodable encodable);

    /**
     * Create ASN1 Octet string wrapper from ASN1 Tagged object wrapper and {@code boolean} parameter.
     *
     * @param taggedObject ASN1 Tagged object wrapper to create ASN1 Octet string wrapper from
     * @param b            boolean to create ASN1 Octet string wrapper
     *
     * @return created ASN1 Octet string wrapper
     */
    IASN1OctetString createASN1OctetString(IASN1TaggedObject taggedObject, boolean b);

    /**
     * Create ASN1 Octet string wrapper from {@code byte[]}.
     *
     * @param bytes {@code byte[]} to create ASN1 Octet string wrapper from
     *
     * @return created ASN1 Octet string wrapper
     */
    IASN1OctetString createASN1OctetString(byte[] bytes);

    /**
     * Cast {@link Object} to ASN1 Sequence wrapper.
     *
     * @param object {@link Object} to be cast. Must be instance of ASN1 Sequence
     *
     * @return casted ASN1 Sequence wrapper
     */
    IASN1Sequence createASN1Sequence(Object object);

    /**
     * Cast ASN1 encodable wrapper to the ASN1 Sequence wrapper.
     *
     * @param encodable to be casted to ASN1 Sequence wrapper
     *
     * @return casted ASN1 Sequence wrapper
     */
    IASN1Sequence createASN1Sequence(IASN1Encodable encodable);

    /**
     * Create ASN1 Sequence wrapper from {@code byte[]}.
     *
     * @param array {@code byte[]} to create ASN1 Sequence wrapper from
     *
     * @return created ASN1 Sequence wrapper
     *
     * @throws IOException if issues occur during ASN1 Sequence creation
     */
    IASN1Sequence createASN1Sequence(byte[] array) throws IOException;

    /**
     * Create ASN1 Sequence wrapper from {@link Object} using {@code getInstance} method call.
     *
     * @param object {@link Object} to create ASN1 Sequence wrapper from
     *
     * @return created ASN1 Sequence wrapper
     */
    IASN1Sequence createASN1SequenceInstance(Object object);

    /**
     * Create DER Sequence wrapper from ASN1 Encodable vector wrapper.
     *
     * @param encodableVector ASN1 Encodable vector wrapper to create DER Sequence wrapper from
     *
     * @return created DER Sequence wrapper
     */
    IDERSequence createDERSequence(IASN1EncodableVector encodableVector);

    /**
     * Create DER Sequence wrapper from ASN1 Primitive wrapper.
     *
     * @param primitive ASN1 Primitive wrapper to create DER Sequence wrapper from
     *
     * @return created DER Sequence wrapper
     */
    IDERSequence createDERSequence(IASN1Primitive primitive);

    /**
     * Create ASN1 Tagged object wrapper from ASN1 Encodable wrapper.
     *
     * @param encodable ASN1 Encodable vector to create ASN1 Tagged object wrapper from
     *
     * @return created ASN1 Tagged object wrapper
     */
    IASN1TaggedObject createASN1TaggedObject(IASN1Encodable encodable);

    /**
     * Cast ASN1 Encodable wrapper to ASN1 Integer wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 Integer
     */
    IASN1Integer createASN1Integer(IASN1Encodable encodable);

    /**
     * Create ASN1 Integer wrapper from {@code int}.
     *
     * @param i {@code int} to create ASN1 Integer wrapper from
     *
     * @return created ASN1 Integer wrapper
     */
    IASN1Integer createASN1Integer(int i);

    /**
     * Create ASN1 Integer wrapper from {@link BigInteger}.
     *
     * @param i {@link BigInteger} to create ASN1 Integer wrapper from
     *
     * @return created ASN1 Integer wrapper
     */
    IASN1Integer createASN1Integer(BigInteger i);

    /**
     * Cast ASN1 Encodable wrapper to ASN1 Set wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 Set
     */
    IASN1Set createASN1Set(IASN1Encodable encodable);

    /**
     * Create ASN1 Set wrapper from {@link Object}.
     *
     * @param encodable {@link Object} to create ASN1 Set wrapper from. Must be instance of ASN1 Set
     *
     * @return created ASN1 Set wrapper
     */
    IASN1Set createASN1Set(Object encodable);

    /**
     * Create ASN1 Set wrapper from ASN1 Tagged object wrapper and {@code boolean} parameter.
     *
     * @param taggedObject ASN1 Tagged object wrapper to create ASN1 Set wrapper from
     * @param b            boolean to create ASN1 Set wrapper
     *
     * @return created ASN1 Set wrapper
     */
    IASN1Set createASN1Set(IASN1TaggedObject taggedObject, boolean b);

    /**
     * Create ASN1 Set wrapper which will store {@code null}.
     *
     * @return ASN1 Set wrapper with {@code null} value
     */
    IASN1Set createNullASN1Set();

    /**
     * Create ASN1 Output stream wrapper from {@link OutputStream}.
     *
     * @param stream {@link OutputStream} to create ASN1 Output stream wrapper from
     *
     * @return created ASN1 Output stream wrapper
     */
    IASN1OutputStream createASN1OutputStream(OutputStream stream);

    /**
     * Create ASN1 Output stream wrapper from {@link OutputStream} and ASN1 Encoding.
     *
     * @param outputStream {@link OutputStream} to create ASN1 Output stream wrapper from
     * @param asn1Encoding ASN1 Encoding to be used
     *
     * @return created ASN1 Output stream wrapper
     */
    IASN1OutputStream createASN1OutputStream(OutputStream outputStream, String asn1Encoding);

    /**
     * Create DER Octet string wrapper from {@code byte[]}.
     *
     * @param bytes {@code byte[]} to create DER Octet string wrapper from
     *
     * @return created DER Octet string wrapper
     */
    IDEROctetString createDEROctetString(byte[] bytes);

    /**
     * Cast ASN1 Encodable wrapper to DER Octet string wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be casted
     *
     * @return DER Octet string wrapper
     */
    IDEROctetString createDEROctetString(IASN1Encodable encodable);

    /**
     * Create ASN1 Encodable wrapper without parameters.
     *
     * @return created ASN1 Encodable wrapper
     */
    IASN1EncodableVector createASN1EncodableVector();

    /**
     * Create DER Null wrapper without parameters.
     *
     * @return created DER Null wrapper
     */
    IDERNull createDERNull();

    /**
     * Create DER Tagged object wrapper from {@code int} value and ASN1 Primitive wrapper.
     *
     * @param i         {@code int} value for DER Tagged object wrapper creation
     * @param primitive ASN1 Primitive wrapper to create DER Tagged object wrapper from
     *
     * @return created DER Tagged object wrapper
     */
    IDERTaggedObject createDERTaggedObject(int i, IASN1Primitive primitive);

    /**
     * Create DER Tagged object wrapper from {@code int} value, {@code boolean} value and ASN1 Primitive wrapper.
     *
     * @param b         {@code boolean} value for DER Tagged object wrapper creation
     * @param i         {@code int} value for DER Tagged object wrapper creation
     * @param primitive ASN1 Primitive wrapper to create DER Tagged object wrapper from
     *
     * @return created DER Tagged object wrapper
     */
    IDERTaggedObject createDERTaggedObject(boolean b, int i, IASN1Primitive primitive);

    /**
     * Create DER Set wrapper from ASN1 Encodable vector wrapper.
     *
     * @param encodableVector ASN1 Encodable vector wrapper to create DER Set wrapper from
     *
     * @return created DER Set wrapper
     */
    IDERSet createDERSet(IASN1EncodableVector encodableVector);

    /**
     * Create DER Set wrapper from ASN1 Primitive wrapper.
     *
     * @param primitive ASN1 Primitive wrapper to create DER Set wrapper from
     *
     * @return created DER Set wrapper
     */
    IDERSet createDERSet(IASN1Primitive primitive);

    /**
     * Create DER Set wrapper from signature policy identifier wrapper.
     *
     * @param identifier signature policy identifier wrapper to create DER Set wrapper from
     *
     * @return created DER Set wrapper
     */
    IDERSet createDERSet(ISignaturePolicyIdentifier identifier);

    /**
     * Create DER Set wrapper from recipient info wrapper.
     *
     * @param recipientInfo recipient info wrapper to create DER Set wrapper from
     *
     * @return created DER Set wrapper
     */
    IDERSet createDERSet(IRecipientInfo recipientInfo);

    /**
     * Create ASN1 Enumerated wrapper from {@code int} value.
     *
     * @param i {@code int} to create ASN1 Enumerated wrapper from
     *
     * @return created ASN1 Enumerated wrapper
     */
    IASN1Enumerated createASN1Enumerated(int i);

    /**
     * Create ASN1 Enumerated wrapper from {@code IASN1Encodable} value.
     *
     * @param object {@code IASN1Encodable} to create ASN1 Enumerated wrapper from
     *
     * @return created ASN1 Enumerated wrapper.
     */
    IASN1Enumerated createASN1Enumerated(IASN1Encodable object);

    /**
     * Create ASN1 Encoding without parameters.
     *
     * @return created ASN1 Encoding
     */
    IASN1Encoding createASN1Encoding();

    /**
     * Create attribute table wrapper from ASN1 Set wrapper.
     *
     * @param unat ASN1 Set wrapper to create attribute table wrapper from
     *
     * @return created attribute table wrapper
     */
    IAttributeTable createAttributeTable(IASN1Set unat);

    /**
     * Create PKCS Object identifiers wrapper without parameters.
     *
     * @return created PKCS Object identifiers
     */
    IPKCSObjectIdentifiers createPKCSObjectIdentifiers();

    /**
     * Create attribute wrapper from ASN1 Object identifier wrapper and ASN1 Set wrapper.
     *
     * @param attrType   ASN1 Object identifier wrapper to create attribute wrapper from
     * @param attrValues ASN1 Object identifier wrapper to create attribute wrapper from
     *
     * @return created attribute wrapper
     */
    IAttribute createAttribute(IASN1ObjectIdentifier attrType, IASN1Set attrValues);

    /**
     * Create content info wrapper from ASN1 Sequence wrapper.
     *
     * @param sequence ASN1 Sequence wrapper to create content info wrapper from
     *
     * @return created content info wrapper
     */
    IContentInfo createContentInfo(IASN1Sequence sequence);

    /**
     * Create content info wrapper from ASN1 Object identifier wrapper and ASN1 Encodable wrapper.
     *
     * @param objectIdentifier ASN1 Object identifier wrapper to create content info wrapper from
     * @param encodable        ASN1 Encodable wrapper to create content info wrapper from
     *
     * @return created content info wrapper
     */
    IContentInfo createContentInfo(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable);

    /**
     * Create timestamp token wrapper from content info wrapper.
     *
     * @param contentInfo content info wrapper to create timestamp token wrapper from
     *
     * @return created timestamp token wrapper
     *
     * @throws AbstractTSPException if actual TSP Exception occurs during wrapper creation
     * @throws IOException          if input-output exception occurs during wrapper creation
     */
    ITimeStampToken createTimeStampToken(IContentInfo contentInfo) throws AbstractTSPException, IOException;

    /**
     * Create signing certificate wrapper from ASN1 Sequence wrapper.
     *
     * @param sequence ASN1 Sequence wrapper to create signing certificate wrapper from
     *
     * @return created signing certificate wrapper
     */
    ISigningCertificate createSigningCertificate(IASN1Sequence sequence);

    /**
     * Create signing certificate version 2 wrapper from ASN1 Sequence wrapper.
     *
     * @param sequence ASN1 Sequence wrapper to create signing certificate version 2 wrapper from
     *
     * @return created signing certificate version 2 wrapper
     */
    ISigningCertificateV2 createSigningCertificateV2(IASN1Sequence sequence);

    /**
     * Create basic OCSP Response wrapper from ASN1 Primitive wrapper.
     *
     * @param primitive ASN1 Primitive wrapper to create basic OCSP response wrapper from
     *
     * @return created basic OCSP response wrapper
     */
    IBasicOCSPResponse createBasicOCSPResponse(IASN1Primitive primitive);

    /**
     * Create basic OCSP Response wrapper from {@code byte[]} array.
     * 
     * @param bytes {@code byte[]} array to create basic OCSP response wrapper from
     * 
     * @return created basic OCSP response wrapper
     */
    IBasicOCSPResponse createBasicOCSPResponse(byte[] bytes);

    /**
     * Create basic OCSP Resp wrapper from basic OCSP Response wrapper.
     *
     * @param response basic OCSP Response wrapper to create basic OCSP Resp wrapper from
     *
     * @return created basic OCSP Resp wrapper
     */
    IBasicOCSPResp createBasicOCSPResp(IBasicOCSPResponse response);

    /**
     * Create basic OCSP Resp wrapper from {@link Object}.
     *
     * @param response {@link Object} to create basic OCSP Resp wrapper from. Must be actual basic OCSP Resp instance
     *
     * @return created basic OCSP Resp wrapper
     */
    IBasicOCSPResp createBasicOCSPResp(Object response);

    /**
     * Create OCSP Object identifiers wrapper without parameters.
     *
     * @return created OCSP Object identifiers wrapper
     */
    IOCSPObjectIdentifiers createOCSPObjectIdentifiers();

    /**
     * Create algorithm identifier wrapper from ASN1 Object identifier wrapper.
     *
     * @param algorithm ASN1 Object identifier wrapper to create algorithm identifier wrapper from
     *
     * @return created algorithm identifier wrapper
     */
    IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm);

    /**
     * Create algorithm identifier wrapper from ASN1 Object identifier wrapper and ASN1 Encodable wrapper
     * for the parameters.
     *
     * @param algorithm ASN1 Object identifier wrapper to create algorithm identifier wrapper from
     * @param parameters ASN1 Encodable wrapper to create algorithm parameters.
     *
     * @return created algorithm identifier wrapper
     */
    IAlgorithmIdentifier createAlgorithmIdentifier(IASN1ObjectIdentifier algorithm, IASN1Encodable parameters);

    /**
     * Create a RSASSA-PSS params wrapper from an ASN1 Encodable wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to create RSASSA-PSS params wrapper from
     *
     * @return created RSASSA-PSS params wrapper
     */
    IRSASSAPSSParams createRSASSAPSSParams(IASN1Encodable encodable);

    /**
     * Create a RSASSA-PSS params wrapper from a digest algorithm OID, a salt length and a trailer field length.
     * The mask generation function will be set to MGF1, and the same digest algorithm will be used to populate the
     * MGF parameters.
     *
     * @param digestAlgoOid  identifier of the digest algorithm to be used both in the MGF and in the signature
     * @param saltLen        salt length value
     * @param trailerField   trailer field value
     *
     * @return an {@link IRSASSAPSSParams} object initialised with the parameters supplied
     */
    IRSASSAPSSParams createRSASSAPSSParamsWithMGF1(IASN1ObjectIdentifier digestAlgoOid, int saltLen, int trailerField);

    /**
     * Get {@link Provider} instance for this factory.
     *
     * @return {@link Provider} instance
     */
    Provider getProvider();

    /**
     * Get {@link String} which represents providers name for this factory.
     *
     * @return {@link String} which represents providers name
     */
    String getProviderName();

    /**
     * Create Jce Key trans enveloped recipient wrapper from {@link PrivateKey}.
     *
     * @param privateKey {@link PrivateKey} to create Jce Key trans enveloped recipient wrapper from
     *
     * @return created Jce Key trans enveloped recipient wrapper
     */
    IJceKeyTransEnvelopedRecipient createJceKeyTransEnvelopedRecipient(PrivateKey privateKey);

    /**
     * Create Jce Key agree enveloped recipient wrapper from {@link PrivateKey}.
     *
     * @param privateKey {@link PrivateKey} to create Jce Key agree enveloped recipient wrapper from
     *
     * @return created Jce Key agree enveloped recipient wrapper
     */
    IJceKeyAgreeEnvelopedRecipient createJceKeyAgreeEnvelopedRecipient(PrivateKey privateKey);

    /**
     * Create Jca Content verifier provider builder wrapper without parameters.
     *
     * @return created Jca Content verifier provider builder wrapper
     */
    IJcaContentVerifierProviderBuilder createJcaContentVerifierProviderBuilder();

    /**
     * Create Jca Simple signer info verifier builder wrapper without parameters.
     *
     * @return created Jca Simple signer info verifier builder wrapper
     */
    IJcaSimpleSignerInfoVerifierBuilder createJcaSimpleSignerInfoVerifierBuilder();

    /**
     * Create Jca X509 Certificate converter wrapper without parameters.
     *
     * @return created Jca X509 Certificate converter wrapper
     */
    IJcaX509CertificateConverter createJcaX509CertificateConverter();

    /**
     * Create Jca Digest calculator provider builder wrapper without parameters.
     *
     * @return created Jca Digest calculator provider builder wrapper
     */
    IJcaDigestCalculatorProviderBuilder createJcaDigestCalculatorProviderBuilder();

    /**
     * Create certificate ID wrapper from digest calculator, X509 Certificate holder wrappers and {@link BigInteger}.
     *
     * @param digestCalculator  digest calculator wrapper to create certificate ID wrapper from
     * @param certificateHolder X509 Certificate holder wrapper to create certificate ID wrapper from
     * @param bigInteger        {@link BigInteger} to create certificate ID wrapper from
     *
     * @return created certificate ID wrapper
     *
     * @throws AbstractOCSPException if actual OCSP Exception occurs during wrapper creation
     */
    ICertificateID createCertificateID(IDigestCalculator digestCalculator, IX509CertificateHolder certificateHolder,
            BigInteger bigInteger) throws AbstractOCSPException;

    /**
     * Create certificate ID wrapper without parameters.
     *
     * @return created certificate ID wrapper
     */
    ICertificateID createCertificateID();

    /**
     * Create X509 Certificate holder wrapper from {@code byte[]}.
     *
     * @param bytes {@code byte[]} value to create X509 Certificate holder wrapper from
     *
     * @return created X509 Certificate holder wrapper
     *
     * @throws IOException if input-output exception occurs during wrapper creation
     */
    IX509CertificateHolder createX509CertificateHolder(byte[] bytes) throws IOException;

    /**
     * Create Jca X509 Certificate holder wrapper from {@link X509Certificate}.
     *
     * @param certificate {@link X509Certificate} to create Jca X509 Certificate holder wrapper from
     *
     * @return created Jca X509 Certificate holder wrapper
     *
     * @throws CertificateEncodingException if certificate encoding exception occurs during wrapper creation
     */
    IJcaX509CertificateHolder createJcaX509CertificateHolder(X509Certificate certificate)
            throws CertificateEncodingException;

    /**
     * Create extension wrapper from ASN1 Object identifier wrapper, {@code boolean} and ASN1 Octet string wrapper.
     *
     * @param objectIdentifier ASN1 Object identifier wrapper to create extension wrapper from
     * @param critical         {@code boolean} to create extension wrapper
     * @param octetString      ASN1 Octet string wrapper to create extension wrapper from
     *
     * @return created extension wrapper
     */
    IExtension createExtension(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString);

    /**
     * Create extension wrapper without parameters.
     *
     * @return created extension wrapper
     */
    IExtension createExtension();

    /**
     * Create extensions wrapper from extension wrapper.
     *
     * @param extension extension wrapper to create extensions wrapper from
     *
     * @return created extensions wrapper
     */
    IExtensions createExtensions(IExtension extension);

    /**
     * Create extensions wrapper for {@code null} value.
     *
     * @return created extensions wrapper
     */
    IExtensions createNullExtensions();

    /**
     * Create OCSP Req builder wrapper without parameters.
     *
     * @return created OCSP Req builder wrapper
     */
    IOCSPReqBuilder createOCSPReqBuilder();

    /**
     * Create sig policy qualifier info wrapper from ASN1 Object identifier wrapper and DERIA5 String wrapper.
     *
     * @param objectIdentifier ASN1 Object identifier wrapper to create sig policy qualifier info wrapper from
     * @param string           DERIA5 String wrapper to create sig policy qualifier info wrapper from
     *
     * @return created sig policy qualifier info wrapper
     */
    ISigPolicyQualifierInfo createSigPolicyQualifierInfo(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string);

    /**
     * Cast ASN1 Encodable wrapper to ASN1 String wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 String wrapper
     */
    IASN1String createASN1String(IASN1Encodable encodable);

    /**
     * Cast ASN1 Encodable wrapper to ASN1 Primitive wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 Primitive wrapper
     */
    IASN1Primitive createASN1Primitive(IASN1Encodable encodable);

    /**
     * Create ASN1 Primitive wrapper from {@code byte[]} value.
     *
     * @param array {@code byte[]} value to create ASN1 Primitive wrapper from
     *
     * @return created ASN1 Primitive wrapper
     *
     * @throws IOException if input-output exception occurs during creation
     */
    IASN1Primitive createASN1Primitive(byte[] array) throws IOException;

    /**
     * Create OCSP Resp wrapper from OCSP Response wrapper.
     *
     * @param ocspResponse OCSP Response wrapper to create OCSP Resp wrapper from
     *
     * @return created OCSP Resp wrapper
     */
    IOCSPResp createOCSPResp(IOCSPResponse ocspResponse);

    /**
     * Create OCSP Resp wrapper from {@code byte[]} value.
     *
     * @param bytes {@code byte[]} value to create OCSP Resp wrapper from
     *
     * @return created OCSP Resp wrapper
     *
     * @throws IOException if input-output exception occurs during creation
     */
    IOCSPResp createOCSPResp(byte[] bytes) throws IOException;

    /**
     * Create OCSP Resp wrapper without parameters.
     *
     * @return created OCSP Resp wrapper
     */
    IOCSPResp createOCSPResp();

    /**
     * Create OCSP Response wrapper from OCSP Response status wrapper and response bytes wrapper.
     *
     * @param respStatus    OCSP Response status wrapper to create OCSP Response wrapper from
     * @param responseBytes response bytes wrapper to create OCSP Response wrapper from
     *
     * @return created OCSP Response wrapper
     */
    IOCSPResponse createOCSPResponse(IOCSPResponseStatus respStatus, IResponseBytes responseBytes);

    /**
     * Create response bytes wrapper from ASN1 Object identifier wrapper and DER Octet string wrapper.
     *
     * @param asn1ObjectIdentifier ASN1 Object identifier wrapper to create response bytes wrapper from
     * @param derOctetString       DER Octet string wrapper to create response bytes wrapper from
     *
     * @return created response bytes wrapper
     */
    IResponseBytes createResponseBytes(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString);

    /**
     * Create OCSP Resp builder wrapper using {@code getInstance} call.
     *
     * @return created OCSP Resp builder wrapper
     */
    IOCSPRespBuilder createOCSPRespBuilderInstance();

    /**
     * Create OCSP Resp builder wrapper without parameters.
     *
     * @return created OCSP Resp builder wrapper
     */
    IOCSPRespBuilder createOCSPRespBuilder();

    /**
     * Create OCSP Response status wrapper from {@code int} value.
     *
     * @param status {@code int} value to create OCSP Response status wrapper from
     *
     * @return created OCSP Response status wrapper
     */
    IOCSPResponseStatus createOCSPResponseStatus(int status);

    /**
     * Create OCSP Response status wrapper without parameters.
     *
     * @return created OCSP Response status wrapper
     */
    IOCSPResponseStatus createOCSPResponseStatus();

    /**
     * Create certificate status wrapper without parameters.
     *
     * @return created certificate status wrapper
     */
    ICertificateStatus createCertificateStatus();

    /**
     * Create revoked status wrapper from certificate status wrapper.
     *
     * @param certificateStatus certificate status wrapper to create revoked status wrapper from
     *
     * @return created revoked status wrapper
     */
    IRevokedStatus createRevokedStatus(ICertificateStatus certificateStatus);

    /**
     * Create revoked status wrapper from {@link Date} and {@code int} value.
     *
     * @param date {@link Date} to create revoked status wrapper from
     * @param i    {@code int} value to create revoked status wrapper from
     *
     * @return created revoked status wrapper
     */
    IRevokedStatus createRevokedStatus(Date date, int i);

    /**
     * Create DERIA5 String wrapper from ASN1 Tagged object wrapper and {@code boolean} value.
     *
     * @param taggedObject ASN1 Tagged object wrapper to create DERIA5 String wrapper from
     * @param b            {@code boolean} value to create DERIA5 String wrapper from
     *
     * @return created DERIA5 String wrapper
     */
    IDERIA5String createDERIA5String(IASN1TaggedObject taggedObject, boolean b);

    /**
     * Create DERIA5 String wrapper from {@link String} value.
     *
     * @param str {@link String} value to create DERIA5 String wrapper from
     *
     * @return created DERIA5 String wrapper
     */
    IDERIA5String createDERIA5String(String str);

    /**
     * Create CRL Dist point wrapper from {@link Object}.
     *
     * @param object {@link Object} to create CRL Dist point wrapper from
     *
     * @return created CRL Dist point wrapper
     */
    ICRLDistPoint createCRLDistPoint(Object object);

    /**
     * Create Issuing Distribution Point wrapper from {@link Object}.
     *
     * @param point {@link Object} to create Issuing Distribution Point wrapper from
     *
     * @return created Issuing Distribution Point wrapper.
     */
    IIssuingDistributionPoint createIssuingDistributionPoint(Object point);

    /**
     * Create Issuing Distribution Point wrapper with specified values.
     *
     * @param distributionPoint     one of names from the corresponding distributionPoint from the cRLDistributionPoints
     *                              extension of every certificate that is within the scope of this CRL
     * @param onlyContainsUserCerts true if the scope of the CRL only includes end entity public key certificates
     * @param onlyContainsCACerts   true if the scope of the CRL only includes CA certificates
     * @param onlySomeReasons       reason codes associated with a distribution point
     * @param indirectCRL           true if CRL includes certificates issued by authorities other than the CRL issuer,
     *                              false if the scope of the CRL only includes certificates issued by the CRL issuer
     * @param onlyContainsAttrCerts true if the scope of the CRL only includes attribute certificates
     *
     * @return created Issuing Distribution Point wrapper.
     */
    IIssuingDistributionPoint createIssuingDistributionPoint(IDistributionPointName distributionPoint,
                                                  boolean onlyContainsUserCerts, boolean onlyContainsCACerts,
                                                  IReasonFlags onlySomeReasons, boolean indirectCRL,
                                                  boolean onlyContainsAttrCerts);

    /**
     * Creates the wrapper for ReasonFlags.
     *
     * @param reasons the bitwise OR of the Key Reason flags giving the allowed uses for the key
     *
     * @return created ReasonFlags wrapper.
     */
    IReasonFlags createReasonFlags(int reasons);

    /**
     * Create distribution point name wrapper without parameters.
     *
     * @return created distribution point name wrapper.
     */
    IDistributionPointName createDistributionPointName();

    /**
     * Create distribution point name wrapper by passing general names.
     *
     * @param generalNames general names to create distribution point name from
     *
     * @return created distribution point name wrapper.
     */
    IDistributionPointName createDistributionPointName(IGeneralNames generalNames);

    /**
     * Cast ASN1 Encodable wrapper to general names wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted general names wrapper
     */
    IGeneralNames createGeneralNames(IASN1Encodable encodable);

    /**
     * Create general name wrapper without parameters.
     *
     * @return created general name wrapper
     */
    IGeneralName createGeneralName();

    /**
     * Create other hash alg and value wrapper from algorithm identifier wrapper and ASN1 Octet string wrapper.
     *
     * @param algorithmIdentifier algorithm identifier wrapper to create other hash alg and value wrapper from
     * @param octetString         ASN1 Octet string wrapper to create other hash alg and value wrapper from
     *
     * @return created other hash alg and value wrapper
     */
    IOtherHashAlgAndValue createOtherHashAlgAndValue(IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString);

    /**
     * Create signature policy id wrapper from ASN1 Object identifier wrapper and other hash alg and value wrapper.
     *
     * @param objectIdentifier ASN1 Object identifier wrapper to create signature policy id wrapper from
     * @param algAndValue      other hash alg and value wrapper to create signature policy id wrapper from
     *
     * @return created signature policy id wrapper
     */
    ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue);

    /**
     * Create signature policy id wrapper from ASN1 Object identifier wrapper, other hash alg and value wrapper
     * and sig policy qualifier info wrappers.
     *
     * @param objectIdentifier ASN1 Object identifier wrapper to create signature policy id wrapper from
     * @param algAndValue      other hash alg and value wrapper to create signature policy id wrapper from
     * @param policyQualifiers sig policy qualifier info wrappers to create signature policy id wrapper from
     *
     * @return created signature policy id wrapper
     */
    ISignaturePolicyId createSignaturePolicyId(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue,
            ISigPolicyQualifierInfo... policyQualifiers);

    /**
     * Create signature policy identifier wrapper from signature policy id wrapper.
     *
     * @param policyId signature policy id wrapper to create signature policy identifier wrapper from
     *
     * @return created signature policy identifier wrapper
     */
    ISignaturePolicyIdentifier createSignaturePolicyIdentifier(ISignaturePolicyId policyId);

    /**
     * Create enveloped data wrapper from originator info wrapper, ASN1 Set wrapper,
     * encrypted content info wrapper and another ASN1 Set wrapper.
     *
     * @param originatorInfo       originator info wrapper to create enveloped data wrapper from
     * @param set                  ASN1 Set wrapper to create enveloped data wrapper from
     * @param encryptedContentInfo encrypted content info wrapper to create enveloped data wrapper from
     * @param set1                 ASN1 Set wrapper to create enveloped data wrapper from
     *
     * @return created enveloped data wrapper
     */
    IEnvelopedData createEnvelopedData(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1);

    /**
     * Create recipient info wrapper from key trans recipient info wrapper.
     *
     * @param keyTransRecipientInfo key trans recipient info wrapper to create recipient info wrapper from
     *
     * @return created recipient info wrapper
     */
    IRecipientInfo createRecipientInfo(IKeyTransRecipientInfo keyTransRecipientInfo);

    /**
     * Create encrypted content info wrapper from ASN1 Object identifier wrapper,
     * algorithm identifier wrapper and ASN1 Octet string wrapper.
     *
     * @param data                ASN1 Object identifier wrapper to create encrypted content info wrapper from
     * @param algorithmIdentifier algorithm identifier wrapper to create encrypted content info wrapper from
     * @param octetString         ASN1 Octet string wrapper to create encrypted content info wrapper from
     *
     * @return created encrypted content info wrapper
     */
    IEncryptedContentInfo createEncryptedContentInfo(IASN1ObjectIdentifier data,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString);

    /**
     * Create TBS Certificate wrapper from ASN1 Encodable wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to create TBS Certificate wrapper from
     *
     * @return created TBS Certificate wrapper
     */
    ITBSCertificate createTBSCertificate(IASN1Encodable encodable);

    /**
     * Create TBS Certificate wrapper from ASN1 Encoded data.
     *
     * @param bytes ASN1 Encoded TBS Certificate
     *
     * @return created TBS Certificate wrapper
     */
    ITBSCertificate createTBSCertificate(byte[] bytes);

    /**
     * Create issuer and serial number wrapper from X500 Name wrapper and {@link BigInteger}.
     *
     * @param issuer X500 Name wrapper to create issuer and serial number wrapper from
     * @param value  {@link BigInteger} to create issuer and serial number wrapper from
     *
     * @return created issuer and serial number wrapper
     */
    IIssuerAndSerialNumber createIssuerAndSerialNumber(IX500Name issuer, BigInteger value);

    /**
     * Create recipient identifier wrapper from issuer and serial number wrapper.
     *
     * @param issuerAndSerialNumber issuer and serial number wrapper to create recipient identifier wrapper from
     *
     * @return created recipient identifier wrapper
     */
    IRecipientIdentifier createRecipientIdentifier(IIssuerAndSerialNumber issuerAndSerialNumber);

    /**
     * Create key trans recipient info wrapper from recipient identifier wrapper,
     * algorithm identifier wrapper and ASN1 Octet string wrapper.
     *
     * @param recipientIdentifier recipient identifier wrapper to create key trans recipient info wrapper from
     * @param algorithmIdentifier algorithm identifier wrapper to create key trans recipient info wrapper from
     * @param octetString         ASN1 Octet string wrapper to create key trans recipient info wrapper from
     *
     * @return created key trans recipient info wrapper
     */
    IKeyTransRecipientInfo createKeyTransRecipientInfo(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString);

    /**
     * Create originator info wrapper with {@code null} value.
     *
     * @return created originator info wrapper
     */
    IOriginatorInfo createNullOriginatorInfo();

    /**
     * Create CMS enveloped data from {@code byte[]} value.
     *
     * @param valueBytes {@code byte[]} value to create CMS enveloped data from
     *
     * @return created CMS enveloped data
     *
     * @throws AbstractCMSException if actual CMS Exception occurs during creation.
     */
    ICMSEnvelopedData createCMSEnvelopedData(byte[] valueBytes) throws AbstractCMSException;

    /**
     * Create timestamp request generator wrapper without parameters.
     *
     * @return created timestamp request generator wrapper
     */
    ITimeStampRequestGenerator createTimeStampRequestGenerator();

    /**
     * Create timestamp response wrapper from {@code byte[]} value.
     *
     * @param respBytes {@code byte[]} value to create timestamp response wrapper from
     *
     * @return created timestamp response wrapper
     *
     * @throws AbstractTSPException if actual TSP Exception was thrown during wrapper creation
     * @throws IOException          if input-output exception occurs during creation
     */
    ITimeStampResponse createTimeStampResponse(byte[] respBytes) throws AbstractTSPException, IOException;

    /**
     * Create OCSP Exception wrapper from usual {@link Exception}.
     *
     * @param e {@link Exception} to create OCSP Exception wrapper from
     *
     * @return created OCSP Exception wrapper
     */
    AbstractOCSPException createAbstractOCSPException(Exception e);

    /**
     * Create unknown status wrapper without parameters.
     *
     * @return created unknown status wrapper
     */
    IUnknownStatus createUnknownStatus();

    /**
     * Create ASN1 Dump wrapper without parameters.
     *
     * @return created ASN1 Dump wrapper
     */
    IASN1Dump createASN1Dump();

    /**
     * Cast ASN1 Encodable wrapper to ASN1 Bit string wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 Bit string wrapper
     */
    IASN1BitString createASN1BitString(IASN1Encodable encodable);

    /**
     * Cast ASN1 Encodable wrapper to ASN1 Generalized time wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 Generalized time wrapper
     */
    IASN1GeneralizedTime createASN1GeneralizedTime(IASN1Encodable encodable);

    /**
     * Cast ASN1 Encodable wrapper to ASN1 UTC Time wrapper.
     *
     * @param encodable ASN1 Encodable wrapper to be cast
     *
     * @return casted ASN1 UTC Time wrapper
     */
    IASN1UTCTime createASN1UTCTime(IASN1Encodable encodable);

    /**
     * Create Jca cert store wrapper from {@link List} of {@link Certificate} objects.
     *
     * @param certificates {@link List} of {@link Certificate} objects to create Jca cert store wrapper from
     *
     * @return created Jca cert store wrapper
     *
     * @throws CertificateEncodingException if certificate encoding exception occurs during wrapper creation
     */
    IJcaCertStore createJcaCertStore(List<Certificate> certificates) throws CertificateEncodingException;

    /**
     * Create timestamp response generator wrapper from timestamp token generator wrapper and {@link Set} of algorithms.
     *
     * @param tokenGenerator timestamp token generator wrapper to create timestamp response generator wrapper from
     * @param algorithms     {@link Set} of algorithms to create timestamp response generator wrapper from
     *
     * @return created timestamp response generator wrapper
     */
    ITimeStampResponseGenerator createTimeStampResponseGenerator(ITimeStampTokenGenerator tokenGenerator,
            Set<String> algorithms);

    /**
     * Create timestamp request wrapper from {@code byte[]} value.
     *
     * @param bytes {@code byte[]} value to create timestamp request wrapper from
     *
     * @return created timestamp request wrapper
     *
     * @throws IOException if input-output exception occurs during creation
     */
    ITimeStampRequest createTimeStampRequest(byte[] bytes) throws IOException;

    /**
     * Create Jca content signer builder wrapper from {@link String} algorithm.
     *
     * @param algorithm {@link String} algorithm to create Jca content signer builder wrapper from
     *
     * @return created Jca content signer builder wrapper
     */
    IJcaContentSignerBuilder createJcaContentSignerBuilder(String algorithm);

    /**
     * Create Jca signer info generator builder wrapper from digest calculator provider wrapper.
     *
     * @param digestCalcProviderProvider digest calculator provider wrapper to create builder wrapper from
     *
     * @return created Jca signer info generator builder wrapper
     */
    IJcaSignerInfoGeneratorBuilder createJcaSignerInfoGeneratorBuilder(
            IDigestCalculatorProvider digestCalcProviderProvider);

    /**
     * Create timestamp token generator wrapper from signer info generator wrapper,
     * digest calculator wrapper and ASN1 Object identifier wrapper.
     *
     * @param siGen  signer info generator wrapper to create timestamp token generator wrapper from
     * @param dgCalc digest calculator wrapper to create timestamp token generator wrapper from
     * @param policy ASN1 Object identifier wrapper to create timestamp token generator wrapper from
     *
     * @return created timestamp token generator wrapper
     *
     * @throws AbstractTSPException if actual TSP Exception occurs during wrapper creation
     */
    ITimeStampTokenGenerator createTimeStampTokenGenerator(ISignerInfoGenerator siGen, IDigestCalculator dgCalc,
            IASN1ObjectIdentifier policy) throws AbstractTSPException;

    /**
     * Create X500 Name wrapper from {@link X509Certificate}.
     *
     * @param certificate {@link X509Certificate} to create X500 Name wrapper from
     *
     * @return created X500 Name wrapper
     *
     * @throws CertificateEncodingException if certificate encoding exception occurs during wrapper creation
     */
    IX500Name createX500Name(X509Certificate certificate) throws CertificateEncodingException;

    /**
     * Create X500 Name wrapper from {@link String}.
     *
     * @param s {@link String} to create X500 Name wrapper from
     *
     * @return created X500 Name wrapper
     */
    IX500Name createX500Name(String s);

    /**
     * Create resp ID wrapper from X500 Name wrapper.
     *
     * @param x500Name X500 Name wrapper to create resp ID wrapper from
     *
     * @return created resp ID wrapper
     */
    IRespID createRespID(IX500Name x500Name);

    /**
     * Create basic OCSP Resp builder wrapper from resp ID wrapper.
     *
     * @param respID resp ID wrapper to create basic OCSP Resp builder wrapper from
     *
     * @return created basic OCSP Resp builder wrapper
     */
    IBasicOCSPRespBuilder createBasicOCSPRespBuilder(IRespID respID);

    /**
     * Create OCSP Req wrapper from {@code byte[]}.
     *
     * @param requestBytes {@code byte[]} to create OCSP Req wrapper from
     *
     * @return created OCSP Req wrapper
     *
     * @throws IOException if input-output exception occurs during creation
     */
    IOCSPReq createOCSPReq(byte[] requestBytes) throws IOException;

    /**
     * Create X509 Version 2 CRL Builder wrapper from X500 Name wrapper and {@link Date}.
     *
     * @param x500Name   X500 Name wrapper to create X509 Version 2 CRL Builder wrapper from
     * @param thisUpdate {@link Date} to create X509 Version 2 CRL Builder wrapper from
     *
     * @return created X509 Version 2 CRL Builder wrapper
     */
    IX509v2CRLBuilder createX509v2CRLBuilder(IX500Name x500Name, Date thisUpdate);

    /**
     * Create Jca X509 Version 3 certificate builder wrapper from {@link X509Certificate},
     * {@link BigInteger}, start {@link Date}, end {@link Date}, X500 Name wrapper and {@link PublicKey}.
     *
     * @param signingCert      {@link X509Certificate} to create Jca X509 Version 3 certificate builder wrapper from
     * @param certSerialNumber {@link BigInteger} to create Jca X509 Version 3 certificate builder wrapper from
     * @param startDate        start {@link Date} to create Jca X509 Version 3 certificate builder wrapper from
     * @param endDate          end {@link Date} to create Jca X509 Version 3 certificate builder wrapper from
     * @param subjectDnName    X500 Name wrapper to create Jca X509 Version 3 certificate builder wrapper from
     * @param publicKey        {@link PublicKey} to create Jca X509 Version 3 certificate builder wrapper from
     *
     * @return created Jca X509 Version 3 certificate builder wrapper
     */
    IJcaX509v3CertificateBuilder createJcaX509v3CertificateBuilder(X509Certificate signingCert,
            BigInteger certSerialNumber, Date startDate, Date endDate, IX500Name subjectDnName, PublicKey publicKey);

    /**
     * Create basic constraints wrapper from {@code boolean} value.
     *
     * @param b {@code boolean} value to create basic constraints wrapper from
     *
     * @return created basic constraints wrapper
     */
    IBasicConstraints createBasicConstraints(boolean b);

    /**
     * Create basic constraints wrapper from {@code int} value.
     *
     * @param pathLength {@code int} flag to create basic constraints wrapper from
     *
     * @return created basic constraints wrapper
     */
    IBasicConstraints createBasicConstraints(int pathLength);

    /**
     * Create key usage wrapper without parameters.
     *
     * @return created key usage wrapper
     */
    IKeyUsage createKeyUsage();

    /**
     * Create key usage wrapper from {@code int} value.
     *
     * @param i {@code int} value to create key usage wrapper from
     *
     * @return created key usage wrapper
     */
    IKeyUsage createKeyUsage(int i);

    /**
     * Create key purpose id wrapper without parameters.
     *
     * @return created key purpose id wrapper
     */
    IKeyPurposeId createKeyPurposeId();

    /**
     * Create key purpose id wrapper from {@link IASN1ObjectIdentifier}.
     *
     * @param objectIdentifier {@link IASN1ObjectIdentifier} to create key purpose id wrapper from
     *
     * @return created key purpose id wrapper
     */
    IKeyPurposeId createKeyPurposeId(IASN1ObjectIdentifier objectIdentifier);

    /**
     * Create extended key usage wrapper from key purpose id wrapper.
     *
     * @param purposeId key purpose id wrapper to create extended key usage wrapper from
     *
     * @return created extended key usage wrapper
     */
    IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId purposeId);

    /**
     * Create extended key usage wrapper from key purpose id wrappers array.
     *
     * @param purposeIds {@link IKeyPurposeId} array to create extended key usage wrapper from
     *
     * @return created extended key usage wrapper
     */
    IExtendedKeyUsage createExtendedKeyUsage(IKeyPurposeId[] purposeIds);

    /**
     * Create X509 Extension utils wrapper from digest calculator wrapper.
     *
     * @param digestCalculator digest calculator wrapper to create X509 Extension utils wrapper from
     *
     * @return created X509 Extension utils wrapper
     */
    IX509ExtensionUtils createX509ExtensionUtils(IDigestCalculator digestCalculator);

    /**
     * Create subject public key info wrapper from {@link Object}.
     *
     * @param obj {@link Object} to create subject public ket info wrapper from
     *
     * @return created subject public ket info wrapper
     */
    ISubjectPublicKeyInfo createSubjectPublicKeyInfo(Object obj);

    /**
     * Create CRL Reason wrapper without parameters.
     *
     * @return created CRL Reason wrapper
     */
    ICRLReason createCRLReason();

    /**
     * Create TST Info wrapper from content info wrapper.
     *
     * @param contentInfo content info wrapper to create TST Info wrapper from
     *
     * @return created TST Info wrapper
     *
     * @throws AbstractTSPException if actual TSP Exception occurs during wrapper creation
     * @throws IOException          if input-output exception occurs during creation
     */
    ITSTInfo createTSTInfo(IContentInfo contentInfo) throws AbstractTSPException, IOException;

    /**
     * Create single resp wrapper from basic OCSP Response wrapper.
     *
     * @param basicResp basic OCSP Response wrapper to create single resp wrapper from
     *
     * @return created single resp wrapper
     */
    ISingleResp createSingleResp(IBasicOCSPResponse basicResp);

    /**
     * Cast {@link Object} element to {@link X509Certificate}.
     *
     * @param element {@link Object} to be cast
     *
     * @return casted {@link X509Certificate}
     */
    X509Certificate createX509Certificate(Object element);

    /**
     * Get {@link IBouncyCastleTestConstantsFactory} corresponding to this {@link IBouncyCastleFactory}.
     *
     * @return {@link IBouncyCastleTestConstantsFactory} instance
     */
    IBouncyCastleTestConstantsFactory getBouncyCastleFactoryTestUtil();

    /**
     * Create {@code null} as {@link CRL} object.
     *
     * @return {@code null} as {@link CRL} object
     */
    CRL createNullCrl();

    /**
     * Create PEM Parser wrapper from {@link Reader}.
     *
     * @param reader {@link Reader} to create PEM Parser wrapper from
     *
     * @return created PEM Parser wrapper
     */
    IPEMParser createPEMParser(Reader reader);

    /**
     * Create Jce open SSL PKCS8 Decryptor provider builder wrapper without parameters.
     *
     * @return created Jce open SSL PKCS8 Decryptor provider builder wrapper
     */
    IJceOpenSSLPKCS8DecryptorProviderBuilder createJceOpenSSLPKCS8DecryptorProviderBuilder();

    /**
     * Create Jca PEM Key converter wrapper without parameters.
     *
     * @return created Jca PEM Key converter wrapper
     */
    IJcaPEMKeyConverter createJcaPEMKeyConverter();

    /**
     * Create time wrapper from {@link Date}.
     *
     * @param date {@link Date} to create time wrapper from
     *
     * @return created time wrapper
     */
    ITime createTime(Date date);

    /**
     * Create time wrapper from the end date of the certificate.
     *
     * @param certificate {@link X509Certificate} to get end date to create time wrapper from
     *
     * @return created time wrapper
     */
    ITime createEndDate(X509Certificate certificate);

    /**
     * Checks if provided extension wrapper wraps {@code null}.
     *
     * @param extNonce extension wrapper to check
     *
     * @return {@code true} if provided extension wrapper wraps {@code null}, {@code false} otherwise
     */
    boolean isNullExtension(IExtension extNonce);

    /**
     * Check if provided encodable wrapper wrap {@code null}.
     * 
     * @param encodable encodable wrapper to be checked
     * 
     * @return {@code true} if provided encodable wrapper wraps {@code null}, {@code false} otherwise
     */
    boolean isNull(IASN1Encodable encodable);

    /**
     * Get {@link SecureRandom} implementation from the factory.
     *
     * @return {@link SecureRandom} implementation
     */
    SecureRandom getSecureRandom();

    /**
     * Check if this bouncy-castle corresponding to this factory is in approved mode.
     *
     * @return {@code true} if approved mode is enabled, {@code false} otherwise
     */
    boolean isInApprovedOnlyMode();

    /**
     * Create cipher bytes from {@link X509Certificate}, {@code byte[]} value and algorithm identifier wrapper.
     *
     * @param x509certificate     {@link X509Certificate} to create cipher bytes from
     * @param abyte0              {@code byte[]} value to create cipher bytes from
     * @param algorithmIdentifier algorithm identifier wrapper to create cipher bytes from
     *
     * @return {@code byte[]} representing created cipher bytes
     *
     * @throws GeneralSecurityException if general security exception occurs during cipher bytes creation
     */
    byte[] createCipherBytes(X509Certificate x509certificate, byte[] abyte0, IAlgorithmIdentifier algorithmIdentifier)
            throws GeneralSecurityException;

    /**
     * Checks whether an algorithm is supported for encryption by the chosen Bouncy Castle implementation,
     * throws an exception when not supported.
     *
     * @param encryptionAlgorithm the type of encryption. It can be one of
     *                            STANDARD_ENCRYPTION_40 = 0
     *                            STANDARD_ENCRYPTION_128 = 1,
     *                            ENCRYPTION_AES_128 = 2
     *                            ENCRYPTION_AES_256 = 3
     *                            in combination with (or-ed)
     *                            DO_NOT_ENCRYPT_METADATA = 8
     *                            and EMBEDDED_FILES_ONLY = 24
     *
     * @param withCertificate true when used with a certificate, false otherwise
     */
    void isEncryptionFeatureSupported(int encryptionAlgorithm, boolean withCertificate);
}
