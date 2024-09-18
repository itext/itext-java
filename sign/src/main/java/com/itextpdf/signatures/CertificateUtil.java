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
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * This class contains a series of static methods that
 * allow you to retrieve information from a Certificate.
 */
public class CertificateUtil {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtil.class);

    // Certificate Revocation Lists

    /**
     * Gets a CRLs from the X509 certificate.
     *
     * @param certificate the X509Certificate to extract the CRLs from
     *
     * @return CRL list or null if there's no CRL available
     *
     * @throws IOException          thrown when the URL couldn't be opened properly.
     * @throws CertificateException thrown if there's no X509 implementation in the provider.
     * @throws CRLException         thrown when encountering errors when parsing the CRL.
     */
    public static List<CRL> getCRLs(X509Certificate certificate)
            throws CertificateException, CRLException, IOException {
        List<CRL> crls = new ArrayList<>();
        for (String crlUrl : getCRLURLs(certificate)) {
            crls.add(CertificateUtil.getCRL(crlUrl));
        }
        return crls;
    }

    /**
     * Gets the list of the Certificate Revocation List URLs for a Certificate.
     *
     * @param certificate the Certificate to get CRL URLs for
     *
     * @return the list of URL strings where you can check if the certificate is revoked.
     */
    public static List<String> getCRLURLs(X509Certificate certificate) {
        List<String> crls = new ArrayList<>();
        IDistributionPoint[] dists = getDistributionPoints(certificate);
        for (IDistributionPoint p : dists) {
            IDistributionPointName distributionPointName = p.getDistributionPoint();
            if (FACTORY.createDistributionPointName().getFullName() != distributionPointName.getType()) {
                continue;
            }
            IGeneralNames generalNames = FACTORY.createGeneralNames(distributionPointName.getName());
            IGeneralName[] names = generalNames.getNames();
            // If the DistributionPointName contains multiple values, each name describes a different mechanism
            // to obtain the same CRL.
            for (IGeneralName name : names) {
                if (name.getTagNo() != FACTORY.createGeneralName().getUniformResourceIdentifier()) {
                    continue;
                }
                IDERIA5String derStr = FACTORY
                        .createDERIA5String(FACTORY.createASN1TaggedObject(name.toASN1Primitive()), false);
                crls.add(derStr.getString());
            }
        }
        return crls;
    }

    /**
     * Gets the Distribution Point from the certificate by name specified in the Issuing Distribution Point from the
     * Certificate Revocation List for a Certificate.
     *
     * @param certificate                  the certificate to retrieve Distribution Points
     * @param issuingDistributionPointName distributionPointName retrieved from the IDP of the CRL
     *
     * @return distribution point withthe same name as specified in the IDP.
     */
    public static IDistributionPoint getDistributionPointByName(X509Certificate certificate,
                                                                IDistributionPointName issuingDistributionPointName) {
        IDistributionPoint[] distributionPoints = getDistributionPoints(certificate);
        List<IGeneralName> issuingNames = Arrays.asList(
                FACTORY.createGeneralNames(issuingDistributionPointName.getName()).getNames());
        for (IDistributionPoint distributionPoint : distributionPoints) {
            IDistributionPointName distributionPointName = distributionPoint.getDistributionPoint();
            IGeneralNames generalNames = distributionPointName.isNull() ? distributionPoint.getCRLIssuer() :
                    FACTORY.createGeneralNames(distributionPointName.getName());
            IGeneralName[] names = generalNames.getNames();
            for (IGeneralName name : names) {
                if (issuingNames.contains(name)) {
                    return distributionPoint;
                }
            }
        }
        return null;
    }

    /**
     * Gets the CRL object using a CRL URL.
     *
     * @param url the URL where the CRL is located
     *
     * @return CRL object
     *
     * @throws IOException          thrown when the URL couldn't be opened properly.
     * @throws CertificateException thrown if there's no X509 implementation in the provider.
     * @throws CRLException         thrown when encountering errors when parsing the CRL.
     */
    public static CRL getCRL(String url) throws IOException, CertificateException, CRLException {
        if (url == null) {
            return null;
        }
        return CertificateUtil.parseCrlFromStream(new URL(url).openStream());
    }

    /**
     * Parses a CRL from an InputStream.
     *
     * @param input the InputStream holding the unparsed CRL
     *
     * @return the parsed CRL object.
     *
     * @throws CertificateException thrown if there's no X509 implementation in the provider.
     * @throws CRLException         thrown when encountering errors when parsing the CRL.
     */
    public static CRL parseCrlFromStream(InputStream input) throws CertificateException, CRLException {
        return SignUtils.parseCrlFromStream(input);
    }

    /**
     * Parses a CRL from bytes.
     *
     * @param crlBytes the bytes holding the unparsed CRL
     *
     * @return the parsed CRL object.
     *
     * @throws CertificateException thrown if there's no X509 implementation in the provider.
     * @throws CRLException         thrown when encountering errors when parsing the CRL.
     */
    public static CRL parseCrlFromBytes(byte[] crlBytes) throws CertificateException, CRLException {
        return SignUtils.parseCrlFromStream(new ByteArrayInputStream(crlBytes));
    }

    /**
     * Retrieves the URL for the issuer certificate for the given CRL.
     *
     * @param crl the CRL response
     *
     * @return the URL or null.
     */
    public static String getIssuerCertURL(CRL crl) {
        IASN1Primitive obj;
        try {
            obj = getExtensionValue(crl, FACTORY.createExtension().getAuthorityInfoAccess().getId());
            return getValueFromAIAExtension(obj, SecurityIDs.ID_CA_ISSUERS);
        } catch (IOException e) {
            return null;
        }
    }

    // Online Certificate Status Protocol

    /**
     * Retrieves the OCSP URL from the given certificate.
     *
     * @param certificate the certificate
     *
     * @return the URL or null
     */
    public static String getOCSPURL(X509Certificate certificate) {
        IASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, FACTORY.createExtension().getAuthorityInfoAccess().getId());
            return getValueFromAIAExtension(obj, SecurityIDs.ID_OCSP);
        } catch (IOException e) {
            return null;
        }
    }

    // Missing certificates in chain

    /**
     * Retrieves the URL for the issuer lists certificates for the given certificate.
     *
     * @param certificate the certificate
     *
     * @return the URL or null.
     */
    public static String getIssuerCertURL(X509Certificate certificate) {
        IASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, FACTORY.createExtension().getAuthorityInfoAccess().getId());
            return getValueFromAIAExtension(obj, SecurityIDs.ID_CA_ISSUERS);
        } catch (IOException e) {
            return null;
        }
    }

    // Time Stamp Authority

    /**
     * Gets the URL of the TSA if it's available on the certificate
     *
     * @param certificate a certificate
     *
     * @return a TSA URL
     */
    public static String getTSAURL(X509Certificate certificate) {
        byte[] der = SignUtils.getExtensionValueByOid(certificate, SecurityIDs.ID_TSA);
        if (der == null) {
            return null;
        }
        IASN1Primitive asn1obj;
        try {
            asn1obj = FACTORY.createASN1Primitive(der);
            IDEROctetString octets = FACTORY.createDEROctetString(asn1obj);
            asn1obj = FACTORY.createASN1Primitive(octets.getOctets());
            IASN1Sequence asn1seq = FACTORY.createASN1SequenceInstance(asn1obj);
            return getStringFromGeneralName(asn1seq.getObjectAt(1).toASN1Primitive());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Generates a certificate object and initializes it with the data read from the input stream inStream.
     *
     * @param data the input stream with the certificates.
     *
     * @return a certificate object initialized with the data from the input stream.
     *
     * @throws CertificateException on parsing errors.
     */
    public static Certificate generateCertificate(InputStream data) throws CertificateException {
        return SignUtils.generateCertificate(data, FACTORY.getProvider());
    }

    /**
     * Try to retrieve CRL and OCSP responses from the signed data crls field.
     *
     * @param taggedObj signed data crls field as {@link IASN1TaggedObject}.
     *
     * @param crls                          collection to store retrieved CRL responses.
     * @param ocsps                         collection of {@link IBasicOCSPResponse} wrappers to store retrieved
     *                                      OCSP responses.
     * @param otherRevocationInfoFormats    collection of revocation info other than OCSP and CRL responses,
     *                                      e.g. SCVP Request and Response, stored as {@link IASN1Sequence}.
     *
     * @throws IOException          if some I/O error occurred.
     * @throws CertificateException if CertificateFactory instance wasn't created.
     */
    public static void retrieveRevocationInfoFromSignedData(IASN1TaggedObject taggedObj, Collection<CRL> crls,
                                                            Collection<IBasicOCSPResponse> ocsps,
                                                            Collection<IASN1Sequence> otherRevocationInfoFormats)
            throws IOException, CertificateException {
        Enumeration revInfo = FACTORY.createASN1Set(taggedObj, false).getObjects();
        while (revInfo.hasMoreElements()) {
            IASN1Sequence s = FACTORY.createASN1Sequence(revInfo.nextElement());
            IASN1ObjectIdentifier o = FACTORY.createASN1ObjectIdentifier(s.getObjectAt(0));
            if (o != null && SecurityIDs.ID_RI_OCSP_RESPONSE.equals(o.getId())) {
                IASN1Sequence ocspResp = FACTORY.createASN1Sequence(s.getObjectAt(1));
                IASN1Enumerated respStatus = FACTORY.createASN1Enumerated(ocspResp.getObjectAt(0));
                if (respStatus.intValueExact() == FACTORY.createOCSPRespBuilderInstance().getSuccessful()) {
                    IASN1Sequence responseBytes = FACTORY.createASN1Sequence(ocspResp.getObjectAt(1));
                    if (responseBytes != null) {
                        ocsps.add(CertificateUtil.createOcsp(responseBytes));
                    }
                }
            } else {
                try {
                    crls.addAll(SignUtils.readAllCRLs(s.getEncoded()));
                } catch (CRLException ignored) {
                    LOGGER.warn(SignLogMessageConstant.UNABLE_TO_PARSE_REV_INFO);
                    otherRevocationInfoFormats.add(s);
                }
            }
        }
    }

    /**
     * Creates the revocation info (crls field) for SignedData structure:
     * RevocationInfoChoices ::= SET OF RevocationInfoChoice
     *
     *       RevocationInfoChoice ::= CHOICE {
     *         crl CertificateList,
     *         other [1] IMPLICIT OtherRevocationInfoFormat }
     *
     *       OtherRevocationInfoFormat ::= SEQUENCE {
     *         otherRevInfoFormat OBJECT IDENTIFIER,
     *         otherRevInfo ANY DEFINED BY otherRevInfoFormat }
     *
     *       CertificateList  ::=  SEQUENCE  {
     *         tbsCertList          TBSCertList,
     *         signatureAlgorithm   AlgorithmIdentifier,
     *         signatureValue       BIT STRING  }
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5652#section-10.2.1">RFC 5652 §10.2.1</a>
     *
     * @param crls                          collection of CRL revocation status information.
     * @param ocsps                         collection of OCSP revocation status information.
     * @param otherRevocationInfoFormats    collection of revocation info other than OCSP and CRL responses,
     *                                      e.g. SCVP Request and Response, stored as {@link IASN1Sequence}.
     *
     * @return {@code crls [1] RevocationInfoChoices} field of SignedData structure. Null if SignedData has
     * no revocation data.
     *
     * @throws CRLException if an encoding error occurs.
     * @throws IOException  if an I/O error occurs.
     */
    public static IDERSet createRevocationInfoChoices(Collection<CRL> crls, Collection<IBasicOCSPResponse> ocsps,
                                                      Collection<IASN1Sequence> otherRevocationInfoFormats)
            throws CRLException, IOException {
        if (crls.isEmpty() && ocsps.isEmpty()) {
            return null;
        }
        IASN1EncodableVector revocationInfoChoices = FACTORY.createASN1EncodableVector();

        // Add CRLs
        for (CRL element : crls) {
            // Add crl CertificateList (crl RevocationInfoChoice)
            revocationInfoChoices.add(FACTORY.createASN1Sequence(((X509CRL) element).getEncoded()));
        }

        // Add OCSPs
        for (IBasicOCSPResponse element : ocsps) {
            IASN1EncodableVector ocspResponseRevInfo = FACTORY.createASN1EncodableVector();
            // Add otherRevInfoFormat (ID_RI_OCSP_RESPONSE)
            ocspResponseRevInfo.add(FACTORY.createASN1ObjectIdentifier(SecurityIDs.ID_RI_OCSP_RESPONSE));

            IASN1EncodableVector ocspResponse = FACTORY.createASN1EncodableVector();
            ocspResponse.add(FACTORY.createOCSPResponseStatus(
                    FACTORY.createOCSPRespBuilderInstance().getSuccessful()).toASN1Primitive());
            ocspResponse.add(FACTORY.createResponseBytes(
                    FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspBasic(),
                    FACTORY.createDEROctetString(element.toASN1Primitive().getEncoded())).toASN1Primitive());
            // Add otherRevInfo (ocspResponse)
            ocspResponseRevInfo.add(FACTORY.createDERSequence(ocspResponse));

            // Add other [1] IMPLICIT OtherRevocationInfoFormat (ocsp RevocationInfoChoice)
            revocationInfoChoices.add(FACTORY.createDERSequence(ocspResponseRevInfo));
        }

        // Add other RevocationInfo formats
        for (IASN1Sequence revInfo : otherRevocationInfoFormats) {
            revocationInfoChoices.add(revInfo);
        }

        return FACTORY.createDERSet(revocationInfoChoices);
    }

    /**
     * Checks if the issuer of the provided certID (specified in the OCSP response) and provided issuer of the
     * certificate in question matches, i.e. checks that issuerNameHash and issuerKeyHash fields of the certID
     * is the hash of the issuer's name and public key.
     *
     * <p>
     * SingleResp contains the basic information of the status of the certificate identified by the certID. The issuer
     * name and serial number identify a unique certificate, so if serial numbers of the certificate in question and
     * certID serial number are equals and issuers match, then SingleResp contains the information about the status of
     * the certificate in question.
     *
     * @param certID     certID specified in the OCSP response
     * @param issuerCert the issuer of the certificate in question
     *
     * @return true if the issuers are the same, false otherwise.
     *
     * @throws AbstractOperatorCreationException in case some digest calculator creation error.
     * @throws AbstractOCSPException             in case some digest calculator creation error.
     * @throws CertificateEncodingException      if an encoding error occurs.
     * @throws IOException                       if input-output exception occurs.
     */
    public static boolean checkIfIssuersMatch(ICertificateID certID, X509Certificate issuerCert)
            throws AbstractOperatorCreationException, AbstractOCSPException, CertificateEncodingException, IOException {
        return SignUtils.checkIfIssuersMatch(certID, issuerCert);
    }

    /**
     * Retrieves certificate extension value by its OID.
     *
     * @param certificate to get extension from
     * @param id          extension OID to retrieve
     *
     * @return encoded extension value.
     */
    public static byte[] getExtensionValueByOid(X509Certificate certificate, String id) {
        return SignUtils.getExtensionValueByOid(certificate, id);
    }

    /**
     * Checks if an OCSP response is genuine.
     *
     * @param ocspResp      {@link IBasicOCSPResp} the OCSP response wrapper
     * @param responderCert the responder certificate
     *
     * @return true if the OCSP response verifies against the responder certificate.
     */
    public static boolean isSignatureValid(IBasicOCSPResp ocspResp, Certificate responderCert) {
        try {
            return SignUtils.isSignatureValid(ocspResp, responderCert, FACTORY.getProviderName());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the certificate is signed by provided issuer certificate.
     *
     * @param subjectCertificate a certificate to check
     * @param issuerCertificate  an issuer certificate to check
     *
     * @return true if the first passed certificate is signed by next passed certificate.
     */
    static boolean isIssuerCertificate(X509Certificate subjectCertificate, X509Certificate issuerCertificate) {
        return subjectCertificate.getIssuerX500Principal().equals(issuerCertificate.getSubjectX500Principal());
    }

    /**
     * Checks if the certificate is self-signed.
     *
     * @param certificate a certificate to check
     *
     * @return true if the certificate is self-signed.
     */
    public static boolean isSelfSigned(X509Certificate certificate) {
        return certificate.getIssuerX500Principal().equals(certificate.getSubjectX500Principal());
    }

    // helper methods

    /**
     * Gets certificate extension value.
     *
     * @param certificate the certificate from which we need the ExtensionValue
     * @param oid         the Object Identifier value for the extension
     *
     * @return the extension value as an {@link IASN1Primitive} object.
     * 
     * @throws IOException on processing exception.
     */
    public static IASN1Primitive getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        return getExtensionValueFromByteArray(SignUtils.getExtensionValueByOid(certificate, oid));
    }

    /**
     * Gets CRL extension value.
     *
     * @param crl the CRL from which we need the ExtensionValue
     * @param oid the Object Identifier value for the extension
     *
     * @return the extension value as an {@link IASN1Primitive} object.
     *
     * @throws IOException on processing exception.
     */
    public static IASN1Primitive getExtensionValue(CRL crl, String oid) throws IOException {
        return getExtensionValueFromByteArray(SignUtils.getExtensionValueByOid(crl, oid));
    }

    /**
     * Converts extension value represented as byte array to {@link IASN1Primitive} object.
     *
     * @param extensionValue the extension value as byte array
     *
     * @return the extension value as an {@link IASN1Primitive} object.
     *
     * @throws IOException on processing exception.
     */
    private static IASN1Primitive getExtensionValueFromByteArray(byte[] extensionValue) throws IOException {
        if (extensionValue == null) {
            return null;
        }
        IASN1OctetString octs;
        try (IASN1InputStream aIn = FACTORY.createASN1InputStream(new ByteArrayInputStream(extensionValue))) {
            octs = FACTORY.createASN1OctetString(aIn.readObject());
        }
        try (IASN1InputStream aIn = FACTORY.createASN1InputStream(new ByteArrayInputStream(octs.getOctets()))) {
            return aIn.readObject();
        }
    }

    /**
     * Gets a String from an ASN1Primitive
     *
     * @param names the {@link IASN1Primitive} primitive wrapper
     *
     * @return a human-readable String
     */
    private static String getStringFromGeneralName(IASN1Primitive names) {
        IASN1TaggedObject taggedObject = FACTORY.createASN1TaggedObject(names);
        return new String(FACTORY.createASN1OctetString(taggedObject, false).getOctets(), StandardCharsets.ISO_8859_1);
    }

    /**
     * Retrieves accessLocation value for specified accessMethod from the Authority Information Access extension.
     *
     * @param extensionValue Authority Information Access extension value
     * @param accessMethod   accessMethod OID; usually id-ad-caIssuers or id-ad-ocsp
     *
     * @return the location (URI) of the information.
     */
    private static String getValueFromAIAExtension(IASN1Primitive extensionValue, String accessMethod) {
        if (extensionValue == null) {
            return null;
        }
        IASN1Sequence accessDescriptions = FACTORY.createASN1Sequence(extensionValue);
        for (int i = 0; i < accessDescriptions.size(); i++) {
            IASN1Sequence accessDescription = FACTORY.createASN1Sequence(accessDescriptions.getObjectAt(i));
            IASN1ObjectIdentifier id = FACTORY.createASN1ObjectIdentifier(accessDescription.getObjectAt(0));
            if (accessDescription.size() == 2 && id != null && accessMethod.equals(id.getId())) {
                IASN1Primitive description = FACTORY.createASN1Primitive(accessDescription.getObjectAt(1));
                return getStringFromGeneralName(description);
            }
        }
        return null;
    }

    /**
     * Helper method that creates the {@link IBasicOCSPResponse} object from the response bytes.
     *
     * @param seq response bytes.
     *
     * @return {@link IBasicOCSPResponse} object.
     *
     * @throws IOException if some I/O error occurred.
     */
    private static IBasicOCSPResponse createOcsp(IASN1Sequence seq) throws IOException {
        IASN1ObjectIdentifier objectIdentifier = FACTORY.createASN1ObjectIdentifier(
                seq.getObjectAt(0));
        IOCSPObjectIdentifiers ocspObjectIdentifiers = FACTORY.createOCSPObjectIdentifiers();
        if (objectIdentifier != null
                && objectIdentifier.getId().equals(ocspObjectIdentifiers.getIdPkixOcspBasic().getId())) {
            IASN1OctetString os = FACTORY.createASN1OctetString(seq.getObjectAt(1));
            try (IASN1InputStream inp = FACTORY.createASN1InputStream(os.getOctets())) {
                return FACTORY.createBasicOCSPResponse(inp.readObject());
            }
        }
        return null;
    }

    private static IDistributionPoint[] getDistributionPoints(X509Certificate certificate) {
        IASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, FACTORY.createExtension().getCRlDistributionPoints().getId());
        } catch (IOException e) {
            obj = null;
        }
        if (obj == null) {
            return new IDistributionPoint[0];
        }
        ICRLDistPoint dist = FACTORY.createCRLDistPoint(obj);
        return dist.getDistributionPoints();
    }
}
