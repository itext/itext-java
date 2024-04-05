package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;

/**
 * Class that provides methods for searching mcr in tag tree.
 */
public final class McrCheckUtil {

    /**
     * Creates a new {@link McrCheckUtil} instance.
     */
    private McrCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if tag structure of TR element contains any mcr.
     *
     * @param elementTR PdfDictionary of TR element.
     *
     * @return true if mcr found.
     */
    public static boolean isTrContainsMcr(PdfDictionary elementTR) {
        TagTreeIterator tagTreeIterator = new TagTreeIterator(new PdfStructElem(elementTR));
        McrCheckUtil.McrTagHandler handler = new McrCheckUtil.McrTagHandler();
        tagTreeIterator.addHandler(handler);
        tagTreeIterator.traverse();
        return handler.tagTreeHaveMcr();
    }

    /**
     * Search for mcr elements in the TagTree.
     */
    private static class McrTagHandler implements ITagTreeIteratorHandler {

        private boolean haveMcr = false;

        /**
         * Method returns if tag tree has mcr in it.
         */
        public boolean tagTreeHaveMcr() {
            return haveMcr;
        }

        /**
         * Creates a new {@link  McrTagHandler} instance.
         */
        public McrTagHandler() {
            //empty constructor
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void nextElement(IStructureNode elem) {
            if ((elem instanceof PdfMcr)) {
                haveMcr = true;
            }
        }
    }
}
