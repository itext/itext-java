package com.itextpdf.layout.properties.margins;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;

import java.util.Objects;

/**
 * Class to store information about page margin content represented by {@link IElement} linked to {@link MarginBoxName}.
 */
public class PageMarginContent {

    private final MarginBoxName marginBoxName;
    private final IElement marginContent;

    private Rectangle pageMarginBoxRectangle;

    /**
     * Creates new {@link PageMarginContent} instance.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginContent {@link IElement} layout element with margin content
     */
    public PageMarginContent(MarginBoxName marginBoxName, IElement marginContent) {
        this.marginBoxName = marginBoxName;
        this.marginContent = marginContent;
    }

    /**
     * Creates new {@link PageMarginContent} instance.
     *
     * <p>
     * The margin will have the specified size in points.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginInPoints <code>float</code> specifying the margin in points
     */
    public PageMarginContent(MarginBoxName marginBoxName, float marginInPoints) {
        this.marginBoxName = marginBoxName;
        Div staticMarginContent = new Div();
        if (marginBoxName == MarginBoxName.TOP || marginBoxName == MarginBoxName.BOTTOM) {
            staticMarginContent.setHeight(marginInPoints);
        } else if (marginBoxName == MarginBoxName.LEFT || marginBoxName == MarginBoxName.RIGHT) {
            staticMarginContent.setWidth(marginInPoints);
        }
        this.marginContent = staticMarginContent;
    }

    /**
     * Creates new {@link PageMarginContent} instance by copying existing one.
     *
     * @param other {@link PageMarginContent} instance to copy
     */
    public PageMarginContent(PageMarginContent other) {
        this.marginBoxName = other.marginBoxName;
        this.marginContent = other.marginContent;
        this.pageMarginBoxRectangle = other.pageMarginBoxRectangle;
    }

    /**
     * Gets the page margin box name {@link MarginBoxName} which is based on its location on the page.
     *
     * @return the margin box name
     */
    public MarginBoxName getMarginBoxName() {
        return marginBoxName;
    }

    /**
     * Returns renderer for layout element representing page margin content.
     *
     * @return {@link IElement} layout element for page margin content
     */
    public IElement getMarginContent() {
        return marginContent;
    }

    /**
     * Sets the rectangle in which page margin box contents are shown.
     *
     * @param pageMarginBoxRectangle {@link Rectangle} defining position and dimensions of the margin box content area
     */
    void setPageMarginBoxRectangle(Rectangle pageMarginBoxRectangle) {
        this.pageMarginBoxRectangle = pageMarginBoxRectangle;
    }

    /**
     * Gets the rectangle in which page margin box contents should be shown.
     *
     * @return the {@link Rectangle} defining position and dimensions of the margin box content area
     */
    Rectangle getPageMarginBoxRectangle() {
        return pageMarginBoxRectangle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageMarginContent that = (PageMarginContent) o;
        return Objects.equals(marginBoxName, that.marginBoxName) && Objects.equals(marginContent, that.marginContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) marginBoxName, marginContent);
    }
}
