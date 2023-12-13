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
package com.itextpdf.kernel.counter.context;

import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IGenericEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic context that allows {@link IGenericEvent} based on the whitelist of supported IDs
 */
public class GenericContext implements IContext {

    private final Set<String> supported;

    public GenericContext(Collection<String> supported) {
        this.supported = new HashSet<>();
        this.supported.addAll(supported);
    }

    @Override
    public boolean allow(IEvent event) {
        if (event instanceof IGenericEvent) {
            return supported.contains(((IGenericEvent) event).getOriginId());
        }
        return false;
    }
}
