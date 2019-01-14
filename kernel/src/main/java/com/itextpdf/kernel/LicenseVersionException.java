/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel;

import com.itextpdf.io.util.MessageFormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception class for License-key version exceptions throw in the Version class
 */
public class LicenseVersionException extends  RuntimeException{

    public static final String NO_I_TEXT7_LICENSE_IS_LOADED_BUT_AN_I_TEXT5_LICENSE_IS_LOADED ="No iText7 License is loaded but an iText5 license is loaded.";
    public static final String THE_MAJOR_VERSION_OF_THE_LICENSE_0_IS_LOWER_THAN_THE_MAJOR_VERSION_1_OF_THE_CORE_LIBRARY ="The major version of the license ({0}) is lower than the major version ({1}) of the Core library.";
    public static final String THE_MAJOR_VERSION_OF_THE_LICENSE_0_IS_HIGHER_THAN_THE_MAJOR_VERSION_1_OF_THE_CORE_LIBRARY ="The major version of the license ({0}) is higher than the major version ({1}) of the Core library.";
    
    public static final String THE_MINOR_VERSION_OF_THE_LICENSE_0_IS_LOWER_THAN_THE_MINOR_VERSION_1_OF_THE_CORE_LIBRARY ="The minor version of the license ({0}) is lower than the minor version ({1}) of the Core library.";
    public static final String THE_MINOR_VERSION_OF_THE_LICENSE_0_IS_HIGHER_THAN_THE_MINOR_VERSION_1_OF_THE_CORE_LIBRARY ="The minor version of the license ({0}) is higher than the minor version ({1}) of the Core library.";

    public static final String VERSION_STRING_IS_EMPTY_AND_CANNOT_BE_PARSED = "Version string is empty and cannot be parsed.";
    public static final String MAJOR_VERSION_IS_NOT_NUMERIC ="Major version is not numeric";
    public static final String MINOR_VERSION_IS_NOT_NUMERIC ="Minor version is not numeric";
    public static final String UNKNOWN_EXCEPTION_WHEN_CHECKING_LICENSE_VERSION ="Unknown Exception when checking License version";

    public static final String LICENSE_FILE_NOT_LOADED = "License file not loaded.";
    /**
     * Object for more details
     */
    protected Object object;

    private List<Object> messageParams;

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     */
    public LicenseVersionException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public LicenseVersionException(Throwable cause) {
        this(UNKNOWN_EXCEPTION_WHEN_CHECKING_LICENSE_VERSION, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param obj     an object for more details.
     */
    public LicenseVersionException(String message, Object obj) {
        this(message);
        this.object = obj;
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public LicenseVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     * @param obj     an object for more details.
     */
    public LicenseVersionException(String message, Throwable cause, Object obj) {
        this(message, cause);
        this.object = obj;
    }

    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.size() == 0) {
            return super.getMessage();
        } else {
            return MessageFormatUtil.format(super.getMessage(), getMessageParams());
        }
    }

    /**
     * Sets additional params for Exception message.
     *
     * @param messageParams additional params.
     * @return object itself.
     */
    public LicenseVersionException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }

    /**
     * Gets additional params for Exception message.
     */
    protected Object[] getMessageParams() {
        Object[] parameters = new Object[messageParams.size()];
        for (int i = 0; i < messageParams.size(); i++) {
            parameters[i] = messageParams.get(i);
        }
        return parameters;
    }
}
