// Copyright 2015 Google Inc. All Rights Reserved.
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
// This is part of java port of project hosted at https://github.com/google/woff2
package com.itextpdf.io.font.woff2;

import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.asU16;
import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.asU8;

// Helper functions for woff2 variable length types: 255UInt16 and UIntBase128
class VariableLength {

    // Based on section 6.1.1 of MicroType Express draft spec
    public static int read255UShort(Buffer buf) {
        final int kWordCode = 253;
        final int kOneMoreByteCode2 = 254;
        final int kOneMoreByteCode1 = 255;
        final int kLowestUCode = 253;
        byte code = 0;
        code = buf.readByte();
        if (asU8(code) == kWordCode) {
            short result = buf.readShort();
            return asU16(result);
        } else if (asU8(code) == kOneMoreByteCode1) {
            byte result = buf.readByte();
            return asU8(result) + kLowestUCode;
        } else if (asU8(code) == kOneMoreByteCode2) {
            byte result = buf.readByte();
            return asU8(result) + kLowestUCode * 2;
        } else {
            return  asU8(code);
        }
    }

    public static int readBase128(Buffer buf) {
        int result = 0;
        for (int i = 0; i < 5; ++i) {
            byte code = 0;
            code = buf.readByte();
            // Leading zeros are invalid.
            if (i == 0 && asU8(code) == 0x80) {
                throw new FontCompressionException(FontCompressionException.READ_BASE_128_FAILED);
            }
            // If any of the top seven bits are set then we're about to overflow.
            if ((result & 0xfe000000) != 0) {
                throw new FontCompressionException(FontCompressionException.READ_BASE_128_FAILED);
            }
            result = (result << 7) | (code & 0x7f);
            if ((code & 0x80) == 0) {
                return result;
            }
        }
        // Make sure not to exceed the size bound
        throw new FontCompressionException(FontCompressionException.READ_BASE_128_FAILED);
    }
}
