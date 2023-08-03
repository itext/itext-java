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
package com.itextpdf.commons.actions.contexts;

import com.itextpdf.commons.actions.NamespaceConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ContextManagerTest extends ExtendedITextTest {

    @Test
    public void getRecognisedNamespaceForSpecificNamespaceTest() {
        String outerNamespaces = NamespaceConstant.ITEXT.toLowerCase();
        String innerNamespaces = NamespaceConstant.PDF_HTML.toLowerCase();

        Assert.assertTrue(innerNamespaces.startsWith(outerNamespaces));

        ContextManager managerOuterBeforeInner = new ContextManager();
        managerOuterBeforeInner
                .registerGenericContext(Collections.singletonList(outerNamespaces), Collections.<String>emptyList());
        managerOuterBeforeInner
                .registerGenericContext(Collections.singletonList(innerNamespaces), Collections.<String>emptyList());

        Assert.assertEquals(outerNamespaces,
                managerOuterBeforeInner.getRecognisedNamespace(outerNamespaces));
        Assert.assertEquals(innerNamespaces,
                managerOuterBeforeInner.getRecognisedNamespace(innerNamespaces));

        ContextManager managerInnerBeforeOuter = new ContextManager();
        managerInnerBeforeOuter
                .registerGenericContext(Collections.singletonList(innerNamespaces), Collections.<String>emptyList());
        managerInnerBeforeOuter
                .registerGenericContext(Collections.singletonList(outerNamespaces), Collections.<String>emptyList());

        Assert.assertEquals(outerNamespaces,
                managerInnerBeforeOuter.getRecognisedNamespace(outerNamespaces));
        Assert.assertEquals(innerNamespaces,
                managerInnerBeforeOuter.getRecognisedNamespace(innerNamespaces));
    }

    @Test
    public void notRegisteredNamespaceTest() {
        String notRegisteredNamespace = "com.hello.world";

        Assert.assertNull(ContextManager.getInstance().getRecognisedNamespace(notRegisteredNamespace));
    }

    @Test
    public void unregisterNamespaceTest() {
        String testNamespace = "com.hello.world";
        String testNamespaceWithCapitals = "com.Bye.World";
        List<String> testNamespaces = Arrays.asList(
                testNamespace,
                testNamespaceWithCapitals
        );

        ContextManager manager = new ContextManager();
        Assert.assertNull(manager.getRecognisedNamespace(testNamespace));
        Assert.assertNull(manager.getRecognisedNamespace(testNamespaceWithCapitals));

        manager.registerGenericContext(testNamespaces, Arrays.asList("myProduct"));

        Assert.assertEquals(testNamespace,
                manager.getRecognisedNamespace(testNamespace + ".MyClass"));
        Assert.assertEquals(testNamespaceWithCapitals.toLowerCase(),
                manager.getRecognisedNamespace(testNamespaceWithCapitals + ".MyClass"));

        manager.unregisterContext(testNamespaces);

        Assert.assertNull(manager.getRecognisedNamespace(testNamespace));
        Assert.assertNull(manager.getRecognisedNamespace(testNamespaceWithCapitals));
    }

    @Test
    public void registeredNamespaceTest() {
        String registeredNamespace = NamespaceConstant.CORE_LAYOUT + "custompackage";

        Assert.assertEquals(NamespaceConstant.CORE_LAYOUT.toLowerCase(),
                ContextManager.getInstance().getRecognisedNamespace(registeredNamespace));
    }
}
