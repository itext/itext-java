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
package com.itextpdf.commons.actions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract class which defines events only for internal usage.
 */
public abstract class AbstractITextEvent implements IEvent {
    private static final String ONLY_FOR_INTERNAL_USE = "AbstractITextEvent is only for internal usage.";

    private static final Map<String, Object> INTERNAL_PACKAGES = new ConcurrentHashMap<>();

    static {
        registerNamespace(NamespaceConstant.ITEXT);
    }

    /**
     * Creates an instance of abstract iText event. Only for internal usage.
     */
    protected AbstractITextEvent() {
        boolean isUnknown = true;
        for (String namespace : INTERNAL_PACKAGES.keySet()) {
            if (this.getClass().getName().startsWith(namespace)) {
                isUnknown = false;
                break;
            }
        }
        if (isUnknown) {
            throw new UnsupportedOperationException(ONLY_FOR_INTERNAL_USE);
        }
    }

    static void registerNamespace(String namespace) {
        INTERNAL_PACKAGES.put(namespace + ".", new Object());
    }
}
