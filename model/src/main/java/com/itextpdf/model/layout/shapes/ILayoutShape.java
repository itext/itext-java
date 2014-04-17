package com.itextpdf.model.layout.shapes;

/**
 * Describes how the element is positioned in the document.
 * It can be box, complex shape or it can specify just a starting point or dimensions.
 * Also it can define positioning rules such as "minHeight", "maxHeight" which means minimum and maximum available height,
 *  "allowPageBreak" which means that element can be splitted between pages and a lot more.
 * The layout manager is responsible for handling IElementPositions.
 * User can define own layout manager and own ILayoutShape implementors in order to implement custom layout features.
 */
public interface ILayoutShape {

}
