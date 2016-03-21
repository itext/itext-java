package com.itextpdf.layout.element;

/**
 * A marker subinterface of {@link IElement} that specifies that the layout object
 * is, by definition, on the lowest tier in the object hierarchy. A
 * {@link ILeafElement leaf element} must not act as a container for other
 * elements.
 * 
 * @param <Type> the type of the implementation
 */
public interface ILeafElement<Type extends IElement> extends IElement<Type> {
}
