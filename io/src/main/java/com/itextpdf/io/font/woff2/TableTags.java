// Copyright 2014 Google Inc. All Rights Reserved.
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

// Font table tags
class TableTags {
    // Note that the byte order is big-endian
    private static int tag(char a, char b, char c, char d) {
        return ((a << 24) | (b << 16) | (c << 8) | d);
    }

    // Tags of popular tables.
    public static final int kGlyfTableTag = 0x676c7966;
    public static final int kHeadTableTag = 0x68656164;
    public static final int kLocaTableTag = 0x6c6f6361;
    public static final int kDsigTableTag = 0x44534947;
    public static final int kCffTableTag = 0x43464620;
    public static final int kHmtxTableTag = 0x686d7478;
    public static final int kHheaTableTag = 0x68686561;
    public static final int kMaxpTableTag = 0x6d617870;

    public static int[] kKnownTags = new int[]{
            tag('c', 'm', 'a', 'p'),  // 0
            tag('h', 'e', 'a', 'd'),  // 1
            tag('h', 'h', 'e', 'a'),  // 2
            tag('h', 'm', 't', 'x'),  // 3
            tag('m', 'a', 'x', 'p'),  // 4
            tag('n', 'a', 'm', 'e'),  // 5
            tag('O', 'S', '/', '2'),  // 6
            tag('p', 'o', 's', 't'),  // 7
            tag('c', 'v', 't', ' '),  // 8
            tag('f', 'p', 'g', 'm'),  // 9
            tag('g', 'l', 'y', 'f'),  // 10
            tag('l', 'o', 'c', 'a'),  // 11
            tag('p', 'r', 'e', 'p'),  // 12
            tag('C', 'F', 'F', ' '),  // 13
            tag('V', 'O', 'R', 'G'),  // 14
            tag('E', 'B', 'D', 'T'),  // 15
            tag('E', 'B', 'L', 'C'),  // 16
            tag('g', 'a', 's', 'p'),  // 17
            tag('h', 'd', 'm', 'x'),  // 18
            tag('k', 'e', 'r', 'n'),  // 19
            tag('L', 'T', 'S', 'H'),  // 20
            tag('P', 'C', 'L', 'T'),  // 21
            tag('V', 'D', 'M', 'X'),  // 22
            tag('v', 'h', 'e', 'a'),  // 23
            tag('v', 'm', 't', 'x'),  // 24
            tag('B', 'A', 'S', 'E'),  // 25
            tag('G', 'D', 'E', 'F'),  // 26
            tag('G', 'P', 'O', 'S'),  // 27
            tag('G', 'S', 'U', 'B'),  // 28
            tag('E', 'B', 'S', 'C'),  // 29
            tag('J', 'S', 'T', 'F'),  // 30
            tag('M', 'A', 'T', 'H'),  // 31
            tag('C', 'B', 'D', 'T'),  // 32
            tag('C', 'B', 'L', 'C'),  // 33
            tag('C', 'O', 'L', 'R'),  // 34
            tag('C', 'P', 'A', 'L'),  // 35
            tag('S', 'V', 'G', ' '),  // 36
            tag('s', 'b', 'i', 'x'),  // 37
            tag('a', 'c', 'n', 't'),  // 38
            tag('a', 'v', 'a', 'r'),  // 39
            tag('b', 'd', 'a', 't'),  // 40
            tag('b', 'l', 'o', 'c'),  // 41
            tag('b', 's', 'l', 'n'),  // 42
            tag('c', 'v', 'a', 'r'),  // 43
            tag('f', 'd', 's', 'c'),  // 44
            tag('f', 'e', 'a', 't'),  // 45
            tag('f', 'm', 't', 'x'),  // 46
            tag('f', 'v', 'a', 'r'),  // 47
            tag('g', 'v', 'a', 'r'),  // 48
            tag('h', 's', 't', 'y'),  // 49
            tag('j', 'u', 's', 't'),  // 50
            tag('l', 'c', 'a', 'r'),  // 51
            tag('m', 'o', 'r', 't'),  // 52
            tag('m', 'o', 'r', 'x'),  // 53
            tag('o', 'p', 'b', 'd'),  // 54
            tag('p', 'r', 'o', 'p'),  // 55
            tag('t', 'r', 'a', 'k'),  // 56
            tag('Z', 'a', 'p', 'f'),  // 57
            tag('S', 'i', 'l', 'f'),  // 58
            tag('G', 'l', 'a', 't'),  // 59
            tag('G', 'l', 'o', 'c'),  // 60
            tag('F', 'e', 'a', 't'),  // 61
            tag('S', 'i', 'l', 'l'),  // 62
    };
}
