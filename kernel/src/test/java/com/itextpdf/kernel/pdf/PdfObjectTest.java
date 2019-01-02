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

import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Category(IntegrationTest.class)
public class PdfObjectTest {

    @Test
    public void indirectsChain1() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        catalog.put(new PdfName("a"), getTestPdfDictionary()
                .makeIndirect(document).getIndirectReference().makeIndirect(document).getIndirectReference().makeIndirect(document));
        PdfObject object = ((PdfIndirectReference)catalog.get(new PdfName("a"), false)).getRefersTo(true);
        Assert.assertTrue(object instanceof PdfDictionary);
        document.close();
    }

    @Test
    public void indirectsChain2() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = getTestPdfDictionary();
        PdfObject object = dictionary;
        for (int i = 0; i < 200; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), object);
        ((PdfIndirectReference)catalog.get(new PdfName("a"))).getRefersTo(true);
        Assert.assertNotNull(((PdfIndirectReference) catalog.get(new PdfName("a"))).getRefersTo(true));
        document.close();
    }

    @Test
    public void indirectsChain3() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = getTestPdfDictionary();
        PdfObject object = dictionary;
        for (int i = 0; i < 31; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), object);
        object = catalog.get(new PdfName("a"), true);
        Assert.assertTrue(object instanceof PdfDictionary);
        Assert.assertEquals(new PdfName("c").toString(), ((PdfDictionary) object).get(new PdfName("b")).toString());
        document.close();
    }

    @Test
    public void indirectsChain4() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = getTestPdfDictionary();
        PdfObject object = dictionary;
        for (int i = 0; i < 31; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        PdfArray array = new PdfArray();
        array.add(object);
        catalog.put(new PdfName("a"), array);
        object = ((PdfArray)catalog.get(new PdfName("a"))).get(0, true);
        Assert.assertTrue(object instanceof PdfDictionary);
        Assert.assertEquals(new PdfName("c").toString(), ((PdfDictionary)object).get(new PdfName("b")).toString());
        document.close();
    }

    @Test
    public void pdfIndirectReferenceFlags(){
        PdfIndirectReference reference = new PdfIndirectReference(null, 1);
        reference.setState(PdfObject.FREE);
        reference.setState(PdfObject.READING);
        reference.setState(PdfObject.MODIFIED);

        Assert.assertEquals("Free", true, reference.checkState(PdfObject.FREE));
        Assert.assertEquals("Reading", true, reference.checkState(PdfObject.READING));
        Assert.assertEquals("Modified", true, reference.checkState(PdfObject.MODIFIED));
        Assert.assertEquals("Free|Reading|Modified", true,
                reference.checkState((byte)(PdfObject.FREE |PdfObject.MODIFIED |PdfObject.READING)));

        reference.clearState(PdfObject.FREE);

        Assert.assertEquals("Free", false, reference.checkState(PdfObject.FREE));
        Assert.assertEquals("Reading", true, reference.checkState(PdfObject.READING));
        Assert.assertEquals("Modified", true, reference.checkState(PdfObject.MODIFIED));
        Assert.assertEquals("Reading|Modified", true,
                reference.checkState((byte)(PdfObject.READING |PdfObject.MODIFIED)));
        Assert.assertEquals("Free|Reading|Modified", false,
                reference.checkState((byte)(PdfObject.FREE |PdfObject.READING |PdfObject.MODIFIED)));

        reference.clearState(PdfObject.READING);

        Assert.assertEquals("Free", false, reference.checkState(PdfObject.FREE));
        Assert.assertEquals("Reading", false, reference.checkState(PdfObject.READING));
        Assert.assertEquals("Modified", true, reference.checkState(PdfObject.MODIFIED));
        Assert.assertEquals("Free|Reading", false,
                reference.checkState((byte) (PdfObject.FREE | PdfObject.READING)));

        reference.clearState(PdfObject.MODIFIED);

        Assert.assertEquals("Free", false, reference.checkState(PdfObject.FREE));
        Assert.assertEquals("Reading", false, reference.checkState(PdfObject.READING));
        Assert.assertEquals("Modified", false, reference.checkState(PdfObject.MODIFIED));


        Assert.assertEquals("Is InUse", true, !reference.isFree());

        reference.setState(PdfObject.FREE);

        Assert.assertEquals("Not IsInUse", false, !reference.isFree());
    }

    @Test
    public void pdtIndirectReferenceLateInitializing1() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(baos));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();

        PdfIndirectReference indRef = document.createNextIndirectReference();
        catalog.put(new PdfName("Smth"), indRef);

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(new PdfName("A"), new PdfString("a"));

        dictionary.makeIndirect(document, indRef);

        document.close();


        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        document = new PdfDocument(new PdfReader(bais));

        PdfObject object = document.getCatalog().getPdfObject().get(new PdfName("Smth"));
        Assert.assertTrue(object instanceof PdfDictionary);
        dictionary = (PdfDictionary) object;
        PdfString a = (PdfString) dictionary.get(new PdfName("A"));
        Assert.assertTrue(a.getValue().equals("a"));

        document.close();
    }

    @Test
    public void pdtIndirectReferenceLateInitializing2() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfDocument document = new PdfDocument(new PdfWriter(baos));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();

        PdfIndirectReference indRef1 = document.createNextIndirectReference();
        PdfIndirectReference indRef2 = document.createNextIndirectReference();

        catalog.put(new PdfName("Smth1"), indRef1);
        catalog.put(new PdfName("Smth2"), indRef2);

        PdfArray array = new PdfArray();
        array.add(new PdfString("array string"));
        array.makeIndirect(document, indRef2);

        document.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        document = new PdfDocument(new PdfReader(bais));

        PdfDictionary catalogDict = document.getCatalog().getPdfObject();
        PdfObject object1 = catalogDict.get(new PdfName("Smth1"));
        PdfObject object2 = catalogDict.get(new PdfName("Smth2"));
        Assert.assertTrue(object1 instanceof PdfNull);
        Assert.assertTrue(object2 instanceof PdfArray);
    }

    @Test
    public void pdtIndirectReferenceLateInitializing3() throws IOException {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();

        PdfIndirectReference indRef1 = document.createNextIndirectReference();
        PdfIndirectReference indRef2 = document.createNextIndirectReference();

        PdfArray array = new PdfArray();
        catalog.put(new PdfName("array1"), array);
        PdfString string = new PdfString("array string");
        array.add(string);
        array.add(indRef1);
        array.add(indRef2);

        PdfDictionary dict = new PdfDictionary();
        dict.makeIndirect(document, indRef1);


        PdfArray arrayClone = (PdfArray) array.clone();

        PdfObject object0 = arrayClone.get(0, false);
        PdfObject object1 = arrayClone.get(1, false);
        PdfObject object2 = arrayClone.get(2, false);

        Assert.assertTrue(object0 instanceof PdfString);
        Assert.assertTrue(object1 instanceof PdfDictionary);
        Assert.assertTrue(object2 instanceof PdfNull);

        PdfString string1 = (PdfString)object0;
        Assert.assertTrue(string != string1);
        Assert.assertTrue(string.getValue().equals(string1.getValue()));

        PdfDictionary dict1 = (PdfDictionary) object1;
        Assert.assertTrue(dict1.getIndirectReference().getObjNumber() == dict.getIndirectReference().getObjNumber());
        Assert.assertTrue(dict1.getIndirectReference().getGenNumber() == dict.getIndirectReference().getGenNumber());
        Assert.assertTrue(dict1 == dict);

        document.close();
    }

    private static PdfDictionary getTestPdfDictionary() {
        HashMap<PdfName, PdfObject> tmpMap = new HashMap<PdfName, PdfObject>();
        tmpMap.put(new PdfName("b"), new PdfName("c"));
        return new PdfDictionary(tmpMap);
    }
}
