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
package com.itextpdf.kernel.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a page range, for instance a page range can contain pages
 * 5, then pages 10 through 15, then page 18, then page 21 and so on.
 */
public class PageRange {

    private static final Pattern SEQUENCE_PATTERN = Pattern.compile("(\\d+)-(\\d+)?");
    private static final Pattern SINGLE_PAGE_PATTERN = Pattern.compile("(\\d+)");

    private List<IPageRangePart> sequences = new ArrayList<>();

    /**
     * Constructs an empty {@link PageRange} instance.
     */
    public PageRange() {
    }

    /**
     * Constructs a {@link PageRange} instance from a range in a string form,
     * for example: "1-12, 15, 45-66". More advanced forms are also available,
     * for example:
     * - "3-" to indicate from page 3 to the last page
     * - "odd" for all odd pages
     * - "even" for all even pages
     * - "3- &amp; odd" for all odd pages starting from page 3
     * 
     * A complete example for pages 1 to 5, page 8 then odd pages starting from
     * page 9: "1-5, 8, odd &amp; 9-".
     *
     * @param pageRange a String of page ranges
     */
    public PageRange(String pageRange) {
        pageRange = pageRange.replaceAll("\\s+", "");
        for (String pageRangePart : pageRange.split(",")) {
            IPageRangePart cond = getRangeObject(pageRangePart);
            if (cond != null) {
                sequences.add(cond);
            }
        }
    }

    private static IPageRangePart getRangeObject(String rangeDef) {
        if (rangeDef.contains("&")) {
            List<IPageRangePart> conditions = new ArrayList<>();
            for (String pageRangeCond : rangeDef.split("&")) {
                IPageRangePart cond = getRangeObject(pageRangeCond);
                if (cond != null) {
                    conditions.add(cond);
                }
            }
            if (conditions.size() > 0) {
                return new PageRangePartAnd(conditions.toArray(new IPageRangePart[]{}));
            } else {
                return null;
            }
        } else {
            Matcher matcher;
            if ((matcher = SEQUENCE_PATTERN.matcher(rangeDef)).matches()) {
                int start = Integer.parseInt(matcher.group(1));
                if (matcher.group(2) != null) {
                    return new PageRangePartSequence(start, Integer.parseInt(matcher.group(2)));
                } else {
                    return new PageRangePartAfter(start);
                }
            } else if ((matcher = SINGLE_PAGE_PATTERN.matcher(rangeDef)).matches()) {
                return new PageRangePartSingle(Integer.parseInt(matcher.group(1)));
            } else if ("odd".equalsIgnoreCase(rangeDef)) {
                return PageRangePartOddEven.ODD;
            } else if ("even".equalsIgnoreCase(rangeDef)) {
                return PageRangePartOddEven.EVEN;
            }
            return null;
        }
    }

    /**
     * Adds any page range part to this page range. Users may define and plug in
     * custom implementations for behavior not found in the standard library.
     *
     * @param part a custom implementation of {@link IPageRangePart}
     * @return this range, already modified
     */
    public PageRange addPageRangePart(IPageRangePart part) {
        sequences.add(part);
        return this;
    }

    /**
     * Adds a page sequence to the range.
     *
     * @param startPageNumber the starting page number of the sequence
     * @param endPageNumber the finishing page number of the sequence
     * @return this range, already modified
     */
    public PageRange addPageSequence(int startPageNumber, int endPageNumber) {
        return addPageRangePart(new PageRangePartSequence(startPageNumber, endPageNumber));
    }

    /**
     * Adds a single page to the range.
     *
     * @param pageNumber the page number to add
     * @return this range, already modified
     */
    public PageRange addSinglePage(int pageNumber) {
        return addPageRangePart(new PageRangePartSingle(pageNumber));
    }

    /**
     * Gets the list of pages that have been added to the range so far.
     *
     * @param nbPages number of pages of the document to get the pages, to list
     * only the pages eligible for this document.
     * @return the list containing page numbers added to the range matching this
     * document
     */
    public List<Integer> getQualifyingPageNums(int nbPages) {
        List<Integer> allPages = new ArrayList<>();
        for (IPageRangePart sequence : sequences) {
            allPages.addAll(sequence.getAllPagesInRange(nbPages));
        }
        return allPages;
    }

