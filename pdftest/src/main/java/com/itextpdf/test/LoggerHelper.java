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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import org.junit.Assert;
import org.junit.runner.Description;
import org.slf4j.LoggerFactory;

public class LoggerHelper {

    static <T extends Annotation> T getTestAnnotation(Description description, Class<T> annotationClass) {
        T annotation = description.getAnnotation(annotationClass);
        if (annotation == null) {
            annotation = description.getTestClass().getAnnotation(annotationClass);
        }
        return annotation;
    }

    static void failWrongMessageCount(int expected, int actual, String messageTemplate, Description description) {
        Assert.fail(MessageFormat.format("{0}:{1} Expected to find {2}, but found {3} messages with the following content: \"{4}\"",
                description.getClassName(), description.getMethodName(), expected, actual, messageTemplate));
    }

    static void failWrongTotalCount(int expected, int actual, Description description) {
        Assert.fail(MessageFormat.format("{0}.{1}: The test does not check the message logging - {2} messages",
                description.getClassName(),
                description.getMethodName(),
                expected - actual));
    }

    /*
    * compare  parametrized message with  base template, for example:
    *  "Hello fox1 , World  fox2 !" with "Hello {0} , World {1} !"
    * */
    static boolean equalsMessageByTemplate(String message, String template) {
        if (template.contains("{") && template.contains("}")) {
            // Note: The escape on '}' is necessary for regex dialect compatibility reasons.
            String templateWithoutParameters = Pattern.quote(template).replace("''", "'").replaceAll("\\{[0-9]+?\\}", "\\\\E(.)*?\\\\Q");
            Pattern p = Pattern.compile(templateWithoutParameters, Pattern.DOTALL);
            return p.matcher(message).matches();
        } else {
            return message.contains(template);
        }
    }

    public static void restoreAppenders(Map<Logger, Map<String, Appender<ILoggingEvent>>> appenders) {
        for (Logger logger : appenders.keySet()) {
            Map<String, Appender<ILoggingEvent>> appenderMap = appenders.get(logger);
            Logger currentLogger = (Logger) LoggerFactory.getLogger(logger.getName());
            for (String appenderName : appenderMap.keySet()) {
                currentLogger.addAppender(appenderMap.get(appenderName));
            }
        }
    }

    public static Map<Logger, Map<String, Appender<ILoggingEvent>>> getAllAppendersMap(LoggerContext loggerContext) {
        Map<Logger, Map<String, Appender<ILoggingEvent>>> resultMap = new HashMap<>();
        for (Logger logger : loggerContext.getLoggerList()) {
            Map<String, Appender<ILoggingEvent>> appendersMap = new HashMap<>();

            Iterator<Appender<ILoggingEvent>> appenderIterator = logger.iteratorForAppenders();
            while (appenderIterator.hasNext()) {
                Appender<ILoggingEvent> appender = appenderIterator.next();
                appendersMap.put(appender.getName(), appender);
            }

            resultMap.put(logger, appendersMap);
        }

        return resultMap;
    }
}
