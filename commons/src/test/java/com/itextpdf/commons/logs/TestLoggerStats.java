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

import com.itextpdf.commons.datastructures.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class TestLoggerStats {

    private final List<String> traceCalls = new ArrayList<String>();
    private final List<Tuple2<String, Throwable>> traceWithThrowableCalls = new ArrayList<Tuple2<String, Throwable>>();
    private final List<String> debugCalls = new ArrayList<String>();
    private final List<Tuple2<String, Throwable>> debugWithThrowableCalls = new ArrayList<Tuple2<String, Throwable>>();
    private final List<String> infoCalls = new ArrayList<String>();
    private final List<Tuple2<String, Throwable>> infoWithThrowableCalls = new ArrayList<Tuple2<String, Throwable>>();
    private final List<String> warnCalls = new ArrayList<String>();
    private final List<Tuple2<String, Throwable>> warnWithThrowableCalls = new ArrayList<Tuple2<String, Throwable>>();
    private final List<String> errorCalls = new ArrayList<String>();
    private final List<Tuple2<String, Throwable>> errorWithThrowableCalls = new ArrayList<Tuple2<String, Throwable>>();

    public TestLoggerStats() {
        // empty constructor
    }

    public void addTraceCall(String message) {
        traceCalls.add(message);
    }

    public List<String> getTraceCalls() {
        return traceCalls;
    }

    public void addTraceWithThrowableCall(String message, Throwable t) {
        traceWithThrowableCalls.add(new Tuple2<String, Throwable>(message, t));
    }

    public List<Tuple2<String, Throwable>> getTraceWithThrowableCalls() {
        return traceWithThrowableCalls;
    }

    public void addDebugCall(String message) {
        debugCalls.add(message);
    }

    public List<String> getDebugCalls() {
        return debugCalls;
    }

    public void addDebugWithThrowableCall(String message, Throwable t) {
        debugWithThrowableCalls.add(new Tuple2<String, Throwable>(message, t));
    }

    public List<Tuple2<String, Throwable>> getDebugWithThrowableCalls() {
        return debugWithThrowableCalls;
    }

    public void addInfoCall(String message) {
        infoCalls.add(message);
    }

    public List<String> getInfoCalls() {
        return infoCalls;
    }

    public void addInfoWithThrowableCall(String message, Throwable t) {
        infoWithThrowableCalls.add(new Tuple2<String, Throwable>(message, t));
    }

    public List<Tuple2<String, Throwable>> getInfoWithThrowableCalls() {
        return infoWithThrowableCalls;
    }

    public void addWarnCall(String message) {
        warnCalls.add(message);
    }

    public List<String> getWarnCalls() {
        return warnCalls;
    }

    public void addWarnWithThrowableCall(String message, Throwable t) {
        warnWithThrowableCalls.add(new Tuple2<String, Throwable>(message, t));
    }

    public List<Tuple2<String, Throwable>> getWarnWithThrowableCalls() {
        return warnWithThrowableCalls;
    }

    public void addErrorCall(String message) {
        errorCalls.add(message);
    }

    public List<String> getErrorCalls() {
        return errorCalls;
    }

    public void addErrorWithThrowableCall(String message, Throwable t) {
        errorWithThrowableCalls.add(new Tuple2<String, Throwable>(message, t));
    }

    public List<Tuple2<String, Throwable>> getErrorWithThrowableCalls() {
        return errorWithThrowableCalls;
    }

    public int getTotalInvocationsCount() {
        return traceCalls.size() + traceWithThrowableCalls.size()
                + debugCalls.size() + debugWithThrowableCalls.size()
                + infoCalls.size() + infoWithThrowableCalls.size()
                + warnCalls.size() + warnWithThrowableCalls.size()
                + errorCalls.size() + errorWithThrowableCalls.size();
    }
}
