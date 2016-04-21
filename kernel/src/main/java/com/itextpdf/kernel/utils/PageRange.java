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
            for (int pageInRange = sequenceStarts.get(ind); pageInRange <= sequenceEnds.get(ind); pageInRange++) {
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