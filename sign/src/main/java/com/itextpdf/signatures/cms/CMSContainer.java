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
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * The CMS container which represents SignedData structure from
 * <a href="https://datatracker.ietf.org/doc/html/rfc5652#section-5.1">rfc5652 Cryptographic Message Syntax (CMS)</a>
 */
public class CMSContainer {

    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * Collection to store revocation info other than OCSP and CRL responses, e.g. SCVP Request and Response.
     */
    final Collection<IASN1Sequence> otherRevocationInfo = new ArrayList<>();

    /**
     * Optional.
     *
     * <p>
     * It is a collection of CRL revocation status information.
     */
    private final Collection<CRL> crls = new ArrayList<>();

    /**
     * Optional.
     *
     * <p>
     * It is a collection of CRL revocation status information.
     */
    private final Collection<IBasicOCSPResponse> ocsps = new ArrayList<>();

    /**
     * This represents the signed content.
     * In the case of a signed PDF document this will of type data with no content.
     */
    private EncapsulatedContentInfo encapContentInfo = new EncapsulatedContentInfo();

    /**
     * Optional.
     *
     * <p>
     * It is intended to add all certificates to be able to validate the entire chain.
     */
    private Collection<X509Certificate> certificates = new ArrayList<>();

    /**
     * This class only supports one signer per signature field.
     */
    private SignerInfo signerInfo = new SignerInfo();

    /**
     * Creates an empty SignedData structure.
     */
    public CMSContainer() {
        // Empty constructor.
    }

    /**
     * Creates a SignedData structure from a serialized ASN1 structure.
     *
     * @param encodedCMSdata the serialized CMS container
     *
     * @throws IOException          if issues occur during ASN1 objects creation.
     * @throws CertificateException if issues occur processing the embedded certificates.
     * @throws CRLException         if CRL encoding error occurs.
     */
    public CMSContainer(byte[] encodedCMSdata) throws IOException, CertificateException, CRLException {
        try (IASN1InputStream is = BC_FACTORY.createASN1InputStream(new ByteArrayInputStream(encodedCMSdata))) {
            IASN1Sequence contentInfo = BC_FACTORY.createASN1Sequence(is.readObject());
            IASN1Sequence signedData = BC_FACTORY.createASN1Sequence(
                    BC_FACTORY.createASN1TaggedObject(contentInfo.getObjectAt(1)).getObject());

            // The digest algorithm is retrieved from SignerInfo later on, here we just validate
            // that there is exactly 1 digest algorithm.
            IASN1Set digestAlgorithms = BC_FACTORY.createASN1Set(signedData.getObjectAt(1));
            if (digestAlgorithms.size() > 1) {
                throw new PdfException(SignExceptionMessageConstant.CMS_ONLY_ONE_SIGNER_ALLOWED);
            }

            IASN1Sequence lencapContentInfo = BC_FACTORY.createASN1Sequence(signedData.getObjectAt(2));
            encapContentInfo = new EncapsulatedContentInfo(lencapContentInfo);
            processCertificates(signedData);
            int next = 4;
            IASN1TaggedObject taggedObj = BC_FACTORY.createASN1TaggedObject(signedData.getObjectAt(next));
            if (taggedObj != null) {
                ++next;
                CertificateUtil.retrieveRevocationInfoFromSignedData(taggedObj, this.crls, this.ocsps,
                        this.otherRevocationInfo);
            }
            IASN1Set signerInfosS = BC_FACTORY.createASN1Set(signedData.getObjectAt(next));
            if (signerInfosS.size() != 1) {
                throw new PdfException(SignExceptionMessageConstant.CMS_ONLY_ONE_SIGNER_ALLOWED);
            }
            signerInfo = new SignerInfo(signerInfosS.getObjectAt(0), certificates);
        } catch (NullPointerException npe) {
            throw new PdfException(SignExceptionMessageConstant.CMS_INVALID_CONTAINER_STRUCTURE, npe);
        }
    }

    /**
     * This class only supports one signer per signature field.
     *
     * @param signerInfo the singerInfo
     */
    public void setSignerInfo(SignerInfo signerInfo) {
        this.signerInfo = signerInfo;
    }

    /**
     * This class only supports one signer per signature field.
     *
     * @return the singerInfo
     */
    public SignerInfo getSignerInfo() {
        return signerInfo;
    }

