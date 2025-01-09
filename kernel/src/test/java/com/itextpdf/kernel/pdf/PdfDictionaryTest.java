/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Tag("UnitTest")
public class PdfDictionaryTest extends ExtendedITextTest {

    @Test
    public void testEntrySet() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            Assertions.assertEquals(e.getKey().toString(), "/"+e.getValue());
            if (!nums.remove(Integer.valueOf(((PdfNumber)e.getValue()).intValue()))) {
                Assertions.fail("Element not found");
            }
        }
    }

    @Test
    public void testEntrySetContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            Assertions.assertTrue(dict.entrySet().contains(e));
        }
    }

    @Test
    public void testEntrySetRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Map.Entry<PdfName, PdfObject>> toRemove = new ArrayList<>();
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            toRemove.add(e);
        }
        for (Map.Entry<PdfName, PdfObject> e: toRemove) {
            Assertions.assertTrue(dict.entrySet().remove(e));
        }
        Assertions.assertEquals(0, dict.entrySet().size());
        Assertions.assertEquals(0, dict.values().size());
        Assertions.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRemove2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        PdfDictionary dict2 = new PdfDictionary();
        dict2.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict2.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict2.put(new PdfName("5"), new PdfNumber(5));
        dict2.put(new PdfName("6"), new PdfNumber(6));

        for (Map.Entry<PdfName, PdfObject> e: dict2.entrySet()) {
            Assertions.assertTrue(dict.entrySet().remove(e));
        }

        Assertions.assertEquals(0, dict.entrySet().size());
        Assertions.assertEquals(0, dict.values().size());
        Assertions.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRemoveAll() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Map.Entry<PdfName, PdfObject>> toRemove = new ArrayList<>();
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            toRemove.add(e);
        }

        dict.entrySet().removeAll(toRemove);
        Assertions.assertEquals(0, dict.entrySet().size());
        Assertions.assertEquals(0, dict.values().size());
        Assertions.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRemoveAll2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        PdfDictionary dict2 = new PdfDictionary();
        dict2.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict2.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict2.put(new PdfName("5"), new PdfNumber(5));
        dict2.put(new PdfName("6"), new PdfNumber(6));

        dict.entrySet().removeAll(dict2.entrySet());
        Assertions.assertEquals(0, dict.entrySet().size());
        Assertions.assertEquals(0, dict.values().size());
        Assertions.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRetainAll() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Map.Entry<PdfName, PdfObject>> toRemove = new ArrayList<>();
        int i = 0;
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            toRemove.add(e);
            if (i++ > 2) break;
        }

        dict.entrySet().retainAll(toRemove);
        Assertions.assertEquals(4, dict.entrySet().size());
        Assertions.assertEquals(4, dict.values().size());
        Assertions.assertEquals(4, dict.size());
    }

    @Test
    public void testEntrySetClear() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        dict.entrySet().clear();
        Assertions.assertEquals(0, dict.entrySet().size());
        Assertions.assertEquals(0, dict.values().size());
        Assertions.assertEquals(0, dict.size());
    }

    @Test
    public void testValues() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            Assertions.assertEquals(e.getKey().toString(), "/"+e.getValue());
            if (!nums.remove(Integer.valueOf(((PdfNumber)e.getValue()).intValue()))) {
                Assertions.fail("Element not found");
            }
        }
    }

    @Test
    public void testValuesContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        for (PdfObject v: dict.values()) {
            Assertions.assertTrue(dict.values().contains(v));
        }
    }

    @Test
    public void testValuesIndirectContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        Assertions.assertTrue(dict.values().contains(dict.get(new PdfName("1"), false)));
        Assertions.assertTrue(dict.values().contains(dict.get(new PdfName("2"), false)));
        Assertions.assertTrue(dict.values().contains(dict.get(new PdfName("3")).getIndirectReference()));
        Assertions.assertTrue(dict.values().contains(dict.get(new PdfName("4")).getIndirectReference()));
    }

    @Test
    public void testPdfNamesFetching() {
        byte[][] namesBytes = new byte[][] {
                // /#C3#9Cberschrift_1
                new byte[]{35, 67, 51, 35, 57, 67, 98, 101, 114, 115, 99, 104, 114, 105, 102, 116, 95, 49},
                // /#C3#9Cberschrift_2
                new byte[]{35, 67, 51, 35, 57, 67, 98, 101, 114, 115, 99, 104, 114, 105, 102, 116, 95, 50},
                // /Article
                new byte[]{65, 114, 116, 105, 99, 108, 101},
                // /Bildunterschrift
                new byte[]{66, 105, 108, 100, 117, 110, 116, 101, 114, 115, 99, 104, 114, 105, 102, 116},
                // /NormalParagraphStyle
                new byte[]{78, 111, 114, 109, 97, 108, 80, 97, 114, 97, 103, 114, 97, 112, 104, 83, 116, 121, 108, 101},
                // /Story
                new byte[]{83, 116, 111, 114, 121},
                // /TOC-1
                new byte[]{84, 79, 67, 45, 49,},
                // /TOC-2-2
                new byte[]{84, 79, 67, 45, 50, 45, 50,},
                // /TOC-Head
                new byte[]{84, 79, 67, 45, 72, 101, 97, 100,},
                // /Tabelle
                new byte[]{84, 97, 98, 101, 108, 108, 101,},
                // /Tabelle_Head
                new byte[]{84, 97, 98, 101, 108, 108, 101, 95, 72, 101, 97, 100,},
                // /Tabelle_fett
                new byte[]{84, 97, 98, 101, 108, 108, 101, 95, 102, 101, 116, 116,},
                // /Text_INFO
                new byte[]{84, 101, 120, 116, 95, 73, 78, 70, 79,},
                // /Text_Info_Head
                new byte[]{84, 101, 120, 116, 95, 73, 110, 102, 111, 95, 72, 101, 97, 100,},
                // /Textk#C3#B6rper
                new byte[]{84, 101, 120, 116, 107, 35, 67, 51, 35, 66, 54, 114, 112, 101, 114,},
                // /Textk#C3#B6rper-Erstzeile
                new byte[]{84, 101, 120, 116, 107, 35, 67, 51, 35, 66, 54, 114, 112, 101, 114, 45, 69, 114, 115, 116, 122, 101, 105, 108, 101,},
                // /Textk#C3#B6rper_Back
                new byte[]{84, 101, 120, 116, 107, 35, 67, 51, 35, 66, 54, 114, 112, 101, 114, 95, 66, 97, 99, 107,},
                // /_No_paragraph_style_
                new byte[]{95, 78, 111, 95, 112, 97, 114, 97, 103, 114, 97, 112, 104, 95, 115, 116, 121, 108, 101, 95}
        };
        boolean[] haveValue = new boolean[] {true, true, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false};
        List<PdfName> names = new ArrayList<>();
        for (int i = 0; i < namesBytes.length; i++) {
            byte[] b = namesBytes[i];
            PdfName n = new PdfName(b);
            names.add(n);
            if (haveValue[i]) {
                n.generateValue();
            }
        }

        PdfDictionary dict = new PdfDictionary();
        for (PdfName name : names) {
            dict.put(name, new PdfName("dummy"));
        }

        PdfName expectedToContain = new PdfName("Article");
        boolean found = false;
        for (PdfName pdfName : dict.keySet()) {
            found = pdfName.equals(expectedToContain);
            if (found) {
                break;
            }
        }
        Assertions.assertTrue(found);
        Assertions.assertTrue(dict.containsKey(expectedToContain));
    }
}
