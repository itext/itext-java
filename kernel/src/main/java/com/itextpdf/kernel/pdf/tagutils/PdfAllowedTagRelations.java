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

import com.itextpdf.kernel.pdf.tagging.StandardRoles;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class defines the allowed parent-child relations for the PDF2.0 standard.
 */
public class PdfAllowedTagRelations {

    private static final String ACTUAL_CONTENT = "CONTENT";
    private static final String OBJR_CONTENT = "OBJR";
    public static final String NUMBERED_HEADER = "Hn";


    private static final Pattern numberedHeaderPattern = Pattern.compile("H(\\d+)");

    protected final Map<String, Collection<String>> allowedParentChildRelations = new HashMap<>();

    /**
     * Creates a new instance of {@link PdfAllowedTagRelations}.
     */
    public PdfAllowedTagRelations() {
        allowedParentChildRelations.put(StandardRoles.DOCUMENT, Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.TITLE,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.DOCUMENTFRAGMENT, Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.TITLE,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.PART, Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.TITLE,
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.DIV, Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.TITLE,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.ASIDE, Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.LBL,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.TITLE, Arrays.asList(
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.SUB, Arrays.asList(
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.L,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.P, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        // Headers get a special treatment, since PDF2.0 there is no limit to the number of levels
        allowedParentChildRelations.put(NUMBERED_HEADER, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.H, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.LBL, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.EM, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.STRONG, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.SPAN, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.LINK, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.ANNOT,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT,
                OBJR_CONTENT));

        allowedParentChildRelations.put(StandardRoles.ANNOT, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT,
                OBJR_CONTENT));

        allowedParentChildRelations.put(StandardRoles.FORM, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.LBL,
                StandardRoles.CAPTION,
                StandardRoles.ARTIFACT,
                OBJR_CONTENT));

        allowedParentChildRelations.put(StandardRoles.RUBY, Arrays.asList(
                StandardRoles.RB,
                StandardRoles.RT,
                StandardRoles.RP,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.RB, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.RT, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.RP, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.WARICHU, Arrays.asList(
                StandardRoles.WT,
                StandardRoles.WP,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.WT, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.WP, Arrays.asList(
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.FENOTE, Arrays.asList(
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.L, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.L,
                StandardRoles.LI,
                StandardRoles.CAPTION,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.LI, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.LBL,
                StandardRoles.FENOTE,
                StandardRoles.LBODY,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.LBODY, Arrays.asList(
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.SUB,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.TABLE, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.TR,
                StandardRoles.THEAD,
                StandardRoles.TBODY,
                StandardRoles.TFOOT,
                StandardRoles.CAPTION,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.TR, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.TH,
                StandardRoles.TD,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.TH, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.TD, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.THEAD, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.TR,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.TBODY, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.TR,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.TFOOT, Arrays.asList(
                StandardRoles.DIV,
                StandardRoles.TR,
                StandardRoles.ARTIFACT));

        allowedParentChildRelations.put(StandardRoles.CAPTION, Arrays.asList(
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.FIGURE, Arrays.asList(
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.FORMULA, Arrays.asList(
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.SUB,
                StandardRoles.LBL,
                StandardRoles.EM,
                StandardRoles.STRONG,
                StandardRoles.SPAN,
                StandardRoles.LINK,
                StandardRoles.ANNOT,
                StandardRoles.FORM,
                StandardRoles.RUBY,
                StandardRoles.WARICHU,
                StandardRoles.FENOTE,
                StandardRoles.L,
                StandardRoles.TABLE,
                StandardRoles.CAPTION,
                StandardRoles.FIGURE,
                StandardRoles.FORMULA,
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));

        allowedParentChildRelations.put(StandardRoles.ARTIFACT, Arrays.asList(
                StandardRoles.DOCUMENT,
                StandardRoles.DOCUMENTFRAGMENT,
                StandardRoles.PART,
                StandardRoles.DIV,
                StandardRoles.ASIDE,
                StandardRoles.P,
                NUMBERED_HEADER,
                StandardRoles.H,
                StandardRoles.TITLE,
                StandardRoles.SUB,
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
                StandardRoles.ARTIFACT,
                ACTUAL_CONTENT));
    }

    /**
     * Checks if the given parent-child relation is allowed.
     *
     * @param parentRole The parent role.
     * @param childRole  The child role.
     * @return {@code true} if the relation is allowed, {@code false} otherwise.
     */
    public boolean isRelationAllowed(String parentRole, String childRole) {
        Collection<String> allowedChildren = allowedParentChildRelations.get(normalizeRole(parentRole));
        if (allowedChildren != null) {
            return allowedChildren.contains(normalizeRole(childRole));
        }
        throw new IllegalArgumentException("parentRole " + parentRole + " is not a valid structure tree role");
    }

    /**
     * Checks if the given parent role allows content.
     *
     * @param parentRole The parent role.
     * @return {@code true} if the parent role allows content, {@code false} otherwise.
     */
    public boolean isContentAllowedInRole(String parentRole) {
        Collection<String> allowedChildren = allowedParentChildRelations.get(normalizeRole(parentRole));
        if (allowedChildren != null) {
            return allowedChildren.contains(ACTUAL_CONTENT);
        }
        throw new IllegalArgumentException("parentRole " + parentRole + " is not a valid structure tree role");
    }

    /**
     * Checks if the given parent role allows content object.
     *
     * @param parentRole The parent role.
     * @return {@code true} if the parent role allows content object, {@code false} otherwise.
     */
    public boolean isContentObjectAllowedInRole(String parentRole) {
        Collection<String> allowedChildren = allowedParentChildRelations.get(normalizeRole(parentRole));
        if (allowedChildren != null) {
            return allowedChildren.contains(OBJR_CONTENT);
        }
        throw new IllegalArgumentException("parentRole " + parentRole + " is not a valid structure tree role");
    }

    /**
     * Normalizes the role.
     *
     * @param role The role to normalize.
     * @return The normalized role.
     */
    public String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        if (numberedHeaderPattern.matcher(role).matches()) {
            return NUMBERED_HEADER;
        }
        return role;
    }
}
