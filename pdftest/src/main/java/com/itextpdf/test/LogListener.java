/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.test;

import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

public class LogListener extends TestWatcher {

    private static final String ROOT_ITEXT_PACKAGE = "com.itextpdf";

    private final CustomListAppender<ILoggingEvent> listAppender = new CustomListAppender<>();

    private final ILoggerFactory lc = LoggerFactory.getILoggerFactory();

    private Map<Logger, Map<String, Appender<ILoggingEvent>>> appenders;

    @Override
    protected void starting(Description description) {
        before(description);
    }

    @Override
    protected void finished(Description description) {
        checkLogMessages(description);
        after();
    }

    private int contains(LogMessage loggingStatement) {
        List<ILoggingEvent> list = listAppender.list;
        int index = 0;
        for (ILoggingEvent event : list) {
            if (isLevelCompatible(loggingStatement.logLevel(), event.getLevel())
                    && LoggerHelper
                    .equalsMessageByTemplate(event.getFormattedMessage(), loggingStatement.messageTemplate())) {
                index++;
            }
        }
        return index;
    }

    private boolean isLevelCompatible(int logMessageLevel, Level eventLevel) {
        switch (logMessageLevel) {
            case LogLevelConstants.UNKNOWN:
                return eventLevel.isGreaterOrEqual(Level.WARN);
            case LogLevelConstants.ERROR:
                return eventLevel == Level.ERROR;
            case LogLevelConstants.WARN:
                return eventLevel == Level.WARN;
            case LogLevelConstants.INFO:
                return eventLevel == Level.INFO;
            case LogLevelConstants.DEBUG:
                return eventLevel == Level.DEBUG;
            default:
                return false;
        }
    }

    public int getSize() {
        return listAppender.list.size();
    }

    private void before(Description description) {
        listAppender.clear();

        LogMessages logMessages = LoggerHelper.getTestAnnotation(description, LogMessages.class);
        if (logMessages != null) {
            Map<String, Boolean> expectedTemplates = new HashMap<>();
            LogMessage[] messages = logMessages.messages();
            for (LogMessage logMessage : messages) {
                expectedTemplates.put(logMessage.messageTemplate(), logMessage.quietMode());
            }
            listAppender.setExpectedTemplates(expectedTemplates);
        }

        // LoggerContext#reset method resets more parameters than appenders,
        // like turbofilters, listeners, etc. But currently it is important to save only appenders.
        appenders = LoggerHelper.getAllAppendersMap((LoggerContext) lc);
        resetLoggingContext();
        addAppenderToPackage();
        listAppender.start();
    }

    private void after() {
        listAppender.stop();
        resetLoggingContext();
        LoggerHelper.restoreAppenders(appenders);
    }

    private void addAppenderToPackage() {
        org.slf4j.Logger logger = LoggerFactory.getLogger(ROOT_ITEXT_PACKAGE);
        if (logger instanceof Logger) {
            ((Logger) logger).addAppender(listAppender);
        }
    }

    private void resetLoggingContext() {
        if (lc instanceof LoggerContext) {
            ((LoggerContext) lc).reset();
        } else if (lc instanceof SubstituteLoggerFactory) {
            ((SubstituteLoggerFactory) lc).clear();
        }
    }

    private void checkLogMessages(Description description) {
        LogMessages logMessages = LoggerHelper.getTestAnnotation(description, LogMessages.class);
        int checkedMessages = 0;
        if (logMessages != null) {
            LogMessage[] messages = logMessages.messages();
            for (LogMessage logMessage : messages) {
                int foundCount = contains(logMessage);
                if (foundCount != logMessage.count() && !logMessages.ignore() && !logMessage.ignore()) {
                    LoggerHelper.failWrongMessageCount(logMessage.count(), foundCount, logMessage.messageTemplate(),
                            description);
                } else {
                    checkedMessages += foundCount;
                }
            }
        }
        if (getSize() > checkedMessages) {
            LoggerHelper.failWrongTotalCount(getSize(), checkedMessages, description);
        }
    }

    private class CustomListAppender<E> extends ListAppender<ILoggingEvent> {

        private Map<String, Boolean> expectedTemplates = new HashMap<>();

        public void setExpectedTemplates(Map<String, Boolean> expectedTemplates) {
            this.expectedTemplates.clear();
            this.expectedTemplates.putAll(expectedTemplates);
        }

        public void clear() {
            this.list.clear();
            expectedTemplates.clear();
        }

        protected void append(ILoggingEvent e) {
            if(!isExpectedMessageQuiet(e.getMessage())){
                System.out.println(e.getLoggerName() + " " + e.getLevel() + " " + e.getMessage());
            }
            printStackTraceIfAny(e);
            if (e.getLevel().isGreaterOrEqual(Level.WARN) || isExpectedMessage(e.getMessage())) {
                this.list.add(e);
            }
        }

        private boolean isExpectedMessage(String message) {
            if (message != null) {
                for (String template : expectedTemplates.keySet()) {
                    if (LoggerHelper.equalsMessageByTemplate(message, template)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isExpectedMessageQuiet(String message) {
            if (message != null) {
                for (String template : expectedTemplates.keySet()) {
                    if (LoggerHelper.equalsMessageByTemplate(message, template) && expectedTemplates.get(template)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void printStackTraceIfAny(ILoggingEvent e) {
            IThrowableProxy throwableProxy = e.getThrowableProxy();
            if (throwableProxy != null) {
                System.out.println(throwableProxy.getMessage());
                for (StackTraceElementProxy el : throwableProxy.getStackTraceElementProxyArray()) {
                    System.out.println("\t" + el);
                }
            }
        }
    }
}
