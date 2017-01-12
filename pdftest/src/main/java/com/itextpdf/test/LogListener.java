/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.test;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.read.ListAppender;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.Assert;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.List;


public class LogListener extends TestWatcher {

    private static final String ROOT_ITEXT_PACKAGE = "com.itextpdf";

    private final ListAppender<ILoggingEvent> listAppender = new CustomListAppender<ILoggingEvent>();

    private final ILoggerFactory lc = LoggerFactory.getILoggerFactory();

    @Override
    protected void starting(Description description) {
        before();
    }

    @Override
    protected void finished(Description description) {
        checkLogMessages(description);
        after();
    }

    private int contains(String loggingStatement) {
        List<ILoggingEvent> list = listAppender.list;
        int index = 0;
        for (ILoggingEvent event : list) {
            if (equalsMessageByTemplate(event.getFormattedMessage(), loggingStatement)) {
                index++;
            }
        }
        return index;
    }

    /*
    * compare  parametrized message with  base template, for example:
    *  "Hello fox1 , World  fox2 !" with "Hello {0} , World {1} !"
    * */
    private boolean equalsMessageByTemplate(String message, String template) {
        if (template.indexOf("{") > 0 && template.indexOf("}") > 0) {
            String templateWithoutParameters = template.replace("''", "'").replaceAll("\\{[0-9]+?\\}", "(.|\\\\s)*?");
            return message.matches(templateWithoutParameters);
        } else {
            return message.contains(template);
        }
    }

    public int getSize() {
        return listAppender.list.size();
    }

    private void before() {
        resetLoggingContext();
        addAppenderToPackage();
        listAppender.start();
    }

    private void after() {
        listAppender.stop();
        resetLoggingContext();
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
        Annotation annotation = description.getAnnotation(LogMessages.class);
        if (annotation == null) {
            annotation = description.getTestClass().getAnnotation(LogMessages.class);
        }
        int checkedMessages = 0;
        if (annotation != null) {
            LogMessages logMessages = (LogMessages) annotation;
            LogMessage[] messages = logMessages.messages();
            for (LogMessage logMessage : messages) {
                int foundCount = contains(logMessage.messageTemplate());
                if (foundCount != logMessage.count() && !logMessages.ignore()) {
                    Assert.fail(MessageFormat.format("{0}:{1} Expected to find {2}, but found {3} messages with the following content: \"{4}\"",
                                    description.getClassName(), description.getMethodName(), logMessage.count(), foundCount, logMessage.messageTemplate()));
                } else {
                    checkedMessages += foundCount;
                }
            }
        }
        if (getSize() > checkedMessages) {
            Assert.fail(MessageFormat.format("{0}.{1}: The test does not check the message logging - {2} messages",
                            description.getClassName(),
                            description.getMethodName(),
                            getSize() - checkedMessages));
        }
    }

    private class CustomListAppender<E> extends ListAppender<ILoggingEvent> {
        protected void append(ILoggingEvent e) {
            System.out.println(e.getLoggerName() + " " + e.getLevel() + " " + e.getMessage());
            printStackTraceIfAny(e);
            if (e.getLevel().isGreaterOrEqual(Level.WARN)) {
                this.list.add(e);
            }
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