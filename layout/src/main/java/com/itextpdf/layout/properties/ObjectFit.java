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
package com.itextpdf.layout.properties;

/**
 * A specialized enum holding the possible values for an object-fit property
 * which define the way of fitting the image into the content box with different size.
 */
public enum ObjectFit {
    /**
     * If object-fit set to FILL, image will be sized to fill the element's content box. This can
     * change the aspect-ratio of the image.
     */
    FILL,
    /**
     * If object-fit set to CONTAIN, image will be scaled keeping its aspect ratio to fit in
     * the content box. The whole picture will be rendered in the document but some are of the
     * image container might be blank.
     */
    CONTAIN,
    /**
     * If object-fit set to COVER, image will be scaled keeping its aspect ratio to cover
     * the content box. The image will be clipped to fit the container's bounds.
     */
    COVER,
    /**
     * If object-fit set to SCALE_DOWN, image will be scaled keeping its aspect ratio to fit in
     * the content box but scaling coefficient cannot be greater than 1. If content box is greater
     * than the image, picture will be rendered in its original size leaving the rest area of
     * the container blank.
     */
    SCALE_DOWN,
    /**
     * If object-fit set to NONE, image will not be scaled. It will keep its original size. If
     * the content box is greater than image it will contain blank areas, otherwise picture will
     * be clipped to fit the container's bounds.
     */
    NONE
}
