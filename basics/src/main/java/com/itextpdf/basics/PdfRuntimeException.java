package com.itextpdf.basics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfRuntimeException extends RuntimeException {

    protected Object object;
    protected String composedMessage;
    private List<Object> messageParams;

    public PdfRuntimeException(String message) {
        super(message);
    }

    public PdfRuntimeException(String message, Object object) {
        this(message);
        this.object = object;
    }

    public PdfRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfRuntimeException(String message, Throwable cause, Object object) {
        this(message, cause);
        this.object = object;
    }

    @Override
    public String getMessage() {
        if (messageParams != null) {
            StringBuilder builder = new StringBuilder(super.getMessage());
            builder.append('+');
            for (Object obj : messageParams) {
                builder.append(obj.toString()).append('+');
            }
            return builder.substring(0, builder.length() - 1);
        }
        return super.getMessage();
    }

    public PdfRuntimeException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<Object>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }

    public String getComposedMessage() {
        return composedMessage;
    }

    public Object getObject() {
        return object;
    }
}
