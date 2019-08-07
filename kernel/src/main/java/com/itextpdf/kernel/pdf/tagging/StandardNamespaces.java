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
package com.itextpdf.kernel.pdf.tagging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class encapsulates information about the standard structure namespaces and provides some utility methods
 * connected to them. The main purpose of this class is to determine if the given role in the specified namespace
 * belongs to the standard or known domain-specific namespace.
 * <p>
 * See ISO 32000-2 14.8.6, "Standard structure namespaces"
 */
public final class StandardNamespaces {
    private static final Set<String> STD_STRUCT_NAMESPACE_1_7_TYPES;
    private static final Set<String> STD_STRUCT_NAMESPACE_2_0_TYPES;

    // other namespaces
    private static final String MATH_ML = "http://www.w3.org/1998/Math/MathML";

    /**
     * Specifies the name of the standard structure namespace for PDF 1.7
     */
    public static final String PDF_1_7 = "http://iso.org/pdf/ssn";

    /**
     * Specifies the name of the standard structure namespace for PDF 2.0
     */
    public static final String PDF_2_0 = "http://iso.org/pdf2/ssn";

    static {
        STD_STRUCT_NAMESPACE_1_7_TYPES = new HashSet<>(Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.P,
                StandardRoles.H,
                StandardRoles.H1,
                StandardRoles.H2,
                StandardRoles.H3,
                StandardRoles.H4,
                StandardRoles.H5,
                StandardRoles.H6,
                StandardRoles.LBL,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.RB,
                StandardRoles.RT,
                StandardRoles.RP,
                StandardRoles.WARICHU,
                StandardRoles.WT,
                StandardRoles.WP,
                StandardRoles.L,
                StandardRoles.LI,
                StandardRoles.LBODY,
                StandardRoles.TABLE,
                StandardRoles.TR,
                StandardRoles.TH,
                StandardRoles.TD,
                StandardRoles.THEAD,
                StandardRoles.TBODY,
                StandardRoles.TFOOT,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,

                StandardRoles.SECT,
                StandardRoles.ART,
                StandardRoles.BLOCKQUOTE,
                StandardRoles.TOC,
                StandardRoles.TOCI,
                StandardRoles.INDEX,
                StandardRoles.NONSTRUCT,
                StandardRoles.PRIVATE,
                StandardRoles.QUOTE,
                StandardRoles.NOTE,
                StandardRoles.REFERENCE,
                StandardRoles.BIBENTRY,
                StandardRoles.CODE
        ));


        STD_STRUCT_NAMESPACE_2_0_TYPES = new HashSet<>(Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.TITLE,
                StandardRoles.SUB,
                StandardRoles.P,
                StandardRoles.H,
                // Hn, this type is handled in roleBelongsToStandardNamespace method
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.RB,
                StandardRoles.RT,
                StandardRoles.RP,
                StandardRoles.WARICHU,
                StandardRoles.WT,
                StandardRoles.WP,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.LI,
                StandardRoles.LBODY,
                StandardRoles.TABLE,
                StandardRoles.TR,
                StandardRoles.TH,
                StandardRoles.TD,
                StandardRoles.THEAD,
                StandardRoles.TBODY,
                StandardRoles.TFOOT,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT

        ));
    }

    /**
     * Gets the name of the default standard structure namespace. When a namespace is not
     * explicitly specified for a given structure element or attribute, it shall be assumed to be within this
     * default standard structure namespace. According to ISO 32000-2 default namespace is {@link StandardNamespaces#PDF_1_7}.
     * @return the name of the default standard structure namespace.
     */
    public static String getDefault() {
        return PDF_1_7;
    }

    /**
     * Checks if the given namespace is identified as the one that is common within broad ranges of documents types
     * and doesn't require a role mapping for it's roles.
     * @param namespace a namespace to be checked, whether it defines a namespace of the known domain specific language.
     * @return true, if the given {@link PdfNamespace} belongs to the domain-specific namespace, false otherwise.
     */
    public static boolean isKnownDomainSpecificNamespace(PdfNamespace namespace) {
        return MATH_ML.equals(namespace.getNamespaceName());
    }

    /**
     * Checks if the given role is considered standard in the specified standard namespace.
     * @param role a role to be checked if it is standard in the given standard structure namespace.
     * @param standardNamespaceName a {@link String} identifying standard structure namespace against which given role
     *                              will be checked.
     * @return false if the given role doesn't belong to the standard roles of the given standard structure namespace or
     * if the given namespace name is not standard; true otherwise.
     */
    public static boolean roleBelongsToStandardNamespace(String role, String standardNamespaceName) {
        if (PDF_1_7.equals(standardNamespaceName)) {
            return STD_STRUCT_NAMESPACE_1_7_TYPES.contains(role);
        } else if (PDF_2_0.equals(standardNamespaceName)) {
            return STD_STRUCT_NAMESPACE_2_0_TYPES.contains(role) || isHnRole(role);
        }

        return false;
    }

    /**
     * Checks if the given role matches the Hn role pattern. To match this pattern, the given role
     * shall always consist of the uppercase letter "H" and one or more digits, representing an unsigned integer
     * greater than or equal to 1, without leading zeroes or any other prefix or postfix.
     * @param role a {@link String} that specifies a role to be checked against Hn role pattern.
     * @return true if the role matches, false otherwise.
     */
    public static boolean isHnRole(String role) {
        if (role.startsWith("H") && role.length() > 1 && role.charAt(1) != '0') {
            try {
                return Integer.parseInt(role.substring(1, role.length())) > 0;
            } catch (Exception ex) {
                // ignored
            }
        }
        return false;
    }
}
