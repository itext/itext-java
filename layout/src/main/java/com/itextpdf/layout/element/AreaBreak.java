package com.itextpdf.layout.element;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.renderer.AreaBreakRenderer;

/**
 * A layout object that terminates the current content area (by default, a page)
 * and creates a new one. If no {@link PageSize} is given, the new content area
 * will have the same size as the current one.
 */
public class AreaBreak extends AbstractElement {

    protected PageSize pageSize;

    /**
     * Creates an AreaBreak. The new content area will have the same size as the
     * current one.
     */
    public AreaBreak() {
        this(Property.AreaBreakType.NEW_AREA);
    }

    public AreaBreak(Property.AreaBreakType areaBreakType) {
        setProperty(Property.AREA_BREAK_TYPE, areaBreakType);
    }
    
    /**
     * Creates an AreaBreak. The new content area will have the specified page
     * size.
     * @param pageSize the size of the new content area
     */
    public AreaBreak(PageSize pageSize) {
        this(Property.AreaBreakType.NEW_PAGE);
        this.pageSize = pageSize;
    }

    /**
     * Gets the page size.
     * @return the {@link PageSize page size} of the next content area.
     */
    public PageSize getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     * @param pageSize the new {@link PageSize page size} of the next content area.
     */
    public void setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    public Property.AreaBreakType getType() {
        return (Property.AreaBreakType) getProperty(Property.AREA_BREAK_TYPE);
    }

    @Override
    protected AreaBreakRenderer makeNewRenderer() {
        return new AreaBreakRenderer(this);
    }
}
