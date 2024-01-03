/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.margincollapse;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class MarginsCollapseHandlerTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 2)
    })
    // This test's aim is to test message logging.
    public void testDefiningMarginCollapse() {
        ParagraphRenderer paragraphRenderer = new ParagraphRenderer(new Paragraph());
        Rectangle rectangle = new Rectangle(0f, 0f);
        paragraphRenderer.getModelElement().setProperty(Property.MARGIN_TOP, UnitValue.createPercentValue(0f));
        paragraphRenderer.getModelElement().setProperty(Property.MARGIN_BOTTOM, UnitValue.createPercentValue(0f));

        MarginsCollapseHandler marginsCollapseHandler = new MarginsCollapseHandler(paragraphRenderer, null);
        AssertUtil.doesNotThrow(() -> marginsCollapseHandler.startMarginsCollapse(rectangle));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 2)
    })
    // This test's aim is to test message logging.
    public void testHasPadding() {
        ParagraphRenderer paragraphRenderer = new ParagraphRenderer(new Paragraph());
        Rectangle rectangle = new Rectangle(0f, 0f);
        paragraphRenderer.getModelElement().setProperty(Property.PADDING_TOP, UnitValue.createPercentValue(0f));
        paragraphRenderer.getModelElement().setProperty(Property.PADDING_BOTTOM, UnitValue.createPercentValue(0f));

        MarginsCollapseHandler marginsCollapseHandler = new MarginsCollapseHandler(paragraphRenderer, null);
        AssertUtil.doesNotThrow(() -> marginsCollapseHandler.startMarginsCollapse(rectangle));
    }
}
