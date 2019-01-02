/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import org.junit.Assert;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogListenerHelper {

    public static <T extends Annotation> T getTestAnnotation(Description description, Class<T> annotationClass) {
        T annotation = description.getAnnotation(annotationClass);
        if (annotation == null) {
            annotation = description.getTestClass().getAnnotation(annotationClass);
        }
        return annotation;
    }

    public static void failWrongMessageCount(int expected, int actual, String messageTemplate, Description description) {
        Assert.fail(MessageFormat.format("{0}:{1} Expected to find {2}, but found {3} messages with the following content: \"{4}\"",
                description.getClassName(), description.getMethodName(), expected, actual, messageTemplate));
    }

    public static void failWrongTotalCount(int expected, int actual, Description description) {
        Assert.fail(MessageFormat.format("{0}.{1}: The test does not check the message logging - {2} messages",
                description.getClassName(),
                description.getMethodName(),
                expected - actual));
    }

    /*
    * compare  parametrized message with  base template, for example:
    *  "Hello fox1 , World  fox2 !" with "Hello {0} , World {1} !"
    * */
    public static boolean equalsMessageByTemplate(String message, String template) {
        if (template.contains("{") && template.contains("}")) {
            String templateWithoutParameters = template.replace("''", "'").replaceAll("\\{[0-9]+?\\}", "(.)*?");
            Pattern p = Pattern.compile(templateWithoutParameters, Pattern.DOTALL);
            Matcher m = p.matcher(message);
            return m.matches();
        } else {
            return message.contains(template);
        }
    }
}
