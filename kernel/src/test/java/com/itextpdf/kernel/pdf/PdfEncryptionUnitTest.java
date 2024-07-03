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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfEncryptionUnitTest extends ExtendedITextTest {
    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentCorrectEntryTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assertions.assertTrue(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectEffTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.Identity);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assertions.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectStmFTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assertions.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectStrFTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.StdCF);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.StdCF, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assertions.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void readEncryptEmbeddedFilesOnlyFromPdfDocumentIncorrectCfTest() {
        PdfDictionary cryptDictionary = new PdfDictionary();
        cryptDictionary.put(PdfName.EFF, PdfName.StdCF);
        cryptDictionary.put(PdfName.StmF, PdfName.Identity);
        cryptDictionary.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary cfDictionary = new PdfDictionary();
        cfDictionary.put(PdfName.DefaultCryptFilter, new PdfDictionary());
        cryptDictionary.put(PdfName.CF, cfDictionary);

        Assertions.assertFalse(PdfEncryption.readEmbeddedFilesOnlyFromEncryptDictionary(cryptDictionary));
    }

    @Test
    public void createIdNormalLength(){
        byte[] originalId = new byte[]{0x33, 0x39, 0x62, 0x38, 0x65, 0x61, 0x30, 0x33, 0x65, 0x32, 0x39, 0x31, 0x38,
                0x32, 0x66, 0x31, 0x39, 0x63, 0x62, 0x65, 0x64, 0x32, 0x33, 0x37, 0x37, 0x33, 0x61, 0x63, 0x39, 0x65,
                0x34, 0x39};
        byte[] modifiedId = new byte[]{0x08, 0x27, 0x04, 0x30, 0x20, 0x42, 0x22, 0x6A, 0x5F, 0x40, 0x56, 0x57,
                0x44, 0x4A, 0x6E, 0x1C, 0x18, 0x76, 0x71, (byte) 0x80, 0x37, (byte) 0x80, 0x71, 0x5C, 0x68, 0x7E, 0x35, 0x41,
                0x76, 0x30, (byte) 0x83, 0x49, 0x2F, 0x07, 0x61, (byte) 0xBC, (byte) 0xBA, 0x04, 0x37, 0x1B, 0x0E,
                (byte) 0x80, 0x34, 0x30, 0x4A, 0x3B, 0x0E, 0x27, 0x4F, 0x01, 0x1D, 0x36, 0x71, 0x7A, 0x42, 0x2B, 0x2C,
                0x14, 0x6A, 0x52, 0x07, 0x1E, 0x4C};
        PdfObject fileId = PdfEncryption.createInfoId(originalId,modifiedId);
        PdfObject expectedFileId = new PdfLiteral("[<3339623865613033653239313832663139636265643233373733616" +
                "339653439><082704302042226a5f405657444a6e1c187671803780715c687e3541763083492f0761bcba04371b0e80343" +
                "04a3b0e274f011d36717a422b2c146a52071e4c>]");

        Assertions.assertEquals(expectedFileId,fileId);
    }

    @Test
    public void createIdNormalLengthWithoutPreserveEncryption(){
        byte[] originalId = new byte[]{(byte) 0xEB, (byte) 0xE3, (byte) 0x89, (byte) 0xDD, 0x5C, (byte) 0xB0, 0x77,
                0x59, (byte) 0xED, (byte) 0x9E, 0x17, 0x49, (byte) 0xFE, (byte) 0x8B, (byte) 0x93, (byte) 0xB2, 0x2F,
                0x6C, (byte) 0x9B, 0x37, 0x6C, 0x5D, 0x5C, (byte) 0x9A, 0x6C, 0x10, 0x56, 0x2A, (byte) 0xAB, 0x38,
                (byte) 0xFE, 0x1B, (byte) 0xB4, (byte) 0xA0, 0x0C, (byte) 0xAF, 0x3F, (byte) 0xED, (byte) 0x99, 0x03,
                (byte) 0x88, (byte) 0x9D, 0x05, 0x5F, (byte) 0xE1, 0x5C, (byte) 0x99, (byte) 0xC4, 0x30, 0x34, 0x5C,
                (byte) 0xEA, 0x68, (byte) 0x83, (byte) 0xD2, (byte) 0xEC, 0x14, 0x77, 0x52, (byte) 0xBE, (byte) 0xA5,
                0x04, 0x47, 0x07, 0x01};
        byte[] modifiedId = new byte[]{(byte) 0xF5, (byte) 0xBF, (byte) 0x98, 0x2C, 0x35, (byte) 0xAA, 0x0F,
                (byte) 0xFE, 0x0C, 0x4C, 0x50, (byte) 0xE5, 0x0D, (byte) 0x9F, 0x09, (byte) 0x8E, (byte) 0xFC,
                (byte) 0xE8, (byte) 0xB6, (byte) 0xBC, (byte) 0xA7, 0x14, (byte) 0xFE, 0x7B, 0x26, (byte) 0xAC,
                (byte) 0xC0, (byte) 0xEB, (byte) 0xDC, (byte) 0xCA, (byte) 0xF9, 0x09, 0x67, (byte) 0x8D, 0x25,
                (byte) 0xF9, 0x2A, (byte) 0x82, (byte) 0xD4, (byte) 0xA9, (byte) 0x83, (byte) 0xE3, 0x4F,
                (byte) 0x8F, 0x71, (byte) 0x97, (byte) 0x8F, (byte) 0x9A, 0x58, 0x10, 0x04, (byte) 0xEB, 0x67,
                (byte) 0xC0};
        PdfObject fileId = PdfEncryption.createInfoId(originalId,modifiedId,false);
        PdfObject expectedFileId = new PdfLiteral("[<ebe389dd5cb07759ed9e1749fe8b93b22f6c9b376c5d5c9a6c105" +
                "62aab38fe1bb4a00caf3fed9903889d055fe15c99c430345cea6883d2ec147752bea504470701><f5bf982c35aa0ffe0" +
                "c4c50e50d9f098efce8b6bca714fe7b26acc0ebdccaf909678d25f92a82d4a983e34f8f71978f9a581004eb67c0>]");

        Assertions.assertEquals(expectedFileId,fileId);
    }

    @Test
    public void createINormalLengthWithPreserveEncryption(){
        byte[] originalId = new byte[]{(byte) 0xB6, (byte) 0xE8, (byte) 0xF7, 0x65, 0x67, (byte) 0xD2, (byte) 0xC6,
                0x13, 0x07, 0x7E, 0x24, (byte) 0xB1, (byte) 0x94, (byte) 0xA9, (byte) 0xF6, 0x2E, 0x1F, 0x45,
                (byte) 0xF5, 0x75, (byte) 0xC2, (byte) 0xA1, 0x43, 0x02, 0x5A, 0x31, 0x7B, (byte) 0xBF, (byte) 0xD5,
                0x3B, (byte) 0x92, (byte) 0xC8, (byte) 0x93, (byte) 0xFA, (byte) 0xD3, 0x2D, (byte) 0x94, (byte) 0xA7,
                0x43, (byte) 0xA1, (byte) 0xE7, (byte) 0xAA, 0x6E, 0x75, (byte) 0xEC, (byte) 0xAF, (byte) 0xB7, 0x1A,
                (byte) 0xA1, 0x32, 0x04, (byte) 0xAB, 0x54, 0x58, (byte) 0xA1, 0x28, (byte) 0xB4, 0x39, 0x6E,
                (byte) 0x97, (byte) 0xCF, (byte) 0xD2, 0x20, 0x16, 0x5E};
        byte[] modifiedId = new byte[]{0x05, 0x2B, 0x7C, 0x61, 0x4A, (byte) 0xE8, 0x02, (byte) 0x8A, (byte) 0xF7, 0x4B,
                (byte) 0xD8, (byte) 0xFF, (byte) 0xE6, 0x38, (byte) 0xB7, (byte) 0x82, (byte) 0x84, (byte) 0x8A,
                (byte) 0xF5, (byte) 0x9C, (byte) 0xDD, (byte) 0xF6, 0x79, 0x38, 0x1A, (byte) 0xAD, (byte) 0xA8,
                (byte) 0x88, 0x23, (byte) 0xC0, (byte) 0xA3, (byte) 0x8C, 0x1B, 0x43, 0x28, 0x3B, 0x3A, 0x2A, 0x27,
                0x20, 0x19, (byte) 0xA8, 0x5E, (byte) 0xD3, (byte) 0xD6, (byte) 0xF5, 0x04, (byte) 0xFA, 0x5E, 0x14,
                (byte) 0xC1, (byte) 0xF9, (byte) 0xDE, 0x77, (byte) 0xC8, (byte) 0x93, 0x6D, (byte) 0xB3, (byte) 0xE9,
                (byte) 0xDF, (byte) 0x80, 0x5D, 0x21, 0x0D};
        PdfObject fileId = PdfEncryption.createInfoId(originalId,modifiedId,true);
        PdfObject expectedFileId = new PdfLiteral("[<b6e8f76567d2c613077e24b194a9f62e1f45f575c2a143025a317bbf" +
                "d53b92c893fad32d94a743a1e7aa6e75ecafb71aa13204ab5458a128b4396e97cfd220165e><052b7c614ae8028af74bd8" +
                "ffe638b782848af59cddf679381aada88823c0a38c1b43283b3a2a272019a85ed3d6f504fa5e14c1f9de77c8936db3e9df" +
                "805d210d>]");

        Assertions.assertEquals(expectedFileId,fileId);
    }

    @Test
    public void createIdShortLengthWithoutPreserveEncryption(){
        byte[] originalId = new byte[]{(byte) 0xE4, 0x04, (byte) 0xD5, 0x40, (byte) 0xF7, 0x09, 0x4B, (byte) 0xE1};
        byte[] modifiedId = new byte[]{(byte) 0xD3, (byte) 0xC4, (byte) 0xE2, (byte) 0x91, (byte) 0xBD, (byte) 0xFF,
                0x7B, (byte) 0xC2, (byte) 0xFB, 0x4B, 0x13, 0x3E};
        PdfObject fileId = PdfEncryption.createInfoId(originalId,modifiedId,false);
        PdfObject expectedFileId = new PdfLiteral("[<e404d540f7094be108090a0b0c0d0e0f><d3c4e291bdff7bc2fb4b" +
                "133e0c0d0e0f>]");

        Assertions.assertEquals(expectedFileId,fileId);
    }

    @Test
    public void createIdShortLengthWithPreserveEncryption(){
        byte[] originalId = new byte[]{0x3B, 0x0D, 0x7A, (byte) 0xED, (byte) 0xE4, (byte) 0xA3, 0x4B, (byte) 0xA6, 0x12,
                0x24, 0x0C, 0x65};
        byte[] modifiedId = new byte[]{0x4E, (byte) 0x84, 0x4F, (byte) 0xC2, (byte) 0x86, 0x50, 0x3A, 0x6C, (byte) 0x82,
                (byte) 0xDF, (byte) 0xAB, 0x7D, 0x16, (byte) 0x80, 0x75};
        PdfObject fileId = PdfEncryption.createInfoId(originalId,modifiedId,true);
        PdfObject expectedFileId = new PdfLiteral("[<3b0d7aede4a34ba612240c65><4e844fc286503a6c82dfab7d168075>]");

        Assertions.assertEquals(expectedFileId,fileId);
    }
}
