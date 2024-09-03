/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("UnitTest")
public class HighPrecisionOutputStreamTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/source/OSTEST.txt";
    private static java.io.OutputStream IO_EXCEPTION_OUTPUT_STREAM;

    static {
        try {
            IO_EXCEPTION_OUTPUT_STREAM = new FileOutputStream(SOURCE_FOLDER, true);
            IO_EXCEPTION_OUTPUT_STREAM.close();
        } catch (IOException e) {
            //ignore
        }
    }

    @Test
    public void changePrecisionTest() throws IOException {
        //the data is random
        double expected = 0.100001d;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes, false)) {
            stream.setLocalHighPrecision(true);
            stream.writeDouble(expected);
            stream.flush();
            Assertions.assertEquals(Objects.toString(expected), new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void changePrecisionToFalseTest() throws IOException {
        //the data is random
        double expected = 0.000002d;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes, false)) {
            stream.setLocalHighPrecision(false);
            stream.writeDouble(expected);
            stream.flush();
            Assertions.assertEquals("0", new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.ATTEMPT_PROCESS_NAN, count = 1)
    })
    @Test
    public void writeNanTest() throws IOException {
        //the data is random
        String expected = "0";
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeDouble(Double.NaN);
            stream.flush();
            Assertions.assertEquals(expected, new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void writeValidByteArrayTest() throws IOException {
        //the data is random
        byte[] expected = new byte[] {(byte) 68, (byte) 14, (byte) 173, (byte) 105};
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.write(expected);
            stream.flush();
            Assertions.assertArrayEquals(expected, bytes.toByteArray());
        }
    }

    @Test
    public void writeValidBytesArrayTest() throws IOException {
        //the data is random
        byte[] expected = new byte[] {(byte) 15, (byte) 233, (byte) 58, (byte) 97};
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeBytes(expected);
            stream.flush();
            Assertions.assertArrayEquals(expected, bytes.toByteArray());
        }
    }

    @Test
    public void writeSingleValidByteTest() throws IOException {
        //the data is random
        byte expected = (byte) 193;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeByte(expected);
            stream.flush();
            Assertions.assertArrayEquals(new byte[] {expected}, bytes.toByteArray());
        }
    }

    @Test
    public void writeSingleValidIntegerTest() throws IOException {
        //the data is random
        int expected = 1695609641;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeInteger(expected);
            stream.flush();
            Assertions.assertEquals(Objects.toString(expected), new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void writeSingleValidLongTest() throws IOException {
        //the data is random
        long expected = 1695609641552L;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeLong(expected);
            stream.flush();
            Assertions.assertEquals(Objects.toString(expected), new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void writeValidFloatsArrayTest() throws IOException {
        //the data is random
        float[] expected = new float[] {12.05f, 0.001f};
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeFloats(expected);
            stream.flush();
            Assertions.assertEquals(expected[0] + " " + expected[1], new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void writeValidBytesWithOffsetTest() throws IOException {
        //the data is random
        byte[] expected = new byte[] {(byte) 233, (byte) 58};
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeBytes(new byte[] {(byte) 15, (byte) 233, (byte) 58, (byte) 97}, 1, 2);
            stream.flush();
            Assertions.assertArrayEquals(expected, bytes.toByteArray());
        }
    }

    @Test()
    public void writeBytesIOExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            byte[] bytesToWrite = new byte[] {(byte) 71};
            try (java.io.OutputStream bytes = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(bytes)) {
                stream.writeBytes(bytesToWrite);
            }
        });
    }

    @Test()
    public void writeByteIOExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            byte byteToWrite = (byte) 71;
            try (java.io.OutputStream bytes = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(bytes)) {
                stream.writeByte(byteToWrite);
            }
        });
    }

    @Test()
    public void writeByteIntIOExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            //the data is random
            int byteToWrite = 71;
            try (java.io.OutputStream bytes = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(bytes)) {
                stream.writeByte(byteToWrite);
            }
        });
    }

    @Test()
    public void writeDoubleIOExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            //the data is random
            double num = 55.55d;
            try (java.io.OutputStream bytes = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(bytes)) {
                stream.writeDouble(num);
            }
        });
    }

    @Test()
    public void writeLongIOExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            //the data is random
            long num = 55L;
            try (java.io.OutputStream bytes = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(bytes)) {
                stream.writeLong(num);
            }
        });
    }

    @Test
    public void writeValidStringTest() throws IOException {
        String expected = "Test string to write";
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.writeString(expected);
            stream.writeNewLine();
            stream.flush();
            Assertions.assertEquals(expected + '\n', new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void gettersAndSettersTest() throws IOException {
        AssertUtil.doesNotThrow(() -> {
            //testing that stream is not closed, if setCloseStream is false
            HighPrecisionOutputStream<OutputStream> stream
                    = new HighPrecisionOutputStream<>(null);
            stream.setCloseStream(false);
            stream.close();
        });
    }

    @Test
    public void assignBytesArrayTest() throws IOException {
        //the data is random
        byte[] expected = new byte[] {(byte) 15, (byte) 233, (byte) 58, (byte) 97};
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes)) {
            stream.assignBytes(expected, 4);
            Assertions.assertArrayEquals(expected, bytes.toByteArray());
        }
    }

    @Test
    public void assignBytesExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            //the data is random
            byte[] bytes = new byte[] {(byte) 15, (byte) 233, (byte) 58, (byte) 97};
            try (java.io.OutputStream outputStream = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(outputStream)) {
                stream.assignBytes(bytes, 4);
            }
        });
    }

    @Test
    public void resetTestNoException() throws IOException {
            AssertUtil.doesNotThrow(() -> {
                try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        HighPrecisionOutputStream<ByteArrayOutputStream> stream
                                = new HighPrecisionOutputStream<>(bytes)) {
                    stream.writeBytes(new byte[] {(byte) 15, (byte) 233, (byte) 58, (byte) 97});
                    stream.flush();
                    stream.reset();
                }
            });
    }

    @Test
    public void resetExceptionTest() throws IOException {
        //Testing that the exception is thrown, not using specific one because of .NET compatability
        Assertions.assertThrows(Exception.class,() -> {
            try (java.io.OutputStream bytes = IO_EXCEPTION_OUTPUT_STREAM;
                    HighPrecisionOutputStream<ByteArrayOutputStream> stream
                            = new HighPrecisionOutputStream<>(bytes)) {
                stream.reset();
            }
        });
    }

    @Test
    public void localHighPrecisionOverridesGlobalTest() throws IOException {

        boolean highPrecision = HighPrecisionOutputStream.getHighPrecision();

        //the data is random
        double numberToWrite = 2.000002d;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HighPrecisionOutputStream<ByteArrayOutputStream> stream
                        = new HighPrecisionOutputStream<>(bytes, false)) {
            HighPrecisionOutputStream.setHighPrecision(true);
            stream.setLocalHighPrecision(false);
            stream.writeDouble(numberToWrite);
            stream.flush();
            Assertions.assertEquals("2", new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        } finally {
            HighPrecisionOutputStream.setHighPrecision(highPrecision);
        }
    }
}
