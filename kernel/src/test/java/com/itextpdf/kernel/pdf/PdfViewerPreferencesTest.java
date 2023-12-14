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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfViewerPreferencesTest extends ExtendedITextTest {

    @Test
    public void printScalingTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.NONE);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.None, dictionary.get(PdfName.PrintScaling));

        preferences.setPrintScaling(PdfViewerPreferences.PdfViewerPreferencesConstants.APP_DEFAULT);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.AppDefault, dictionary.get(PdfName.PrintScaling));
    }

    @Test
    public void duplexTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.SIMPLEX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.Simplex, dictionary.get(PdfName.Duplex));

        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.DUPLEX_FLIP_LONG_EDGE);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.DuplexFlipLongEdge, dictionary.get(PdfName.Duplex));

        preferences.setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.DUPLEX_FLIP_SHORT_EDGE);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.DuplexFlipShortEdge, dictionary.get(PdfName.Duplex));
    }

    @Test
    public void nonFullScreenPageModeTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_THUMBS);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.UseThumbs, dictionary.get(PdfName.NonFullScreenPageMode));

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_NONE);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.UseNone, dictionary.get(PdfName.NonFullScreenPageMode));

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_OC);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.UseOC, dictionary.get(PdfName.NonFullScreenPageMode));

        preferences.setNonFullScreenPageMode(PdfViewerPreferences.PdfViewerPreferencesConstants.USE_OUTLINES);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.UseOutlines, dictionary.get(PdfName.NonFullScreenPageMode));
    }

    @Test
    public void directionTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setDirection(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setDirection(PdfViewerPreferences.PdfViewerPreferencesConstants.LEFT_TO_RIGHT);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.L2R, dictionary.get(PdfName.Direction));

        preferences.setDirection(PdfViewerPreferences.PdfViewerPreferencesConstants.RIGHT_TO_LEFT);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.R2L, dictionary.get(PdfName.Direction));
    }

    @Test
    public void viewAreaTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.CropBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.ViewArea));

        preferences.setViewArea(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.ViewArea));
    }

    @Test
    public void viewClipTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.CropBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.ViewClip));

        preferences.setViewClip(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.ViewClip));
    }

    @Test
    public void printAreaTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.CropBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.PrintArea));

        preferences.setPrintArea(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.PrintArea));
    }

    @Test
    public void printClipTest() {
        PdfViewerPreferences preferences = new PdfViewerPreferences();
        PdfDictionary dictionary = preferences.getPdfObject();
        Assert.assertEquals(0, dictionary.size());

        // Set non-appropriate value
        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.PRINT_AREA);
        Assert.assertEquals(0, dictionary.size());

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.CROP_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.CropBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.ART_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.ArtBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.BLEED_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.BleedBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.MEDIA_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.MediaBox, dictionary.get(PdfName.PrintClip));

        preferences.setPrintClip(PdfViewerPreferences.PdfViewerPreferencesConstants.TRIM_BOX);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(PdfName.TrimBox, dictionary.get(PdfName.PrintClip));
    }
}
