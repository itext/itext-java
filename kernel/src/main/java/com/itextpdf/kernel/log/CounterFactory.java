package com.itextpdf.kernel.log;

/**
 * Factory that creates a counter for every reader or writer class.
 * You can implement your own counter and declare it like this:
 * <code>CounterFactory.getInstance().setCounter(new SysoCounter());</code>
 * SysoCounter is just an example of a Counter implementation.
 * It writes info about files being read and written to the System.out.
 * <p>
 * This functionality can be used to create metrics in a SaaS context.
 */
public class CounterFactory {

    /**
     * The singleton instance.
     */
    private static CounterFactory instance;

    /**
     * The current counter implementation.
     */
    private Counter counter = new DefaultCounter();

    static {
        instance = new CounterFactory();
    }

    /**
     * The empty constructor.
     */
    private CounterFactory() {
    }

    /**
     * Returns the singleton instance of the factory.
     */
    public static CounterFactory getInstance() {
        return instance;
    }

    /**
     * Returns a counter factory.
     */
    public static Counter getCounter(Class<?> cls) {
        return instance.counter.getCounter(cls);
    }

    /**
     * Getter for the counter.
     */
    public Counter getCounter() {
        return counter;
    }

    /**
     * Setter for the counter.
     */
    public void setCounter(Counter counter) {
        this.counter = counter;
    }

}
