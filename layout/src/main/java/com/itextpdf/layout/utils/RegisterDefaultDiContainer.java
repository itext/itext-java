package com.itextpdf.layout.utils;

import com.itextpdf.commons.utils.DIContainer;

/**
 * Registers a default instance for a dependency injection container for the layout module.
 */
public class RegisterDefaultDiContainer {

    static {
        DIContainer.registerDefault(LayoutInfiniteLoopResolver.class, () -> new LayoutInfiniteLoopResolver());
    }

    /**
     * Creates an instance of {@link com.itextpdf.kernel.utils.RegisterDefaultDiContainer}.
     */
    public RegisterDefaultDiContainer() {
        // Empty constructor but should be public as we need it for automatic class loading
        // sharp
    }
}
