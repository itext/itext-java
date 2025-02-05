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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.Stack;

@Tag("UnitTest")
public class CharacterRenderInfoTest extends ExtendedITextTest {

    @Test
    public void boundingBoxForRotatedText() throws IOException {
        TextRenderInfo tri = initTRI("abc", Math.PI / 2);

        CharacterRenderInfo characterRenderInfo = new CharacterRenderInfo(tri);

        Assertions.assertTrue(characterRenderInfo.getBoundingBox().equalsWithEpsilon(new Rectangle(-8.616f, 0f, 11.1f, 19.344f)));
    }

    private static TextRenderInfo initTRI(String text, double angle) throws IOException {
        CanvasGraphicsState gs = new CanvasGraphicsState();
        gs.setFont(PdfFontFactory.createFont());
        gs.setFontSize(12);
        float[] matrix = new float[6];
        AffineTransform transform = AffineTransform.getRotateInstance(angle);
        transform.getMatrix(matrix);
        return new TextRenderInfo(new PdfString(text), gs,
                new Matrix(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]), new Stack<CanvasTag>());
    }
}
