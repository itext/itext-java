/* Copyright 2015 Google Inc. All Rights Reserved.

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package com.itextpdf.io.codec.brotli.dec;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link BitReader}.
 */
@Tag("UnitTest")
public class BitReaderTest extends ExtendedITextTest {

    @Test
    public void testReadAfterEos() {
        State reader = new State();
        reader.input = new ByteArrayInputStream(new byte[1]);
        Decode.initState(reader);
        BitReader.readBits(reader, 9);
        try {
            BitReader.checkHealth(reader, 0);
        } catch (BrotliRuntimeException ex) {
            // This exception is expected.
            return;
        }
        fail("BrotliRuntimeException should have been thrown by BitReader.checkHealth");
    }

    @Test
    @Disabled("We should set BROTLI_ENABLE_ASSERTS environment variable to true.")
    public void testAccumulatorUnderflowDetected() {
        State reader = new State();
        reader.input = new ByteArrayInputStream(new byte[8]);
        Decode.initState(reader);
        // 65 bits is enough for both 32 and 64 bit systems.
        BitReader.readBits(reader, 13);
        BitReader.readBits(reader, 13);
        BitReader.readBits(reader, 13);
        BitReader.readBits(reader, 13);
        BitReader.readBits(reader, 13);
        try {
            BitReader.fillBitWindow(reader);
        } catch (IllegalStateException ex) {
            // This exception is expected.
            return;
        }
        fail("IllegalStateException should have been thrown by 'broken' BitReader");
    }
}
