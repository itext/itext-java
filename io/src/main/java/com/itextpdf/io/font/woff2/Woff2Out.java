// Copyright 2016 Google Inc. All Rights Reserved.
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

/**
 * Output interface for the woff2 decoding.
 *
 * Writes to arbitrary offsets are supported to facilitate updating offset
 * table and checksums after tables are ready. Reading the current size is
 * supported so a 'loca' table can be built up while writing glyphs.
 *
 * By default limits size to kDefaultMaxSize.
 */
interface Woff2Out {

    // Append n bytes of data from buf.
    // Return true if all written, false otherwise.
    void write(byte[] buf, int buff_offset, int n);

    // Write n bytes of data from buf at offset.
    // Return true if all written, false otherwise.
    void write(byte buf[], int buff_offset, int offset, int n);

    int size();
}
