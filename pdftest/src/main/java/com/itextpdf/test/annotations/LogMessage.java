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
package com.itextpdf.test.annotations;

import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.LogListener;

/**
 * An annotation to be used in a {@link LogMessages} wrapper, which signifies
 * a particular log message that must appear in a test a specific number of
 * times.
 */
public @interface LogMessage {
    /**
     * Defines the parameterized log message to look for in the logs.
     *
     * @return the message template that must be checked for
     */
    String messageTemplate();

    /**
     * A certain message may have to be called several times, and the {@link
     * LogListener} algorithm checks whether it has been called the correct
     * number of times.
     * Defaults to once.
     *
     * @return the number of times a message template must appear in the logs
     */
    int count() default 1;

    int logLevel() default LogLevelConstants.UNKNOWN;

    /**
     * Defines whether the {@link LogListener} algorithm should be ignored. If
     * ignored, no checks will be done on the certain log message.
     *
     * Defaults to {@code false}.
     *
     * @return whether to ignore the {@link LogListener} algorithm for a particular log message
     */
    boolean ignore() default false;

    /**
     * Defines whether the {@link LogListener} logs should be suppressed in console output.
     *
     * Defaults to {@code false}.
     *
     * @return whether to suppress the {@link LogListener} console output
     */
    boolean quietMode() default false;
}
