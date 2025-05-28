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
package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;
import com.itextpdf.kernel.pdf.tagutils.PdfAllowedTagRelations;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is used to resolve prohibited relations between parent and child tags.
 */
public class ProhibitedTagRelationsResolver {

    private static final Map<String, String> updateRules20 = new HashMap<>();
    private static final Map<String, String> updateRules17 = new HashMap<>();
    private static final List<String> rolesToSkip = Arrays.asList(
            StandardRoles.DIV,
            StandardRoles.NONSTRUCT,
            null);
    private static final PdfAllowedTagRelations allowedRelations = new PdfAllowedTagRelations();

    private final PdfDocument pdfDocument;
    private final Map<String, String> overriddenRoles = new HashMap<>();

    static {

        //PDF 1.7 rules
        updateRules17.put(generateKey(StandardRoles.H, StandardRoles.P), StandardRoles.SPAN);
        updateRules17.put(generateKey(StandardRoles.P, StandardRoles.P), StandardRoles.SPAN);
        updateRules17.put(generateKey(StandardRoles.P, StandardRoles.DIV), StandardRoles.SPAN);
        updateRules17.put(generateKey(StandardRoles.TOC, StandardRoles.SPAN), StandardRoles.CAPTION);
        updateRules17.put(generateKey(StandardRoles.TOCI, StandardRoles.SPAN), StandardRoles.LBL);


        //PDF 2.0 rules
        updateRules20.put(generateKey(StandardRoles.H, StandardRoles.P), StandardRoles.SUB);
        updateRules20.put(generateKey(PdfAllowedTagRelations.NUMBERED_HEADER, StandardRoles.P), StandardRoles.SUB);

        updateRules20.put(generateKey(StandardRoles.FORM, StandardRoles.P), StandardRoles.LBL);
        updateRules20.put(generateKey(StandardRoles.FORM, StandardRoles.FORM), StandardRoles.DIV);
        updateRules20.put(generateKey(StandardRoles.FORM, StandardRoles.SPAN), StandardRoles.LBL);
        updateRules20.put(generateKey(StandardRoles.FORM, PdfAllowedTagRelations.NUMBERED_HEADER), StandardRoles.LBL);

        updateRules20.put(generateKey(StandardRoles.LBL, StandardRoles.P), StandardRoles.SPAN);

        updateRules20.put(generateKey(StandardRoles.P, StandardRoles.P), StandardRoles.SPAN);
        updateRules20.put(generateKey(StandardRoles.P, StandardRoles.DIV), StandardRoles.SUB);

        updateRules20.put(generateKey(StandardRoles.SPAN, StandardRoles.P), StandardRoles.SPAN);
        updateRules20.put(generateKey(StandardRoles.SPAN, StandardRoles.DIV), StandardRoles.SUB);

        updateRules20.put(generateKey(StandardRoles.SUB, StandardRoles.P), StandardRoles.SPAN);
        updateRules20.put(generateKey(StandardRoles.SUB, StandardRoles.SUB), StandardRoles.SPAN);
        updateRules20.put(generateKey(StandardRoles.SUB, StandardRoles.DIV), StandardRoles.SPAN);

        updateRules20.put(generateKey(StandardRoles.TOC, StandardRoles.SPAN), StandardRoles.CAPTION);
        updateRules20.put(generateKey(StandardRoles.TOCI, StandardRoles.SPAN), StandardRoles.LBL);

        updateRules20.put(generateKey(StandardRoles.DOCUMENT, StandardRoles.SPAN), StandardRoles.P);

    }

