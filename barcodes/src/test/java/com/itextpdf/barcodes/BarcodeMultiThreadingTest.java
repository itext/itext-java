package com.itextpdf.barcodes;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BarcodeMultiThreadingTest {

    private static class DataMatrixThread extends Thread {
        @Override
        public void run() {
            BarcodeDataMatrix bc = new BarcodeDataMatrix();
            bc.setOptions(BarcodeDataMatrix.DM_AUTO);
            bc.setWidth(10);
            bc.setHeight(10);
            int result = bc.setCode("AB01");

            Assert.assertEquals(BarcodeDataMatrix.DM_NO_ERROR, result);
        }
    }

    @Test(timeout = 10000)
    public void test() throws InterruptedException {
        Thread[] threads = new DataMatrixThread[20];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new DataMatrixThread();
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
