/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.layout.property.Property;
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
                assert result != null && result.getOccupiedArea() != null;
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

    public LayoutArea getCurrentArea() {
        if (currentArea == null) {
            updateCurrentArea(null);
        }
        return currentArea;
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
