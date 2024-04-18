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
package com.itextpdf.commons.actions.contexts;

import com.itextpdf.commons.actions.NamespaceConstant;
import com.itextpdf.commons.actions.ProductNameConstant;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The class that retrieves context of its invocation.
 */
public class ContextManager {

    private static final ContextManager INSTANCE;
    private final SortedMap<String, IContext> contextMappings = new TreeMap<>(new LengthComparator());

    static {
        ContextManager local = new ContextManager();
        local.registerGenericContext(NamespaceConstant.ITEXT_CORE_NAMESPACES,
                Collections.singleton(ProductNameConstant.ITEXT_CORE));

        local.registerGenericContext(Collections.singleton(NamespaceConstant.CORE_SIGN),
                Collections.singleton(ProductNameConstant.ITEXT_CORE_SIGN));

        local.registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_HTML),
                Collections.singleton(ProductNameConstant.PDF_HTML));

        local.registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_SWEEP),
                Collections.singleton(ProductNameConstant.PDF_SWEEP));

        local.registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_OCR_TESSERACT4),
                Collections.singleton(ProductNameConstant.PDF_OCR_TESSERACT4));

        INSTANCE = local;
    }

    ContextManager() {

    }

    /**
     * Gets the singleton instance of this class.
     *
     * @return the {@link ContextManager} instance
     */
    public static ContextManager getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the context associated with the passed class object.
     * The context is determined by class namespace.
     *
     * @param clazz the class for which the context will be determined.
     * @return the {@link IContext} associated with the class, or {@code null} if the class is unknown.
     */
    public IContext getContext(Class<?> clazz) {
        return clazz == null ? null : getContext(clazz.getName());
    }

    /**
     * Gets the context associated with the passed class object.
     * The context is determined by class namespace.
     *
     * @param className the class name with the namespace for which the context will be determined.
     * @return the {@link IContext} associated with the class, or {@code null} if the class is unknown.
     */
    public IContext getContext(String className) {
        return getNamespaceMapping(getRecognisedNamespace(className));
    }

    String getRecognisedNamespace(String className) {
        if (className != null) {
            String normalizedClassName = normalize(className);
            // If both "a" and "a.b" namespaces are registered,
            // iText should consider the context of "a.b" for an "a.b" event,
            // that's why the contexts are sorted by the length of the namespace
            for (String namespace : contextMappings.keySet()) {
                if (normalizedClassName.startsWith(namespace)) {
                    return namespace;
                }
            }
        }
        return null;
    }

    void unregisterContext(Collection<String> namespaces) {
        for (String namespace : namespaces) {
            contextMappings.remove(normalize(namespace));
        }
    }

    private IContext getNamespaceMapping(String namespace) {
        if (namespace != null) {
            return contextMappings.get(namespace);
        }
        return null;
    }

    void registerGenericContext(Collection<String> namespaces, Collection<String> products) {
        final GenericContext context = new GenericContext(products);
        for (String namespace : namespaces) {
            contextMappings.put(normalize(namespace), context);
        }
    }

    private static String normalize(String namespace) {
        // Conversion to lowercase is done to be compatible with possible changes in case of packages/namespaces
        return namespace.toLowerCase();
    }

    private static class LengthComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int lengthComparison = Integer.compare(o2.length(), o1.length());
            if (0 == lengthComparison) {
                return o1.compareTo(o2);
            } else {
                return lengthComparison;
            }
        }
    }
}
