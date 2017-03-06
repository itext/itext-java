package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StandardStructureNamespace {
    private static Set<PdfName> STD_STRUCT_NAMESPACE_1_7_TYPES = new HashSet<>();
    private static Set<PdfName> STD_STRUCT_NAMESPACE_2_0_TYPES = new HashSet<>();

    public static final PdfString STANDARD_STRUCTURE_NAMESPACE_FOR_1_7 = new PdfString("http://www.iso.org/pdf/ssn", null, true);
    public static final PdfString STANDARD_STRUCTURE_NAMESPACE_FOR_2_0 = new PdfString("http://www.iso.org/pdf2/ssn", null, true);

    // other namespaces
    private static final PdfString MATH_ML = new PdfString("http://www.w3.org/1998/Math/MathML", null, true);

    static {
        STD_STRUCT_NAMESPACE_1_7_TYPES = new HashSet<>(Arrays.asList(
                PdfName.Document,
                PdfName.Part,
                PdfName.Div,
                PdfName.P,
                PdfName.H,
                PdfName.H1,
                PdfName.H2,
                PdfName.H3,
                PdfName.H4,
                PdfName.H5,
                PdfName.H6,
                PdfName.Lbl,
                PdfName.Span,
                PdfName.Link,
                PdfName.Annot,
                PdfName.Form,
                PdfName.Ruby,
                PdfName.RB,
                PdfName.RT,
                PdfName.RP,
                PdfName.Warichu,
                PdfName.WT,
                PdfName.WP,
                PdfName.L,
                PdfName.LI,
                PdfName.LBody,
                PdfName.Table,
                PdfName.TR,
                PdfName.TH,
                PdfName.TD,
                PdfName.THead,
                PdfName.TBody,
                PdfName.TFoot,
                PdfName.Caption,
                PdfName.Figure,
                PdfName.Formula,

                PdfName.Sect,
                PdfName.Art,
                PdfName.BlockQuote,
                PdfName.TOC,
                PdfName.TOCI,
                PdfName.Index,
                PdfName.NonStruct,
                PdfName.Private,
                PdfName.Quote,
                PdfName.Note,
                PdfName.Reference,
                PdfName.BibEntry,
                PdfName.Code
        ));


        STD_STRUCT_NAMESPACE_2_0_TYPES = new HashSet<>(Arrays.asList(
                PdfName.Document,
                PdfName.DocumentFragment,
                PdfName.Part,
                PdfName.Div,
                PdfName.Aside,
                PdfName.Title,
                PdfName.Sub,
                PdfName.P,
                PdfName.H,
                // Hn, this type is handled in roleBelongsToStandardNamespace method
                PdfName.Lbl,
                PdfName.Em,
                PdfName.Strong,
                PdfName.Span,
                PdfName.Link,
                PdfName.Annot,
                PdfName.Form,
                PdfName.Ruby,
                PdfName.RB,
                PdfName.RT,
                PdfName.RP,
                PdfName.Warichu,
                PdfName.WT,
                PdfName.WP,
                PdfName.FENote,
                PdfName.L,
                PdfName.LI,
                PdfName.LBody,
                PdfName.Table,
                PdfName.TR,
                PdfName.TH,
                PdfName.TD,
                PdfName.THead,
                PdfName.TBody,
                PdfName.TFoot,
                PdfName.Caption,
                PdfName.Figure,
                PdfName.Formula,
                PdfName.Artifact
        ));
    }

    public static PdfString getDefaultStandardStructureNamespace() {
        return STANDARD_STRUCTURE_NAMESPACE_FOR_1_7;
    }

    public static boolean isKnownDomainSpecificNamespace(PdfNamespace namespace) {
        return MATH_ML.equals(namespace.getNamespaceName());
    }

    public static boolean roleBelongsToStandardNamespace(PdfName role, PdfString standardNamespaceName) {
        if (STANDARD_STRUCTURE_NAMESPACE_FOR_1_7.equals(standardNamespaceName)) {
            return STD_STRUCT_NAMESPACE_1_7_TYPES.contains(role);
        } else if (STANDARD_STRUCTURE_NAMESPACE_FOR_2_0.equals(standardNamespaceName)) {
            return STD_STRUCT_NAMESPACE_2_0_TYPES.contains(role) || isHnRole(role);
        }

        return false;
    }

    public static boolean isHnRole(PdfName role) {
        String roleStrVal = role.getValue();
        if (roleStrVal.startsWith("H") && roleStrVal.length() > 1 && roleStrVal.charAt(1) != '0') {
            try {
                return Integer.parseInt(roleStrVal.substring(1, roleStrVal.length())) > 0;
            } catch (Exception ex) {
                // ignored
            }
        }
        return false;
    }
}
