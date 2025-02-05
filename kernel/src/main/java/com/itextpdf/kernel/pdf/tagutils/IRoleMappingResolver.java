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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.PdfNamespace;

/**
 * A helper interface that facilitates roles mapping resolving for the tag structures that are defined for different
 * PDF document specification versions.
 * <p>
 * Be aware, that it is explicitly allowed for the document by the specification to have circular or transitive mappings.
 */
public interface IRoleMappingResolver {

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
