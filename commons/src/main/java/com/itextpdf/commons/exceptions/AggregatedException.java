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
package com.itextpdf.commons.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite exception class.
 */
public class AggregatedException extends ITextException {
    /**
     * Notifies that event processing failed.
     */
    public static final String ERROR_DURING_EVENT_PROCESSING = "Error during event processing";

    private static final String AGGREGATED_MESSAGE = "Aggregated message";
    private final List<RuntimeException> aggregatedExceptions;

    /**
     * Creates an instance of aggregated exception based on the collection of exceptions.
     *
     * @param aggregatedExceptions is a list of aggregated exceptions
     */
    public AggregatedException(List<RuntimeException> aggregatedExceptions) {
        super("");
        this.aggregatedExceptions = new ArrayList<>(aggregatedExceptions);
    }

    /**
     * Creates an instance of aggregated exception based on the collection of exceptions.
     *
     * @param message the detail message
     * @param aggregatedExceptions is a list of aggregated exceptions
     */
    public AggregatedException(String message, List<RuntimeException> aggregatedExceptions) {
        super(message);
        this.aggregatedExceptions = new ArrayList<>(aggregatedExceptions);
    }

    /**
     * Builds message for the exception including its own message and all messages from the
     * aggregated exceptions.
     *
     * @return aggregated message
     */
    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null || message.isEmpty()) {
            message = AGGREGATED_MESSAGE;
        }
        final StringBuilder builder = new StringBuilder(message);
        builder.append(":\n");
        for (int i = 0; i < aggregatedExceptions.size(); ++i) {
            Exception current = aggregatedExceptions.get(i);
            if (current != null) {
                builder.append(i).append(") ").append(current.getMessage()).append('\n');
            }
        }
        return builder.toString();
    }

    /**
     * Gets a list of aggregated exceptions.
     *
     * @return aggregated exceptions
     */
    public List<Exception> getAggregatedExceptions() {
        return Collections.unmodifiableList(aggregatedExceptions);
    }
}
