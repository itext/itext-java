/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DigestAlgorithmsManualTest extends ExtendedITextTest {

    @Test
    public void digestSHA1SunPKCS11Test() throws GeneralSecurityException, IOException {
        InputStream data = new ByteArrayInputStream(new byte[] {13, 16, 20, 0, 10});
        byte[] hash = DigestAlgorithms.digest(data, DigestAlgorithms.SHA1, "SunPKCS11");
        byte[] expected =
                new byte[] {15, 20, 1, 9, -106, 49, -37, -65, -45, -63, 53, -70, 76, -71, 102, -68, 78, -51, -100, 50};
        Assert.assertArrayEquals(expected, hash);
    }

    @Test
    @org.junit.Ignore
    public void digestSHA256SUNTest() throws GeneralSecurityException, IOException {
        InputStream data = new ByteArrayInputStream(new byte[] {13, 16, 20, 0, 10});
        byte[] hash = DigestAlgorithms.digest(data, DigestAlgorithms.SHA256, "SUN");
        byte[] expected =
                new byte[] {19, -84, -84, -45, -36, 121, -15, -18, -89, 97, -17, 51, 81,
                        119, -42, -59, -31, 121, -87, -82, -45, 119, 61, 92, 110, -99, 105, 4, 97, 12, 127, -62};
        Assert.assertArrayEquals(expected, hash);
    }
}
