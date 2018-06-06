/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.kernel.counter;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.kernel.counter.context.GenericContext;
import com.itextpdf.kernel.counter.context.IContext;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class that retrieves context of its invocation.
 */
public class ContextManager {

    private static final ContextManager instance = new ContextManager();

    private static final long SECURITY_ERROR_LOGGING_INTERVAL = 60000;
    private volatile long securityErrorLastLogged = 0;

    private final Map<String, IContext> contextMappings = new ConcurrentHashMap<>();

    private ContextManager() {
        registerGenericContext(Arrays.asList(
                NamespaceConstant.CORE_IO,
                NamespaceConstant.CORE_KERNEL,
                NamespaceConstant.CORE_LAYOUT,
                NamespaceConstant.CORE_BARCODES,
                NamespaceConstant.CORE_PDFA,
                NamespaceConstant.CORE_SIGN,
                NamespaceConstant.CORE_FORMS,
                NamespaceConstant.CORE_SXP,
                NamespaceConstant.CORE_SVG), Collections.singletonList(NamespaceConstant.ITEXT));
        registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_DEBUG), Collections.singletonList(NamespaceConstant.PDF_DEBUG));
        registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_HTML), Collections.singletonList(NamespaceConstant.PDF_HTML));
        registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_INVOICE), Collections.singletonList(NamespaceConstant.PDF_INVOICE));
        registerGenericContext(Collections.singletonList(NamespaceConstant.PDF_SWEEP), Collections.singletonList(NamespaceConstant.PDF_SWEEP));
    }

    /**
     * Gets the singelton instance of this class
     *
     * @return the {@link ContextManager} instance
     */
    public static ContextManager getInstance() {
        return instance;
    }

    /**
     * Gets the top {@link IContext} based on the stack trace.
     *
     * For example if the {@link PdfDocument} was created inside pdfHtml,
     * and the pdfHtml was called directly,
     * then calling this method from {@link PdfDocument} will retrieve pdfHtml context.
     *
     * @return the top {@link IContext} instance, or {@code null} if the context is unknown
     */
    public IContext getTopContext() {
        return getTopContext(null);
    }

    /**
     * Gets the top {@link IContext} based on the stack trace.
     *
     * For example if the {@link PdfDocument} was created inside pdfHtml,
     * and the pdfHtml was called directly,
     * then calling this method from {@link PdfDocument} will retrieve pdfHtml context.
     *
     * @param stop  if this class is encountered during stack trace analysis,
     *              then the process is stopped and {@code null} is returned
     * @return      the top {@link IContext} instance, or {@code null}
     *              if the context is unknown or {@code stop} is encountered
     */
    public IContext getTopContext(Class<?> stop) {
        return getNamespaceMapping(getTopRecognisedNamespace(stop));
    }

    /**
     * Gets the top recognised namespace based on the stack trace.
     *
     * For example if the {@link PdfDocument} was created inside pdfHtml,
     * and the pdfHtml was called directly,
     * then calling this method from {@link PdfDocument} will retrieve pdfHtml namespace.
     *
     * @return the top recognised namespace, or {@code null} if it is unrecognised
     */
    public String getTopRecognisedNamespace() {
        return getTopRecognisedNamespace(null);
    }

    /**
     * Gets the top recognised namespace based on the stack trace.
     *
     * For example if the {@link PdfDocument} was created inside pdfHtml,
     * and the pdfHtml was called directly,
     * then calling this method from {@link PdfDocument} will retrieve pdfHtml namespace.
     *
     * @param stop  if this class is encountered during stack trace analysis,
     *              then the process is stopped and {@code null} is returned
     * @return the top recognised namespace, or {@code null} if the context is unknown
     */
    public String getTopRecognisedNamespace(Class<?> stop) {
        if (stop == null) {
            stop = getClass();
        }
        try {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            String result;
            for(int i=trace.length-1; i>=0; --i){
                if (stop.getName().equals(trace[i].getClassName())) {
                    return null;
                }
                result = getRecognisedNamespace(trace[i].getClassName());
                if (result != null) {
                    return result;
                }
            }
        } catch (SecurityException e) {
            long current = SystemUtil.getRelativeTimeMillis();
            if (current - securityErrorLastLogged > SECURITY_ERROR_LOGGING_INTERVAL) {
                LoggerFactory.getLogger(getClass()).error(LogMessageConstant.UNABLE_TO_SEARCH_FOR_EVENT_CONTEXT);
                securityErrorLastLogged = current;
            }
        }
        return null;
    }

    private String getRecognisedNamespace(String className) {
        if (className != null) {
            for (String namespace : contextMappings.keySet()) {
                //Conversion to lowercase is done to be compatible with possible changes in case of packages/namespaces
                if (className.toLowerCase().startsWith(namespace)) {
                    return namespace;
                }
            }
        }
        return null;
    }

    private IContext getNamespaceMapping(String namespace) {
        if (namespace != null) {
            return contextMappings.get(namespace);
        }
        return null;
    }

    private void registerGenericContext(Collection<String> namespaces, Collection<String> eventIds) {
        GenericContext context = new GenericContext(eventIds);
        for (String namespace : namespaces) {
            //Conversion to lowercase is done to be compatible with possible changes in case of packages/namespaces
            registerContext(namespace.toLowerCase(), context);
        }
    }
    
    private void registerContext(String namespace, IContext context) {
        contextMappings.put(namespace, context);
    }
}
