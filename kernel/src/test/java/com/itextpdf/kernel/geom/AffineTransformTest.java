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
package com.itextpdf.kernel.geom;

import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AffineTransformTest {

    @Test
    public void selfTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assert.assertTrue(affineTransform.equals(affineTransform));
    }

    @Test
    public void nullTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assert.assertFalse(affineTransform.equals(null));
    }

    @Test
    public void otherClassTest() {
        AffineTransform affineTransform = new AffineTransform();
        String string = "Test";

        Assert.assertFalse(affineTransform.equals(string));
    }

    @Test
    public void sameValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assert.assertFalse(affineTransform1 == affineTransform2);
        Assert.assertEquals(hash1, hash2);
        Assert.assertTrue(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void differentValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(5d, 4d, 3d, 2d, 1d, 1d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assert.assertFalse(affineTransform1 == affineTransform2);
        Assert.assertNotEquals(hash1, hash2);
        Assert.assertFalse(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void rotateTest(){
        AffineTransform rotateOne  = AffineTransform.getRotateInstance(Math.PI/2);
        AffineTransform expected = new AffineTransform(0,1,-1,0,0,0);

        Assert.assertEquals(rotateOne,expected);
    }

    @Test
    public void rotateTranslateTest(){
        AffineTransform rotateTranslate = AffineTransform.getRotateInstance(Math.PI/2, 10,5);
        AffineTransform expected = new AffineTransform(0,1,-1,0,15,-5);

        Assert.assertEquals(rotateTranslate,expected);
    }
}