    /**
     * Checks if a given page is present in the range built so far.
     *
     * @param pageNumber the page number to check
     * @return <code>true</code> if the page is present in this range,
     * <code>false</code> otherwise
     */
    public boolean isPageInRange(int pageNumber) {
        for (IPageRangePart sequence : sequences) {
            if (sequence.isPageInRange(pageNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PageRange)) {
            return false;
        }

        PageRange other = (PageRange) obj;
        return sequences.equals(other.sequences);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return sequences.hashCode();
    }

    /**
     * Inner interface for range parts definition
     */
    public static interface IPageRangePart {

        //public List<Integer> getAllPages();

        public List<Integer> getAllPagesInRange(int nbPages);

        public boolean isPageInRange(int pageNumber);
    }

    /**
     * Class for range part containing a single page
     */
    public static class PageRangePartSingle implements IPageRangePart {

        private final int page;

        public PageRangePartSingle(int page) {
            this.page = page;
        }

        @Override
        public List<Integer> getAllPagesInRange(int nbPages) {
            if (page <= nbPages) {
                return Collections.singletonList(page);
            } else {
                return Collections.<Integer>emptyList();
            }
        }

        @Override
        public boolean isPageInRange(int pageNumber) {
            return page == pageNumber;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageRangePartSingle)) {
                return false;
            }

            PageRangePartSingle other = (PageRangePartSingle) obj;
            return page == other.page;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return page;
        }
    }

    /**
     * Class for range part containing a range of pages represented by a start
     * and an end page
     */
    public static class PageRangePartSequence implements IPageRangePart {

        private final int start;
        private final int end;

        public PageRangePartSequence(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public List<Integer> getAllPagesInRange(int nbPages) {
            List<Integer> allPages = new ArrayList<>();
            for (int pageInRange = start; pageInRange <= end && pageInRange <= nbPages; pageInRange++) {
                allPages.add(pageInRange);
            }
            return allPages;
        }

        @Override
        public boolean isPageInRange(int pageNumber) {
            return start <= pageNumber && pageNumber <= end;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageRangePartSequence)) {
                return false;
            }

            PageRangePartSequence other = (PageRangePartSequence) obj;
            return start == other.start && end == other.end;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return start * 31 + end;
        }
    }

    /**
     * Class for range part containing a range of pages for all pages after a
     * given start page
     */
    public static class PageRangePartAfter implements IPageRangePart {

        private final int start;

        public PageRangePartAfter(int start) {
            this.start = start;
        }

        @Override
        public List<Integer> getAllPagesInRange(int nbPages) {
            List<Integer> allPages = new ArrayList<>();
            for (int pageInRange = start; pageInRange <= nbPages; pageInRange++) {
                allPages.add(pageInRange);
            }
            return allPages;
        }

        @Override
        public boolean isPageInRange(int pageNumber) {
            return start <= pageNumber;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageRangePartAfter)) {
                return false;
            }

            PageRangePartAfter other = (PageRangePartAfter) obj;
            return start == other.start;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return start * 31 + -1;
        }
    }

    /**
     * Class for range part for all even or odd pages. The class contains only 2
     * instances, one for odd pages and one for even pages.
     */
    public static class PageRangePartOddEven implements IPageRangePart {

        private final boolean isOdd;
        private final int mod;

        public static final PageRangePartOddEven ODD = new PageRangePartOddEven(true);
        public static final PageRangePartOddEven EVEN = new PageRangePartOddEven(false);

        private PageRangePartOddEven(boolean isOdd) {
            this.isOdd = isOdd;
            if (isOdd) {
                mod = 1;
            } else {
                mod = 0;
            }
        }

        @Override
        public List<Integer> getAllPagesInRange(int nbPages) {
            List<Integer> allPages = new ArrayList<>();
            for (int pageInRange = (mod == 0 ? 2 : mod); pageInRange <= nbPages; pageInRange += 2) {
                allPages.add(pageInRange);
            }
            return allPages;
        }

        @Override
        public boolean isPageInRange(int pageNumber) {
            return pageNumber % 2 == mod;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageRangePartOddEven)) {
                return false;
            }

            PageRangePartOddEven other = (PageRangePartOddEven) obj;
            return isOdd == other.isOdd;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            if (isOdd) {
                return 127;
            }
            return 128;
        }
    }

    /**
     * Class for range part based on several range parts. A 'and' is performed
     * between all conditions. This allows for example to configure odd pages
     * between page 19 and 25.
     */
    public static class PageRangePartAnd implements IPageRangePart {

        private final List<IPageRangePart> conditions = new ArrayList<>();

        public PageRangePartAnd(IPageRangePart... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
        }

        @Override
        public List<Integer> getAllPagesInRange(int nbPages) {
            List<Integer> allPages = new ArrayList<>();
            if (!conditions.isEmpty()) {
                allPages.addAll(conditions.get(0).getAllPagesInRange(nbPages));
            }
            for (IPageRangePart cond : conditions) {
                allPages.retainAll(cond.getAllPagesInRange(nbPages));
            }
            return allPages;
        }
        
        @Override
        public boolean isPageInRange(int pageNumber) {
            for (IPageRangePart cond : conditions) {
                if (!cond.isPageInRange(pageNumber)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageRangePartAnd)) {
                return false;
            }

            PageRangePartAnd other = (PageRangePartAnd) obj;
            return conditions.equals(other.conditions);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return conditions.hashCode();
        }
    }
}
