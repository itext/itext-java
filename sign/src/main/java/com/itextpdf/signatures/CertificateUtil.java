/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * This class contains a series of static methods that
 * allow you to retrieve information from a Certificate.
 */
public class CertificateUtil {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    // Certificate Revocation Lists

    /**
     * Gets a CRL from an X509 certificate.
     *
     * @param certificate the X509Certificate to extract the CRL from
     *
     * @return CRL or null if there's no CRL available
     *
     * @throws IOException          thrown when the URL couldn't be opened properly.
     * @throws CertificateException thrown if there's no X509 implementation in the provider.
     * @throws CRLException         thrown when encountering errors when parsing the CRL.
     */
    public static CRL getCRL(X509Certificate certificate) throws CertificateException, CRLException, IOException {
        return CertificateUtil.getCRL(CertificateUtil.getCRLURL(certificate));
    }

    /**
     * Gets the URL of the Certificate Revocation List for a Certificate
     *
     * @param certificate the Certificate
     *
     * @return the String where you can check if the certificate was revoked
     */
    public static String getCRLURL(X509Certificate certificate) {
        IASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, FACTORY.createExtension().getCRlDistributionPoints().getId());
        } catch (IOException e) {
            obj = null;
        }
        if (obj == null) {
            return null;
        }
        ICRLDistPoint dist = FACTORY.createCRLDistPoint(obj);
        IDistributionPoint[] dists = dist.getDistributionPoints();
        for (IDistributionPoint p : dists) {
            IDistributionPointName distributionPointName = p.getDistributionPoint();
            if (FACTORY.createDistributionPointName().getFullName() != distributionPointName.getType()) {
                continue;
            }
            IGeneralNames generalNames = FACTORY.createGeneralNames(distributionPointName.getName());
            IGeneralName[] names = generalNames.getNames();
            for (IGeneralName name : names) {
                if (name.getTagNo() != FACTORY.createGeneralName().getUniformResourceIdentifier()) {
                    continue;
                }
                IDERIA5String derStr = FACTORY
                        .createDERIA5String(FACTORY.createASN1TaggedObject(name.toASN1Primitive()), false);
                return derStr.getString();
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
        return SignUtils.parseCrlFromStream(new URL(url).openStream());
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
            if (obj == null) {
                return null;
            }
            IASN1Sequence accessDescriptions = FACTORY.createASN1Sequence(obj);
            for (int i = 0; i < accessDescriptions.size(); i++) {
                IASN1Sequence accessDescription = FACTORY.createASN1Sequence(accessDescriptions.getObjectAt(i));
                IASN1ObjectIdentifier id = FACTORY.createASN1ObjectIdentifier(accessDescription.getObjectAt(0));
                if (accessDescription.size() == 2 && id != null && SecurityIDs.ID_OCSP.equals(id.getId())) {
                    IASN1Primitive description = FACTORY.createASN1Primitive(accessDescription.getObjectAt(1));
                    return getStringFromGeneralName(description);
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
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

    // helper methods

    /**
     * @param certificate the certificate from which we need the ExtensionValue
     * @param oid         the Object Identifier value for the extension.
     *
     * @return the extension value as an {@link IASN1Primitive} object
     * 
     * @throws IOException
     */
    private static IASN1Primitive getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        byte[] bytes = SignUtils.getExtensionValueByOid(certificate, oid);
        if (bytes == null) {
            return null;
        }
        IASN1OctetString octs;
        try (IASN1InputStream aIn = FACTORY.createASN1InputStream(new ByteArrayInputStream(bytes))) {
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
}
