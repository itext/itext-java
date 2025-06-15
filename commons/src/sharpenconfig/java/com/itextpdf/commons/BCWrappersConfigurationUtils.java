/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.commons;

import sharpen.config.MappingConfigurator;
import sharpen.config.MemberKind;

public class BCWrappersConfigurationUtils {
    private BCWrappersConfigurationUtils() {}

    public static void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapMethod("java.security.cert.X509Certificate.getSerialNumber", "GetSerialNumber");
        configurator.mapMethod("java.security.cert.X509Certificate.getThisUpdate", "GetThisUpdate");
        configurator.mapMethod("java.security.cert.X509Certificate.getNotBefore", "GetNotBefore");
        configurator.mapMethod("java.security.cert.X509CRL.getNextUpdate", "GetNextUpdate");
        configurator.mapMethod("java.security.cert.X509Certificate.getSubjectDN", "GetSubjectDN");
        configurator.mapMethod("java.security.cert.X509CRL.getThisUpdate", "GetThisUpdate");
        configurator.mapType("java.math.BigInteger", "iText.Commons.Bouncycastle.Math.IBigInteger");
        configurator.mapMemberToInvocationsChain("java.security.MessageDigest.getInstance(java.lang.String)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateIDigest",
                MemberKind.Method);
        configurator.mapType("java.security.MessageDigest", "iText.Commons.Digest.IMessageDigest");
        configurator.mapMethod("java.security.MessageDigest.digest(byte[])", "Digest");

        configurator.mapType("java.security.NoSuchProviderException", "Org.BouncyCastle.Security.NoSuchProviderException");

        configurator.mapType("org.bouncycastle.cert.ocsp.SingleResp", "Org.BouncyCastle.Asn1.Ocsp.SingleResponse");
        configurator.mapType("org.bouncycastle.cert.ocsp.CertificateID", "Org.BouncyCastle.Asn1.Ocsp.CertID");
        configurator.mapType("org.bouncycastle.cert.ocsp.CertificateStatus", "Org.BouncyCastle.Asn1.Ocsp.CertStatus");
        configurator.mapType("org.bouncycastle.cert.ocsp.BasicOCSPResp", "Org.BouncyCastle.Asn1.Ocsp.BasicOcspResponse");
        configurator.mapType("org.bouncycastle.cert.ocsp.OCSPReq", "Org.BouncyCastle.Asn1.Ocsp.OcspRequest");
        configurator.mapType("org.bouncycastle.asn1.pkcs.RSASSAPSSparams", "Org.BouncyCastle.Asn1.Pkcs.RsassaPssParameters");

        configurator.mapProperty("org.bouncycastle.asn1.x509.AlgorithmIdentifier.getAlgorithm", "Algorithm");
        configurator.mapProperty("org.bouncycastle.asn1.x509.AlgorithmIdentifier.getParameters", "Parameters");
    }
}
