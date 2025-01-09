/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.exceptions;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception class for exceptions in kernel module.
 */
public class PdfException extends ITextException {
    /**
     * Object for more details
     */
    protected Object object;

    private List<Object> messageParams;

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     */
    public PdfException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public PdfException(Throwable cause) {
        this(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param obj     an object for more details.
     */
    public PdfException(String message, Object obj) {
        this(message);
        this.object = obj;
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     * @param obj     an object for more details.
     */
    public PdfException(String message, Throwable cause, Object obj) {
        this(message, cause);
        this.object = obj;
    }

    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.size() == 0) {
            return super.getMessage();
        } else {
            return MessageFormatUtil.format(super.getMessage(), getMessageParams());
        }
    }

    /**
     * Sets additional params for Exception message.
     *
     * @param messageParams additional params.
     * @return object itself.
     */
    public PdfException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }

    /**
     * Gets additional params for Exception message.
     * @return array of additional params
     */
    protected Object[] getMessageParams() {
        Object[] parameters = new Object[messageParams.size()];
        for (int i = 0; i < messageParams.size(); i++) {
            parameters[i] = messageParams.get(i);
        }
        return parameters;
    }
}
