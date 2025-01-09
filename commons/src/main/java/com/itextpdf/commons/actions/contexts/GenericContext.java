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

import com.itextpdf.commons.actions.AbstractContextBasedITextEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic context that allows {@link AbstractContextBasedITextEvent} based on the whitelist of supported events.
 */
public class GenericContext implements IContext {
    private final Set<String> supportedProducts = new HashSet<>();


    /**
     * Creates a Generic Context instance with support of provided namespaces and products.
     *
     * @param supportedProducts is a collection of supported products
     */
    public GenericContext(Collection<String> supportedProducts) {
        this.supportedProducts.addAll(supportedProducts);
    }

    /**
     * Checks if the source product of the event is supported by the context.
     *
     * @param event {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isAllowed(AbstractContextBasedITextEvent event) {
        return supportedProducts.contains(event.getProductName());
    }
}
