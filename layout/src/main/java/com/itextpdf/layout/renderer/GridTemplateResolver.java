/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.grid.AutoRepeatValue;
import com.itextpdf.layout.properties.grid.FixedRepeatValue;
import com.itextpdf.layout.properties.grid.GridValue;
import com.itextpdf.layout.properties.grid.MinMaxValue;
import com.itextpdf.layout.properties.grid.PercentValue;
import com.itextpdf.layout.properties.grid.PointValue;
import com.itextpdf.layout.properties.grid.TemplateValue;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

class GridTemplateResolver {
    private final float space;
    private final float gap;
    private boolean containsIntrinsicOrFlexible = false;
    private AutoRepeatResolver autoRepeatResolver = null;
    private Result result = new Result(new ArrayList<>());

    GridTemplateResolver(float space, float gap) {
        this.space = space;
        this.gap = gap;
    }

    /**
     * Determines if auto-fit repeat was encountered during processing.
     *
     * @return true if auto-fit repeat was encountered, false otherwise
     */
    boolean isCollapseNullLines() {
        return autoRepeatResolver != null;
    }

    /**
     * Determines how many fixed values (all template values except auto-fit/fill repeat) in the result.
     *
     * @return number of fixed values in template list
     */
    int getFixedValuesCount() {
        if (autoRepeatResolver == null) {
            return result.size();
        }
        return result.size() - (autoRepeatResolver.end - autoRepeatResolver.start);
    }

    /**
     * Shrinks template list to fit the given size by reducing number of auto-fit/fill repetitions.
     *
     * @param sizeToFit size to fit template list
     */
    List<GridValue> shrinkTemplatesToFitSize(int sizeToFit) {
        if (autoRepeatResolver == null) {
            return result.getList();
        }
        return autoRepeatResolver.shrinkTemplatesToFitSize(sizeToFit);
    }

    /**
     * Resolves template values to grid values by flatting repeats.
     *
     * @param template template values list
     * @return grid values list
     */
    List<GridValue> resolveTemplate(List<TemplateValue> template) {
        if (template == null) {
            return null;
        }
        try {
            float leftSpace = this.space;
            for (TemplateValue value : template) {
                leftSpace -= processValue(value);
                leftSpace -= gap;
            }
            leftSpace += gap;
            if (autoRepeatResolver != null) {
                if (autoRepeatResolver.start == result.size()) {
                    // This additional gap is needed when auto-repeat is located at the end of a template
                    // It's for simplifying the logic of auto-repeat, because it always adds gap after last element
                    leftSpace += gap;
                }
                autoRepeatResolver.resolve(leftSpace);
            }
            return result.getList();
        } catch (IllegalStateException exception) {
            LoggerFactory.getLogger(GridTemplateResolver.class).warn(exception.getMessage());
            reset();
        }
        return null;
    }

    private float processValue(TemplateValue value) {
        switch (value.getType()) {
            case MIN_CONTENT:
            case MAX_CONTENT:
            case AUTO:
            case FLEX:
            case FIT_CONTENT:
                result.addValue((GridValue) value);
                containsIntrinsicOrFlexible = true;
                break;
            case POINT:
                result.addValue((GridValue) value);
                return ((PointValue) value).getValue();
            case PERCENT:
                result.addValue((GridValue) value);
                return space > 0.0f ? ((PercentValue) value).getValue() / 100 * space : 0.0f;
            case MINMAX:
                result.addValue((GridValue) value);
                result.setFreeze(true);
                // Treating each track as its max track sizing function if that is definite
                // or as its minimum track sizing function otherwise
                // if encountered intrinsic or flexible before, then it doesn't matter what to process
                boolean currentValue = containsIntrinsicOrFlexible;
                float length = processValue(((MinMaxValue)value).getMax());
                if (containsIntrinsicOrFlexible) {
                    length = processValue(((MinMaxValue)value).getMin());
                }
                containsIntrinsicOrFlexible = currentValue;
                result.setFreeze(false);
                return length;
            case FIXED_REPEAT:
                float usedSpace = 0.0f;
                FixedRepeatValue repeat = (FixedRepeatValue) value;
                for (int i = 0; i < repeat.getRepeatCount(); ++i) {
                    for (GridValue element : repeat.getValues()) {
                        usedSpace += processValue(element);
                    }
                    usedSpace += (repeat.getValues().size() - 1) * gap;
                }
                return usedSpace;
            case AUTO_REPEAT:
                if (autoRepeatResolver != null) {
                    throw new IllegalStateException(LayoutExceptionMessageConstant.GRID_AUTO_REPEAT_CAN_BE_USED_ONLY_ONCE);
                }
                autoRepeatResolver = new AutoRepeatResolver((AutoRepeatValue) value, result.size());
                break;
        }
        return 0.0f;
    }

