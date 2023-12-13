/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.counter;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ContextManagerTest extends ExtendedITextTest {

    @Test
    public void getRecognisedNamespaceForSpecificNamespaceTest() {
        String outerNamespaces = NamespaceConstant.PDF_OCR.toLowerCase();
        String innerNamespaces = NamespaceConstant.PDF_OCR_TESSERACT4.toLowerCase();

        // Since both NamespaceConstant.PDF_OCR and NamespaceConstant.PDF_OCR_TESSERACT4 are registered
        // and the latter one begins with the former, we should check that correct namespaces are
        // recognized for each of them
        Assert.assertTrue(innerNamespaces.startsWith(outerNamespaces));
        Assert.assertEquals(outerNamespaces,
                ContextManager.getInstance().getRecognisedNamespace(outerNamespaces));
        Assert.assertEquals(innerNamespaces,
                ContextManager.getInstance().getRecognisedNamespace(innerNamespaces));
    }

    @Test
    public void notRegisteredNamespaceTest() {
        String notRegisteredNamespace = "com.hello.world";

        Assert.assertEquals(null,
                ContextManager.getInstance().getRecognisedNamespace(notRegisteredNamespace));
    }
}
