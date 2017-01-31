package com.itextpdf.layout.minmaxwidth;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.IRenderer;

public class MinMaxWidthUtils {

    private static final float eps = 0.01f;
    private static final float max = 32760f;

    public static float getEps() {
        return eps;
    }

    public static float getMax() {
        return max;
    }

    public static MinMaxWidth countDefaultMinMaxWidth(IRenderer renderer, float availableWidth) {
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(availableWidth, AbstractRenderer.INF))));
        return result.getStatus() == LayoutResult.NOTHING ? new MinMaxWidth(0, availableWidth) :
                new MinMaxWidth(0, availableWidth, 0, result.getOccupiedArea().getBBox().getWidth());
    }
    
    public static float getBorderWidth(IPropertyContainer element) {
        Border border = element.<Border>getProperty(Property.BORDER);
        Border rightBorder = element.<Border>getProperty(Property.BORDER_RIGHT);
        Border leftBorder = element.<Border>getProperty(Property.BORDER_LEFT);
        
        if (!element.hasOwnProperty(Property.BORDER_RIGHT)) {
            rightBorder = border;
        }
        if (!element.hasOwnProperty(Property.BORDER_LEFT)) {
            leftBorder = border;
        }
        
        float rightBorderWidth = rightBorder != null ? rightBorder.getWidth() : 0;
        float leftBorderWidth = leftBorder != null ? leftBorder.getWidth() : 0;
        return rightBorderWidth + leftBorderWidth;
    }
    
    public static float getMarginsWidth(IPropertyContainer element) {
        Float rightMargin = element.<Float>getProperty(Property.MARGIN_RIGHT);
        Float leftMargin = element.<Float>getProperty(Property.MARGIN_LEFT);
        
        float rightMarginWidth = rightMargin != null ? (float) rightMargin : 0;
        float leftMarginWidth = leftMargin != null ? (float) leftMargin : 0;
        
        return  rightMarginWidth + leftMarginWidth;
    }
    
    public static float getPaddingWidth(IPropertyContainer element) {
        Float rightPadding = element.<Float>getProperty(Property.PADDING_RIGHT);
        Float leftPadding = element.<Float>getProperty(Property.PADDING_LEFT);

        float rightPaddingWidth = rightPadding != null ? (float) rightPadding : 0;
        float leftPaddingWidth = leftPadding != null ? (float) leftPadding : 0;

        return  rightPaddingWidth + leftPaddingWidth;
    }
}