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
package com.itextpdf.commons.actions.contexts;

import com.itextpdf.commons.actions.NamespaceConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ContextManagerTest extends ExtendedITextTest {

    @Test
    public void getRecognisedNamespaceForSpecificNamespaceTest() {
        String outerNamespaces = NamespaceConstant.ITEXT.toLowerCase();
        String innerNamespaces = NamespaceConstant.PDF_HTML.toLowerCase();

        Assertions.assertTrue(innerNamespaces.startsWith(outerNamespaces));

        ContextManager managerOuterBeforeInner = new ContextManager();
        managerOuterBeforeInner
                .registerGenericContext(Collections.singletonList(outerNamespaces), Collections.<String>emptyList());
        managerOuterBeforeInner
                .registerGenericContext(Collections.singletonList(innerNamespaces), Collections.<String>emptyList());

        Assertions.assertEquals(outerNamespaces,
                managerOuterBeforeInner.getRecognisedNamespace(outerNamespaces));
        Assertions.assertEquals(innerNamespaces,
                managerOuterBeforeInner.getRecognisedNamespace(innerNamespaces));

        ContextManager managerInnerBeforeOuter = new ContextManager();
        managerInnerBeforeOuter
                .registerGenericContext(Collections.singletonList(innerNamespaces), Collections.<String>emptyList());
        managerInnerBeforeOuter
                .registerGenericContext(Collections.singletonList(outerNamespaces), Collections.<String>emptyList());

        Assertions.assertEquals(outerNamespaces,
                managerInnerBeforeOuter.getRecognisedNamespace(outerNamespaces));
        Assertions.assertEquals(innerNamespaces,
                managerInnerBeforeOuter.getRecognisedNamespace(innerNamespaces));
    }

    @Test
    public void notRegisteredNamespaceTest() {
        String notRegisteredNamespace = "com.hello.world";

        Assertions.assertNull(ContextManager.getInstance().getRecognisedNamespace(notRegisteredNamespace));
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
        Assertions.assertNull(manager.getRecognisedNamespace(testNamespace));
        Assertions.assertNull(manager.getRecognisedNamespace(testNamespaceWithCapitals));

        manager.registerGenericContext(testNamespaces, Arrays.asList("myProduct"));

        Assertions.assertEquals(testNamespace,
                manager.getRecognisedNamespace(testNamespace + ".MyClass"));
        Assertions.assertEquals(testNamespaceWithCapitals.toLowerCase(),
                manager.getRecognisedNamespace(testNamespaceWithCapitals + ".MyClass"));

        manager.unregisterContext(testNamespaces);

        Assertions.assertNull(manager.getRecognisedNamespace(testNamespace));
        Assertions.assertNull(manager.getRecognisedNamespace(testNamespaceWithCapitals));
    }

    @Test
    public void registeredNamespaceTest() {
        String registeredNamespace = NamespaceConstant.CORE_LAYOUT + "custompackage";

        Assertions.assertEquals(NamespaceConstant.CORE_LAYOUT.toLowerCase(),
                ContextManager.getInstance().getRecognisedNamespace(registeredNamespace));
    }
}
