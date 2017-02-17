package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.IElement;

/**
 * Interface for implementing custom symbols for lists
 */
public interface IListSymbolFactory {
    /**
     * Creates symbol.
     *
     * @param index - the positive (greater then zero) index of list item in list.
     * @param list - the {@link IPropertyContainer} with all properties of corresponding list.
     * @param listItem - the {@link IPropertyContainer} with all properties of corresponding list item.
     * @return the {@link IElement} representing symbol.
     */
    IElement createSymbol(int index, IPropertyContainer list, IPropertyContainer listItem);
}
