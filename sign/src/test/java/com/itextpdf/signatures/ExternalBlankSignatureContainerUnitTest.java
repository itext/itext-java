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
package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ExternalBlankSignatureContainerUnitTest extends ExtendedITextTest {

    @Test
    public void createBlankSignTest() throws GeneralSecurityException {
        IExternalSignatureContainer container = new ExternalBlankSignatureContainer(new PdfDictionary());
        byte[] blankSign = container.sign(new ByteArrayInputStream(new byte[] {1, 0, 32, 5}));
        Assertions.assertEquals(0, blankSign.length);
    }

    @Test
    public void modifySigningDictionarySignTest() throws GeneralSecurityException {
        PdfDictionary initDict = new PdfDictionary();
        initDict.put(new PdfName("test_key"), new PdfName("test_value"));
        IExternalSignatureContainer container = new ExternalBlankSignatureContainer(initDict);

        PdfDictionary addDict = new PdfDictionary();
        addDict.put(new PdfName("add_key"), new PdfName("add_value"));

        container.modifySigningDictionary(addDict);

        byte[] blankSign = container.sign(new ByteArrayInputStream(new byte[] {1, 0, 32, 5}));
        Assertions.assertEquals(0, blankSign.length);
    }
}
