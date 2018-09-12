package com.itextpdf.styledxmlparser.css.resolve;

/**
 * Interface for attribute and style-inheritance logic
 */
public interface IStyleInheritance {
    /**
     * Checks if a property or attribute is inheritable is inheritable.
     *
     * @param propertyIdentifier the identifier for property
     * @return true, if the property is inheritable, false otherwise
     */
    boolean isInheritable(String propertyIdentifier);
}
