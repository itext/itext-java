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

/**
 * The fallback {@link IContext}.
 */
public class UnknownContext implements IContext {

    /**
     * The {@link IContext} that forbids all events.
     */
    public static final IContext RESTRICTIVE = new UnknownContext(false);
    /**
     * The {@link IContext} that allows all events.
     */
    public static final IContext PERMISSIVE = new UnknownContext(true);

    private final boolean allowEvents;

    /**
     * Creates a fallback {@link IContext}.
     *
     * @param allowEvents defines whether the context allows all events or not
     */
    public UnknownContext(boolean allowEvents) {
        this.allowEvents = allowEvents;
    }

    /**
     * Depending on its internal state allows or rejects all event.
     * Behaviour is defined via constructor {@link UnknownContext#UnknownContext(boolean)}
     *
     * @param event {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isAllowed(AbstractContextBasedITextEvent event) {
        return allowEvents;
    }
}
