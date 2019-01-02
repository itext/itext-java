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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfNameTest extends ITextTest {

    @Test
    public void specialCharactersTest(){
        String str1 = " %()<>";
        String str2 = "[]{}/#";
        PdfName name1 = new PdfName(str1);
        Assert.assertEquals(str1, createStringByEscaped(name1.getInternalContent()));
        PdfName name2 = new PdfName(str2);
        Assert.assertEquals(str2, createStringByEscaped(name2.getInternalContent()));
    }

    @Test
    public void basicCompareToTest(){
        // /#C3#9Cberschrift_1
        byte[] name1Content = new byte[] {35, 67, 51, 35, 57, 67, 98, 101, 114, 115, 99, 104, 114, 105, 102, 116, 95, 49};
        // /TOC-1
        byte[] name2Content = new byte[] {84, 79, 67, 45, 49};
        // /NormalParagraphStyle
        byte[] name3Content = new byte[] {78, 111, 114, 109, 97, 108, 80, 97, 114, 97, 103, 114, 97, 112, 104, 83, 116, 121, 108, 101};

        // /#C3#9Cberschrift_1, Ãberschrift_1
        PdfName name1 = new PdfName(name1Content);
        PdfName name1ContentOnly = new PdfName(name1Content);
        // /TOC-1, TOC-1
        PdfName name2 = new PdfName(name2Content);
        // /NormalParagraphStyle, NormalParagraphStyle
        PdfName name3 = new PdfName(name3Content);
        name1.generateValue();
        name2.generateValue();

        int oneToTwo = name1.compareTo(name2);
        int twoToOne = name2.compareTo(name1);

        int oneToThree = name1.compareTo(name3);
        int twoToThree = name2.compareTo(name3);

        int oneToOneContent = name1.compareTo(name1ContentOnly);
        int oneContentToTwo = name1ContentOnly.compareTo(name2);

        double delta = 1e-8;
        Assert.assertEquals(Math.signum(oneToTwo), -Math.signum(twoToOne), delta);

        Assert.assertEquals(Math.signum(oneToTwo), Math.signum(twoToThree), delta);
        Assert.assertEquals(Math.signum(oneToTwo), Math.signum(oneToThree), delta);

        Assert.assertEquals(oneToOneContent, 0);
        Assert.assertEquals(Math.signum(oneToTwo), Math.signum(oneContentToTwo), delta);
    }

}
