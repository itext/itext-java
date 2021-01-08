/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.renderer.objectfit;

import com.itextpdf.layout.property.ObjectFit;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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
        Assert.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void fillModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void fillModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void fillModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.FILL, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        Assert.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        float expectedWidth = SMALL_WIDTH / SMALL_HEIGHT * BIG_HEIGHT ;
        Assert.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        float expectedHeight = BIG_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        float expectedHeight = SMALL_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void containModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.CONTAIN, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        float expectedWidth = SMALL_WIDTH / BIG_HEIGHT * SMALL_HEIGHT;
        Assert.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        float expectedHeight = SMALL_HEIGHT / SMALL_WIDTH * BIG_WIDTH;
        Assert.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        float expectedWidth = BIG_WIDTH / BIG_HEIGHT * SMALL_HEIGHT;
        Assert.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        float expectedWidth = BIG_WIDTH / SMALL_HEIGHT * BIG_HEIGHT;
        Assert.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void coverModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.COVER, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        float expectedHeight = BIG_HEIGHT / SMALL_WIDTH * BIG_WIDTH;
        Assert.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        float expectedHeight = BIG_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        float expectedHeight = SMALL_HEIGHT / BIG_WIDTH * SMALL_WIDTH;
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(expectedHeight, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void scaleDownModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.SCALE_DOWN, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        float expectedWidth = SMALL_WIDTH / BIG_HEIGHT * SMALL_HEIGHT;
        Assert.assertEquals(expectedWidth, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }


    @Test
    public void noneModeContainerIsGreaterThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, SMALL_WIDTH, SMALL_HEIGHT, BIG_WIDTH, BIG_HEIGHT
        );
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertFalse(result.isImageCuttingRequired());
    }

    @Test
    public void noneModeContainerIsLessThanImageTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT
        );
        Assert.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void noneModeContainerIsHorizontalAndImageIsVerticalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, BIG_WIDTH, SMALL_HEIGHT, SMALL_WIDTH, BIG_HEIGHT
        );
        Assert.assertEquals(BIG_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(SMALL_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }

    @Test
    public void noneModeContainerIsVerticalAndImageIsHorizontalTest() {
        ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(
                ObjectFit.NONE, SMALL_WIDTH, BIG_HEIGHT, BIG_WIDTH, SMALL_HEIGHT
        );
        Assert.assertEquals(SMALL_WIDTH, result.getRenderedImageWidth(), 0.1);
        Assert.assertEquals(BIG_HEIGHT, result.getRenderedImageHeight(), 0.1);
        Assert.assertTrue(result.isImageCuttingRequired());
    }
}
