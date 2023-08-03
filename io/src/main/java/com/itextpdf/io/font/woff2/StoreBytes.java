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
// This is part of java port of project hosted at https://github.com/google/woff2
package com.itextpdf.io.font.woff2;

import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.toU8;

// Helper functions for storing integer values into byte streams.
// No bounds checking is performed, that is the responsibility of the caller.
class StoreBytes {

    public static int storeU32(byte[] dst, int offset, int x) {
        dst[offset] = toU8(x >> 24);
        dst[offset + 1] = toU8(x >> 16);
        dst[offset + 2] = toU8(x >> 8);
        dst[offset + 3] = toU8(x);
        return offset + 4;
    }

    public static int storeU16(byte[] dst, int offset, int x) {
        dst[offset] = toU8(x >> 8);
        dst[offset + 1] = toU8(x);
        return offset + 2;
    }
}
