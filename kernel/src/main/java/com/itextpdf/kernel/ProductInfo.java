/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel;

import java.io.Serializable;

/**
 * Describes an iText 7 add on. An add on should register itself to a PdfDocument object if it
 * wants to be included in the debugging information.
 */
public class ProductInfo implements Serializable {
    private static final long serialVersionUID = 2410734474798313936L;

    private String name;
    private int major;
    private int minor;
    private int patch;
    private boolean snapshot;

    /**
     * Instantiates a ProductInfo object.
     *
     * @param name      name of the add on
     * @param major     major version of the add on
     * @param minor     minor version of the add on
     * @param patch     patch number of the add on
     * @param snapshot  whether the version of this add on is a snapshot build or not
     */
    public ProductInfo(String name, int major, int minor, int patch, boolean snapshot) {
        this.name = name;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
    }

    public String getName() {
        return name;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    @Override
    public String toString() {
        return name + "-" + major + "." + minor + "." + patch + ( snapshot ? "-SNAPSHOT" : "" );
    }

}
