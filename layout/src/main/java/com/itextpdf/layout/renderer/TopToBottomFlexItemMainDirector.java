package com.itextpdf.layout.renderer;

import com.itextpdf.layout.properties.JustifyContent;
import com.itextpdf.layout.renderer.FlexUtil.FlexItemCalculationInfo;

import java.util.List;

class TopToBottomFlexItemMainDirector implements IFlexItemMainDirector {
    TopToBottomFlexItemMainDirector() {
    }
    
    @Override
    public List<IRenderer> applyDirection(List<List<FlexItemInfo>> lines) {
        // TODO DEVSIX-7595 Shall be implemented in the scope of this ticket
        return null;
    }

    @Override
    public <T> void applyDirectionForLine(List<T> renderers) {
        // TODO DEVSIX-7595 Shall be implemented in the scope of this ticket
    }

    @Override
    public void applyJustifyContent(List<FlexItemCalculationInfo> line, JustifyContent justifyContent,
            float freeSpace) {
        switch (justifyContent) {
            case END:
            case SELF_END:
            case FLEX_END:
                line.get(0).yShift = freeSpace;
                break;
            case CENTER:
                line.get(0).yShift = freeSpace / 2;
                break;
            case FLEX_START:
            case NORMAL:
            case STRETCH:
            case START:
            case LEFT:
            case RIGHT:
            case SELF_START:
            default:
                // We don't need to do anything in these cases
        }
    }
}
