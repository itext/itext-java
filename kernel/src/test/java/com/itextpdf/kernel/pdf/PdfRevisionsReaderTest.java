/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfRevisionsReaderTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfRevisionsReaderTest/";

    @Test
    public void singleRevisionDocumentTest() throws IOException {
        String filename = SOURCE_FOLDER + "singleRevisionDocument.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(1, documentRevisions.size());

            DocumentRevision firstRevision = documentRevisions.get(0);
            assertResultingRevision(firstRevision, 1, 2, 3, 4, 5, 6);
            Assert.assertEquals(929, firstRevision.getEofOffset());
        }
    }

    @Test
    public void singleRevisionWithXrefStreamTest() throws IOException {
        String filename = SOURCE_FOLDER + "singleRevisionWithXrefStream.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(1, documentRevisions.size());

            DocumentRevision firstRevision = documentRevisions.get(0);
            assertResultingRevision(firstRevision, 1, 2, 3, 4, 5, 6, 7, 8);
            Assert.assertEquals(1085, firstRevision.getEofOffset());
        }
    }

    @Test
    public void multipleRevisionsDocument() throws IOException {
        String filename = SOURCE_FOLDER + "multipleRevisionsDocument.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(3, documentRevisions.size());

            DocumentRevision firstRevision = documentRevisions.get(0);
            assertResultingRevision(firstRevision, 1, 2, 3, 4, 5, 6);
            Assert.assertEquals(929, firstRevision.getEofOffset());

            DocumentRevision secondRevision = documentRevisions.get(1);
            assertResultingRevision(secondRevision, 1, 3, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15);
            Assert.assertEquals(28119, secondRevision.getEofOffset());

            DocumentRevision thirdRevision = documentRevisions.get(2);
            assertResultingRevision(thirdRevision, 1, 3, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28);
            Assert.assertEquals(36207, thirdRevision.getEofOffset());
        }
    }

    @Test
    public void freeReferencesDocument() throws IOException {
        String filename = SOURCE_FOLDER + "freeReferencesDocument.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(5, documentRevisions.size());

            DocumentRevision firstRevision = documentRevisions.get(0);
            assertResultingRevision(firstRevision, 1, 2, 3, 4, 5, 6);
            Assert.assertEquals(929, firstRevision.getEofOffset());

            DocumentRevision secondRevision = documentRevisions.get(1);
            assertResultingRevision(secondRevision, 1, 3, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15);
            Assert.assertEquals(28119, secondRevision.getEofOffset());

            DocumentRevision thirdRevision = documentRevisions.get(2);
            assertResultingRevision(thirdRevision, 1, 3, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28);
            Assert.assertEquals(36207, thirdRevision.getEofOffset());

            DocumentRevision fourthRevision = documentRevisions.get(3);
            assertResultingRevision(fourthRevision, new int[] {1, 3, 23, 24}, new int[] {0, 0, 1, 1});
            Assert.assertEquals(37006, fourthRevision.getEofOffset());

            DocumentRevision fifthRevision = documentRevisions.get(4);
            assertResultingRevision(fifthRevision, new int[] {1, 3, 19, 20, 21, 22, 23, 25},
                    new int[] {0, 0, 1, 1, 1, 1, 1, 1});
            Assert.assertEquals(38094, fifthRevision.getEofOffset());
        }
    }

    @Test
    public void multipleRevisionsWithXrefStreamTest() throws IOException {
        String filename = SOURCE_FOLDER + "multipleRevisionsWithXrefStream.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(3, documentRevisions.size());

            DocumentRevision firstRevision = documentRevisions.get(0);
            assertResultingRevision(firstRevision, 1, 2, 3, 4, 5, 6, 7, 8);
            Assert.assertEquals(1085, firstRevision.getEofOffset());

            DocumentRevision secondRevision = documentRevisions.get(1);
            assertResultingRevision(secondRevision, 1, 3, 4, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
            Assert.assertEquals(28137, secondRevision.getEofOffset());

            DocumentRevision thirdRevision = documentRevisions.get(2);
            assertResultingRevision(thirdRevision, 1, 3, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34);
            Assert.assertEquals(36059, thirdRevision.getEofOffset());
        }
    }

    @Test
    public void freeReferencesWithXrefStream() throws IOException {
        String filename = SOURCE_FOLDER + "freeReferencesWithXrefStream.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(5, documentRevisions.size());

            DocumentRevision firstRevision = documentRevisions.get(0);
            assertResultingRevision(firstRevision, 1, 2, 3, 4, 5, 6, 7, 8);
            Assert.assertEquals(1085, firstRevision.getEofOffset());

            DocumentRevision secondRevision = documentRevisions.get(1);
            assertResultingRevision(secondRevision, 1, 3, 4, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
            Assert.assertEquals(28137, secondRevision.getEofOffset());

            DocumentRevision thirdRevision = documentRevisions.get(2);
            assertResultingRevision(thirdRevision, 1, 3, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34);
            Assert.assertEquals(36059, thirdRevision.getEofOffset());

            DocumentRevision fourthRevision = documentRevisions.get(3);
            assertResultingRevision(fourthRevision, new int[] {1, 3, 27, 28, 35}, new int[] {0, 0, 1, 1, 0});
            Assert.assertEquals(36975, fourthRevision.getEofOffset());

            DocumentRevision fifthRevision = documentRevisions.get(4);
            assertResultingRevision(fifthRevision, new int[] {1, 3, 23, 24, 25, 26, 27, 29, 36},
                    new int[] {0, 0, 1, 1, 1, 1, 1, 1, 0});
            Assert.assertEquals(38111, fifthRevision.getEofOffset());
        }
    }

    @Test
    public void documentWithStreamAndTableXref() throws IOException {
        String filename = SOURCE_FOLDER + "documentWithStreamAndTableXref.pdf";

        try (PdfReader reader = new PdfReader(filename)) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(reader);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            Assert.assertEquals(3, documentRevisions.size());

            DocumentRevision thirdRevision = revisionsReader.getAllRevisions().get(0);
            // xref was broken in this revision and fixed in the next one
            assertResultingRevision(thirdRevision, new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                    new int[] {0, 0, 0, 0, 0, 0, 65535, 0, 0});
            Assert.assertEquals(1381, thirdRevision.getEofOffset());

            DocumentRevision secondRevision = revisionsReader.getAllRevisions().get(1);
            assertResultingRevision(secondRevision, 1, 2, 3, 4, 5, 6, 7, 8);
            Assert.assertEquals(1381, secondRevision.getEofOffset());

            DocumentRevision firstRevision = revisionsReader.getAllRevisions().get(2);
            assertResultingRevision(firstRevision);
            Assert.assertEquals(1550, firstRevision.getEofOffset());
        }
    }

    private void assertResultingRevision(DocumentRevision documentRevision, int... objNumbers) {
        assertResultingRevision(documentRevision, objNumbers, new int[objNumbers.length]);
    }

    private void assertResultingRevision(DocumentRevision documentRevision, int[] objNumbers, int[] objGens) {
        Assert.assertEquals(objNumbers.length, objGens.length);
        Assert.assertEquals(objNumbers.length + 1, documentRevision.getModifiedObjects().size());
        for (int i = 0; i < objNumbers.length; ++i) {
            int objNumber = objNumbers[i];
            int objGen = objGens[i];
            Assert.assertTrue(documentRevision.getModifiedObjects().stream().anyMatch(
                    reference -> reference.getObjNumber() == objNumber && reference.getGenNumber() == objGen));
        }
        Assert.assertTrue(documentRevision.getModifiedObjects().stream().anyMatch(
                reference -> reference.getObjNumber() == 0 && reference.getGenNumber() == 65535 && reference.isFree()));
    }
}
