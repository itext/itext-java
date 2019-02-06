/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.test.annotations.type.UnitTest;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CertificateSupportedCriticalExtensionsTest {

    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void supportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.KEY_USAGE, OID.X509Extensions.BASIC_CONSTRAINTS)
                .setKeyUsage(true, true);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void basicConstraintsSupportedTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.BASIC_CONSTRAINTS)
                .setKeyUsage(true, true);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void extendedKeyUsageWithIdKpTimestampingTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.EXTENDED_KEY_USAGE, OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setExtendedKeyUsage(OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void extendedKeyUsageWithoutIdKpTimestampingTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.EXTENDED_KEY_USAGE)
                .setExtendedKeyUsage("Not ID KP TIMESTAMPING.")
                .setKeyUsage(true, true);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void idKpTimestampingWithoutExtendedKeyUsageTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void unsupportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs("Totally not supported OID")
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void noUnsupportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(false);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void certificateIsNullTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("X509Certificate can't be null.");

        SignUtils.hasUnsupportedCriticalExtension(null);
    }
}
