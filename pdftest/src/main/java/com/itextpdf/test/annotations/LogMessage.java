package com.itextpdf.test.annotations;


public @interface LogMessage {
    public String messageTemplate();
    public int count() default 1;
}
