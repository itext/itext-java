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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfViewerPreferencesTest extends ExtendedITextTest {

    @Test
    public void printScalingTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.NONE);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.None, dictionary.get(PdfName.PrintScaling));

        preferences.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.APP_DEFAULT);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.AppDefault, dictionary.get(PdfName.PrintScaling));
    }

    @Test
    public void duplexTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.SIMPLEX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.Simplex, dictionary.get(PdfName.Duplex));

        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.DUPLEX_FLIP_LONG_EDGE);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.DuplexFlipLongEdge, dictionary.get(PdfName.Duplex));

        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.DUPLEX_FLIP_SHORT_EDGE);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.DuplexFlipShortEdge, dictionary.get(PdfName.Duplex));
    }

    @Test
    public void nonFullScreenPageModeTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_THUMBS);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.UseThumbs, dictionary.get(PdfName.NonFullScreenPageMode));

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_NONE);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.UseNone, dictionary.get(PdfName.NonFullScreenPageMode));

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_OC);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.UseOC, dictionary.get(PdfName.NonFullScreenPageMode));

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_OUTLINES);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.UseOutlines, dictionary.get(PdfName.NonFullScreenPageMode));
    }

    @Test
    public void directionTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setDirection(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setDirection(PdfViewerPreferences.PdfViewerPreferencesConstants.LEFT_TO_RIGHT);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.L2R, dictionary.get(PdfName.Direction));

        preferences.setDirection(PdfViewerPreferences.PdfViewerPreferencesConstants.RIGHT_TO_LEFT);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.R2L, dictionary.get(PdfName.Direction));
    }

    @Test
    public void viewAreaTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.CropBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.ViewArea));
    }

    @Test
    public void viewClipTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.CropBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.ViewClip));
    }

    @Test
    public void printAreaTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.CropBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.PrintArea));
    }

    @Test
    public void printClipTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assertions.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assertions.assertEquals(0, dictionary.size());

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.CropBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assertions.assertEquals(1, dictionary.size());
        Assertions.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.PrintClip));
    }
}
