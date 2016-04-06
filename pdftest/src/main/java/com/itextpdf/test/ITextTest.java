/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.test;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ITextTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static void createDestinationFolder(String path) {
        File fpath = new File(path);
        fpath.mkdirs();
    }

    public static void createOrClearDestinationFolder(String path) {
        File fpath = new File(path);
        fpath.mkdirs();
        for (File file : fpath.listFiles())
            file.delete();
    }

    public static void deleteDirectory(String path) {
        File fpath = new File(path);
        if (fpath.exists() && fpath.listFiles() != null) {
            for (File f : fpath.listFiles()) {
                if (f.isDirectory()) {
                    deleteDirectory(f.getPath());
                    f.delete();
                } else {
                    f.delete();
                }
            }
            fpath.delete();
        }
    }

    protected byte[] readFile(String filename) throws IOException {
        FileInputStream input = new FileInputStream(filename);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        input.close();
        return output.toByteArray();
    }

    protected String createStringByEscaped(byte[] bytes) {
        String[] chars = (new String(bytes)).substring(1).split("#");
        StringBuilder buf = new StringBuilder(chars.length);
        for (String ch : chars) {
            if (ch.length() == 0) continue;
            Integer b = Integer.parseInt(ch, 16);
            buf.append((char) b.intValue());
        }
        return buf.toString();
    }

}
