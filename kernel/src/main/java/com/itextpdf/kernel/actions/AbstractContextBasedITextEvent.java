/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.actions;

import com.itextpdf.kernel.actions.data.ProductData;
import com.itextpdf.kernel.counter.event.IMetaInfo;

/**
 * Represents a context-based event. See also {@link AbstractContextBasedEventHandler}.
 * Only for internal usage.
 */
public abstract class AbstractContextBasedITextEvent extends AbstractProductITextEvent {
    private final IMetaInfo metaInfo;

    /**
     * Creates an event containing auxiliary meta data.
     *
     * @param productData is a description of the product which has generated an event
     * @param metaInfo is an auxiliary meta info
     */
    public AbstractContextBasedITextEvent(ProductData productData, IMetaInfo metaInfo) {
        super(productData);
        this.metaInfo = metaInfo;
    }

    /**
     * Obtains stored meta info associated with the event.
     *
     * @return meta info
     */
    public IMetaInfo getMetaInfo() {
        return metaInfo;
    }

    /**
     * Obtains a current event context class.
     *
     * @return context class
     */
    public Class<?> getClassFromContext() {
        return this.getClass();
    }
}
