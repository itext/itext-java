// Copyright 2013 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// The parts of ots.h & opentype-sanitiser.h that we need, taken from the
// https://code.google.com/p/ots/ project.
//
// This is part of java port of project hosted at https://github.com/google/woff2
package com.itextpdf.io.font.woff2;

import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.asU8;
import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.toU16;
import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.toU8;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

// -----------------------------------------------------------------------------
// Buffer helper class
//
// This class perform some trival buffer operations while checking for
// out-of-bounds errors. As a family they throw exception if anything is amiss,
// updating the current offset otherwise.
// -----------------------------------------------------------------------------
class Buffer {
    private byte[] data;
    private int offset;
    private int initial_offset;
    private int length;

    public Buffer(byte[] data, int data_offset, int length) {
        this.offset = 0;
        this.initial_offset = data_offset;
        this.length = length;
        this.data = data;
    }

    public Buffer(Buffer other) {
        this.offset = other.offset;
        this.initial_offset = other.initial_offset;
        this.length = other.length;
        this.data = other.data;
    }

    public int readInt() {
        return readAsNumber(4);
    }

    public short readShort() {
        return toU16(readAsNumber(2));
    }

    public byte readByte() {
        return toU8(readAsNumber(1));
    }

    public void skip(int n_bytes) {
        read(null, 0, n_bytes);
    }

    public void read(byte[] data, int data_offset, int n_bytes) {
        if (offset + n_bytes > length || offset > length - n_bytes) {
            throw new FontCompressionException(FontCompressionException.BUFFER_READ_FAILED);
        }
        if (data != null) {
            if (data_offset + n_bytes > data.length || data_offset > data.length - n_bytes) {
                throw new FontCompressionException(FontCompressionException.BUFFER_READ_FAILED);
            }
            System.arraycopy(this.data, initial_offset + offset, data, data_offset, n_bytes);
        }
        this.offset += n_bytes;
    }

    public int getOffset() {
        return offset;
    }

    public int getInitialOffset() {
        return initial_offset;
    }

    public int getLength() {
        return length;
    }

    private int readAsNumber(int n_bytes) {
        byte[] buffer = new byte[n_bytes];
        read(buffer, 0, n_bytes);
        int result = 0;
        for (int i = 0; i < n_bytes; ++i) {
            result = (result << 8) | asU8(buffer[i]);
        }
        return result;
    }
}
