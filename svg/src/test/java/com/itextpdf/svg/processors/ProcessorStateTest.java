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
package com.itextpdf.svg.processors;

import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.processors.impl.ProcessorState;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;

import java.util.EmptyStackException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ProcessorStateTest extends ExtendedITextTest{

    /**
     * Push test
     */
    @Test
    public void processorStateTestPush() {
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test");
        testProcessorState.push(renderer);

        Assertions.assertTrue(testProcessorState.size() == 1);
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
        Assertions.assertTrue(popped.toString().equals("test") && testProcessorState.empty());
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
        Assertions.assertTrue(viewed.toString().equals("test") && ! testProcessorState.empty());

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
        Assertions.assertTrue(result);
    }

    @Test
    public void processorStateTestPopEmpty() {
        ProcessorState testProcessorState = new ProcessorState();

        Assertions.assertThrows(EmptyStackException.class, () -> testProcessorState.pop());
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
        Assertions.assertTrue(result);
    }


    @Test
    public void processorStateTestPeekEmpty() {
        ProcessorState testProcessorState = new ProcessorState();

        Assertions.assertThrows(EmptyStackException.class, () -> testProcessorState.pop());
    }


}
