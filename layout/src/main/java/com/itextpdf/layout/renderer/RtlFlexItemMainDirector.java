package com.itextpdf.layout.renderer;

import com.itextpdf.layout.properties.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for right to left placement of flex items.
 */
class RtlFlexItemMainDirector implements IFlexItemMainDirector {
    RtlFlexItemMainDirector() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRenderer> applyDirection(List<List<FlexItemInfo>> lines) {
        List<IRenderer> renderers = new ArrayList<>();
        for (List<FlexItemInfo> line : lines) {
            applyDirectionForLine(line);
            for (FlexItemInfo itemInfo : line) {
                renderers.add(itemInfo.getRenderer());
            }
        }

        return renderers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void applyDirectionForLine(List<T> renderers) {
        Collections.reverse(renderers);
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
            case FLEX_START:
                line.get(line.size() - 1).xShift = freeSpace;
                break;
            case CENTER:
                line.get(line.size() - 1).xShift = freeSpace / 2;
                break;
            case FLEX_END:
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
