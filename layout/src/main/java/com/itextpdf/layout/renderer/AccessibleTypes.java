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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to identify standard structure role type based only on it's name for the sake of applying
 * standard structure attributes.
 * <p>
 * These types mostly resemble structure type levels in the pdf 1.7 specification, however they are not exact.
 * In pdf 2.0 some of these types are not even present and moreover, specific roles with the same name might belong
 * to different type levels depending on context (which consists of kids, parents and their types).
 * <p>
 * So, these types are mostly useful for the internal itext usage and are not backed by any spec. They are designed for
 * the most part to return the value the most suitable and handy for the purposes of accessibility properties applying.
 * <p>
 * Here are the main reasons to leave these types as is for now, even after introducing of PDF 2.0:
 * <ul>
 *     <li>Standard structure types for pdf 1.7 and 2.0 are very alike. There are some differences, like new/removed roles
 *     and attributes, however they are not used in current layout auto tagging mechanism.
 *     <li>Differentiating  possible types for the same role based on the context is not supported at the moment.
 * </ul>
 * In general, the correct way to handle role types would be to have separate classes for every namespace that define type
 * and apply attributes. However I believe, that for now it is not feasible at the moment to implement this approach.
 * The right time to improve and replace this class might be when new roles and attributes (specific to the different standard structure namespaces)
 * will be more widely used in the auto tagging mechanism by default, and also when may be there will be more known
 * practical examples of utilizing standard structure attributes.
 */
class AccessibleTypes {

    static int Unknown = 0;
    static int Grouping = 1;
    static int BlockLevel = 2;
    static int InlineLevel = 3;
    static int Illustration = 4;

    static Set<String> groupingRoles = new HashSet<>();
    static Set<String> blockLevelRoles = new HashSet<>();
    static Set<String> inlineLevelRoles = new HashSet<>();
    static Set<String> illustrationRoles = new HashSet<>();

    static {

        // Some tag roles are not in any of the sets that define types. Some - because we don't want to write any accessibility
        // properties for them, some - because they are ambiguous for different pdf versions or don't have any possible
        // properties to set at the moment.
//        StandardStructureTypes.Document
//        StandardStructureTypes.DocumentFragment
//        StandardStructureTypes.Artifact
//        StandardStructureTypes.THead
//        StandardStructureTypes.TBody
//        StandardStructureTypes.TFoot

        groupingRoles.add(StandardRoles.PART);
        groupingRoles.add(StandardRoles.ART);
        groupingRoles.add(StandardRoles.SECT);
        groupingRoles.add(StandardRoles.DIV);
        groupingRoles.add(StandardRoles.BLOCKQUOTE);
        groupingRoles.add(StandardRoles.CAPTION);
        groupingRoles.add(StandardRoles.TOC);
        groupingRoles.add(StandardRoles.TOCI);
        groupingRoles.add(StandardRoles.INDEX);
        groupingRoles.add(StandardRoles.NONSTRUCT);
        groupingRoles.add(StandardRoles.PRIVATE);
        groupingRoles.add(StandardRoles.ASIDE);

        blockLevelRoles.add(StandardRoles.P);
        blockLevelRoles.add(StandardRoles.H);
        blockLevelRoles.add(StandardRoles.H1);
        blockLevelRoles.add(StandardRoles.H2);
        blockLevelRoles.add(StandardRoles.H3);
        blockLevelRoles.add(StandardRoles.H4);
        blockLevelRoles.add(StandardRoles.H5);
        blockLevelRoles.add(StandardRoles.H6);
        // Hn type is handled separately in identifyType method
        blockLevelRoles.add(StandardRoles.L);
        blockLevelRoles.add(StandardRoles.LBL);
        blockLevelRoles.add(StandardRoles.LI);
        blockLevelRoles.add(StandardRoles.LBODY);
        blockLevelRoles.add(StandardRoles.TABLE);
        blockLevelRoles.add(StandardRoles.TR);
        blockLevelRoles.add(StandardRoles.TH);
        blockLevelRoles.add(StandardRoles.TD);
        blockLevelRoles.add(StandardRoles.TITLE);
        blockLevelRoles.add(StandardRoles.FENOTE);
        blockLevelRoles.add(StandardRoles.SUB);
        blockLevelRoles.add(StandardRoles.CAPTION);

        inlineLevelRoles.add(StandardRoles.SPAN);
        inlineLevelRoles.add(StandardRoles.QUOTE);
        inlineLevelRoles.add(StandardRoles.NOTE);
        inlineLevelRoles.add(StandardRoles.REFERENCE);
        inlineLevelRoles.add(StandardRoles.BIBENTRY);
        inlineLevelRoles.add(StandardRoles.CODE);
        inlineLevelRoles.add(StandardRoles.LINK);
        inlineLevelRoles.add(StandardRoles.ANNOT);
        inlineLevelRoles.add(StandardRoles.RUBY);
        inlineLevelRoles.add(StandardRoles.WARICHU);
        inlineLevelRoles.add(StandardRoles.RB);
        inlineLevelRoles.add(StandardRoles.RT);
        inlineLevelRoles.add(StandardRoles.RP);
        inlineLevelRoles.add(StandardRoles.WT);
        inlineLevelRoles.add(StandardRoles.WP);
        inlineLevelRoles.add(StandardRoles.EM);
        inlineLevelRoles.add(StandardRoles.STRONG);

        illustrationRoles.add(StandardRoles.FIGURE);
        illustrationRoles.add(StandardRoles.FORMULA);
        illustrationRoles.add(StandardRoles.FORM);
    }

    /**
     * Identifies standard structure role type based only on it's name. The return types might be one of the constants:
     * <ul>
     *     <li>{@link #Unknown}
     *     <li>{@link #Grouping}
     *     <li>{@link #BlockLevel}
     *     <li>{@link #InlineLevel}
     *     <li>{@link #Illustration}
     * </ul>
     * See also remarks in the {@link AccessibleTypes} class documentation.
     */
    static int identifyType(String role) {
        if (groupingRoles.contains(role)) {
            return Grouping;
        } else if (blockLevelRoles.contains(role) || StandardNamespaces.isHnRole(role)) {
            return BlockLevel;
        } else if (inlineLevelRoles.contains(role)) {
            return InlineLevel;
        } else if (illustrationRoles.contains(role)) {
            return Illustration;
        }

        return Unknown;
    }
}