    /**
     * When all fields except for signer.signedAttributes.digest and signer.signature are completed
     * it is possible to calculate the eventual size of the signature by serializing except for the signature
     * (that depends on the digest and cypher but is set at 1024 bytes) and later added unsigned attributes like
     * timestamps.
     *
     * @return the estimated size of the complete CMS container before signature is added, size for the signature is
     * added, size for other attributes like timestamps is not.
     *
     * @throws CertificateEncodingException if an encoding error occurs in {@link X509Certificate}.
     * @throws IOException                  if an I/O error occurs.
     * @throws CRLException                 if CRL encoding error occurs.
     */
    public long getSizeEstimation() throws CertificateEncodingException, IOException, CRLException {
        byte[] result = serialize(true);
        return result.length;
    }

    /**
     * Only version 1 is supported by this class.
     *
     * @return 1 as CMSversion
     */
    public int getCmsVersion() {
        return 1;
    }

    /**
     * The digest algorithm OID and parameters used by the signer.
     * This class only supports one signer for use in pdf signatures, so only one digest algorithm is supported.
     *
     * <p>
     * This field is set when adding the signerInfo.
     *
     * @return {@link AlgorithmIdentifier} digest algorithm.
     */
    public AlgorithmIdentifier getDigestAlgorithm() {
        if (signerInfo == null) {
            return null;
        }
        return signerInfo.getDigestAlgorithm();
    }

    /**
     * This represents the signed content.
     * In the case of a signed PDF document this will be of type data with no content.
     *
     * @return a representation of the data to be signed.
     */
    public EncapsulatedContentInfo getEncapContentInfo() {
        return encapContentInfo;
    }

    /**
     * This represents the signed content.
     * In the case of a signed PDF document this will be of type data with no content.
     * Defaults to 1.2.840.113549.1.7.1 {iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) pkcs-7(7) id-data(1)}
     *
     * @param encapContentInfo a representation of the data to be signed.
     */
    public void setEncapContentInfo(EncapsulatedContentInfo encapContentInfo) {
        this.encapContentInfo = encapContentInfo;
    }

    /**
     * Adds a certificate.
     *
     * @param cert the certificate to be added
     */
    public void addCertificate(X509Certificate cert) {
        certificates.add(cert);
    }

    /**
     * Adds a set of certificates.
     *
     * @param certs the certificates to be added
     */
    public void addCertificates(X509Certificate[] certs) {
        certificates = Arrays.asList(certs);
    }

    /**
     * Retrieves a copy of the list of certificates.
     *
     * @return the list of certificates to be used for signing and certificate validation
     */
    public Collection<X509Certificate> getCertificates() {
        return Collections.unmodifiableCollection(certificates);
    }

    /**
     * Retrieves a copy of the list of CRLs.
     *
     * @return the list of CRL revocation info.
     */
    public Collection<CRL> getCrls() {
        return Collections.unmodifiableCollection(crls);
    }

    /**
     * Adds a CRL response to the CMS container.
     *
     * @param crl the CRL response to be added.
     */
    public void addCrl(CRL crl) {
        crls.add(crl);
    }

    /**
     * Retrieves a copy of the list of OCSPs.
     *
     * @return the list of OCSP revocation info.
     */
    public Collection<IBasicOCSPResponse> getOcsps() {
        return Collections.unmodifiableCollection(ocsps);
    }

    /**
     * Adds an OCSP response to the CMS container.
     *
     * @param ocspResponse the OCSP response to be added.
     */
    public void addOcsp(IBasicOCSPResponse ocspResponse) {
        ocsps.add(ocspResponse);
    }

    /**
     * Sets the Signed Attributes of the signer info to this serialized version.
     * The signed attributes will become read-only.
     *
     * @param signedAttributesData the serialized Signed Attributes
     */
    public void setSerializedSignedAttributes(byte[] signedAttributesData) {
        signerInfo.setSerializedSignedAttributes(signedAttributesData);
    }

    /**
     * Retrieves the encoded signed attributes of the signer info.
     * This makes the signed attributes read only.
     *
     * @return the encoded signed attributes of the signer info.
     *
     * @throws IOException if issues occur during ASN1 objects creation.
     */
    public byte[] getSerializedSignedAttributes() throws IOException {
        if (signerInfo == null) {
            throw new IllegalStateException(SignExceptionMessageConstant.CMS_SIGNERINFO_NOT_INITIALIZED);
        }
        return signerInfo.serializeSignedAttributes();
    }

    /**
     * Serializes the SignedData structure and makes the signer infos signed attributes read only.
     *
     * @return the encoded DignedData structure.
     *
     * @throws CertificateEncodingException if errors occur during certificate processing.
     * @throws IOException                  if issues occur during ASN1 objects creation.
     * @throws CRLException                 if CRL encoding error occurs.
     */
    public byte[] serialize() throws CertificateEncodingException, IOException, CRLException {
        return serialize(false);
    }

