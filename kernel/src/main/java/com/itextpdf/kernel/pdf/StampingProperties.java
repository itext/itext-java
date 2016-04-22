package com.itextpdf.kernel.pdf;

import java.io.Serializable;

public class StampingProperties implements Serializable {

    private static final long serialVersionUID = 6108082513101777457L;

    protected boolean appendMode;
    protected boolean preserveEncryption;

    public StampingProperties() {
        appendMode = false;
        preserveEncryption = false;
    }

    /**
     * Defines if the document will be edited in append mode.
     * @return this {@link StampingProperties} instance
     */
    public StampingProperties useAppendMode() {
        appendMode = true;
        return this;
    }

    /**
     * Defines if the encryption of the original document (if it was encrypted) will be preserved.
     * By default, the resultant document doesn't preserve the original encryption.
     * @return this {@link StampingProperties} instance
     */
    public StampingProperties preserveEncryption() {
        this.preserveEncryption = true;
        return this;
    }
}
