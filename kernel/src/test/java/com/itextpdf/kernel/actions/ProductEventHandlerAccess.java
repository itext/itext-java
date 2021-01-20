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

import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.sequence.SequenceId;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductEventHandlerAccess implements Closeable {
    private Set<String> registeredProducts = new HashSet<>();

    public ITextProductEventProcessor addProcessor(String productName, ITextProductEventProcessor processor) {
        registeredProducts.add(productName);
        return ProductEventHandler.INSTANCE.addProcessor(productName, processor);
    }

    public ITextProductEventProcessor removeProcessor(String productName) {
        return ProductEventHandler.INSTANCE.removeProcessor(productName);
    }

    public ITextProductEventProcessor getProcessor(String productName) {
        return ProductEventHandler.INSTANCE.getProcessor(productName);
    }

    public List<AbstractITextProductEvent> getEvents(SequenceId id) {
        return ProductEventHandler.INSTANCE.getEvents(id);
    }

    public void addEvent(SequenceId id, AbstractITextProductEvent event) {
        ProductEventHandler.INSTANCE.addEvent(id, event);
    }

    @Override
    public void close() throws IOException {
        for (String product: registeredProducts) {
            removeProcessor(product);
        }
    }
}