    private byte[] serialize(boolean forEstimation) throws CertificateEncodingException, IOException, CRLException {
    /* ContentInfo SEQUENCE
           ContentType OBJECT IDENTIFIER (1.2.840.113549.1.7.2)
           Content [0] SEQUENCE
               SignedData SEQUENCE
                 version INTEGER
                 digestAlgorithms SET
                     DigestAlgorithmIdentifier SEQUENCE
                         algorithm OBJECT IDENTIFIER
                         parameters ANY
                 encapContentInfo EncapsulatedContentInfo SEQUENCE
                         eContentType ContentType OBJECT IDENTIFIER (1.2.840.113549.1.7.1 data)
                 certificates CertificateSet [0] SET
                         CertificateChoices SEQUENCE
                             tbsCertificate TBSCertificate SEQUENCE
                 crls RevocationInfoChoices [1] SET
                         RevocationInfoChoice CHOICE {
                             crl CertificateList SEQUENCE,
                             other OtherRevocationInfoFormat SEQUENCE
                                    otherRevInfoFormat OBJECT IDENTIFIER,
                                    otherRevInfo ANY DEFINED BY otherRevInfoFormat (SEQUENCE for OCSP)
                         }
                 signerInfos SignerInfos SET
     */

        IASN1EncodableVector contentInfoV = BC_FACTORY.createASN1EncodableVector();
        contentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(OID.PKCS7_SIGNED_DATA));
        IASN1EncodableVector singedDataV = BC_FACTORY.createASN1EncodableVector();
        singedDataV.add(BC_FACTORY.createASN1Integer(getCmsVersion())); // version
        IASN1EncodableVector digestAlgorithmsV = BC_FACTORY.createASN1EncodableVector();
        digestAlgorithmsV.add(getDigestAlgorithm().getAsASN1Sequence());
        singedDataV.add(BC_FACTORY.createDERSet(digestAlgorithmsV));
        IASN1EncodableVector encapContentInfoV = BC_FACTORY.createASN1EncodableVector();
        encapContentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(encapContentInfo.getContentType()));
        if (encapContentInfo.getContent() != null) {
            encapContentInfoV.add(encapContentInfo.getContent());
        }
        singedDataV.add(BC_FACTORY.createDERSequence(encapContentInfoV));
        IASN1EncodableVector certificateSetV = BC_FACTORY.createASN1EncodableVector();
        for (X509Certificate cert : certificates) {
            certificateSetV.add(BC_FACTORY.createASN1Primitive(cert.getEncoded()));
        }
        singedDataV.add(BC_FACTORY.createDERTaggedObject(false, 0, BC_FACTORY.createDERSet(certificateSetV)));

        IDERSet revInfoChoices =
                CertificateUtil.createRevocationInfoChoices(this.crls, this.ocsps, this.otherRevocationInfo);
        if (revInfoChoices != null) {
            singedDataV.add(BC_FACTORY.createDERTaggedObject(false, 1, revInfoChoices));
        }

        IASN1EncodableVector signerInfosV = BC_FACTORY.createASN1EncodableVector();
        signerInfosV.add(signerInfo.getAsDerSequence(forEstimation));
        singedDataV.add(BC_FACTORY.createDERSet(signerInfosV));
        contentInfoV.add(BC_FACTORY.createDERTaggedObject(0, BC_FACTORY.createDERSequence(singedDataV)));
        return BC_FACTORY.createDERSequence(contentInfoV).getEncoded();
    }

    private void processCertificates(IASN1Sequence signedData) throws CertificateException, IOException {
        // Certificates are optional according to the specs, but we do require at least the signing certificate.
        IASN1TaggedObject taggedCertificatesSet = BC_FACTORY.createASN1TaggedObject(signedData.getObjectAt(3));
        if (taggedCertificatesSet == null) {
            throw new PdfException(SignExceptionMessageConstant.CMS_MISSING_CERTIFICATES);
        }
        IASN1Set certificatesSet = BC_FACTORY.createASN1Set(taggedCertificatesSet, false);
        if (certificatesSet.isNull() || certificatesSet.size() == 0) {
            throw new PdfException(SignExceptionMessageConstant.CMS_MISSING_CERTIFICATES);
        }
        for (IASN1Encodable certObj : certificatesSet.toArray()) {
            try (InputStream cis = new ByteArrayInputStream(certObj.toASN1Primitive().
                    getEncoded(BC_FACTORY.createASN1Encoding().getDer()))) {
                certificates.add((X509Certificate) CertificateUtil.generateCertificate(cis));
            }
        }
    }
}
