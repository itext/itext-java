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
package com.itextpdf.styledxmlparser.css.validate.impl.datatype;


import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

/**
 * {@link ICssDataTypeValidator} implementation for identifiers.
 * In CSS, identifiers (including element names, classes, and IDs in selectors) can contain only the characters [a-zA-Z0-9]
 * and ISO 10646 characters U+00A0 and higher, plus the hyphen (-) and the underscore (_);
 * they cannot start with a digit, two hyphens, or a hyphen followed by a digit.
 * Identifiers can also contain escaped characters and any ISO 10646 character as a numeric code (see next item).
 * For instance, the identifier "B&amp;W?" may be written as "B\&amp;W\?" or "B\26 W\3F".
 */
public class CssIdentifierValidator implements ICssDataTypeValidator {

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String objectString) {
        // TODO now the validation is very lenient. Make it more strict
        if (objectString.length() >= 2 && objectString.startsWith("--")) {
            return false;
        } else if (objectString.matches("^[0-9].*")) {
            return false;
        } else {
            return true;
        }
    }

}
