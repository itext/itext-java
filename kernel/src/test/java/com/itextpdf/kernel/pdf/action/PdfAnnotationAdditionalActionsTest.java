package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfAnnotationAdditionalActionsTest  extends ExtendedITextTest {

    @Test
    public void onEnterTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnEnter(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnEnter().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onExitTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnExit(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnExit().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onFocusTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnFocus(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnFocus().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onLostFocusTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnLostFocus(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnLostFocus().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onMouseDownTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnMouseDown(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnMouseDown().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onMouseUpTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnMouseUp(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnMouseUp().getPdfObject().getAsString(PdfName.T));
    }


    @Test
    public void onPageClosedTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnPageClosed(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnPageClosed().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onPageLostViewTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnPageLostView(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnPageLostView().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onPageOpenedTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnPageOpened(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnPageOpened().getPdfObject().getAsString(PdfName.T));
    }

    @Test
    public void onPageVisibleTest() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction action = PdfAction.createHide("test", false);
        aa.setOnPageVisible(action);

        Assert.assertEquals(action.getPdfObject().getAsString(PdfName.T), aa.getOnPageVisible().getPdfObject().getAsString(PdfName.T));
    }
    
    @Test
    public void getAllKnownActions() {
        PdfAnnotationAdditionalActions aa = new PdfAnnotationAdditionalActions(new PdfDictionary());
        PdfAction actionPageVisible = PdfAction.createHide("PageVisible", false);
        PdfAction actionPageOpened = PdfAction.createHide("PageOpened", false);
        PdfAction actionPageLostView = PdfAction.createHide("PageLostView", false);
        PdfAction actionPageClosed = PdfAction.createHide("PageClosed", false);
        PdfAction actionMouseUp = PdfAction.createHide("MouseUp", false);
        PdfAction actionMouseDown = PdfAction.createHide("MouseDown", false);
        PdfAction actionLostFocus = PdfAction.createHide("LostFocus", false);
        PdfAction actionFocus = PdfAction.createHide("Focus", false);
        PdfAction actionExit = PdfAction.createHide("Exit", false);
        PdfAction actionEnter = PdfAction.createHide("Enter", false);
        
        aa.setOnPageVisible(actionPageVisible);
        aa.setOnPageClosed(actionPageClosed);
        aa.setOnPageOpened(actionPageOpened);
        aa.setOnPageLostView(actionPageLostView);
        aa.setOnExit(actionExit);
        aa.setOnEnter(actionEnter);
        aa.setOnMouseUp(actionMouseUp);
        aa.setOnMouseDown(actionMouseDown);
        aa.setOnFocus(actionFocus);
        aa.setOnLostFocus(actionLostFocus);

        List<PdfAction> result = aa.getAllKnownActions();
        Assert.assertEquals(10, result.size());
        
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "PageVisible"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "PageClosed"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "PageOpened"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "PageLostView"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "Exit"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "Enter"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "MouseUp"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "MouseDown"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "Focus"));
        Assert.assertTrue(result.stream().
                anyMatch((PdfAction a) -> a.getPdfObject().getAsString(PdfName.T).getValue() == "LostFocus"));
    }

}
