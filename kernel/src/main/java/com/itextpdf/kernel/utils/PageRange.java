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
package com.itextpdf.kernel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRange {
    private List<Integer> sequenceStarts = new ArrayList<>();
    private List<Integer> sequenceEnds = new ArrayList<>();

    public PageRange() {
    }

    /**
     * You can call specify the page range in a string form, for example: "1-12, 15, 45-66".
     *
     * @param pageRange the page range.
     */
    public PageRange(String pageRange) {
        pageRange = pageRange.replaceAll("\\s+", "");
        Pattern sequencePattern = Pattern.compile("(\\d+)-(\\d+)");
        Pattern singlePagePattern = Pattern.compile("(\\d+)");
        for (String pageRangePart : pageRange.split(",")) {
            Matcher matcher;
            if ((matcher = sequencePattern.matcher(pageRangePart)).matches()) {
                sequenceStarts.add(Integer.parseInt(matcher.group(1)));
                sequenceEnds.add(Integer.parseInt(matcher.group(2)));
            } else if ((matcher = singlePagePattern.matcher(pageRangePart)).matches()) {
                int pageNumber = Integer.parseInt(matcher.group(1));
                sequenceStarts.add(pageNumber);
                sequenceEnds.add(pageNumber);
            }
        }
    }

    public PageRange addPageSequence(int startPageNumber, int endPageNumber) {
        sequenceStarts.add(startPageNumber);
        sequenceEnds.add(endPageNumber);
        return this;
    }

    public PageRange addSinglePage(int pageNumber) {
        sequenceStarts.add(pageNumber);
        sequenceEnds.add(pageNumber);
        return this;
    }

    public List<Integer> getAllPages() {
        List<Integer> allPages = new ArrayList<>();
        for (int ind = 0; ind < sequenceStarts.size(); ind++) {
            for (int pageInRange = (int) sequenceStarts.get(ind); pageInRange <= sequenceEnds.get(ind); pageInRange++) {
                allPages.add(pageInRange);
            }
        }
        return allPages;
    }

    public boolean isPageInRange(int pageNumber) {
        for (int ind = 0; ind < sequenceStarts.size(); ind++) {
            if (sequenceStarts.get(ind) <= pageNumber && pageNumber <= sequenceEnds.get(ind))
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PageRange))
            return false;

        PageRange other = (PageRange) obj;
        return sequenceStarts.equals(other.sequenceStarts) && sequenceEnds.equals(other.sequenceEnds);
    }

    @Override
    public int hashCode() {
        return sequenceStarts.hashCode() * 31 + sequenceEnds.hashCode();
    }
}