    private void reset() {
        autoRepeatResolver = null;
        result.getList().clear();
        result.setInsertPoint(-1);
    }

    private class AutoRepeatResolver {
        final AutoRepeatValue repeat;
        final int start;
        int end = -1;

        AutoRepeatResolver(AutoRepeatValue repeat, int pos) {
            this.repeat = repeat;
            this.start = pos;
        }

        /**
         * Resolves auto-fit/fill repeat if it was encountered.
         * If given space is less than 0, only one iteration will be performed.
         *
         * @param leftSpace space to fit repeat values on
         */
        public void resolve(float leftSpace) {
            float usedSpace = 0.0f;
            float usedSpacePerIteration = -1.0f;
            int fixedTemplatesCount = result.size();
            do {
                result.setInsertPoint(start);
                for (GridValue value : repeat.getValues()) {
                    usedSpace += processValue(value);
                    usedSpace += gap;
                }
                if (usedSpacePerIteration < 0.0f) {
                    usedSpacePerIteration = usedSpace;
                }
                if (containsIntrinsicOrFlexible) {
                    throw new IllegalStateException(
                            LayoutExceptionMessageConstant.GRID_AUTO_REPEAT_CANNOT_BE_COMBINED_WITH_INDEFINITE_SIZES);
                }
            } while (usedSpace + usedSpacePerIteration <= leftSpace);
            end = start + result.size() - fixedTemplatesCount;
        }

        /**
         * Shrinks template list to fit the given size by reducing number of auto-fit/fill repetitions.
         *
         * @param sizeToFit size to fit template list
         */
        List<GridValue> shrinkTemplatesToFitSize(int sizeToFit) {
            // Getting max number of available repetitions
            final int allowedRepeatValuesCount = getAllowedRepeatValuesCount(sizeToFit);

            // It could be done with .subList(), but this is not portable on .NET
            List<GridValue> shrankResult = new ArrayList<>(result.size());
            List<GridValue> previousResult = result.getList();
            for (int i = 0; i < start; ++i) {
                shrankResult.add(previousResult.get(i));
            }
            for (int i = 0; i < allowedRepeatValuesCount; ++i) {
                shrankResult.addAll(repeat.getValues());
            }
            for (int i = end; i < previousResult.size(); ++i) {
                shrankResult.add(previousResult.get(i));
            }
            result = new Result(shrankResult);
            return result.getList();
        }

        private int getAllowedRepeatValuesCount(int sizeToFit) {
            // int division with rounding down
            int allowedRepeatValuesCount =
                    (Math.min(sizeToFit - getFixedValuesCount(), end - start))
                    / repeat.getValues().size()
                    * repeat.getValues().size();

            // if space was indefinite than repeat can be used only once
            if (space < 0.0f && allowedRepeatValuesCount > 0) {
                allowedRepeatValuesCount = 1;
            }
            return allowedRepeatValuesCount;
        }
    }

    private static class Result {
        final List<GridValue> result;
        int insertPoint = -1;
        boolean freeze = false;

        Result(List<GridValue> result) {
            this.result = result;
        }

        public void addValue(GridValue value) {
            if (freeze) {
                return;
            }
            if (insertPoint < 0) {
                result.add( value);
            } else {
                result.add(insertPoint++, value);
            }
        }

        public void setInsertPoint(int insertPoint) {
            this.insertPoint = insertPoint;
        }

        public int size() {
            return result.size();
        }

        public List<GridValue> getList() {
            return result;
        }

        public void setFreeze(boolean freeze) {
            this.freeze = freeze;
        }
    }
}
