package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.StandardStructureNamespace;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;
import java.util.HashSet;
import java.util.Set;

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
        // properties for them, some - because they are ambiguous for different pdf versions and don't have any possible
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
