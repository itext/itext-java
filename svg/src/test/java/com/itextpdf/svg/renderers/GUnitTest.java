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
package com.itextpdf.svg.renderers;

import com.itextpdf.test.ITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class GUnitTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/gunit/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/gunit/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void meetTheTeam() {
        List<Exception> assertionErrorsThrown = new ArrayList<>();
        for ( int i = 1; i < 6; i++) {
            try {
                convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "test_00" + i);
            }catch(Exception ae){
                if(ae.getMessage().contains("expected null, but was")){
                    assertionErrorsThrown.add(ae);

                }
            }
        }
        if(assertionErrorsThrown.size() != 0) Assertions.fail("At least one compare file was not identical with the result");
    }

    @Test
    public void viewboxTest() throws IOException,InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "test_viewbox");
    }

    @Test
    public void simpleGTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleG");
    }
}
