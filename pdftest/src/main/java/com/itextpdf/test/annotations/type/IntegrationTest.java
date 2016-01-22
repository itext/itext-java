package com.itextpdf.test.annotations.type;

/**
 * Tests that cover more functionality than a simple Unit Test. Contrary to Unit
 * Tests, they don't use mocked objects but real objects. These tests are
 * intended to check end-to-end functionality of a feature.
 * <p>
 * For example, if a test creates a Document, manipulates it, writes the result
 * to disk, and then compares the file with a reference document, then it is
 * definitely not a Unit Test but most likely an Integration Test.
 * 
 * @author Amedee Van Gasse
 *
 */
public interface IntegrationTest extends SlowTest {
}
