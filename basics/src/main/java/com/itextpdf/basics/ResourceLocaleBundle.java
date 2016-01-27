package com.itextpdf.basics;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public class ResourceLocaleBundle {

    public final static Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public final static Set<String> locales = new HashSet(Arrays.asList("en", "pt", "nl"));
    private final static String RESOURCE_BUNDLE_ERROR_MESSAGE = "Exception message {0} is not found in the file";


    public synchronized static String getMessage(String path, String key, List<Object> args) {
        Locale currentLocale = Locale.getDefault();
        if (!locales.contains(currentLocale.getLanguage())) {
            currentLocale = DEFAULT_LOCALE;
        }
        ResourceBundle bundle = ResourceBundle.getBundle(path, currentLocale);
        try {
            String parametrizedMessage = bundle.getString(key);
            if (args == null || args.isEmpty()) {
                return parametrizedMessage;
            } else {
                return MessageFormat.format(parametrizedMessage, args.toArray());
            }
        } catch (MissingResourceException ignore) {
            return MessageFormat.format(RESOURCE_BUNDLE_ERROR_MESSAGE, key);
        }

    }

}
