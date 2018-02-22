package com.itextpdf.svg.processors;

import com.itextpdf.svg.processors.impl.ProcessorState;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.EmptyStackException;

@Category(UnitTest.class)
public class ProcessorStateTest {

    @Test
    /**
     * Push test
     */
    public void processorStateTestPush(){
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test",null);
        testProcessorState.push(renderer);

        Assert.assertTrue(testProcessorState.getStack().size() == 1);
    }

    /**
     * Pop test
     */
    @Test
    public void processorStateTestPop(){
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test",null);
        testProcessorState.push(renderer);

        ISvgNodeRenderer popped=  testProcessorState.pop();
        Assert.assertTrue(popped.toString().equals("test") && testProcessorState.empty());
    }

    @Test
    /**
     * Peek test
     */
    public void processorStateTestPeek(){
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer renderer = new DummySvgNodeRenderer("test",null);
        testProcessorState.push(renderer);

        ISvgNodeRenderer viewed=  testProcessorState.top();
        Assert.assertTrue(viewed.toString().equals("test") && !testProcessorState.empty());

    }

    /**
     * Multiple push test
     */
    @Test
    public void processorStateTestMultiplePushesPopAndPeek(){
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer rendererOne = new DummySvgNodeRenderer("test01",null);
        testProcessorState.push(rendererOne);
        ISvgNodeRenderer rendererTwo = new DummySvgNodeRenderer("test02",null);
        testProcessorState.push(rendererTwo);

        ISvgNodeRenderer popped =  testProcessorState.pop();
        boolean result = popped.toString().equals("test02");
        result = result && testProcessorState.top().toString().equals("test01");
        Assert.assertTrue(result);
    }

    @Test(expected = EmptyStackException.class)
    public void processorStateTestPopEmpty(){
        ProcessorState testProcessorState = new ProcessorState();
        testProcessorState.pop();
    }

    @Test()
    public void processorStateTestPushSameElementTwice(){
        ProcessorState testProcessorState = new ProcessorState();
        ISvgNodeRenderer rendererOne = new DummySvgNodeRenderer("test01",null);
        testProcessorState.push(rendererOne);
        testProcessorState.push(rendererOne);

        ISvgNodeRenderer popped =  testProcessorState.pop();
        boolean result = popped.toString().equals("test01");
        result = result && testProcessorState.top().toString().equals("test01");
        Assert.assertTrue(result);
    }


    @Test(expected = EmptyStackException.class)
    public void processorStateTestPeekEmpty(){
        ProcessorState testProcessorState = new ProcessorState();
        testProcessorState.pop();
    }




}
