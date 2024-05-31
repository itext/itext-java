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
package com.itextpdf.signatures;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Utility class with some KeyStore related methods.
 */
public class KeyStoreUtil {

    /**
     * Loads the default root certificates at &lt;java.home&gt;/lib/security/cacerts.
     * @param provider the provider or <code>null</code> for the default provider
     * @return a <CODE>KeyStore</CODE>
     */
    public static KeyStore loadCacertsKeyStore(String provider) {
        File file = new File(System.getProperty("java.home"), "lib");
        file = new File(file, "security");
        file = new File(file, "cacerts");
        InputStream fin = null;
        try {
            fin = FileUtil.getInputStreamForFile(file);
            KeyStore k;
            if (provider == null) {
                k = KeyStore.getInstance("JKS");
            } else {
                k = KeyStore.getInstance("JKS", provider);
            }
            k.load(fin, null);
            return k;
        }
        catch (Exception e) {
            throw new PdfException(e);
        }
        finally {
            try  {
                if (fin != null) {
                    fin.close();
                }
            } catch (Exception ex) {
                // do nothing
            }
        }
    }

    /**
     * Loads the default root certificates at &lt;java.home&gt;/lib/security/cacerts
     * with the default provider.
     * @return a <CODE>KeyStore</CODE>
     */
    public static KeyStore loadCacertsKeyStore() {
        return loadCacertsKeyStore(null);
    }
}
