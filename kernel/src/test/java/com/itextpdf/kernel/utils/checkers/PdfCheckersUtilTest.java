/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.utils.checkers;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfCheckersUtilTest extends ExtendedITextTest {

    @Test
    public void getFormFieldsEmptyArrayReturnsEmpty() {
        PdfArray input = new PdfArray();
        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getFormFieldsSingleFieldWithoutKids() {
        PdfDictionary field = new PdfDictionary();
        field.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("field1"));

        PdfArray input = new PdfArray();
        input.add(field);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(field, result.get(0));
    }

    @Test
    public void getFormFieldsMultipleFieldsWithoutKids() {
        PdfDictionary field1 = new PdfDictionary();
        field1.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("field1"));
        PdfDictionary field2 = new PdfDictionary();
        field2.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("field2"));

        PdfArray input = new PdfArray();
        input.add(field1);
        input.add(field2);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(field1, result.get(0));
        Assertions.assertEquals(field2, result.get(1));
    }

    @Test
    public void getFormFieldsSingleFieldWithOneKid() {
        PdfDictionary kid = new PdfDictionary();
        kid.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("kid1"));

        PdfArray kids = new PdfArray();
        kids.add(kid);

        PdfDictionary parent = new PdfDictionary();
        parent.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("parent"));
        parent.put(PdfName.Kids, kids);

        PdfArray input = new PdfArray();
        input.add(parent);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(parent, result.get(0));
        Assertions.assertEquals(kid, result.get(1));
    }

    @Test
    public void getFormFieldsNestedKids() {
        PdfDictionary grandchild = new PdfDictionary();
        grandchild.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("grandchild"));

        PdfArray grandchildArray = new PdfArray();
        grandchildArray.add(grandchild);

        PdfDictionary child = new PdfDictionary();
        child.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("child"));
        child.put(PdfName.Kids, grandchildArray);

        PdfArray childArray = new PdfArray();
        childArray.add(child);

        PdfDictionary root = new PdfDictionary();
        root.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("root"));
        root.put(PdfName.Kids, childArray);

        PdfArray input = new PdfArray();
        input.add(root);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(root, result.get(0));
        Assertions.assertEquals(child, result.get(1));
        Assertions.assertEquals(grandchild, result.get(2));
    }

    @Test
    public void getFormFieldsMultipleKidsAtSameLevel() {
        PdfDictionary kid1 = new PdfDictionary();
        kid1.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("kid1"));
        PdfDictionary kid2 = new PdfDictionary();
        kid2.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("kid2"));

        PdfArray kids = new PdfArray();
        kids.add(kid1);
        kids.add(kid2);

        PdfDictionary parent = new PdfDictionary();
        parent.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("parent"));
        parent.put(PdfName.Kids, kids);

        PdfArray input = new PdfArray();
        input.add(parent);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(parent, result.get(0));
        Assertions.assertEquals(kid1, result.get(1));
        Assertions.assertEquals(kid2, result.get(2));
    }

    @Test
    public void getFormFieldsMixedFieldsWithAndWithoutKids() {
        PdfDictionary kid = new PdfDictionary();
        kid.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("kid"));

        PdfArray kids = new PdfArray();
        kids.add(kid);

        PdfDictionary fieldWithKids = new PdfDictionary();
        fieldWithKids.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("fieldWithKids"));
        fieldWithKids.put(PdfName.Kids, kids);

        PdfDictionary fieldWithoutKids = new PdfDictionary();
        fieldWithoutKids.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("fieldWithoutKids"));

        PdfArray input = new PdfArray();
        input.add(fieldWithKids);
        input.add(fieldWithoutKids);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(fieldWithKids, result.get(0));
        Assertions.assertEquals(kid, result.get(1));
        Assertions.assertEquals(fieldWithoutKids, result.get(2));
    }

    @Test
    public void getFormFieldsFieldWithEmptyKidsArray() {
        PdfDictionary field = new PdfDictionary();
        field.put(PdfName.T, new com.itextpdf.kernel.pdf.PdfString("field"));
        field.put(PdfName.Kids, new PdfArray());

        PdfArray input = new PdfArray();
        input.add(field);

        PdfArray result = PdfCheckersUtil.getFormFields(input);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(field, result.get(0));
    }
}