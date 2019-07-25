/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.svg.processors;

import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.processors.impl.ProcessorState;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.EmptyStackException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category( UnitTest.class )
public class ProcessorStateTest extends ExtendedITextTest{

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    /**
     * Push test
     */
    @Test
    public void processorStateTestPush() {
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test");
        testProcessorState.push(renderer);

        Assert.assertTrue(testProcessorState.size() == 1);
    }

    /**
     * Pop test
     */
    @Test
    public void processorStateTestPop() {
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test");
        testProcessorState.push(renderer);

        ISvgNodeRenderer popped = testProcessorState.pop();
        Assert.assertTrue(popped.toString().equals("test") && testProcessorState.empty());
    }

    /**
     * Peek test
     */
    @Test
    public void processorStateTestPeek() {
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test");
        testProcessorState.push(renderer);

        ISvgNodeRenderer viewed = testProcessorState.top();
        Assert.assertTrue(viewed.toString().equals("test") && ! testProcessorState.empty());

    }

    /**
     * Multiple push test
     */
    @Test
    public void processorStateTestMultiplePushesPopAndPeek() {
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer rendererOne = new DummySvgNodeRenderer("test01");
        testProcessorState.push(rendererOne);
        ISvgNodeRenderer rendererTwo = new DummySvgNodeRenderer("test02");
        testProcessorState.push(rendererTwo);

        ISvgNodeRenderer popped = testProcessorState.pop();
        boolean result = popped.toString().equals("test02");
        result = result && testProcessorState.top().toString().equals("test01");
        Assert.assertTrue(result);
    }

    @Test
    public void processorStateTestPopEmpty() {
        junitExpectedException.expect(EmptyStackException.class);
        ProcessorState testProcessorState = new ProcessorState();

        testProcessorState.pop();
    }

    @Test
    public void processorStateTestPushSameElementTwice() {
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer rendererOne = new DummySvgNodeRenderer("test01");
        testProcessorState.push(rendererOne);
        testProcessorState.push(rendererOne);

        ISvgNodeRenderer popped = testProcessorState.pop();
        boolean result = popped.toString().equals("test01");
        result = result && testProcessorState.top().toString().equals("test01");
        Assert.assertTrue(result);
    }


    @Test
    public void processorStateTestPeekEmpty() {
        junitExpectedException.expect(EmptyStackException.class);
        ProcessorState testProcessorState = new ProcessorState();
        testProcessorState.pop();
    }


}
