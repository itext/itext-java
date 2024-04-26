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

    private final List<IPageRangePart> sequences = new ArrayList<>();

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
        int hashCode = 0;
        for (IPageRangePart part : sequences) {
            hashCode += part.hashCode();
        }

        return hashCode;
    }

    /**
     * Inner interface for range parts definition
     */
    public static interface IPageRangePart {

        //public List<Integer> getAllPages();

        /**
         * Gets the list of pages that have been added to the range part so far.
         *
         * @param nbPages number of pages of the document to get the pages,
         *                to list only the pages eligible for this document
         *
         * @return the list containing page numbers added to the range part matching this document
         */
        public List<Integer> getAllPagesInRange(int nbPages);

        /**
         * Checks if a given page is present in the range part built so far.
         *
         * @param pageNumber the page number to check
         *
         * @return <code>true</code> if the page is present in this range, <code>false</code> otherwise
         */
        public boolean isPageInRange(int pageNumber);
    }

    /**
     * Class for range part containing a single page.
     */
    public static class PageRangePartSingle implements IPageRangePart {

        private final int page;

        /**
         * Creates new {@link PageRangePartSingle} instance.
         *
         * @param page a single page for a range part
         */
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
     * and an end page.
     */
    public static class PageRangePartSequence implements IPageRangePart {

        private final int start;
        private final int end;

        /**
         * Creates new {@link PageRangePartSequence} instance.
         *
         * @param start the number of the first page in a range part
         * @param end   the number of the last page in a range part
         */
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
     * given start page.
     */
    public static class PageRangePartAfter implements IPageRangePart {

        private final int start;

        /**
         * Creates new {@link PageRangePartAfter} instance.
         *
         * @param start the number of the first page in a range part
         */
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

        /**
         * Creates new {@link PageRangePartAnd} instance.
         *
         * @param conditions {@link IPageRangePart} conditions to combine several range parts,
         *                   e.g. to configure odd pages between page 19 and 25
         */
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
            int hashCode = 0;
            for (IPageRangePart part : conditions) {
                hashCode += part.hashCode();
            }

            return hashCode;
        }
    }
}
