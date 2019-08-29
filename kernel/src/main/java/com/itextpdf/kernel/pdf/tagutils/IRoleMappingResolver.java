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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import java.io.Serializable;

/**
 * A helper interface that facilitates roles mapping resolving for the tag structures that are defined for different
 * PDF document specification versions.
 * <p>
 * Be aware, that it is explicitly allowed for the document by the specification to have circular or transitive mappings.
 */
public interface IRoleMappingResolver extends Serializable {

    /**
     * Defines the current role of the resolver. On every successful resolving "step" the role returned by this method
     * changes in order to reflect the mapping of the previous role.
     * @return the {@link String} which identifies current role of the resolver.
     */
    String getRole();

    /**
     * Defines the namespace of the current role.
     * @return the {@link PdfNamespace} instance of the namespace dictionary wrapper. The role returned by the {@link #getRole()}
     * method call is considered to belong to this namespace. Might be null, which means that role belongs to the
     * default standard namespace.
     */
    PdfNamespace getNamespace();

    /**
     * Checks if the current role belongs to one of the standard structure namespaces.
     * @return true if the current namespace is a standard structure namespace and the current role is defined as standard role in it.
     */
    boolean currentRoleIsStandard();

    /**
     * Checks if the current role and namespace are specified to be obligatory mapped to the standard structure namespace
     * in order to be a valid role in the Tagged PDF.
     * @return true, if the current role in the current namespace either belongs to the standard structure roles or is in the
     * domain specific namespace; otherwise false.
     */
    boolean currentRoleShallBeMappedToStandard();

    /**
     * Performs a mapping resolving "step". Essentially finds the role and it's namespace to which the current role is mapped to.
     * After this method call {@link #getRole()} and {@link #getNamespace()} methods might change their return value.
     * @return true if current role and/or namespace have changed their values; otherwise false which means that current
     * role is not mapped.
     */
    boolean resolveNextMapping();
}
