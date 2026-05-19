package com.itextpdf.layout.properties.margins;

import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;

import java.util.Objects;

/**
 * Class to store information about page margin content represented by {@link IElement} linked to {@link MarginBoxName}.
 */
public class PageMarginContent extends AbstractPageContent {
    private final MarginBoxName marginBoxName;

    /**
     * Creates new {@link PageMarginContent} instance.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginContent {@link IElement} layout element with margin content
     */
    public PageMarginContent(MarginBoxName marginBoxName, IElement marginContent) {
        super(marginContent);
        this.marginBoxName = marginBoxName;
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
        this(marginBoxName, getStaticMarginContent(marginBoxName, marginInPoints));
    }

    /**
     * Creates new {@link PageMarginContent} instance by copying existing one.
     *
     * @param other {@link PageMarginContent} instance to copy
     */
    public PageMarginContent(PageMarginContent other) {
        super(other);
        this.marginBoxName = other.marginBoxName;
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
     * Creates {@link Div} layout element of the fixed size to represent a static margin.
     *
     * @param marginBoxName {@link MarginBoxName} specifying margin name based on its location on the page
     * @param marginInPoints {@code float} specifying the margin in points
     *
     * @return {@link Div} layout element with static size
     */
    private static Div getStaticMarginContent(MarginBoxName marginBoxName, float marginInPoints) {
        Div staticMarginContent = new Div();
        if (marginBoxName == MarginBoxName.TOP || marginBoxName == MarginBoxName.BOTTOM) {
            staticMarginContent.setHeight(marginInPoints);
        } else if (marginBoxName == MarginBoxName.LEFT || marginBoxName == MarginBoxName.RIGHT) {
            staticMarginContent.setWidth(marginInPoints);
        }
        return staticMarginContent;
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
        return Objects.equals(marginBoxName, that.marginBoxName) && Objects.equals(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) marginBoxName, getContent());
    }
}
