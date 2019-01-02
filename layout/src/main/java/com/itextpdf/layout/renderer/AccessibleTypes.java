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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * This class is used to identify standard structure role type based only on it's name for the sake of applying
 * standard structure attributes.
 *
 * <p>
 * These types mostly resemble structure type levels in the pdf 1.7 specification, however they are not exact.
 * In pdf 2.0 some of these types are not even present and moreover, specific roles with the same name might belong
 * to different type levels depending on context (which consists of kids, parents and their types).
 * </p>
 *
 * <p>
 * So, these types are mostly useful for the internal itext usage and are not backed by any spec. They are designed for
 * the most part to return the value the most suitable and handy for the purposes of accessibility properties applying.
 * </p>
 *
 * <p>
 * Here are the main reasons to leave these types as is for now, even after introducing of PDF 2.0:
 * <ul>
 *     <li>Standard structure types for pdf 1.7 and 2.0 are very alike. There are some differences, like new/removed roles
 *     and attributes, however they are not used in current layout auto tagging mechanism.
 *     </li>
 *     <li>Differentiating  possible types for the same role based on the context is not supported at the moment.</li>
 * </ul>
 * In general, the correct way to handle role types would be to have separate classes for every namespace that define type
 * and apply attributes. However I believe, that for now it is not feasible at the moment to implement this approach.
 * </p>
 *
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
     *     <li>{@link #Unknown}</li>
     *     <li>{@link #Grouping}</li>
     *     <li>{@link #BlockLevel}</li>
     *     <li>{@link #InlineLevel}</li>
     *     <li>{@link #Illustration}</li>
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
