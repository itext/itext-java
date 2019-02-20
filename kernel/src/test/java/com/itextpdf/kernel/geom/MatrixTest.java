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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class MatrixTest extends ExtendedITextTest {

    @Test
    public void testMultiply() throws Exception{
        Matrix m1 = new Matrix(2, 3, 4, 5, 6, 7);
        Matrix m2 = new Matrix(8, 9, 10, 11, 12, 13);
        Matrix shouldBe = new Matrix(46, 51, 82, 91, 130, 144);

        Matrix rslt = m1.multiply(m2);
        Assert.assertEquals(shouldBe, rslt);
    }

    @Test
    public void testDeterminant(){
        Matrix m = new Matrix(2, 3, 4, 5, 6, 7);
        Assert.assertEquals(-2f, m.getDeterminant(), .001f);
    }

    @Test
    public void testSubtract() throws Exception{
        Matrix m1 = new Matrix(1, 2, 3, 4, 5, 6);
        Matrix m2 = new Matrix(6, 5, 4, 3, 2, 1);
        Matrix shouldBe = new Matrix(-5, -3,0, -1, 1,0, 3, 5,0);

        Matrix rslt = m1.subtract(m2);
        Assert.assertEquals(shouldBe, rslt);
    }

    @Test
    public void testAdd() throws Exception{
        Matrix m1 = new Matrix(1, 2, 3, 4, 5, 6);
        Matrix m2 = new Matrix(6, 5, 4, 3, 2, 1);
        Matrix shouldBe = new Matrix(7, 7,0, 7, 7,0, 7, 7,2);

        Matrix rslt = m1.add(m2);
        Assert.assertEquals(shouldBe, rslt);
    }

}
