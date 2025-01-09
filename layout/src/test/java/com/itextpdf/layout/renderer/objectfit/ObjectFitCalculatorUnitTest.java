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
package com.itextpdf.layout.renderer.objectfit;

import com.itextpdf.layout.properties.ObjectFit;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ObjectFitCalculatorUnitTest extends ExtendedITextTest {

    private final static float SMALL_WIDTH = 200;
    private final static float BIG_WIDTH = 500;
    private final static float SMALL_HEIGHT = 400;
    private final static float BIG_HEIGHT = 700;

    @Test
    public void fillModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        Assertions.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void fillModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void fillModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void fillModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        Assertions.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        float expectedWidth = SMALL_WIDTH / SMALL_HEIGHT * BIG_HEIGHT ;
        Assertions.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        float expectedHeight = BIG_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        float expectedHeight = SMALL_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        float expectedWidth = SMALL_WIDTH / BIG_HEIGHT * SMALL_HEIGHT;
        Assertions.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        float expectedHeight = SMALL_HEIGHT / SMALL_WIDTH * BIG_WIDTH;
        Assertions.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        float expectedWidth = BIG_WIDTH / BIG_HEIGHT * SMALL_HEIGHT;
        Assertions.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        float expectedWidth = BIG_WIDTH / SMALL_HEIGHT * BIG_HEIGHT;
        Assertions.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        float expectedHeight = BIG_HEIGHT / SMALL_WIDTH * BIG_WIDTH;
        Assertions.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        float expectedHeight = BIG_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        float expectedHeight = SMALL_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        float expectedWidth = SMALL_WIDTH / BIG_HEIGHT * SMALL_HEIGHT;
        Assertions.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }


    @Test
    public void noneModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void noneModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        Assertions.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void noneModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        Assertions.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void noneModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        Assertions.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assertions.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assertions.assertTrue(result.isImageCuttingRequired());
    }
}
