/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.events;

/**
 * Event dispatcher interface.
 */
public interface IEventDispatcher {

    /**
     * Adds new event handler.
     *
     * @param type a type of event to be handled
     * @param handler event handler
     */
    void addEventHandler(String type, IEventHandler handler);

    /**
     * Dispatches an event.
     *
     * @param event the {@link Event} to be dispatched
     */
    void dispatchEvent(Event event);

    /**
     * Dispatches a delayed event.
     * Sometimes event cannot be handled immediately because event handler has not been set yet.
     * In this case event is placed into event ques of dispatcher and is waiting until handler is assigned.
     *
     * @param event the {@link Event} to be dispatched
     * @param delayed flag whether {@link Event} delayed or not
     */
    void dispatchEvent(Event event, boolean delayed);

    /**
     * Checks if event dispatcher as an event handler assigned for a certain event type.
     *
     * @param type a type of the {@link Event}
     * @return true if event dispatcher as an event handler assigned for a certain event type
     */
    boolean hasEventHandler(String type);

    /**
     * Removes event handler.
     *
     * @param type a type of the {@link Event}
     * @param handler event handler {@link IEventHandler}
     */
    void removeEventHandler(String type, IEventHandler handler);

    /**
     * Remove all event handlers.
     */
    void removeAllHandlers();
}
