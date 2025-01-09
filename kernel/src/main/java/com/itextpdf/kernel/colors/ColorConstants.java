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
package com.itextpdf.kernel.colors;

/**
 * Class containing predefined {@link DeviceRgb} colors.
 * Color space specific classes should be used for the advanced handling of colors.
 * The most common ones are {@link DeviceGray}, {@link DeviceCmyk} and {@link DeviceRgb}.
 */
public class ColorConstants {
    /**
     * Predefined black DeviceRgb color
     */
    public static final Color BLACK = DeviceRgb.BLACK;
    /**
     * Predefined blue  DeviceRgb color
     */
    public static final Color BLUE = DeviceRgb.BLUE;
    /**
     * Predefined cyan DeviceRgb color
     */
    public static final Color CYAN = new DeviceRgb(0, 255, 255);
    /**
     * Predefined dark gray DeviceRgb color
     */
    public static final Color DARK_GRAY = new DeviceRgb(64, 64, 64);
    /**
     * Predefined gray DeviceRgb color
     */
    public static final Color GRAY = new DeviceRgb(128, 128, 128);
    /**
     * Predefined green DeviceRgb color
     */
    public static final Color GREEN = DeviceRgb.GREEN;
    /**
     * Predefined light gray DeviceRgb color
     */
    public static final Color LIGHT_GRAY = new DeviceRgb(192, 192, 192);
    /**
     * Predefined magenta DeviceRgb color
     */
    public static final Color MAGENTA = new DeviceRgb(255, 0, 255);
    /**
     * Predefined orange DeviceRgb color
     */
    public static final Color ORANGE = new DeviceRgb(255, 200, 0);
    /**
     * Predefined pink DeviceRgb color
     */
    public static final Color PINK = new DeviceRgb(255, 175, 175);
    /**
     * Predefined red DeviceRgb color
     */
    public static final Color RED = DeviceRgb.RED;
    /**
     * Predefined white DeviceRgb color
     */
    public static final Color WHITE = DeviceRgb.WHITE;
    /**
     * Predefined yellow DeviceRgb color
     */
    public static final Color YELLOW = new DeviceRgb(255, 255, 0);
}
