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

import org.slf4j.Logger;
import org.slf4j.Marker;

public class TestLogger implements Logger {

    private final TestLoggerStats stats = new TestLoggerStats();

    private final boolean isEnabled;

    public TestLogger(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled;
    }

    @Override
    public void trace(String msg) {
        stats.addTraceCall(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String msg, Throwable t) {
        stats.addTraceWithThrowableCall(msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled;
    }

    @Override
    public void debug(String msg) {
        stats.addDebugCall(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String msg, Throwable t) {
        stats.addDebugWithThrowableCall(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled;
    }

    @Override
    public void info(String msg) {
        stats.addInfoCall(msg);
    }

    @Override
    public void info(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String msg, Throwable t) {
        stats.addInfoWithThrowableCall(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled;
    }

    @Override
    public void warn(String msg) {
        stats.addWarnCall(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String msg, Throwable t) {
        stats.addWarnWithThrowableCall(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled;
    }

    @Override
    public void error(String msg) {
        stats.addErrorCall(msg);
    }

    @Override
    public void error(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String msg, Throwable t) {
        stats.addErrorWithThrowableCall(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    public TestLoggerStats getStats() {
        return stats;
    }
}
