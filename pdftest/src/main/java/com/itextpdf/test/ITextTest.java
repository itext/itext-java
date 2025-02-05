/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * This is a generic class for testing. Subclassing it, or its subclasses is considered a good practice of
 * creating your own tests.
 */
@org.junit.jupiter.api.Timeout(value = 5, unit = TimeUnit.MINUTES)
public abstract class ITextTest {

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

    public static void printOutCmpPdfNameAndDir(String out, String cmp) {
        printPathToConsole(out, "Out pdf: ");
        printPathToConsole(cmp, "Cmp pdf: ");
        System.out.println();
        printPathToConsole(new File(out).getParent(), "Out file folder: ");
        printPathToConsole(new File(cmp).getParent(), "Cmp file folder: ");
    }

    public static void printOutputPdfNameAndDir(String pdfName) {
        printPathToConsole(pdfName, "Output PDF: ");
        printPathToConsole(new File(pdfName).getParent(), "Output PDF folder: ");
    }

    public static void printPathToConsole(String path, String comment) {
        System.out.println(comment + "file://" + new File(path).toURI().normalize().getPath());
    }

    protected byte[] readFile(String filename) throws IOException {
        InputStream input = Files.newInputStream(Paths.get(filename));
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

}
