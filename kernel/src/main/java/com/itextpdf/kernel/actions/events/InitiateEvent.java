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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.kernel.counter.event.IMetaInfo;

/**
 * The event identifies that product usage was started.
 */
public class InitiateEvent extends AbstractITextProductEvent {
    private static final String INITIATE_TYPE = "initiate-product-session-event";
    private final String productName;

    /**
     * Creates a new instance of the event.
     *
     * @param productName is a product name
     * @param metaInfo is an auxiliary meta data
     */
    public InitiateEvent(String productName, IMetaInfo metaInfo) {
        super(metaInfo);
        this.productName = productName;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return INITIATE_TYPE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getProductName() {
        return productName;
    }
}
