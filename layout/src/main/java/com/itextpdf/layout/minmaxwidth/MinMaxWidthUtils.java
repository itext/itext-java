package com.itextpdf.layout.minmaxwidth;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.IRenderer;

import static java.lang.Math.PI;
import static java.lang.Math.min;

public class MinMaxWidthUtils {
    private static final float eps = 0.01f;
    private static final float max = 32760f;

    public static float getEps() {
        return eps;
    }

    public static float getMax() {
        return max;
    }

    public static float toEffectiveWidth(BlockElement b, float fullWidth) {
        if (b instanceof Table) {
            return fullWidth + ((Table) b).getNumberOfColumns() * eps;
        } else {
            return fullWidth - getBorderWidth(b) - getMarginsWidth(b) - getPaddingWidth(b) + eps;
        }
    }

    public static float[] toEffectiveTableColumnWidth(float[] tableColumnWidth) {
        float[] result = tableColumnWidth.clone();
        for (int i = 0; i < result.length; ++i) {
            result[i] += eps;
        }
        return result;
    }

    //heuristic method
    public static MinMaxWidth countRotationMinMaxWidth(MinMaxWidth minMaxWidth, BlockRenderer renderer) {
        Float rotation = renderer.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (rotation != null && renderer.getModelElement() instanceof BlockElement) {
            float angle = rotation.floatValue();
            boolean restoreRendererProperty = renderer.hasOwnProperty(Property.ROTATION_ANGLE);
            renderer.setProperty(Property.ROTATION_ANGLE, new Float(0));
            float width = toEffectiveWidth((BlockElement) renderer.getModelElement(), minMaxWidth.getAvailableWidth());
            LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(width, AbstractRenderer.INF))));
            if (restoreRendererProperty) {
                renderer.setProperty(Property.ROTATION_ANGLE, rotation);
            } else {
                renderer.deleteOwnProperty(Property.ROTATION_ANGLE);
            }
            if (result.getOccupiedArea() != null) {
                double a = result.getOccupiedArea().getBBox().getWidth();
                double b = result.getOccupiedArea().getBBox().getHeight();
                return new MinMaxWidth(0, minMaxWidth.getAvailableWidth(), (float) Math.sqrt(2 * a * b), (float) Math.sqrt(a * a + b * b));
            }
        }
        return minMaxWidth;
    }

    public static MinMaxWidth countDefaultMinMaxWidth(IRenderer renderer, float availableWidth) {
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(availableWidth, AbstractRenderer.INF))));
        return result.getStatus() == LayoutResult.NOTHING ? new MinMaxWidth(0, availableWidth) :
                new MinMaxWidth(0, availableWidth, 0, result.getOccupiedArea().getBBox().getWidth());
    }
    
    private static float getBorderWidth(IElement element) {
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
    
    private static float getMarginsWidth(IElement element) {
        Float rightMargin = element.<Float>getProperty(Property.MARGIN_RIGHT);
        Float leftMargin = element.<Float>getProperty(Property.MARGIN_LEFT);
        
        float rightMarginWidth = rightMargin != null ? (float) rightMargin : 0;
        float leftMarginWidth = leftMargin != null ? (float) leftMargin : 0;
        
        return  rightMarginWidth + leftMarginWidth;
    }
    
    private static float getPaddingWidth(IElement element) {
        Float rightPadding = element.<Float>getProperty(Property.PADDING_RIGHT);
        Float leftPadding = element.<Float>getProperty(Property.PADDING_LEFT);

        float rightPaddingWidth = rightPadding != null ? (float) rightPadding : 0;
        float leftPaddingWidth = leftPadding != null ? (float) leftPadding : 0;

        return  rightPaddingWidth + leftPaddingWidth;
    }
    
}
