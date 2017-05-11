package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class encapsulates information about the standard structure namespaces and provides some utility methods
 * connected to them. The main purpose of this class is to determine if the given role in the specified namespace
 * belongs to the standard or known domain-specific namespace.
 *
 * <p>See ISO 32000-2 14.8.6, "Standard structure namespaces"</p>
 */
public final class StandardStructureNamespace {
    private static final Set<PdfName> STD_STRUCT_NAMESPACE_1_7_TYPES;
    private static final Set<PdfName> STD_STRUCT_NAMESPACE_2_0_TYPES;

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

    /**
     * Gets the name of the default standard structure namespace. When a namespace is not
     * explicitly specified for a given structure element or attribute, it shall be assumed to be within this
     * default standard structure namespace.
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
    public static boolean roleBelongsToStandardNamespace(PdfName role, String standardNamespaceName) {
        if (PDF_1_7.equals(standardNamespaceName)) {
            return STD_STRUCT_NAMESPACE_1_7_TYPES.contains(role);
        } else if (PDF_2_0.equals(standardNamespaceName)) {
            return STD_STRUCT_NAMESPACE_2_0_TYPES.contains(role) || isHnRole(role);
        }

        return false;
    }

    /**
     * Checks if the given {@link PdfName} matches the Hn role pattern. To match this pattern, the given role
     * shall always consist of the uppercase letter "H" and one or more digits, representing an unsigned integer
     * greater than or equal to 1, without leading zeroes or any other prefix or postfix
     * @param role a {@link PdfName} that specifies a role to be checked against Hn role pattern.
     * @return true if the role matches, false otherwise.
     */
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
