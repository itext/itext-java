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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IRSASSAPSSParams;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

/**
 * This class does all the processing related to signing
 * and verifying a PKCS#7 / CMS signature.
 */
public class PdfPKCS7 {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private ISignaturePolicyIdentifier signaturePolicyIdentifier;

    // Encryption provider

    /**
     * The encryption provider, e.g. "BC" if you use BouncyCastle.
     */
    private final String provider;

    // Signature info

    /**
     * Holds value of property signName.
     */
    private String signName;

    /**
     * Holds value of property reason.
     */
    private String reason;

    /**
     * Holds value of property location.
     */
    private String location;

    /**
     * Holds value of property signDate.
     */
    private Calendar signDate = (Calendar) TimestampConstants.UNDEFINED_TIMESTAMP_DATE;

    /**
     * Collection to store revocation info other than OCSP and CRL responses, e.g. SCVP Request and Response.
     */
    private final Collection<IASN1Sequence> signedDataRevocationInfo = new ArrayList<>();

    private final IASN1EncodableVector unsignedAttributes = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();

    // Constructors for creating new signatures

    /**
     * Assembles all the elements needed to create a signature, except for the data.
     *
     * @param privKey         the private key
     * @param certChain       the certificate chain
     * @param interfaceDigest the interface digest
     * @param hashAlgorithm   the hash algorithm
     * @param provider        the provider or <code>null</code> for the default provider
     * @param hasEncapContent <CODE>true</CODE> if the sub-filter is adbe.pkcs7.sha1
     * @throws InvalidKeyException      on error
     * @throws NoSuchProviderException  on error
     * @throws NoSuchAlgorithmException on error
     */
    public PdfPKCS7(PrivateKey privKey, Certificate[] certChain,
                    String hashAlgorithm, String provider, IExternalDigest interfaceDigest, boolean hasEncapContent)
            throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
        this.provider = provider;
        this.interfaceDigest = interfaceDigest;
        // message digest
        digestAlgorithmOid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);
        if (digestAlgorithmOid == null) {
            throw new PdfException(SignExceptionMessageConstant.UNKNOWN_HASH_ALGORITHM)
                    .setMessageParams(hashAlgorithm);
        }

        // Copy the certificates
        signCert = (X509Certificate) certChain[0];
        certs = new ArrayList<>();
        Collections.addAll(certs, certChain);

        // initialize and add the digest algorithms.
        digestalgos = new HashSet<>();
        digestalgos.add(digestAlgorithmOid);

        // find the signing algorithm
        if (privKey != null) {
            String signatureAlgo = SignUtils.getPrivateKeyAlgorithm(privKey);
            String mechanismOid = SignatureMechanisms.getSignatureMechanismOid(signatureAlgo, hashAlgorithm);
            if (mechanismOid == null) {
                throw new PdfException(SignExceptionMessageConstant.COULD_NOT_DETERMINE_SIGNATURE_MECHANISM_OID)
                        .setMessageParams(signatureAlgo, hashAlgorithm);
            }
            this.signatureMechanismOid = mechanismOid;
        }

        // initialize the encapsulated content
        if (hasEncapContent) {
            encapMessageContent = new byte[0];
            messageDigest = DigestAlgorithms.getMessageDigest(getDigestAlgorithmName(), provider);
        }

        // initialize the Signature object
        if (privKey != null) {
            sig = initSignature(privKey);
        }
    }

    /**
     * Assembles all the elements needed to create a signature, except for the data.
     *
     * @param privKey         the private key
     * @param certChain       the certificate chain
     * @param hashAlgorithm   the hash algorithm
     * @param provider        the provider or <code>null</code> for the default provider
     * @param hasEncapContent <CODE>true</CODE> if the sub-filter is adbe.pkcs7.sha1
     * @throws InvalidKeyException      on error
     * @throws NoSuchProviderException  on error
     * @throws NoSuchAlgorithmException on error
     */
    public PdfPKCS7(PrivateKey privKey, Certificate[] certChain, String hashAlgorithm, String provider,
                    boolean hasEncapContent)
            throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
        this(privKey, certChain, hashAlgorithm, provider, new BouncyCastleDigest(), hasEncapContent);
    }

    // Constructors for validating existing signatures

    /**
     * Use this constructor if you want to verify a signature using the sub-filter adbe.x509.rsa_sha1.
     *
     * @param contentsKey the /Contents key
     * @param certsKey    the /Cert key
     * @param provider    the provider or <code>null</code> for the default provider
     */
    public PdfPKCS7(byte[] contentsKey, byte[] certsKey, String provider) {
        try {
            this.provider = provider;
            certs = SignUtils.readAllCerts(certsKey);
            signCerts = certs;
            signCert = (X509Certificate) SignUtils.getFirstElement(certs);
            crls = new ArrayList<>();

            try (IASN1InputStream in =
                    BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(contentsKey))) {
                signatureValue = BOUNCY_CASTLE_FACTORY.createASN1OctetString(in.readObject()).getOctets();
            }

            sig = SignUtils.getSignatureHelper("SHA1withRSA", provider);
            sig.initVerify(signCert.getPublicKey());

            // setting the oid to SHA1withRSA
            digestAlgorithmOid = "1.2.840.10040.4.3";
            signatureMechanismOid = "1.3.36.3.3.1.2";
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Use this constructor if you want to verify a signature.
     *
     * @param contentsKey   the /Contents key
     * @param filterSubtype the filtersubtype
     * @param provider      the provider or <code>null</code> for the default provider
     */
    public PdfPKCS7(byte[] contentsKey, PdfName filterSubtype, String provider) {
        this.filterSubtype = filterSubtype;
        isTsp = PdfName.ETSI_RFC3161.equals(filterSubtype);
        isCades = PdfName.ETSI_CAdES_DETACHED.equals(filterSubtype);
        try {
            this.provider = provider;

            //
            // Basic checks to make sure it's a PKCS#7 SignedData Object
            //
            IASN1Primitive pkcs;

            try (IASN1InputStream din =
                    BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(contentsKey))) {
                pkcs = din.readObject();
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        SignExceptionMessageConstant.CANNOT_DECODE_PKCS7_SIGNED_DATA_OBJECT);
            }
            IASN1Sequence signedData = BOUNCY_CASTLE_FACTORY.createASN1Sequence(pkcs);
            if (signedData == null) {
                throw new IllegalArgumentException(
                        SignExceptionMessageConstant.NOT_A_VALID_PKCS7_OBJECT_NOT_A_SEQUENCE);
            }
            IASN1ObjectIdentifier objId = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(signedData.getObjectAt(0));
            if (!objId.getId().equals(OID.PKCS7_SIGNED_DATA)) {
                throw new IllegalArgumentException(
                        SignExceptionMessageConstant.NOT_A_VALID_PKCS7_OBJECT_NOT_SIGNED_DATA);
            }
            IASN1Sequence content = BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                    BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(signedData.getObjectAt(1)).getObject());
            // the positions that we care are:
            //     0 - version
            //     1 - digestAlgorithms
            //     2 - possible ID_PKCS7_DATA
            //     (the certificates and crls are taken out by other means)
            //     last - signerInfos

            // the version
            version = BOUNCY_CASTLE_FACTORY.createASN1Integer(content.getObjectAt(0)).getValue().intValue();

            // the digestAlgorithms
            digestalgos = new HashSet<>();
            Enumeration e = BOUNCY_CASTLE_FACTORY.createASN1Set(content.getObjectAt(1)).getObjects();
            while (e.hasMoreElements()) {
                IASN1Sequence s = BOUNCY_CASTLE_FACTORY.createASN1Sequence(e.nextElement());
                IASN1ObjectIdentifier o = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(s.getObjectAt(0));
                digestalgos.add(o.getId());
            }

            // the possible ID_PKCS7_DATA
            IASN1Sequence encapContentInfo = BOUNCY_CASTLE_FACTORY.createASN1Sequence(content.getObjectAt(2));
            if (encapContentInfo.size() > 1) {
                IASN1OctetString encapContent = BOUNCY_CASTLE_FACTORY.createASN1OctetString(
                        BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(encapContentInfo.getObjectAt(1)).getObject());
                this.encapMessageContent = encapContent.getOctets();
            }

            int next = 3;
            IASN1TaggedObject taggedObj;
            while ((taggedObj = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(content.getObjectAt(next))) != null) {
                ++next;
                if (taggedObj.getTagNo() == 1) {
                    // the crls
                    CertificateUtil.retrieveRevocationInfoFromSignedData(taggedObj, this.signedDataCrls,
                            this.signedDataOcsps, this.signedDataRevocationInfo);
                }
            }

            // the certificates
            this.certs = SignUtils.readAllCerts(contentsKey);

            // the signerInfos
            IASN1Set signerInfos = BOUNCY_CASTLE_FACTORY.createASN1Set(content.getObjectAt(next));
            if (signerInfos.size() != 1) {
                throw new IllegalArgumentException(
                        SignExceptionMessageConstant.THIS_PKCS7_OBJECT_HAS_MULTIPLE_SIGNERINFOS_ONLY_ONE_IS_SUPPORTED_AT_THIS_TIME);
            }
            IASN1Sequence signerInfo = BOUNCY_CASTLE_FACTORY.createASN1Sequence(signerInfos.getObjectAt(0));
            // the positions that we care are
            //     0 - version
            //     1 - the signing certificate issuer and serial number
            //     2 - the digest algorithm
            //     3 or 4 - digestEncryptionAlgorithm
            //     4 or 5 - encryptedDigest
            signerversion = BOUNCY_CASTLE_FACTORY.createASN1Integer(signerInfo.getObjectAt(0)).getValue().intValue();
            // Get the signing certificate
            IASN1Sequence issuerAndSerialNumber = BOUNCY_CASTLE_FACTORY.createASN1Sequence(signerInfo.getObjectAt(1));
            X500Principal issuer = SignUtils.getIssuerX500Principal(issuerAndSerialNumber);
            BigInteger serialNumber = BOUNCY_CASTLE_FACTORY.createASN1Integer(issuerAndSerialNumber.getObjectAt(1))
                    .getValue();
            for (Object element : certs) {
                X509Certificate cert = BOUNCY_CASTLE_FACTORY.createX509Certificate(element);
                if (cert.getIssuerX500Principal().equals(issuer) && serialNumber.equals(cert.getSerialNumber())) {
                    signCert = cert;
                    break;
                }
            }
            if (signCert == null) {
                throw new PdfException(SignExceptionMessageConstant.CANNOT_FIND_SIGNING_CERTIFICATE_WITH_THIS_SERIAL).
                        setMessageParams(issuer.getName() + " / " + serialNumber.toString(16));
            }
            signCertificateChain();
            digestAlgorithmOid = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                    BOUNCY_CASTLE_FACTORY.createASN1Sequence(signerInfo.getObjectAt(2)).getObjectAt(0)).getId();
            next = 3;
            boolean foundCades = false;
            IASN1TaggedObject tagsig = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(signerInfo.getObjectAt(next));
            if (tagsig != null) {
                IASN1Set sseq = BOUNCY_CASTLE_FACTORY.createASN1Set(tagsig, false);
                sigAttr = sseq.getEncoded();
                // maybe not necessary, but we use the following line as fallback:
                sigAttrDer = sseq.getEncoded(BOUNCY_CASTLE_FACTORY.createASN1Encoding().getDer());

                for (int k = 0; k < sseq.size(); ++k) {
                    IASN1Sequence seq2 = BOUNCY_CASTLE_FACTORY.createASN1Sequence(sseq.getObjectAt(k));
                    String idSeq2 = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(seq2.getObjectAt(0)).getId();
                    if (idSeq2.equals(OID.MESSAGE_DIGEST)) {
                        IASN1Set set = BOUNCY_CASTLE_FACTORY.createASN1Set(seq2.getObjectAt(1));
                        digestAttr = BOUNCY_CASTLE_FACTORY.createASN1OctetString(set.getObjectAt(0)).getOctets();
                    } else if (idSeq2.equals(OID.ADBE_REVOCATION)) {
                        IASN1Set setout = BOUNCY_CASTLE_FACTORY.createASN1Set(seq2.getObjectAt(1));
                        IASN1Sequence seqout = BOUNCY_CASTLE_FACTORY.createASN1Sequence(setout.getObjectAt(0));
                        for (int j = 0; j < seqout.size(); ++j) {
                            IASN1TaggedObject tg = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(seqout.getObjectAt(j));
                            if (tg.getTagNo() == 0) {
                                IASN1Sequence seqin = BOUNCY_CASTLE_FACTORY.createASN1Sequence(tg.getObject());
                                findCRL(seqin);
                            }
                            if (tg.getTagNo() == 1) {
                                IASN1Sequence seqin = BOUNCY_CASTLE_FACTORY.createASN1Sequence(tg.getObject());
                                findOcsp(seqin);
                            }
                        }
                    } else if (isCades && idSeq2.equals(OID.AA_SIGNING_CERTIFICATE_V1)) {
                        IASN1Set setout = BOUNCY_CASTLE_FACTORY.createASN1Set(seq2.getObjectAt(1));
                        IASN1Sequence seqout = BOUNCY_CASTLE_FACTORY.createASN1Sequence(setout.getObjectAt(0));
                        ISigningCertificate sv2 = BOUNCY_CASTLE_FACTORY.createSigningCertificate(seqout);
                        IESSCertID[] cerv2m = sv2.getCerts();
                        IESSCertID cerv2 = cerv2m[0];
                        byte[] enc2 = signCert.getEncoded();
                        MessageDigest m2 = SignUtils.getMessageDigest("SHA-1");
                        byte[] signCertHash = m2.digest(enc2);
                        byte[] hs2 = cerv2.getCertHash();
                        if (!Arrays.equals(signCertHash, hs2)) {
                            throw new IllegalArgumentException(
                                    "Signing certificate doesn't match the ESS information.");
                        }
                        foundCades = true;
                    } else if (isCades && idSeq2.equals(OID.AA_SIGNING_CERTIFICATE_V2)) {
                        IASN1Set setout = BOUNCY_CASTLE_FACTORY.createASN1Set(seq2.getObjectAt(1));
                        IASN1Sequence seqout = BOUNCY_CASTLE_FACTORY.createASN1Sequence(setout.getObjectAt(0));
                        ISigningCertificateV2 sv2 = BOUNCY_CASTLE_FACTORY.createSigningCertificateV2(seqout);
                        IESSCertIDv2[] cerv2m = sv2.getCerts();
                        IESSCertIDv2 cerv2 = cerv2m[0];
                        IAlgorithmIdentifier ai2 = cerv2.getHashAlgorithm();
                        byte[] enc2 = signCert.getEncoded();
                        MessageDigest m2
                                = SignUtils.getMessageDigest(DigestAlgorithms.getDigest(ai2.getAlgorithm().getId()));
                        byte[] signCertHash = m2.digest(enc2);
                        byte[] hs2 = cerv2.getCertHash();
                        if (!Arrays.equals(signCertHash, hs2)) {
                            throw new IllegalArgumentException(
                                    "Signing certificate doesn't match the ESS information.");
                        }
                        foundCades = true;
                    }
                }
                if (digestAttr == null) {
                    throw new IllegalArgumentException(
                            SignExceptionMessageConstant.AUTHENTICATED_ATTRIBUTE_IS_MISSING_THE_DIGEST);
                }
                ++next;
            }
            if (isCades && !foundCades) {
                throw new IllegalArgumentException("CAdES ESS information missing.");
            }
            IASN1Sequence signatureMechanismInfo = BOUNCY_CASTLE_FACTORY
                    .createASN1Sequence(signerInfo.getObjectAt(next));
            ++next;
            signatureMechanismOid = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                    signatureMechanismInfo.getObjectAt(0)).getId();
            if (signatureMechanismInfo.size() > 1) {
                signatureMechanismParameters = signatureMechanismInfo.getObjectAt(1);
            }
            signatureValue = BOUNCY_CASTLE_FACTORY.createASN1OctetString(signerInfo.getObjectAt(next)).getOctets();
            ++next;
            if (next < signerInfo.size()) {
                IASN1TaggedObject taggedObject = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(
                        signerInfo.getObjectAt(next));
                if (taggedObject != null) {
                    IASN1Set unat = BOUNCY_CASTLE_FACTORY.createASN1Set(taggedObject, false);
                    IAttributeTable attble = BOUNCY_CASTLE_FACTORY.createAttributeTable(unat);
                    IPKCSObjectIdentifiers ipkcsObjectIdentifiers = BOUNCY_CASTLE_FACTORY.createPKCSObjectIdentifiers();
                    IAttribute ts = attble.get(ipkcsObjectIdentifiers.getIdAaSignatureTimeStampToken());
                    if (!BOUNCY_CASTLE_FACTORY.isNull(ts) && ts.getAttrValues().size() > 0) {
                        IASN1Set attributeValues = ts.getAttrValues();
                        IASN1Sequence tokenSequence =
                                BOUNCY_CASTLE_FACTORY.createASN1SequenceInstance(attributeValues.getObjectAt(0));
                        this.timestampSignatureContainer = new PdfPKCS7(tokenSequence.getEncoded(),
                                PdfName.ETSI_RFC3161, BOUNCY_CASTLE_FACTORY.getProviderName());
                        this.timestampSignatureContainer.update(signatureValue, 0, signatureValue.length);
                        this.timestampCerts = SignUtils.readAllCerts(tokenSequence.getEncoded());
                        IContentInfo contentInfo = BOUNCY_CASTLE_FACTORY.createContentInfo(tokenSequence);
                        this.timeStampTokenInfo = BOUNCY_CASTLE_FACTORY.createTSTInfo(contentInfo);
                    }
                }
            }
            if (isTsp) {
                IContentInfo contentInfoTsp = BOUNCY_CASTLE_FACTORY.createContentInfo(signedData);
                this.timeStampTokenInfo = BOUNCY_CASTLE_FACTORY.createTSTInfo(contentInfoTsp);
                this.timestampCerts = this.certs;
                String algOID = timeStampTokenInfo.getMessageImprint().getHashAlgorithm().getAlgorithm().getId();
                messageDigest = DigestAlgorithms.getMessageDigestFromOid(algOID, null);
                encContDigest = DigestAlgorithms.getMessageDigest(getDigestAlgorithmName(), provider);
            } else {
                if (this.encapMessageContent != null || digestAttr != null) {
                    if (PdfName.Adbe_pkcs7_sha1.equals(getFilterSubtype())) {
                        messageDigest = DigestAlgorithms.getMessageDigest("SHA1", provider);
                    } else {
                        messageDigest = DigestAlgorithms.getMessageDigest(getDigestAlgorithmName(), provider);
                    }
                    encContDigest = DigestAlgorithms.getMessageDigest(getDigestAlgorithmName(), provider);
                }
                sig = initSignature(signCert.getPublicKey());
            }
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Get unsigned attributes associated with this PKCS7 signature container.
     *
     * @return unsigned attributes as {@link IASN1EncodableVector}
     */
    public IASN1EncodableVector getUnsignedAttributes() {
        return unsignedAttributes;
    }

    /**
     * Set signature policy identifier to be used during signature creation.
     *
     * @param signaturePolicy {@link SignaturePolicyInfo} to be used during signature creation
     */
    public void setSignaturePolicy(SignaturePolicyInfo signaturePolicy) {
        this.signaturePolicyIdentifier = signaturePolicy.toSignaturePolicyIdentifier();
    }

    /**
     * Set signature policy identifier to be used during signature creation.
     *
     * @param signaturePolicy {@link ISignaturePolicyIdentifier} to be used during signature creation
     */
    public void setSignaturePolicy(ISignaturePolicyIdentifier signaturePolicy) {
        this.signaturePolicyIdentifier = signaturePolicy;
    }

    /**
     * Getter for property sigName.
     *
     * @return Value of property sigName.
     */
    public String getSignName() {
        return this.signName;
    }

    /**
     * Setter for property sigName.
     *
     * @param signName New value of property sigName.
     */
    public void setSignName(String signName) {
        this.signName = signName;
    }

    /**
     * Getter for property reason.
     *
     * @return Value of property reason.
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Setter for property reason.
     *
     * @param reason New value of property reason.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Getter for property location.
     *
     * @return Value of property location.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Setter for property location.
     *
     * @param location New value of property location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter for property signDate.
     *
     * @return Value of property signDate.
     */
    public Calendar getSignDate() {
        Calendar dt = getTimeStampDate();
        if (dt == TimestampConstants.UNDEFINED_TIMESTAMP_DATE) {
            return this.signDate;
        } else {
            return dt;
        }
    }

    /**
     * Setter for property signDate.
     *
     * @param signDate New value of property signDate.
     */
    public void setSignDate(Calendar signDate) {
        this.signDate = signDate;
    }

    // version info

    /**
     * Version of the PKCS#7 object
     */
    private int version = 1;

    /**
     * Version of the PKCS#7 "SignerInfo" object.
     */
    private int signerversion = 1;

    /**
     * Get the version of the PKCS#7 object.
     *
     * @return the version of the PKCS#7 object.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get the version of the PKCS#7 "SignerInfo" object.
     *
     * @return the version of the PKCS#7 "SignerInfo" object.
     */
    public int getSigningInfoVersion() {
        return signerversion;
    }

    // Message digest algorithm

    /**
     * The ID of the digest algorithm, e.g. "2.16.840.1.101.3.4.2.1".
     */
    private final String digestAlgorithmOid;

    /**
     * The object that will create the digest
     */
    private MessageDigest messageDigest;

    /**
     * The digest algorithms
     */
    private Set<String> digestalgos;

    /**
     * The digest attributes
     */
    private byte[] digestAttr;

    private PdfName filterSubtype;

    /**
     * The signature algorithm.
     */
    private String signatureMechanismOid;

    private IASN1Encodable signatureMechanismParameters = null;

    /**
     * Getter for the ID of the digest algorithm, e.g. "2.16.840.1.101.3.4.2.1".
     * See ISO-32000-1, section 12.8.3.3 PKCS#7 Signatures as used in ISO 32000
     *
     * @return the ID of the digest algorithm
     */
    public String getDigestAlgorithmOid() {
        return digestAlgorithmOid;
    }

    /**
     * Returns the name of the digest algorithm, e.g. "SHA256".
     *
     * @return the digest algorithm name, e.g. "SHA256"
     */
    public String getDigestAlgorithmName() {
        String hashAlgoName = DigestAlgorithms.getDigest(digestAlgorithmOid);
        // Ed25519 and Ed448 do not allow a choice of hashing algorithm,
        // and ISO 32002 requires using a fixed hashing algorithm to
        // digest the document content
        if (OID.ED25519.equals(this.signatureMechanismOid)
                && !OID.SHA_512.equals(digestAlgorithmOid)) {
            // We compare based on OID to ensure that there are no name normalisation issues.
            throw new PdfException(SignExceptionMessageConstant.ALGO_REQUIRES_SPECIFIC_HASH)
                    .setMessageParams("Ed25519", "SHA-512", hashAlgoName);
        } else if (OID.ED448.equals(this.signatureMechanismOid)
                    && !OID.SHAKE_256.equals(digestAlgorithmOid)) {
            throw new PdfException(SignExceptionMessageConstant.ALGO_REQUIRES_SPECIFIC_HASH)
                    .setMessageParams("Ed448", "512-bit SHAKE256", hashAlgoName);
        }
        return hashAlgoName;
    }

    /**
     * Getter for the signature algorithm OID.
     * See ISO-32000-1, section 12.8.3.3 PKCS#7 Signatures as used in ISO 32000
     *
     * @return the signature algorithm OID
     */
    public String getSignatureMechanismOid() {
        return signatureMechanismOid;
    }

    /**
     * Get the signature mechanism identifier, including both the digest function
     * and the signature algorithm, e.g. "SHA1withRSA".
     * See ISO-32000-1, section 12.8.3.3 PKCS#7 Signatures as used in ISO 32000
     *
     * @return the algorithm used to calculate the signature
     */
    public String getSignatureMechanismName() {
        switch (this.signatureMechanismOid) {
            case OID.ED25519:
                // Ed25519 and Ed448 do not involve a choice of hashing algorithm
                return "Ed25519";
            case OID.ED448:
                return "Ed448";
            case OID.RSASSA_PSS:
                // For RSASSA-PSS, the algorithm parameters dictate everything, so
                // there's no need to duplicate that information in the algorithm name.
                return "RSASSA-PSS";
            default:
                return SignatureMechanisms.getMechanism(signatureMechanismOid, getDigestAlgorithmName());
        }
    }


    /**
     * Returns the name of the signature algorithm only (disregarding the digest function, if any).
     *
     * @return the name of an encryption algorithm
     */
    public String getSignatureAlgorithmName() {
        String signAlgo = SignatureMechanisms.getAlgorithm(signatureMechanismOid);
        if (signAlgo == null) {
            signAlgo = signatureMechanismOid;
        }
        return signAlgo;
    }

    /*
     *	DIGITAL SIGNATURE CREATION
     */

    private IExternalDigest interfaceDigest;
    // The signature is created externally

    /**
     * The signature value or signed digest, if created outside this class
     */
    private byte[] externalSignatureValue;

    /**
     * Externally specified encapsulated message content.
     */
    private byte[] externalEncapMessageContent;


    /**
     * Sets the signature to an externally calculated value.
     *
     * @param signatureValue            the signature value
     * @param signedMessageContent      the extra data that goes into the data tag in PKCS#7
     * @param signatureAlgorithm        the signature algorithm. It must be <CODE>null</CODE> if the
     *                                  <CODE>signatureValue</CODE> is also <CODE>null</CODE>.
     *                                  If the <CODE>signatureValue</CODE> is not <CODE>null</CODE>,
     *                                  possible values include "RSA", "DSA", "ECDSA", "Ed25519" and "Ed448".
     */
    public void setExternalSignatureValue(byte[] signatureValue, byte[] signedMessageContent, String signatureAlgorithm) {
        setExternalSignatureValue(signatureValue, signedMessageContent, signatureAlgorithm, null);
    }

    /**
     * Sets the signature to an externally calculated value.
     *
     * @param signatureValue            the signature value
     * @param signedMessageContent      the extra data that goes into the data tag in PKCS#7
     * @param signatureAlgorithm        the signature algorithm. It must be <CODE>null</CODE> if the
     *                                  <CODE>signatureValue</CODE> is also <CODE>null</CODE>.
     *                                  If the <CODE>signatureValue</CODE> is not <CODE>null</CODE>,
     *                                  possible values include "RSA", "RSASSA-PSS", "DSA",
     *                                  "ECDSA", "Ed25519" and "Ed448".
     * @param signatureMechanismParams  parameters for the signature mechanism, if required
     */
    public void setExternalSignatureValue(
            byte[] signatureValue, byte[] signedMessageContent,
            String signatureAlgorithm, ISignatureMechanismParams signatureMechanismParams) {
        externalSignatureValue = signatureValue;
        externalEncapMessageContent = signedMessageContent;
        if (signatureAlgorithm != null) {
            String digestAlgo = this.getDigestAlgorithmName();
            String oid = SignatureMechanisms.getSignatureMechanismOid(signatureAlgorithm, digestAlgo);
            if (oid == null) {
                throw new PdfException(SignExceptionMessageConstant.COULD_NOT_DETERMINE_SIGNATURE_MECHANISM_OID)
                        .setMessageParams(signatureAlgorithm, digestAlgo);
            }
            this.signatureMechanismOid = oid;
        }
        if (signatureMechanismParams != null) {
            this.signatureMechanismParameters = signatureMechanismParams.toEncodable();
        }
    }
    // The signature is created internally

    /**
     * Class from the Java SDK that provides the functionality of a digital signature algorithm.
     */
    private Signature sig;

    /**
     * The raw signature value as calculated by this class (or extracted from an existing PDF)
     */
    private byte[] signatureValue;

    /**
     * The content to which the signature applies, if encapsulated in the PKCS #7 payload.
     */
    private byte[] encapMessageContent;

    // Signing functionality.

    private Signature initSignature(PrivateKey key) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException {
        Signature signature = SignUtils.getSignatureHelper(getSignatureMechanismName(), provider);
        signature.initSign(key);
        return signature;
    }

    private Signature initSignature(PublicKey key) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException {

        String signatureMechanism;
        if (PdfName.Adbe_x509_rsa_sha1.equals(getFilterSubtype())) {
            signatureMechanism = "SHA1withRSA";
        } else {
            signatureMechanism = getSignatureMechanismName();
        }
        Signature signature = SignUtils.getSignatureHelper(signatureMechanism, provider);
        configureSignatureMechanismParameters(signature);
        signature.initVerify(key);
        return signature;
    }

    private void configureSignatureMechanismParameters(Signature signature) {
        if (OID.RSASSA_PSS.equals(this.signatureMechanismOid)) {
            IRSASSAPSSParams params = BOUNCY_CASTLE_FACTORY.createRSASSAPSSParams(this.signatureMechanismParameters);
            String mgfOid = params.getMaskGenAlgorithm().getAlgorithm().getId();
            if (!OID.MGF1.equals(mgfOid)) {
                throw new IllegalArgumentException(SignExceptionMessageConstant.ONLY_MGF1_SUPPORTED_IN_RSASSA_PSS);
            }
            // Even though having separate digests at all "layers" is mathematically fine,
            // it's bad practice at best (and a security problem at worst).
            // We don't support such hybridisation outside RSASSA-PSS either.
            // => on the authority of RFC 8933 we enforce the restriction here.
            String mechParamDigestAlgoOid = params.getHashAlgorithm().getAlgorithm().getId();
            if (!this.digestAlgorithmOid.equals(mechParamDigestAlgoOid)) {
                throw new IllegalArgumentException(MessageFormatUtil.format(
                        SignExceptionMessageConstant.RSASSA_PSS_DIGESTMISSMATCH,
                        mechParamDigestAlgoOid, this.digestAlgorithmOid));
            }

            // This is actually morally an IAlgorithmIdentifier too, but since it's pretty much always going to be a
            // one-element sequence, it's probably not worth putting in a conversion method in the factory interface
            IASN1Sequence mgfParams = BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                    params.getMaskGenAlgorithm().getParameters()
            );
            String mgfParamDigestAlgoOid = BOUNCY_CASTLE_FACTORY
                    .createASN1ObjectIdentifier(mgfParams.getObjectAt(0))
                    .getId();
            if (!this.digestAlgorithmOid.equals(mgfParamDigestAlgoOid)) {
                throw new IllegalArgumentException(
                        MessageFormatUtil.format(
                                SignExceptionMessageConstant.DISGEST_ALGORITM_MGF_MISMATCH,
                         mgfParamDigestAlgoOid , this.digestAlgorithmOid));
            }
            try {
                int saltLength = params.getSaltLength().intValue();
                int trailerField = params.getTrailerField().intValue();
                SignUtils.setRSASSAPSSParamsWithMGF1(signature, getDigestAlgorithmName(), saltLength, trailerField);
            } catch (InvalidAlgorithmParameterException e) {
                throw new IllegalArgumentException(SignExceptionMessageConstant.INVALID_ARGUMENTS,e);
            }
        }
    }

    /**
     * Update the digest with the specified bytes.
     * This method is used both for signing and verifying
     *
     * @param buf the data buffer
     * @param off the offset in the data buffer
     * @param len the data length
     *
     * @throws SignatureException on error
     */
    public void update(byte[] buf, int off, int len) throws SignatureException {
        if (encapMessageContent != null || digestAttr != null || isTsp) {
            messageDigest.update(buf, off, len);
        } else {
            sig.update(buf, off, len);
        }
    }

    // adbe.x509.rsa_sha1 (PKCS#1)

    /**
     * Gets the bytes for the PKCS#1 object.
     *
     * @return a byte array
     */
    public byte[] getEncodedPKCS1() {
        try {
            if (externalSignatureValue != null) {
                signatureValue = externalSignatureValue;
            } else {
                signatureValue = sig.sign();
            }
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            IASN1OutputStream dout = BOUNCY_CASTLE_FACTORY.createASN1OutputStream(bOut);
            dout.writeObject(BOUNCY_CASTLE_FACTORY.createDEROctetString(signatureValue));
            dout.close();

            return bOut.toByteArray();
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    // other subfilters (PKCS#7)

    /**
     * Gets the bytes for the PKCS7SignedData object.
     *
     * @return the bytes for the PKCS7SignedData object
     */
    public byte[] getEncodedPKCS7() {
        return getEncodedPKCS7(null, PdfSigner.CryptoStandard.CMS, null, null, null);
    }

    /**
     * Gets the bytes for the PKCS7SignedData object. Optionally the authenticatedAttributes
     * in the signerInfo can also be set. If either of the parameters is <CODE>null</CODE>, none will be used.
     *
     * @param secondDigest the digest in the authenticatedAttributes
     *
     * @return the bytes for the PKCS7SignedData object
     */
    public byte[] getEncodedPKCS7(byte[] secondDigest) {
        return getEncodedPKCS7(secondDigest, PdfSigner.CryptoStandard.CMS, null, null, null);
    }

    /**
     * Gets the bytes for the PKCS7SignedData object. Optionally the authenticatedAttributes
     * in the signerInfo can also be set, and/or a time-stamp-authority client
     * may be provided.
     *
     * @param secondDigest the digest in the authenticatedAttributes
     * @param sigtype      specifies the PKCS7 standard flavor to which created PKCS7SignedData object will adhere:
     *                     either basic CMS or CAdES
     * @param tsaClient    TSAClient - null or an optional time stamp authority client
     * @param ocsp         collection of DER-encoded BasicOCSPResponses for the  certificate in the signature
     *                     certificates
     *                     chain, or null if OCSP revocation data is not to be added.
     * @param crlBytes     collection of DER-encoded CRL for certificates from the signature certificates chain,
     *                     or null if CRL revocation data is not to be added.
     *
     * @return byte[] the bytes for the PKCS7SignedData object
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6960#section-4.2.1">RFC 6960 § 4.2.1</a>
     */
    public byte[] getEncodedPKCS7(byte[] secondDigest, PdfSigner.CryptoStandard sigtype, ITSAClient tsaClient,
            Collection<byte[]> ocsp, Collection<byte[]> crlBytes) {
        try {
            if (externalSignatureValue != null) {
                signatureValue = externalSignatureValue;
                if (encapMessageContent != null) {
                    encapMessageContent = externalEncapMessageContent;
                }
            } else if (externalEncapMessageContent != null && encapMessageContent != null) {
                encapMessageContent = externalEncapMessageContent;
                sig.update(encapMessageContent);
                signatureValue = sig.sign();
            } else {
                if (encapMessageContent != null) {
                    encapMessageContent = messageDigest.digest();
                    sig.update(encapMessageContent);
                }
                signatureValue = sig.sign();
            }

            // Create the set of Hash algorithms
            IASN1EncodableVector digestAlgorithms = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            for (String element : digestalgos) {
                IASN1EncodableVector algos = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                algos.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(element));
                algos.add(BOUNCY_CASTLE_FACTORY.createDERNull());
                digestAlgorithms.add(BOUNCY_CASTLE_FACTORY.createDERSequence(algos));
            }

            // Create the contentInfo.
            IASN1EncodableVector v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.PKCS7_DATA));
            if (encapMessageContent != null) {
                v.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(0,
                        BOUNCY_CASTLE_FACTORY.createDEROctetString(encapMessageContent)));
            }
            IDERSequence contentinfo = BOUNCY_CASTLE_FACTORY.createDERSequence(v);

            // Get all the certificates
            v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            for (Object element : certs) {
                try (IASN1InputStream tempstream = BOUNCY_CASTLE_FACTORY.createASN1InputStream(
                        new ByteArrayInputStream(BOUNCY_CASTLE_FACTORY.createX509Certificate(element).getEncoded()))) {
                    v.add(tempstream.readObject());
                }
            }

            IDERSet dercertificates = BOUNCY_CASTLE_FACTORY.createDERSet(v);

            // Get the revocation info (crls field)
            IDERSet revInfoChoices = CertificateUtil.createRevocationInfoChoices(this.signedDataCrls,
                    this.signedDataOcsps, this.signedDataRevocationInfo);

            // Create signerInfo structure
            IASN1EncodableVector signerInfo = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();

            // Add the signerInfo version
            signerInfo.add(BOUNCY_CASTLE_FACTORY.createASN1Integer(signerversion));

            v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();

            v.add(CertificateInfo.getIssuer(signCert.getTBSCertificate()));
            v.add(BOUNCY_CASTLE_FACTORY.createASN1Integer(signCert.getSerialNumber()));
            signerInfo.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));

            // Add the digestAlgorithm
            v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(digestAlgorithmOid));
            v.add(BOUNCY_CASTLE_FACTORY.createDERNull());
            signerInfo.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));

            // add the authenticated attribute if present
            if (secondDigest != null) {
                signerInfo.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(false, 0,
                        getAuthenticatedAttributeSet(secondDigest, ocsp, crlBytes, sigtype)));
            }
            // Add the digestEncryptionAlgorithm
            v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(signatureMechanismOid));
            if (this.signatureMechanismParameters == null) {
                v.add(BOUNCY_CASTLE_FACTORY.createDERNull());
            } else {
                v.add(this.signatureMechanismParameters.toASN1Primitive());
            }
            signerInfo.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));

            // Add the digest
            signerInfo.add(BOUNCY_CASTLE_FACTORY.createDEROctetString(signatureValue));

            // When requested, go get and add the timestamp. May throw an exception.
            // Added by Martin Brunecky, 07/12/2007 folowing Aiken Sam, 2006-11-15
            // Sam found Adobe expects time-stamped SHA1-1 of the encrypted digest
            if (tsaClient != null) {
                byte[] tsImprint = tsaClient.getMessageDigest().digest(signatureValue);
                byte[] tsToken = tsaClient.getTimeStampToken(tsImprint);
                addTimestampTokenToUnsignedAttributes(tsToken);
            }
            if (unsignedAttributes.size() > 0) {
                signerInfo.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(
                        false, 1, BOUNCY_CASTLE_FACTORY.createDERSet(unsignedAttributes)));
            }

            // Finally build the body out of all the components above
            IASN1EncodableVector body = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            body.add(BOUNCY_CASTLE_FACTORY.createASN1Integer(version));
            body.add(BOUNCY_CASTLE_FACTORY.createDERSet(digestAlgorithms));
            body.add(contentinfo);
            body.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(false, 0, dercertificates));
            if (revInfoChoices != null) {
                body.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(false, 1, revInfoChoices));
            }

            // Only allow one signerInfo
            body.add(BOUNCY_CASTLE_FACTORY.createDERSet(BOUNCY_CASTLE_FACTORY.createDERSequence(signerInfo)));

            // Now we have the body, wrap it in it's PKCS7Signed shell
            // and return it
            //
            IASN1EncodableVector whole = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            whole.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.PKCS7_SIGNED_DATA));
            whole.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(0, BOUNCY_CASTLE_FACTORY.createDERSequence(body)));

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            IASN1OutputStream dout = BOUNCY_CASTLE_FACTORY.createASN1OutputStream(bOut);
            dout.writeObject(BOUNCY_CASTLE_FACTORY.createDERSequence(whole));
            dout.close();

            return bOut.toByteArray();
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Added by Aiken Sam, 2006-11-15, modifed by Martin Brunecky 07/12/2007
     * to start with the timeStampToken (signedData 1.2.840.113549.1.7.2).
     * Token is the TSA response without response status, which is usually
     * handled by the (vendor supplied) TSA request/response interface).
     *
     * @param timeStampToken byte[] - time stamp token, DER encoded signedData
     *
     * @throws IOException if an I/O error occurs.
     */
    private void addTimestampTokenToUnsignedAttributes(byte[] timeStampToken) throws IOException {
        if (timeStampToken == null) {
            return;
        }

        IASN1EncodableVector v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
        v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.AA_TIME_STAMP_TOKEN));
        try (IASN1InputStream tempstream =
                BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(timeStampToken))) {
            IASN1Sequence seq = BOUNCY_CASTLE_FACTORY.createASN1Sequence(tempstream.readObject());
            v.add(BOUNCY_CASTLE_FACTORY.createDERSet(seq));
        }

        unsignedAttributes.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));
    }

    // Authenticated attributes

    /**
     * When using authenticatedAttributes the authentication process is different.
     * The document digest is generated and put inside the attribute. The signing is done over the DER encoded
     * authenticatedAttributes. This method provides that encoding and the parameters must be
     * exactly the same as in {@link #getEncodedPKCS7(byte[])}.
     *
     * <p>
     * Note: do not pass in the full DER-encoded OCSPResponse object obtained from the responder,
     * only the DER-encoded IBasicOCSPResponse value contained in the response data.
     *
     * <p>
     * A simple example:
     * <pre>
     * Calendar cal = Calendar.getInstance();
     * PdfPKCS7 pk7 = new PdfPKCS7(key, chain, null, "SHA1", null, false);
     * MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
     * byte[] buf = new byte[8192];
     * int n;
     * InputStream inp = sap.getRangeStream();
     * while ((n = inp.read(buf)) &gt; 0) {
     *    messageDigest.update(buf, 0, n);
     * }
     * byte[] hash = messageDigest.digest();
     * byte[] sh = pk7.getAuthenticatedAttributeBytes(hash, cal);
     * pk7.update(sh, 0, sh.length);
     * byte[] sg = pk7.getEncodedPKCS7(hash, cal);
     * </pre>
     *
     * @param secondDigest the content digest
     * @param sigtype      specifies the PKCS7 standard flavor to which created PKCS7SignedData object will adhere:
     *                     either basic CMS or CAdES
     * @param ocsp         collection of DER-encoded BasicOCSPResponses for the  certificate in the signature
     *                     certificates
     *                     chain, or null if OCSP revocation data is not to be added.
     * @param crlBytes     collection of DER-encoded CRL for certificates from the signature certificates chain,
     *                     or null if CRL revocation data is not to be added.
     *
     * @return the byte array representation of the authenticatedAttributes ready to be signed
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6960#section-4.2.1">RFC 6960 § 4.2.1</a>
     */
    public byte[] getAuthenticatedAttributeBytes(byte[] secondDigest, PdfSigner.CryptoStandard sigtype,
            Collection<byte[]> ocsp, Collection<byte[]> crlBytes) {
        try {
            return getAuthenticatedAttributeSet(secondDigest, ocsp, crlBytes, sigtype)
                    .getEncoded(BOUNCY_CASTLE_FACTORY.createASN1Encoding().getDer());
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * This method provides that encoding and the parameters must be
     * exactly the same as in {@link #getEncodedPKCS7(byte[])}.
     *
     * @param secondDigest the content digest
     *
     * @return the byte array representation of the authenticatedAttributes ready to be signed
     */
    private IDERSet getAuthenticatedAttributeSet(byte[] secondDigest, Collection<byte[]> ocsp,
            Collection<byte[]> crlBytes, PdfSigner.CryptoStandard sigtype) {
        try {
            IASN1EncodableVector attribute = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            IASN1EncodableVector v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.CONTENT_TYPE));
            v.add(BOUNCY_CASTLE_FACTORY.createDERSet(
                    BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.PKCS7_DATA)));
            attribute.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));
            v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
            v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.MESSAGE_DIGEST));
            v.add(BOUNCY_CASTLE_FACTORY.createDERSet(BOUNCY_CASTLE_FACTORY.createDEROctetString(secondDigest)));
            attribute.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));
            boolean haveCrl = false;
            if (crlBytes != null) {
                for (byte[] bCrl : crlBytes) {
                    if (bCrl != null) {
                        haveCrl = true;
                        break;
                    }
                }
            }
            if (ocsp != null && !ocsp.isEmpty() || haveCrl) {
                v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.ADBE_REVOCATION));

                IASN1EncodableVector revocationV = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();

                if (haveCrl) {
                    IASN1EncodableVector v2 = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                    for (byte[] bCrl : crlBytes) {
                        if (bCrl == null) {
                            continue;
                        }
                        try (IASN1InputStream t =
                                BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(bCrl))) {
                            v2.add(t.readObject());
                        }
                    }
                    revocationV.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(
                            true, 0, BOUNCY_CASTLE_FACTORY.createDERSequence(v2)));
                }

                if (ocsp != null && !ocsp.isEmpty()) {
                    IASN1EncodableVector vo1 = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                    for (byte[] ocspBytes : ocsp) {
                        IDEROctetString doctet = BOUNCY_CASTLE_FACTORY.createDEROctetString(ocspBytes);
                        IASN1EncodableVector v2 = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                        IOCSPObjectIdentifiers objectIdentifiers = BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers();
                        v2.add(objectIdentifiers.getIdPkixOcspBasic());
                        v2.add(doctet);
                        IASN1Enumerated den = BOUNCY_CASTLE_FACTORY.createASN1Enumerated(0);
                        IASN1EncodableVector v3 = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                        v3.add(den);
                        v3.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(
                                true, 0, BOUNCY_CASTLE_FACTORY.createDERSequence(v2)));
                        vo1.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v3));
                    }
                    revocationV.add(BOUNCY_CASTLE_FACTORY.createDERTaggedObject(
                            true, 1, BOUNCY_CASTLE_FACTORY.createDERSequence(vo1)));
                }

                v.add(BOUNCY_CASTLE_FACTORY.createDERSet(BOUNCY_CASTLE_FACTORY.createDERSequence(revocationV)));
                attribute.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));
            }
            if (sigtype == PdfSigner.CryptoStandard.CADES) {
                v = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                v.add(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(OID.AA_SIGNING_CERTIFICATE_V2));

                IASN1EncodableVector aaV2 = BOUNCY_CASTLE_FACTORY.createASN1EncodableVector();
                if (!OID.SHA_256.equals(digestAlgorithmOid)) {
                    IAlgorithmIdentifier algoId = BOUNCY_CASTLE_FACTORY.createAlgorithmIdentifier(
                            BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(digestAlgorithmOid));
                    aaV2.add(algoId);
                }
                MessageDigest md = SignUtils.getMessageDigest(getDigestAlgorithmName(), interfaceDigest);
                byte[] dig = md.digest(signCert.getEncoded());
                aaV2.add(BOUNCY_CASTLE_FACTORY.createDEROctetString(dig));

                v.add(BOUNCY_CASTLE_FACTORY.createDERSet(BOUNCY_CASTLE_FACTORY.createDERSequence(
                        BOUNCY_CASTLE_FACTORY.createDERSequence(BOUNCY_CASTLE_FACTORY.createDERSequence(aaV2)))));
                attribute.add(BOUNCY_CASTLE_FACTORY.createDERSequence(v));
            }

            if (signaturePolicyIdentifier != null) {
                IPKCSObjectIdentifiers ipkcsObjectIdentifiers = BOUNCY_CASTLE_FACTORY.createPKCSObjectIdentifiers();
                IAttribute attr = BOUNCY_CASTLE_FACTORY.createAttribute(ipkcsObjectIdentifiers.getIdAaEtsSigPolicyId(),
                        BOUNCY_CASTLE_FACTORY.createDERSet(signaturePolicyIdentifier));
                attribute.add(attr);
            }

            return BOUNCY_CASTLE_FACTORY.createDERSet(attribute);
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /*
     *	DIGITAL SIGNATURE VERIFICATION
     */

    /**
     * Signature attributes
     */
    private byte[] sigAttr;
    /**
     * Signature attributes (maybe not necessary, but we use it as fallback)
     */
    private byte[] sigAttrDer;

    /**
     * encrypted digest
     */
    private MessageDigest encContDigest; // Stefan Santesson

    /**
     * Indicates if a signature has already been verified
     */
    private boolean verified;

    /**
     * The result of the verification
     */
    private boolean verifyResult;

    // verification

    /**
     * Verifies that signature integrity is intact (or in other words that signed data wasn't modified)
     * by checking that embedded data digest corresponds to the calculated one. Also ensures that signature
     * is genuine and is created by the owner of private key that corresponds to the declared public certificate.
     * <p>
     * Even though signature can be authentic and signed data integrity can be intact,
     * one shall also always check that signed data is not only a part of PDF contents but is actually a complete PDF
     * file.
     * In order to check that given signature covers the current {@link com.itextpdf.kernel.pdf.PdfDocument} please
     * use {@link SignatureUtil#signatureCoversWholeDocument(String)} method.
     *
     * @return <CODE>true</CODE> if the signature checks out, <CODE>false</CODE> otherwise
     *
     * @throws java.security.GeneralSecurityException if this signature object is not initialized properly,
     *                                                the passed-in signature is improperly encoded or of the wrong
     *                                                type, if this signature algorithm is unable to
     *                                                process the input data provided, if the public key is invalid or
     *                                                if security provider or signature algorithm
     *                                                are not recognized, etc.
     */
    public boolean verifySignatureIntegrityAndAuthenticity() throws GeneralSecurityException {
        if (verified) {
            return verifyResult;
        }
        if (sigAttr != null || sigAttrDer != null) {
            final byte[] msgDigestBytes = messageDigest.digest();
            boolean verifySignedMessageContent = true;
            // Stefan Santesson fixed a bug, keeping the code backward compatible
            boolean encContDigestCompare = false;
            if (encapMessageContent != null) {
                if (isTsp) {
                    byte[] tstInfo = new byte[0];
                    try {
                        tstInfo = timeStampTokenInfo.toASN1Primitive().getEncoded();
                    } catch (IOException e) {
                        // Ignore.
                    }
                    // Check that encapMessageContent is TSTInfo
                    boolean isTSTInfo = Arrays.equals(tstInfo, encapMessageContent);
                    IMessageImprint imprint = timeStampTokenInfo.getMessageImprint();
                    byte[] imphashed = imprint.getHashedMessage();
                    verifySignedMessageContent = isTSTInfo && Arrays.equals(msgDigestBytes, imphashed);
                } else {
                    verifySignedMessageContent = Arrays.equals(msgDigestBytes, encapMessageContent);
                }
                encContDigest.update(encapMessageContent);
                encContDigestCompare = Arrays.equals(encContDigest.digest(), digestAttr);
            }
            boolean absentEncContDigestCompare = Arrays.equals(msgDigestBytes, digestAttr);
            boolean concludingDigestCompare = absentEncContDigestCompare || encContDigestCompare;
            boolean sigVerify = verifySigAttributes(sigAttr) || verifySigAttributes(sigAttrDer);
            verifyResult = concludingDigestCompare && sigVerify && verifySignedMessageContent;
        } else {
            if (encapMessageContent != null) {
                SignUtils.updateVerifier(sig, messageDigest.digest());
            }
            verifyResult = sig.verify(signatureValue);
        }
        verified = true;
        return verifyResult;
    }

    private boolean verifySigAttributes(byte[] attr) throws GeneralSecurityException {
        Signature signature = initSignature(signCert.getPublicKey());
        SignUtils.updateVerifier(signature, attr);
        return signature.verify(signatureValue);
    }

    /**
     * Checks if the timestamp refers to this document.
     *
     * @return true if it checks false otherwise
     *
     * @throws GeneralSecurityException on error
     */
    public boolean verifyTimestampImprint() throws GeneralSecurityException {
        if (timeStampTokenInfo == null) {
            return false;
        }
        IMessageImprint imprint = timeStampTokenInfo.getMessageImprint();
        String algOID = imprint.getHashAlgorithm().getAlgorithm().getId();
        byte[] md = SignUtils.getMessageDigest(DigestAlgorithms.getDigest(algOID)).digest(signatureValue);
        byte[] imphashed = imprint.getHashedMessage();
        return Arrays.equals(md, imphashed);
    }

    // Certificates

    /**
     * All the X.509 certificates in no particular order.
     */
    private final Collection<Certificate> certs;

    private Collection<Certificate> timestampCerts;

    /**
     * All the X.509 certificates used for the main signature.
     */
    Collection<Certificate> signCerts;

    /**
     * The X.509 certificate that is used to sign the digest.
     */
    private X509Certificate signCert;

    /**
     * Get all the X.509 certificates associated with this PKCS#7 object in no particular order.
     * Other certificates, from OCSP for example, will also be included.
     *
     * @return the X.509 certificates associated with this PKCS#7 object
     */
    public Certificate[] getCertificates() {
        return certs.toArray(new Certificate[0]);
    }

    /**
     * Get all X.509 certificates associated with this PKCS#7 object timestamp in no particular order.
     *
     * @return {@link Certificate[]} array
     */
    public Certificate[] getTimestampCertificates() {
        return timestampCerts.toArray(new Certificate[0]);
    }

    /**
     * Get the X.509 sign certificate chain associated with this PKCS#7 object.
     * Only the certificates used for the main signature will be returned, with
     * the signing certificate first.
     *
     * @return the X.509 certificates associated with this PKCS#7 object
     */
    public Certificate[] getSignCertificateChain() {
        return signCerts.toArray(new Certificate[0]);
    }

    /**
     * Get the X.509 certificate actually used to sign the digest.
     *
     * @return the X.509 certificate actually used to sign the digest
     */
    public X509Certificate getSigningCertificate() {
        return signCert;
    }

    /**
     * Helper method that creates the collection of certificates
     * used for the main signature based on the complete list
     * of certificates and the sign certificate.
     */
    private void signCertificateChain() {
        List<Certificate> cc = new ArrayList<>();
        cc.add(signCert);
        List<Certificate> oc = new ArrayList<>(certs);
        for (int k = 0; k < oc.size(); ++k) {
            if (signCert.equals(oc.get(k))) {
                oc.remove(k);
                --k;
            }
        }
        boolean found = true;
        while (found) {
            X509Certificate v = (X509Certificate) cc.get(cc.size() - 1);
            found = false;
            for (int k = 0; k < oc.size(); ++k) {
                X509Certificate issuer = (X509Certificate) oc.get(k);
                if (SignUtils.verifyCertificateSignature(v, issuer.getPublicKey(), provider)) {
                    found = true;
                    cc.add(oc.get(k));
                    oc.remove(k);
                    break;
                }
            }
        }
        signCerts = cc;
    }

    // Certificate Revocation Lists

    // Stored in the SignerInfo.
    private Collection<CRL> crls;

    // Stored in crls field of th SignedData.
    private final Collection<CRL> signedDataCrls = new ArrayList<>();

    /**
     * Get the X.509 certificate revocation lists associated with this PKCS#7 object (stored in Signer Info).
     *
     * @return the X.509 certificate revocation lists associated with this PKCS#7 object.
     */
    public Collection<CRL> getCRLs() {
        return crls;
    }

    /**
     * Get the X.509 certificate revocation lists associated with this PKCS#7 Signed Data object.
     *
     * @return the X.509 certificate revocation lists associated with this PKCS#7 Signed Data object.
     */
    public Collection<CRL> getSignedDataCRLs() {
        return signedDataCrls;
    }

    /**
     * Helper method that tries to construct the CRLs.
     */
    void findCRL(IASN1Sequence seq) {
        try {
            crls = new ArrayList<>();
            for (int k = 0; k < seq.size(); ++k) {
                ByteArrayInputStream ar = new ByteArrayInputStream(seq.getObjectAt(k).toASN1Primitive()
                        .getEncoded(BOUNCY_CASTLE_FACTORY.createASN1Encoding().getDer()));
                X509CRL crl = (X509CRL) SignUtils.parseCrlFromStream(ar);
                crls.add(crl);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    // Online Certificate Status Protocol

    /**
     * BouncyCastle IBasicOCSPResponse
     */
    IBasicOCSPResponse basicResp;

    private final Collection<IBasicOCSPResponse> signedDataOcsps = new ArrayList<>();

    /**
     * Gets the OCSP basic response collection retrieved from SignedData structure.
     *
     * @return the OCSP basic response collection.
     */
    public Collection<IBasicOCSPResponse> getSignedDataOcsps() {
        return signedDataOcsps;
    }

    /**
     * Gets the OCSP basic response from the SignerInfo if there is one.
     *
     * @return the OCSP basic response or null.
     */
    public IBasicOCSPResponse getOcsp() {
        return basicResp;
    }

    /**
     * Checks if OCSP revocation refers to the document signing certificate.
     *
     * @return true if it checks, false otherwise
     */
    public boolean isRevocationValid() {
        if (basicResp == null) {
            return false;
        }
        if (signCerts.size() < 2) {
            return false;
        }
        try {
            Certificate[] cs = getSignCertificateChain();
            ISingleResp sr = BOUNCY_CASTLE_FACTORY.createSingleResp(basicResp);
            ICertificateID cid = sr.getCertID();
            X509Certificate sigcer = getSigningCertificate();
            X509Certificate isscer = (X509Certificate) cs[1];
            ICertificateID tis = SignUtils.generateCertificateId(isscer, sigcer.getSerialNumber(), cid.getHashAlgOID());
            return tis.equals(cid);
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Helper method that creates the IBasicOCSPResp object.
     *
     * @param seq {@link IASN1Sequence} wrapper
     *
     * @throws IOException if some I/O error occurred.
     */
    private void findOcsp(IASN1Sequence seq) throws IOException {
        basicResp = null;
        boolean ret;
        while (true) {
            IASN1ObjectIdentifier objectIdentifier = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                    seq.getObjectAt(0));
            IOCSPObjectIdentifiers ocspObjectIdentifiers = BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers();
            if (objectIdentifier != null
                    && objectIdentifier.getId().equals(ocspObjectIdentifiers.getIdPkixOcspBasic().getId())) {
                break;
            }
            ret = true;
            for (int k = 0; k < seq.size(); ++k) {
                IASN1Sequence nextSeq = BOUNCY_CASTLE_FACTORY.createASN1Sequence(seq.getObjectAt(k));
                if (nextSeq != null) {
                    seq = nextSeq;
                    ret = false;
                    break;
                }
                IASN1TaggedObject tag = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(seq.getObjectAt(k));
                if (tag != null) {
                    nextSeq = BOUNCY_CASTLE_FACTORY.createASN1Sequence(tag.getObject());
                    if (nextSeq != null) {
                        seq = nextSeq;
                        ret = false;
                        break;
                    } else {
                        return;
                    }
                }
            }
            if (ret) {
                return;
            }
        }
        IASN1OctetString os = BOUNCY_CASTLE_FACTORY.createASN1OctetString(seq.getObjectAt(1));
        try (IASN1InputStream inp = BOUNCY_CASTLE_FACTORY.createASN1InputStream(os.getOctets())) {
            basicResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(inp.readObject());
        }
    }

    // Time Stamps

    /**
     * True if there's a PAdES LTV time stamp.
     */
    private boolean isTsp;

    /**
     * True if it's a CAdES signature type.
     */
    private boolean isCades;

    /**
     * Inner timestamp signature container.
     */
    private PdfPKCS7 timestampSignatureContainer;

    /**
     * BouncyCastle TSTInfo.
     */
    private ITSTInfo timeStampTokenInfo;
    /**
     * Check if it's a PAdES-LTV time stamp.
     *
     * @return true if it's a PAdES-LTV time stamp, false otherwise
     */
    public boolean isTsp() {
        return isTsp;
    }

    /**
     * Retrieves inner timestamp signature container if there is one.
     *
     * @return timestamp signature container or null.
     */
    public PdfPKCS7 getTimestampSignatureContainer() {
        return timestampSignatureContainer;
    }

    /**
     * Gets the timestamp token info if there is one.
     *
     * @return the timestamp token info or null
     */
    public ITSTInfo getTimeStampTokenInfo() {
        return timeStampTokenInfo;
    }

    /**
     * Gets the timestamp date.
     *
     * <p>
     * In case the signed document doesn't contain timestamp,
     * {@link TimestampConstants#UNDEFINED_TIMESTAMP_DATE} will be returned.
     *
     * @return the timestamp date
     */
    public Calendar getTimeStampDate() {
        if (timeStampTokenInfo == null) {
            return (Calendar) TimestampConstants.UNDEFINED_TIMESTAMP_DATE;
        }
        return SignUtils.getTimeStampDate(timeStampTokenInfo);
    }

    /**
     * Getter for the filter subtype.
     *
     * @return the filter subtype
     */
    public PdfName getFilterSubtype() {
        return filterSubtype;
    }
}
