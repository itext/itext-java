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

import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;

@Category(IntegrationTest.class)
public class PdfResourcesTest {


    @Test
    public void resourcesTest1() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        PdfName n1 = resources.addExtGState(egs1);
        Assert.assertEquals("Gs1", n1.getValue());
        PdfName n2 = resources.addExtGState(egs2);
        Assert.assertEquals("Gs2", n2.getValue());
        n1 = resources.addExtGState(egs1);
        Assert.assertEquals("Gs1", n1.getValue());

        document.close();
    }

    @Test
    public void resourcesTest2() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(baos));
        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        resources.addExtGState(egs1);
        resources.addExtGState(egs2);
        document.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
        document = new PdfDocument(reader, new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream()));
        page = document.getPage(1);
        resources = page.getResources();
        Set<PdfName> names = resources.getResourceNames();
        Assert.assertEquals(2, names.size());

        String[] expectedNames = { "Gs1", "Gs2"};
        int i = 0;
        for (PdfName name : names) {
            Assert.assertEquals(expectedNames[i++], name.getValue());
        }

        PdfExtGState egs3 = new PdfExtGState();
        PdfName n3 = resources.addExtGState(egs3);
        Assert.assertEquals("Gs3", n3.getValue());
        PdfDictionary egsResources = page.getPdfObject().getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.ExtGState);
        PdfDictionary e1 = egsResources.getAsDictionary(new PdfName("Gs1"));
        PdfName n1 = resources.addExtGState(e1);
        Assert.assertEquals("Gs1", n1.getValue());
        PdfDictionary e2 = egsResources.getAsDictionary(new PdfName("Gs2"));
        PdfName n2 = resources.addExtGState(e2);
        Assert.assertEquals("Gs2", n2.getValue());
        PdfDictionary e4 = (PdfDictionary) e2.clone();
        PdfName n4 = resources.addExtGState(e4);
        Assert.assertEquals("Gs4", n4.getValue());
        document.close();
    }

}
