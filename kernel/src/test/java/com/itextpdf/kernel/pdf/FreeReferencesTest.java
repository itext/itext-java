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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FreeReferencesTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/FreeReferencesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/FreeReferencesTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void freeReferencesTest01() throws IOException {
        String src = "freeRefsGapsAndMaxGen.pdf";
        String out = "freeReferencesTest01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                        "xref\n" +
                        "0 15\n" +
                        "0000000010 65535 f \n" +
                        "0000000269 00000 n \n" +
                        "0000000561 00000 n \n" +
                        "0000000314 00000 n \n" +
                        "0000000000 65535 f \n" +
                        "0000000006 00000 f \n" +
                        "0000000007 00000 f \n" +
                        "0000000008 00000 f \n" +
                        "0000000009 00000 f \n" +
                        "0000000000 00000 f \n" +
                        "0000000011 00000 f \n" +
                        "0000000005 00001 f \n" +
                        "0000000133 00000 n \n" +
                        "0000000015 00000 n \n" +
                        "0000000613 00000 n \n" };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeReferencesTest02() throws IOException {
        String src = "freeRefsGapsAndMaxGen.pdf";
        String out = "freeReferencesTest02.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                        "xref\n" +
                        "0 5\n" +
                        "0000000010 65535 f \n" +
                        "0000000269 00000 n \n" +
                        "0000000569 00000 n \n" +
                        "0000000314 00000 n \n" +
                        "0000000000 65535 f \n" +
                        "10 5\n" +
                        "0000000011 00000 f \n" + // Append mode, no possibility to fix subsections in first xref
                        "0000000000 00001 f \n" +
                        "0000000133 00000 n \n" +
                        "0000000015 00000 n \n" +
                        "0000000480 00000 n \n",

                        "xref\n" +
                        "3 1\n" +
                        "0000000995 00000 n \n"};
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeReferencesTest03() throws IOException {
        String src = "freeRefsDeletedObj.pdf";
        String out = "freeReferencesTest03.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());
        pdfDocument.addNewPage();

        // fix page content
        PdfStream firstPageContentStream = pdfDocument.getPage(1).getContentStream(0);
        String firstPageData = new String(firstPageContentStream.getBytes());
        firstPageContentStream.setData((firstPageData.substring(0, firstPageData.lastIndexOf("BT")) + "ET").getBytes());
        firstPageContentStream.setModified();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                        "xref\n" +
                        "0 7\n" +
                        "0000000000 65535 f \n" +
                        "0000000265 00000 n \n" +
                        "0000000564 00000 n \n" +
                        "0000000310 00000 n \n" +
                        "0000000132 00000 n \n" +
                        "0000000015 00001 n \n" +
                        "0000000476 00000 n \n",

                        "xref\n" +
                        "0 1\n" +
                        "0000000005 65535 f \n" +
                        "3 3\n" +
                        "0000000923 00000 n \n" +
                        "0000001170 00000 n \n" +
                        "0000000000 00002 f \n" +
                        "7 1\n" +
                        "0000001303 00000 n \n",

                        "xref\n" +
                        "1 3\n" +
                        "0000001706 00000 n \n" +
                        "0000001998 00000 n \n" +
                        "0000001751 00000 n \n" +
                        "7 3\n" +
                        "0000002055 00000 n \n" +
                        "0000002171 00000 n \n" +
                        "0000002272 00000 n \n"};
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeReferencesTest04() throws IOException {
        String src = "simpleDoc.pdf";
        String out = "freeReferencesTest04.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        PdfObject contentsObj = pdfDocument.getPage(1).getPdfObject().remove(PdfName.Contents);
        Assert.assertTrue(contentsObj instanceof PdfIndirectReference);

        PdfIndirectReference contentsRef = (PdfIndirectReference) contentsObj;
        contentsRef.setFree();
        PdfObject freedContentsRefRefersTo = contentsRef.getRefersTo();
        Assert.assertNull(freedContentsRefRefersTo);
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 7\n" +
                "0000000005 65535 f \n" +
                "0000000133 00000 n \n" +
                "0000000425 00000 n \n" +
                "0000000178 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000000 00001 f \n" +
                "0000000476 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeReferencesTest05() throws IOException {
        String src = "simpleDocWithSubsections.pdf";
        String out = "freeReferencesTest05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 14\n" +
                "0000000004 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000005 00000 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000010 00000 f \n" +
                "0000000000 00000 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n"
        };
        compareXrefTables(xrefString, expected);

    }

    @Test
    public void freeReferencesTest06() throws IOException {
        String src = "simpleDocWithSubsections.pdf";
        String out = "freeReferencesTest06.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                        "xref\n" +
                        "0 4\n" +
                        "0000000000 65535 f \n" +
                        "0000000269 00000 n \n" +
                        "0000000569 00000 n \n" +
                        "0000000314 00000 n \n" +
                        "11 3\n" +
                        "0000000133 00000 n \n" + // Append mode, no possibility to fix subsections in first xref
                        "0000000015 00000 n \n" +
                        "0000000480 00000 n \n",

                        "xref\n" +
                        "3 1\n" +
                        "0000000935 00000 n \n"
                        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeReferencesTest07() throws IOException {
        String out = "freeReferencesTest07.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + out));

        pdfDocument.createNextIndirectReference();

        pdfDocument.addNewPage();
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                        "xref\n" +
                        "0 7\n" +
                        "0000000004 65535 f \n" +
                        "0000000203 00000 n \n" +
                        "0000000414 00000 n \n" +
                        "0000000248 00000 n \n" +
                        "0000000000 00001 f \n" +
                        "0000000088 00000 n \n" +
                        "0000000015 00000 n \n"
                        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeReferencesTest08() throws IOException {
        String src = "simpleDoc.pdf";
        String out = "freeReferencesTest08.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());
        PdfObject contentsObj = pdfDocument.getPage(1).getPdfObject().remove(PdfName.Contents);
        pdfDocument.getPage(1).setModified();
        Assert.assertTrue(contentsObj instanceof PdfIndirectReference);

        PdfIndirectReference contentsRef = (PdfIndirectReference) contentsObj;
        contentsRef.setFree();
        PdfObject freedContentsRefRefersTo = contentsRef.getRefersTo();
        Assert.assertNull(freedContentsRefRefersTo);
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 7\n" +
                "0000000000 65535 f \n" +
                "0000000265 00000 n \n" +
                "0000000564 00000 n \n" +
                "0000000310 00000 n \n" +
                "0000000132 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000476 00000 n \n",

                "xref\n" +
                "0 1\n" +
                "0000000005 65535 f \n" +
                "3 3\n" +
                "0000000923 00000 n \n" +
                "0000001170 00000 n \n" +
                "0000000000 00001 f \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ALREADY_FLUSHED_INDIRECT_OBJECT_MADE_FREE))
    public void freeARefInWrongWayTest01() throws IOException {
        String out = "freeARefInWrongWayTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + out));

        pdfDocument.addNewPage();
        PdfDictionary catalogDict = pdfDocument.getCatalog().getPdfObject();

        String outerString = "Outer array. Contains inner array at both 0 and 1 index. At 0 - as pdf object, at 1 - as in ref.";
        String innerString = "Inner array.";
        String description = "Inner array first flushed, then it's ref is made free.";
        PdfArray a1 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        PdfArray a2 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        a1.add(a2);
        a1.add(a2.getIndirectReference());
        a1.add(new PdfString(outerString));
        a1.add(new PdfString(description));
        a2.add(new PdfString(innerString));

        catalogDict.put(new PdfName("TestArray"), a1);

        a2.flush();
        a2.getIndirectReference().setFree();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 8\n" +
                "0000000000 65535 f \n" +
                "0000000235 00000 n \n" +
                "0000000462 00000 n \n" +
                "0000000296 00000 n \n" +
                "0000000120 00000 n \n" +
                "0000000047 00000 n \n" +
                "0000000513 00000 n \n" +
                "0000000015 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE, count = 2))
    public void freeARefInWrongWayTest02() throws IOException {
        String out = "freeARefInWrongWayTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + out));

        pdfDocument.addNewPage();
        PdfDictionary catalogDict = pdfDocument.getCatalog().getPdfObject();

        String outerString = "Outer array. Contains inner array at both 0 and 1 index. At 0 - as pdf object, at 1 - as in ref.";
        String innerString = "Inner array.";
        String description = "Inner array ref made free, then outer array is flushed.";
        PdfArray a1 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        PdfArray a2 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        a1.add(a2);
        a1.add(a2.getIndirectReference());
        a1.add(new PdfString(outerString));
        a1.add(new PdfString(description));
        a2.add(new PdfString(innerString));

        catalogDict.put(new PdfName("TestArray"), a1);

        a2.getIndirectReference().setFree();

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        Assert.assertTrue(a1.get(1, false) instanceof PdfIndirectReference);
        Assert.assertTrue(((PdfIndirectReference)a1.get(1, false)).isFree());
        a1.flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 9\n" +
                "0000000007 65535 f \n" +
                "0000000432 00000 n \n" +
                "0000000659 00000 n \n" +
                "0000000493 00000 n \n" +
                "0000000317 00000 n \n" +
                "0000000244 00000 n \n" +
                "0000000060 00000 n \n" +
                "0000000000 00001 f \n" +
                "0000000015 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INDIRECT_REFERENCE_USED_IN_FLUSHED_OBJECT_MADE_FREE))
    public void freeARefInWrongWayTest03() throws IOException {
        String out = "freeARefInWrongWayTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + out));

        pdfDocument.addNewPage();
        PdfDictionary catalogDict = pdfDocument.getCatalog().getPdfObject();

        String outerString = "Outer array. Contains inner array at both 0 and 1 index. At 0 - as pdf object, at 1 - as in ref.";
        String innerString = "Inner array.";
        String description = "Outer array is flushed, then inner array ref made free.";
        PdfArray a1 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        PdfArray a2 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        a1.add(a2);
        a1.add(a2.getIndirectReference());
        a1.add(new PdfString(outerString));
        a1.add(new PdfString(description));
        a2.add(new PdfString(innerString));

        catalogDict.put(new PdfName("TestArray"), a1);

        a1.flush();
        a2.getIndirectReference().setFree();

        Assert.assertFalse(a2.getIndirectReference().isFree());

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 9\n" +
                "0000000000 65535 f \n" +
                "0000000431 00000 n \n" +
                "0000000658 00000 n \n" +
                "0000000492 00000 n \n" +
                "0000000316 00000 n \n" +
                "0000000243 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000709 00000 n \n" +
                "0000000201 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ALREADY_FLUSHED_INDIRECT_OBJECT_MADE_FREE))
    public void freeARefInWrongWayTest04() throws IOException {
        String out = "freeARefInWrongWayTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + out));

        pdfDocument.addNewPage();
        PdfDictionary catalogDict = pdfDocument.getCatalog().getPdfObject();

        String outerString = "Outer array. Contains inner array at both 0 and 1 index. At 0 - as pdf object, at 1 - as in ref.";
        String innerString = "Inner array.";
        String description = "Outer array is flushed, then inner array ref made free.";
        PdfArray a1 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        PdfArray a2 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        a1.add(a2);
        a1.add(a2.getIndirectReference());
        a1.add(new PdfString(outerString));
        a1.add(new PdfString(description));
        a2.add(new PdfString(innerString));

        catalogDict.put(new PdfName("TestArray"), a1);

        a1.flush();
        a2.flush();
        a2.getIndirectReference().setFree();

        Assert.assertFalse(a2.getIndirectReference().isFree());

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 9\n" +
                "0000000000 65535 f \n" +
                "0000000431 00000 n \n" +
                "0000000658 00000 n \n" +
                "0000000492 00000 n \n" +
                "0000000316 00000 n \n" +
                "0000000243 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000709 00000 n \n" +
                "0000000201 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsAtEndOfXref01() throws IOException {
        String out = "freeRefsAtEndOfXref01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + out));

        pdfDocument.addNewPage();
        PdfDictionary catalogDict = pdfDocument.getCatalog().getPdfObject();

        String outerString = "Outer array. Contains inner array at both 0 and 1 index. At 0 - as pdf object, at 1 - as ind ref.";
        String innerString = "Inner array.";
        String description = "Last entry in the document xref table is free";
        PdfArray a1 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        PdfArray a2 = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        a1.add(a2);
        a1.add(a2.getIndirectReference());
        a1.add(new PdfString(outerString));
        a1.add(new PdfString(description));
        a2.add(new PdfString(innerString));

        catalogDict.put(new PdfName("TestArray"), a1);

        new PdfArray()
                .makeIndirect(pdfDocument)
                .getIndirectReference()
                .setFree();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 8\n" +
                "0000000000 65535 f \n" +
                "0000000203 00000 n \n" +
                "0000000430 00000 n \n" +
                "0000000264 00000 n \n" +
                "0000000088 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000481 00000 n \n" +
                "0000000658 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsAtEndOfXref02() throws IOException {
        String src = "lastXrefEntryFree.pdf";
        String out = "freeRefsAtEndOfXref02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 8\n" +
                "0000000000 65535 f \n" +
                "0000000203 00000 n \n" +
                "0000000511 00000 n \n" +
                "0000000264 00000 n \n" +
                "0000000088 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000562 00000 n \n" +
                "0000000739 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsAtEndOfXref03() throws IOException {
        String src = "lastXrefEntryFree.pdf";
        String out = "freeRefsAtEndOfXref03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        new PdfArray()
                .makeIndirect(pdfDocument)
                .getIndirectReference()
                .setFree();

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 11\n" +
                "0000000008 65535 f \n" +
                "0000000246 00000 n \n" +
                "0000000554 00000 n \n" +
                "0000000307 00000 n \n" +
                "0000000131 00000 n \n" +
                "0000000058 00000 n \n" +
                "0000000605 00000 n \n" +
                "0000000782 00000 n \n" +
                "0000000009 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000015 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsAtEndOfXref04() throws IOException {
        String src = "lastXrefEntryFree.pdf";
        String out = "freeRefsAtEndOfXref04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 9\n" +
                "0000000008 65535 f \n" +
                "0000000203 00000 n \n" +
                "0000000430 00000 n \n" +
                "0000000264 00000 n \n" +
                "0000000088 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000481 00000 n \n" +
                "0000000658 00000 n \n" +
                "0000000000 00001 f \n",

                "xref\n" +
                "3 1\n" +
                "0000001038 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsAtEndOfXref05() throws IOException {
        String src = "lastXrefEntryFree.pdf";
        String out = "freeRefsAtEndOfXref05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        new PdfArray()
                .makeIndirect(pdfDocument)
                .getIndirectReference()
                .setFree();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 9\n" +
                "0000000008 65535 f \n" +
                "0000000203 00000 n \n" +
                "0000000430 00000 n \n" +
                "0000000264 00000 n \n" +
                "0000000088 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000481 00000 n \n" +
                "0000000658 00000 n \n" +
                "0000000000 00001 f \n",

                "xref\n" +
                "3 1\n" +
                "0000001038 00000 n \n" +
                "8 2\n" +
                "0000000009 00001 f \n" +
                "0000000000 00001 f \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsAtEndOfXref06() throws IOException {
        String src = "lastXrefEntryFree.pdf";
        String out = "freeRefsAtEndOfXref06.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        new PdfArray()
                .makeIndirect(pdfDocument)
                .getIndirectReference()
                .setFree();

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 9\n" +
                "0000000008 65535 f \n" +
                "0000000203 00000 n \n" +
                "0000000430 00000 n \n" +
                "0000000264 00000 n \n" +
                "0000000088 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000481 00000 n \n" +
                "0000000658 00000 n \n" +
                "0000000000 00001 f \n",

                "xref\n" +
                "3 1\n" +
                "0000001081 00000 n \n" +
                "8 3\n" +
                "0000000009 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000001038 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void notUsedIndRef01() throws IOException {
        String src = "freeRefsDeletedObj.pdf";
        String out = "notUsedIndRef01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        pdfDocument.setFlushUnusedObjects(true);

        PdfIndirectReference newIndRef1 = pdfDocument.createNextIndirectReference();
        PdfIndirectReference newIndRef2 = pdfDocument.createNextIndirectReference();

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 11\n" +
                "0000000005 65535 f \n" +
                "0000000308 00000 n \n" +
                "0000000600 00000 n \n" +
                "0000000353 00000 n \n" +
                "0000000175 00000 n \n" +
                "0000000008 00002 f \n" +
                "0000000651 00000 n \n" +
                "0000000058 00000 n \n" +
                "0000000009 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000015 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void notUsedIndRef02() throws IOException {
        String src = "freeRefsDeletedObj.pdf";
        String out = "notUsedIndRef02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        pdfDocument.setFlushUnusedObjects(false);

        PdfIndirectReference newIndRef1 = pdfDocument.createNextIndirectReference();
        PdfIndirectReference newIndRef2 = pdfDocument.createNextIndirectReference();

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 11\n" +
                "0000000005 65535 f \n" +
                "0000000308 00000 n \n" +
                "0000000600 00000 n \n" +
                "0000000353 00000 n \n" +
                "0000000175 00000 n \n" +
                "0000000008 00002 f \n" +
                "0000000651 00000 n \n" +
                "0000000058 00000 n \n" +
                "0000000009 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000015 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void notUsedIndRef03() throws IOException {
        String src = "freeRefsDeletedObj.pdf";
        String out = "notUsedIndRef03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        PdfIndirectReference newIndRef1 = pdfDocument.createNextIndirectReference();
        PdfIndirectReference newIndRef2 = pdfDocument.createNextIndirectReference();

        List<PdfObject> objects = Arrays.asList(new PdfObject[]{new PdfString("The answer to life is "), new PdfNumber(42)});
        new PdfArray(objects)
                .makeIndirect(pdfDocument)
                .flush();

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 7\n" +
                "0000000000 65535 f \n" +
                "0000000265 00000 n \n" +
                "0000000564 00000 n \n" +
                "0000000310 00000 n \n" +
                "0000000132 00000 n \n" +
                "0000000015 00001 n \n" +
                "0000000476 00000 n \n",

                "xref\n" +
                "0 1\n" +
                "0000000005 65535 f \n" +
                "3 3\n" +
                "0000000923 00000 n \n" +
                "0000001170 00000 n \n" +
                "0000000000 00002 f \n" +
                "7 1\n" +
                "0000001303 00000 n \n",

                "xref\n" +
                "3 1\n" +
                "0000001749 00000 n \n" +
                "5 1\n" +
                "0000000008 00002 f \n" +
                "8 3\n" +
                "0000000009 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000001706 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void corruptedDocIndRefToFree01() throws IOException {
        String src = "corruptedDocIndRefToFree.pdf";
        String out = "corruptedDocIndRefToFree01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        pdfDocument.close();


        pdfDocument = new PdfDocument(new PdfReader(destinationFolder + out));
        PdfObject contentsObj = pdfDocument.getPage(1).getPdfObject().get(PdfName.Contents);
        Assert.assertEquals(PdfNull.PDF_NULL, contentsObj);
        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 7\n" +
                "0000000005 65535 f \n" +
                "0000000147 00000 n \n" +
                "0000000439 00000 n \n" +
                "0000000192 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000000 00001 f \n" +
                "0000000490 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling01() throws IOException {
        String src = "invalidFreeRefsList01.pdf";
        String out = "invalidFreeRefsListHandling01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 15\n" +
                "0000000010 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00000 f \n" +
                "0000000000 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling02() throws IOException {
        String src = "invalidFreeRefsList02.pdf";
        String out = "invalidFreeRefsListHandling02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.setFlushUnusedObjects(true);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000702 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling03() throws IOException {
        String src = "invalidFreeRefsList03.pdf";
        String out = "invalidFreeRefsListHandling03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.setFlushUnusedObjects(true);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000702 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling04() throws IOException {
        String src = "invalidFreeRefsList04.pdf";
        String out = "invalidFreeRefsListHandling04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.setFlushUnusedObjects(true);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000006 65535 f \n" +
                "0000000004 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000702 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling05() throws IOException {
        String src = "invalidFreeRefsList05.pdf";
        String out = "invalidFreeRefsListHandling05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.setFlushUnusedObjects(true);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000005 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000010 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000015 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000702 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling06() throws IOException {
        String src = "invalidFreeRefsList06.pdf";
        String out = "invalidFreeRefsListHandling06.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.setFlushUnusedObjects(true);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000702 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling07() throws IOException {
        String src = "invalidFreeRefsList07.pdf";
        String out = "invalidFreeRefsListHandling07.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        pdfDocument.setFlushUnusedObjects(true);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000004 00001 f \n" +
                "0000000702 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling08() throws IOException {
        String src = "invalidFreeRefsList08.pdf";
        String out = "invalidFreeRefsListHandling08.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000315 00000 n \n" +
                "0000000607 00000 n \n" +
                "0000000360 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000179 00000 n \n" +
                "0000000061 00000 n \n" +
                "0000000659 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000002 00001 f \n" +
                "0000000015 00000 n \n",

                "xref\n" +
                "3 1\n" +
                "0000001278 00000 n \n" +
                "16 1\n" +
                "0000000000 00001 f \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling09() throws IOException {
        String src = "invalidFreeRefsList09.pdf";
        String out = "invalidFreeRefsListHandling09.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000315 00000 n \n" +
                "0000000607 00000 n \n" +
                "0000000360 00000 n \n" +
                "0009999999 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000179 00000 n \n" +
                "0000000061 00000 n \n" +
                "0000000659 00000 n \n" +
                "0000999999 00001 f \n" +
                "0000000000 00001 f \n" +
                "0000000015 00000 n \n",

                "xref\n" +
                "3 2\n" +
                "0000001278 00000 n \n" +
                "0000000016 65535 f \n" +
                "15 1\n" +
                "0000000004 00001 f \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void invalidFreeRefsListHandling10() throws IOException {
        String src = "invalidFreeRefsList10.pdf";
        String out = "invalidFreeRefsListHandling10.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out),
                new StampingProperties().useAppendMode());

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 18\n" +
                "0000000010 65535 f \n" +
                "0000000315 00000 n \n" +
                "0000000607 00000 n \n" +
                "0000000360 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000002 00000 f \n" +
                "0000000016 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000015 00000 f \n" +
                "0000000011 00000 f \n" +
                "0000000005 00001 f \n" +
                "0000000179 00000 n \n" +
                "0000000061 00000 n \n" +
                "0000000659 00000 n \n" +
                "0000000016 00001 f \n" +
                "0000000008 00001 f \n" +
                "0000000015 00000 n \n",

                "xref\n" +
                "3 1\n" +
                "0000001278 00000 n \n" +
                "6 2\n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "16 1\n" +
                "0000000000 00001 f \n"
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsXrefStream01() throws IOException {
        String src = "freeRefsGapsAndListSpecificOrder.pdf";
        String out1 = "freeRefsXrefStream01_xrefStream.pdf";
        String out2 = "freeRefsXrefStream01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src),
                new PdfWriter(destinationFolder + out1, new WriterProperties().setFullCompressionMode(true)));

        pdfDocument.close();

        pdfDocument = new PdfDocument(new PdfReader(destinationFolder + out1),
                new PdfWriter(destinationFolder + out2, new WriterProperties().setFullCompressionMode(false)));

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out2);
        String[] expected = new String[] {
                "xref\n" +
                "0 15\n" +
                "0000000011 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000561 00000 n \n" +
                "0000000314 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000000 00000 f \n" +
                "0000000005 00000 f \n" +
                "0000000010 00001 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000613 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    /**
     * Free refs reusing is disabled at the moment, however it might be valuable to keep an eye on such case,
     * in case something will change.
     */
    @Test
    public void freeRefsReusingTest01() throws IOException {
        String src = "simpleDoc.pdf";
        String out = "freeRefsReusingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        PdfString s = new PdfString("New indirect object in the document.");
        PdfArray newIndObj = (PdfArray) new PdfArray(Collections.<PdfObject>singletonList(s))
                .makeIndirect(pdfDocument);
        pdfDocument.getCatalog().put(new PdfName("TestKey"), newIndObj);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 8\n" +
                "0000000000 65535 f \n" +
                "0000000265 00000 n \n" +
                "0000000571 00000 n \n" +
                "0000000324 00000 n \n" +
                "0000000132 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000622 00000 n \n" +
                "0000000710 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    /**
     * Free refs reusing is disabled at the moment, however it might be valuable to keep an eye on such case,
     * in case something will change.
     */
    @Test
    public void freeRefsReusingTest02() throws IOException {
        String src = "simpleDocWithSubsections.pdf";
        String out = "freeRefsReusingTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        PdfString s = new PdfString("New indirect object in the document.");
        PdfArray newIndObj = (PdfArray) new PdfArray(Collections.<PdfObject>singletonList(s))
                .makeIndirect(pdfDocument);
        pdfDocument.getCatalog().put(new PdfName("TestKey"), newIndObj);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 15\n" +
                "0000000004 65535 f \n" +
                "0000000269 00000 n \n" +
                "0000000576 00000 n \n" +
                "0000000329 00000 n \n" +
                "0000000005 00000 f \n" +
                "0000000006 00000 f \n" +
                "0000000007 00000 f \n" +
                "0000000008 00000 f \n" +
                "0000000009 00000 f \n" +
                "0000000010 00000 f \n" +
                "0000000000 00000 f \n" +
                "0000000133 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000628 00000 n \n" +
                "0000000717 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    /**
     * Free refs reusing is disabled at the moment, however it might be valuable to keep an eye on such case,
     * in case something will change.
     */
    @Test
    public void freeRefsReusingTest03() throws IOException {
        String src = "simpleDocWithFreeList.pdf";
        String out = "freeRefsReusingTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        PdfString s = new PdfString("New indirect object in the document.");
        PdfArray newIndObj = (PdfArray) new PdfArray(Collections.<PdfObject>singletonList(s))
                .makeIndirect(pdfDocument);
        pdfDocument.getCatalog().put(new PdfName("TestKey"), newIndObj);
        pdfDocument.getCatalog().put(new PdfName("TestKey2"), pdfDocument.getPdfObject(10));

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 12\n" +
                "0000000009 65535 f \n" +
                "0000000265 00000 n \n" +
                "0000000588 00000 n \n" +
                "0000000341 00000 n \n" +
                "0000000132 00000 n \n" +
                "0000000000 00002 f \n" +
                "0000000639 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000005 00001 f \n" +
                "0000000008 00001 f \n" +
                "0000000727 00000 n \n" +
                "0000000773 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    /**
     * Free refs reusing is disabled at the moment, however it might be valuable to keep an eye on such cases,
     * if something will change in future.
     */
    @Test
    public void freeRefsReusingTest04() throws IOException {
        String src = "freeRefsMaxGenOnly.pdf";
        String out = "freeRefsReusingTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        PdfString s = new PdfString("New indirect object in the document.");
        PdfArray newIndObj = (PdfArray) new PdfArray(Collections.<PdfObject>singletonList(s))
                .makeIndirect(pdfDocument);
        pdfDocument.getCatalog().put(new PdfName("TestKey"), newIndObj);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 8\n" +
                "0000000005 65535 f \n" +
                "0000000133 00000 n \n" +
                "0000000439 00000 n \n" +
                "0000000192 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000000 65535 f \n" +
                "0000000490 00000 n \n" +
                "0000000578 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    /**
     * Free refs reusing is disabled at the moment, however it might be valuable to keep an eye on such case,
     * in case something will change.
     */
    @Test
    public void freeRefsReusingTest05() throws IOException {
        String src = "simpleDocWithFreeList.pdf";
        String out = "freeRefsReusingTest05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));

        PdfString s = new PdfString("New indirect object in the document.");
        PdfArray newIndObj = (PdfArray) new PdfArray(Collections.<PdfObject>singletonList(s))
                .makeIndirect(pdfDocument);
        newIndObj.getIndirectReference().setFree();

        pdfDocument.getCatalog().put(new PdfName("TestKey"), pdfDocument.getPdfObject(10));

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 11\n" +
                "0000000009 65535 f \n" +
                "0000000265 00000 n \n" +
                "0000000572 00000 n \n" +
                "0000000325 00000 n \n" +
                "0000000132 00000 n \n" +
                "0000000000 00002 f \n" +
                "0000000623 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000005 00001 f \n" +
                "0000000008 00001 f \n" +
                "0000000711 00000 n \n",
        };
        compareXrefTables(xrefString, expected);
    }

    @Test
    public void freeRefsReusingTest06() throws IOException {
        String src = "simpleDoc.pdf";
        String out = "freeRefsReusingTest06.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + src), new PdfWriter(destinationFolder + out));
        PdfObject contentsObj = pdfDocument.getPage(1).getPdfObject().remove(PdfName.Contents);
        Assert.assertTrue(contentsObj instanceof PdfIndirectReference);

        PdfIndirectReference contentsRef = (PdfIndirectReference) contentsObj;
        contentsRef.setFree();

        PdfString s = new PdfString("New indirect object in the document.");
        PdfArray newIndObj = (PdfArray) new PdfArray(Collections.<PdfObject>singletonList(s))
                .makeIndirect(pdfDocument);
        pdfDocument.getCatalog().put(new PdfName("TestKey"), newIndObj);

        pdfDocument.close();

        String[] xrefString = extractXrefTableAsStrings(out);
        String[] expected = new String[] {
                "xref\n" +
                "0 8\n" +
                "0000000005 65535 f \n" +
                "0000000133 00000 n \n" +
                "0000000439 00000 n \n" +
                "0000000192 00000 n \n" +
                "0000000015 00000 n \n" +
                "0000000000 00001 f \n" +
                "0000000490 00000 n \n" +
                "0000000578 00000 n \n"
        };
        compareXrefTables(xrefString, expected);
    }

    private void compareXrefTables(String[] xrefString, String[] expected) {
        Assert.assertEquals(expected.length, xrefString.length);
        for (int i = 0; i < xrefString.length; ++i) {
            if (!compareXrefSection(xrefString[i], expected[i])) {
                // XrefTables are different. Use Assert method in order to show differences gracefully.
                Assert.assertArrayEquals(expected, xrefString);
            }
        }
    }

    private boolean compareXrefSection(String xrefSection, String expectedSection) {
        String[] xrefEntries = xrefSection.split("\n");
        String[] expectedEntries = expectedSection.split("\n");
        if (xrefEntries.length != expectedEntries.length) {
            return false;
        }

        for (int i = 0; i < xrefEntries.length; ++i) {
            String actual = xrefEntries[i].trim();
            String expected = expectedEntries[i].trim();
            if (actual.endsWith("n")) {
                actual = actual.substring(10);
                expected = expected.substring(10);
            }
            if (!actual.equals(expected)) {
                return false;
            }
        }
        return true;
    }

    private String[] extractXrefTableAsStrings(String out) throws IOException {
        byte[] outPdfBytes = readFile(destinationFolder + out);
        String outPdfContent = new String(outPdfBytes, StandardCharsets.US_ASCII);
        String xrefStr = "\nxref";
        String trailerStr = "trailer";
        int xrefInd = outPdfContent.indexOf(xrefStr);
        int trailerInd = outPdfContent.indexOf(trailerStr);
        int lastXrefInd = outPdfContent.lastIndexOf(xrefStr);
        List<String> xrefs = new ArrayList<>();
        while (true) {
            xrefs.add(outPdfContent.substring(xrefInd + 1, trailerInd));
            if (xrefInd == lastXrefInd) {
                break;
            }
            xrefInd = outPdfContent.indexOf(xrefStr, xrefInd + 1);
            trailerInd = outPdfContent.indexOf(trailerStr, trailerInd + 1);
        }
        return xrefs.toArray(new String[xrefs.size()]);
    }
}
