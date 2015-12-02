package com.itextpdf.test.annotations;


public @interface LogMessage {
    String messageTemplate();
    int count() default 1;
}
