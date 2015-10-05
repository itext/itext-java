package com.itextpdf.test;


import com.itextpdf.test.annotations.LogMessage;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Assert;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;


public class LogListener extends TestWatcher {

    private final static String ROOT_ITEXT_PACKAGE = "com.itextpdf";

    private final CustomListAppender<ILoggingEvent> listAppender = new CustomListAppender<ILoggingEvent>();

    private final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    @Override
    protected void starting(Description description) {
        before();
    }

    @Override
    protected void finished(Description description) {
        checkLogMessages(description);
        after();
    }

    public boolean contains(String loggingStatement) {
        List<ILoggingEvent> list = listAppender.list;
        for (ILoggingEvent event : list) {
            if (event.getFormattedMessage().contains(loggingStatement)) {
                return true;
            }
        }
        return false;
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
        Logger logger = (Logger) LoggerFactory.getLogger(ROOT_ITEXT_PACKAGE);
        logger.addAppender(listAppender);
    }

    private void resetLoggingContext() {
        lc.reset();
    }

    private void checkLogMessages(Description description) {
        Annotation annotation = description.getAnnotation(LogMessage.class);
        if (annotation != null) {
            LogMessage logMessage = (LogMessage) annotation;
            if (!logMessage.ignore()) {
                for (String message : logMessage.messages()) {
                    Assert.assertTrue(description.getClassName()
                            + "."
                            + description.getMethodName()
                            + ": " + "Some log messages are not found in test execution", contains(message));
                }
            }
        } else {
            if (getSize() > 0) {
                Assert.assertFalse(description.getClassName()
                        + "." + description.getMethodName()
                        + ": "
                        + "The test doesn't check the message logging", true);
            }
        }
    }

    private class CustomListAppender<E> extends ListAppender<ILoggingEvent> {
        protected void append(ILoggingEvent e) {
            System.out.println(e.getLoggerName() + " " + e.getLevel() + " " + e.getMessage());
            this.list.add(e);
        }
    }

}