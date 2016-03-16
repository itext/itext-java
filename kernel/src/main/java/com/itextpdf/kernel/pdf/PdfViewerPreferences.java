package com.itextpdf.kernel.pdf;

public class PdfViewerPreferences extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -6885879361985241602L;

	public enum PdfViewerPreferencesConstants {
        /**
         * PageMode constants. Use them for NonFullScreenPageMode.
         */
        USE_NONE, USE_OUTLINES, USE_THUMBS, USE_OC,
        /**
         * Direction constants. Use them for Direction property.
         */
        LEFT_TO_RIGHT, RIGHT_TO_LEFT,
        /**
         * PageBoundary constants. Use them for ViewArea, ViewClip, PrintArea and PrintClip properties
         */
        MEDIA_BOX, CROP_BOX, BLEED_BOX, TRIM_BOX, ART_BOX,
        /**
         * ViewArea, ViewClip, PrintArea and PrintClip constants.
         */
        VIEW_AREA, VIEW_CLIP, PRINT_AREA, PRINT_CLIP,
        /**
         * Page scaling option constants. Use them for PrintScaling property
         */
        NONE, APP_DEFAULT,
        /**
         * The paper handling option constants. use them for Duplex property
         */
        SIMPLEX, DUPLEX_FLIP_SHORT_EDGE, DUPLEX_FLIP_LONG_EDGE
    }

    public PdfViewerPreferences() {
        this(new PdfDictionary());
    }

    public PdfViewerPreferences(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * This method sets HideToolBar flag to true or false
     * @param hideToolbar
     * @return
     */
    public PdfViewerPreferences setHideToolbar(boolean hideToolbar) {
        return put(PdfName.HideToolbar, new PdfBoolean(hideToolbar));
    }

    /**
     * This method sets HideMenuBar flag to true or false
     * @param hideMenubar
     * @return
     */
    public PdfViewerPreferences setHideMenubar(boolean hideMenubar) {
        return put(PdfName.HideMenubar, new PdfBoolean(hideMenubar));
    }

    /**
     * This method sets HideWindowUI flag to true or false
     * @param hideWindowUI
     * @return
     */
    public PdfViewerPreferences setHideWindowUI(boolean hideWindowUI) {
        return put(PdfName.HideWindowUI, new PdfBoolean(hideWindowUI));
    }

    /**
     * This method sets FitWindow flag to true or false
     * @param fitWindow
     * @return
     */
    public PdfViewerPreferences setFitWindow(boolean fitWindow) {
        return put(PdfName.FitWindow, new PdfBoolean(fitWindow));
    }

    /**
     * This method sets CenterWindow flag to true or false
     * @param centerWindow
     * @return
     */
    public PdfViewerPreferences setCenterWindow(boolean centerWindow) {
        return put(PdfName.CenterWindow, new PdfBoolean(centerWindow));
    }

    /**
     * This method sets DisplayDocTitle flag to true or false
     * @param displayDocTitle
     * @return
     */
    public PdfViewerPreferences setDisplayDocTitle(boolean displayDocTitle) {
        return put(PdfName.DisplayDocTitle, new PdfBoolean(displayDocTitle));
    }

    /**
     * This method sets NonFullScreenPageMode property. Allowed values are UseNone, UseOutlines, useThumbs, UseOC.
     * This entry is meaningful only if the value of the PageMode entry in the Catalog dictionary is FullScreen
     * @param nonFullScreenPageMode
     * @return
     */
    public PdfViewerPreferences setNonFullScreenPageMode(PdfViewerPreferencesConstants nonFullScreenPageMode) {
        switch (nonFullScreenPageMode) {
            case USE_NONE:
                put(PdfName.NonFullScreenPageMode, PdfName.UseNone);
                break;
            case USE_OUTLINES:
                put(PdfName.NonFullScreenPageMode, PdfName.UseOutlines);
                break;
            case USE_THUMBS:
                put(PdfName.NonFullScreenPageMode, PdfName.UseThumbs);
                break;
            case USE_OC:
                put(PdfName.NonFullScreenPageMode, PdfName.UseOC);
                break;
            default:
        }
        return this;
    }

    /**
     * This method sets predominant reading order of text.
     * @param direction
     * @return
     */
    public PdfViewerPreferences setDirection(PdfViewerPreferencesConstants direction) {
        switch (direction) {
            case LEFT_TO_RIGHT:
                put(PdfName.Direction, PdfName.L2R);
                break;
            case RIGHT_TO_LEFT:
                put(PdfName.Direction, PdfName.R2L);
                break;
            default:
        }
        return this;
    }

    /**
     * This method sets the name of the page boundary representing the area of a page that shall be displayed when
     * viewing the document on the screen.
     * @param pageBoundary
     * @return
     */
    public PdfViewerPreferences setViewArea(PdfViewerPreferencesConstants pageBoundary) {
        return setPageBoundary(PdfViewerPreferencesConstants.VIEW_AREA, pageBoundary);
    }

    /**
     * This method sets the name of the page boundary to which the contents of a page shall be clipped when
     * viewing the document on the screen.
     * @param pageBoundary
     * @return
     */
    public PdfViewerPreferences setViewClip(PdfViewerPreferencesConstants pageBoundary) {
        return setPageBoundary(PdfViewerPreferencesConstants.VIEW_CLIP, pageBoundary);
    }

    /**
     * This method sets the name of the page boundary representing the area of a page that shall be
     * rendered when printing the document.
     * @param pageBoundary
     * @return
     */
    public PdfViewerPreferences setPrintArea(PdfViewerPreferencesConstants pageBoundary) {
        return setPageBoundary(PdfViewerPreferencesConstants.PRINT_AREA, pageBoundary);
    }

    /**
     * This method sets the name of the page boundary to which the contents of a page shall be clipped when
     * printing the document.
     * @param pageBoundary
     * @return
     */
    public PdfViewerPreferences setPrintClip(PdfViewerPreferencesConstants pageBoundary) {
        return setPageBoundary(PdfViewerPreferencesConstants.PRINT_CLIP, pageBoundary);
    }

    /**
     * This method sets the page scaling option that shall be selected when a print dialog is displayed for this
     * document. Valid values are None and AppDefault.
     * @param printScaling
     * @return
     */
    public PdfViewerPreferences setPrintScaling(PdfViewerPreferencesConstants printScaling) {
        switch (printScaling) {
            case NONE:
                put(PdfName.PrintScaling, PdfName.None);
                break;
            case APP_DEFAULT:
                put(PdfName.PrintScaling, PdfName.AppDefault);
                break;
            default:
        }

        return this;
    }

    /**
     * This method sets the paper handling option that shall be used when printing the file from the print dialog.
     * The following values are valid: Simplex, DuplexFlipShortEdge, DuplexFlipLongEdge.
     * @param duplex
     * @return
     */
    public PdfViewerPreferences setDuplex(PdfViewerPreferencesConstants duplex) {
        switch (duplex) {
            case SIMPLEX:
                put(PdfName.Duplex, PdfName.Simplex);
                break;
            case DUPLEX_FLIP_SHORT_EDGE:
                put(PdfName.Duplex, PdfName.DuplexFlipShortEdge);
                break;
            case DUPLEX_FLIP_LONG_EDGE:
                put(PdfName.Duplex, PdfName.DuplexFlipLongEdge);
                break;
            default:
        }
        return this;
    }

    /**
     * This method sets PickTrayByPDFSize flag to true or false.
     * @param pickTrayByPdfSize
     * @return
     */
    public PdfViewerPreferences setPickTrayByPDFSize(boolean pickTrayByPdfSize) {
        return put(PdfName.PickTrayByPDFSize, new PdfBoolean(pickTrayByPdfSize));
    }

    /**
     * This method sets the page numbers used to initialize the print dialog box when the file is printed.
     * @param printPageRange
     * @return
     */
    public PdfViewerPreferences setPrintPageRange(int[] printPageRange) {
        return put(PdfName.PrintPageRange, new PdfArray(printPageRange));
    }

    /**
     * This method sets the number of copies that shall be printed when the print dialog is opened for this file.
     * @param numCopies
     * @return
     */
    public PdfViewerPreferences setNumCopies(int numCopies) {
        return put(PdfName.NumCopies, new PdfNumber(numCopies));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private PdfViewerPreferences setPageBoundary(PdfViewerPreferencesConstants viewerPreferenceType, PdfViewerPreferencesConstants pageBoundary) {
        PdfName type = null;
        switch (viewerPreferenceType) {
            case VIEW_AREA :
                type = PdfName.ViewArea;
                break;
            case VIEW_CLIP :
                type = PdfName.ViewArea;
                break;
            case PRINT_AREA :
                type = PdfName.ViewArea;
                break;
            case PRINT_CLIP :
                type = PdfName.ViewArea;
                break;
            default:
        }
        if (type != null) {
            switch (pageBoundary) {
                case MEDIA_BOX:
                    put(type, PdfName.MediaBox);
                    break;
                case CROP_BOX:
                    put(type, PdfName.CropBox);
                    break;
                case BLEED_BOX:
                    put(type, PdfName.BleedBox);
                    break;
                case TRIM_BOX:
                    put(type, PdfName.TrimBox);
                    break;
                case ART_BOX:
                    put(type, PdfName.ArtBox);
                    break;
                default:
            }
        }

        return this;
    }
}
