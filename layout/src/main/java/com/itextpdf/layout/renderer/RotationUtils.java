/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.minmaxwidth.RotationMinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.HashMap;

final class RotationUtils {

    private RotationUtils() {
    }

    /**
     * This method tries to calculate min-max-width of rotated element using heuristics
     * of {@link RotationMinMaxWidth#calculate(double, double, MinMaxWidth)}.
     * This method may call {@link IRenderer#layout(LayoutContext)} once in best case
     * (if the width is set on element, or if we are really lucky) and three times in worst case.
     *
     * @param minMaxWidth the minMaxWidth of NOT rotated renderer
     * @param renderer    the actual renderer
     * @return minMaxWidth of rotated renderer or original value in case rotated value can not be calculated, or renderer isn't rotated.
     */
    public static MinMaxWidth countRotationMinMaxWidth(MinMaxWidth minMaxWidth, AbstractRenderer renderer) {
        PropertiesBackup backup = new PropertiesBackup(renderer);
        Float rotation = backup.storeFloatProperty(Property.ROTATION_ANGLE);
        if (rotation != null) {
            float angle = (float) rotation;
            //This width results in more accurate values for min-width calculations.
            float layoutWidth = minMaxWidth.getMaxWidth() + MinMaxWidthUtils.getEps();
            LayoutResult layoutResult = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(layoutWidth, AbstractRenderer.INF))));
            if (layoutResult.getOccupiedArea() != null) {
                Rectangle layoutBBox = layoutResult.getOccupiedArea().getBBox();
                if (MinMaxWidthUtils.isEqual(minMaxWidth.getMinWidth(), minMaxWidth.getMaxWidth())) {
                    backup.restoreProperty(Property.ROTATION_ANGLE);
                    float rotatedWidth = (float) RotationMinMaxWidth.calculateRotatedWidth(layoutBBox, angle);
                    return new MinMaxWidth(rotatedWidth, rotatedWidth, 0);
                }
                double area = layoutResult.getOccupiedArea().getBBox().getWidth() * layoutResult.getOccupiedArea().getBBox().getHeight();
                RotationMinMaxWidth rotationMinMaxWidth = RotationMinMaxWidth.calculate(angle, area, minMaxWidth);
                Float rotatedMinWidth = getLayoutRotatedWidth(renderer, (float) rotationMinMaxWidth.getMinWidthOrigin(), layoutBBox, angle);
                if (rotatedMinWidth != null) {
                    if (rotatedMinWidth > rotationMinMaxWidth.getMaxWidth()) {
                        rotationMinMaxWidth.setChildrenMinWidth((float) rotatedMinWidth);
                        Float rotatedMaxWidth = getLayoutRotatedWidth(renderer, (float) rotationMinMaxWidth.getMaxWidthOrigin(), layoutBBox, angle);
                        if (rotatedMaxWidth != null && rotatedMaxWidth > rotatedMinWidth) {
                            rotationMinMaxWidth.setChildrenMaxWidth((float) rotatedMaxWidth);
                        } else {
                            rotationMinMaxWidth.setChildrenMaxWidth((float) rotatedMinWidth);
                        }
                    } else {
                        rotationMinMaxWidth.setChildrenMinWidth((float) rotatedMinWidth);
                    }
                    backup.restoreProperty(Property.ROTATION_ANGLE);
                    return rotationMinMaxWidth;
                }
            }
        }
        backup.restoreProperty(Property.ROTATION_ANGLE);
        return minMaxWidth;
    }

    /**
     * This method tries to calculate width of not rotated renderer, so after rotation it fits availableWidth.
     * This method uses heuristics of {@link RotationMinMaxWidth#calculate(double, double, MinMaxWidth, double)}.
     * It doesn't take into account any of height properties of renderer or height of layoutArea.
     * The minMaxWidth calculations and initial layout may take long time, but they won't be called if the renderer have width property.
     *
     * @param availableWidth the width of layoutArea
     * @param renderer       the actual renderer
     * @return the width that should be set as width of layout area to properly layout element, or fallback to
     * {@link AbstractRenderer#retrieveWidth(float)} in case it can not be calculated, or renderer isn't rotated.
     */
    public static Float retrieveRotatedLayoutWidth(float availableWidth, AbstractRenderer renderer) {
        PropertiesBackup backup = new PropertiesBackup(renderer);
        Float rotation = backup.storeFloatProperty(Property.ROTATION_ANGLE);
        if (rotation != null && renderer.<UnitValue>getProperty(Property.WIDTH) == null) {
            float angle = (float) rotation;
            backup.<UnitValue>storeProperty(Property.HEIGHT);
            backup.<UnitValue>storeProperty(Property.MIN_HEIGHT);
            backup.<UnitValue>storeProperty(Property.MAX_HEIGHT);
            backup.storeBoolProperty(Property.FORCED_PLACEMENT);
            MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
            // Using this width for initial layout helps in case of small elements. They may have more free spaces,
            // but it's more likely they fit.
            float length = (minMaxWidth.getMaxWidth() + minMaxWidth.getMinWidth()) / 2 + MinMaxWidthUtils.getEps();
            LayoutResult layoutResult = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(length, AbstractRenderer.INF))));
            backup.restoreProperty(Property.HEIGHT);
            backup.restoreProperty(Property.MIN_HEIGHT);
            backup.restoreProperty(Property.MAX_HEIGHT);
            backup.restoreProperty(Property.FORCED_PLACEMENT);

            Rectangle additions = new Rectangle(0, 0);
            renderer.applyPaddings(additions, true);
            renderer.applyBorderBox(additions, true);
            renderer.applyMargins(additions, true);

            if (layoutResult.getOccupiedArea() != null) {
                double area = layoutResult.getOccupiedArea().getBBox().getWidth() * layoutResult.getOccupiedArea().getBBox().getHeight();
                RotationMinMaxWidth result = RotationMinMaxWidth.calculate(angle, area, minMaxWidth, availableWidth);
                if (result != null) {
                    backup.restoreProperty(Property.ROTATION_ANGLE);
                    if (result.getMaxWidthHeight() > result.getMinWidthHeight()) {
                        return (float) (result.getMinWidthOrigin() - additions.getWidth() + MinMaxWidthUtils.getEps());
                    } else {
                        return (float) (result.getMaxWidthOrigin() - additions.getWidth() + MinMaxWidthUtils.getEps());
                    }
                }
            }
        }
        backup.restoreProperty(Property.ROTATION_ANGLE);
        return renderer.retrieveWidth(availableWidth);
    }

    //Get actual width of element based on it's layout. May use occupied are of layout result of initial layout for time saving.
    private static Float getLayoutRotatedWidth(AbstractRenderer renderer, float availableWidth, Rectangle previousBBox, double angle) {
        if (MinMaxWidthUtils.isEqual(availableWidth, previousBBox.getWidth())) {
            return (float) RotationMinMaxWidth.calculateRotatedWidth(previousBBox, angle);
        }
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(availableWidth + MinMaxWidthUtils.getEps(), AbstractRenderer.INF))));
        if (result.getOccupiedArea() != null) {
            return (float) RotationMinMaxWidth.calculateRotatedWidth(result.getOccupiedArea().getBBox(), angle);
        }
        return null;
    }

    private static class PropertiesBackup {

        private AbstractRenderer renderer;
        private HashMap<Integer, PropertyBackup> propertiesBackup = new HashMap<>();

        public PropertiesBackup(AbstractRenderer renderer) {
            this.renderer = renderer;
        }

        //workaround for autoport
        public Float storeFloatProperty(int property) {
            Float value = renderer.getPropertyAsFloat(property);
            if (value != null) {
                propertiesBackup.put(property, new PropertyBackup(value, renderer.hasOwnProperty(property)));
                renderer.setProperty(property, null);
            }
            return value;
        }

        public Boolean storeBoolProperty(int property) {
            Boolean value = renderer.getPropertyAsBoolean(property);
            if (value != null) {
                propertiesBackup.put(property, new PropertyBackup(value, renderer.hasOwnProperty(property)));
                renderer.setProperty(property, null);
            }
            return value;
        }

        public <T> T storeProperty(int property) {
            T value = renderer.<T>getProperty(property);
            if (value != null) {
                propertiesBackup.put(property, new PropertyBackup(value, renderer.hasOwnProperty(property)));
                renderer.setProperty(property, null);
            }
            return value;
        }

        public void restoreProperty(int property) {
            PropertyBackup backup = propertiesBackup.remove(property);
            if (backup != null) {
                if (backup.isOwnedByRender()) {
                    renderer.setProperty(property, backup.getValue());
                } else {
                    renderer.deleteOwnProperty(property);
                }
            }
        }

        private static class PropertyBackup {
            private Object propertyValue;
            private boolean isOwnedByRender;

            public PropertyBackup(Object propertyValue, boolean isOwnedByRender) {
                this.propertyValue = propertyValue;
                this.isOwnedByRender = isOwnedByRender;
            }

            public Object getValue() {
                return propertyValue;
            }

            public boolean isOwnedByRender() {
                return isOwnedByRender;
            }
        }
    }
}
