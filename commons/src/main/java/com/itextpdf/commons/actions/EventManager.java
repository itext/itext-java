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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.processors.UnderAgplProductProcessorFactory;
import com.itextpdf.commons.exceptions.AggregatedException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Entry point for event handling mechanism. Class is a singleton,
 * see {@link EventManager#getInstance()}.
 */
public final class EventManager {
    private static final EventManager INSTANCE = new EventManager();

    private final Set<IEventHandler> handlers = new LinkedHashSet<>();

    private EventManager() {
        handlers.add(ProductEventHandler.INSTANCE);
    }

    /**
     * Allows access to the instance of EventManager.
     *
     * @return the instance of the class
     */
    public static EventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Deliberately turns off the warning message about AGPL usage.
     *
     * <p>
     * <b> Important note. Calling of this method means that the terms of AGPL license are met. </b>
     */
    public static void acknowledgeAgplUsageDisableWarningMessage() {
        ProductProcessorFactoryKeeper.setProductProcessorFactory(new UnderAgplProductProcessorFactory());
    }

    /**
     * Handles the event.
     *
     * @param event to handle
     */
    public void onEvent(IEvent event) {
        final List<RuntimeException> caughtExceptions = new ArrayList<>();
        for (final IEventHandler handler : handlers) {
            try {
                handler.onEvent(event);
            } catch (RuntimeException ex) {
                caughtExceptions.add(ex);
            }
        }
        if (event instanceof AbstractITextConfigurationEvent) {
            try {
                final AbstractITextConfigurationEvent itce = (AbstractITextConfigurationEvent) event;
                itce.doAction();
            } catch (RuntimeException ex) {
                caughtExceptions.add(ex);
            }
        }

        if (caughtExceptions.size() == 1) {
            throw caughtExceptions.get(0);
        }
        if (!caughtExceptions.isEmpty()) {
            throw new AggregatedException(AggregatedException.ERROR_DURING_EVENT_PROCESSING, caughtExceptions);
        }
    }

    /**
     * Add new {@link IEventHandler} to the event handling process.
     *
     * @param handler is a handler to add
     */
    public void register(IEventHandler handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    /**
     * Check if the handler was registered for event handling process.
     *
     * @param handler is a handler to check
     * @return true if handler has been already registered and false otherwise
     */
    public boolean isRegistered(IEventHandler handler) {
        if (handler != null) {
            return handlers.contains(handler);
        }
        return false;
    }

    /**
     * Removes handler from event handling process.
     *
     * @param handler is a handle to remove
     * @return true if the handler had been registered previously and was removed. False if the
     * handler was not found among registered handlers
     */
    public boolean unregister(IEventHandler handler) {
        if (handler != null) {
            return handlers.remove(handler);
        }
        return false;
    }
}
