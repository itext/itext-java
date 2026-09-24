/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.commons.logs;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents logger wrapper with lazy log operations for lazy log message constructions.
 */
public final class LazyLogger {

    private final Logger logger;

    /**
     * Creates the logger instance with the provided clazz naming.
     *
     * @param clazz - the returned logger will be named after clazz
     */
    public LazyLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    /**
     * Creates the logger instance wrapping the provided logger.
     *
     * @param logger - the logger to wrap
     */
    LazyLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Logs on error level if it is enabled.
     *
     * @param messageSupplier log message supplier
     */
    public void error(Supplier<String> messageSupplier) {
        if (isErrorEnabled()) {
            logger.error(messageSupplier.get());
        }
    }

    /**
     * Logs on error level if it is enabled.
     *
     * @param messageSupplier log message supplier
     * @param exception exception to log
     */
    public void error(Supplier<String> messageSupplier, Throwable exception) {
        if (isErrorEnabled()) {
            logger.error(messageSupplier.get(), exception);
        }
    }

    /**
     * Checks whether error logs would be logged.
     *
     * @return {@code true} if error logs would be logged.
     */
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    /**
     * Logs on warning level if it is enabled.
     *
     * @param messageSupplier log message supplier
     */
    public void warn(Supplier<String> messageSupplier) {
        if (isWarnEnabled()) {
            logger.warn(messageSupplier.get());
        }
    }

    /**
     * Logs on warning level if it is enabled.
     *
     * @param messageSupplier log message supplier
     * @param exception exception to log
     */
    public void warn(Supplier<String> messageSupplier, Throwable exception) {
        if (isWarnEnabled()) {
            logger.warn(messageSupplier.get(), exception);
        }
    }

    /**
     * Checks whether warn logs would be logged.
     *
     * @return {@code true} if warn logs would be logged.
     */
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    /**
     * Logs on info level if it is enabled.
     *
     * @param messageSupplier log message supplier
     */
    public void info(Supplier<String> messageSupplier) {
        if (isInfoEnabled()) {
            logger.info(messageSupplier.get());
        }
    }

    /**
     * Logs on info level if it is enabled.
     *
     * @param messageSupplier log message supplier
     * @param exception exception to log
     */
    public void info(Supplier<String> messageSupplier, Throwable exception) {
        if (isInfoEnabled()) {
            logger.info(messageSupplier.get(), exception);
        }
    }

    /**
     * Checks whether info logs would be logged.
     *
     * @return {@code true} if info logs would be logged.
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * Logs on debug level if it is enabled.
     *
     * @param messageSupplier log message supplier
     */
    public void debug(Supplier<String> messageSupplier) {
        if (isDebugEnabled()) {
            logger.debug(messageSupplier.get());
        }
    }

    /**
     * Logs on debug level if it is enabled.
     *
     * @param messageSupplier log message supplier
     * @param exception exception to log
     */
    public void debug(Supplier<String> messageSupplier, Throwable exception) {
        if (isDebugEnabled()) {
            logger.debug(messageSupplier.get(), exception);
        }
    }

    /**
     * Checks whether debug logs would be logged.
     *
     * @return {@code true} if debug logs would be logged.
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Logs on trace level if it is enabled.
     *
     * @param messageSupplier log message supplier
     */
    public void trace(Supplier<String> messageSupplier) {
        if (isTraceEnabled()) {
            logger.trace(messageSupplier.get());
        }
    }

    /**
     * Logs on trace level if it is enabled.
     *
     * @param messageSupplier log message supplier
     * @param exception exception to log
     */
    public void trace(Supplier<String> messageSupplier, Throwable exception) {
        if (isTraceEnabled()) {
            logger.trace(messageSupplier.get(), exception);
        }
    }

    /**
     * Checks whether trace logs would be logged.
     *
     * @return {@code true} if trace logs would be logged.
     */
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }
}
