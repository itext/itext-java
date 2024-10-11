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
package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.DIContainer;
import com.itextpdf.kernel.di.pagetree.IPageTreeListFactory;
import com.itextpdf.kernel.di.pagetree.DefaultPageTreeListFactory;
import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.mac.StandaloneMacContainerLocator;

/**
 * Registers a default instance for a dependency injection container for the kernel module.
 */
public class RegisterDefaultDiContainer {

    private static final int DEFAULT_PAGE_TREE_LIST_FACTORY_MAX_SAFE_ENTRIES = 50_000;

    /**
     * Creates an instance of {@link RegisterDefaultDiContainer}.
     */
    public RegisterDefaultDiContainer() {
        // Empty constructor but should be public as we need it for automatic class loading
        // sharp
    }

    static {
        DIContainer.registerDefault(IPageTreeListFactory.class,
                () -> new DefaultPageTreeListFactory(DEFAULT_PAGE_TREE_LIST_FACTORY_MAX_SAFE_ENTRIES));
        DIContainer.registerDefault(IMacContainerLocator.class, () -> new StandaloneMacContainerLocator());
    }
}
