/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a generic class for testing. Subclassing it, or its subclasses is considered a good practice of
 * creating your own tests.
 */
public abstract class ITextTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Rule
    public Timeout testTimeout = new Timeout(5, TimeUnit.MINUTES);

    /**
     * Creates a folder with a given path, including all necessary nonexistent parent directories.
     * If a folder is already present, no action is performed.
     * @param path the path of the folder to create
     */
    public static void createDestinationFolder(String path) {
        File fpath = new File(path);
        fpath.mkdirs();
    }

    /**
     * Creates a directory with given path if it does not exist and clears the contents
     * of the directory in case it exists.
     * @param path the path of the directory to be created/cleared
     */
    public static void createOrClearDestinationFolder(String path) {
        File fpath = new File(path);
        fpath.mkdirs();
        deleteDirectoryContents(path, false);
    }

    /**
     * Removes the directory with given path along with its content including all the subdirectories.
     * @param path the path of the directory to be removed
     */
    public static void deleteDirectory(String path) {
        deleteDirectoryContents(path, true);
    }

    /**
     * Due to import control restrictions by the governments of a few countries,
     * the encryption libraries shipped by default with the Java SDK restrict the
     * length, and as a result the strength, of encryption keys. Be aware that by
     * using this method we remove cryptography restrictions via reflection for
     * testing purposes.
     *
     * For more conventional way of solving this problem you need to replace the
     * default security JARs in your Java installation with the Java Cryptography
     * Extension (JCE) Unlimited Strength Jurisdiction Policy Files. These JARs
     * are available for download from http://java.oracle.com/ in eligible countries.
     */
    public static void removeCryptographyRestrictions() {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").
                    getDeclaredField("isRestricted");
            if (field.isAccessible()) {
                // unexpected case
                return;
            }

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            modifiersField.setAccessible(false);

            field.setAccessible(true);
            if (field.getBoolean(null)) {
                field.set(null, java.lang.Boolean.FALSE);
            } else {
                field.setAccessible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * By using this method we restore cryptography restrictions via reflection.
     * This method is opposite to {@link ITextTest#removeCryptographyRestrictions()}.
     */
    public static void restoreCryptographyRestrictions() {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").
                    getDeclaredField("isRestricted");
            if (field.isAccessible()) {
                field.set(null, java.lang.Boolean.TRUE);
                field.setAccessible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteDirectoryContents(String path, boolean removeParentDirectory) {
        File file = new File(path);
        if (file.exists() && file.listFiles() != null) {
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    deleteDirectoryContents(f.getPath(), false);
                    f.delete();
                } else {
                    f.delete();
                }
            }
            if (removeParentDirectory) {
                file.delete();
            }
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
