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
package com.itextpdf.kernel.pdf.colorspace.shading;

/**
 * The constants of shading type (see ISO-320001 Table 78).
 */
public final class ShadingType {

    /**
     * The int value of function-based shading type
     */
    public static final int FUNCTION_BASED = 1;
    /**
     * The int value of axial shading type
     */
    public static final int AXIAL = 2;
    /**
     * The int value of radial shading type
     */
    public static final int RADIAL = 3;
    /**
     * The int value of free-form Gouraud-shaded triangle mesh shading type
     */
    public static final int FREE_FORM_GOURAUD_SHADED_TRIANGLE_MESH = 4;
    /**
     * The int value of lattice-form Gouraud-shaded triangle mesh shading type
     */
    public static final int LATTICE_FORM_GOURAUD_SHADED_TRIANGLE_MESH = 5;
    /**
     * The int value of coons patch meshes shading type
     */
    public static final int COONS_PATCH_MESH = 6;
    /**
     * The int value of tensor-product patch meshes shading type
     */
    public static final int TENSOR_PRODUCT_PATCH_MESH = 7;

    private ShadingType() {
    }

}