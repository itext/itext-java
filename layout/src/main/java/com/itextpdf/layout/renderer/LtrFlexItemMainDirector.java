package com.itextpdf.layout.renderer;

import com.itextpdf.layout.properties.JustifyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for left to right placement of flex items.
 */
class LtrFlexItemMainDirector implements IFlexItemMainDirector {
    LtrFlexItemMainDirector() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRenderer> applyDirection(List<List<FlexItemInfo>> lines) {
        List<IRenderer> renderers = new ArrayList<>();
        for (List<FlexItemInfo> line : lines) {
            for (FlexItemInfo itemInfo : line) {
                renderers.add(itemInfo.getRenderer());
            }
        }

        return renderers;
    }

    @Override
    public <T> void applyDirectionForLine(List<T> renderers) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyAlignment(List<FlexUtil.FlexItemCalculationInfo> line, JustifyContent justifyContent,
                               float freeSpace) {
        switch (justifyContent) {
            case RIGHT:
            case END:
            case SELF_END:
            case FLEX_END:
                line.get(0).xShift = freeSpace;
                break;
            case CENTER:
                line.get(0).xShift = freeSpace / 2;
                break;
            case FLEX_START:
            case NORMAL:
            case STRETCH:
            case START:
            case LEFT:
            case SELF_START:
            default:
                // We don't need to do anything in these cases
        }
    }
}
