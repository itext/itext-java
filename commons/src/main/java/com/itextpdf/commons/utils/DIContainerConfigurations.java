package com.itextpdf.commons.utils;

public class DIContainerConfigurations {

    private DIContainerConfigurations() {
        // Empty constructor
    }

    private static final String[] DEFAULT_CONFIGURATIONS_CLASS = new String[] {
            "com.itextpdf.forms.util.RegisterDefaultDiContainer"
    };

    public static void loadDefaultConfigurations() {
        for (final String defaultConfigurationClass : DEFAULT_CONFIGURATIONS_CLASS) {
            try {
                Class.forName(defaultConfigurationClass);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }
    }
}
