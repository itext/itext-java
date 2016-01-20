package com.itextpdf.test;


import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;


public class LogListener extends TestWatcher {


    private final static String ROOT_ITEXT_PACKAGE = "com.itextpdf";

    private final ListAppender<ILoggingEvent> listAppender = new CustomListAppender<ILoggingEvent>();

    private final ILoggerFactory lc = LoggerFactory.getILoggerFactory();

    private final String LEFT_CURLY_BRACES = "{";
    private final String RIGHT_CURLY_BRACES = "}";

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
                index ++ ;
            }
        }
        return index;
    }

    /*
    * compare  parametrized message with  base template, for example:
    *  "Hello fox1 , World  fox2 !" with "Hello {0} , World {1} !"
    * */
    private boolean equalsMessageByTemplate(String message, String template) {
        if (template.indexOf(RIGHT_CURLY_BRACES) > 0 && template.indexOf(LEFT_CURLY_BRACES) > 0) {
            String templateWithoutParameters = template.replaceAll("\\{.*?\\} ?", "");
            String[] splitTemplate = templateWithoutParameters.split("\\s+");
            int prevPosition = 0;
            for (int i = 0; i < splitTemplate.length; i++) {
                int foundedIndex = message.indexOf(splitTemplate[i]);
                if (foundedIndex < 0 && foundedIndex < prevPosition) {
                    return false;
                } else {
                    prevPosition = foundedIndex;
                }
            }
            return true;
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
        Logger logger = (Logger) LoggerFactory.getLogger(ROOT_ITEXT_PACKAGE);
        logger.addAppender(listAppender);
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
        if (annotation != null) {
            LogMessages logMessages = (LogMessages) annotation;
            if (!logMessages.ignore()) {
                LogMessage[] messages = logMessages.messages();
                for (LogMessage logMessage : messages) {
                    int foundedCount = contains(logMessage.messageTemplate());
                    if(foundedCount != logMessage.count()){
                        Assert.assertTrue(MessageFormat.format("{0}.{1}: Some log messages are not found in test execution - {2} messages",
                                description.getClassName(),
                                description.getMethodName(),
                                logMessage.count() - foundedCount),
                                false);
                    }
                }
            }
        } else {
            if (getSize() > 0) {
                Assert.assertTrue(MessageFormat.format("{0}.{1}: The test does not check the message logging - {2} messages",
                        description.getClassName(),
                        description.getMethodName(),
                        getSize()),
                        false);
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