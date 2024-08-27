package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.DIContainer;
import com.itextpdf.kernel.di.pagetree.IPageTreeListFactory;
import com.itextpdf.kernel.di.pagetree.DefaultPageTreeListFactory;

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
    }
}
