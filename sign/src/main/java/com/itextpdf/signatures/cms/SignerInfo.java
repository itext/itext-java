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
package com.itextpdf.signatures.cms;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.CertificateInfo;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * This class represents the SignerInfo structure from
 * <a href="https://datatracker.ietf.org/doc/html/rfc5652#section-5.3">rfc5652   Cryptographic Message Syntax (CMS)</a>
 */
public class SignerInfo {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final int DEFAULT_SIGNATURE_SIZE = 1024;

    private AlgorithmIdentifier digestAlgorithm;
    private AlgorithmIdentifier signingAlgorithm;
    private final Collection<CmsAttribute> signedAttributes = new ArrayList<>();
    private final Collection<CmsAttribute> unSignedAttributes;
    private byte[] serializedSignedAttributes;
    private Collection<byte[]> ocspResponses;
    private Collection<byte[]> crlResponses;
    private byte[] signatureData;
    private boolean signedAttributesReadOnly;
    private X509Certificate signerCertificate;

    /**
     * Creates an empty SignerInfo structure.
     */
    public SignerInfo() {
        CmsAttribute contentType =
                new CmsAttribute(OID.CONTENT_TYPE,
                        BC_FACTORY.createDERSet(BC_FACTORY.createASN1ObjectIdentifier(OID.PKCS7_DATA)));
        signedAttributes.add(contentType);
        unSignedAttributes = new ArrayList<>();
    }

    /**
     * Creates a SignerInfo structure from an ASN1 structure.
     *
     * @param signerInfoStructure the ASN1 structure containing signerInfo
     * @param certificates        the certificates of the CMS, it should contain the signing certificate
     *
     * @throws IOException if issues occur during ASN1 objects creation.
     */
    public SignerInfo(IASN1Encodable signerInfoStructure, Collection<X509Certificate> certificates) throws IOException {
        int index = 0;
        try {
            IASN1Sequence signerInfoSeq = BC_FACTORY.createASN1Sequence(signerInfoStructure);
            IASN1Integer version = BC_FACTORY.createASN1Integer(signerInfoSeq.getObjectAt(index++));
            if (version.getValue().intValue() == 1) {
                processIssuerAndSerialNumberSignerCertificate(signerInfoSeq.getObjectAt(index++), certificates);
            } else {
                processSubjectKeyIdentifierSignerCertificate(signerInfoSeq.getObjectAt(index++), certificates);
            }
            digestAlgorithm = new AlgorithmIdentifier(signerInfoSeq.getObjectAt(index++));
            IASN1TaggedObject taggedSingedAttributes =
                    BC_FACTORY.createASN1TaggedObject(signerInfoSeq.getObjectAt(index));
            if (taggedSingedAttributes != null) {
                index++;
                setSerializedSignedAttributes(BC_FACTORY.createASN1Set(taggedSingedAttributes, false)
                        .getEncoded(BC_FACTORY.createASN1Encoding().getDer()));
            }
            signingAlgorithm = new AlgorithmIdentifier(signerInfoSeq.getObjectAt(index++));

            IDEROctetString signatureDataOS = BC_FACTORY.createDEROctetString(signerInfoSeq.getObjectAt(index++));
            if (signatureDataOS != null) {
                signatureData = signatureDataOS.getOctets();
            }

            if (signerInfoSeq.size() > index) {
                IASN1TaggedObject taggedUnsingedAttributes =
                        BC_FACTORY.createASN1TaggedObject(signerInfoSeq.getObjectAt(index));
                unSignedAttributes = processAttributeSet(BC_FACTORY.createASN1Set(taggedUnsingedAttributes, false));
            } else {
                unSignedAttributes = new ArrayList<>();
            }
        } catch (NullPointerException npe) {
            throw new PdfException(SignExceptionMessageConstant.CMS_INVALID_CONTAINER_STRUCTURE, npe);
        }
    }

    /**
     * Returns the algorithmId to create the digest of the data to sign.
     *
     * @return the OID of the digest algorithm.
     */
    public AlgorithmIdentifier getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * Sets the algorithmId to create the digest of the data to sign.
     *
     * @param algorithmId the OID of the algorithm
     */
    public void setDigestAlgorithm(AlgorithmIdentifier algorithmId) {
        digestAlgorithm = algorithmId;
    }

