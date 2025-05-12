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
package com.itextpdf.test;

import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.read.ListAppender;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

public class LogListener implements BeforeTestExecutionCallback, AfterTestExecutionCallback, BeforeAllCallback {

    private static final String ROOT_ITEXT_PACKAGE = "com.itextpdf";
    private static final String ITEXT_LICENCING_PACKAGE = "com.itextpdf.licensing";
    private static final String ITEXT_ACTIONS_PACKAGE = "com.itextpdf.commons.actions.processors";
    private static final String TOKEN_ITEXT_LOGLEVEL = "ITEXT_SILENT_MODE";

    private static final String DEFAULT_THREAD_ID = "1";
    private AtomicBoolean threadsAware;

    private static CustomListAppender<ILoggingEvent> listAppender;
    private static ILoggerFactory lc;

    public LogListener() {
        synchronized(LogListener.class) {
            if (listAppender == null) {
                String logLevel = getPropertyOrEnvironmentVariable(TOKEN_ITEXT_LOGLEVEL);
                listAppender = new CustomListAppender<>(parseSilentMode(logLevel));
                if (lc == null) {
                    lc = LoggerFactory.getILoggerFactory();
                    resetLoggingContext();
                }
                addAppenderToPackage();
                listAppender.start();
            }
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        LogMessages logMessages = LoggerHelper.getTestAnnotation(extensionContext, LogMessages.class);
        if (logMessages != null) {
            Map<String, Boolean> expectedTemplates = new HashMap<>();
            LogMessage[] messages = logMessages.messages();
            for (LogMessage logMessage : messages) {
                expectedTemplates.put(logMessage.messageTemplate(), logMessage.quietMode());
            }
            listAppender.setExpectedTemplates(expectedTemplates);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        checkLogMessages(context);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (threadsAware == null) {
            ExecutionMode executionMode = extensionContext.getExecutionMode();
            threadsAware = new AtomicBoolean(executionMode == ExecutionMode.CONCURRENT);
        }
    }

    private boolean parseSilentMode(String logLevel) {
        if (logLevel == null){
            return  false;
        }
        return logLevel.equalsIgnoreCase("TRUE");
    }

    private int contains(LogMessage loggingStatement) {
        int count = 0;
        synchronized(LogListener.class) {
            List<ILoggingEvent> list = listAppender.list;
            for (ILoggingEvent event : list) {
                if (isLevelCompatible(loggingStatement.logLevel(), event.getLevel())
                        && LoggerHelper
                        .equalsMessageByTemplate(event.getFormattedMessage(), loggingStatement.messageTemplate())
                        && (event.getThreadName().equals(getThreadId()))) {
                    count++;
                }
            }
        }

        return count;
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

    private int getSizeBy(String threadId) {
        int nOfEvents = 0;
        synchronized(LogListener.class) {
            for (ILoggingEvent iLoggingEvent : listAppender.list) {
                if (Objects.equals(iLoggingEvent.getThreadName(), threadId)) {
                    nOfEvents++;
                }
            }
        }

        return nOfEvents;
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

    private void checkLogMessages(ExtensionContext context) {
        LogMessages logMessages = LoggerHelper.getTestAnnotation(context, LogMessages.class);
        int checkedMessages = 0;
        if (logMessages != null) {
            LogMessage[] messages = logMessages.messages();
            for (LogMessage logMessage : messages) {
                int foundCount = contains(logMessage);
                if (foundCount != logMessage.count() && !logMessages.ignore() && !logMessage.ignore()) {
                    listAppender.clear();
                    LoggerHelper.failWrongMessageCount(logMessage.count(), foundCount, logMessage.messageTemplate(),
                            context);
                } else {
                    checkedMessages += foundCount;
                }
            }
        }

        final int size = getSizeBy(getThreadId());
        if (size > checkedMessages) {
            listAppender.clear();
            LoggerHelper.failWrongTotalCount(size, checkedMessages, context);
        }
        listAppender.clear();
    }

    private static String getPropertyOrEnvironmentVariable(String name) {
        String s = System.getProperty(name);
        if (s == null) {
            s = System.getenv(name);
        }
        return s;
    }

    private String getThreadId() {
        if (threadsAware != null && threadsAware.get()) {
            return String.valueOf(Thread.currentThread().getId());
        } else {
            return DEFAULT_THREAD_ID;
        }
    }

    private class CustomListAppender<E> extends ListAppender<ILoggingEvent> {
        // Thread id - templates
        private Map<String, Map<String, Boolean>> expectedTemplates = new ConcurrentHashMap<>();
        private  final boolean runTestsInSilentMode;

        private CustomListAppender(boolean runTestsInSilentMode) {
            this.runTestsInSilentMode = runTestsInSilentMode;
        }

        public void setExpectedTemplates(Map<String, Boolean> expectedTemplates) {
            String threadId = getThreadId();
            if (!this.expectedTemplates.containsKey(threadId)) {
                this.expectedTemplates.put(threadId, new HashMap<>());
            }
            this.expectedTemplates.get(threadId).clear();
            this.expectedTemplates.get(threadId).putAll(expectedTemplates);
        }

        public void clear() {
            String threadId = getThreadId();
            synchronized(LogListener.class) {
                list.removeIf(next -> Objects.equals(next.getThreadName(), threadId));
            }
            if (expectedTemplates.containsKey(threadId)) {
                expectedTemplates.get(threadId).clear();
            }
        }

        protected void append(ILoggingEvent e) {
            String threadId = getThreadId();

            if(!isExpectedMessageQuiet(e.getMessage(), threadId)){
                if (shouldPrintMessage(e)){
                    System.out.println(e.getLoggerName() + " " + e.getLevel() + " " + e.getMessage());
                }
            }
            printStackTraceIfAny(e);

            if (e.getLevel().isGreaterOrEqual(Level.WARN) || isExpectedMessage(e.getMessage(), threadId)) {
                ((LoggingEvent) e).setThreadName(threadId);
                synchronized(LogListener.class) {
                    this.list.add(e);
                }
            }
        }

        private boolean shouldPrintMessage(ILoggingEvent level) {
            //Those 2 if statements are when we rely on the logmessages being printed for certain tests
            if (level.getLoggerName().startsWith(ITEXT_LICENCING_PACKAGE)){
                return true;
            }
            if (level.getLoggerName().startsWith(ITEXT_ACTIONS_PACKAGE)){
                return true;
            }
            return !runTestsInSilentMode;
        }

        private boolean isExpectedMessage(String message, String threadId) {
            if (message != null && expectedTemplates.containsKey(threadId)) {
                for (String template : expectedTemplates.get(threadId).keySet()) {
                    if (LoggerHelper.equalsMessageByTemplate(message, template)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isExpectedMessageQuiet(String message, String threadId) {
            if (message != null && expectedTemplates.containsKey(threadId)) {
                for (String template : expectedTemplates.get(threadId).keySet()) {
                    if (LoggerHelper.equalsMessageByTemplate(message, template) && expectedTemplates.get(threadId).get(template)) {
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