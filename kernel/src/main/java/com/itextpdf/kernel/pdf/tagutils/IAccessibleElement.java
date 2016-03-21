package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfName;

/**
 * A layout element which can have a <em>role</em>. The name of the role will be
 * used to tag the element if it is added to a Tagged PDF document. It can also
 * have {@link AccessibleElementProperties}, metadata for the tag dictionary.
 */
public interface IAccessibleElement {

    /**
     * Gets the element's role.
     * @return a {@link PdfName} containing the name of the role
     */
    PdfName getRole();

    /**
     * Sets the element's role.
     * @param role the new role which the {@link IAccessibleElement} should take
     */
    void setRole(PdfName role);

    /**
     * Gets the {@link AccessibleElementProperties accessibility properties}.
     * @return a properties wrapper object specific to a tagged element in Tagged PDF
     */
    AccessibleElementProperties getAccessibilityProperties();
}