    /**
     * Adds or replaces the message digest signed attribute.
     *
     * @param digest ASN.1 type MessageDigest
     */
    public void setMessageDigest(byte[] digest) {
        if (signedAttributesReadOnly) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_READONLY);
        }
        CmsAttribute digestAttribute = new CmsAttribute(OID.MESSAGE_DIGEST, BC_FACTORY.createDERSet(
                                BC_FACTORY.createDEROctetString(digest)));
        signedAttributes.add(digestAttribute);
    }

    /**
     * Sets the certificate that is used to sign.
     *
     * @param certificate the certificate that is used to sign
     * @throws CertificateEncodingException if an encoding error occurs.
     */
    public void setSigningCertificate(X509Certificate certificate) throws CertificateEncodingException {
        this.signerCertificate = certificate;

        ITBSCertificate tbsCert = BC_FACTORY.createTBSCertificate(certificate.getTBSCertificate());
        if (signingAlgorithm != null) {
            return;
        }
        if (tbsCert.getSubjectPublicKeyInfo().getAlgorithm().getParameters() != null) {
            if (tbsCert.getSubjectPublicKeyInfo().getAlgorithm().getParameters().isNull()) {
                this.signingAlgorithm = new AlgorithmIdentifier(
                        tbsCert.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm().getId(),
                        BC_FACTORY.createDERNull());
                return;
            }
            this.signingAlgorithm = new AlgorithmIdentifier(
                    tbsCert.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm().getId(),
                    tbsCert.getSubjectPublicKeyInfo().getAlgorithm().getParameters().toASN1Primitive());
            return;
        }
        this.signingAlgorithm = new AlgorithmIdentifier(
                tbsCert.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm().getId());
    }

    /**
     * Gets the certificate that is used to sign.
     *
     * @return the certificate that is used to sign.
     */
    public X509Certificate getSigningCertificate() {
        return signerCertificate;
    }

    /**
     * Gets the signature data.
     *
     * @return the signature data.
     */
    public byte[] getSignatureData() {
        return signatureData;
    }

    /**
     * Sets the certificate that is used to sign a document and adds it to the signed attributes.
     *
     * @param certificate        the certificate that is used to sign
     * @param digestAlgorithmOid the oid of the digest algorithm to be added to the signed attributes
     *
     * @throws CertificateEncodingException if an encoding error occurs.
     * @throws NoSuchAlgorithmException     when the algorithm is unknown.
     * @throws NoSuchProviderException      when provider is unknown.
     */
    public void setSigningCertificateAndAddToSignedAttributes(X509Certificate certificate, String digestAlgorithmOid)
            throws CertificateEncodingException, NoSuchAlgorithmException, NoSuchProviderException {
        setSigningCertificate(certificate);
        addSignerCertificateToSignedAttributes(certificate, digestAlgorithmOid);
    }

    /**
     * Adds a set of OCSP responses as signed attributes.
     *
     * @param ocspResponses a set of binary representations of OCSP responses.
     */
    public void setOcspResponses(Collection<byte[]> ocspResponses) {
        if (signedAttributesReadOnly) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_READONLY);
        }
        this.ocspResponses = Collections.unmodifiableCollection(ocspResponses);
        setRevocationInfo();
    }

    /**
     * Adds a set of CRL responses as signed attributes.
     *
     * @param crlResponses a set of binary representations of CRL responses.
     */
    public void setCrlResponses(Collection<byte[]> crlResponses) {
        if (signedAttributesReadOnly) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_READONLY);
        }
        this.crlResponses = Collections.unmodifiableCollection(crlResponses);
        setRevocationInfo();
    }

    /**
     * Adds the signer certificate to the signed attributes as a SigningCertificateV2 structure.
     *
     * @param cert               the certificate to add
     * @param digestAlgorithmOid the digest algorithm oid that will be used
     *
     * @throws NoSuchAlgorithmException     when the algorithm is unknown.
     * @throws NoSuchProviderException      when the security provider is not known.
     * @throws CertificateEncodingException when there was a problem parsing th certificate.
     */
    public void addSignerCertificateToSignedAttributes(X509Certificate cert, String digestAlgorithmOid)
            throws NoSuchAlgorithmException, NoSuchProviderException, CertificateEncodingException {
        if (signedAttributesReadOnly) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_READONLY);
        }
        MessageDigest md = DigestAlgorithms.getMessageDigestFromOid(digestAlgorithmOid,
                BC_FACTORY.getProviderName());
        IASN1EncodableVector certContents = BC_FACTORY.createASN1EncodableVector();
        // don't add if it is the default value
        if (!OID.SHA_256.equals(digestAlgorithmOid)) {
            IAlgorithmIdentifier algoId = BC_FACTORY.createAlgorithmIdentifier(
                    BC_FACTORY.createASN1ObjectIdentifier(digestAlgorithmOid));
            certContents.add(algoId);
        }
        byte[] dig = md.digest(cert.getEncoded());
        certContents.add(BC_FACTORY.createDEROctetString(dig));
        IASN1Sequence issuerName = BC_FACTORY.createASN1Sequence(
                CertificateInfo.getIssuer(cert.getTBSCertificate()));
        IDERTaggedObject issuerTagged = BC_FACTORY.createDERTaggedObject(true, 4, issuerName);
        IDERSequence issuer = BC_FACTORY.createDERSequence(issuerTagged);
        IASN1Integer serial = BC_FACTORY.createASN1Integer(cert.getSerialNumber());
        IASN1EncodableVector v = BC_FACTORY.createASN1EncodableVector();
        v.add(issuer);
        v.add(serial);
        IDERSequence issuerS = BC_FACTORY.createDERSequence(v);
        certContents.add(issuerS);
        IDERSequence certContentsSeq = BC_FACTORY.createDERSequence(certContents);
        IDERSequence certContentsSeqSeq = BC_FACTORY.createDERSequence(certContentsSeq);
        IDERSequence certContentsSeqSeqSeq = BC_FACTORY.createDERSequence(certContentsSeqSeq);
        IDERSet certContentsSeqSeqSeqSet = BC_FACTORY.createDERSet(certContentsSeqSeqSeq);
        CmsAttribute attribute = new CmsAttribute(OID.AA_SIGNING_CERTIFICATE_V2, certContentsSeqSeqSeqSet);

        signedAttributes.add(attribute);
    }

    /**
     * Sets the actual signature.
     *
     * @param signatureData a byte array containing the signature
     */
    public void setSignature(byte[] signatureData) {
        this.signatureData = Arrays.copyOf(signatureData, signatureData.length);
    }

    /**
     * Optional.
     * Sets the OID and parameters of the algorithm that will be used to create the signature.
     * This will be overwritten when setting the signing certificate.
     *
     * @param algorithm The OID and parameters of the algorithm that will be used to create the signature.
     */
    public void setSignatureAlgorithm(AlgorithmIdentifier algorithm) {
        this.signingAlgorithm = algorithm;
    }

    /**
     * Value 0 when no signerIdentifier is available.
     * Value 1 when signerIdentifier is of type issuerAndSerialNumber.
     * Value 3 when signerIdentifier is of type subjectKeyIdentifier.
     *
     * @return CMS version.
     */
    public int getCmsVersion() {
        return 1;
    }

    /**
     * Optional.
     *
     * <p>
     * Attributes that should be part of the signed content
     * optional, but it MUST be present if the content type of
     * the EncapsulatedContentInfo value being signed is not id-data.
     * In that case it must at least contain the following two attributes:
     *
     * <p>
     * A content-type attribute having as its value the content type
     * of the EncapsulatedContentInfo value being signed.  Section
     * 11.1 defines the content-type attribute.  However, the
     * content-type attribute MUST NOT be used as part of a
     * countersignature unsigned attribute as defined in Section 11.4.
     *
     * <p>
     * A message-digest attribute, having as its value the message
     * digest of the content.  Section 11.2 defines the message-digest
     * attribute.
     *
     * @return collection of the signed attributes.
     */
    public Collection<CmsAttribute> getSignedAttributes() {
        return Collections.unmodifiableCollection(signedAttributes);
    }

    /**
     * Adds a new attribute to the signed attributes.
     * This become readonly after retrieving the serialized version {@link SignerInfo#serializeSignedAttributes()}.
     *
     * @param attribute the attribute to add
     */
    public void addSignedAttribute(CmsAttribute attribute) {
        if (signedAttributesReadOnly) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_READONLY);
        }
        signedAttributes.add(attribute);
    }

    /**
     * Retrieves the optional unsigned attributes.
     *
     * @return the optional unsigned attributes.
     */
    public Collection<CmsAttribute> getUnSignedAttributes() {
        return Collections.unmodifiableCollection(unSignedAttributes);
    }

    /**
     * Optional.
     *
     * <p>
     * Adds attribute that should not or can not be part of the signed content.
     *
     * @param attribute the attribute to add
     */
    public void addUnSignedAttribute(CmsAttribute attribute) {
        unSignedAttributes.add(attribute);
    }

    /**
     * Removes unsigned attribute from signer info object based on attribute type.
     *
     * @param type {@link String} attribute type
     */
    public void removeUnSignedAttribute(String type) {
        unSignedAttributes.removeIf(cmsAttribute -> cmsAttribute.getType().equals(type));
    }

    /**
     * Retrieves the encoded signed attributes of the signer info.
     * This makes the signed attributes read only.
     *
     * @return the encoded signed attributes of the signer info.
     *
     * @throws IOException if issues occur during ASN1 objects creation.
     */
    public byte[] serializeSignedAttributes() throws IOException {
        if (!signedAttributesReadOnly) {
            IDERSet derView = getAttributesAsDERSet(signedAttributes);
            serializedSignedAttributes = derView.getEncoded(BC_FACTORY.createASN1Encoding().getDer());
            signedAttributesReadOnly = true;
        }
        return Arrays.copyOf(serializedSignedAttributes, serializedSignedAttributes.length);
    }

    /**
     * Sets the signed attributes from a serialized version.
     * This makes the signed attributes read only.
     *
     * @param serializedSignedAttributes the encoded signed attributes.
     */
    public final void setSerializedSignedAttributes(byte[] serializedSignedAttributes) {
        if (signedAttributesReadOnly) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_READONLY);
        }
        this.signedAttributesReadOnly = true;
        this.serializedSignedAttributes = Arrays.copyOf(serializedSignedAttributes, serializedSignedAttributes.length);
        try {
            signedAttributes.clear();
            this.signedAttributes.addAll(
                    processAttributeSet(BC_FACTORY.createASN1Primitive(serializedSignedAttributes)));
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Calculates an estimate size for the SignerInfo structure.
     * This takes into account the values added including the signature, but does not account for unset items like
     * a timestamp response added after actual signing.
     *
     * @return the estimated size of the structure.
     *
     * @throws IOException                  if issues occur during ASN1 objects creation.
     * @throws CertificateEncodingException if issues occur during processing of certificates.
     */
    public long getEstimatedSize() throws IOException, CertificateEncodingException {
        IDERSequence derView = getAsDerSequence(true);
        byte[] temp = derView.getEncoded(BC_FACTORY.createASN1Encoding().getDer());
        return temp.length;
    }

    /**
     * Serializes the SignerInfo structure and makes the signed attributes readonly.
     *
     * @return the encoded SignerInfo structure.
     *
     * @throws CertificateEncodingException if issues occur during processing of certificates.
     */
    public IDERSequence getAsDerSequence() throws CertificateEncodingException {
        return getAsDerSequence(false);
    }

    /**
     * Serializes the SignerInfo structure and makes the signed attributes readonly.
     * With the possibility to skip making the signed attributes read only for estimation purposes.
     *
     * @param estimationRun set to true to not make signed attributes read only
     *
     * @return the encoded SignerInfo structure.
     *
     * @throws CertificateEncodingException if issues occur during processing of certificates.
     */
    IDERSequence getAsDerSequence(boolean estimationRun) throws CertificateEncodingException {
        IASN1EncodableVector signerInfoV = BC_FACTORY.createASN1EncodableVector();
        // version
        signerInfoV.add(BC_FACTORY.createASN1Integer(getCmsVersion()));
        // sid
        IASN1EncodableVector issuerAndSerialNumberV = BC_FACTORY.createASN1EncodableVector();
        if (signerCertificate != null) {
            issuerAndSerialNumberV.add(CertificateInfo.getIssuer(signerCertificate.getTBSCertificate()));
            issuerAndSerialNumberV.add(BC_FACTORY.createASN1Integer(signerCertificate.getSerialNumber()));
        }
        signerInfoV.add(BC_FACTORY.createDERSequence(issuerAndSerialNumberV));
        // digest algorithm
        IASN1EncodableVector digestalgorithmV = BC_FACTORY.createASN1EncodableVector();

        digestalgorithmV.add(BC_FACTORY.createASN1ObjectIdentifier(this.digestAlgorithm.getAlgorithmOid()));
        digestalgorithmV.addOptional(digestAlgorithm.getParameters());

        signerInfoV.add(BC_FACTORY.createDERSequence(digestalgorithmV));
        // signed attributes
        if (!signedAttributes.isEmpty() || signedAttributesReadOnly) {
            if (estimationRun || !signedAttributesReadOnly) {
                signerInfoV.add(BC_FACTORY.createDERTaggedObject(false, 0, getAttributesAsDERSet(signedAttributes)));
            } else {
                try (IASN1InputStream saIS = BC_FACTORY.createASN1InputStream(serializedSignedAttributes)) {
                    signerInfoV.add(BC_FACTORY.createDERTaggedObject(false, 0, saIS.readObject()));
                } catch (IOException e) {
                    throw new PdfException(e);
                }
            }
        }
        // signatureAlgorithm
        if (signingAlgorithm != null) {
            IASN1EncodableVector signatureAlgorithmV = BC_FACTORY.createASN1EncodableVector();
            signatureAlgorithmV.add(BC_FACTORY.createASN1ObjectIdentifier(signingAlgorithm.getAlgorithmOid()));
            signatureAlgorithmV.addOptional(signingAlgorithm.getParameters());
            signerInfoV.add(BC_FACTORY.createDERSequence(signatureAlgorithmV));
        }
        // signatureValue
        byte[] workingSignatureData;
        if (signatureData == null) {
            workingSignatureData = new byte[DEFAULT_SIGNATURE_SIZE];
        } else {
            workingSignatureData = signatureData;
        }
        IASN1OctetString signatureDataOS = BC_FACTORY.createDEROctetString(workingSignatureData);
        signerInfoV.add(signatureDataOS);
        // UnsignedAttributes
        if (!unSignedAttributes.isEmpty()) {
            signerInfoV.add(BC_FACTORY.createDERTaggedObject(false, 1, getAttributesAsDERSet(unSignedAttributes)));
        }

        return BC_FACTORY.createDERSequence(signerInfoV);
    }

    private void processSubjectKeyIdentifierSignerCertificate(IASN1Encodable asnStruct,
                                                              Collection<X509Certificate> certificates)
            throws IOException {
        IASN1OctetString subjectKeyIdentifierOs = BC_FACTORY.createASN1OctetString(
                BC_FACTORY.createASN1TaggedObject(asnStruct).getObject());

        try (IASN1InputStream aIn = BC_FACTORY.createASN1InputStream(
                new ByteArrayInputStream(subjectKeyIdentifierOs.getOctets()))) {
            IASN1Primitive subjectKeyIdentifier = aIn.readObject();

            for (X509Certificate certificate : certificates) {
                IASN1Primitive ski = CertificateUtil.getExtensionValue(certificate,
                        OID.X509Extensions.SUBJECT_KEY_IDENTIFIER);
                if (ski.equals(subjectKeyIdentifier)) {
                    this.signerCertificate = certificate;
                    return;
                }
            }
        }
        throw new PdfException(SignExceptionMessageConstant.CMS_CERTIFICATE_NOT_FOUND);
    }

    private void processIssuerAndSerialNumberSignerCertificate(IASN1Encodable asnStruct,
                                                               Collection<X509Certificate> certificates) {
        IASN1Sequence signIdSeq = BC_FACTORY.createASN1Sequence(asnStruct);
        IASN1Integer serial = BC_FACTORY.createASN1Integer(signIdSeq.getObjectAt(1));
        for (X509Certificate certificate : certificates) {
            if (certificate.getSerialNumber().equals(serial.getValue())) {
                this.signerCertificate = certificate;
                break;
            }
        }
        if (signerCertificate == null) {
            throw new PdfException(SignExceptionMessageConstant.CMS_CERTIFICATE_NOT_FOUND);
        }
    }

    private static Collection<CmsAttribute> processAttributeSet(IASN1Encodable asnStruct) {
        IASN1Set usaSet = BC_FACTORY.createASN1Set(asnStruct);
        Collection<CmsAttribute> attributes = new ArrayList<>(usaSet.size());
        for (int i = 0; i < usaSet.size(); i++) {
            IASN1Sequence attrSeq = BC_FACTORY.createASN1Sequence(usaSet.getObjectAt(i));
            IASN1ObjectIdentifier attrType = BC_FACTORY.createASN1ObjectIdentifier(attrSeq.getObjectAt(0));
            IASN1Primitive attrVal = BC_FACTORY.createASN1Primitive(attrSeq.getObjectAt(1));
            attributes.add(new CmsAttribute(attrType.getId(), attrVal));
        }
        return attributes;
    }

    private void setRevocationInfo() {
        signedAttributes.removeIf(a -> OID.ADBE_REVOCATION.equals(a.getType()));

        if (containsRevocationData()) {

            IASN1EncodableVector revocationV = BC_FACTORY.createASN1EncodableVector();

            createCRLStructure(revocationV);
            createOCPSStructure(revocationV);

            CmsAttribute digestAttribute =
                    new CmsAttribute(OID.ADBE_REVOCATION,
                            BC_FACTORY.createDERSequence(revocationV));
            signedAttributes.add(digestAttribute);
        }
    }

    private void createCRLStructure(IASN1EncodableVector revocationV) {
        if (crlResponses != null && !crlResponses.isEmpty()) {
            IASN1EncodableVector v2 = BC_FACTORY.createASN1EncodableVector();
            for (byte[] bCrl : crlResponses) {
                if (bCrl == null) {
                    continue;
                }
                try (IASN1InputStream t =
                             BC_FACTORY.createASN1InputStream(new ByteArrayInputStream(bCrl))) {
                    v2.add(t.readObject());
                } catch (IOException e) {
                    throw new PdfException(e);
                }
            }
            revocationV.add(BC_FACTORY.createDERTaggedObject(
                    true, 0, BC_FACTORY.createDERSequence(v2)));
        }
    }

    private void createOCPSStructure(IASN1EncodableVector revocationV) {
        if (ocspResponses != null && !ocspResponses.isEmpty()) {
            IASN1EncodableVector vo1 = BC_FACTORY.createASN1EncodableVector();
            for (byte[] ocspBytes : ocspResponses) {
                IDEROctetString doctet = BC_FACTORY.createDEROctetString(ocspBytes);
                IASN1EncodableVector v2 = BC_FACTORY.createASN1EncodableVector();
                IOCSPObjectIdentifiers objectIdentifiers = BC_FACTORY.createOCSPObjectIdentifiers();
                v2.add(objectIdentifiers.getIdPkixOcspBasic());
                v2.add(doctet);
                IASN1Enumerated den = BC_FACTORY.createASN1Enumerated(0);
                IASN1EncodableVector v3 = BC_FACTORY.createASN1EncodableVector();
                v3.add(den);
                v3.add(BC_FACTORY.createDERTaggedObject(
                        true, 0, BC_FACTORY.createDERSequence(v2)));
                vo1.add(BC_FACTORY.createDERSequence(v3));
            }
            revocationV.add(BC_FACTORY.createDERTaggedObject(
                    true, 1, BC_FACTORY.createDERSequence(vo1)));
        }
    }

    private boolean containsRevocationData() {
        return (ocspResponses != null && !ocspResponses.isEmpty()) ||
                (crlResponses != null && !crlResponses.isEmpty());
    }

    private static IDERSet getAttributesAsDERSet(Collection<CmsAttribute> attributeSet) {
        IASN1EncodableVector attributes = BC_FACTORY.createASN1EncodableVector();
        for (CmsAttribute attr : attributeSet) {
            IASN1EncodableVector v = BC_FACTORY.createASN1EncodableVector();
            v.add(BC_FACTORY.createASN1ObjectIdentifier(attr.getType()));
            v.add(attr.getValue());
            attributes.add(BC_FACTORY.createDERSequence(v));
        }
        return BC_FACTORY.createDERSet(attributes);
    }
}
