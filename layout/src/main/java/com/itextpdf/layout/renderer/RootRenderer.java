package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RootRenderer extends AbstractRenderer {

    protected boolean immediateFlush = true;
    protected LayoutArea currentArea;
    protected int currentPageNumber;

    public void addChild(IRenderer renderer) {
        super.addChild(renderer);

        if (currentArea == null) {
            updateCurrentArea(null);
        }

        // Static layout
        if (currentArea != null && !childRenderers.isEmpty() && childRenderers.get(childRenderers.size() - 1) == renderer) {
            List<IRenderer> resultRenderers = new ArrayList<>();
            LayoutResult result = null;

            LayoutArea storedArea = null;
            LayoutArea nextStoredArea = null;
            while (currentArea != null && renderer != null && (result = renderer.layout(new LayoutContext(currentArea.clone()))).getStatus() != LayoutResult.FULL) {
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    if (result.getOverflowRenderer() instanceof ImageRenderer) {
                        ((ImageRenderer) result.getOverflowRenderer()).autoScale(currentArea);
                    } else {
                        processRenderer(result.getSplitRenderer(), resultRenderers);
                        if (nextStoredArea != null) {
                            currentArea = nextStoredArea;
                            currentPageNumber = nextStoredArea.getPageNumber();
                            nextStoredArea = null;
                        } else {
                            updateCurrentArea(result);
                        }
                    }
                } else if (result.getStatus() == LayoutResult.NOTHING) {
                    if (result.getOverflowRenderer() instanceof ImageRenderer) {
                        if (currentArea.getBBox().getHeight() < ((ImageRenderer) result.getOverflowRenderer()).imageHeight && !currentArea.isEmptyArea()) {
                            updateCurrentArea(result);
                        }
                        ((ImageRenderer)result.getOverflowRenderer()).autoScale(currentArea);
                    } else {
                        if (currentArea.isEmptyArea() && !(renderer instanceof AreaBreakRenderer)) {
                            if (Boolean.valueOf(true).equals(result.getOverflowRenderer().getModelElement().getProperty(Property.KEEP_TOGETHER))) {
                                result.getOverflowRenderer().getModelElement().setProperty(Property.KEEP_TOGETHER, false);
                                Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                                logger.warn(MessageFormat.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, "KeepTogether property will be ignored."));
                                if (storedArea != null) {
                                    nextStoredArea = currentArea;
                                    currentArea = storedArea;
                                    currentPageNumber = storedArea.getPageNumber();
                                }
                                storedArea = currentArea;
                            } else {
                                result.getOverflowRenderer().setProperty(Property.FORCED_PLACEMENT, true);
                                Logger logger = LoggerFactory.getLogger(RootRenderer.class);
                                logger.warn(MessageFormat.format(LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, ""));
                            }
                            renderer = result.getOverflowRenderer();

                            continue;
                        }
                        storedArea = currentArea;
                        updateCurrentArea(result);
                    }
                }
                renderer = result.getOverflowRenderer();
            }
            if (currentArea != null) {
                currentArea.getBBox().setHeight(currentArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());
                currentArea.setEmptyArea(false);
                if (renderer != null) {
                    processRenderer(renderer, resultRenderers);
                }
            }

            childRenderers.remove(childRenderers.size() - 1);
            if (!immediateFlush) {
                childRenderers.addAll(resultRenderers);
            }
        } else if (positionedRenderers.size() > 0 && positionedRenderers.get(positionedRenderers.size() - 1) == renderer) {
            Integer positionedPageNumber = renderer.getProperty(Property.PAGE_NUMBER);
            if (positionedPageNumber == null)
                positionedPageNumber = currentPageNumber;
            renderer.layout(new LayoutContext(new LayoutArea(positionedPageNumber, currentArea.getBBox().clone())));

            if (immediateFlush) {
                flushSingleRenderer(renderer);
                positionedRenderers.remove(positionedRenderers.size() - 1);
            }
        }
    }

    // Drawing of content. Might need to rename.
    public void flush() {
        for (IRenderer resultRenderer: childRenderers) {
            flushSingleRenderer(resultRenderer);
        }
        for (IRenderer resultRenderer : positionedRenderers) {
            flushSingleRenderer(resultRenderer);
        }
        childRenderers.clear();
        positionedRenderers.clear();
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new IllegalStateException("Layout is not supported for root renderers.");
    }

    protected abstract void flushSingleRenderer(IRenderer resultRenderer);

    protected abstract LayoutArea updateCurrentArea(LayoutResult overflowResult);

    private void processRenderer(IRenderer renderer, List<IRenderer> resultRenderers) {
        alignChildHorizontally(renderer, currentArea.getBBox().getWidth());
        if (immediateFlush) {
            flushSingleRenderer(renderer);
        } else {
            resultRenderers.add(renderer);
        }
    }

}
