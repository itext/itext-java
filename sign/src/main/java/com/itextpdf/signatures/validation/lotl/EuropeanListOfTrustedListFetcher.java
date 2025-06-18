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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

/**
 * Fetches the European List of Trusted Lists (LOTL) from a predefined URL.
 * <p>
 * This class is used to retrieve the LOTL XML file, which contains information about trusted lists in the European
 * Union.
 */
public class EuropeanListOfTrustedListFetcher {


    private final IResourceRetriever resourceRetriever;
    private byte[] lotlData;
    private Date lastLoaded;

    /**
     * Creates a new instance of {@link EuropeanListOfTrustedListFetcher}.
     *
     * @param resourceRetriever the resource retriever used to fetch the LOTL data
     */
    public EuropeanListOfTrustedListFetcher(IResourceRetriever resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
    }


    /**
     * Loads the List of Trusted Lists (LOTL) from the predefined URL.
     *
     * @throws IOException if there is an error retrieving the LOTL data
     */
    public void load() throws IOException {
        EuropeanTrustedListConfigurationFactory factory = EuropeanTrustedListConfigurationFactory.getFactory().get();
        final URL url = UrlUtil.toURL(factory.getTrustedListUri());
        byte[] data = resourceRetriever.getByteArrayByUrl(url);
        if (data == null) {
            throw new ITextException(MessageFormatUtil.format(
                    SignExceptionMessageConstant.FAILED_TO_GET_EU_LOTL, url.toString()));
        }
        this.lotlData = data;
        this.lastLoaded = new Date();
    }

    /**
     * Retrieves the List of Trusted Lists (LOTL) data.
     * If the data has not been loaded yet, it will call the {@link #load()} method to fetch it.
     *
     * @return the LOTL data as a byte array
     * @throws IOException if there is an error loading the LOTL data
     */
    public byte[] getLotlData() throws IOException {
        if (lotlData == null) {
            load();
        }
        // Ensure the data is not modified outside this class
        return Arrays.copyOf(lotlData, lotlData.length);
    }

    /**
     * Gets the last loaded date of the LOTL data.
     *
     * @return the date when the LOTL data was last loaded
     */
    public Date getLastLoaded() {
        return lastLoaded;
    }
}
