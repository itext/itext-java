package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.StandardStructureNamespace;
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

    static Set<PdfName> groupingRoles = new HashSet<PdfName>();
    static Set<PdfName> blockLevelRoles = new HashSet<PdfName>();
    static Set<PdfName> inlineLevelRoles = new HashSet<PdfName>();
    static Set<PdfName> illustrationRoles = new HashSet<PdfName>();

    static {

        // Some tag roles are not in any of the sets that define types. Some - because we don't want to write any accessibility
        // properties for them, some - because they are ambiguous for different pdf versions or don't have any possible
        // properties to set at the moment.
//        PdfName.Document
//        PdfName.DocumentFragment
//        PdfName.Artifact
//        PdfName.THead
//        PdfName.TBody
//        PdfName.TFoot

        groupingRoles.add(PdfName.Part);
        groupingRoles.add(PdfName.Art);
        groupingRoles.add(PdfName.Sect);
        groupingRoles.add(PdfName.Div);
        groupingRoles.add(PdfName.BlockQuote);
        groupingRoles.add(PdfName.Caption);
        groupingRoles.add(PdfName.TOC);
        groupingRoles.add(PdfName.TOCI);
        groupingRoles.add(PdfName.Index);
        groupingRoles.add(PdfName.NonStruct);
        groupingRoles.add(PdfName.Private);
        groupingRoles.add(PdfName.Aside);

        blockLevelRoles.add(PdfName.P);
        blockLevelRoles.add(PdfName.H);
        blockLevelRoles.add(PdfName.H1);
        blockLevelRoles.add(PdfName.H2);
        blockLevelRoles.add(PdfName.H3);
        blockLevelRoles.add(PdfName.H4);
        blockLevelRoles.add(PdfName.H5);
        blockLevelRoles.add(PdfName.H6);
        // Hn type is handled separately in identifyType method
        blockLevelRoles.add(PdfName.L);
        blockLevelRoles.add(PdfName.Lbl);
        blockLevelRoles.add(PdfName.LI);
        blockLevelRoles.add(PdfName.LBody);
        blockLevelRoles.add(PdfName.Table);
        blockLevelRoles.add(PdfName.TR);
        blockLevelRoles.add(PdfName.TH);
        blockLevelRoles.add(PdfName.TD);
        blockLevelRoles.add(PdfName.Title);
        blockLevelRoles.add(PdfName.FENote);
        blockLevelRoles.add(PdfName.Sub);
        blockLevelRoles.add(PdfName.Caption);

        inlineLevelRoles.add(PdfName.Span);
        inlineLevelRoles.add(PdfName.Quote);
        inlineLevelRoles.add(PdfName.Note);
        inlineLevelRoles.add(PdfName.Reference);
        inlineLevelRoles.add(PdfName.BibEntry);
        inlineLevelRoles.add(PdfName.Code);
        inlineLevelRoles.add(PdfName.Link);
        inlineLevelRoles.add(PdfName.Annot);
        inlineLevelRoles.add(PdfName.Ruby);
        inlineLevelRoles.add(PdfName.Warichu);
        inlineLevelRoles.add(PdfName.RB);
        inlineLevelRoles.add(PdfName.RT);
        inlineLevelRoles.add(PdfName.RP);
        inlineLevelRoles.add(PdfName.WT);
        inlineLevelRoles.add(PdfName.WP);
        inlineLevelRoles.add(PdfName.Em);
        inlineLevelRoles.add(PdfName.Strong);

        illustrationRoles.add(PdfName.Figure);
        illustrationRoles.add(PdfName.Formula);
        illustrationRoles.add(PdfName.Form);
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
    static int identifyType(PdfName role) {
        if (groupingRoles.contains(role)) {
            return Grouping;
        } else if (blockLevelRoles.contains(role) || StandardStructureNamespace.isHnRole(role)) {
            return BlockLevel;
        } else if (inlineLevelRoles.contains(role)) {
            return InlineLevel;
        } else if (illustrationRoles.contains(role)) {
            return Illustration;
        }

        return Unknown;
    }
}
