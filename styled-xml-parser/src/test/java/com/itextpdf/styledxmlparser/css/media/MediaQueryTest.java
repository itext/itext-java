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
package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.test.ExtendedITextTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")

public class MediaQueryTest extends ExtendedITextTest {
    @Test
    public void matchTest() {
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        MediaQuery query = MediaQueryParser.parseMediaQuery("not all and (min-width: 600px)");
        List<MediaQuery> queries = MediaQueryParser.parseMediaQueries("not all and (min-width: 600px), not all and (min-width: 500px)");
        Assertions.assertTrue(query.matches(deviceDescription));
        Assertions.assertTrue(queries.get(0).matches(deviceDescription));
        Assertions.assertTrue(queries.get(1).matches(deviceDescription));
    }
}
