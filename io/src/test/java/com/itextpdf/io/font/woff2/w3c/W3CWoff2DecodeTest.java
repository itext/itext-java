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
package com.itextpdf.io.font.woff2.w3c;

import com.itextpdf.io.font.woff2.Woff2DecodeTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public abstract class W3CWoff2DecodeTest extends Woff2DecodeTest{
    private static final String baseSourceFolder = "./src/test/resources/com/itextpdf/io/font/woff2/w3c/";
    private static final String baseDestinationFolder = "./target/test/com/itextpdf/io/font/woff2/w3c/";

    protected abstract String getFontName();

    protected abstract String getTestInfo();

    protected abstract boolean isFontValid();

    @BeforeEach
    public void setUp() {
        if (isDebug()) {
            createOrClearDestinationFolder(getDestinationFolder());
        }
    }

    @Test
    public void runTest() throws IOException{
        System.out.print("\n" + getTestInfo() + "\n");
        runTest(getFontName(), getSourceFolder(), getDestinationFolder(), isFontValid());
    }

    private String getDestinationFolder() {
        String localPackage = getLocalPackage().toLowerCase();
        return baseDestinationFolder + localPackage + File.separatorChar + getTestClassName() + File.separatorChar;
    }

    private String getSourceFolder() {
        String localPackage = getLocalPackage().toLowerCase();
        return baseSourceFolder + localPackage + File.separatorChar;
    }

    private String getTestClassName() {
        return getClass().getSimpleName();
    }

    private String getLocalPackage() {
        String packageName = getClass().getPackage().getName();
        String basePackageName = W3CWoff2DecodeTest.class.getPackage().getName();
        return packageName.substring(basePackageName.length()).replace('.', File.separatorChar);
    }
}
