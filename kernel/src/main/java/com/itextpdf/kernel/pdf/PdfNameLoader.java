package com.itextpdf.kernel.pdf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

final class PdfNameLoader {

    static Map<String, PdfName> loadNames() {
        Field[] fields = PdfName.class.getDeclaredFields();
        Map<String, PdfName> staticNames = new HashMap<>(fields.length);
        final int flags = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        try {
            for (Field field : fields) {
                if ((field.getModifiers() & flags) == flags && field.getType().equals(PdfName.class)) {
                    PdfName name = (PdfName) field.get(null);
                    staticNames.put(name.getValue(), name);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return staticNames;
    }
}
