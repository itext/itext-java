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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DigestAlgorithmsTest extends ExtendedITextTest {
    @Test
    public void emptyStringOidGetDigestTest() {
        String oid = "";
        Assert.assertEquals(oid, DigestAlgorithms.getDigest(oid));
    }

    @Test
    public void nonExistingOidGetDigestTest() {
        String oid = "non_existing_oid";
        Assert.assertEquals(oid, DigestAlgorithms.getDigest(oid));
    }

    @Test
    public void emptyStringNameGetAllowedDigestTest() {
        Assert.assertNull(DigestAlgorithms.getAllowedDigest(""));
    }

    @Test
    public void nonExistingNameGetAllowedDigestTest() {
        Assert.assertNull(DigestAlgorithms.getAllowedDigest("non_existing_oid"));
    }

    @Test
    public void nullNameGetAllowedDigestTest() {
        Assert.assertThrows(IllegalArgumentException.class, () -> DigestAlgorithms.getAllowedDigest(null));
    }
}