    /**
     * Creates a new instance of {@link ProhibitedTagRelationsResolver}.
     *
     * @param pdfDocument the document to be processed.
     */
    public ProhibitedTagRelationsResolver(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Resolves prohibited relations between parent and child tags.
     *
     * @param taggingHelper the tagging helper.
     * @param topRender     the top renderer.
     */
    public void repairTagStructure(LayoutTaggingHelper taggingHelper, IRenderer topRender) {
        final TaggingHintKey currentThk = LayoutTaggingHelper.getOrCreateHintKey(topRender);
        if (!currentThk.isAccessible()) {
            return;
        }
        final String normalizedParentRole = resolveToFinalRole(taggingHelper, currentThk, false);
        for (IRenderer childRenderer : topRender.getChildRenderers()) {
            if (childRenderer instanceof AreaBreakRenderer) {
                continue;
            }
            final TaggingHintKey kid = LayoutTaggingHelper.getOrCreateHintKey(childRenderer);
            if (!kid.isAccessible()) {
                continue;
            }
            //To not change the role of non-struct elements
            if (isKidNonStructElement(kid)) {
                continue;
            }
            final String normalizedKidRole = resolveToFinalRole(taggingHelper, kid, true);
            final String key = generateKey(normalizedParentRole, normalizedKidRole);
            executeRoleReplacementRule(kid, key);
        }
    }

    /**
     * Overwrites tagging rule if it already exists. Otherwise, adds the new rule.
     *
     * @param parentRole The parent role.
     * @param childRole  The child role.
     * @param newRole    The new role the child should have.
     */
    public void overwriteTaggingRule(String parentRole, String childRole, String newRole) {
        overriddenRoles.put(generateKey(parentRole, childRole), newRole);
    }

    private void executeRoleReplacementRule(TaggingHintKey kid, String key) {
        Map<String, String> updateRules = PdfVersion.PDF_2_0.equals(
                pdfDocument.getPdfVersion()) ? updateRules20 : updateRules17;
        if (updateRules.containsKey(key)) {
            kid.setOverriddenRole(updateRules.get(key));
        }
        if (overriddenRoles.containsKey(key)) {
            kid.setOverriddenRole(overriddenRoles.get(key));
        }
    }

    private static boolean isKidNonStructElement(TaggingHintKey kid) {
        if (kid.getAccessibleElement() == null) {
            return false;
        }
        if (kid.getAccessibleElement().getAccessibilityProperties() == null) {
            return false;
        }
        return StandardRoles.NONSTRUCT.equals(kid.getAccessibleElement().getAccessibilityProperties().getRole())
                || StandardRoles.NONSTRUCT.equals(kid.getOverriddenRole());
    }

    private static String generateKey(String parentRole, String childRole) {
        return parentRole + ":" + childRole;
    }

    private String resolveToFinalRole(LayoutTaggingHelper helper, TaggingHintKey taggingHintKey, boolean isKid) {
        String role = taggingHintKey.getAccessibilityProperties().getRole();
        if (taggingHintKey.getOverriddenRole() != null) {
            role = taggingHintKey.getOverriddenRole();
        }
        role = resolveToStandardRole(role);
        role = allowedRelations.normalizeRole(role);
        if (isKid) {
            return role;
        }
        if (rolesToSkip.contains(role)) {
            return getParentRole(helper, taggingHintKey, rolesToSkip);
        }
        return role;

    }

    private String getParentRole(LayoutTaggingHelper helper, TaggingHintKey hintKey, List<String> rolesToSkip) {
        String currentRole = hintKey.getAccessibilityProperties().getRole();
        if (hintKey.getOverriddenRole() != null) {
            currentRole = hintKey.getOverriddenRole();
        }
        currentRole = resolveToStandardRole(currentRole);
        if (!rolesToSkip.contains(currentRole)) {
            return currentRole;
        }
        TaggingHintKey parent = helper.getAccessibleParentHint(hintKey);
        if (parent == null) {
            return null;
        }
        return getParentRole(helper, parent, rolesToSkip);
    }

    private String resolveToStandardRole(String role) {
        if (role == null) {
            return null;
        }
        final TagStructureContext tagStructureContext = pdfDocument.getTagStructureContext();
        final IRoleMappingResolver resolver = tagStructureContext
                .resolveMappingToStandardOrDomainSpecificRole(role, tagStructureContext.getDocumentDefaultNamespace());
        if (resolver == null) {
            return role;
        }
        return resolver.getRole();
    }

}

