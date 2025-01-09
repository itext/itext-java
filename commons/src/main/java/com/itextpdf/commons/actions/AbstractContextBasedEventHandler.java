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

import com.itextpdf.commons.actions.contexts.ContextManager;
import com.itextpdf.commons.actions.contexts.IContext;

/**
 * Base class for events handling depending on the context.
 */
public abstract class AbstractContextBasedEventHandler implements IEventHandler {
    private final IContext defaultContext;

    /**
     * Creates a new instance of the handler with the defined fallback for events within unknown
     * contexts.
     *
     * @param onUnknownContext is a fallback for events within unknown context
     */
    protected AbstractContextBasedEventHandler(IContext onUnknownContext) {
        super();
        this.defaultContext = onUnknownContext;
    }

    /**
     * Performs context validation and if event is allowed to be processed passes it to
     * {@link #onAcceptedEvent(AbstractContextBasedITextEvent)}.
     *
     * @param event to handle
     */
    public final void onEvent(IEvent event) {
        if (!(event instanceof AbstractContextBasedITextEvent)) {
            return;
        }

        IContext context = null;
        final AbstractContextBasedITextEvent iTextEvent = (AbstractContextBasedITextEvent) event;
        if (iTextEvent.getMetaInfo() != null) {
            context = ContextManager.getInstance().getContext(iTextEvent.getMetaInfo().getClass());
        }
        if (context == null) {
            context = ContextManager.getInstance().getContext(iTextEvent.getClassFromContext());
        }

        if (context == null) {
            context = this.defaultContext;
        }

        if (context.isAllowed(iTextEvent)) {
            onAcceptedEvent(iTextEvent);
        }
    }

    /**
     * Handles the accepted event.
     *
     * @param event to handle
     */
    protected abstract void onAcceptedEvent(AbstractContextBasedITextEvent event);
}
