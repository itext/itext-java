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
package com.itextpdf.forms.fields;

import com.itextpdf.commons.utils.Action;
import com.itextpdf.layout.renderer.MetaInfoContainer;

/**
 * Class to store meta info that will be used in forms module in static context.
 */
public final class FormsMetaInfoStaticContainer {

    private static ThreadLocal<MetaInfoContainer> metaInfoForLayout = new ThreadLocal<>();

    private FormsMetaInfoStaticContainer() {
        // Empty constructor.
    }

    /**
     * Sets meta info related to forms into static context, executes the action and then cleans meta info.
     *
     * <p>
     * Keep in mind that this instance will only be accessible from the same thread.
     *
     * @param metaInfoContainer instance to be set.
     * @param action action which will be executed while meta info is set to static context.
     */
    // TODO DEVSIX-6368 We want to prevent customer code being run while meta info is in the static context
    public static void useMetaInfoDuringTheAction(MetaInfoContainer metaInfoContainer, Action action) {
        try {
            metaInfoForLayout.set(metaInfoContainer);
            action.execute();
        } finally {
            metaInfoForLayout.set(null);
        }
    }

    /**
     * Gets meta info which was set previously.
     *
     * <p>
     * Keep in mind that this operation will return meta info instance which was set previously from the same thread.
     *
     * @return meta info instance.
     */
    static MetaInfoContainer getMetaInfoForLayout() {
        return metaInfoForLayout.get();
    }
}
