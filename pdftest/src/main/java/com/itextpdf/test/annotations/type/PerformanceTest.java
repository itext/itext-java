package com.itextpdf.test.annotations.type;

/**
 * Performance Tests generate performance metrics: speed, memory usage, disk
 * space,...
 * <p>
 * A Performance Test may repeat the same (or similar) test cases a lot, and may
 * compare the metrics with metrics of a previous version.
 * 
 * @author Amedee Van Gasse
 *
 */
public interface PerformanceTest extends SlowTest {
}
