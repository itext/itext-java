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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfActionOcgStateTest extends ExtendedITextTest {
    @Test
    public void pdfActionOcgStateUsageTest() {
        PdfName stateName = PdfName.ON;

        PdfDictionary ocgDict1 = new PdfDictionary();
        ocgDict1.put(PdfName.Type, PdfName.OCG);
        ocgDict1.put(PdfName.Name, new PdfName("ocg1"));

        PdfDictionary ocgDict2 = new PdfDictionary();
        ocgDict2.put(PdfName.Type, PdfName.OCG);
        ocgDict2.put(PdfName.Name, new PdfName("ocg2"));

        List<PdfDictionary> dicts = new ArrayList<>();
        dicts.add(ocgDict1);
        dicts.add(ocgDict2);

        PdfActionOcgState ocgState = new PdfActionOcgState(stateName, dicts);

        Assertions.assertEquals(stateName, ocgState.getState());
        Assertions.assertEquals(dicts, ocgState.getOcgs());

        List<PdfObject> states = ocgState.getObjectList();
        Assertions.assertEquals(3, states.size());
        Assertions.assertEquals(stateName, states.get(0));
        Assertions.assertEquals(ocgDict1, states.get(1));
        Assertions.assertEquals(ocgDict2, states.get(2));
    }
}
