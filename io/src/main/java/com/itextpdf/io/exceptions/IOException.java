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
package com.itextpdf.io.exceptions;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception class for exceptions in io module.
 */
public class IOException extends ITextException {

    /**
     * Object for more details
     */
    protected Object obj;

    private List<Object> messageParams;

    /**
     * Creates a new IOException.
     *
     * @param message the detail message.
     */
    public IOException(String message) {
        super(message);
    }

    /**
     * Creates a new IOException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public IOException(Throwable cause) {
        this(IoExceptionMessageConstant.UNKNOWN_IO_EXCEPTION, cause);
    }

    /**
     * Creates a new IOException.
     *
     * @param message the detail message.
     * @param obj     an object for more details.
     */
    public IOException(String message, Object obj) {
        this(message);
        this.obj = obj;
    }

    /**
     * Creates a new IOException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public IOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of IOException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     * @param obj     an object for more details.
     */
    public IOException(String message, Throwable cause, Object obj) {
        this(message, cause);
        this.obj = obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.size() == 0) {
            return super.getMessage();
        } else {
            return MessageFormatUtil.format(super.getMessage(), getMessageParams());
        }
    }

    /**
     * Gets additional params for Exception message.
     *
     * @return params for exception message.
     */
    protected Object[] getMessageParams() {
        Object[] parameters = new Object[messageParams.size()];
        for (int i = 0; i < messageParams.size(); i++) {
            parameters[i] = messageParams.get(i);
        }
        return parameters;
    }

    /**
     * Sets additional params for Exception message.
     *
     * @param messageParams additional params.
     * @return object itself.
     */
    public IOException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }
}
