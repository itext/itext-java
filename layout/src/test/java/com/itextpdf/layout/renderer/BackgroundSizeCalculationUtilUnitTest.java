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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.test.ExtendedITextTest;

import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BackgroundSizeCalculationUtilUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/BackgroundImageTest/";
    private static final float delta = 0.0001f;

    @Test
    public void calculateImageSizeTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        float[] widthAndHeight = BackgroundSizeCalculationUtil.calculateBackgroundImageSize(backgroundImage, 200f, 300f);

        Assertions.assertArrayEquals(new float[] {45f, 45f}, widthAndHeight, delta);
    }

    @Test
    public void calculateImageSizeWithCoverPropertyTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToCover();

        float[] widthAndHeight = BackgroundSizeCalculationUtil.calculateBackgroundImageSize(backgroundImage, 200f, 300f);

        Assertions.assertArrayEquals(new float[] {300f, 300f}, widthAndHeight, delta);
    }

    @Test
    public void calculateSizeWithContainPropertyTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToContain();

        float[] widthAndHeight = BackgroundSizeCalculationUtil.calculateBackgroundImageSize(backgroundImage, 200f, 300f);

        Assertions.assertArrayEquals(new float[] {200f, 200.000015f}, widthAndHeight, delta);
    }

    @Test
    public void calculateSizeWithContainAndImageWeightMoreThatHeightTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToContain();

        float[] widthAndHeight = BackgroundSizeCalculationUtil.calculateBackgroundImageSize(backgroundImage, 200f, 300f);

        Assertions.assertArrayEquals(new float[] {200f, 112.5f}, widthAndHeight, delta);
    }

    @Test
    public void calculateSizeWithCoverAndImageWeightMoreThatHeightTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToCover();

        float[] widthAndHeight = BackgroundSizeCalculationUtil.calculateBackgroundImageSize(backgroundImage, 200f, 300f);

        Assertions.assertArrayEquals(new float[] {533.3333f, 300f}, widthAndHeight, delta);
    }
}
