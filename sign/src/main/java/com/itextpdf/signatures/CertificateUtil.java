/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
                IASN1Sequence AccessDescription = FACTORY.createASN1Sequence(accessDescriptions.getObjectAt(i));
                IASN1ObjectIdentifier id = FACTORY.createASN1ObjectIdentifier(AccessDescription.getObjectAt(0));
                if (AccessDescription.size() != 2) {
                    // do nothing and continue
                } else if (id != null) {
                    if (SecurityIDs.ID_OCSP.equals(id.getId())) {
                        IASN1Primitive description = FACTORY.createASN1Primitive(AccessDescription.getObjectAt(1));
                        return getStringFromGeneralName(description);
                    }
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
     * @throws IOException
     * @return the extension value as an {@link IASN1Primitive} object
     */
    private static IASN1Primitive getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        byte[] bytes = SignUtils.getExtensionValueByOid(certificate, oid);
        if (bytes == null) {
            return null;
        }
        IASN1InputStream aIn = FACTORY.createASN1InputStream(new ByteArrayInputStream(bytes));
        IASN1OctetString octs = FACTORY.createASN1OctetString(aIn.readObject());
        aIn = FACTORY.createASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
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
