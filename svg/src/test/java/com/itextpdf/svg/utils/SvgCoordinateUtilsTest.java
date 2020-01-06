/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgCoordinateUtilsTest extends ExtendedITextTest {

    private final static double delta = 0.0000001;

    @Test
    public void calculateAngleBetweenTwoVectors45degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(1, 1, 0);
        double expected = Math.PI / 4;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors45degInverseTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(1, -1, 0);
        double expected = Math.PI / 4;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors135degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, 1, 0);
        double expected = (Math.PI - Math.PI / 4);
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors135degInverseTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, -1, 0);
        double expected = (Math.PI - Math.PI / 4);
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }


    @Test
    public void calculateAngleBetweenTwoVectors90degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(0, 1, 0);
        double expected =  Math.PI / 2;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors180degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, 0, 0);
        double expected =  Math.PI;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }
}
