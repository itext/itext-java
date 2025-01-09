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
package com.itextpdf.kernel.actions;

import com.itextpdf.commons.actions.AbstractITextConfigurationEvent;
import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.actions.sequence.SequenceId;

import java.io.Closeable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used for testing purposes to have an access to ProductEventHandler. Note
 * that work with it may access further tests because the state of ProductEventHandler is shared
 * across application. It is strongly recommended to call {@link ProductEventHandlerAccess#close()}
 * method to return ProductEventHandler to initial state.
 */
public class ProductEventHandlerAccess extends AbstractITextConfigurationEvent implements Closeable {
    private final Set<String> registeredProducts = new HashSet<>();

    public ITextProductEventProcessor publicAddProcessor(ITextProductEventProcessor processor) {
        registeredProducts.add(processor.getProductName());
        return super.addProcessor(processor);
    }

    public ITextProductEventProcessor publicRemoveProcessor(String productName) {
        return super.removeProcessor(productName);
    }

    public Map<String, ITextProductEventProcessor> publicGetProcessors() {
        return super.getProcessors();
    }

    public List<AbstractProductProcessITextEvent> publicGetEvents(SequenceId id) {
        return super.getEvents(id);
    }

    public void publicAddEvent(SequenceId id, AbstractProductProcessITextEvent event) {
        super.addEvent(id, event);
    }

    @Override
    protected void doAction() {
        throw new IllegalStateException();
    }

    @Override
    public void close() {
        for (String product: registeredProducts) {
            removeProcessor(product);
        }
    }
}
